#!/usr/bin/env python3
"""Análise estática do AndroidManifest.xml e da assinatura do APK base do KineMaster.

Fontes:
  - AndroidManifest.xml decodificado pelo Apktool (XML legível), em
    $KM_WORKDIR/decompilado/apktool-2.4.1-base/AndroidManifest.xml;
  - certificados e esquemas de assinatura lidos do próprio APK com androguard.

Saída: docs/analise/inventarios/manifesto-base.json e .md (sem alterar os arquivos de origem).

Uso:
    python3 scripts/analise_manifesto.py <apk_base> <manifesto_decodificado.xml>
"""
import hashlib
import json
import logging
import os
import sys
import xml.etree.ElementTree as ET

ANDROID = "{http://schemas.android.com/apk/res/android}"
SAIDA_BASE = os.path.join(os.path.dirname(__file__), "..", "docs", "analise", "inventarios", "manifesto-base")


def nome(elem):
    return elem.get(ANDROID + "name")


def exportado(elem, tem_intent_filter):
    """Retorna o valor declarado de android:exported ou a inferência padrão do Android."""
    valor = elem.get(ANDROID + "exported")
    if valor is not None:
        return valor == "true"
    # Padrão: exportado quando há intent-filter (comportamento do Android < 12 e exigido em >= 12 quando declarado).
    return tem_intent_filter


def componentes(raiz, tag):
    itens = []
    for el in raiz.iter(tag):
        filtros = el.findall("intent-filter")
        acoes = sorted({a.get(ANDROID + "name") for f in filtros for a in f.findall("action")})
        categorias = sorted({c.get(ANDROID + "name") for f in filtros for c in f.findall("category")})
        dados = []
        for f in filtros:
            for d in f.findall("data"):
                dados.append({k.replace(ANDROID, "android:"): v for k, v in d.attrib.items()})
        itens.append(
            {
                "nome": nome(el),
                "exportado_efetivo": exportado(el, bool(filtros)),
                "exportado_declarado": el.get(ANDROID + "exported"),
                "permissao": el.get(ANDROID + "permission"),
                "processo": el.get(ANDROID + "process"),
                "acoes_intent_filter": acoes,
                "categorias_intent_filter": categorias,
                "dados_intent_filter": dados,
            }
        )
    return sorted(itens, key=lambda c: c["nome"] or "")


def analisar_manifesto(caminho):
    raiz = ET.parse(caminho).getroot()
    app = raiz.find("application")
    resultado = {
        "pacote": raiz.get("package"),
        "versao_nome": raiz.get("{http://schemas.android.com/apk/res/android}versionName")
        or raiz.get("versionName"),
        "versao_codigo": raiz.get(ANDROID + "versionCode"),
        "compile_sdk": raiz.get(ANDROID + "compileSdkVersion"),
        "tipos_de_split_requeridos": raiz.get(ANDROID + "requiredSplitTypes"),
        "permissoes_declaradas": sorted({p.get(ANDROID + "name") for p in raiz.findall("uses-permission")}),
        "permissoes_de_maior_risco_declaradas": [],
        "permissoes_customizadas": sorted(
            {p.get(ANDROID + "name") for p in raiz.iter("permission") if p.get(ANDROID + "name")}
        ),
        "recursos_de_hardware_declarados": sorted(
            {f.get(ANDROID + "name") for f in raiz.findall("uses-feature") if f.get(ANDROID + "name")}
        ),
        "sdk": {
            "minSdk": None,
            "targetSdk": None,
        },
        "application": {},
        "activities": [],
        "services": [],
        "receivers": [],
        "providers": [],
        "queries_pacotes": sorted(
            {p.get(ANDROID + "name") for p in raiz.iter("package") if p.get(ANDROID + "name")}
        ),
    }
    usos = raiz.find("uses-sdk")
    if usos is not None:
        resultado["sdk"]["minSdk"] = usos.get(ANDROID + "minSdkVersion")
        resultado["sdk"]["targetSdk"] = usos.get(ANDROID + "targetSdkVersion")
    if app is not None:
        resultado["application"] = {
            "nome": app.get(ANDROID + "name"),
            "debuggable": app.get(ANDROID + "debuggable"),
            "allowBackup": app.get(ANDROID + "allowBackup"),
            "usesCleartextTraffic": app.get(ANDROID + "usesCleartextTraffic"),
            "networkSecurityConfig": app.get(ANDROID + "networkSecurityConfig"),
            "extractNativeLibs": app.get(ANDROID + "extractNativeLibs"),
            "largeHeap": app.get(ANDROID + "largeHeap"),
            "requestLegacyExternalStorage": app.get(ANDROID + "requestLegacyExternalStorage"),
            "usesNonSdkApi": app.get(ANDROID + "usesNonSdkApi"),
        }
        resultado["activities"] = componentes(app, "activity")
        resultado["services"] = componentes(app, "service")
        resultado["receivers"] = componentes(app, "receiver")
        resultado["providers"] = componentes(app, "provider")
    perigosas = {
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.RECORD_AUDIO",
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO",
        "android.permission.READ_MEDIA_AUDIO",
        "android.permission.BLUETOOTH_SCAN",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.NEARBY_WIFI_DEVICES",
        "android.permission.USE_CREDENTIALS",
        "android.permission.POST_NOTIFICATIONS",
    }
    resultado["permissoes_de_maior_risco_declaradas"] = sorted(
        p for p in resultado["permissoes_declaradas"] if p in perigosas
    )
    return resultado


