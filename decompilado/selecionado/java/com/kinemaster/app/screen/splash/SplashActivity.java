package com.kinemaster.app.screen.splash;

import android.os.Build;
import android.os.Bundle;
import kotlin.C2354Metadata;

@C2354Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/kinemaster/app/screen/splash/SplashActivity;", "Lcom/kinemaster/app/screen/base/nav/BaseNavActivity;", "<init>", "()V", "KineMaster"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public final class SplashActivity extends Hilt_SplashActivity {
    public static final /* synthetic */ int z = 0;

    public final void onCreate(Bundle bundle) {
        xl8 xl8;
        int i = Build.VERSION.SDK_INT;
        if (i >= 31) {
            xl8 = new wl8(this);
        } else {
            xl8 = new xl8(this);
        }
        xl8.a();
        xl8.b(new yk1(this, 15));
        super.onCreate(bundle);
        if (i >= 29) {
            getWindow().setNavigationBarContrastEnforced(false);
        }
    }

    public final boolean s() {
        return true;
    }

    public final int z() {
        return 2131820569;
    }
}
