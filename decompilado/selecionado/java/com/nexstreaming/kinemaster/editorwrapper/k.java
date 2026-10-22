package com.nexstreaming.kinemaster.editorwrapper;

import android.content.Context;
import com.kinemaster.module.nextask.task.ResultTask;
import com.nextreaming.nexeditorui.NexProjectHeader;
import com.nextreaming.nexeditorui.NexTimeline;
import com.nextreaming.nexeditorui.ReEncodedType;
import com.nextreaming.nexeditorui.d;
import com.nextreaming.nexeditorui.g;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlinx.coroutines.CoroutineStart;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public final class k implements un1 {
    public File a;
    public final OutputStream b;
    public final boolean c;
    public final boolean d;
    public final c57 e;
    public final jn1 f;
    public ZipOutputStream g;
    public final ArrayList h = new ArrayList();
    public long i;
    public long j;
    public long k;
    public yn8 l;
    public ResultTask m;
    public final byte[] n = new byte[32768];

    public k(File file, OutputStream outputStream, boolean z, boolean z2, c57 c57, jn1 jn1) {
        file.getClass();
        outputStream.getClass();
        jn1.getClass();
        this.a = file;
        this.b = outputStream;
        this.c = z;
        this.d = z2;
        this.e = c57;
        this.f = jn1;
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x006e */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x009a A[Catch:{ Exception -> 0x006e, all -> 0x0074, IOException -> 0x0072 }] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00a0 A[Catch:{ Exception -> 0x006e, all -> 0x0074, IOException -> 0x0072 }] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00c1 A[Catch:{ Exception -> 0x006e, all -> 0x0074, IOException -> 0x0072 }, LOOP:0: B:23:0x00bb->B:25:0x00c1, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00c5 A[SYNTHETIC] */
    public static final boolean a(k kVar, h hVar, File file) {
        byte[] bArr;
        ZipOutputStream zipOutputStream;
        d57 d57;
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        int read;
        bArr = kVar.n;
        NexProjectHeader b2 = hVar.b();
        NexTimeline nexTimeline = hVar.a;
        hVar.d = true;
        zipOutputStream = kVar.g;
        if (zipOutputStream == null) {
            return false;
        }
        try {
            ZipEntry zipEntry = new ZipEntry(kVar.a.getName());
            zipEntry.getName();
            zipOutputStream.putNextEntry(zipEntry);
            byte[] byteArray = b2.asProtoBuf().toByteArray();
            byte[] byteArray2 = nexTimeline.asProtoBuf(hVar).toByteArray();
            zipOutputStream.write(243);
            zipOutputStream.write(75);
            zipOutputStream.write(77);
            zipOutputStream.write(234);
            h(zipOutputStream, 1);
            g(zipOutputStream, lv2.a, byteArray);
            g(zipOutputStream, lv2.b, byteArray2);
            d57 = null;
            g(zipOutputStream, lv2.c, (byte[]) null);
            zipOutputStream.closeEntry();
            if (file == null || !file.exists()) {
                return true;
            }
            fileInputStream = new FileInputStream(file);
            d57 = e(fileInputStream, bArr.length);
            fileInputStream.close();
            fileInputStream2 = new FileInputStream(file);
            String name = file.getName();
            ZipEntry zipEntry2 = new ZipEntry(name + ".backup");
            if (d57 != null) {
                zipEntry2.setMethod(8);
            } else {
                zipEntry2.setMethod(0);
                zipEntry2.setSize(d57.a);
                zipEntry2.setCompressedSize(d57.b);
                zipEntry2.setCrc(d57.c);
            }
            zipEntry2.getName();
            Objects.toString(d57);
            zipOutputStream.putNextEntry(zipEntry2);
            while (true) {
                read = fileInputStream2.read(bArr);
                if (read <= 0) {
                    zipOutputStream.write(bArr, 0, read);
                } else {
                    zipOutputStream.closeEntry();
                    return true;
                }
            }
        } catch (Exception ) {
            fileInputStream.close();
            fileInputStream2 = new FileInputStream(file);
            String name2 = file.getName();
            ZipEntry zipEntry22 = new ZipEntry(name2 + ".backup");
            if (d57 != null) {
            }
            zipEntry22.getName();
            Objects.toString(d57);
            zipOutputStream.putNextEntry(zipEntry22);
            while (true) {
                read = fileInputStream2.read(bArr);
                if (read <= 0) {
                }
                zipOutputStream.write(bArr, 0, read);
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        } catch (Throwable th) {
            fileInputStream.close();
            throw th;
        }
    }

    public static final InputStream b(k kVar, yj5 yj5) {
        kVar.getClass();
        if (yj5 == null) {
            return null;
        }
        if (yj5.v("media")) {
            return yj5.G();
        }
        try {
            File h2 = yj5.h();
            if (h2 != null) {
                return new FileInputStream(h2);
            }
            return null;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static final Object c(k kVar, long j2, SuspendLambda suspendLambda) {
        kVar.getClass();
        i12 i12 = v92.a;
        Object N = xj3.N(y85.a, new ProjectExporter$onProgress$2(j2, 100, kVar, (lm1<? super ProjectExporter$onProgress$2>) null), suspendLambda);
        if (N == CoroutineSingletons.COROUTINE_SUSPENDED) {
            return N;
        }
        return gy9.a;
    }

    /* JADX WARNING: type inference failed for: r0v16, types: [java.lang.Object, jf7] */
    /* JADX WARNING: type inference failed for: r6v39, types: [java.lang.Object, jf7] */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0232 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:102:0x0235 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0258 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x027e A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0284 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x028e A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:116:0x0294 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x029e A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02a4 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x02ae A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02b4 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x02be A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c4 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x015d A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0160 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0183 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01a9 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x01af A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01b9 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01bf A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x01c9 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01cf A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01d9 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01e0 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:86:0x01eb A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01f2 A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x020a A[Catch:{ IllegalStateException -> 0x02ec }] */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x020d A[Catch:{ IllegalStateException -> 0x02ec }] */
    public static final boolean d(k kVar, HashMap hashMap, h hVar, n56 n56) {
        String str;
        String str2;
        jf7 h1;
        yj5 yj5;
        String normalize;
        yj5 z;
        String str3;
        jf7 h12;
        int i2;
        jf7 h13;
        int i3;
        jf7 h14;
        int i4;
        jf7 h15;
        int i5;
        kf7 kf7;
        jf7 Y;
        yj5 yj52;
        String normalize2;
        yj5 z2;
        String str4;
        jf7 Y2;
        int i6;
        jf7 Y3;
        int i7;
        jf7 Y4;
        int i8;
        jf7 Y5;
        int i9;
        jf7 Y6;
        String str5;
        kf7 kf72;
        yj5 yj53;
        k kVar2 = kVar;
        HashMap hashMap2 = hashMap;
        h hVar2 = hVar;
        n56 n562 = n56;
        kVar2.getClass();
        try {
            yj5 a2 = n562.a2();
            if (a2 == null) {
                return false;
            }
            String str6 = null;
            if (a2.v("media")) {
                str2 = a2.g();
                str = a2.E();
                if (!hr8.H(str2) && !pr8.k(str, ".".concat(str2), true)) {
                    str = str + "." + str2;
                }
            } else if (a2.n()) {
                str2 = a2.g();
                str = a2.d((Locale) null, true);
            } else {
                str2 = a2.g();
                str = a2.E();
            }
            String y = t94.y(str, ".".concat(str2));
            Normalizer.Form form = Normalizer.Form.NFC;
            String normalize3 = Normalizer.normalize(y, form);
            if (!hashMap2.containsKey(normalize3)) {
                hashMap2.put(normalize3, a2);
                kVar2.i += a2.H();
            }
            AtomicBoolean atomicBoolean = yj5.l;
            yj5 z3 = cj3.z("./contents/" + normalize3, (String) null, new a57(hVar2, 0));
            if (z3 != null) {
                if (n562 instanceof g) {
                    ((g) n562).O3(z3);
                } else if (n562 instanceof d) {
                    ((d) n562).h3(z3);
                } else if (n562 instanceof d14) {
                    ((d14) n562).a5(z3);
                } else if (n562 instanceof o5a) {
                    ((o5a) n562).X4(z3);
                }
            }
            if (n562 instanceof d14) {
                if (((d14) n562).u()) {
                    yj5 yj54 = ((d14) n562).N0;
                    if (yj54 != null) {
                        normalize3.getClass();
                        String R = hr8.R(normalize3, ".", "mask");
                        hashMap2.put(R, yj54);
                        kVar2.i += yj54.H();
                        ((d14) n562).N0 = cj3.z("./contents/".concat(R), (String) null, new a57(hVar2, 1));
                    }
                    if ((n562 instanceof vi9) && ((vi9) n562).I1()) {
                        Y = ((vi9) n562).Y();
                        if (Y == null) {
                            yj52 = Y.b;
                        } else {
                            yj52 = null;
                        }
                        if (yj52 != null && !yj52.m()) {
                            normalize2 = Normalizer.normalize(t94.y(yj52.E(), ".".concat(yj52.g())), form);
                            if (!hashMap2.containsKey(normalize2)) {
                                hashMap2.put(normalize2, yj52);
                                kVar2.i += yj52.H();
                            }
                            z2 = cj3.z("./contents/" + normalize2, (String) null, new a57(hVar2, 3));
                            if (z2 == null) {
                                str4 = z2.J();
                            } else {
                                str4 = null;
                            }
                            Y2 = ((vi9) n562).Y();
                            if (Y2 == null) {
                                i6 = Y2.d();
                            } else {
                                i6 = 0;
                            }
                            Y3 = ((vi9) n562).Y();
                            if (Y3 == null) {
                                i7 = Y3.c();
                            } else {
                                i7 = 0;
                            }
                            Y4 = ((vi9) n562).Y();
                            if (Y4 == null) {
                                i8 = Y4.g();
                            } else {
                                i8 = 0;
                            }
                            Y5 = ((vi9) n562).Y();
                            if (Y5 == null) {
                                i9 = Y5.f();
                            } else {
                                i9 = 0;
                            }
                            yg6 yg6 = new yg6(str4, i6, i7, i8, i9);
                            ReEncodedType reEncodedType = ReEncodedType.AIStyle;
                            Y6 = ((vi9) n562).Y();
                            if (Y6 == null || (kf72 = Y6.a) == null) {
                                str5 = null;
                            } else {
                                str5 = kf72.b;
                            }
                            kf7 kf73 = new kf7(reEncodedType, str5, yg6);
                            ? obj = new Object();
                            obj.a = kf73;
                            ((vi9) n562).z0(obj);
                        }
                    }
                    if ((n562 instanceof jj9) && ((jj9) n562).U0()) {
                        h1 = ((jj9) n562).h1();
                        if (h1 == null) {
                            yj5 = h1.b;
                        } else {
                            yj5 = null;
                        }
                        if (yj5 != null && !yj5.m()) {
                            normalize = Normalizer.normalize(t94.y(yj5.E(), ".".concat(yj5.g())), form);
                            if (!hashMap2.containsKey(normalize)) {
                                hashMap2.put(normalize, yj5);
                                kVar2.i += yj5.H();
                            }
                            z = cj3.z("./contents/" + normalize, (String) null, new a57(hVar2, 4));
                            if (z == null) {
                                str3 = z.J();
                            } else {
                                str3 = null;
                            }
                            h12 = ((jj9) n562).h1();
                            if (h12 == null) {
                                i2 = h12.d();
                            } else {
                                i2 = 0;
                            }
                            h13 = ((jj9) n562).h1();
                            if (h13 == null) {
                                i3 = h13.c();
                            } else {
                                i3 = 0;
                            }
                            h14 = ((jj9) n562).h1();
                            if (h14 == null) {
                                i4 = h14.g();
                            } else {
                                i4 = 0;
                            }
                            h15 = ((jj9) n562).h1();
                            if (h15 == null) {
                                i5 = h15.f();
                            } else {
                                i5 = 0;
                            }
                            yg6 yg62 = new yg6(str3, i2, i3, i4, i5);
                            ReEncodedType reEncodedType2 = ReEncodedType.NoiseReduction;
                            jf7 h16 = ((jj9) n562).h1();
                            if (!(h16 == null || (kf7 = h16.a) == null)) {
                                str6 = kf7.b;
                            }
                            kf7 kf74 = new kf7(reEncodedType2, str6, yg62);
                            ? obj2 = new Object();
                            obj2.a = kf74;
                            ((jj9) n562).R0(obj2);
                        }
                    }
                    return true;
                }
            }
            if ((n562 instanceof g) && ((g) n562).v3() && ((g) n562).u() && (yj53 = ((g) n562).b1) != null) {
                normalize3.getClass();
                String R2 = hr8.R(normalize3, ".", "mask");
                hashMap2.put(R2, yj53);
                kVar2.i += yj53.H();
                ((g) n562).b1 = cj3.z("./contents/".concat(R2), (String) null, new a57(hVar2, 2));
            }
            Y = ((vi9) n562).Y();
            if (Y == null) {
            }
            normalize2 = Normalizer.normalize(t94.y(yj52.E(), ".".concat(yj52.g())), form);
            if (!hashMap2.containsKey(normalize2)) {
            }
            z2 = cj3.z("./contents/" + normalize2, (String) null, new a57(hVar2, 3));
            if (z2 == null) {
            }
            Y2 = ((vi9) n562).Y();
            if (Y2 == null) {
            }
            Y3 = ((vi9) n562).Y();
            if (Y3 == null) {
            }
            Y4 = ((vi9) n562).Y();
            if (Y4 == null) {
            }
            Y5 = ((vi9) n562).Y();
            if (Y5 == null) {
            }
            yg6 yg63 = new yg6(str4, i6, i7, i8, i9);
            ReEncodedType reEncodedType3 = ReEncodedType.AIStyle;
            Y6 = ((vi9) n562).Y();
            if (Y6 == null || (kf72 = Y6.a) == null) {
            }
            kf7 kf732 = new kf7(reEncodedType3, str5, yg63);
            ? obj3 = new Object();
            obj3.a = kf732;
            ((vi9) n562).z0(obj3);
            h1 = ((jj9) n562).h1();
            if (h1 == null) {
            }
            normalize = Normalizer.normalize(t94.y(yj5.E(), ".".concat(yj5.g())), form);
            if (!hashMap2.containsKey(normalize)) {
            }
            z = cj3.z("./contents/" + normalize, (String) null, new a57(hVar2, 4));
            if (z == null) {
            }
            h12 = ((jj9) n562).h1();
            if (h12 == null) {
            }
            h13 = ((jj9) n562).h1();
            if (h13 == null) {
            }
            h14 = ((jj9) n562).h1();
            if (h14 == null) {
            }
            h15 = ((jj9) n562).h1();
            if (h15 == null) {
            }
            yg6 yg622 = new yg6(str3, i2, i3, i4, i5);
            ReEncodedType reEncodedType22 = ReEncodedType.NoiseReduction;
            jf7 h162 = ((jj9) n562).h1();
            str6 = kf7.b;
            kf7 kf742 = new kf7(reEncodedType22, str6, yg622);
            ? obj22 = new Object();
            obj22.a = kf742;
            ((jj9) n562).R0(obj22);
            return true;
        } catch (IllegalStateException unused) {
        }
    }

    public static d57 e(InputStream inputStream, int i2) {
        CRC32 crc32 = new CRC32();
        crc32.reset();
        byte[] bArr = new byte[i2];
        long j2 = 0;
        while (true) {
            int read = inputStream.read(bArr, 0, i2);
            if (read != -1) {
                crc32.update(bArr, 0, read);
                j2 += (long) read;
            } else {
                return new d57(j2, j2, crc32.getValue());
            }
        }
    }

    public static void g(ZipOutputStream zipOutputStream, int i2, byte[] bArr) {
        zipOutputStream.getClass();
        h(zipOutputStream, i2);
        if (bArr == null) {
            h(zipOutputStream, 0);
            return;
        }
        h(zipOutputStream, bArr.length);
        zipOutputStream.write(bArr);
    }

    public static void h(ZipOutputStream zipOutputStream, int i2) {
        zipOutputStream.getClass();
        zipOutputStream.write((i2 >> 24) & 255);
        zipOutputStream.write((i2 >> 16) & 255);
        zipOutputStream.write((i2 >> 8) & 255);
        zipOutputStream.write(i2 & 255);
    }

    public final void f(Context context, boolean z, boolean z2) {
        context.getClass();
        if (z || this.d) {
            com.nexstreaming.kinemaster.project.util.d dVar = com.nexstreaming.kinemaster.project.util.d.a;
            com.nexstreaming.kinemaster.project.util.d.r(this.a, new j(this, z2), (mn1) null, 28);
            return;
        }
        File d2 = wn4.d(".tmpProjectExport");
        int hashCode = this.a.getAbsolutePath().hashCode();
        ox1.L(16);
        String num = Integer.toString(hashCode, 16);
        num.getClass();
        File file = new File(d2, num.concat(".kine"));
        file.mkdirs();
        File file2 = new File(file, this.a.getName());
        File file3 = this.a;
        b57 b57 = new b57(this, context, file, z2);
        i12 i12 = v92.a;
        xj3.E(this, e02.b, (CoroutineStart) null, new ProjectExporter$prepareProjectFile$1(true, file3, b57, file2, (lm1<? super ProjectExporter$prepareProjectFile$1>) null), 2);
    }

    public final jn1 m() {
        i12 i12 = v92.a;
        return e02.b.plus(this.f);
    }
}
