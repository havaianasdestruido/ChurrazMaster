package com.kinemaster.module.nexeditormodule.nexvideoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Log;
import android.util.Size;
import com.kinemaster.module.nexeditormodule.config.EditorGlobal$Edition;
import com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditor;
import com.kinemaster.module.nexeditormodule.nexvideoeditor.NexImageLoader;
import com.kinemaster.module.nextask.task.ResultTask;
import com.tiktok.appevents.contents.TTContentsEventConstants;
import dalvik.system.BaseDexClassLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public class NexEditorUtils {
    private static final String LOG_TAG = "NexEditorUtils";

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum Abi {
        NONE,
        ARM_64,
        ARM,
        X86_64,
        X86
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class CropMaskParam {
        public int color = 0;
        public int stroke_color = 0;
        public int stroke_width = 0;
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum ImageTranscodingType {
        NONE,
        SEGMENTATION,
        SUPER_RESOLUTION,
        AI_STYLE
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class InitImageAiStyleParam extends InitImageTranscodingParam {
        public int m_iHeight;
        public int m_iWidth;
        public Size m_outResolution;
        public String m_sCacheDirPath;

        public InitImageAiStyleParam(String str, String str2, int i, int i2) {
            this.m_eType = ImageTranscodingType.AI_STYLE;
            this.m_sTflitePath = str;
            this.m_sCacheDirPath = str2;
            this.m_iWidth = i;
            this.m_iHeight = i2;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class InitImageSegmentationParam extends InitImageTranscodingParam {
        public String m_sCacheDirPath;

        public InitImageSegmentationParam(String str, String str2) {
            this.m_eType = ImageTranscodingType.SEGMENTATION;
            this.m_sTflitePath = str;
            this.m_sCacheDirPath = str2;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class InitImageSuperResolutionParam extends InitImageTranscodingParam {
        public int m_iHeight;
        public int m_iScale;
        public int m_iWidth;
        public Size m_outResolution;

        public InitImageSuperResolutionParam(String str, int i, int i2, int i3) {
            this.m_eType = ImageTranscodingType.SUPER_RESOLUTION;
            this.m_sTflitePath = str;
            this.m_iWidth = i;
            this.m_iHeight = i2;
            this.m_iScale = i3;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class InitImageTranscodingParam {
        public boolean m_bSuccess = false;
        public ImageTranscodingType m_eType = ImageTranscodingType.NONE;
        public String m_sTflitePath;
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class MaskBuffer {
        public ByteBuffer byteBuffer;
        public int height;
        public int width;
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ProcImageAiStyleParam extends ProcImageTranscodingParam {
        public ProcImageAiStyleParam(byte[] bArr, byte[] bArr2) {
            this.m_eType = ImageTranscodingType.AI_STYLE;
            this.m_srcBitmap = bArr;
            this.m_dstBitmap = bArr2;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ProcImageSegmentationParam extends ProcImageTranscodingParam {
        public int m_iHeight;
        public int m_iOutHeight;
        public int m_iOutWidth;
        public int m_iOutputType;
        public int m_iWidth;

        public ProcImageSegmentationParam(byte[] bArr, int i, int i2, int i3, int i4, int i5, byte[] bArr2) {
            this.m_eType = ImageTranscodingType.SEGMENTATION;
            this.m_srcBitmap = bArr;
            this.m_iWidth = i;
            this.m_iHeight = i2;
            this.m_iOutputType = i3;
            this.m_iOutWidth = i4;
            this.m_iOutHeight = i5;
            this.m_dstBitmap = bArr2;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ProcImageSuperResolutionParam extends ProcImageTranscodingParam {
        public ProcImageSuperResolutionParam(byte[] bArr, byte[] bArr2) {
            this.m_eType = ImageTranscodingType.SUPER_RESOLUTION;
            this.m_srcBitmap = bArr;
            this.m_dstBitmap = bArr2;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ProcImageTranscodingParam {
        public byte[] m_dstBitmap;
        public ImageTranscodingType m_eType;
        public byte[] m_srcBitmap;
    }

    static {
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            System.loadLibrary("nexeditorsdk");
        } catch (UnsatisfiedLinkError e) {
            Log.e(LOG_TAG, "[nexUtils.java] nexeditor load failed : " + e);
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
        }
    }

    public static native int NexCheckMP3VBR(String str, int i);

    public static native int NexGetClipInfo(String str, NexClipInfo nexClipInfo, int i, int i2);

    public static int NexGetClipInfoJava(Context context, String str, NexClipInfo nexClipInfo, int i, int i2) {
        try {
            if (!ContentPathResolver.isKindOfContentUri(str)) {
                return NexGetClipInfo(str, nexClipInfo, i, i2);
            }
            ContentPathResolver contentPath = ContentPathResolver.getContentPath(context, Uri.parse(str), false);
            ParcelFileDescriptor parcelFileDescriptor = contentPath.getParcelFileDescriptor();
            if (parcelFileDescriptor != null) {
                str = "sharedsinglefd://" + parcelFileDescriptor.detachFd();
            }
            int NexGetClipInfo = NexGetClipInfo(str, nexClipInfo, i, i2);
            contentPath.close();
            return NexGetClipInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void cancelCompressionKMProject(ResultTask<NexEditor> resultTask) {
        NexEditor result = resultTask.getResult();
        if (result != null) {
            result.stopCompressionMediaContents();
        }
    }

    public static int checkMP3VBRJava(Context context, String str, int i) {
        try {
            if (!ContentPathResolver.isKindOfContentUri(str)) {
                return NexCheckMP3VBR(str, i);
            }
            ContentPathResolver contentPath = ContentPathResolver.getContentPath(context, Uri.parse(str), false);
            ParcelFileDescriptor parcelFileDescriptor = contentPath.getParcelFileDescriptor();
            if (parcelFileDescriptor != null) {
                str = "sharedsinglefd://" + parcelFileDescriptor.detachFd();
            }
            int NexCheckMP3VBR = NexCheckMP3VBR(str, i);
            contentPath.close();
            return NexCheckMP3VBR;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static ResultTask<NexEditor> compressKMProject(Context context, String str, String str2, long j) {
        boolean z;
        ResultTask<NexEditor> resultTask = new ResultTask<>();
        NexEditor createEditorForInternal = createEditorForInternal(context);
        boolean z2 = true;
        resultTask.setCancellable(true);
        createEditorForInternal.setEventHandler((NexEditorListener) new s0(resultTask));
        File file = new File(str, TTContentsEventConstants.Params.EVENT_PROPERTY_CONTENTS);
        if (!file.exists()) {
            z2 = file.mkdirs();
        }
        if (z2) {
            z = createEditorForInternal.compressMediaContentsInKMProject(str, str2, j);
        } else {
            z = false;
        }
        if (!z) {
            createEditorForInternal.closeProject();
            createEditorForInternal.destroy();
            resultTask.setResult(null);
            resultTask.sendFailure(NexEditor.ErrorCode.UNKNOWN);
            return resultTask;
        }
        resultTask.setResult(createEditorForInternal);
        return resultTask;
    }

    private static NexEditor createEditorForInternal(Context context) {
        wu1 wu1 = ho4.c().b;
        m74 m74 = ho4.c().c;
        NexEditor nexEditor = null;
        try {
            EditorGlobal$Edition editorGlobal$Edition = di2.a;
            Context context2 = context;
            NexEditor nexEditor2 = new NexEditor(context2, wu1, m74, (NexThemeView) null, Build.MODEL, di2.d(), new NexImageLoader.OverlayPathResolver(), new int[]{2, 16, 1, 1, 3, wu1.i(), 0});
            try {
                nexEditor2.createProject();
                return nexEditor2;
            } catch (UnsatisfiedLinkError unused) {
                nexEditor = nexEditor2;
                Log.e(LOG_TAG, "UnsatisfiedLinkError!!!");
                return nexEditor;
            } catch (NexEditor.EditorInitException unused2) {
                nexEditor = nexEditor2;
                Log.e(LOG_TAG, "EditorInitException!!!");
                return nexEditor;
            }
        } catch (UnsatisfiedLinkError unused3) {
            Log.e(LOG_TAG, "UnsatisfiedLinkError!!!");
            return nexEditor;
        } catch (NexEditor.EditorInitException unused4) {
            Log.e(LOG_TAG, "EditorInitException!!!");
            return nexEditor;
        }
    }

    public static native byte[] decryptKMAsset(byte[] bArr);

    private static native void deinitImageAiStyle();

    private static native void deinitImageSegmentation();

    private static native void deinitImageSuperResolution();

    public static void deinitImageTranscoding(ImageTranscodingType imageTranscodingType) {
        int i = t0.a[imageTranscodingType.ordinal()];
        if (i == 1) {
            deinitImageSegmentation();
        } else if (i == 2) {
            deinitImageSuperResolution();
        } else if (i == 3) {
            deinitImageAiStyle();
        }
    }

    public static void finalizeCompressionKMProject(ResultTask<NexEditor> resultTask) {
        NexEditor result = resultTask.getResult();
        if (result != null) {
            if (resultTask.isRunning()) {
                result.stopCompressionMediaContents();
            }
            result.closeProject();
            result.destroy();
        }
        resultTask.setResult(null);
    }

    public static MaskBuffer getCropMask(byte[] bArr, int i, float f) {
        Object[] cropMaskN = getCropMaskN(bArr, i, f);
        if (cropMaskN == null || cropMaskN.length != 3) {
            return null;
        }
        MaskBuffer maskBuffer = new MaskBuffer();
        maskBuffer.byteBuffer = ByteBuffer.wrap((byte[]) cropMaskN[0]);
        maskBuffer.width = ((Integer) cropMaskN[1]).intValue();
        maskBuffer.height = ((Integer) cropMaskN[2]).intValue();
        return maskBuffer;
    }

    private static native Object[] getCropMaskN(byte[] bArr, int i, float f);

    private static String getEngineLibPathWithContext(Context context) {
        if (context.getFilesDir() != null) {
            String findLibrary = ((BaseDexClassLoader) context.getClassLoader()).findLibrary("nexcralbody_mc_jb");
            if (findLibrary != null) {
                String str = File.separator;
                int lastIndexOf = findLibrary.lastIndexOf(str);
                return findLibrary.substring(0, lastIndexOf) + str;
            }
            String str2 = context.getApplicationInfo().nativeLibraryDir;
            String str3 = File.separator;
            if (!str2.endsWith(str3)) {
                str2 = qq2.r(str2, str3);
            }
            if (new File(str2, "libnexeditorsdk.so").exists()) {
                return str2;
            }
            return null;
        }
        c.o("No files directory - cannot play video - relates to Android issue: 8886!");
        return null;
    }

    private static String getEngineLibPathWithJavaLibraryPath(Context context) {
        String property = System.getProperty("java.library.path");
        if (property == null) {
            return null;
        }
        String[] split = property.split(":");
        for (int i = 0; i < split.length; i++) {
            if (!split[i].endsWith("/")) {
                split[i] = x5.o(new StringBuilder(), split[i], "/");
            }
            if (new File(split[i], "libnexeditorsdk.so").exists()) {
                return split[i];
            }
        }
        return null;
    }

    public static String getEngineNativeLibPath(Context context) {
        String engineLibPathWithContext = getEngineLibPathWithContext(context);
        if (engineLibPathWithContext != null && engineLibPathWithContext.length() != 0) {
            return engineLibPathWithContext;
        }
        String engineLibPathWithJavaLibraryPath = getEngineLibPathWithJavaLibraryPath(context);
        if (engineLibPathWithJavaLibraryPath != null && engineLibPathWithJavaLibraryPath.length() != 0) {
            return engineLibPathWithJavaLibraryPath;
        }
        String str = context.getApplicationInfo().nativeLibraryDir;
        String str2 = File.separator;
        if (!str.endsWith(str2)) {
            str = qq2.r(str, str2);
        }
        String str3 = "/system/lib64/";
        if (!str.contains("/arm64") && !str.contains("/x86_64")) {
            str3 = "/system/lib/";
        }
        new File(str3, "libnexeditorsdk.so").exists();
        return str3;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0055 A[SYNTHETIC, Splitter:B:28:0x0055] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x005b A[SYNTHETIC, Splitter:B:34:0x005b] */
    public static boolean getFP16CpuInfo() {
        FileInputStream fileInputStream = null;
        try {
            FileInputStream fileInputStream2 = new FileInputStream("/proc/cpuinfo");
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream2));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        int indexOf = readLine.indexOf(58);
                        if (indexOf > 0) {
                            String trim = readLine.substring(0, indexOf).trim();
                            String trim2 = readLine.substring(indexOf + 1).trim();
                            if (trim.equals("Features") && trim2.contains("fphp")) {
                                try {
                                    fileInputStream2.close();
                                } catch (IOException unused) {
                                }
                                return true;
                            }
                        }
                    } else {
                        try {
                            fileInputStream2.close();
                            return false;
                        } catch (IOException unused2) {
                        }
                    }
                }
            } catch (IOException unused3) {
                fileInputStream = fileInputStream2;
                if (fileInputStream != null) {
                }
                return false;
            } catch (Throwable th) {
                th = th;
                fileInputStream = fileInputStream2;
                if (fileInputStream != null) {
                }
                throw th;
            }
        } catch (IOException unused4) {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            return false;
        } catch (Throwable th2) {
            th = th2;
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException unused5) {
                }
            }
            throw th;
        }
    }

    public static int getKenBurnsRects(int i, int i2, NexRectangle[] nexRectangleArr, int i3, int i4, int i5, NexRectangle[] nexRectangleArr2) {
        int i6;
        if (nexRectangleArr != null) {
            i6 = nexRectangleArr.length;
        } else {
            i6 = 0;
        }
        return getKenBurnsRectsN(i, i2, i6, nexRectangleArr, i3, i4, i5, nexRectangleArr2);
    }

    private static native int getKenBurnsRectsN(int i, int i2, int i3, NexRectangle[] nexRectangleArr, int i4, int i5, int i6, NexRectangle[] nexRectangleArr2);

    private static native Size getOutputResInfoImageAiStyle(int i, int i2);

    public static Bitmap getThumbnailCropMask(byte[] bArr, int i, CropMaskParam[] cropMaskParamArr) {
        return getThumbnailCropMaskN(bArr, i, cropMaskParamArr[0], cropMaskParamArr[1]);
    }

    private static native Bitmap getThumbnailCropMaskN(byte[] bArr, int i, CropMaskParam cropMaskParam, CropMaskParam cropMaskParam2);

    public static Abi getWorkingEngineAbi(Context context) {
        Abi abi = Abi.NONE;
        String engineLibPathWithContext = getEngineLibPathWithContext(context);
        if ((engineLibPathWithContext == null || engineLibPathWithContext.length() == 0) && ((engineLibPathWithContext = getEngineLibPathWithJavaLibraryPath(context)) == null || engineLibPathWithContext.length() == 0)) {
            engineLibPathWithContext = context.getApplicationInfo().nativeLibraryDir;
        }
        if (engineLibPathWithContext == null || engineLibPathWithContext.length() == 0) {
            return abi;
        }
        if (engineLibPathWithContext.contains("/arm64")) {
            return Abi.ARM_64;
        }
        if (engineLibPathWithContext.contains("/arm")) {
            return Abi.ARM;
        }
        if (engineLibPathWithContext.contains("/x86_64")) {
            return Abi.X86_64;
        }
        if (engineLibPathWithContext.contains("/x86")) {
            return Abi.X86;
        }
        return abi;
    }

    private static native boolean initImageAiStyle(String str, String str2);

    private static native boolean initImageSegmentation(String str, String str2);

    private static native Size initImageSuperResolution(String str, int i, int i2, int i3);

    public static void initImageTranscoding(InitImageTranscodingParam initImageTranscodingParam) {
        if (initImageTranscodingParam != null) {
            initImageTranscodingParam.m_bSuccess = false;
        }
        int i = t0.a[initImageTranscodingParam.m_eType.ordinal()];
        if (i == 1) {
            InitImageSegmentationParam initImageSegmentationParam = (InitImageSegmentationParam) initImageTranscodingParam;
            initImageSegmentationParam.m_bSuccess = initImageSegmentation(initImageSegmentationParam.m_sTflitePath, initImageSegmentationParam.m_sCacheDirPath);
        } else if (i == 2) {
            InitImageSuperResolutionParam initImageSuperResolutionParam = (InitImageSuperResolutionParam) initImageTranscodingParam;
            Size initImageSuperResolution = initImageSuperResolution(initImageSuperResolutionParam.m_sTflitePath, initImageSuperResolutionParam.m_iWidth, initImageSuperResolutionParam.m_iHeight, initImageSuperResolutionParam.m_iScale);
            if (initImageSuperResolution != null) {
                initImageSuperResolutionParam.m_bSuccess = true;
                initImageSuperResolutionParam.m_outResolution = initImageSuperResolution;
            }
        } else if (i == 3) {
            InitImageAiStyleParam initImageAiStyleParam = (InitImageAiStyleParam) initImageTranscodingParam;
            boolean initImageAiStyle = initImageAiStyle(initImageAiStyleParam.m_sTflitePath, initImageAiStyleParam.m_sCacheDirPath);
            initImageAiStyleParam.m_bSuccess = initImageAiStyle;
            if (initImageAiStyle) {
                initImageAiStyleParam.m_outResolution = getOutputResInfoImageAiStyle(initImageAiStyleParam.m_iWidth, initImageAiStyleParam.m_iHeight);
            }
        }
    }

    private static native int procImageAiStyle(byte[] bArr, byte[] bArr2);

    private static native int procImageSegmentation(byte[] bArr, int i, int i2, int i3, int i4, int i5, byte[] bArr2);

    private static native int procImageSuperResolution(byte[] bArr, byte[] bArr2);

    public static int procImageTranscoding(ProcImageTranscodingParam procImageTranscodingParam) {
        int i = t0.a[procImageTranscodingParam.m_eType.ordinal()];
        if (i == 1) {
            ProcImageSegmentationParam procImageSegmentationParam = (ProcImageSegmentationParam) procImageTranscodingParam;
            return procImageSegmentation(procImageSegmentationParam.m_srcBitmap, procImageSegmentationParam.m_iWidth, procImageSegmentationParam.m_iHeight, procImageSegmentationParam.m_iOutputType, procImageSegmentationParam.m_iOutWidth, procImageSegmentationParam.m_iOutHeight, procImageSegmentationParam.m_dstBitmap);
        } else if (i == 2) {
            ProcImageSuperResolutionParam procImageSuperResolutionParam = (ProcImageSuperResolutionParam) procImageTranscodingParam;
            return procImageSuperResolution(procImageSuperResolutionParam.m_srcBitmap, procImageSuperResolutionParam.m_dstBitmap);
        } else if (i != 3) {
            return -1;
        } else {
            ProcImageAiStyleParam procImageAiStyleParam = (ProcImageAiStyleParam) procImageTranscodingParam;
            return procImageAiStyle(procImageAiStyleParam.m_srcBitmap, procImageAiStyleParam.m_dstBitmap);
        }
    }
}
