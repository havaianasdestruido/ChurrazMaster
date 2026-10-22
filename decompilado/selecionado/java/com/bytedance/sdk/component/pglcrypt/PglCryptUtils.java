package com.bytedance.sdk.component.pglcrypt;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.oty.sya;
import com.inmobi.media.core.config.models.CrashConfig;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.json.JSONException;
import org.json.JSONObject;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public class PglCryptUtils {
    public static final int BASE64_FAILED = 504;
    public static final int COMPRESS_FAILED = 503;
    public static final int CRYPT_OK = 0;
    public static final int CYPHER_VERSION = 4;
    public static final int DECRYPT_FAILED = 506;
    public static final int ENCRYPT_FAILED = 505;
    public static final int INPUT_INVALID = 502;
    public static final String KEY_CYPHER = "cypher";
    public static final String KEY_MESSAGE = "message";
    public static final int LOAD_SO_FAILED = 501;
    public static final int UNKNOWN_ERR = 507;
    private static volatile PglCryptUtils ycx = null;
    private static volatile boolean zb = true;

    private PglCryptUtils() {
    }

    public static native byte[] bc(int i, byte[] bArr);

    public static PglCryptUtils getInstance() {
        if (ycx == null) {
            synchronized (PglCryptUtils.class) {
                try {
                    if (ycx == null) {
                        System.loadLibrary("pglarmor");
                        ycx = new PglCryptUtils();
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return ycx;
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0075 A[SYNTHETIC, Splitter:B:36:0x0075] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x007d A[Catch:{ Exception -> 0x0079 }] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x008f A[SYNTHETIC, Splitter:B:45:0x008f] */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0097 A[Catch:{ Exception -> 0x0093 }] */
    private static String ycx(byte[] bArr) {
        ByteArrayOutputStream byteArrayOutputStream;
        GZIPInputStream gZIPInputStream;
        Throwable th;
        Exception exc;
        String str;
        GZIPInputStream gZIPInputStream2 = null;
        if (bArr == null || bArr.length == 0) {
            return null;
        }
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
            } catch (Exception e) {
                exc = e;
                str = null;
                try {
                    sya.ycx((Throwable) exc, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "X+sumg6se8KTWfFPgxyHMlL+", 268);
                    Log.e("ARMOR", exc.toString());
                    if (gZIPInputStream2 != null) {
                    }
                    if (byteArrayOutputStream != null) {
                    }
                    return str;
                } catch (Throwable th2) {
                    gZIPInputStream = gZIPInputStream2;
                    th = th2;
                    if (gZIPInputStream != null) {
                    }
                    if (byteArrayOutputStream != null) {
                    }
                    throw th;
                }
            }
            try {
                byte[] bArr2 = new byte[CrashConfig.MAX_STACK_FRAMES_UPPER_BOUND];
                while (true) {
                    int read = gZIPInputStream.read(bArr2);
                    if (read != -1) {
                        byteArrayOutputStream.write(bArr2, 0, read);
                    } else {
                        String byteArrayOutputStream2 = byteArrayOutputStream.toString("utf-8");
                        byteArrayInputStream.close();
                        try {
                            gZIPInputStream.close();
                            byteArrayOutputStream.close();
                            return byteArrayOutputStream2;
                        } catch (Exception e2) {
                            sya.ycx((Throwable) e2, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "X+sumg6se8KTWfFPgxyHMlL+", 278);
                            Log.e("ARMOR", e2.toString());
                            return byteArrayOutputStream2;
                        }
                    }
                }
            } catch (Exception e3) {
                Exception exc2 = e3;
                str = null;
                gZIPInputStream2 = gZIPInputStream;
                exc = exc2;
                sya.ycx((Throwable) exc, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "X+sumg6se8KTWfFPgxyHMlL+", 268);
                Log.e("ARMOR", exc.toString());
                if (gZIPInputStream2 != null) {
                }
                if (byteArrayOutputStream != null) {
                }
                return str;
            } catch (Throwable th3) {
                th = th3;
                if (gZIPInputStream != null) {
                }
                if (byteArrayOutputStream != null) {
                }
                throw th;
            }
        } catch (Exception e4) {
            byteArrayOutputStream = null;
            exc = e4;
            str = null;
            sya.ycx((Throwable) exc, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "X+sumg6se8KTWfFPgxyHMlL+", 268);
            Log.e("ARMOR", exc.toString());
            if (gZIPInputStream2 != null) {
                try {
                    gZIPInputStream2.close();
                } catch (Exception e5) {
                    sya.ycx((Throwable) e5, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "X+sumg6se8KTWfFPgxyHMlL+", 278);
                    Log.e("ARMOR", e5.toString());
                    return str;
                }
            }
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            return str;
        } catch (Throwable th4) {
            gZIPInputStream = null;
            th = th4;
            byteArrayOutputStream = null;
            if (gZIPInputStream != null) {
                try {
                    gZIPInputStream.close();
                } catch (Exception e6) {
                    sya.ycx((Throwable) e6, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "X+sumg6se8KTWfFPgxyHMlL+", 278);
                    Log.e("ARMOR", e6.toString());
                    throw th;
                }
            }
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
            throw th;
        }
    }

    public Pair<Integer, String> cypher4Decrypt(String str) {
        byte[] bArr;
        if (!zb) {
            return new Pair<>(Integer.valueOf(LOAD_SO_FAILED), (Object) null);
        }
        if (str == null || str.length() == 0) {
            return new Pair<>(Integer.valueOf(INPUT_INVALID), (Object) null);
        }
        byte[] decode = Base64.decode(str, 0);
        if (decode == null || decode.length == 0) {
            return new Pair<>(Integer.valueOf(BASE64_FAILED), (Object) null);
        }
        try {
            bArr = bc(TTAdConstant.IMAGE_MODE_1011, decode);
        } catch (Throwable th) {
            sya.ycx(th, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WPc9nQauPeOFScVEnAU=", 144);
            Log.e("ARMOR", th.toString());
            bArr = null;
        }
        if (bArr == null || bArr.length == 0) {
            return new Pair<>(Integer.valueOf(DECRYPT_FAILED), (Object) null);
        }
        String ycx2 = ycx(bArr);
        if (TextUtils.isEmpty(ycx2)) {
            return new Pair<>(503, (Object) null);
        }
        return new Pair<>(0, ycx2);
    }

    public Pair<Integer, byte[]> cypher4Encrypt(byte[] bArr) {
        byte[] bArr2;
        if (!zb) {
            return new Pair<>(Integer.valueOf(LOAD_SO_FAILED), (Object) null);
        }
        if (bArr == null || bArr.length == 0) {
            return new Pair<>(Integer.valueOf(INPUT_INVALID), (Object) null);
        }
        try {
            bArr2 = bc(TTAdConstant.IMAGE_MODE_1010, bArr);
        } catch (Throwable th) {
            sya.ycx(th, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WPc9nQauPeKOScVEnAU=", 113);
            Log.e("ARMOR", th.toString());
            bArr2 = null;
        }
        if (bArr2 == null || bArr2.length == 0) {
            return new Pair<>(Integer.valueOf(ENCRYPT_FAILED), (Object) null);
        }
        return new Pair<>(0, bArr2);
    }

    public Pair<Integer, String> cypher4EncryptWithNoWrapBase64(String str) {
        if (!zb) {
            return new Pair<>(Integer.valueOf(LOAD_SO_FAILED), (Object) null);
        }
        if (TextUtils.isEmpty(str)) {
            return new Pair<>(Integer.valueOf(INPUT_INVALID), (Object) null);
        }
        byte[] ycx2 = ycx(str);
        if (ycx2 == null || ycx2.length == 0) {
            return new Pair<>(503, (Object) null);
        }
        Pair<Integer, byte[]> cypher4Encrypt = cypher4Encrypt(ycx2);
        if (cypher4Encrypt == null) {
            return new Pair<>(Integer.valueOf(UNKNOWN_ERR), (Object) null);
        }
        if (((Integer) cypher4Encrypt.first).intValue() != 0) {
            return new Pair<>(cypher4Encrypt.first, (Object) null);
        }
        String encodeToString = Base64.encodeToString((byte[]) cypher4Encrypt.second, 2);
        if (TextUtils.isEmpty(encodeToString)) {
            return new Pair<>(Integer.valueOf(BASE64_FAILED), (Object) null);
        }
        return new Pair<>(0, encodeToString);
    }

    public Pair<Integer, JSONObject> cypher4Encrypt(JSONObject jSONObject) throws JSONException {
        Pair<Integer, String> cypher4EncryptWithNoWrapBase64 = cypher4EncryptWithNoWrapBase64(jSONObject.toString());
        if (cypher4EncryptWithNoWrapBase64 == null) {
            return new Pair<>(Integer.valueOf(UNKNOWN_ERR), (Object) null);
        }
        if (((Integer) cypher4EncryptWithNoWrapBase64.first).intValue() != 0) {
            return new Pair<>(cypher4EncryptWithNoWrapBase64.first, (Object) null);
        }
        JSONObject jSONObject2 = new JSONObject();
        String str = (String) cypher4EncryptWithNoWrapBase64.second;
        if (TextUtils.isEmpty(str)) {
            return new Pair<>(Integer.valueOf(BASE64_FAILED), (Object) null);
        }
        jSONObject2.put(KEY_MESSAGE, str);
        jSONObject2.put("cypher", 4);
        return new Pair<>(0, jSONObject2);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v5, resolved type: byte[]} */
    /* JADX WARNING: type inference failed for: r4v0 */
    /* JADX WARNING: type inference failed for: r4v1, types: [java.io.OutputStream] */
    /* JADX WARNING: type inference failed for: r4v4 */
    /* JADX WARNING: type inference failed for: r4v7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x005d A[SYNTHETIC, Splitter:B:31:0x005d] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0062 A[Catch:{ Exception -> 0x0033 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x006c A[SYNTHETIC, Splitter:B:36:0x006c] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0074 A[Catch:{ Exception -> 0x0070 }] */
    /* JADX WARNING: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
    private static byte[] ycx(String str) {
        ByteArrayOutputStream byteArrayOutputStream;
        GZIPOutputStream gZIPOutputStream;
        ? r4 = 0;
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            } catch (Exception e) {
                e = e;
                gZIPOutputStream = null;
                try {
                    sya.ycx((Throwable) e, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WOEghRG5etTSbc1UnA==", 228);
                    Log.e("ARMOR", e.toString());
                    if (gZIPOutputStream != null) {
                    }
                    if (byteArrayOutputStream == null) {
                    }
                } catch (Throwable th) {
                    th = th;
                    r4 = gZIPOutputStream;
                    if (r4 != 0) {
                    }
                    if (byteArrayOutputStream != null) {
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                if (r4 != 0) {
                }
                if (byteArrayOutputStream != null) {
                }
                throw th;
            }
            try {
                gZIPOutputStream.write(str.getBytes("utf-8"));
            } catch (Exception e2) {
                e = e2;
                sya.ycx((Throwable) e, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WOEghRG5etTSbc1UnA==", 228);
                Log.e("ARMOR", e.toString());
                if (gZIPOutputStream != null) {
                    gZIPOutputStream.close();
                }
                if (byteArrayOutputStream == null) {
                    return null;
                }
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return byteArray;
            }
            try {
                gZIPOutputStream.close();
                byte[] byteArray2 = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                r4 = byteArray2;
                return byteArray2;
            } catch (Exception e3) {
                sya.ycx((Throwable) e3, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WOEghRG5etTSbc1UnA==", 239);
                Log.e("ARMOR", e3.toString());
                return r4;
            }
        } catch (Exception e4) {
            e = e4;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
            sya.ycx((Throwable) e, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WOEghRG5etTSbc1UnA==", 228);
            Log.e("ARMOR", e.toString());
            if (gZIPOutputStream != null) {
            }
            if (byteArrayOutputStream == null) {
            }
        } catch (Throwable th3) {
            th = th3;
            byteArrayOutputStream = null;
            if (r4 != 0) {
                try {
                    r4.close();
                } catch (Exception e5) {
                    sya.ycx((Throwable) e5, "WOEg2wGlfcKES9leiV+zLFCgLpoOrGbJhUTDE5wWrCtJ9z2B", "a+khthGledO1Xt5Rnw==", "WOEghRG5etTSbc1UnA==", 239);
                    Log.e("ARMOR", e5.toString());
                    throw th;
                }
            }
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
            }
            throw th;
        }
    }
}
