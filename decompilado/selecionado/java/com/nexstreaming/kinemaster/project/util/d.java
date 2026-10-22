package com.nexstreaming.kinemaster.project.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.inmobi.media.core.config.models.CrashConfig;
import com.kinemaster.app.database.util.ProjectDatabaseHelper$GenerateUniqueType;
import com.kinemaster.app.database.util.e;
import com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditor;
import com.kinemaster.module.nextask.task.Task;
import com.nexstreaming.kinemaster.editorwrapper.h;
import com.nexstreaming.kinemaster.usage.analytics.a;
import com.nextreaming.nexeditorui.NexProjectHeader;
import com.nextreaming.nexeditorui.NexTimeline;
import com.nextreaming.nexeditorui.NexVideoClipItem$CropMode;
import com.nextreaming.nexeditorui.TemplarReplaceableTag;
import com.nextreaming.nexeditorui.g;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.b;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.FileWalkDirection;
import kotlinx.coroutines.CoroutineStart;
import org.keyczar.Keyczar;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public final class d implements un1 {
    public static final d a = new Object();
    public static final AtomicBoolean b = new AtomicBoolean(false);
    public static final AtomicBoolean c = new AtomicBoolean(false);
    public static final lv8 d = l68.a();
    public static final ConcurrentHashMap e = new ConcurrentHashMap();
    public static yn8 f;
    public static yn8 g;
    public static yn8 h;

    public static Object C(File file, lm1 lm1) {
        i12 i12 = v92.a;
        return xj3.N(e02.b, new ProjectHelper$sizeOfProject$2(file, (lm1<? super ProjectHelper$sizeOfProject$2>) null), lm1);
    }

    public static boolean D(File file, h hVar) {
        yj5 a2;
        File file2;
        yj5 yj5;
        if (file == null || hVar == null) {
            return false;
        }
        File file3 = new File(file, "contents/");
        Iterator<p46> it = hVar.a.getPrimaryItems().iterator();
        boolean z = false;
        while (true) {
            File file4 = null;
            if (!it.hasNext()) {
                break;
            }
            p46 next = it.next();
            if ((next instanceof g) && (yj5 = ((g) next).c) != null && yj5.F()) {
                try {
                    yj5 yj52 = ((g) next).c;
                    if (yj52 != null) {
                        file4 = yj52.i(file3);
                    }
                    if (file4 != null && file4.exists()) {
                        ((g) next).W3(file4.getAbsolutePath());
                        file4.getAbsolutePath();
                        z = true;
                    }
                } catch (Exception e2) {
                    if (b.compareAndSet(false, true)) {
                        a.g(new RuntimeException("[ProjectHelper] updateCopiedClip(Primary) failure", a.k(e2)));
                    }
                }
            }
        }
        for (r46 next2 : hVar.a.getSecondaryItems()) {
            if (((next2 instanceof kj5) || (next2 instanceof com.nextreaming.nexeditorui.d)) && (a2 = next2.a2()) != null && a2.F()) {
                try {
                    yj5 a22 = next2.a2();
                    if (a22 != null) {
                        file2 = a22.i(file3);
                    } else {
                        file2 = null;
                    }
                    if (file2 != null && file2.exists()) {
                        if (next2 instanceof kj5) {
                            ((kj5) next2).F4(file2.getAbsolutePath());
                        } else if (next2 instanceof com.nextreaming.nexeditorui.d) {
                            com.nextreaming.nexeditorui.d dVar = (com.nextreaming.nexeditorui.d) next2;
                            String absolutePath = file2.getAbsolutePath();
                            AtomicBoolean atomicBoolean = yj5.l;
                            dVar.c = cj3.A(absolutePath, (String) null, (jm3) null, 4);
                            dVar.T = "";
                            dVar.c3();
                        }
                        file2.getAbsolutePath();
                        z = true;
                    }
                } catch (Exception e3) {
                    if (c.compareAndSet(false, true)) {
                        a.g(new RuntimeException("[ProjectHelper] updateCopiedClip(Layer) failure", a.k(e3)));
                    }
                }
            }
        }
        return z;
    }

    public static final String a(sl5 sl5, File file) {
        InputStream G;
        try {
            String g2 = sl5.g();
            if (g2 == null) {
                return "";
            }
            if (hr8.H(g2)) {
                return "";
            }
            if (!file.exists()) {
                file.mkdir();
            }
            File file2 = new File(file, g2);
            if (file2.exists()) {
                file2.delete();
            } else {
                file2.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            yj5 j = sl5.j();
            if (j == null || (G = j.G()) == null) {
                return "";
            }
            try {
                byte[] bArr = new byte[4096];
                while (true) {
                    int read = G.read(bArr);
                    if (read > 0) {
                        fileOutputStream.write(bArr, 0, read);
                    } else {
                        fileOutputStream.flush();
                        String absolutePath = file2.getAbsolutePath();
                        absolutePath.getClass();
                        G.close();
                        return absolutePath;
                    }
                }
            } catch (IOException unused) {
                G.close();
                return "";
            } catch (Throwable th) {
                ox1.M(G, th);
                throw th;
            }
        } catch (Exception unused2) {
            return "";
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x004f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        defpackage.ox1.M(r4, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0053, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0055, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        defpackage.ox1.M(r3, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0059, code lost:
        throw r5;
     */
    public static final String b(Context context, Uri uri, File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        String w = w(context, uri);
        if (w == null) {
            w = qq2.m(System.currentTimeMillis(), "drop_");
        }
        File file2 = new File(file, w);
        if (file2.exists()) {
            file2.delete();
        } else {
            file2.createNewFile();
        }
        try {
            InputStream openInputStream = context.getContentResolver().openInputStream(uri);
            if (openInputStream == null) {
                return "";
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            cv9.i(openInputStream, fileOutputStream);
            fileOutputStream.close();
            String absolutePath = file2.getAbsolutePath();
            openInputStream.close();
            if (absolutePath == null) {
                return "";
            }
            return absolutePath;
        } catch (IOException unused) {
            return "";
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: com.nexstreaming.kinemaster.editorwrapper.h} */
    /* JADX WARNING: type inference failed for: r1v1, types: [u97, java.lang.Object] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x003b  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0025  */
    public static final Object c(d dVar, Context context, h hVar, ContinuationImpl continuationImpl) {
        ProjectHelper$reloadThumbnail$1 projectHelper$reloadThumbnail$1;
        int i;
        Bitmap bitmap;
        p46 p46;
        dVar.getClass();
        if (continuationImpl instanceof ProjectHelper$reloadThumbnail$1) {
            projectHelper$reloadThumbnail$1 = (ProjectHelper$reloadThumbnail$1) continuationImpl;
            int i2 = projectHelper$reloadThumbnail$1.label;
            if ((i2 & Integer.MIN_VALUE) != 0) {
                projectHelper$reloadThumbnail$1.label = i2 - Integer.MIN_VALUE;
                Object obj = projectHelper$reloadThumbnail$1.result;
                Object obj2 = CoroutineSingletons.COROUTINE_SUSPENDED;
                i = projectHelper$reloadThumbnail$1.label;
                if (i != 0) {
                    b.b(obj);
                    projectHelper$reloadThumbnail$1.L$0 = null;
                    projectHelper$reloadThumbnail$1.L$1 = hVar;
                    projectHelper$reloadThumbnail$1.label = 1;
                    by0 by0 = new by0(1, xj3.A(projectHelper$reloadThumbnail$1));
                    by0.u();
                    NexTimeline nexTimeline = hVar.a;
                    ? obj3 = new Object();
                    xq4 xq4 = new xq4(by0);
                    nexTimeline.getClass();
                    if (context == null) {
                        xq4.g((Bitmap) null);
                    }
                    Iterator<p46> it = nexTimeline.getPrimaryItems().iterator();
                    boolean z = false;
                    int i3 = 0;
                    while (true) {
                        if (!it.hasNext()) {
                            p46 = null;
                            break;
                        }
                        p46 = it.next();
                        if (p46 != null && (p46 instanceof g)) {
                            g gVar = (g) p46;
                            if (!gVar.y3()) {
                                break;
                            } else if (!z) {
                                i3 = gVar.h3();
                                z = true;
                            }
                        }
                    }
                    if (p46 == null && z) {
                        try {
                            Bitmap createBitmap = Bitmap.createBitmap(16, 9, Bitmap.Config.ARGB_8888);
                            createBitmap.getClass();
                            new Canvas(createBitmap).drawColor(i3);
                            xq4.g(createBitmap);
                        } catch (IllegalArgumentException unused) {
                            xq4.g((Bitmap) null);
                        }
                    } else if (p46 == null) {
                        try {
                            Bitmap createBitmap2 = Bitmap.createBitmap(16, 9, Bitmap.Config.ARGB_8888);
                            createBitmap2.getClass();
                            new Canvas(createBitmap2).drawColor(-16777216);
                            xq4.g(createBitmap2);
                        } catch (IllegalArgumentException unused2) {
                            xq4.g((Bitmap) null);
                        }
                    } else {
                        g gVar2 = (g) p46;
                        if (context != null) {
                            gVar2.m3(mw6.c).onResultAvailable(new t97(obj3, xq4)).onFailure((Task.OnFailListener) new t97(obj3, xq4));
                        }
                    }
                    obj = by0.t();
                    CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
                    if (obj == obj2) {
                        return obj2;
                    }
                } else if (i == 1) {
                    hVar = projectHelper$reloadThumbnail$1.L$1;
                    Context context2 = (Context) projectHelper$reloadThumbnail$1.L$0;
                    b.b(obj);
                } else {
                    c.o("call to 'resume' before 'invoke' with coroutine");
                    return null;
                }
                bitmap = (Bitmap) obj;
                if (bitmap != null) {
                    hVar.b().setDefaultThumbnail(bitmap);
                    bitmap.recycle();
                }
                return gy9.a;
            }
        }
        projectHelper$reloadThumbnail$1 = new ProjectHelper$reloadThumbnail$1(dVar, continuationImpl);
        Object obj4 = projectHelper$reloadThumbnail$1.result;
        Object obj22 = CoroutineSingletons.COROUTINE_SUSPENDED;
        i = projectHelper$reloadThumbnail$1.label;
        if (i != 0) {
        }
        bitmap = (Bitmap) obj4;
        if (bitmap != null) {
        }
        return gy9.a;
    }

    public static final long d(File file, h hVar) {
        yj5 a2;
        yj5 yj5;
        try {
            D(file, hVar);
        } catch (Exception unused) {
        }
        HashMap hashMap = new HashMap();
        List<p46> primaryItems = hVar.a.getPrimaryItems();
        ArrayList y = qq2.y(primaryItems);
        for (T next : primaryItems) {
            p46 p46 = (p46) next;
            if ((p46 instanceof g) && (yj5 = ((g) p46).c) != null && yj5.v("path")) {
                y.add(next);
            }
        }
        Iterator it = y.iterator();
        while (it.hasNext()) {
            yj5 a22 = ((p46) it.next()).a2();
            if (a22 != null && !hashMap.containsKey(a22.J())) {
                hashMap.put(a22.J(), Long.valueOf(a22.H()));
            }
        }
        List<r46> secondaryItems = hVar.a.getSecondaryItems();
        ArrayList y2 = qq2.y(secondaryItems);
        for (T next2 : secondaryItems) {
            r46 r46 = (r46) next2;
            if ((r46 instanceof kj5) && (a2 = ((kj5) r46).a2()) != null && a2.v("path")) {
                y2.add(next2);
            }
        }
        Iterator it2 = y2.iterator();
        while (it2.hasNext()) {
            yj5 a23 = ((r46) it2.next()).a2();
            if (a23 != null && !hashMap.containsKey(a23.J())) {
                hashMap.put(a23.J(), Long.valueOf(a23.H()));
            }
        }
        Collection<Number> values = hashMap.values();
        values.getClass();
        long j = 0;
        for (Number longValue : values) {
            j += longValue.longValue();
        }
        return j;
    }

    public static void e(ProjectJob projectJob, od4 od4) {
        ConcurrentHashMap concurrentHashMap = e;
        HashSet hashSet = (HashSet) concurrentHashMap.get(projectJob);
        if (hashSet == null) {
            hashSet = new HashSet();
        }
        ArrayList arrayList = new ArrayList();
        for (Object next : hashSet) {
            if (((od4) next).isCompleted()) {
                arrayList.add(next);
            }
        }
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            hashSet.remove((od4) it.next());
        }
        if (od4 != null) {
            hashSet.add(od4);
        }
        concurrentHashMap.put(projectJob, hashSet);
    }

    public static void f() {
        x(ProjectJob.CONVERT);
    }

    public static void g() {
        x(ProjectJob.COPY);
    }

    public static File i() {
        String str;
        if (wn4.k()) {
            str = "Demo Project";
        } else {
            str = o();
        }
        File file = new File(wn4.i("Project").getAbsolutePath(), qq2.r(UUID.randomUUID().toString(), ".kine"));
        String concat = str.concat(".kmproject");
        if (file.exists() || file.mkdirs()) {
            return new File(file, concat);
        }
        return null;
    }

    public static void j(un1 un1, File file, NexVideoClipItem$CropMode nexVideoClipItem$CropMode, int i, int i2, String str, h47 h47) {
        un1.getClass();
        str.getClass();
        i12 i12 = v92.a;
        xj3.E(un1, e02.b, (CoroutineStart) null, new ProjectHelper$createNewProject$1(nexVideoClipItem$CropMode, i, i2, file, str, h47, (lm1<? super ProjectHelper$createNewProject$1>) null), 2);
    }

    public static Object k(com.kinemaster.app.repository.project.a aVar, ye3 ye3, lm1 lm1) {
        i12 i12 = v92.a;
        Object N = xj3.N(e02.b, new ProjectHelper$createNewProjectTitle$2(aVar, ye3, (lm1<? super ProjectHelper$createNewProjectTitle$2>) null), lm1);
        if (N == CoroutineSingletons.COROUTINE_SUSPENDED) {
            return N;
        }
        return gy9.a;
    }

    /* JADX WARNING: Removed duplicated region for block: B:76:0x016d  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0175  */
    public static Object l(Context context, File file, float f2, NexVideoClipItem$CropMode nexVideoClipItem$CropMode, Integer num, Integer num2, String str, SuspendLambda suspendLambda, int i) {
        NexVideoClipItem$CropMode nexVideoClipItem$CropMode2;
        Integer num3;
        Integer num4;
        String str2;
        Object obj;
        String str3;
        String str4;
        ArrayList arrayList;
        boolean z;
        String str5;
        File[] listFiles;
        if ((i & 8) != 0) {
            nexVideoClipItem$CropMode2 = null;
        } else {
            nexVideoClipItem$CropMode2 = nexVideoClipItem$CropMode;
        }
        if ((i & 16) != 0) {
            num3 = null;
        } else {
            num3 = num;
        }
        if ((i & 32) != 0) {
            num4 = null;
        } else {
            num4 = num2;
        }
        if ((i & 64) != 0) {
            str2 = null;
        } else {
            str2 = str;
        }
        xv7 xv7 = new xv7(xj3.A(suspendLambda));
        String parent = file.getParent();
        String name = file.getName();
        String str6 = "";
        if (name == null || !pr8.k(name, ".nexvideoproject", false)) {
            obj = null;
            String name2 = file.getName();
            if (name2 != null) {
                int K = hr8.K(name2, ".kmproject", 0, 6);
                if (K != -1) {
                    name2 = name2.substring(0, K);
                }
                str3 = name2;
            } else {
                str3 = str6;
            }
        } else {
            str3 = file.getName();
            str3.getClass();
            obj = null;
            int K2 = hr8.K(str3, ".nexvideoproject", 0, 6);
            if (K2 != -1) {
                str3 = str3.substring(0, K2);
            }
        }
        String D = qq2.D("-", context.getString(2132019763));
        String R = hr8.R(str3, D, str6);
        if (R.length() > 0 && !pr8.k(R, D, false)) {
            R = R.concat(D);
        }
        if (file.getParentFile() != null) {
            File parentFile = file.getParentFile();
            if (parentFile == null || (listFiles = parentFile.listFiles()) == null) {
                str4 = str2;
                arrayList = new ArrayList();
            } else {
                ArrayList arrayList2 = new ArrayList();
                int length = listFiles.length;
                int i2 = 0;
                while (i2 < length) {
                    File file2 = listFiles[i2];
                    String str7 = str2;
                    String name3 = file2.getName();
                    name3.getClass();
                    File[] fileArr = listFiles;
                    if (pr8.k(name3, ".kmproject", false)) {
                        arrayList2.add(file2);
                    }
                    i2++;
                    listFiles = fileArr;
                    str2 = str7;
                }
                str4 = str2;
                arrayList = new ArrayList(ab1.W(arrayList2, 10));
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    arrayList.add(((File) it.next()).getName());
                }
            }
        } else {
            str4 = str2;
            arrayList = new ArrayList();
        }
        e eVar = e.a;
        File file3 = new File(parent, e.b(R, arrayList, ProjectDatabaseHelper$GenerateUniqueType.FILE_NAME).concat(".kmproject"));
        if (str4 == null) {
            str5 = file3.getName();
            str5.getClass();
            z = false;
            int J = hr8.J(str5, '.', 0, 6);
            if (J != -1) {
                str5 = str5.substring(0, J);
            }
        } else {
            z = false;
            str5 = str4;
        }
        String name4 = file.getName();
        name4.getClass();
        if (!pr8.k(name4, ".kmproject", z)) {
            String name5 = file.getName();
            name5.getClass();
            if (!pr8.k(name5, ".nexvideoproject", z)) {
                str6 = "Unknown project file type";
                if (!TextUtils.isEmpty(str6)) {
                    xv7.resumeWith(Result.m13constructorimpl(obj));
                } else {
                    i12 i12 = v92.a;
                    r(file, new g57(f2, nexVideoClipItem$CropMode2, num3, num4, xv7, str5, file3), e02.b, 8);
                }
                Object b2 = xv7.b();
                CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
                return b2;
            }
        }
        File parentFile2 = file3.getParentFile();
        if (parentFile2 == null || !parentFile2.isDirectory()) {
            str6 = "Unknown project location";
            if (!TextUtils.isEmpty(str6)) {
            }
            Object b22 = xv7.b();
            CoroutineSingletons coroutineSingletons2 = CoroutineSingletons.COROUTINE_SUSPENDED;
            return b22;
        }
        if (file3.exists() || str5.length() == 0) {
            str6 = "Cannot create new project";
        }
        if (!TextUtils.isEmpty(str6)) {
        }
        Object b222 = xv7.b();
        CoroutineSingletons coroutineSingletons22 = CoroutineSingletons.COROUTINE_SUSPENDED;
        return b222;
    }

    public static te7 n(h hVar, float f2, NexVideoClipItem$CropMode nexVideoClipItem$CropMode, Integer num, Integer num2) {
        int i;
        int i2;
        hVar.getClass();
        int aspectWidth = (int) hVar.aspectWidth();
        int aspectHeight = (int) hVar.aspectHeight();
        if (f2 > 1.0f) {
            i2 = cv9.M(720.0f * f2);
            i = 720;
        } else if (f2 < 1.0f) {
            i = cv9.M(720.0f / f2);
            i2 = 720;
        } else {
            i2 = 720;
            i = 720;
        }
        String str = null;
        if (aspectWidth <= 0 || aspectHeight <= 0 || i2 <= 0 || i <= 0) {
            return null;
        }
        float f3 = ((float) i2) / ((float) aspectWidth);
        float f4 = ((float) i) / ((float) aspectHeight);
        if (nexVideoClipItem$CropMode != null) {
            str = nexVideoClipItem$CropMode.getValue();
        }
        return new te7(i2, i, f3, f4, str, num, num2);
    }

    public static String o() {
        String format = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
        format.getClass();
        return format;
    }

    public static boolean p(h hVar) {
        NexTimeline nexTimeline;
        T t;
        T t2;
        lj9 lj9;
        TemplarReplaceableTag m0;
        lj9 lj92;
        TemplarReplaceableTag m02;
        if (hVar == null || (nexTimeline = hVar.a) == null) {
            return false;
        }
        int i = hVar.b().totalPlayTime;
        List<p46> primaryItems = nexTimeline.getPrimaryItems();
        primaryItems.getClass();
        Iterator<T> it = primaryItems.iterator();
        while (true) {
            t = null;
            if (!it.hasNext()) {
                t2 = null;
                break;
            }
            t2 = it.next();
            p46 p46 = (p46) t2;
            if (p46 instanceof lj9) {
                lj92 = (lj9) p46;
            } else {
                lj92 = null;
            }
            if (lj92 != null && (m02 = lj92.m0()) != null && m02 != TemplarReplaceableTag.DISABLE) {
                break;
            }
        }
        if (t2 != null) {
            return true;
        }
        List<r46> secondaryItems = nexTimeline.getSecondaryItems();
        secondaryItems.getClass();
        Iterator<T> it2 = secondaryItems.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            T next = it2.next();
            r46 r46 = (r46) next;
            if (r46 instanceof lj9) {
                lj9 = (lj9) r46;
            } else {
                lj9 = null;
            }
            if (lj9 != null && (m0 = lj9.m0()) != null && m0 != TemplarReplaceableTag.DISABLE && r46.T1() < i) {
                t = next;
                break;
            }
        }
        if (t != null) {
            return true;
        }
        return false;
    }

    public static void r(File file, qc6 qc6, mn1 mn1, int i) {
        boolean z;
        if ((i & 4) != 0) {
            i12 i12 = v92.a;
            mn1 = y85.a;
        }
        mn1 mn12 = mn1;
        if ((i & 8) != 0) {
            z = true;
        } else {
            z = false;
        }
        a.q(file, qc6, mn12, z, false);
    }

    /* JADX WARNING: type inference failed for: r7v1, types: [g67, java.lang.Object] */
    public static NexProjectHeader s(File file) {
        Throwable th;
        int i;
        if (file != null) {
            String name = file.getName();
            if (name == null) {
                name = "";
            }
            int J = hr8.J(name, '.', 0, 6);
            if (J > 0) {
                name = name.substring(0, J);
            }
            int length = name.length();
            StringBuilder sb = new StringBuilder(length);
            ByteBuffer allocate = ByteBuffer.allocate(CrashConfig.MAX_STACK_FRAMES_UPPER_BOUND);
            allocate.clear();
            int i2 = 0;
            boolean z = false;
            while (i2 < length) {
                char charAt = name.charAt(i2);
                if (charAt == '$') {
                    z = !z;
                } else if (!z || (i = i2 + 1) >= length || !t94.p(charAt) || !t94.p(name.charAt(i))) {
                    if (allocate.position() > 0) {
                        try {
                            byte[] array = allocate.array();
                            array.getClass();
                            int position = allocate.position();
                            Charset forName = Charset.forName(Keyczar.DEFAULT_ENCODING);
                            forName.getClass();
                            sb.append(new String(array, 0, position, forName));
                        } catch (UnsupportedEncodingException unused) {
                            byte[] array2 = allocate.array();
                            array2.getClass();
                            sb.append(new String(array2, 0, allocate.position(), e41.a));
                        }
                        allocate.clear();
                    }
                    sb.append(charAt);
                } else {
                    allocate.put((byte) (t94.o(name.charAt(i)) + (t94.o(charAt) * 16)));
                    i2 = i;
                }
                i2++;
            }
            if (allocate.position() > 0) {
                try {
                    byte[] array3 = allocate.array();
                    array3.getClass();
                    int position2 = allocate.position();
                    Charset forName2 = Charset.forName(Keyczar.DEFAULT_ENCODING);
                    forName2.getClass();
                    sb.append(new String(array3, 0, position2, forName2));
                } catch (UnsupportedEncodingException unused2) {
                    byte[] array4 = allocate.array();
                    array4.getClass();
                    sb.append(new String(array4, 0, allocate.position(), e41.a));
                }
                allocate.clear();
            }
            String sb2 = sb.toString();
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                f67 e2 = g67.e(new Object(), file, fileInputStream, ProjectLoader$LoadMode.HeaderOnly, sb2, false, 48);
                if (e2 instanceof e67) {
                    NexProjectHeader b2 = ((h) ((e67) e2).a).b();
                    fileInputStream.close();
                    return b2;
                } else if (e2 instanceof d67) {
                    throw new IOException(((d67) e2).a);
                } else {
                    throw new NoWhenBranchMatchedException();
                }
            } catch (Throwable th2) {
                ox1.M(fileInputStream, th);
                throw th2;
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    public static void u(Context context, float f2, List list, com.kinemaster.app.screen.home.create.e eVar) {
        context.getClass();
        list.getClass();
        NexEditor j = wn4.j();
        if (j == null) {
            eVar.invoke((Object) null);
        } else if (list.isEmpty()) {
            eVar.invoke((Object) null);
        } else {
            ky4 l = t94.l(mw6.c);
            i12 i12 = v92.a;
            com.kinemaster.app.screen.home.create.e eVar2 = eVar;
            xj3.E(l, e02.b, (CoroutineStart) null, new ProjectHelper$makeProjectWithDroppedUris$1(eVar2, f2, j, context, list, l, (lm1<? super ProjectHelper$makeProjectWithDroppedUris$1>) null), 2);
        }
    }

    public static void v(Context context, float f2, ArrayList arrayList, ArrayList arrayList2, zt3 zt3, r64 r64) {
        r64 r642 = r64;
        if (!arrayList.isEmpty() || !arrayList2.isEmpty()) {
            NexEditor j = wn4.j();
            if (j == null) {
                r642.invoke((Object) null);
                return;
            }
            ky4 l = t94.l(mw6.c);
            i12 i12 = v92.a;
            zt3 zt32 = zt3;
            xj3.E(l, e02.b, (CoroutineStart) null, new ProjectHelper$makeProjectWithSharedMediaStoreItems$1(r642, f2, j, context, zt32, l, arrayList2, arrayList, (lm1<? super ProjectHelper$makeProjectWithSharedMediaStoreItems$1>) null), 2);
            return;
        }
        r642.invoke((Object) null);
    }

    public static String w(Context context, Uri uri) {
        Throwable th;
        String str;
        try {
            Cursor query = context.getContentResolver().query(uri, new String[]{"_display_name"}, (String) null, (String[]) null, (String) null);
            if (query != null) {
                try {
                    if (query.moveToFirst()) {
                        str = query.getString(0);
                    } else {
                        str = null;
                    }
                    query.close();
                    return str;
                } catch (Throwable th2) {
                    ox1.M(query, th);
                    throw th2;
                }
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static void x(ProjectJob projectJob) {
        ConcurrentHashMap concurrentHashMap = e;
        HashSet hashSet = (HashSet) concurrentHashMap.get(projectJob);
        if (hashSet != null) {
            ArrayList arrayList = new ArrayList();
            for (Object next : hashSet) {
                if (((od4) next).isActive()) {
                    arrayList.add(next);
                }
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                od4 od4 = (od4) it.next();
                for (od4 cancel : od4.getChildren()) {
                    cancel.cancel((CancellationException) null);
                }
                od4.cancel((CancellationException) null);
            }
        }
        concurrentHashMap.remove(projectJob);
    }

    public static Object y(String str, File file, SuspendLambda suspendLambda) {
        i12 i12 = v92.a;
        return xj3.N(e02.b, new ProjectHelper$renameProjectTo$2(file, str, (lm1<? super ProjectHelper$renameProjectTo$2>) null), suspendLambda);
    }

    public static Object z(h hVar, File file, lm1 lm1) {
        i12 i12 = v92.a;
        Object N = xj3.N(e02.b, new ProjectHelper$runCopyClip$2(file, hVar, false, (lm1<? super ProjectHelper$runCopyClip$2>) null), lm1);
        if (N == CoroutineSingletons.COROUTINE_SUSPENDED) {
            return N;
        }
        return gy9.a;
    }

    public final void A(h hVar, File file, rc6 rc6) {
        hVar.getClass();
        yn8 yn8 = h;
        if (yn8 != null && yn8.isActive()) {
            x(ProjectJob.SAVE);
        }
        h = xj3.E(this, (jn1) null, (CoroutineStart) null, new ProjectHelper$saveProject$2(hVar, file, rc6, (lm1<? super ProjectHelper$saveProject$2>) null), 3);
        e(ProjectJob.SAVE, h);
    }

    public final void B(h hVar, File file, v29 v29, v29 v292, j4a j4a) {
        yn8 yn8 = h;
        if (yn8 != null && yn8.isActive()) {
            x(ProjectJob.SAVE);
        }
        h = xj3.E(this, (jn1) null, (CoroutineStart) null, new ProjectHelper$saveProject$4(hVar, file, v29, v292, j4a, (lm1<? super ProjectHelper$saveProject$4>) null), 3);
        e(ProjectJob.SAVE, h);
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x031e A[LOOP:6: B:157:0x0318->B:159:0x031e, LOOP_END] */
    public final void h(File file, h hVar, ye3 ye3) {
        List c1;
        h hVar2;
        jf7 jf7;
        yj5 yj5;
        int i;
        jf7 jf72;
        yj5 yj52;
        int i2;
        jf7 Y;
        yj5 yj53;
        int i3;
        yj5 yj54;
        int i4;
        int i5;
        jf7 jf73;
        yj5 yj55;
        int i6;
        jf7 jf74;
        yj5 yj56;
        int i7;
        yj5 yj57;
        int i8;
        int i9;
        file.getClass();
        hVar.getClass();
        File parentFile = file.getParentFile();
        if (parentFile == null || parentFile.isDirectory()) {
            File parentFile2 = file.getParentFile();
            parentFile2.getClass();
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = com.kinemaster.app.util.file.a.b;
            synchronized (hashMap2) {
                try {
                    Collection values = hashMap2.values();
                    values.getClass();
                    c1 = ya1.c1(values);
                } catch (Throwable th) {
                    throw th;
                }
            }
            try {
                if (D(parentFile2, hVar)) {
                    hVar2 = hVar;
                    z77 e2 = new e87().e(hVar2, file, (ye3) null, (ye3) null, false);
                    if (e2 instanceof x77) {
                        Log.e("ProjectHelper", "removeFilesInProject - save failed(" + ((x77) e2).a + "), skip content cleanup");
                        ye3.invoke(Boolean.TRUE);
                        c0.a.evictAll();
                        Set<ProjectJob> keySet = e.keySet();
                        keySet.getClass();
                        for (ProjectJob projectJob : keySet) {
                            projectJob.getClass();
                            x(projectJob);
                        }
                        kotlinx.coroutines.d.c(m());
                        return;
                    }
                } else {
                    hVar2 = hVar;
                }
                File[] listFiles = parentFile2.listFiles();
                if (listFiles != null) {
                    ArrayList arrayList = new ArrayList();
                    for (File file2 : listFiles) {
                        String name = file2.getName();
                        name.getClass();
                        if (pr8.k(name, ".kmproject", false)) {
                            arrayList.add(file2);
                        }
                    }
                    if (arrayList.size() >= 2) {
                        ye3.invoke(Boolean.TRUE);
                    } else {
                        List<p46> primaryItems = hVar2.a.getPrimaryItems();
                        ArrayList y = qq2.y(primaryItems);
                        for (T next : primaryItems) {
                            if (((p46) next) instanceof g) {
                                y.add(next);
                            }
                        }
                        Iterator it = y.iterator();
                        while (it.hasNext()) {
                            p46 p46 = (p46) it.next();
                            yj5 a2 = p46.a2();
                            if (a2 != null && a2.v("path")) {
                                Integer num = (Integer) hashMap.get(a2.L((we3) null));
                                if (num != null) {
                                    i9 = num.intValue();
                                } else {
                                    i9 = 1;
                                }
                                hashMap.put(a2.L((we3) null), Integer.valueOf(i9 + 1));
                            }
                            boolean z = p46 instanceof g;
                            if (z) {
                                g gVar = (g) p46;
                                if (gVar.u() && (yj57 = gVar.b1) != null) {
                                    Integer num2 = (Integer) hashMap.get(yj57.L((we3) null));
                                    if (num2 != null) {
                                        i8 = num2.intValue();
                                    } else {
                                        i8 = 1;
                                    }
                                    hashMap.put(yj57.L((we3) null), Integer.valueOf(i8 + 1));
                                }
                            }
                            if (!(!z || (jf74 = ((g) p46).c1) == null || (yj56 = jf74.b) == null)) {
                                Integer num3 = (Integer) hashMap.get(yj56.L((we3) null));
                                if (num3 != null) {
                                    i7 = num3.intValue();
                                } else {
                                    i7 = 1;
                                }
                                hashMap.put(yj56.L((we3) null), Integer.valueOf(i7 + 1));
                            }
                            if (!(!z || (jf73 = ((g) p46).d1) == null || (yj55 = jf73.b) == null)) {
                                Integer num4 = (Integer) hashMap.get(yj55.L((we3) null));
                                if (num4 != null) {
                                    i6 = num4.intValue();
                                } else {
                                    i6 = 1;
                                }
                                hashMap.put(yj55.L((we3) null), Integer.valueOf(i6 + 1));
                            }
                        }
                        List<r46> secondaryItems = hVar2.a.getSecondaryItems();
                        ArrayList y2 = qq2.y(secondaryItems);
                        for (T next2 : secondaryItems) {
                            r46 r46 = (r46) next2;
                            if ((r46 instanceof kj5) || (r46 instanceof com.nextreaming.nexeditorui.d)) {
                                y2.add(next2);
                            }
                        }
                        Iterator it2 = y2.iterator();
                        while (it2.hasNext()) {
                            r46 r462 = (r46) it2.next();
                            yj5 a22 = r462.a2();
                            if (a22 != null && a22.v("path")) {
                                Integer num5 = (Integer) hashMap.get(a22.L((we3) null));
                                if (num5 != null) {
                                    i5 = num5.intValue();
                                } else {
                                    i5 = 1;
                                }
                                hashMap.put(a22.L((we3) null), Integer.valueOf(i5 + 1));
                            }
                            if (r462 instanceof d14) {
                                d14 d14 = (d14) r462;
                                if (d14.u() && (yj54 = d14.N0) != null) {
                                    Integer num6 = (Integer) hashMap.get(yj54.L((we3) null));
                                    if (num6 != null) {
                                        i4 = num6.intValue();
                                    } else {
                                        i4 = 1;
                                    }
                                    hashMap.put(yj54.L((we3) null), Integer.valueOf(i4 + 1));
                                }
                            }
                            if (r462 instanceof kj5) {
                                kj5 kj5 = (kj5) r462;
                                if (!(kj5.Y() == null || (Y = kj5.Y()) == null || (yj53 = Y.b) == null)) {
                                    Integer num7 = (Integer) hashMap.get(yj53.L((we3) null));
                                    if (num7 != null) {
                                        i3 = num7.intValue();
                                    } else {
                                        i3 = 1;
                                    }
                                    hashMap.put(yj53.L((we3) null), Integer.valueOf(i3 + 1));
                                }
                            }
                            if (!(!(r462 instanceof o5a) || (jf72 = ((o5a) r462).M1) == null || (yj52 = jf72.b) == null)) {
                                Integer num8 = (Integer) hashMap.get(yj52.L((we3) null));
                                if (num8 != null) {
                                    i2 = num8.intValue();
                                } else {
                                    i2 = 1;
                                }
                                hashMap.put(yj52.L((we3) null), Integer.valueOf(i2 + 1));
                            }
                            if (!(!(r462 instanceof com.nextreaming.nexeditorui.d) || (jf7 = ((com.nextreaming.nexeditorui.d) r462).O) == null || (yj5 = jf7.b) == null)) {
                                Integer num9 = (Integer) hashMap.get(yj5.L((we3) null));
                                if (num9 != null) {
                                    i = num9.intValue();
                                } else {
                                    i = 1;
                                }
                                hashMap.put(yj5.L((we3) null), Integer.valueOf(i + 1));
                            }
                        }
                        FileWalkDirection fileWalkDirection = FileWalkDirection.TOP_DOWN;
                        fileWalkDirection.getClass();
                        fz2 fz2 = new fz2(new gz2(new la2(parentFile2, fileWalkDirection), true, new ov(c1, 15)));
                        while (fz2.hasNext()) {
                            File file3 = (File) fz2.next();
                            if (!hashMap.containsKey(file3.getAbsolutePath())) {
                                file3.getAbsolutePath();
                                file3.delete();
                            }
                        }
                        ye3.invoke(Boolean.TRUE);
                    }
                } else {
                    ye3.invoke(Boolean.FALSE);
                }
            } catch (Exception e3) {
                a.g(new RuntimeException("[ProjectHelper] removeFilesInProject failure", a.k(e3)));
                ye3.invoke(Boolean.TRUE);
            }
            c0.a.evictAll();
            Set<ProjectJob> keySet2 = e.keySet();
            keySet2.getClass();
            while (r12.hasNext()) {
            }
            kotlinx.coroutines.d.c(m());
            return;
        }
        ye3.invoke(Boolean.FALSE);
    }

    public final jn1 m() {
        i12 i12 = v92.a;
        return e02.b.plus(d);
    }

    public final void q(File file, qc6 qc6, mn1 mn1, boolean z, boolean z2) {
        ProjectLoader$LoadMode projectLoader$LoadMode;
        file.getClass();
        mn1.getClass();
        String str = e87.b;
        e87.b = null;
        e87.c = 0;
        j08 j08 = j08.a;
        j08.c = Long.MAX_VALUE;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            if (z2) {
                projectLoader$LoadMode = ProjectLoader$LoadMode.DependenciesOnly;
            } else {
                projectLoader$LoadMode = ProjectLoader$LoadMode.Default;
            }
            ProjectLoader$LoadMode projectLoader$LoadMode2 = projectLoader$LoadMode;
            yn8 yn8 = f;
            if (yn8 != null && yn8.isActive()) {
                x(ProjectJob.LOAD);
            }
            boolean z3 = z;
            f = xj3.E(this, (jn1) null, (CoroutineStart) null, new ProjectHelper$loadProject$2(file, fileInputStream, projectLoader$LoadMode2, z3, mn1, qc6, (lm1<? super ProjectHelper$loadProject$2>) null), 3);
            e(ProjectJob.LOAD, f);
        } catch (FileNotFoundException e2) {
            FileNotFoundException fileNotFoundException = e2;
            fileNotFoundException.printStackTrace();
            qc6.e(fileNotFoundException);
        }
    }

    public final void t(h hVar, ByteArrayInputStream byteArrayInputStream, g4a g4a) {
        yn8 yn8 = g;
        if (yn8 != null && yn8.isActive()) {
            x(ProjectJob.LOAD);
        }
        g = xj3.E(this, (jn1) null, (CoroutineStart) null, new ProjectHelper$loadTimeline$2(hVar, byteArrayInputStream, g4a, (lm1<? super ProjectHelper$loadTimeline$2>) null), 3);
        e(ProjectJob.LOAD, g);
    }
}
