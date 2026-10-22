# Manifesto e assinatura do APK base (gerado por `scripts/analise_manifesto.py`)

Fato observado: dados lidos do `AndroidManifest.xml` decodificado pelo Apktool 2.4.1 e dos certificados do APK.
`exportado_efetivo` é a interpretação estática (declaração explícita ou inferência pelo intent-filter); não é prova de execução.

## Identificação

- Pacote: `com.nexstreaming.app.kinemasterfree`
- compileSdkVersion: `37`; tipos de split requeridos: `base__abi,base__density`

### Versão e SDK: fontes independentes (divergência registrada)

| Campo | Apktool 2.4.1 (XML decodificado) | androguard (manifesto binário) |
|---|---|---|
| versionName | ausente | 8.2.14.37398.GP |
| versionCode | ausente | 37398 |
| minSdk | ausente | 26 |
| targetSdk | ausente | 36 |

Observação: o XML decodificado pelo Apktool 2.4.1 não traz `<uses-sdk>` nem `versionName`/`versionCode`;
os valores acima do androguard são os usados como referência para versão e SDK (ver `docs/analise/ferramentas-e-restricoes.md`).

## Esquemas de assinatura

- v1: False; v2: True; v3: True
- Certificado: sujeito `O=NexStreaming,L=Seoul,C=KR`; SHA-256 do certificado `b2b90f2f5a3679186fb82a6fb8c07f5e6ab4b55ea01b5625b28a9b5549fc6988`; válido até 2063-03-06T00:19:59+00:00

## Application

- `nome`: com.kinemaster.app.base.KineMasterApplication
- `debuggable`: None
- `allowBackup`: false
- `usesCleartextTraffic`: None
- `networkSecurityConfig`: @xml/network_security_config
- `extractNativeLibs`: false
- `largeHeap`: true
- `requestLegacyExternalStorage`: true
- `usesNonSdkApi`: None

## Permissões declaradas (32)

- `android.permission.ACCESS_ADSERVICES_AD_ID`
- `android.permission.ACCESS_ADSERVICES_ATTRIBUTION`
- `android.permission.ACCESS_ADSERVICES_TOPICS`
- `android.permission.ACCESS_NETWORK_STATE`
- `android.permission.ACCESS_WIFI_STATE`
- `android.permission.BLUETOOTH_ADVERTISE`
- `android.permission.BLUETOOTH_CONNECT` (sensível)
- `android.permission.BLUETOOTH_SCAN` (sensível)
- `android.permission.CHANGE_WIFI_STATE`
- `android.permission.FLAG_GRANT_READ_URI_PERMISSION`
- `android.permission.FOREGROUND_SERVICE`
- `android.permission.FOREGROUND_SERVICE_DATA_SYNC`
- `android.permission.INTERNET`
- `android.permission.NEARBY_WIFI_DEVICES` (sensível)
- `android.permission.POST_NOTIFICATIONS` (sensível)
- `android.permission.READ_EXTERNAL_STORAGE` (sensível)
- `android.permission.READ_MEDIA_AUDIO` (sensível)
- `android.permission.READ_MEDIA_IMAGES` (sensível)
- `android.permission.READ_MEDIA_VIDEO` (sensível)
- `android.permission.READ_MEDIA_VISUAL_USER_SELECTED`
- `android.permission.RECORD_AUDIO` (sensível)
- `android.permission.USE_CREDENTIALS` (sensível)
- `android.permission.VIBRATE`
- `android.permission.WAKE_LOCK`
- `android.permission.WRITE_EXTERNAL_STORAGE` (sensível)
- `com.android.vending.BILLING`
- `com.android.vending.CHECK_LICENSE`
- `com.google.android.c2dm.permission.RECEIVE`
- `com.google.android.finsky.permission.BIND_GET_INSTALL_REFERRER_SERVICE`
- `com.google.android.gms.permission.AD_ID`
- `com.google.android.providers.gsf.permission.READ_GSERVICES`
- `com.nexstreaming.app.kinemasterfree.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`

Permissões customizadas definidas pelo app: ['com.nexstreaming.app.kinemasterfree.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION']