def assinatura(apk):
    """Lê esquemas de assinatura e impressão digital dos certificados via androguard."""
    logging.getLogger().setLevel(logging.CRITICAL)
    try:
        from loguru import logger

        logger.remove()
    except ImportError:
        pass
    from androguard.core.apk import APK

    a = APK(apk)
    certs_der = a.get_certificates_der_v2() or a.get_certificates_der_v3()
    info = {
        "manifesto_lido_pelo_androguard": {
            "pacote": a.get_package(),
            "versao_nome": a.get_androidversion_name(),
            "versao_codigo": a.get_androidversion_code(),
            "minSdk": a.get_min_sdk_version(),
            "targetSdk": a.get_target_sdk_version(),
        },
        "esquema_v1": bool(a.is_signed_v1()),
        "esquema_v2": bool(a.is_signed_v2()),
        "esquema_v3": bool(a.is_signed_v3()),
        "certificados": [],
    }
    for der in certs_der:
        registro = {"sha256_der": hashlib.sha256(der).hexdigest()}
        try:
            from cryptography import x509
            from cryptography.hazmat.primitives import hashes

            cert = x509.load_der_x509_certificate(der)
            registro["assunto"] = cert.subject.rfc4514_string()
            registro["emissor"] = cert.issuer.rfc4514_string()
            registro["valido_de"] = cert.not_valid_before_utc.isoformat()
            registro["valido_ate"] = cert.not_valid_after_utc.isoformat()
            registro["sha256_certificado"] = cert.fingerprint(hashes.SHA256()).hex()
            registro["algoritmo_chave"] = type(cert.public_key()).__name__
        except Exception as exc:  # pragma: no cover - diagnóstico explícito
            registro["erro_leitura"] = str(exc)
        info["certificados"].append(registro)
    return info


