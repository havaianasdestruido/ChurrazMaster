package com.kinemaster.app.base;

import android.app.ActivityManager;
import android.app.Application;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import androidx.fragment.app.z;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.concurrent.FirebaseExecutors;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.inappmessaging.FirebaseInAppMessaging;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.kinemaster.app.database.font.FontCollectionEntity;
import com.kinemaster.app.database.util.c;
import com.kinemaster.app.dci.CapabilityData;
import com.kinemaster.app.modules.pref.PrefKey;
import com.kinemaster.app.modules.pref.PrefName;
import com.kinemaster.app.repository.home.b;
import com.kinemaster.app.repository.home.d;
import com.kinemaster.app.screen.home.model.AccountInfo;
import com.kinemaster.module.network.remote.KinemasterService$ServerType;
import com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditor;
import com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditorListener;
import com.kinemaster.module.nextask.task.ResultTask;
import com.nexstreaming.app.general.nexasset.assetpackage.security.provider.a;
import com.nexstreaming.kinemaster.usage.analytics.KMEvents;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import kotlinx.coroutines.CoroutineStart;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public final class KineMasterApplication extends hr3 implements vi1, lp {
    public static volatile boolean I;
    public static volatile boolean J;
    public static KineMasterApplication K;
    public static a[] L;
    public final Object A;
    public ResultTask B;
    public d64 C;
    public final Object D;
    public ResultTask E;
    public volatile boolean F;
    public final b G;
    public final b H;
    public sp3 c;
    public String d = KMEvents.UNKNOWN_NAME;
    public nl5 e;
    public boolean f;
    public String g;
    public NexEditor h;
    public NexEditor.EditorInitException i;
    public UnsatisfiedLinkError j;
    public boolean k;
    public final ld0 l = new ld0(5);
    public final ky4 m;
    public d n;
    public com.kinemaster.app.repository.project.a o;
    public com.kinemaster.app.repository.font.a p;
    public b q;
    public final com.kinemaster.app.modules.a r;
    public final zn4 s;
    public final hv0 t;
    public final Object u;
    public final bo4 v;
    public final Object w;
    public volatile ResultTask x;
    public final Object y;
    public volatile ResultTask z;

    public KineMasterApplication() {
        mw6 mw6 = mw6.c;
        this.m = t94.l(mw6);
        this.r = new com.kinemaster.app.modules.a("InitializedEnvironments", mw6);
        this.s = new zn4(this);
        K = this;
        this.t = new hv0(new zd3(2), new ao4(this, 0));
        this.u = new Object();
        this.v = new bo4(this);
        this.w = new Object();
        this.y = new Object();
        this.A = new Object();
        this.D = new Object();
        this.G = new b(this, 0);
        this.H = new b(this, 1);
    }

    public static boolean j(PrefKey prefKey) {
        String str = (String) com.kinemaster.app.modules.pref.a.d(prefKey, "product");
        if (t94.c(str, "product")) {
            return true;
        }
        t94.c(str, "test");
        return false;
    }

    public final void a() {
        if (I) {
            ky4 ky4 = this.m;
            i12 i12 = v92.a;
            xj3.E(ky4, y85.a, (CoroutineStart) null, new KineMasterApplication$applyAirbridgeSwitch$1((lm1<? super KineMasterApplication$applyAirbridgeSwitch$1>) null), 2);
        }
    }

    public final ld0 b() {
        return this.l;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x003d, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00be, code lost:
        r5.j = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x003d A[Catch:{ Exception -> 0x009b, UnsatisfiedLinkError -> 0x003d, UnsatisfiedLinkError -> 0x003d, EditorInitException -> 0x003a, Exception -> 0x0038 }, ExcHandler: UnsatisfiedLinkError (r0v1 'e' java.lang.UnsatisfiedLinkError A[CUSTOM_DECLARE, Catch:{  }]), Splitter:B:2:0x0006] */
    public final void c() {
        if (this.h == null) {
            try {
                m74 m74 = new m74(c.c.t(), this);
                CapabilityData d2 = my0.c.d();
                d2.getClass();
                wu1 m2 = joa.m();
                m2.j(my0.c(d2.getGlEsVersion()));
                v7c.b = false;
                ho4 c2 = ho4.c();
                if (c2 == null) {
                    c2 = new ho4(this, m2);
                }
                c2.h(m74);
                NexEditor d3 = c2.d();
                if (d3 == null) {
                    Throwable f2 = c2.f();
                    if (f2 == null) {
                        f2 = c2.e();
                        if (f2 == null) {
                            f2 = new NexEditor.EditorInitException("editor is null");
                        }
                    }
                    throw f2;
                }
                d3.setEventHandler((NexEditorListener) new co4(this));
                d3.createProject();
                String g2 = wn4.g(d3);
                HashMap hashMap = new HashMap();
                hashMap.put("appVersion", "8.2.14.37398.GP");
                hashMap.put("engineVersion", g2);
                KMEvents.VERSION_CHECK.logEvent((Map<String, String>) hashMap);
                d3.setProperty(NexEditor.PROP_MISSING_CLIP_IMAGE_URL, "kmm://missing/image");
                d3.setProperty("PreviewFPS", "30");
                d3.setProperty("supportContentDuration", "99");
                this.h = d3;
            } catch (Exception e2) {
                throw e2;
            } catch (UnsatisfiedLinkError e3) {
            } catch (NexEditor.EditorInitException e4) {
                this.i = e4;
            } catch (Exception e5) {
                this.i = new NexEditor.EditorInitException(e5.getMessage());
                com.nexstreaming.kinemaster.usage.analytics.a.g(new Exception(qq2.D("EditorInitException + ", e5.getMessage()), e5));
            }
        }
    }

    public final NexEditor d() {
        synchronized (this.u) {
            try {
                if (this.h == null) {
                    if (!lj3.s(bn2.a())) {
                        return null;
                    }
                    if (!this.t.D()) {
                        com.nexstreaming.kinemaster.usage.analytics.a.g(new IllegalStateException("NexEditor created without any Activity in this process"));
                    }
                    c();
                    Objects.toString(this.h);
                }
                NexEditor nexEditor = this.h;
                return nexEditor;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public final ResultTask e() {
        ResultTask resultTask;
        ResultTask resultTask2 = this.z;
        if (resultTask2 != null) {
            return resultTask2;
        }
        synchronized (this.y) {
            resultTask = this.z;
            if (resultTask == null) {
                resultTask = new ResultTask();
                this.z = resultTask;
                ky4 ky4 = this.m;
                i12 i12 = v92.a;
                xj3.E(ky4, e02.b, (CoroutineStart) null, new KineMasterApplication$initializeEngine$1$1$1(this, resultTask, (lm1<? super KineMasterApplication$initializeEngine$1$1$1>) null), 2);
            }
        }
        return resultTask;
    }

    public final void f() {
        FirebaseApp.h(this);
        String str = (String) com.kinemaster.app.modules.pref.a.d(PrefKey.SET_TEST_NAME, "");
        if (str.length() > 0) {
            t94.w(this, "TestName", str);
        }
        wn4.n();
        i();
        ko4.s = new jx1(25);
        t94.c(com.kinemaster.app.modules.pref.a.c(PrefKey.APP_BUILD_TIME), 1792388964581L);
        ky4 ky4 = this.m;
        i12 i12 = v92.a;
        e02 e02 = e02.b;
        xj3.E(ky4, e02, (CoroutineStart) null, new KineMasterApplication$initOperationElements$1(this, (lm1<? super KineMasterApplication$initOperationElements$1>) null), 2);
        xj3.E(ky4, e02, (CoroutineStart) null, new KineMasterApplication$initOperationElements$2(this, (lm1<? super KineMasterApplication$initOperationElements$2>) null), 2);
        xj3.E(ky4, v92.a, (CoroutineStart) null, new KineMasterApplication$initOperationElements$3(this, (lm1<? super KineMasterApplication$initOperationElements$3>) null), 2);
        com.nexstreaming.kinemaster.usage.analytics.a.a();
        Context applicationContext = getApplicationContext();
        applicationContext.getClass();
        FirebaseAnalytics.getInstance(applicationContext).a.zzq(Boolean.TRUE);
        zxc q2 = v7c.q();
        Context applicationContext2 = getApplicationContext();
        applicationContext2.getClass();
        q2.j(applicationContext2);
        J = true;
        a();
        xj3.E(this.m, e02, (CoroutineStart) null, new KineMasterApplication$initializeEnvironments$1(this, (lm1<? super KineMasterApplication$initializeEnvironments$1>) null), 2);
        FirebaseInAppMessaging firebaseInAppMessaging = (FirebaseInAppMessaging) FirebaseApp.d().b(FirebaseInAppMessaging.class);
        firebaseInAppMessaging.getClass();
        firebaseInAppMessaging.d = true;
        PrefKey prefKey = PrefKey.ANDROID_ID;
        String U = vw9.U((String) com.kinemaster.app.modules.pref.a.d(prefKey, "0"), new km3(this, 15), new r04(13));
        if (U != null) {
            com.kinemaster.app.modules.pref.a.i(prefKey, U);
            new BackupManager(this).dataChanged();
        }
        xj3.E(this.m, (jn1) null, (CoroutineStart) null, new KineMasterApplication$sendLogEvent$1((lm1<? super KineMasterApplication$sendLogEvent$1>) null), 3);
        com.kinemaster.app.modules.a.c(this.r);
    }

    public final ResultTask g() {
        ResultTask resultTask;
        ResultTask resultTask2 = this.B;
        if (resultTask2 != null) {
            return resultTask2;
        }
        synchronized (this.A) {
            try {
                resultTask = new ResultTask();
                this.B = resultTask;
                Context applicationContext = getApplicationContext();
                applicationContext.getClass();
                if (os0.H(applicationContext)) {
                    Context applicationContext2 = getApplicationContext();
                    applicationContext2.getClass();
                    os0.U(applicationContext2, true);
                    d64 d2 = b64.c(this).d();
                    this.C = d2;
                    d2.d(new kk3(13, (Object) d2, (Object) resultTask));
                } else {
                    resultTask.sendResult(null);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return resultTask;
    }

    public final ResultTask h() {
        ResultTask resultTask;
        ResultTask resultTask2 = this.x;
        if (resultTask2 != null) {
            return resultTask2;
        }
        synchronized (this.w) {
            resultTask = this.x;
            if (resultTask == null) {
                resultTask = new ResultTask();
                this.x = resultTask;
                f();
                xj3.E(this.m, e02.b, (CoroutineStart) null, new KineMasterApplication$initializeLibraries$1$1$1(this, resultTask, (lm1<? super KineMasterApplication$initializeLibraries$1$1$1>) null), 2);
            }
        }
        return resultTask;
    }

    public final ResultTask i() {
        ResultTask resultTask;
        ResultTask resultTask2 = this.E;
        if (resultTask2 != null) {
            return resultTask2;
        }
        synchronized (this.D) {
            resultTask = new ResultTask();
            this.E = resultTask;
            ((qk7) os0.w()).a(new it3((Object) this, 5));
            c04 w2 = os0.w();
            kk3 kk3 = new kk3(14, (Object) resultTask, (Object) this);
            qk7 qk7 = (qk7) w2;
            FirebaseRemoteConfig firebaseRemoteConfig = qk7.a;
            firebaseRemoteConfig.g.a(firebaseRemoteConfig.d().b.b).onSuccessTask(FirebaseExecutors.a(), new nx2(3)).addOnCompleteListener(new fn2(2, qk7, kk3));
        }
        return resultTask;
    }

    public final void k() {
        if (((Boolean) com.kinemaster.app.modules.pref.a.d(PrefKey.PUT_USER_TYPE, Boolean.FALSE)).booleanValue()) {
            String str = (String) com.kinemaster.app.modules.pref.a.d(PrefKey.USER_TYPE, KMEvents.UNKNOWN_NAME);
            if (!t94.c(this.d, str)) {
                this.d = str;
                t94.w(this, "User Type", str);
            }
        }
    }

    public final void onConfigurationChanged(Configuration configuration) {
        configuration.getClass();
        super.onConfigurationChanged(configuration);
    }

    /* JADX WARNING: type inference failed for: r1v75, types: [he4, java.lang.Object] */
    public final void onCreate() {
        String str;
        boolean z2;
        String str2;
        String str3;
        FirebaseMessaging instance;
        Task task;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo;
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo2;
        z.R = false;
        if (Build.VERSION.SDK_INT >= 28) {
            str = Application.getProcessName();
            str.getClass();
        } else {
            Object systemService = getSystemService("activity");
            systemService.getClass();
            int myPid = Process.myPid();
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = ((ActivityManager) systemService).getRunningAppProcesses();
            if (runningAppProcesses != null) {
                Iterator<T> it = runningAppProcesses.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        runningAppProcessInfo2 = null;
                        break;
                    }
                    runningAppProcessInfo2 = it.next();
                    if (runningAppProcessInfo2.pid == myPid) {
                        break;
                    }
                }
                runningAppProcessInfo = (ActivityManager.RunningAppProcessInfo) runningAppProcessInfo2;
            } else {
                runningAppProcessInfo = null;
            }
            if (runningAppProcessInfo == null || (str = runningAppProcessInfo.processName) == null) {
                str = "";
            }
        }
        if (pr8.k(str, ":perf", false)) {
            super.onCreate();
            com.kinemaster.app.modules.pref.a.a = new ao4(this, 1);
            FirebaseApp.h(this);
            return;
        }
        com.kinemaster.app.modules.pref.a.a = new ao4(this, 2);
        if (j(PrefKey.SERVER_TYPE)) {
            ko4.a = KinemasterService$ServerType.PRODUCT_SERVER;
        } else {
            ko4.a = KinemasterService$ServerType.TEST_SERVER;
        }
        if (j(PrefKey.NEO_ASSET_SERVER_TYPE)) {
            KinemasterService$ServerType kinemasterService$ServerType = KinemasterService$ServerType.PRODUCT_SERVER;
            kinemasterService$ServerType.getClass();
            ko4.b = kinemasterService$ServerType;
        } else {
            KinemasterService$ServerType kinemasterService$ServerType2 = KinemasterService$ServerType.TEST_SERVER;
            kinemasterService$ServerType2.getClass();
            ko4.b = kinemasterService$ServerType2;
        }
        if (j(PrefKey.ASSET_SERVER_TYPE)) {
            KinemasterService$ServerType kinemasterService$ServerType3 = KinemasterService$ServerType.PRODUCT_SERVER;
            kinemasterService$ServerType3.getClass();
            ko4.c = kinemasterService$ServerType3;
        } else {
            KinemasterService$ServerType kinemasterService$ServerType4 = KinemasterService$ServerType.TEST_SERVER;
            kinemasterService$ServerType4.getClass();
            ko4.c = kinemasterService$ServerType4;
        }
        ko4.d = "Android";
        ko4.j = true;
        String languageTag = Locale.getDefault().toLanguageTag();
        languageTag.getClass();
        ko4.f = pr8.q(languageTag, "_", "-");
        ko4.g = ((Number) com.kinemaster.app.modules.pref.a.d(PrefKey.ASSET_ENV, 3)).intValue();
        String c2 = dt0.c(this);
        if (c2 == null) {
            c2 = "";
        }
        ko4.k = c2;
        ko4.l = dt0.a(this);
        dt0.d();
        ko4.m = "8.2.14.37398.GP";
        ko4.n = "ZmNjMTM4ZGIyODEwOWEzOTg5NzNlZmMyMzA0YjdhNjJmMWQ5MDVlYzo";
        ko4.o = ko4.k;
        ko4.p = dt0.b(this);
        ko4.q = 29;
        super.onCreate();
        ok.a(this);
        I = true;
        cs9 cs9 = new cs9(getApplicationContext(), 5);
        if (zj2.h == null) {
            synchronized (zj2.g) {
                try {
                    if (zj2.h == null) {
                        zj2.h = new zj2(cs9);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        PrefKey prefKey = PrefKey.IS_SUBSCRIPTION_OR_PROMOTION;
        prefKey.getClass();
        PrefName prefName = prefKey.getPrefName();
        prefName.getClass();
        SharedPreferences f2 = com.kinemaster.app.modules.pref.a.f(prefName.getPrefName());
        if (f2 != null) {
            z2 = f2.contains(prefKey.getKey());
        } else {
            z2 = false;
        }
        if (z2) {
            if (((Boolean) com.kinemaster.app.modules.pref.a.d(prefKey, Boolean.FALSE)).booleanValue()) {
                PrefKey prefKey2 = PrefKey.IS_SUBSCRIPTION_FOR_ADS;
                Boolean bool = Boolean.TRUE;
                com.kinemaster.app.modules.pref.a.i(prefKey2, bool);
                com.kinemaster.app.modules.pref.a.i(PrefKey.IS_PROMOTION_FOR_ADS, bool);
            }
            com.kinemaster.app.modules.pref.a.a(prefKey);
        }
        boolean hasSystemFeature = getApplicationContext().getPackageManager().hasSystemFeature("org.chromium.arc.device_management");
        Context applicationContext = getApplicationContext();
        applicationContext.getClass();
        if (hasSystemFeature) {
            str2 = "chrome";
        } else {
            str2 = FontCollectionEntity.SYSTEM_COLLECTION_ID;
        }
        t94.w(applicationContext, "OSType", str2);
        Context applicationContext2 = getApplicationContext();
        applicationContext2.getClass();
        String languageTag2 = Locale.getDefault().toLanguageTag();
        languageTag2.getClass();
        t94.w(applicationContext2, "app_language", pr8.q(languageTag2, "_", "-"));
        Context applicationContext3 = getApplicationContext();
        applicationContext3.getClass();
        t94.w(applicationContext3, "device_identifier", dt0.c(this));
        b bVar = this.q;
        if (bVar != null) {
            AccountInfo u2 = bVar.u();
            if (u2 == null || (str3 = u2.getUserId()) == null) {
                str3 = "";
            }
            Context applicationContext4 = getApplicationContext();
            applicationContext4.getClass();
            t94.w(applicationContext4, "user_id_num", str3);
            Context applicationContext5 = getApplicationContext();
            applicationContext5.getClass();
            FirebaseAnalytics.getInstance(applicationContext5).a.zzo(str3);
            Object c3 = com.kinemaster.app.modules.pref.a.c(PrefKey.USE_SOURCE_MEDIA_FILE);
            Context applicationContext6 = getApplicationContext();
            applicationContext6.getClass();
            t94.w(applicationContext6, "OriginalContent", c3.toString());
            PrefKey prefKey3 = PrefKey.INCLUDE_BACKUP_PROJECT;
            Boolean bool2 = Boolean.FALSE;
            com.kinemaster.app.modules.pref.a.i(prefKey3, bool2);
            com.kinemaster.app.modules.pref.a.i(PrefKey.CAPTURE_LOG, bool2);
            com.kinemaster.app.modules.pref.a.i(PrefKey.EXPORT_PROJECT, bool2);
            com.kinemaster.app.modules.pref.a.i(PrefKey.MBROWSER_HIERARCHY, bool2);
            com.kinemaster.app.modules.pref.a.i(PrefKey.NETWORK_CURRENT_TIME_VALUE, 0L);
            com.kinemaster.app.modules.pref.a.i(PrefKey.SERVER_MAINTENANCE_MODE, bool2);
            PrefKey prefKey4 = PrefKey.DEVICE_LAST_LANGUAGE;
            Locale locale = Locale.getDefault();
            locale.getClass();
            com.kinemaster.app.modules.pref.a.i(prefKey4, gm1.M(locale));
            com.kinemaster.app.modules.pref.a.i(PrefKey.INVITATION_REFERER, "");
            c.c.E(this);
            if (z54.d == null) {
                z54.d = new z54(this);
            }
            if (com.kinemaster.app.screen.assetstore.editorpick.b.h == null) {
                com.kinemaster.app.screen.assetstore.editorpick.b.h = new com.kinemaster.app.screen.assetstore.editorpick.b(this);
            }
            os0 os0 = ld0.c;
            Context applicationContext7 = getApplicationContext();
            applicationContext7.getClass();
            if (ld0.d == null) {
                synchronized (os0) {
                    try {
                        if (ld0.d == null) {
                            ld0.d = new ld0(applicationContext7, 0);
                        }
                    } catch (Throwable th2) {
                        throw th2;
                    }
                }
            }
            if (he4.b == null) {
                ? obj = new Object();
                obj.a = new cs9(this);
                he4.b = obj;
            }
            this.e = new nl5(this);
            String packageName = getPackageName();
            t94.i(packageName.length() + "y{}x!hbtY3kg![gJ:Vh4uzZ" + packageName);
            HashMap hashMap = new HashMap();
            hashMap.put(new int[]{52, 63, 34, 116, 56, 63, 42}, new String[]{"12940175c51504b4246475650180214535e51475e1210144750414b5d5c514018021969401146435647414018021012606b7f73706b601e1210144750414b5d5c5c774f505750401802130e1210175a424d504643505e5750180214535e51475f4e121940114643564741401802101371766b7467701e1210144750414b5d5c5c774f505750401802100e1210175a424d504643505e5750180214535e51475f4e121940114643564741401802101371766b7467701e1210144750414b5d5c5c774f505750401802110e1210175a424d504643505e5750180214535e51475f4e121940114643564741401802101371766b7467701e1210144750414b5d5c5c774f505750401802160e1210175a424d504643505e5750180214535e51475f4e121940114643564741401802101371766b7467701e1210144750414b5d5c5c774f505750401802170e1210175a424d504643505e5750180214535e51475f4e121940114643564741401802101371766b7467701e1210144750414b5d5c5c774f505750401802140e1210175a424d504643505e5750180214535e51475f4f6e1210164b424750180210137771601e12101c535f5750180210135141475641504b4246401e1210124740424d514750180210167771706b62666d637c767d677c71706b6266601f4", "d6d84de9b97959db39f84dcc6dd84de9b97959db39f85a2848f989194dcc6d4dfcf958d94bfc7a3c48789bf80a2a489938bd0b2adb895cdbabb939ec9bfbeb68c81b6828c87b4aabe9b9994dad6d4d58f9c8394dcc6d4c3c0cb8ad6d4d793958db39f85a2848f989194dcc6d4d584a48d9ebfb5cc8e8a97b3b086a8b2a4b7c3c39ab7a4dad6d4db99929394dcc6d4d5b4b5b4dad6d4d58f9c8394dcc6d7c4cecb8", "57c055f1a16141c321e055d475c055f1a16141c321e0423050e1910155d47555c374f1311034f1f3c3f4d12474510003542470114203c17253636253636323913224e344a1e0f462b170a355b5755540e1d02155d475542414a0b57555612140c321e0423050e1910155d47555d1b1f111e40040a1305060e4d2f4e174441244f4f06355b57555a181312155d4755543534355b5755540e1d02155d4756454f4a0", "6833a6025292b230d213a6278633a6025292b230d213b1c3a31262f2a62786a6a2e3e78762c7c1f20723d16087d10321f201c20762b79110b1b7b030f2d2e191f7026211b772e7e032e3b2a64686a6b31223d2a62786a7d7e7534686a692d2b330d213b1c3a31262f2a62786a611b30222e7e0a14010300271a2d2b0f100a3d73060f2a64686a65272c2d2a62786a6b0a0b0a64686a6b31223d2a6278697a70753", "ad6ffa5e0eceee6c8e4ffa7bda6ffa5e0eceee6c8e4fed9fff4e3eaefa7bdafa4ceb1c5c8bfb9d8fae2eae4daebf3e5fdb0c7cfc4ddfcbdded0ebecebd7f8c5bbd8e0c9e0a2d7eae7dbb6efa1adafaef4e7f8efa7bdafb8bbb0f1adaface8eef6c8e4fed9fff4e3eaefa7bdafa8ddf2c6ccf7ecc0e6c8ccfae2c2eeb2ddf4def7eaeaffa1adafa0e2e9e8efa7bdafaecfcecfa1adafaef4e7f8efa7bdacbfb5b0f", "cf49dc7828e8c84aa869dc5dfc49dc7828e8c84aa869cbb9d9681888dc5dfcdcc9a89a4acbca0ab9bb0a98fbeded8898c9e86d6d697be8cb1a6acaeb9dedadf918bb2a58ed89f9db69fbbddc3cfcdcc96859a8dc5dfcddad9d293cfcdce8a8c94aa869cbb9d9681888dc5dfcdc982c589948e80b5aad0acaebf9d988fdcb0af9edebebdc3cfcdc2808b8a8dc5dfcdccadacadc3cfcdcc96859a8dc5dfceddd7d29", "a5ef7ade8e4e6eec0ecf7afb5aef7ade8e4e6eec0ecf6d1f7fcebe2e7afb5a7a5d4dcd7edd1d1dcb4b2c1fdc3c3f7e6f1c1fffcc1e1d1c4bdbfc0baddcfe5bfcec6d8cbcdecedc4edbfcee7a9a5a7a6fceff0e7afb5a7b0b3b8f9a5a7a4e0e6fec0ecf6d1f7fcebe2e7afb5a7a4fdf0bfe4e1b6e6eec9e4e2e1fbc2cfdaebe3bbcfd2e7a9a5a7a8eae1e0e7afb5a7a6c7c6c7a9a5a7a6fceff0e7afb5a4b7bdb8f"});
            L = new a[hashMap.size()];
            int i2 = -1;
            for (Map.Entry entry : hashMap.entrySet()) {
                a[] aVarArr = L;
                if (aVarArr != null) {
                    i2++;
                    aVarArr[i2] = new a((int[]) entry.getKey(), (String[]) entry.getValue());
                    a[] aVarArr2 = L;
                    if (aVarArr2 != null) {
                        a aVar = aVarArr2[i2];
                        if (aVar != null) {
                            HashMap hashMap2 = com.nexstreaming.app.general.nexasset.assetpackage.a.j;
                            int[] iArr = aVar.a;
                            char[] cArr = new char[iArr.length];
                            for (int i3 = 0; i3 < iArr.length; i3++) {
                                cArr[i3] = (char) (iArr[i3] ^ 90);
                            }
                            String str4 = new String(cArr);
                            if (str4.length() >= 1) {
                                HashMap hashMap3 = com.nexstreaming.app.general.nexasset.assetpackage.a.k;
                                if (hashMap3.get(str4) != null) {
                                    Log.d("AssetPackageReader", "This provider ID is already registered! ".concat(str4));
                                } else if (hashMap3.values().contains(aVar)) {
                                    Log.d("AssetPackageReader", "This provider is already registered! ");
                                } else {
                                    hashMap3.put(str4, aVar);
                                }
                            } else {
                                c.i("id is null or empty");
                                return;
                            }
                        }
                    } else {
                        t94.C("securityProvider");
                        throw null;
                    }
                } else {
                    t94.C("securityProvider");
                    throw null;
                }
            }
            ot m2 = ij3.m(this);
            zn4 zn4 = this.s;
            zn4.getClass();
            m2.g.b(zn4);
            registerActivityLifecycleCallbacks(this.v);
            com.nexstreaming.kinemaster.ad.d dVar = com.nexstreaming.kinemaster.ad.d.a;
            com.nexstreaming.kinemaster.ad.d.c(this);
            Context applicationContext8 = getApplicationContext();
            applicationContext8.getClass();
            File file = new File(applicationContext8.getFilesDir(), "segmentation");
            if (dt0.j(applicationContext8) && file.exists()) {
                cz2.Q(file);
            }
            Context applicationContext9 = getApplicationContext();
            applicationContext9.getClass();
            File file2 = new File(applicationContext9.getFilesDir(), "aistyle");
            if (!file2.exists()) {
                file2.mkdirs();
            }
            if (dt0.j(applicationContext9) && file2.exists()) {
                cz2.Q(file2);
            }
            Context applicationContext10 = getApplicationContext();
            applicationContext10.getClass();
            File file3 = new File(applicationContext10.getFilesDir(), "music_recommendation");
            if (!file3.exists()) {
                file3.mkdirs();
            }
            if (dt0.j(applicationContext10) && file3.exists()) {
                cz2.Q(file3);
            }
            di2.c = yi7.w();
            aq aqVar = bq.a;
            int i4 = l2a.a;
            if (pc7.d == null) {
                pc7 pc7 = new pc7(this);
                pc7.d = pc7;
                xj3.E(this.m, v92.a, (CoroutineStart) null, new KineMasterApplication$onCreate$3$1(pc7, (lm1<? super KineMasterApplication$onCreate$3$1>) null), 2);
            }
            xj3.E(this.m, v92.a, (CoroutineStart) null, new KineMasterApplication$setNotificationSetting$1((lm1<? super KineMasterApplication$setNotificationSetting$1>) null), 2);
            if (hr8.H((String) com.kinemaster.app.modules.pref.a.d(PrefKey.FCM_TOKEN, ""))) {
                synchronized (FirebaseMessaging.class) {
                    try {
                        instance = FirebaseMessaging.getInstance(FirebaseApp.d());
                    } catch (Throwable th3) {
                        while (true) {
                            throw th3;
                        }
                    }
                }
                if (instance.e.g()) {
                    task = Tasks.forException(new IllegalStateException("API disabled. Please use {@link #register()} instead or enable this API by removing {@code <meta-data android:name=\"firebase_messaging_installation_id_enabled\" android:value=\"true\" />} from your app's manifest."));
                } else {
                    FirebaseInstanceIdInternal firebaseInstanceIdInternal = instance.b;
                    if (firebaseInstanceIdInternal != null) {
                        task = firebaseInstanceIdInternal.b();
                    } else {
                        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
                        instance.h.execute(new ah1(7, instance, taskCompletionSource));
                        task = taskCompletionSource.getTask();
                    }
                }
                task.addOnSuccessListener(new g03(new a(this), 2));
            }
            this.k = true;
            gm1.q = new yk1(new bb(7), 11);
            if (((Boolean) com.kinemaster.app.modules.pref.a.d(PrefKey.PRIVACY_PERMISSION, Boolean.FALSE)).booleanValue()) {
                h();
            }
            g();
            gm1.w("Version: 8.2.14.37398.GP ");
            return;
        }
        t94.C("accountRepository");
        throw null;
    }

    public final void onLowMemory() {
        super.onLowMemory();
        if (this.k) {
            com.bumptech.glide.a a = com.bumptech.glide.a.a(this);
            a.getClass();
            t0a.a();
            a.c.e(0);
            a.b.j();
            c85 c85 = a.e;
            synchronized (c85) {
                c85.b(0);
            }
        }
    }

    public final void onTerminate() {
        if (this.F) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(this.G);
            unregisterReceiver(this.H);
        }
        super.onTerminate();
    }

    public final void onTrimMemory(int i2) {
        super.onTrimMemory(i2);
        if (this.k) {
            com.bumptech.glide.a.a(this).d(i2);
        }
    }
}