Recursos de hardware (`uses-feature`): 8

## Atividades (55; exportados efetivos: 4)

- `androidx.credentials.playservices.HiddenActivity`
- `com.android.billingclient.api.ProxyBillingActivity`
- `com.android.billingclient.api.ProxyBillingActivityV2`
- `com.bytedance.sdk.openadsdk.activity.TTFullWebActivity`
- `com.bytedance.sdk.openadsdk.activity.TTRewardWebActivity`
- `com.bytedance.sdk.openadsdk.activity.single.IABLandingPageActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTAdActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTAppOpenAdActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTAppOpenAdTransActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTCeilingLandingPageActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTDelegateActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTFullScreenExpressVideoActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTFullScreenVideoActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTHistoryActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTHistoryLandingPageActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTLandingPageActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTPlayableLandingPageActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTRewardExpressVideoActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTRewardVideoActivity`
- `com.bytedance.sdk.openadsdk.activity.single.TTVideoLandingPageLink2Activity`
- `com.bytedance.sdk.openadsdk.activity.single.TTWebsiteActivity`
- `com.facebook.ads.AudienceNetworkActivity`
- `com.fyber.inneractive.sdk.activities.FyberReportAdActivity`
- `com.fyber.inneractive.sdk.activities.InneractiveFullscreenAdActivity`
- `com.fyber.inneractive.sdk.activities.InneractiveInternalBrowserActivity`
- `com.fyber.inneractive.sdk.activities.InneractiveRichMediaVideoPlayerActivityCore`
- `com.fyber.inneractive.sdk.activities.InternalStoreWebpageActivity`
- `com.google.android.gms.ads.AdActivity`
- `com.google.android.gms.ads.NotificationHandlerActivity`
- `com.google.android.gms.ads.OutOfContextTestingActivity`
- `com.google.android.gms.auth.api.signin.internal.SignInHubActivity`
- `com.google.android.gms.common.api.GoogleApiActivity`
- `com.google.android.play.core.common.PlayCoreDialogWrapperActivity`
- `com.google.android.play.core.hsdp.service.HsdpShimActivity`
- `com.google.firebase.auth.internal.GenericIdpActivity` **[exportado]** — ações: android.intent.action.VIEW
- `com.google.firebase.auth.internal.RecaptchaActivity` **[exportado]** — ações: android.intent.action.VIEW
- `com.inmobi.ads.rendering.InMobiAdActivity`
- `com.inmobi.ads.rendering.InMobiInAppBrowserActivity`
- `com.kinemaster.app.screen.assetstore.AssetStoreActivity`
- `com.kinemaster.app.screen.handover.HandoverReceivingActivity`
- `com.kinemaster.app.screen.home.HomeActivity`
- `com.kinemaster.app.screen.input.InputTextActivity`
- `com.kinemaster.app.screen.launch.kmscheme.KMSchemePendingActivity`
- `com.kinemaster.app.screen.launch.shareintent.ShareIntentActivity` **[exportado]** — ações: android.intent.action.SEND, android.intent.action.SEND_MULTIPLE, android.intent.action.VIEW, com.kinemaster.intent.NEW_PROJECT, com.kinemaster.intent.SHARE_PROJECT
- `com.kinemaster.app.screen.projecteditor.ProjectEditorActivity`
- `com.kinemaster.app.screen.saveas.SaveAsActivity`
- `com.kinemaster.app.screen.saveas.SaveAsPanelActivity`
- `com.kinemaster.app.screen.sign.SignActivity`
- `com.kinemaster.app.screen.splash.SplashActivity` **[exportado]** — ações: android.intent.action.MAIN
- `com.kinemaster.app.screen.subscription.SubscriptionActivity`
- `com.kinemaster.app.screen.templar.TemplarActivity`
- `com.kinemaster.app.screen.upload.TemplateUploadActivity`
- `com.nexstreaming.kinemaster.ad.bridge.InterstitialAdsActivity`
- `com.nexstreaming.kinemaster.ui.settings.SettingsActivity`
- `com.nexstreaming.kinemaster.util.update.AssetUpdateActivity`

