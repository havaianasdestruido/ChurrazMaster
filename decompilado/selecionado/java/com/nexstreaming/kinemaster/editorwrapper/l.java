package com.nexstreaming.kinemaster.editorwrapper;

import android.content.Context;
import android.graphics.Bitmap;
import com.nexstreaming.kinemaster.project.util.d;
import java.io.File;
import java.io.InputStream;
import kotlin.coroutines.intrinsics.CoroutineSingletons;
import kotlin.jvm.internal.Ref$ObjectRef;
import kotlinx.coroutines.CoroutineStart;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public final class l implements un1 {
    public final InputStream a;
    public final String b;
    public final String c;
    public final long d;
    public final String e;
    public final jn1 f;
    public final boolean g;
    public final Bitmap h;
    public final v57 i;
    public final String j;
    public String k;
    public yn8 l;

    public l(InputStream inputStream, String str, String str2, long j2, String str3, jn1 jn1, boolean z, Bitmap bitmap, v57 v57) {
        inputStream.getClass();
        str.getClass();
        str2.getClass();
        jn1.getClass();
        this.a = inputStream;
        this.b = str;
        this.c = str2;
        this.d = j2;
        this.e = str3;
        this.f = jn1;
        this.g = z;
        this.h = bitmap;
        this.i = v57;
        this.j = !pr8.k(str, ".kine", false) ? str.concat(".kine") : str;
    }

    public static final Object a(l lVar, Context context, lm1 lm1) {
        lVar.getClass();
        i12 i12 = v92.a;
        return xj3.N(e02.b, new ProjectImporter$checkFreeSpaceAtInternalStorage$2(context, (lm1<? super ProjectImporter$checkFreeSpaceAtInternalStorage$2>) null), lm1);
    }

    public static final Object b(l lVar, File file, String str, Bitmap bitmap, lm1 lm1) {
        lVar.getClass();
        by0 by0 = new by0(1, xj3.A(lm1));
        by0.u();
        d dVar = d.a;
        File file2 = file;
        d.r(file2, new z57(by0, lVar, bitmap, str, file2), (mn1) null, 28);
        Object t = by0.t();
        CoroutineSingletons coroutineSingletons = CoroutineSingletons.COROUTINE_SUSPENDED;
        return t;
    }

    public static final Object c(l lVar, long j2, lm1 lm1) {
        lVar.getClass();
        i12 i12 = v92.a;
        Object N = xj3.N(y85.a, new ProjectImporter$onProgress$2(j2, 100, lVar, (lm1<? super ProjectImporter$onProgress$2>) null), lm1);
        if (N == CoroutineSingletons.COROUTINE_SUSPENDED) {
            return N;
        }
        return gy9.a;
    }

    public static final Object d(l lVar, lm1 lm1) {
        lVar.getClass();
        i12 i12 = v92.a;
        return xj3.N(e02.b, new ProjectImporter$unzipProject$2(lVar, (lm1<? super ProjectImporter$unzipProject$2>) null), lm1);
    }

    public final void e(Context context) {
        context.getClass();
        this.l = xj3.E(this, (jn1) null, (CoroutineStart) null, new ProjectImporter$importStart$1(this, context, new Ref$ObjectRef(), (lm1<? super ProjectImporter$importStart$1>) null), 3);
    }

    public final jn1 m() {
        i12 i12 = v92.a;
        return e02.b.plus(this.f);
    }
}
