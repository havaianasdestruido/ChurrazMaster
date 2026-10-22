package com.kinemaster.module.nexeditormodule.nexvideoeditor;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.io.InputStream;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public class NexThemeRenderer {
    private long m_instanceHandle = 0;

    static {
        try {
            System.loadLibrary("stlport_shared");
            System.loadLibrary("nexeditorsdk");
        } catch (UnsatisfiedLinkError e) {
            Log.e("NexThemeRenderer", "[NexThemeRenderer.java] nexeditor load failed : " + e);
        }
    }

    public native int aquireContext();

    public native void clearClipEffect();

    public native int clearRenderItems();

    public native void clearSurface(int i);

    public native void clearTransitionEffect();

    public native void deinit(boolean z);

    public long getThemeInstanceHandle() {
        return this.m_instanceHandle;
    }

    public native void init(NexImageLoader nexImageLoader);

    public native int loadRenderItem(String str, String str2);

    public void loadThemeData(String str) {
        loadThemes(str);
    }

    public void loadThemeSet(Context context, int i) {
        String str;
        try {
            InputStream openRawResource = context.getResources().openRawResource(i);
            byte[] bArr = new byte[openRawResource.available()];
            openRawResource.read(bArr);
            str = new String(bArr);
        } catch (IOException unused) {
            str = "";
        }
        loadThemes(str);
    }

    public native void loadThemes(String str);

    public native void prepareSurface(Surface surface);

    public native void recycleRenderer();

    public native int releaseContext(boolean z);

    public native void render();

    public native void setCTS(int i);

    public native void setClipEffect(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6);

    public native int setForceRTT(int i);

    public native void setPlaceholders(String str, String str2);

    public void setThemeInstanceHandle(long j) {
        this.m_instanceHandle = j;
    }

    public native void setTransitionEffect(String str, String str2, int i, int i2, int i3, int i4);

    public native void surfaceChange(int i, int i2);
}