## Serviços (19; exportados efetivos: 3)

- `androidx.credentials.playservices.CredentialProviderMetadataHolder`
- `androidx.room.MultiInstanceInvalidationService`
- `androidx.work.impl.background.systemjob.SystemJobService` **[exportado]** permissão: `android.permission.BIND_JOB_SERVICE`
- `androidx.work.impl.foreground.SystemForegroundService`
- `com.google.android.datatransport.runtime.backends.TransportBackendDiscovery`
- `com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService` permissão: `android.permission.BIND_JOB_SERVICE`
- `com.google.android.gms.ads.AdService`
- `com.google.android.gms.auth.api.signin.RevocationBoundService` **[exportado]** permissão: `com.google.android.gms.auth.api.signin.permission.REVOCATION_NOTIFICATION`
- `com.google.android.gms.measurement.AppMeasurementJobService` permissão: `android.permission.BIND_JOB_SERVICE`
- `com.google.android.gms.measurement.AppMeasurementService`
- `com.google.android.play.core.assetpacks.AssetPackExtractionService` **[exportado]**
- `com.google.android.play.core.assetpacks.ExtractionForegroundService`
- `com.google.firebase.components.ComponentDiscoveryService`
- `com.google.firebase.messaging.FirebaseMessagingService` — ações: com.google.firebase.MESSAGING_EVENT
- `com.google.firebase.sessions.SessionLifecycleService`
- `com.google.mlkit.common.internal.MlKitComponentDiscoveryService`
- `com.kinemaster.app.dci.service.DCIService` — ações: com.nexstreaming.app.kinemasterfree.ACTION_PERF_CANCEL, com.nexstreaming.app.kinemasterfree.ACTION_PERF_CHECK
- `com.kinemaster.app.screen.handover.HandoverTransferService`
- `com.nexstreaming.kinemaster.notification.PushNotificationService` — ações: com.google.firebase.MESSAGING_EVENT

## Receptores (8; exportados efetivos: 4)

- `androidx.profileinstaller.ProfileInstallReceiver` **[exportado]** permissão: `android.permission.DUMP` — ações: androidx.profileinstaller.action.BENCHMARK_OPERATION, androidx.profileinstaller.action.INSTALL_PROFILE, androidx.profileinstaller.action.SAVE_PROFILE, androidx.profileinstaller.action.SKIP_FILE
- `androidx.work.impl.background.systemalarm.RescheduleReceiver` — ações: android.intent.action.BOOT_COMPLETED
- `androidx.work.impl.diagnostics.DiagnosticsReceiver` **[exportado]** permissão: `android.permission.DUMP` — ações: androidx.work.diagnostics.REQUEST_DIAGNOSTICS
- `androidx.work.impl.utils.ForceStopRunnable$BroadcastReceiver`
- `com.google.android.datatransport.runtime.scheduling.jobscheduling.AlarmManagerSchedulerBroadcastReceiver`
- `com.google.android.gms.measurement.AppMeasurementReceiver`
- `com.google.android.play.core.assetpacks.SessionStateBroadcastReceiver` **[exportado]** permissão: `android.permission.INSTALL_PACKAGES` — ações: com.google.android.play.core.assetpacks.receiver.ACTION_SESSION_UPDATE
- `com.google.firebase.iid.FirebaseInstanceIdReceiver` **[exportado]** permissão: `com.google.android.c2dm.permission.SEND` — ações: com.google.android.c2dm.intent.RECEIVE

## Provedores (8; exportados efetivos: 0)

- `androidx.core.content.FileProvider`
- `co.ab180.airbridge.internal.lifecycle.AirbridgeLifecycleProvider`
- `com.facebook.ads.AudienceNetworkContentProvider`
- `com.google.android.gms.ads.MobileAdsInitProvider`
- `com.google.firebase.provider.FirebaseInitProvider`
- `com.google.mlkit.common.internal.MlKitInitProvider`
- `com.inmobi.sdk.InMobiInitProvider`
- `com.squareup.picasso.PicassoProvider`

