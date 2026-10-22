package com.kinemaster.app.screen.launch.kmscheme;

import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import com.kinemaster.app.screen.base.BaseActivity;
import com.nexstreaming.kinemaster.ui.share.a;
import com.nexstreaming.kinemaster.usage.analytics.KMEvents;
import java.util.ArrayList;
import java.util.Iterator;
import kotlin.C2354Metadata;

@C2354Metadata(d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"}, d2 = {"Lcom/kinemaster/app/screen/launch/kmscheme/KMSchemePendingActivity;", "Lcom/kinemaster/app/screen/base/BaseActivity;", "<init>", "()V", "KineMaster"}, k = 1, mv = {2, 3, 0}, xi = 48)
/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public final class KMSchemePendingActivity extends BaseActivity {
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v3, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: android.app.NotificationManager} */
    /* JADX WARNING: Multi-variable type inference failed */
    public final void onCreate(Bundle bundle) {
        String str;
        String str2;
        String str3;
        String str4;
        StatusBarNotification[] activeNotifications;
        super.onCreate(bundle);
        Uri data = getIntent().getData();
        NotificationManager notificationManager = null;
        if (data != null) {
            str = data.getQueryParameter("inboxReadingId");
        } else {
            str = null;
        }
        if (str != null && !hr8.H(str)) {
            data = oc7.a(data, "inboxReadingId");
        }
        if (data != null) {
            str2 = data.getQueryParameter("notificationType");
        } else {
            str2 = null;
        }
        if (str2 != null && !hr8.H(str2)) {
            data = oc7.a(data, "notificationType");
        }
        if (data != null) {
            str3 = data.getQueryParameter("notificationTarget");
        } else {
            str3 = null;
        }
        if (str3 != null && !hr8.H(str3)) {
            data = oc7.a(data, "notificationTarget");
        }
        if (data != null) {
            str4 = data.getQueryParameter("notificationTag");
        } else {
            str4 = null;
        }
        if (str4 != null && !hr8.H(str4)) {
            if (!hr8.H(str4)) {
                Object systemService = getApplicationContext().getSystemService("notification");
                if (systemService instanceof NotificationManager) {
                    notificationManager = systemService;
                }
                if (!(notificationManager == null || (activeNotifications = notificationManager.getActiveNotifications()) == null)) {
                    ArrayList arrayList = new ArrayList();
                    int length = activeNotifications.length;
                    int i = 0;
                    while (i < length) {
                        StatusBarNotification statusBarNotification = activeNotifications[i];
                        StatusBarNotification[] statusBarNotificationArr = activeNotifications;
                        if (t94.c(statusBarNotification.getTag(), str4)) {
                            arrayList.add(statusBarNotification);
                        }
                        i++;
                        activeNotifications = statusBarNotificationArr;
                    }
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        notificationManager.cancel(str4, ((StatusBarNotification) it.next()).getId());
                    }
                }
            }
            data = oc7.a(data, "notificationTag");
        }
        Bundle d = yi7.d();
        d.putString("link_url", String.valueOf(data));
        KMEvents.SERVICE.logServiceEvent(KMEvents.EventType.PUSH_GOTO_PUSH_LINK, d);
        Intent b = oc7.b(data, this);
        if (str != null && !hr8.H(str) && str2 != null && !hr8.H(str2)) {
            b.putExtra("inboxReadingId", str);
            b.putExtra("notificationType", str2);
            if (str3 != null && !hr8.H(str3)) {
                b.putExtra("notificationTarget", str3);
            }
        }
        a.c().a();
        startActivity(b);
        finish();
    }
}
