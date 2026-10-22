# Inventário das bibliotecas nativas (gerado por `scripts/inventario_nativo.py`)

Observações estáticas via `pyelftools`. Nenhuma biblioteca foi executada.

| Biblioteca | Tamanho | Entropia | Export. | Import. | JNI `Java_*` | JNI_OnLoad | Símbolos de depuração | NEEDED |
|---|---:|---:|---:|---:|---:|:-:|:-:|---|
| libairbridgelib.so | 12816 | 5.411 | 19 | 11 | 1 | não | não | liblog.so, libm.so, libdl.so, libc.so |
| libapminsighta.so | 102256 | 6.2834 | 1 | 109 | 0 | sim | não | liblog.so, libc.so, libm.so, libdl.so |
| libapminsightb.so | 124352 | 6.4175 | 71 | 68 | 0 | não | não | liblog.so, libc.so, libm.so, libdl.so |
| libbuffer_pgl.so | 9000 | 4.602 | 12 | 17 | 11 | não | não | liblog.so, libm.so, libdl.so, libc.so |
| libdatastore_shared_counter.so | 7112 | 3.9452 | 9 | 10 | 4 | não | não | libm.so, libdl.so, libc.so |
| libfile_lock_pgl.so | 6312 | 4.201 | 6 | 9 | 6 | não | não | liblog.so, libm.so, libdl.so, libc.so |
| libkmSTTv4_armv80.so | 2784416 | 5.6707 | 1209 | 132 | 0 | não | não | liblog.so, libdl.so, libonnxruntime.so, libm.so, libc.so |
| libkmSTTv4_armv82.so | 2783712 | 5.67 | 1209 | 132 | 0 | não | não | liblog.so, libdl.so, libonnxruntime.so, libm.so, libc.so |
| libnexcralbody_mc_jb.so | 801920 | 6.6275 | 4 | 99 | 0 | não | não | libandroid.so, liblog.so, libGLESv2.so, libEGL.so, libdl.so, libm.so, libc.so |
| libnexeditorsdk.so | 9691912 | 6.8335 | 372 | 404 | 370 | sim | não | libonnxruntime.so, libtensorflowlite_jni.so, libtensorflowlite_gpu_jni.so, libGLESv3.so, libEGL.so, libz.so, libandroid.so, libjnigraphics.so, liblog.so, libdl.so, libm.so, libc.so |
| libnexralbody_audio.so | 420288 | 6.3444 | 3 | 108 | 0 | não | não | liblog.so, libdl.so, libm.so, libc.so |
| libnms.so | 302248 | 5.8427 | 1 | 39 | 0 | sim | não | liblog.so, libm.so, libdl.so, libc.so |
| libonnxruntime.so | 17571152 | 6.6888 | 5 | 203 | 0 | não | não | libdl.so, liblog.so, libm.so, libc.so |
| libpglarmor.so | 68776 | 6.3403 | 1 | 13 | 1 | não | não | liblog.so, libz.so, libm.so, libdl.so, libc.so |
| librenderscript-toolkit.so | 3894848 | 5.4124 | 864 | 91 | 22 | não | sim | libjnigraphics.so, liblog.so, libdl.so, libm.so, libc.so |
| libtensorflowlite_gpu_jni.so | 2471512 | 6.2429 | 10 | 184 | 6 | não | não | libGLESv3.so, libEGL.so, libdl.so, libm.so, liblog.so, libc.so |
| libtensorflowlite_jni.so | 4152032 | 6.8903 | 277 | 129 | 56 | não | não | libm.so, libdl.so, liblog.so, libc.so |
| libtranslate_jni.so | 16361048 | 6.5744 | 8 | 282 | 3 | sim | não | libandroid.so, libdl.so, libm.so, liblog.so, libc.so |
| libtt_ugen_layout.so | 440672 | 6.0399 | 708 | 51 | 0 | sim | não | liblog.so, libm.so, libdl.so, libc.so |
| libwrapnexgifcalbody.so | 284584 | 5.7945 | 434 | 54 | 0 | não | não | libandroid.so, liblog.so, libm.so, libdl.so, libc.so |
| libwrapneximagevideocalbody.so | 285864 | 5.7971 | 433 | 54 | 0 | não | não | libandroid.so, liblog.so, libm.so, libdl.so, libc.so |
| libwrapnexpcmcalbody.so | 21192 | 5.8514 | 36 | 17 | 0 | não | não | libandroid.so, liblog.so, libm.so, libdl.so, libc.so |

Total de bibliotecas: 22; com erro de análise: 0.
