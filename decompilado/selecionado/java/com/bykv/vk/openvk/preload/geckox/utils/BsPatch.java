package com.bykv.vk.openvk.preload.geckox.utils;

import java.io.File;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public class BsPatch {
    static {
        System.loadLibrary("geckox_bspatch");
    }

    public static void a(File file, File file2, File file3, String str) throws Exception {
        if (!file.exists()) {
            dr9.p(file.getAbsolutePath(), "full package not exist：");
        } else if (file2.exists()) {
            file3.mkdirs();
            File file4 = new File(file3, str);
            file4.delete();
            int patch = patch(file.getAbsolutePath(), file4.getAbsolutePath(), file2.getAbsolutePath());
            if (patch != 0) {
                StringBuilder w = qq2.w(patch, "patch merged failed, code：", " full:");
                w.append(file.getAbsolutePath());
                w.append(" patch:");
                w.append(file2.getAbsolutePath());
                w.append(" dest:");
                fj3.p(w, file4.getAbsolutePath());
            }
        } else {
            dr9.p(file2.getAbsolutePath(), "patch package not exist：");
        }
    }

    private static native int patch(String str, String str2, String str3) throws Exception;
}