def gerar_markdown(dados):
    m = dados["manifesto"]
    s = dados["assinatura"]
    andro = s["manifesto_lido_pelo_androguard"]
    linhas = [
        "# Manifesto e assinatura do APK base (gerado por `scripts/analise_manifesto.py`)",
        "",
        "Fato observado: dados lidos do `AndroidManifest.xml` decodificado pelo Apktool 2.4.1 e dos certificados do APK.",
        "`exportado_efetivo` é a interpretação estática (declaração explícita ou inferência pelo intent-filter); não é prova de execução.",
        "",
        "## Identificação",
        "",
        f"- Pacote: `{m['pacote']}`",
        f"- compileSdkVersion: `{m['compile_sdk']}`; tipos de split requeridos: `{m['tipos_de_split_requeridos']}`",
        "",
        "### Versão e SDK: fontes independentes (divergência registrada)",
        "",
        "| Campo | Apktool 2.4.1 (XML decodificado) | androguard (manifesto binário) |",
        "|---|---|---|",
        f"| versionName | {m['versao_nome'] or 'ausente'} | {andro['versao_nome']} |",
        f"| versionCode | {m['versao_codigo'] or 'ausente'} | {andro['versao_codigo']} |",
        f"| minSdk | {m['sdk']['minSdk'] or 'ausente'} | {andro['minSdk']} |",
        f"| targetSdk | {m['sdk']['targetSdk'] or 'ausente'} | {andro['targetSdk']} |",
        "",
        "Observação: o XML decodificado pelo Apktool 2.4.1 não traz `<uses-sdk>` nem `versionName`/`versionCode`;",
        "os valores acima do androguard são os usados como referência para versão e SDK (ver `docs/analise/ferramentas-e-restricoes.md`).",
        "",
        "## Esquemas de assinatura",
        "",
        f"- v1: {s['esquema_v1']}; v2: {s['esquema_v2']}; v3: {s['esquema_v3']}",
    ]
    for c in s["certificados"]:
        linhas.append(f"- Certificado: sujeito `{c.get('assunto', '?')}`; SHA-256 do certificado `{c.get('sha256_certificado', '?')}`; válido até {c.get('valido_ate', '?')}")
    linhas += [
        "",
        "## Application",
        "",
    ]
    for k, v in m["application"].items():
        linhas.append(f"- `{k}`: {v}")
    linhas += [
        "",
        f"## Permissões declaradas ({len(m['permissoes_declaradas'])})",
        "",
    ]
    for p in m["permissoes_declaradas"]:
        marca = " (sensível)" if p in m["permissoes_de_maior_risco_declaradas"] else ""
        linhas.append(f"- `{p}`{marca}")
    linhas += ["", f"Permissões customizadas definidas pelo app: {m['permissoes_customizadas'] or 'nenhuma'}", ""]
    linhas += [f"Recursos de hardware (`uses-feature`): {len(m['recursos_de_hardware_declarados'])}", ""]
    for chave, titulo in (
        ("activities", "Atividades"),
        ("services", "Serviços"),
        ("receivers", "Receptores"),
        ("providers", "Provedores"),
    ):
        itens = m[chave]
        exportados = [i for i in itens if i["exportado_efetivo"]]
        linhas += [
            f"## {titulo} ({len(itens)}; exportados efetivos: {len(exportados)})",
            "",
        ]
        for i in itens:
            filtros = ""
            if i["acoes_intent_filter"]:
                filtros = f" — ações: {', '.join(i['acoes_intent_filter'])}"
            marca = " **[exportado]**" if i["exportado_efetivo"] else ""
            permissao = f" permissão: `{i['permissao']}`" if i["permissao"] else ""
            linhas.append(f"- `{i['nome']}`{marca}{permissao}{filtros}")
        linhas.append("")
    return "\n".join(linhas) + "\n"


def main():
    if len(sys.argv) != 3:
        print("uso: analise_manifesto.py <apk_base> <manifesto_decodificado.xml>", file=sys.stderr)
        return 2
    apk, manifesto = sys.argv[1], sys.argv[2]
    dados = {
        "apk": os.path.basename(apk),
        "manifesto": analisar_manifesto(manifesto),
        "assinatura": assinatura(apk),
    }
    base = os.path.abspath(SAIDA_BASE)
    with open(base + ".json", "w", encoding="utf-8") as f:
        json.dump(dados, f, ensure_ascii=False, indent=2)
    with open(base + ".md", "w", encoding="utf-8") as f:
        f.write(gerar_markdown(dados))
    m = dados["manifesto"]
    print(
        "ok: atividades={a} serviços={s} receptores={r} provedores={p} permissões={pm}".format(
            a=len(m["activities"]),
            s=len(m["services"]),
            r=len(m["receivers"]),
            p=len(m["providers"]),
            pm=len(m["permissoes_declaradas"]),
        )
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
