package com.kinemaster.module.nexeditormodule.nexvideoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.bykv.vk.openvk.preload.falconx.statistic.StatisticData;
import com.facebook.ads.AdError;
import com.fyber.inneractive.sdk.external.InneractiveMediationDefs;
import com.inmobi.media.core.config.models.AdConfig;
import com.inmobi.media.core.config.models.CrashConfig;
import com.kinemaster.module.nexeditormodule.codeccolorformat.ColorFormatChecker$ColorFormat;
import com.kinemaster.module.nexeditormodule.nexvideoeditor.NexImageLoader;
import com.kinemaster.module.nextask.task.ResultTask;
import com.kinemaster.module.nextask.task.Task;
import com.nexstreaming.kinemaster.usage.analytics.KMEvents;
import com.yalantis.ucrop.view.CropImageView;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

/* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
public class NexEditor {
    public static final int CAPTURE_CURRENT = 2147418113;
    public static final int CAPTURE_CURRENT_NOFX = 2147418114;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_EXPORT_AUDIO_BITRATE = 131072;
    private static final int DEFAULT_EXPORT_CAPTURE_DURATION = 0;
    private static final int DEFAULT_EXPORT_CODEC = 268501760;
    public static final int ENTITLEMENT_BUSY = 20;
    public static final int ENTITLEMENT_DECRYPT_FAILED = 6;
    public static final int ENTITLEMENT_DEVICE_BINDING_MISMATCH = 8;
    public static final int ENTITLEMENT_ENGINE_NOT_READY = 15;
    public static final int ENTITLEMENT_ENROLL_TIER_EXHAUSTED = 18;
    public static final int ENTITLEMENT_EXPIRED = 9;
    public static final int ENTITLEMENT_FAILED_KEY_NONE = 0;
    public static final int ENTITLEMENT_FAILED_KEY_SIGN = 1;
    public static final int ENTITLEMENT_FAILED_KEY_WRAP = 2;
    public static final int ENTITLEMENT_FALLBACK_NONE = 0;
    public static final int ENTITLEMENT_FALLBACK_SIGN = 1;
    public static final int ENTITLEMENT_FALLBACK_WRAP = 2;
    public static final int ENTITLEMENT_FALLBACK_WRAP_SOFTWARE = 4;
    public static final int ENTITLEMENT_INTERNAL = 12;
    public static final int ENTITLEMENT_INVALID_ARG = 2;
    public static final int ENTITLEMENT_KEYSTORE_ERROR = 10;
    public static final int ENTITLEMENT_KEYSTORE_REBOOT_REQUIRED = 21;
    public static final int ENTITLEMENT_KEYSTORE_SIGN_KEY = 16;
    public static final int ENTITLEMENT_KEYSTORE_TIMEOUT = 19;
    public static final int ENTITLEMENT_KEYSTORE_TRANSIENT = 14;
    public static final int ENTITLEMENT_KEYSTORE_UNRESPONSIVE = 22;
    public static final int ENTITLEMENT_KEYSTORE_WRAP_KEY = 17;
    private static final int ENTITLEMENT_MAX_CHALLENGE = 64;
    public static final int ENTITLEMENT_NOT_ENROLLED = 4;
    public static final int ENTITLEMENT_NOT_SUPPORTED = 1;
    public static final int ENTITLEMENT_NO_PENDING = 3;
    public static final int ENTITLEMENT_OK = 0;
    public static final int ENTITLEMENT_PERSIST_FAILED = 11;
    public static final int ENTITLEMENT_RETRY_BACKOFF = 2;
    public static final int ENTITLEMENT_RETRY_CONNECTIVITY = 3;
    public static final int ENTITLEMENT_RETRY_NEVER = 1;
    public static final int ENTITLEMENT_RETRY_REBOOT = 4;
    public static final int ENTITLEMENT_RETRY_UNKNOWN = -1;
    public static final int ENTITLEMENT_RKID_MISMATCH = 13;
    public static final int ENTITLEMENT_SIGNATURE_INVALID = 7;
    public static final int ENTITLEMENT_STUCK_ORIGIN_RENDER_BIT = 256;
    public static final int ENTITLEMENT_SUB_KIND_ONE_TIME = 1;
    public static final int ENTITLEMENT_SUB_KIND_SUBSCRIPTION = 0;
    public static final int ENTITLEMENT_UNWRAP_FAILED = 5;
    private static final int LOAD_THEME_PREVIEW = 1;
    private static final String LOG_TAG = "NexEditor.java";
    private static final int MAX_FAST_PREVIEW_COUNT = 2;
    private static final int MSG_RENDER_REQ_EXPIRED = 1;
    public static final String OPENGL_ES_VER_20 = "OpenGL_ES_2.0";
    public static final String OPENGL_ES_VER_30 = "OpenGL_ES_3.0";
    public static final String OPENGL_ES_VER_31 = "OpenGL_ES_3.1";
    public static final String OPENGL_ES_VER_31_EXT = "OpenGL_ES_3.1_Extension";
    public static final String OPENGL_ES_VER_32 = "OpenGL_ES_3.2";
    public static final int PROP_DEPTH_BUFFER_BITS = 2;
    public static final int PROP_ENGINE_LOG_LEVEL = 3;
    public static final int PROP_GL_VERSION = 4;
    public static final String PROP_MISSING_CLIP_IMAGE_URL = "MissingClipImageUrl";
    public static final int PROP_MULTISAMPLE = 1;
    private static boolean isCheckLibOpenCL = false;
    public static final int kAspectRatio_Mode_16v9 = 1;
    public static final int kAspectRatio_Mode_1v1 = 2;
    public static final int kAspectRatio_Mode_1v2 = 5;
    public static final int kAspectRatio_Mode_2v1 = 4;
    public static final int kAspectRatio_Mode_3v4 = 7;
    public static final int kAspectRatio_Mode_4v3 = 6;
    public static final int kAspectRatio_Mode_4v5 = 8;
    public static final int kAspectRatio_Mode_9v16 = 3;
    public static final int kLayerMode_ScreenDimension = 0;
    public static final int kLayerMode_Stretching = 1;
    public static final int kScreenMode_horizonDual = 1;
    public static final int kScreenMode_normal = 0;
    private static int layerHeight = 720;
    private static int layerMode = 0;
    private static int layerWidth = 1280;
    private static final OnCompletionListener m_emptyCompletionListener = new OnCompletionListener();
    private static boolean sLinkError = false;
    private static boolean sPrepareSurfaceSetToNull = false;
    private OnGetThumbDoneListener OnGetThumbDoneListener;
    private OnHighLightDoneListener OnHighLightDoneListener;
    /* access modifiers changed from: private */
    public ResultTask<Rect> captureChangeTask;
    /* access modifiers changed from: private */
    public SurfaceHolder.Callback captureSurfaceCallback;
    private Task fastOptionPreviewTask;
    private int mAspectMode;
    /* access modifiers changed from: private */
    public boolean mCaptureCompleting;
    /* access modifiers changed from: private */
    public boolean mCaptureFrameInProgress;
    /* access modifiers changed from: private */
    public boolean mCaptureInProgress;
    /* access modifiers changed from: private */
    public int mCaptureOriginalTime;
    private Task mColorFormatTask;
    private ColorFormatChecker$ColorFormat mColorFormatTaskResult;
    private Context mContext;
    /* access modifiers changed from: private */
    public n36 mDeviceProfile;
    private volatile int mEntitlementJavaError;
    private volatile boolean mEntitlementUnavailable;
    private String mExportPath;
    private int mFastPreviewInProgressCount;
    private int mJPEGMaxHeight;
    private int mJPEGMaxSize;
    private int mJPEGMaxWidth;
    private int mPendingFastDisplay;
    private String mPendingFastPreview;
    private float mPreviewScaleFactor;
    private int mScreenMode;
    File mTranscodingTempFile;
    private NexThemeView mView;
    private OnAIBoxTrackingListener m_AIBoxTrackingListener;
    private OnAIVocalSeparatorListener m_AIVocalSeparatorListener;
    private OnAIVoiceListener m_AIVoiceListener;
    private OnActionRecognitionListener m_ActionRecognitionListener;
    private Size m_ExportResolution;
    private OnImageLabelingListener m_ImageLabelingListener;
    private OnJumpCutListener m_JumpCutListener;
    private OnSTTListener m_STTListener;
    private OnText2SpeechListener m_Text2SpeechListener;
    private Deque<OnAddClipDoneListener> m_addAudioClipDoneListeners;
    private Deque<OnAddClipDoneListener> m_addVisualClipDoneListeners;
    private String m_currentThemeId;
    private int m_currentTime;
    private Deque<OnDeleteClipDoneListener> m_deleteClipDoneListeners;
    private boolean m_didSetTimeSinceLastPlay;
    private int m_exportProjectDuration;
    private Task m_exportingTask;
    private int m_frameTime;
    private int m_getInfoTag;
    private int m_iNextClipID = 1;
    private boolean m_isPendingNonDisplaySeek;
    private boolean m_isPendingSeek;
    private boolean m_isPlaying;
    private boolean m_isSeeking;
    private ErrorCode m_lastSetTimeResult;
    private NexEditorEventListener m_listener;
    private Deque<Task> m_loadListAsyncTasks;
    private Deque<OnMoveClipDoneListener> m_moveClipDoneListeners;
    private Deque<Integer> m_movedClipId;
    private int m_nextTag;
    private OnCallbackInRendererContextListener m_onCallbackInRendererContextListener;
    private Deque<OnCaptureListener> m_onCaptureListeners;
    private Deque<OnCompletionListener> m_onCommandMarkerListeners;
    private OnEditorDestroyedListener m_onEditorDestroyedListener;
    private Deque<OnExportListener> m_onExportListeners;
    private OnGetClipInfoDoneListener m_onGetClipInfoDoneListener;
    private OnIdleListener m_onIdleListener;
    /* access modifiers changed from: private */
    public Deque<OnPlayListener> m_onPlayListeners;
    private Deque<OnPlayListener> m_onPlayStartListeners;
    private Deque<OnCompletionListener> m_onSetTimeCancelListeners;
    private Deque<OnCompletionListener> m_onStopListeners;
    private OnSurfaceChangeListener m_onSurfaceChangeListener;
    private int m_pendingNonDisplaySeekLocation;
    private int m_pendingSeekIDR;
    private int m_pendingSeekLocation;
    /* access modifiers changed from: private */
    public boolean m_playRequestPending;
    /* access modifiers changed from: private */
    public int m_playRequestSerial;
    /* access modifiers changed from: private */
    public boolean m_playRequested;
    private int m_reachedTag;
    private int m_seekSerial;
    private Deque<OnSetTimeDoneListener> m_setTimeDoneListeners;
    private Theme[] m_themeList;
    private Effect[] m_titleEffectList;
    private OnTranscodingListener m_transcodingListener;
    private Effect[] m_transitionEffectList;
    private NexEditorListener m_uiListener;
    private boolean m_useThemeManager;
    private long nativeHandle;
    /* access modifiers changed from: private */
    public ImageReader nxcapture;
    private OnAudioFeatureListener onAudioFeatureListener;
    private OnDiagnosticEventListener onDiagnosticEventListener;
    private Object seekSyncObject;
    private Thread thread_;
    private VideoTranscodingType transcodingType;
    /* access modifiers changed from: private */
    public Surface viewSurface;

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class AIBoxTrackingParam extends BaseTranscodingParam {
        public int m_iBoxHeight = 0;
        public int m_iBoxWidth = 0;
        public int m_iBoxX = 0;
        public int m_iBoxY = 0;
        public String m_sDstJsonPath = null;
        public String m_sOnnxPath = null;

        public AIBoxTrackingParam(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            this.m_eType = VideoTranscodingType.AIBOXTRACKING;
            this.m_sOnnxPath = str;
            this.m_sSrcClipPath = str2;
            this.m_sDstJsonPath = str3;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iBoxX = i3;
            this.m_iBoxY = i4;
            this.m_iBoxWidth = i5;
            this.m_iBoxHeight = i6;
            this.m_iUserTag = i7;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class AIVocalSeparatorParam extends BaseTranscodingParam {
        public int m_iMode = 0;
        public String m_sDstInstrumentsPath = null;
        public String m_sDstVocalPath = null;
        public String m_sInstrumentsOnnxPath = null;
        public String m_sSrcPcmPath = null;
        public String m_sVocalOnnxPath = null;

        public AIVocalSeparatorParam(String str, String str2, String str3, String str4, String str5, String str6, int i, int i2, int i3, int i4) {
            this.m_eType = VideoTranscodingType.AIVOCAL_SEPARATOR;
            this.m_sVocalOnnxPath = str;
            this.m_sInstrumentsOnnxPath = str2;
            this.m_sSrcClipPath = str3;
            this.m_sSrcPcmPath = str4;
            this.m_sDstVocalPath = str5;
            this.m_sDstInstrumentsPath = str6;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iMode = i3;
            this.m_iUserTag = i4;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class AIVoiceIVCClParam extends BaseTranscodingParam {
        public String m_sSourceEmPath = null;
        public String m_sSourcePcmPath = null;
        public String m_sTargetEmPath = null;

        public AIVoiceIVCClParam(String str, String str2, String str3, String str4, String str5, int i) {
            this.m_eType = VideoTranscodingType.AIVOICE_IVC_CL;
            this.m_sTFLitePath = str;
            this.m_sTargetEmPath = str2;
            this.m_sSourceEmPath = str3;
            this.m_sSourcePcmPath = str4;
            this.m_sDstPath = str5;
            this.m_iUserTag = i;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class AIVoiceIVCEmParam extends BaseTranscodingParam {
        public String m_sEmbeddingPath = null;
        public String m_sPCMPath = null;

        public AIVoiceIVCEmParam(String str, String str2, String str3, String str4, int i, int i2, int i3) {
            this.m_eType = VideoTranscodingType.AIVOICE_IVC_EM;
            this.m_sTFLitePath = str;
            this.m_sSrcClipPath = str2;
            this.m_sEmbeddingPath = str3;
            this.m_sPCMPath = str4;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iUserTag = i3;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class AIVoiceRVCClParam extends BaseTranscodingParam {
        public int m_iPitch = 0;
        public int m_iTargetFormant = 0;
        public int m_iTargetSamplingrate = 0;
        public String m_sSrcPcmPath = null;
        public String m_sTargetOnnxPath = null;

        public AIVoiceRVCClParam(String str, String str2, String str3, String str4, int i, int i2, int i3, int i4) {
            this.m_eType = VideoTranscodingType.AIVOICE_RVC_CL;
            this.m_sTFLitePath = str;
            this.m_sTargetOnnxPath = str2;
            this.m_sSrcPcmPath = str3;
            this.m_sDstPath = str4;
            this.m_iPitch = i;
            this.m_iTargetSamplingrate = i2;
            this.m_iTargetFormant = i3;
            this.m_iUserTag = i4;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class AIVoiceRVCEmParam extends BaseTranscodingParam {
        public int m_iTargetFormant = 0;
        public int m_iTargetSamplingrate = 0;
        public String m_sSrcPcmPath = null;
        public String m_sTargetOnnxPath = null;

        public AIVoiceRVCEmParam(String str, String str2, String str3, String str4, int i, int i2, int i3, int i4, int i5) {
            this.m_eType = VideoTranscodingType.AIVOICE_RVC_EM;
            this.m_sTFLitePath = str;
            this.m_sTargetOnnxPath = str2;
            this.m_sSrcClipPath = str3;
            this.m_sSrcPcmPath = str4;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iTargetSamplingrate = i3;
            this.m_iTargetFormant = i4;
            this.m_iUserTag = i5;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ActionRecognitionParam extends BaseTranscodingParam {
        public int m_iClipID = -1;
        public int m_iFrameInterval = 50;
        public int m_iMaxCount = 10;
        public int m_iUITime = 0;
        public String m_sCacheDirPath;

        public ActionRecognitionParam(String str, String str2, int i, int i2, int i3, int i4, int i5) {
            this.m_eType = VideoTranscodingType.ACTIONRECOGNITION;
            this.m_sTFLitePath = str;
            this.m_sCacheDirPath = str2;
            this.m_iClipID = i;
            this.m_iUITime = i2;
            this.m_iMaxCount = i3;
            this.m_iFrameInterval = i4;
            this.m_iUserTag = i5;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class BaseTranscodingParam {
        public VideoTranscodingType m_eType = VideoTranscodingType.NONE;
        public int m_iEndTime = 0;
        public int m_iProfile = 0;
        public int m_iStartTime = 0;
        public int m_iUserTag = 0;
        public int m_iVideoBitrate = 0;
        public int m_iVideoCodecType = 0;
        public int m_iVideoFPS = 0;
        public long m_lMaxFileSize = 0;
        public String m_sDstPath = null;
        public String m_sSrcClipPath = null;
        public String m_sTFLitePath = null;
        public String m_sUserData = null;
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface ClipCallback {
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class Effect extends ThemeElement {
        private String m_categoryTitle = null;
        private int m_defaultDuration = 0;
        private int m_effectOffset = 0;
        private String m_icon = null;
        private String m_id;
        private boolean m_isGlobal = true;
        private int m_maximumDuration = 0;
        private int m_minimumDuration = 0;
        private String m_name = null;
        private int m_videoOverlap = 0;

        public Effect(String str, String str2, String str3, boolean z, int i, int i2, int i3, int i4, int i5) {
            this.m_id = str;
            if (str2 != null && str2.contains("\t")) {
                int indexOf = str2.indexOf("\t");
                this.m_name = NexEditor.unescape(str2.substring(0, indexOf));
                this.m_categoryTitle = NexEditor.unescape(str2.substring(indexOf + 1));
                str2 = this.m_name;
            }
            String str4 = this.m_categoryTitle;
            if (str4 == null || str4.length() < 1) {
                this.m_categoryTitle = "Other";
            }
            if (str3 != null) {
                this.m_name = str2;
                this.m_icon = str3;
            } else if (str2.contains("$")) {
                int lastIndexOf = str2.lastIndexOf("$");
                this.m_name = str2.substring(0, lastIndexOf);
                this.m_icon = str2.substring(lastIndexOf + 1);
            } else {
                this.m_name = str2;
                this.m_icon = null;
            }
            this.m_effectOffset = i;
            this.m_videoOverlap = i2;
            this.m_maximumDuration = i4;
            this.m_minimumDuration = i3;
            this.m_defaultDuration = i5;
            this.m_isGlobal = z;
        }

        public String getCategoryTitle() {
            return this.m_categoryTitle;
        }

        public int getDefaultDuration() {
            return this.m_defaultDuration;
        }

        public String getIcon() {
            return this.m_icon;
        }

        public String getId() {
            return this.m_id;
        }

        public int getMaximumDuration() {
            return this.m_maximumDuration;
        }

        public int getMinimumDuration() {
            return this.m_minimumDuration;
        }

        public String getName() {
            return this.m_name;
        }

        public int getOffset() {
            return this.m_effectOffset;
        }

        public int getOverlap() {
            return this.m_videoOverlap;
        }

        public boolean isGlobal() {
            return this.m_isGlobal;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class EncodeProjectAIDataParam {
        public int m_iAIType;
        public int m_iBoxHeight = 0;
        public int m_iBoxWidth = 0;
        public int m_iBoxX = 0;
        public int m_iBoxY = 0;
        public String m_sOnnxPath = null;

        public EncodeProjectAIDataParam(String str, int i) {
            int i2 = EncodeProjectAIType.None.aiType;
            this.m_iAIType = i;
            this.m_sOnnxPath = str;
        }

        public void EncodeProjectAIBoxRect(int i, int i2, int i3, int i4) {
            this.m_iBoxX = i;
            this.m_iBoxY = i2;
            this.m_iBoxWidth = i3;
            this.m_iBoxHeight = i4;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum EncodeProjectAIType {
        None(0),
        AIBOXTRACKING(1);
        
        public int aiType;

        private EncodeProjectAIType(int i) {
            this.aiType = i;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum EngineVersion {
        MAJOR(1),
        MINOR(2),
        PATCH(3),
        BUILD(4);
        
        /* access modifiers changed from: private */
        public int versionType;

        private EngineVersion(int i) {
            this.versionType = i;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface FastPreviewBuilder {
        FastPreviewBuilder clipID(int i);

        FastPreviewBuilder colorAdj(FastPreviewOption fastPreviewOption, int i);

        FastPreviewBuilder cropRect(int i, int i2, int i3, int i4);

        FastPreviewBuilder cropRect(Rect rect);

        FastPreviewBuilder cts(int i);

        boolean execute();

        boolean executeNoDisplay();

        FastPreviewBuilder lutPower(int i);

        FastPreviewBuilder option(FastPreviewOption fastPreviewOption, int i);

        FastPreviewBuilder optionString(FastPreviewOption fastPreviewOption, String str);

        FastPreviewBuilder swapv(boolean z);

        FastPreviewBuilder video360(int i, int i2);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum FastPreviewOption {
        normal,
        clip_id,
        brightness,
        contrast,
        saturation,
        vibrance,
        hue,
        shadows,
        highlights,
        gain,
        lift,
        gamma,
        temperature,
        adj_brightness,
        adj_contrast,
        adj_saturation,
        tintColor,
        adj_vibrance,
        adj_hue,
        adj_shadows,
        adj_highlights,
        adj_gain,
        adj_lift,
        adj_gamma,
        adj_temperature,
        left,
        top,
        right,
        bottom,
        nofx,
        cts,
        swapv,
        video360flag,
        video360_horizontal,
        video360_vertical,
        adj_vignette,
        adj_vignetteRange,
        adj_sharpness,
        customlut_power,
        lut_power,
        start_trim,
        opacity,
        blurAmount,
        eid,
        evalue
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ImageLabelingParam extends BaseTranscodingParam {
        public String m_sCacheDirPath = null;
        public String m_sImageLabelingTFPath = null;
        public String m_sNSFWTFPath = null;

        public ImageLabelingParam(String str, String str2, String str3, String str4, int i) {
            this.m_eType = VideoTranscodingType.IMAGE_LABELING;
            this.m_sSrcClipPath = str;
            this.m_sImageLabelingTFPath = str2;
            this.m_sNSFWTFPath = str3;
            this.m_sCacheDirPath = str4;
            this.m_iUserTag = i;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class JumpCutParam extends BaseTranscodingParam {
        public int m_iFrontMargin;
        public int m_iShortestSilience;
        public int m_iThresholdDB;
        public String m_sPCMPath = null;

        public JumpCutParam(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6) {
            this.m_eType = VideoTranscodingType.JUMP_CUT;
            this.m_sSrcClipPath = str;
            this.m_sPCMPath = str2;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iShortestSilience = i3;
            this.m_iFrontMargin = i4;
            this.m_iThresholdDB = i5;
            this.m_iUserTag = i6;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public class KedlMediaInfo {
        float endTime = CropImageView.DEFAULT_ASPECT_RATIO;
        float fps = CropImageView.DEFAULT_ASPECT_RATIO;
        int height = 0;
        float speed = 1.0f;
        float startTime = CropImageView.DEFAULT_ASPECT_RATIO;
        boolean userfield = false;
        int width = 0;

        public KedlMediaInfo() {
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface LayerRenderCallback {
        void renderLayers(LayerRenderer layerRenderer);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnAIBoxTrackingListener {
        void onAIBoxTrackingDone(ErrorCode errorCode, int i, int i2, int i3);

        void onAIBoxTrackingProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnAIVocalSeparatorListener {
        void onAIVocalSeparatorDone(ErrorCode errorCode, int i, int i2, int i3);

        void onAIVocalSeparatorProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnAIVoiceListener {
        void onAIVoiceDone(ErrorCode errorCode, int i, int i2, int i3);

        void onAIVoiceProgress(int i, int i2, int i3, int i4);

        void onAIVoiceRVCInfo(int i);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnActionRecognitionListener {
        void onActionRecognitionDone(ErrorCode errorCode, int i, int i2, int i3);

        void onActionRecognitionInfo(int i, int i2, int i3, int i4);

        void onActionRecognitionProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnAddClipDoneListener {
        int clipId;

        public abstract void onAddClipDone(int i, int i2, int i3);

        public abstract void onAddClipFail(ErrorCode errorCode);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnAudioFeatureListener {
        void onBeatDetectionDone(ErrorCode errorCode, int i);

        void onProgress(int i, int i2, int i3);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnCallbackInRendererContextListener {
        public abstract void onCallbackInRendererContext(int i);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnCaptureListener {
        public abstract void onCapture(Bitmap bitmap, int i);

        public abstract void onCaptureFail(ErrorCode errorCode, int i);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnCompletionListener {
        /* access modifiers changed from: private */
        public int m_tag;

        public abstract void onComplete(ErrorCode errorCode);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnDeleteClipDoneListener {
        public abstract void onDeleteClipDone();

        public abstract void onDeleteClipFail(ErrorCode errorCode);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnDiagnosticEventListener {
        void onDiagnosticEvent(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnEditorDestroyedListener {
        void onEditorDestroyed();
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnExportListener {
        public abstract void onExportDone();

        public abstract void onExportFail(ErrorCode errorCode);

        public abstract void onExportProgress(int i);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnGetClipInfoDoneListener {
        void onGetClipInfoDone(ErrorCode errorCode, int i);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnGetThumbDoneListener {
        void onGetThumbDoneListener(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6, int i7, int i8);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnHighLightDoneListener {
        int onHighLightDoneListener(int i, int[] iArr);

        int onHighLightErrorListener(int i);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnIdleListener {
        void onIdle();
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnImageLabelingListener {
        void onImageLabelingDone(ErrorCode errorCode, int i, int i2, int i3);

        void onImageLabelingInfo(String str);

        void onImageLabelingProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnJumpCutListener {
        void onJumpCutDone(ErrorCode errorCode, int i, int i2, int i3);

        void onJumpCutInfo(int i, int i2);

        void onJumpCutProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnMoveClipDoneListener {
        public abstract void onMoveClipDone(int i);

        public abstract void onMoveClipFail(ErrorCode errorCode);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnPlayListener {
        public abstract void onPlayRequestProcessed(ErrorCode errorCode);

        public abstract void onPlayStarted();
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnSTTListener {
        void onSTTDone(ErrorCode errorCode, int i, int i2, int i3);

        void onSTTInfo(int i, int i2, String str);

        void onSTTProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnSeekDoneListener {
        public abstract void onSeekDone(int i, ErrorCode errorCode);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnSetTimeDoneListener {
        /* access modifiers changed from: private */
        public int m_reqTime;
        /* access modifiers changed from: private */
        public int m_serialNumber;
        StackTraceElement[] m_stackTrace;

        public abstract String getSetTimeLabel();

        public abstract void onSetTimeDone(int i, int i2);

        public abstract void onSetTimeFail(ErrorCode errorCode);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class OnSurfaceChangeListener {
        public abstract void onSurfaceChanged();
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnText2SpeechListener {
        void onTTSInfo(String str);

        void onText2SpeechDone(ErrorCode errorCode, int i, int i2, int i3);

        void onText2SpeechProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public interface OnTranscodingListener {
        void onAiFeatureMode(int i, int i2);

        void onTFMode(TFMode tFMode, int i);

        void onTranscodingDone(ErrorCode errorCode, int i, int i2, int i3);

        void onTranscodingProgress(int i, int i2, int i3, int i4);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum PlayState {
        NONE(0),
        IDLE(1),
        RUN(2),
        RECORD(3),
        PAUSE(4),
        RESUME(5);
        
        private int mValue;

        private PlayState(int i) {
            this.mValue = i;
        }

        public static PlayState fromValue(int i) {
            PlayState playState = NONE;
            if (i != 0) {
                if (i == 1) {
                    return IDLE;
                }
                if (i == 2) {
                    return RUN;
                }
                if (i == 3) {
                    return RECORD;
                }
                if (i == 4) {
                    return PAUSE;
                }
                if (i == 5) {
                    return RESUME;
                }
            }
            return playState;
        }

        public int getValue() {
            return this.mValue;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class STTParam extends BaseTranscodingParam {
        public int m_iInputLanguage;
        public String m_sAudioThumbPath;
        public String m_sVadPath;

        public STTParam(String str, String str2, String str3, int i, int i2, int i3, int i4, String str4) {
            this.m_eType = VideoTranscodingType.STT;
            this.m_sVadPath = str;
            this.m_sTFLitePath = str2;
            this.m_sSrcClipPath = str3;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iInputLanguage = i3;
            this.m_iUserTag = i4;
            this.m_sAudioThumbPath = str4;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum TFMode {
        TF_GPU,
        TF_CPU,
        TF_NONE
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class Text2SpeechParam extends BaseTranscodingParam {
        public float[] m_fScales = new float[3];
        public int m_iLangidInternal = -1;
        public int m_iLanguage = 0;
        public int m_iModelType = 0;
        public int m_iSamplingRate = 22050;
        public int m_iSpeakerId = 0;
        public ArrayList<int[]> m_iTokens = null;
        public int m_iUserTag = 0;
        public ArrayList<String> m_sSplitTexts = null;
        public String m_sT2MPath = null;
        public String m_sVOCPath = null;

        public Text2SpeechParam(String str, String str2, String str3, int i, int i2, int i3, float[] fArr, int i4, int i5, ArrayList<String> arrayList, ArrayList<int[]> arrayList2, int i6) {
            this.m_eType = VideoTranscodingType.TTS;
            this.m_sDstPath = str;
            this.m_sT2MPath = str2;
            this.m_sVOCPath = str3;
            this.m_iLanguage = i;
            this.m_iSpeakerId = i2;
            this.m_iUserTag = i6;
            this.m_iModelType = i3;
            this.m_fScales = fArr != null ? (float[]) fArr.clone() : new float[]{0.0f, 0.0f, 0.0f};
            this.m_iLangidInternal = i4;
            this.m_iSamplingRate = i5;
            this.m_sSplitTexts = arrayList;
            this.m_iTokens = arrayList2;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class Theme extends ThemeElement {
        private String m_icon = null;
        private String m_id;
        private String m_name = null;

        public Theme(String str, String str2) {
            this.m_id = str;
            if (str2.contains("$")) {
                int lastIndexOf = str2.lastIndexOf("$");
                this.m_name = str2.substring(0, lastIndexOf);
                this.m_icon = str2.substring(lastIndexOf + 1);
                return;
            }
            this.m_name = str2;
            this.m_icon = null;
        }

        public String getIcon() {
            return this.m_icon;
        }

        public String getId() {
            return this.m_id;
        }

        public String getName() {
            return this.m_name;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static abstract class ThemeElement {
        public abstract String getIcon();

        public abstract String getId();

        public abstract String getName();
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public abstract class ThemeItemCallback {
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoAiStyleParam extends BaseTranscodingParam {
        public int m_iHeight;
        public int m_iWidth;
        public String m_sCacheDirPath;

        public VideoAiStyleParam(String str, String str2, String str3, String str4, int i, int i2, int i3, int i4, int i5, int i6, long j, int i7, int i8, int i9) {
            this.m_eType = VideoTranscodingType.AI_STYLE;
            this.m_sTFLitePath = str;
            this.m_sCacheDirPath = str2;
            this.m_sSrcClipPath = str3;
            this.m_sDstPath = str4;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iWidth = i3;
            this.m_iHeight = i4;
            this.m_iVideoBitrate = i5;
            this.m_iVideoFPS = i6;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i7;
            this.m_iVideoCodecType = i8;
            this.m_iUserTag = i9;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoNoiseReductionParam extends BaseTranscodingParam {
        public VideoNoiseReductionParam(String str, String str2, String str3, int i, int i2, long j, int i3) {
            this.m_eType = VideoTranscodingType.NOISE_REDUCTION;
            this.m_sTFLitePath = str;
            this.m_sSrcClipPath = str2;
            this.m_sDstPath = str3;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_lMaxFileSize = j;
            this.m_iUserTag = i3;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoSegmentationParam extends BaseTranscodingParam {
        public String m_sCacheDirPath;

        public VideoSegmentationParam(String str, String str2, String str3, String str4, int i, int i2, long j, int i3, int i4, int i5) {
            this.m_eType = VideoTranscodingType.SEGMENTATION;
            this.m_sTFLitePath = str;
            this.m_sCacheDirPath = str2;
            this.m_sSrcClipPath = str3;
            this.m_sDstPath = str4;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i3;
            this.m_iVideoCodecType = i4;
            this.m_iUserTag = i5;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoStabilizerParam extends BaseTranscodingParam {
        public int m_iHeight;
        public int m_iWidth;
        public String m_sTmpPath;

        public VideoStabilizerParam(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, long j, int i7, int i8, int i9) {
            this.m_eType = VideoTranscodingType.VIDEO_STABILIZER;
            this.m_sSrcClipPath = str;
            this.m_sDstPath = str2;
            this.m_sTmpPath = str3;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iWidth = i3;
            this.m_iHeight = i4;
            this.m_iVideoBitrate = i5;
            this.m_iVideoFPS = i6;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i7;
            this.m_iVideoCodecType = i8;
            this.m_iUserTag = i9;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoSuperResolutionParam extends BaseTranscodingParam {
        public int m_iScale = 0;
        public Point m_pDstPoint = null;
        public Size m_sDstRes = null;

        public VideoSuperResolutionParam(String str, String str2, String str3, int i, int i2, int i3, Point point, Size size, int i4, int i5, long j, int i6, int i7, int i8) {
            this.m_eType = VideoTranscodingType.SUPER_RESOLUTION;
            this.m_sTFLitePath = str;
            this.m_sSrcClipPath = str2;
            this.m_sDstPath = str3;
            this.m_iScale = i;
            this.m_iStartTime = i2;
            this.m_iEndTime = i3;
            this.m_pDstPoint = point;
            this.m_sDstRes = size;
            this.m_iVideoBitrate = i4;
            this.m_iVideoFPS = i5;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i6;
            this.m_iVideoCodecType = i7;
            this.m_iUserTag = i8;
        }
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum VideoTranscodingType {
        NONE,
        TRANSCODING,
        REVERSE,
        SEGMENTATION,
        SUPER_RESOLUTION,
        AI_STYLE,
        NOISE_REDUCTION,
        VIDEO_STABILIZER,
        STT,
        JUMP_CUT,
        ACTIONRECOGNITION,
        TTS,
        IMAGE_LABELING,
        AIVOICE_IVC_EM,
        AIVOICE_IVC_CL,
        AIVOICE_RVC_EM,
        AIVOICE_RVC_CL,
        AIVOCAL_SEPARATOR,
        AIBOXTRACKING
    }

    static {
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            System.loadLibrary("nexeditorsdk");
        } catch (UnsatisfiedLinkError e) {
            Log.e(LOG_TAG, "[NexEditor.java] nexeditor load failed : " + e);
        } finally {
            StrictMode.setThreadPolicy(allowThreadDiskReads);
        }
    }

    public NexEditor(Context context, n36 n36, EffectResourceLoader effectResourceLoader, NexThemeView nexThemeView, String str, int i, NexImageLoader.OverlayPathResolver overlayPathResolver, int[] iArr) throws EditorInitException {
        int[] copyOf;
        NexThemeView nexThemeView2 = nexThemeView;
        int[] iArr2 = iArr;
        boolean z = true;
        boolean z2 = false;
        this.m_isPlaying = false;
        this.m_useThemeManager = false;
        this.mContext = null;
        this.m_listener = null;
        this.mView = null;
        this.m_uiListener = null;
        this.m_themeList = null;
        this.m_transitionEffectList = null;
        this.m_titleEffectList = null;
        this.m_didSetTimeSinceLastPlay = false;
        this.m_addAudioClipDoneListeners = new LinkedList();
        this.m_addVisualClipDoneListeners = new LinkedList();
        this.m_deleteClipDoneListeners = new LinkedList();
        this.m_moveClipDoneListeners = new LinkedList();
        this.m_setTimeDoneListeners = new LinkedList();
        this.m_onStopListeners = new LinkedList();
        this.m_onPlayListeners = new LinkedList();
        this.m_onPlayStartListeners = new LinkedList();
        this.m_onCommandMarkerListeners = new LinkedList();
        this.m_onSetTimeCancelListeners = new LinkedList();
        this.m_movedClipId = new LinkedList();
        this.m_loadListAsyncTasks = new ArrayDeque();
        this.m_onCaptureListeners = new LinkedList();
        this.m_onExportListeners = new LinkedList();
        this.m_onSurfaceChangeListener = null;
        this.m_onCallbackInRendererContextListener = null;
        this.m_nextTag = 1;
        this.m_reachedTag = 0;
        this.m_currentTime = 0;
        this.m_frameTime = 0;
        this.m_lastSetTimeResult = null;
        this.m_getInfoTag = 0;
        this.m_ExportResolution = null;
        this.mColorFormatTaskResult = null;
        this.mFastPreviewInProgressCount = 0;
        this.mPendingFastPreview = null;
        this.mDeviceProfile = null;
        this.mAspectMode = 1;
        this.mScreenMode = 0;
        this.thread_ = null;
        this.mColorFormatTask = null;
        this.fastOptionPreviewTask = null;
        this.nativeHandle = 0;
        this.mEntitlementUnavailable = false;
        this.mEntitlementJavaError = 0;
        this.transcodingType = VideoTranscodingType.NONE;
        this.captureSurfaceCallback = new ga9(this, 2);
        this.mCaptureFrameInProgress = false;
        this.mCaptureInProgress = false;
        this.mCaptureCompleting = false;
        this.m_playRequestSerial = 0;
        this.m_playRequestPending = false;
        this.m_playRequested = false;
        this.m_exportingTask = null;
        this.nxcapture = null;
        this.m_isSeeking = false;
        this.m_isPendingSeek = false;
        this.m_pendingSeekLocation = 0;
        this.m_pendingSeekIDR = 0;
        this.m_isPendingNonDisplaySeek = false;
        this.m_pendingNonDisplaySeekLocation = 0;
        this.m_seekSerial = 0;
        this.seekSyncObject = new Object();
        this.mPreviewScaleFactor = 1.0f;
        this.mTranscodingTempFile = null;
        this.mContext = context;
        this.mDeviceProfile = n36;
        String engineNativeLibPath = NexEditorUtils.getEngineNativeLibPath(context);
        setApplicationPacakge4Protection(context.getPackageName());
        check4km(context);
        int i2 = Build.VERSION.SDK_INT;
        int initUserData = (i ^ 323655054) ^ initUserData();
        if (iArr2 == null) {
            copyOf = null;
        } else {
            copyOf = Arrays.copyOf(iArr2, iArr2.length + 2);
        }
        int createEditor = createEditor(engineNativeLibPath, str, i2, initUserData, copyOf);
        if (createEditor == 0) {
            String str2 = "1";
            if (!isCheckLibOpenCL) {
                isCheckLibOpenCL = true;
                try {
                    System.loadLibrary("OpenCL-pixel");
                    z2 = true;
                } catch (UnsatisfiedLinkError unused) {
                }
                try {
                    System.loadLibrary("OpenCL-car");
                    z2 = true;
                } catch (UnsatisfiedLinkError unused2) {
                }
                try {
                    System.loadLibrary("OpenCL");
                } catch (UnsatisfiedLinkError unused3) {
                    z = z2;
                }
                if (z) {
                    setProperty("TFLiteModeReferOpenGL", "2");
                } else {
                    setProperty("TFLiteModeReferOpenGL", str2);
                }
                if (NexEditorUtils.getFP16CpuInfo() && isAbiArmV8()) {
                    setProperty("SupportFP16InArmV8", str2);
                } else if (isAbiArmV8()) {
                    setProperty("SupportFP16InArmV8", "2");
                } else {
                    setProperty("SupportFP16InArmV8", "0");
                }
            }
            setCodecLimits(-1, -1, -1);
            setProperty("FeatureVersion", "3");
            setProperty("DeviceExtendMode", str2);
            setProperty("InputMaxFPS", "" + this.mDeviceProfile.e());
            this.mDeviceProfile.getClass();
            setProperty("UseAndroidJPEG", str2);
            this.mDeviceProfile.getClass();
            setProperty("WaitGLRenderFinish", "0");
            this.mDeviceProfile.getClass();
            setProperty("SupportFrameTimeChecker", str2);
            setProperty("UseContentAbsolutePath", !this.mDeviceProfile.g() ? "0" : str2);
            this.mDeviceProfile.getClass();
            setProperty("ContentResolverTimeout", "3000");
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int max = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
            int i3 = CrashConfig.MAX_STACK_FRAMES_UPPER_BOUND;
            while (true) {
                int i4 = i3 * 2;
                if (i4 >= max) {
                    break;
                }
                i3 = i4;
            }
            this.mJPEGMaxWidth = i3;
            this.mJPEGMaxHeight = i3;
            this.mJPEGMaxSize = i3 * i3;
            setProperty("JpegMaxWidthFactor", "" + this.mJPEGMaxWidth);
            setProperty("JpegMaxHeightFactor", "" + this.mJPEGMaxHeight);
            setProperty("JpegMaxSizeFactor", "" + this.mJPEGMaxSize);
            setProperty("supportContentDuration", StatisticData.ERROR_CODE_NOT_FOUND);
            String sysProperty = getSysProperty("ro.product.model");
            if (sysProperty != null && sysProperty.toUpperCase().startsWith("VTR-AL00")) {
                setProperty("HDR2SDR", "0");
            }
            NexThemeView nexThemeView3 = this.mView;
            if (nexThemeView3 != nexThemeView2) {
                if (nexThemeView3 != null) {
                    nexThemeView3.linkToEditor((NexEditor) null);
                }
                nexThemeView.linkToEditor(this);
                this.mView = nexThemeView2;
            }
            NexEditorEventListener nexEditorEventListener = new NexEditorEventListener(this, context, effectResourceLoader, overlayPathResolver);
            this.m_listener = nexEditorEventListener;
            setEventHandler(nexEditorEventListener);
            return;
        }
        throw new EditorInitException(x5.f("Editor Initialization Failed (result=", createEditor, ")"));
    }

    private native int actionRecognitionStartInternal(ActionRecognitionParam actionRecognitionParam);

    private native int addAudioClip(int i, String str, int i2);

    private native int addVisualClip(int i, String str, int i2);

    private native int aiBoxTrackingStart(AIBoxTrackingParam aIBoxTrackingParam);

    private native int aiBoxTrackingStop();

    private native int aiStyleStartInternal(VideoAiStyleParam videoAiStyleParam);

    private native int aiStyleStop();

    private native int aiVocalSeparatorStart(AIVocalSeparatorParam aIVocalSeparatorParam);

    private native int aiVocalSeparatorStop();

    private native int aiVoiceIVCClStart(AIVoiceIVCClParam aIVoiceIVCClParam);

    private native int aiVoiceIVCEmStart(AIVoiceIVCEmParam aIVoiceIVCEmParam);

    private native int aiVoiceRVCClStart(AIVoiceRVCClParam aIVoiceRVCClParam);

    private native int aiVoiceRVCEmStart(AIVoiceRVCEmParam aIVoiceRVCEmParam);

    private native int aiVoiceStop();

    private native int asyncLoadList(NexVisualClip[] nexVisualClipArr, NexAudioClip[] nexAudioClipArr, int i);

    private boolean canUpdateAudioClip(NexAudioClip nexAudioClip) {
        NexAudioClip audioClip = getAudioClip(nexAudioClip.mClipID);
        if (nexAudioClip.mAudioOnOff == audioClip.mAudioOnOff && nexAudioClip.mClipVolume == audioClip.mClipVolume && nexAudioClip.mPanLeft == audioClip.mPanLeft && nexAudioClip.mPanRight == audioClip.mPanRight) {
            return false;
        }
        return true;
    }

    private boolean canUpdateVisualClip(NexVisualClip nexVisualClip) {
        NexVisualClip visualClip = getVisualClip(nexVisualClip.mClipID);
        if (nexVisualClip.mBrightness == visualClip.mBrightness && nexVisualClip.mContrast == visualClip.mContrast && nexVisualClip.mSaturation == visualClip.mSaturation && nexVisualClip.mVibrance == visualClip.mVibrance && nexVisualClip.mHue == visualClip.mHue && nexVisualClip.mShadows == visualClip.mShadows && nexVisualClip.mHighlights == visualClip.mHighlights && nexVisualClip.mGain == visualClip.mGain && nexVisualClip.mLift == visualClip.mLift && nexVisualClip.mGamma == visualClip.mGamma && nexVisualClip.mTemperature == visualClip.mTemperature && nexVisualClip.mTintcolor == visualClip.mTintcolor && nexVisualClip.mLUT == visualClip.mLUT && nexVisualClip.mLUT_Power == visualClip.mLUT_Power && nexVisualClip.mCustomLUT_A == visualClip.mCustomLUT_A && nexVisualClip.mCustomLUT_B == visualClip.mCustomLUT_B && nexVisualClip.mCustomLUT_Power == visualClip.mCustomLUT_Power && nexVisualClip.mVignette == visualClip.mVignette && nexVisualClip.mAudioOnOff == visualClip.mAudioOnOff && nexVisualClip.mClipVolume == visualClip.mClipVolume && nexVisualClip.mBGMVolume == visualClip.mBGMVolume && nexVisualClip.mPanLeft == visualClip.mPanLeft && nexVisualClip.mPanRight == visualClip.mPanRight) {
            return false;
        }
        return true;
    }

    private native int cancelNativeBeatDetection(int i);

    private native int captureCurrentFrame(boolean z);

    /* access modifiers changed from: private */
    public int captureCurrentFrameInternal(boolean z, OnCaptureListener onCaptureListener) {
        int captureCurrentFrame = captureCurrentFrame(z);
        if (captureCurrentFrame == 0) {
            this.m_onCaptureListeners.add(onCaptureListener);
        }
        return captureCurrentFrame;
    }

    private native int captureSelectedClip(int i, boolean z, int i2);

    private native int check4km(Context context);

    private native int cleanupMaskWithWhite(int i);

    private native int clearCachedVisualTrack(int i);

    private native int clearList();

    private native int clearRenderItems(int i);

    private native int clearScreen(int i);

    private native int closeOutputFile(int i);

    private native int commandMarker(int i);

    private native boolean compressVideoContentsInKMProject(String str, String str2, long j, int i);

    private native boolean compressVideoContentsStop();

    private native int createEditor(String str, String str2, int i, int i2, int[] iArr);

    private native int createNexEDL(String str, int i);

    private native int createRenderItem(String str, int i);

    private native int deleteClipID(int i);

    private native int destroyEditor();

    private native int directExport(String str, long j, long j2, String str2, int i);

    private native int drawNexEDLOverlay(int i, int i2, String str, int i3, int i4, int i5, int i6, float[] fArr, float f, float f2, float f3, float f4, float f5, int i7);

    private native int drawRenderItemOverlay(int i, int i2, String str, int i3, int i4, int i5, int i6, float[] fArr, int i7, int i8, float f, float f2, float f3, float f4, float f5, boolean z);

    private void dumpSetTimeDoneLabels() {
        for (OnSetTimeDoneListener next : this.m_setTimeDoneListeners) {
        }
    }

    private native int encodeProject(String str, String str2, int i, int i2, Size size, Rect rect, int i3, long j, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, boolean z, int i13);

    private int encodeProjectInternal(String str, String str2, int i, int i2, Size size, Rect rect, int i3, long j, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, boolean z, int i13) {
        int i14;
        Size size2;
        NexEditor nexEditor;
        int i15;
        String chipset = getChipset();
        if (!Build.MANUFACTURER.equalsIgnoreCase("HUAWEI") || !chipset.equalsIgnoreCase("kirin980")) {
            i15 = i3;
        } else {
            i15 = i3;
            if (i15 > 80000000) {
                i14 = 80000000;
                nexEditor = this;
                size2 = size;
                nexEditor.m_ExportResolution = size2;
                return nexEditor.encodeProject(str, str2, i, i2, size2, rect, i14, j, i4, i5, i6, i7, i8, i9, i10, i11, i12, z, i13);
            }
        }
        i14 = i15;
        size2 = size;
        nexEditor = this;
        nexEditor.m_ExportResolution = size2;
        return nexEditor.encodeProject(str, str2, i, i2, size2, rect, i14, j, i4, i5, i6, i7, i8, i9, i10, i11, i12, z, i13);
    }

    private native int encodeProjectJpeg(Surface surface, String str, int i, int i2, int i3, int i4);

    private native int encodeProjectToAIData(String str, int i, int i2, Size size, int i3, EncodeProjectAIDataParam encodeProjectAIDataParam);

    private native int encodeProjectToGIF(String str, int i, int i2, Size size, int i3, int i4, int i5, int i6, float f, int i7, int i8, int i9, boolean z, boolean z2);

    private native int encodeProjectToWEBP(String str, int i, int i2, Size size, int i3, int i4, int i5, boolean z);

    private native int entitlementClearEnrollmentN();

    private native int entitlementClearSubscriptionN(int i);

    private native int entitlementDiscardPendingN();

    private native String entitlementEnrollStateN();

    private native int entitlementInstallEnrollN(byte[] bArr, byte[] bArr2, long j);

    private native int entitlementInstallSubscriptionN(byte[] bArr, byte[] bArr2, long j, int i);

    private native long entitlementLastAckExpN();

    private native long entitlementLastAckIatN();

    private native int entitlementLastErrorFailedKeyN();

    private native int entitlementLastErrorFallbackPlatformCodeN();

    private native int entitlementLastErrorFallbackRetryPolicyN();

    private native int entitlementLastErrorN();

    private native int entitlementLastErrorPlatformCodeN();

    private native int entitlementLastErrorRetryPolicyN();

    private native String entitlementLastKeystoreFailureN();

    private native int entitlementLastProvisionFallbackN();

    private native int entitlementLastProvisionStageN();

    private native int entitlementLastProvisionStallHintN();

    private native String entitlementProvisionN(byte[] bArr, long j);

    private static native int entitlementRegisteredCountN();

    private native byte[] entitlementSignN(byte[] bArr);

    private native int entitlementStuckOriginN();

    private native String entitlementSubscriptionStateN(long j, int i);

    private native int fastOptionPreview(String str, int i);

    /* access modifiers changed from: private */
    public boolean fastOptionPreviewWrapper(String str, int i) {
        if (this.m_lastSetTimeResult != ErrorCode.NONE) {
            return false;
        }
        if (this.mFastPreviewInProgressCount >= 2) {
            this.mPendingFastPreview = str;
            this.mPendingFastDisplay = i;
            return true;
        }
        int fastOptionPreview = fastOptionPreview(str, i);
        if (fastOptionPreview == 0) {
            this.mFastPreviewInProgressCount++;
        }
        if (fastOptionPreview == 0) {
            return true;
        }
        return false;
    }

    private void fastPreviewForCapture(FastPreviewOption fastPreviewOption, int i, boolean z) {
        fastOptionPreviewWrapper(fastPreviewOption.name() + "=" + i, z ? 1 : 0);
    }

    private native int getAudioSessionID();

    private String getChipset() {
        String sysProperty = getSysProperty("ro.board.platform");
        String sysProperty2 = getSysProperty("ro.product.board");
        if (sysProperty != null && sysProperty.trim().equalsIgnoreCase("msm8916") && sysProperty2 != null && sysProperty2.trim().equalsIgnoreCase("msm8939")) {
            sysProperty = "msm8939";
        }
        if (sysProperty != null && sysProperty.trim().equalsIgnoreCase("msm8960") && sysProperty2 != null && sysProperty2.trim().equalsIgnoreCase("8x30")) {
            sysProperty = "MSM8x30n";
        }
        if (sysProperty == null || !sysProperty.trim().equalsIgnoreCase("exynos5") || sysProperty2 == null || !sysProperty2.startsWith("universal") || sysProperty2.startsWith("universal5")) {
            sysProperty2 = sysProperty;
        }
        if (sysProperty2 == null || sysProperty2.trim().length() < 1) {
            sysProperty2 = getSysProperty("ro.mediatek.platform");
        }
        if (sysProperty2 == null || sysProperty2.trim().length() < 1) {
            sysProperty2 = getSysProperty("ro.hardware");
        }
        if (sysProperty2 == null || sysProperty2.trim().length() < 1) {
            return KMEvents.UNKNOWN_NAME;
        }
        return sysProperty2;
    }

    private native int getClipAudioThumb(String str, String str2, String str3, int i, int i2, int i3);

    private native int getClipAudioThumbPartial(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6);

    private native int getClipEffectList(ThemeItemCallback themeItemCallback);

    private native int getClipInfo(String str, String str2, String str3, NexClipInfo nexClipInfo, int i, int i2, int i3, int i4, int i5);

    private native int getClipInfoSyncInternal(String str, NexClipInfo nexClipInfo, int i, int i2);

    private native int getClipInfoSyncInternalStop(String str);

    private native int getClipList(ClipCallback clipCallback);

    private native int getClipStopThumb(int i);

    private native int getClipVideoThumb(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    private native int getClipVideoThumbWithTimeTable(String str, String str2, int i, int i2, int i3, int[] iArr, int i4, int i5);

    private Task getFastOptionPreviewTask() {
        if (this.fastOptionPreviewTask == null) {
            this.fastOptionPreviewTask = new Task();
        }
        return this.fastOptionPreviewTask;
    }

    private native ArrayList<KedlMediaInfo> getKedlMediaInfoList(String str);

    public static int getLayerHeight() {
        return layerHeight;
    }

    public static int getLayerMode() {
        return layerMode;
    }

    public static int getLayerWidth() {
        return layerWidth;
    }

    private native String getLoadThemeError(int i);

    private native int getNativeBeatDetectionDoneResult(NexAudioFeatureResultInfo nexAudioFeatureResultInfo, int i);

    private native int getNativeSDKInfoWM();

    public static int getOpenGLIntVersionFromStrVersion(String str) {
        str.getClass();
        char c = 65535;
        switch (str.hashCode()) {
            case -1274344459:
                if (str.equals(OPENGL_ES_VER_31_EXT)) {
                    c = 0;
                    break;
                }
                break;
            case -1078949933:
                if (str.equals(OPENGL_ES_VER_20)) {
                    c = 1;
                    break;
                }
                break;
            case -1078948972:
                if (str.equals(OPENGL_ES_VER_30)) {
                    c = 2;
                    break;
                }
                break;
            case -1078948971:
                if (str.equals(OPENGL_ES_VER_31)) {
                    c = 3;
                    break;
                }
                break;
            case -1078948970:
                if (str.equals(OPENGL_ES_VER_32)) {
                    c = 4;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 3:
            case 4:
                return 3;
            case 1:
                return 2;
            case 2:
                return 3;
            default:
                return 2;
        }
    }

    /* access modifiers changed from: private */
    public static native long getPerformanceCount(int i);

    private native float getPreviewScaleFactor();

    public static String getSysProperty(String str) {
        try {
            return getSystemProperty(str);
        } catch (UnsatisfiedLinkError unused) {
            sLinkError = true;
            return "";
        }
    }

    private static native String getSystemProperty(String str);

    private native int getTexNameForBlend(int i);

    private native int getTexNameForClipID(int i, int i2);

    private native int getTexNameForClipIDExternal(int i, int i2, float[] fArr);

    private native int getTexNameForMask(int i);

    private native int getTexNameForWhite(int i);

    private native String getThemeID();

    private native int getThemeList(ThemeItemCallback themeItemCallback);

    private native byte[] getTimeThumbData(int i);

    private native int getTitleEffectList(ThemeItemCallback themeItemCallback);

    public static boolean hadLinkErorr() {
        return sLinkError;
    }

    private native int highlightStart(String str, int i, int i2, int i3, int i4, String str2, int i5, int i6, int i7, long j, int i8);

    private native int highlightStop();

    private native int imageLabelingStart(ImageLabelingParam imageLabelingParam);

    private native int imageLabelingStop();

    private native int initUserData();

    private boolean isAbiArmV8() {
        return Build.SUPPORTED_ABIS[0].equals("arm64-v8a");
    }

    private static boolean isSubKind(int i) {
        if (i == 0 || i == 1) {
            return true;
        }
        return false;
    }

    private native int jumpCutStart(JumpCutParam jumpCutParam);

    private native int loadClipList(NexVisualClip[] nexVisualClipArr, NexAudioClip[] nexAudioClipArr);

    private native int loadList(NexVisualClip[] nexVisualClipArr, NexAudioClip[] nexAudioClipArr, int i);

    private native int loadRenderItem(String str, String str2, int i);

    private native int loadTheme(String str, String str2, int i);

    private native int modifyAudioClipN(NexAudioClip nexAudioClip);

    private native int modifyVisualClipN(NexVisualClip nexVisualClip);

    private native int moveAudioClip(int i, int i2);

    private native int moveVisualClip(int i, int i2);

    private native int noiseReductionStartInternal(VideoNoiseReductionParam videoNoiseReductionParam);

    private native int noiseReductionStop();

    private native int pausePlay();

    /* access modifiers changed from: private */
    public native int prepareSurface(Surface surface);

    private native int pushLoadedBitmap(String str, int[] iArr, int i, int i2, int i3);

    private native int releaseLUTTexture(int i);

    private native int releaseNexEDL(int i, int i2);

    private native int releaseRenderItem(int i, int i2);

    private void releaseTranscodingParameter() {
        File file = this.mTranscodingTempFile;
        if (file != null) {
            if (file.exists()) {
                Log.d(LOG_TAG, "Delete temp file! " + this.mTranscodingTempFile.getName());
                this.mTranscodingTempFile.delete();
            }
            this.mTranscodingTempFile = null;
        }
        this.transcodingType = VideoTranscodingType.NONE;
    }

    private native int removeBitmap(String str);

    private native int requestCallbackInRenderContext(int i);

    private native int resetFaceDetectInfo(int i);

    private native int resumePlay();

    private native int reverseStartInternal(String str, String str2, String str3, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7, int i8);

    private native int reverseStop();

    private ErrorCode reverseTranscodingStart(String str, File file, File file2, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7, int i8) {
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        this.mExportPath = getExportContentResolverPath(file);
        String exportContentResolverPath = getExportContentResolverPath(file2);
        Log.d(LOG_TAG, "reverseTranscodingStart_internal() : Destination Path" + this.mExportPath);
        ErrorCode fromValue = ErrorCode.fromValue(reverseStartInternal(str, this.mExportPath, exportContentResolverPath, i, i2, i3, j, i4, i5, i6, i7, i8));
        if (fromValue.isError()) {
            return fromValue;
        }
        this.transcodingType = VideoTranscodingType.REVERSE;
        return fromValue;
    }

    private String seekStateDump() {
        return " m_seekSerial=" + this.m_seekSerial + "; m_isSeeking=" + this.m_isSeeking + "; m_isPendingSeek=" + this.m_isPendingSeek + "; m_pendingSeekLocation=" + this.m_pendingSeekLocation + "; m_setTimeDoneListeners.size()=" + this.m_setTimeDoneListeners.size();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0066, code lost:
        return;
     */
    private void seek_internal(int i, boolean z, int i2, OnSetTimeDoneListener onSetTimeDoneListener) {
        synchronized (this.seekSyncObject) {
            if (onSetTimeDoneListener != null) {
                try {
                    onSetTimeDoneListener.m_reqTime = i;
                    onSetTimeDoneListener.m_serialNumber = this.m_seekSerial;
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (this.m_isSeeking) {
                if (onSetTimeDoneListener != null) {
                    this.m_setTimeDoneListeners.add(onSetTimeDoneListener);
                }
                if (z) {
                    this.m_isPendingSeek = true;
                    this.m_pendingSeekLocation = i;
                    this.m_pendingSeekIDR = i2;
                } else {
                    this.m_isPendingNonDisplaySeek = true;
                    this.m_pendingNonDisplaySeekLocation = i;
                }
            } else {
                this.m_seekSerial++;
                if (onSetTimeDoneListener != null) {
                    this.m_setTimeDoneListeners.add(onSetTimeDoneListener);
                }
                this.m_didSetTimeSinceLastPlay = true;
                setIsSeekingFlag(true);
                int time = setTime(i, z ? 1 : 0, i2);
                if (time != 0) {
                    if (onSetTimeDoneListener != null) {
                        onSetTimeDoneListener.onSetTimeFail(ErrorCode.fromValue(time));
                    }
                    if (onSetTimeDoneListener != null) {
                        this.m_setTimeDoneListeners.remove(onSetTimeDoneListener);
                    }
                    this.m_seekSerial--;
                    setIsSeekingFlag(false);
                    return;
                }
                this.m_pendingSeekLocation = i;
                if (z) {
                    this.m_isPendingSeek = false;
                } else {
                    this.m_isPendingNonDisplaySeek = false;
                }
            }
        }
    }

    private native int segmentationStartInternal(String str, String str2, String str3, String str4, int i, int i2, long j, int i3, int i4, int i5);

    private native int segmentationStop();

    private native int set360VideoTrackPosition(int i, int i2, int i3);

    public static void setApplicationPacakge4Protection(String str) {
        setPacakgeName4Protection(str);
    }

    private native int setBackgroundMusic(String str, int i);

    private native int setEventHandler(NexEditorEventListener nexEditorEventListener);

    private native int setGLVersion(int i);

    private void setIsSeekingFlag(boolean z) {
        if (z != this.m_isSeeking) {
            this.m_isSeeking = z;
            NexEditorListener nexEditorListener = this.m_uiListener;
            if (nexEditorListener != null) {
                nexEditorListener.onSeekStateChanged(z);
            }
        }
    }

    public static void setLayerScreen(int i, int i2, int i3) {
        layerWidth = i;
        layerHeight = i2;
        layerMode = i3;
    }

    private native int setOutputFile(FileDescriptor fileDescriptor, long j, long j2);

    private static native void setPacakgeName4Protection(String str);

    public static void setPrepareSurfaceSetToNull(boolean z) {
        sPrepareSurfaceSetToNull = z;
    }

    private native int setPreviewScaleFactor(float f);

    private native int setRenderToDefault(int i);

    private native int setRenderToMask(int i);

    private native int setTexNameForLUT(int i, int i2, float f, float f2);

    private native int setThemeID(String str);

    private native int setThumbnailRoutine(int i);

    private native int setTime(int i, int i2, int i3);

    private void setTimeCancel(OnCompletionListener onCompletionListener) {
        this.m_isPendingSeek = false;
        this.m_isPendingNonDisplaySeek = false;
        if (!this.m_isSeeking) {
            onCompletionListener.onComplete(ErrorCode.NONE);
        } else if (onCompletionListener != null) {
            this.m_onSetTimeCancelListeners.add(onCompletionListener);
        }
    }

    private String setTimeListenerDump() {
        String str = "";
        int i = 0;
        for (OnSetTimeDoneListener next : this.m_setTimeDoneListeners) {
            i++;
            String str2 = str + "\n     " + i + ": " + next + " m_reqTime=" + next.m_reqTime + " m_serialNumber=" + next.m_serialNumber + " ";
            StackTraceElement[] stackTraceElementArr = next.m_stackTrace;
            if (stackTraceElementArr != null) {
                int i2 = 0;
                for (StackTraceElement stackTraceElement : stackTraceElementArr) {
                    String className = stackTraceElement.getClassName();
                    if (!className.equals("dalvik.system.VMStack") && !className.equals("java.lang.Thread") && !className.equals("dalvik.system.VMStack") && !className.equals("android.app.ActivityThread") && !className.equals("java.lang.reflect.Method") && !className.equals("com.android.internal.os.ZygoteInit") && !className.equals("com.android.internal.os.ZygoteInit$MethodAndArgsCaller:") && !className.equals("dalvik.system.NativeStart") && !className.equals("android.os.Looper")) {
                        str2 = str2 + "\n          " + i2 + ": " + className + "::" + stackTraceElement.getMethodName() + "(" + stackTraceElement.getFileName() + " " + stackTraceElement.getLineNumber() + ")";
                    }
                    i2++;
                }
            }
            str = str2;
        }
        return str;
    }

    private native int setVideoTrackUUID(int i, byte[] bArr);

    private native int startNativeBeatDetection(String str, int i, int i2);

    /* access modifiers changed from: private */
    public native int startPlay(int i);

    private void startPlay(int i, OnPlayListener onPlayListener) {
        int i2 = this.m_playRequestSerial + 1;
        this.m_playRequestSerial = i2;
        this.m_playRequestPending = true;
        this.m_didSetTimeSinceLastPlay = false;
        setTimeCancel(new d(this, i2, i, onPlayListener));
    }

    private native int stopPlay(int i);

    private native int sttStartInternal(STTParam sTTParam);

    private native int superResolutionStartInternal(VideoSuperResolutionParam videoSuperResolutionParam);

    private native int superResolutionStop();

    private native int swapBlendMain(int i);

    private int tagNextCommand() {
        int i = this.m_nextTag;
        this.m_nextTag = i + 1;
        commandMarker(i);
        return i;
    }

    private native int text2SpeechStart(Text2SpeechParam text2SpeechParam);

    private ErrorCode transcodingStart(String str, File file, int i, int i2, int i3, int i4, int i5, long j, int i6, int i7, int i8, int i9) {
        this.mExportPath = getExportContentResolverPath(file);
        Log.d(LOG_TAG, "transcodingStart_internal() : Destination Path" + this.mExportPath);
        return transcodingStart(str, this.mExportPath, i, i2, i3, i4, i3, i4, i5, j, i6, i7, i8, 0, i9);
    }

    private native int transcodingStartInternal(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j, int i8, int i9, int i10, int i11, int i12);

    private native int transcodingStop();

    /* access modifiers changed from: private */
    public static String unescape(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if (z) {
                if (charAt == 't') {
                    sb.append("\t");
                } else if (charAt == '\\') {
                    sb.append('\\');
                }
                z = false;
            } else if (charAt == '\\') {
                z = true;
            } else {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    private native int updateRenderInfo(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13, int i14);

    private native int videoStabilizerStartInternal(VideoStabilizerParam videoStabilizerParam);

    private native int videoStabilizerStop();

    /* access modifiers changed from: private */
    public ResultTask<Rect> waitForCaptureDimensionsChanged() {
        if (this.captureChangeTask == null) {
            this.captureChangeTask = new ResultTask<>();
        }
        return this.captureChangeTask;
    }

    public ErrorCode actionRecognitionProcessStart(ActionRecognitionParam actionRecognitionParam, OnActionRecognitionListener onActionRecognitionListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(actionRecognitionStartInternal(actionRecognitionParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.ACTIONRECOGNITION;
            this.m_ActionRecognitionListener = onActionRecognitionListener;
        }
        return fromValue;
    }

    public int actionRecognitionProcessStop() {
        return actionRecognitionStop();
    }

    public native int actionRecognitionStop();

    public int addAudioClip(int i, String str, OnAddClipDoneListener onAddClipDoneListener) {
        int i2 = this.m_iNextClipID;
        this.m_iNextClipID = i2 + 1;
        onAddClipDoneListener.clipId = i2;
        int addAudioClip = addAudioClip(i, str, i2);
        if (addAudioClip == 0) {
            this.m_addAudioClipDoneListeners.add(onAddClipDoneListener);
        }
        return addAudioClip;
    }

    public native int addUDTA(int i, String str);

    public int addVisualClip(int i, String str, OnAddClipDoneListener onAddClipDoneListener) {
        int i2 = this.m_iNextClipID;
        this.m_iNextClipID = i2 + 1;
        onAddClipDoneListener.clipId = i2;
        int addVisualClip = addVisualClip(i, str, i2);
        if (addVisualClip == 0) {
            this.m_addVisualClipDoneListeners.add(onAddClipDoneListener);
        }
        return addVisualClip;
    }

    public ErrorCode aiBoxTrackingProcessStart(AIBoxTrackingParam aIBoxTrackingParam, OnAIBoxTrackingListener onAIBoxTrackingListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(aiBoxTrackingStart(aIBoxTrackingParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.AIBOXTRACKING;
            this.m_AIBoxTrackingListener = onAIBoxTrackingListener;
        }
        return fromValue;
    }

    public int aiBoxTrackingProcessStop() {
        return aiBoxTrackingStop();
    }

    public ErrorCode aiVocalSeparatorProcessStart(AIVocalSeparatorParam aIVocalSeparatorParam, OnAIVocalSeparatorListener onAIVocalSeparatorListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(aiVocalSeparatorStart(aIVocalSeparatorParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.AIVOCAL_SEPARATOR;
            this.m_AIVocalSeparatorListener = onAIVocalSeparatorListener;
        }
        return fromValue;
    }

    public int aiVocalSeparatorProcessStop() {
        return aiVocalSeparatorStop();
    }

    public ErrorCode aiVoiceIVCClProcessStart(AIVoiceIVCClParam aIVoiceIVCClParam, OnAIVoiceListener onAIVoiceListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(aiVoiceIVCClStart(aIVoiceIVCClParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.AIVOICE_IVC_CL;
            this.m_AIVoiceListener = onAIVoiceListener;
        }
        return fromValue;
    }

    public ErrorCode aiVoiceIVCEmProcessStart(AIVoiceIVCEmParam aIVoiceIVCEmParam, OnAIVoiceListener onAIVoiceListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(aiVoiceIVCEmStart(aIVoiceIVCEmParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.AIVOICE_IVC_EM;
            this.m_AIVoiceListener = onAIVoiceListener;
        }
        return fromValue;
    }

    public int aiVoiceProcessStop() {
        return aiVoiceStop();
    }

    public ErrorCode aiVoiceRVCClProcessStart(AIVoiceRVCClParam aIVoiceRVCClParam, OnAIVoiceListener onAIVoiceListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(aiVoiceRVCClStart(aIVoiceRVCClParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.AIVOICE_RVC_CL;
            this.m_AIVoiceListener = onAIVoiceListener;
        }
        return fromValue;
    }

    public ErrorCode aiVoiceRVCEmProcessStart(AIVoiceRVCEmParam aIVoiceRVCEmParam, OnAIVoiceListener onAIVoiceListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(aiVoiceRVCEmStart(aIVoiceRVCEmParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.AIVOICE_RVC_EM;
            this.m_AIVoiceListener = onAIVoiceListener;
        }
        return fromValue;
    }

    public native int asyncDrawInfoList(NexDrawInfo[] nexDrawInfoArr, NexDrawInfo[] nexDrawInfoArr2);

    public int autoTrim(String str, int i, int i2, int i3, int i4) {
        return highlightStart(str, i, i2, i3, 0, "dummy", 0, 0, 0, Long.MAX_VALUE, i4);
    }

    public int autoTrimStop() {
        return highlightStop();
    }

    public int beginBlend(int i) {
        return swapBlendMain(i);
    }

    public FastPreviewBuilder buildFastPreview() {
        return new n(this);
    }

    public boolean canUseSoftwareCodec() {
        String property = getProperty("canUseSoftwareCodec");
        if (property == null) {
            return false;
        }
        if (property.equals(InneractiveMediationDefs.SHOW_HOUSE_AD_YES)) {
            return true;
        }
        if (property.equals("false")) {
            return false;
        }
        throw new InternalError();
    }

    public ErrorCode cancelBeatDetection(int i) {
        return ErrorCode.fromValue(cancelNativeBeatDetection(i));
    }

    public ErrorCode capture(int i, boolean z, OnCaptureListener onCaptureListener) {
        int i2;
        if (i == 2147418113) {
            i = this.m_currentTime;
        }
        int i3 = i;
        if (this.mCaptureInProgress) {
            ErrorCode errorCode = ErrorCode.INVALID_STATE;
            onCaptureListener.onCaptureFail(errorCode, -1);
            return errorCode;
        }
        this.mCaptureInProgress = true;
        if (!this.mCaptureCompleting) {
            if (this.m_isPendingSeek || this.m_isSeeking) {
                i2 = this.m_pendingSeekLocation;
            } else {
                i2 = this.m_currentTime;
            }
            this.mCaptureOriginalTime = i2;
        }
        r rVar = new r(this, onCaptureListener);
        if (i3 == 2147418114) {
            fastPreviewForCapture(FastPreviewOption.nofx, 1, false);
            captureCurrentFrameInternal(z, rVar);
        } else {
            seek(i3, false, new s(this, i3, onCaptureListener, z, rVar));
        }
        return ErrorCode.NONE;
    }

    public ErrorCode captureCurrentFrame(boolean z, OnCaptureListener onCaptureListener) {
        return capture(CAPTURE_CURRENT, z, onCaptureListener);
    }

    public ResultTask<Bitmap> captureFrame(SurfaceView surfaceView, int i, int i2, boolean z) {
        ResultTask<Bitmap> resultTask = new ResultTask<>();
        this.mCaptureFrameInProgress = true;
        clearCachedVisualTrack(0);
        surfaceView.getHolder().addCallback(this.captureSurfaceCallback);
        SurfaceView surfaceView2 = surfaceView;
        waitForCaptureDimensionsChanged().setTimeout(500).onResultAvailable(new q36(this, i, i2, surfaceView2, z, resultTask)).onFailure((Task.OnFailListener) new o36(this, surfaceView2, resultTask));
        surfaceView2.getHolder().setFixedSize(16, 16);
        return resultTask;
    }

    public ErrorCode captureSelectedClip(int i, boolean z, int i2, OnCaptureListener onCaptureListener) {
        if (onCaptureListener == null) {
            Log.e(LOG_TAG, "captureSelectedClip : OnCaptureListener is null");
            return ErrorCode.ARGUMENT_FAILED;
        } else if (captureSelectedClip(i, z, i2) != 0) {
            return ErrorCode.GENERAL;
        } else {
            this.m_onCaptureListeners.add(onCaptureListener);
            return ErrorCode.NONE;
        }
    }

    public native int changeSurface(int i, int i2);

    public native int checkDirectExport(int i);

    public native int checkIDREnd();

    public native int checkIDRStart(String str);

    public native int checkIDRTime(int i);

    public native int checkPFrameDirectExportSync(String str);

    public int cleanupMaskWithWhiteJ(int i) {
        return cleanupMaskWithWhite(i);
    }

    public void clearPendingEvents(OnCompletionListener onCompletionListener) {
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener == null) {
            Log.w(LOG_TAG, "clearPendingEvents : Null event handler");
            return;
        }
        nexEditorEventListener.ignoreEventsUntilTag(this.m_nextTag);
        this.m_onPlayListeners.clear();
        this.m_onStopListeners.clear();
        this.m_addAudioClipDoneListeners.clear();
        this.m_addVisualClipDoneListeners.clear();
        this.m_onCaptureListeners.clear();
        this.m_deleteClipDoneListeners.clear();
        this.m_loadListAsyncTasks.clear();
        this.m_moveClipDoneListeners.clear();
        this.m_setTimeDoneListeners.clear();
        this.m_onSetTimeCancelListeners.clear();
        setIsSeekingFlag(false);
        this.m_isPendingSeek = false;
        this.m_isPendingNonDisplaySeek = false;
        this.mCaptureInProgress = false;
        finishAllCommands(onCompletionListener);
    }

    public void clearPendingFastPreview() {
        this.mPendingFastPreview = null;
    }

    public native int clearProject();

    public void clearRenderItems(boolean z) {
        clearRenderItems(z ^ true ? 1 : 0);
    }

    public int clearScreen() {
        return clearScreen(1);
    }

    public int clearTrackCache() {
        return clearCachedVisualTrack(2);
    }

    public native int clearUDTA();

    public native int closeProject();

    public boolean compressMediaContentsInKMProject(String str, String str2, long j) {
        return compressVideoContentsInKMProject(str, str2, j, 0);
    }

    public native int[] createCubeLUT(byte[] bArr);

    public native int[] createLGLUT(byte[] bArr);

    public int createNexEDLJ(String str, int i) {
        return createNexEDL(str, i);
    }

    public native int createProject();

    public int createRenderItemJ(String str, int i) {
        return createRenderItem(str, i);
    }

    public int[] decodeNV12(byte[] bArr, int i, int i2) {
        int i3 = i;
        int i4 = i2;
        int i5 = i3 * i4;
        int[] iArr = new int[i5];
        int i6 = 0;
        for (int i7 = 0; i7 < i4; i7++) {
            int i8 = ((i7 >> 1) * i3) + i5;
            int i9 = 0;
            int i10 = 0;
            int i11 = 0;
            while (i9 < i3) {
                int i12 = (bArr[i6] & AdConfig.NETWORK_LOAD_LIMIT_DISABLED) - 16;
                if (i12 < 0) {
                    i12 = 0;
                }
                if ((i9 & 1) == 0) {
                    int i13 = i8 + 1;
                    i8 += 2;
                    int i14 = (bArr[i8] & AdConfig.NETWORK_LOAD_LIMIT_DISABLED) - 128;
                    i11 = (bArr[i13] & AdConfig.NETWORK_LOAD_LIMIT_DISABLED) - 128;
                    i10 = i14;
                }
                int i15 = i12 * 1192;
                int i16 = (i11 * 1634) + i15;
                int i17 = (i15 - (i11 * 833)) - (i10 * 400);
                int i18 = (i10 * 2066) + i15;
                if (i16 < 0) {
                    i16 = 0;
                } else if (i16 > 262143) {
                    i16 = 262143;
                }
                if (i17 < 0) {
                    i17 = 0;
                } else if (i17 > 262143) {
                    i17 = 262143;
                }
                if (i18 < 0) {
                    i18 = 0;
                } else if (i18 > 262143) {
                    i18 = 262143;
                }
                iArr[i6] = ((i16 << 6) & 16711680) | -16777216 | ((i17 >> 2) & 65280) | ((i18 >> 10) & 255);
                i9++;
                i6++;
            }
        }
        return iArr;
    }

    public int deleteClipID(int i, OnDeleteClipDoneListener onDeleteClipDoneListener) {
        if (onDeleteClipDoneListener == null) {
            onDeleteClipDoneListener = new OnDeleteClipDoneListener();
        }
        int deleteClipID = deleteClipID(i);
        if (deleteClipID == 0) {
            this.m_deleteClipDoneListeners.add(onDeleteClipDoneListener);
        }
        return deleteClipID;
    }

    public void destroy() {
        onDestroy();
        destroyEditor();
        this.m_listener.destroyRenderer();
        this.m_listener = null;
        NexThemeView nexThemeView = this.mView;
        if (nexThemeView != null) {
            nexThemeView.linkToEditor((NexEditor) null);
            this.mView = null;
        }
    }

    public Task detectAndSetEditorColorFormat(Context context) {
        if (this.mColorFormatTask == null) {
            this.mColorFormatTask = new Task();
            this.mDeviceProfile.getClass();
            this.mColorFormatTask.signalEvent(Task.Event.COMPLETE);
        }
        return this.mColorFormatTask;
    }

    public int directExport_internal(Uri uri, long j, long j2, String str, int i) {
        return directExport(uri.toString(), j, j2, str, i);
    }

    public int drawNexEDLOverlayJ(int i, int i2, String str, int i3, int i4, int i5, int i6, float[] fArr, float f, float f2, float f3, float f4, float f5, int i7) {
        return drawNexEDLOverlay(i, i2, str, i3, i4, i5, i6, fArr, f, f2, f3, f4, f5, i7);
    }

    public int drawRenderItemOverlayJ(int i, int i2, String str, int i3, int i4, int i5, int i6, float[] fArr, int i7, int i8, float f, float f2, float f3, float f4, float f5, boolean z) {
        return drawRenderItemOverlay(i, i2, str, i3, i4, i5, i6, fArr, i7, i8, f, f2, f3, f4, f5, z);
    }

    public native int encodePause();

    public native int encodeResume();

    public int endBlend(int i) {
        return swapBlendMain(i);
    }

    public native int endVoiceRecorder(NexClipInfo nexClipInfo);

    public int entitlementClearEnrollment() {
        this.mEntitlementJavaError = 0;
        try {
            return entitlementClearEnrollmentN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 1;
        }
    }

    public int entitlementClearSubscription(int i) {
        if (!isSubKind(i)) {
            this.mEntitlementJavaError = 2;
            return 2;
        }
        this.mEntitlementJavaError = 0;
        try {
            return entitlementClearSubscriptionN(i);
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 1;
        }
    }

    public int entitlementDiscardPending() {
        this.mEntitlementJavaError = 0;
        try {
            return entitlementDiscardPendingN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 1;
        }
    }

    public String entitlementEnrollState() {
        this.mEntitlementJavaError = 0;
        try {
            return entitlementEnrollStateN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return null;
        }
    }

    public int entitlementInstallEnroll(byte[] bArr, byte[] bArr2, long j) {
        if (bArr == null || bArr.length == 0 || bArr2 == null || bArr2.length == 0) {
            this.mEntitlementJavaError = 2;
            return 2;
        }
        this.mEntitlementJavaError = 0;
        try {
            return entitlementInstallEnrollN(bArr, bArr2, j);
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 1;
        }
    }

    public int entitlementInstallSubscription(byte[] bArr, byte[] bArr2, long j, int i) {
        if (bArr == null || bArr.length == 0 || bArr2 == null || bArr2.length == 0 || !isSubKind(i)) {
            this.mEntitlementJavaError = 2;
            return 2;
        }
        this.mEntitlementJavaError = 0;
        try {
            return entitlementInstallSubscriptionN(bArr, bArr2, j, i);
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 1;
        }
    }

    public long entitlementLastAckExp() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return 0;
        }
        try {
            return entitlementLastAckExpN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 0;
        }
    }

    public long entitlementLastAckIat() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return 0;
        }
        try {
            return entitlementLastAckIatN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 0;
        }
    }

    public int entitlementLastError() {
        if (this.mEntitlementUnavailable) {
            return 1;
        }
        if (this.mEntitlementJavaError != 0) {
            return this.mEntitlementJavaError;
        }
        try {
            return entitlementLastErrorN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 1;
        }
    }

    public int entitlementLastErrorFailedKey() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return 0;
        }
        try {
            return entitlementLastErrorFailedKeyN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 0;
        }
    }

    public int entitlementLastErrorFallbackPlatformCode() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return -1;
        }
        try {
            return entitlementLastErrorFallbackPlatformCodeN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return -1;
        }
    }

    public int entitlementLastErrorFallbackRetryPolicy() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return -1;
        }
        try {
            return entitlementLastErrorFallbackRetryPolicyN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return -1;
        }
    }

    public int entitlementLastErrorPlatformCode() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return -1;
        }
        try {
            return entitlementLastErrorPlatformCodeN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return -1;
        }
    }

    public int entitlementLastErrorRetryPolicy() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return -1;
        }
        try {
            return entitlementLastErrorRetryPolicyN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return -1;
        }
    }

    public String entitlementLastKeystoreFailure() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return "";
        }
        try {
            String entitlementLastKeystoreFailureN = entitlementLastKeystoreFailureN();
            if (entitlementLastKeystoreFailureN == null) {
                return "";
            }
            return entitlementLastKeystoreFailureN;
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return "";
        }
    }

    public int entitlementLastProvisionFallback() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return 0;
        }
        try {
            return entitlementLastProvisionFallbackN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 0;
        }
    }

    public int entitlementLastProvisionStage() {
        if (this.mEntitlementUnavailable) {
            return -1;
        }
        try {
            return entitlementLastProvisionStageN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return -1;
        }
    }

    public int entitlementLastProvisionStallHint() {
        if (this.mEntitlementUnavailable || this.mEntitlementJavaError != 0) {
            return 0;
        }
        try {
            return entitlementLastProvisionStallHintN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 0;
        }
    }

    public String entitlementProvision(byte[] bArr, long j) {
        if (bArr == null || bArr.length == 0) {
            this.mEntitlementJavaError = 2;
            return null;
        }
        this.mEntitlementJavaError = 0;
        try {
            return entitlementProvisionN(bArr, j);
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return null;
        }
    }

    public byte[] entitlementSign(byte[] bArr) {
        if (bArr == null || bArr.length == 0 || bArr.length > 64) {
            this.mEntitlementJavaError = 2;
            return null;
        }
        this.mEntitlementJavaError = 0;
        try {
            return entitlementSignN(bArr);
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return null;
        }
    }

    public int entitlementStuckOrigin() {
        if (this.mEntitlementUnavailable) {
            return 0;
        }
        try {
            return entitlementStuckOriginN();
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return 0;
        }
    }

    public String entitlementSubscriptionState(long j, int i) {
        if (!isSubKind(i)) {
            this.mEntitlementJavaError = 2;
            return null;
        }
        this.mEntitlementJavaError = 0;
        try {
            return entitlementSubscriptionStateN(j, i);
        } catch (UnsatisfiedLinkError unused) {
            this.mEntitlementUnavailable = true;
            return null;
        }
    }

    public Task export(String str, String str2, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7, int i8, boolean z, int i9) {
        int i10;
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        this.m_exportProjectDuration = 0;
        Size size = new Size(i, i2);
        if (i4 <= 0) {
            i10 = 3000;
        } else {
            i10 = i4;
        }
        int encodeProjectInternal = encodeProjectInternal(str, str2, 0, 0, size, (Rect) null, i3, j, i10, 1920, 1080, i7, DEFAULT_EXPORT_AUDIO_BITRATE, i5, i6, i8, 0, z, i9);
        if (encodeProjectInternal != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectInternal));
            return task;
        }
        this.mExportPath = null;
        this.m_exportingTask = task;
        return task;
    }

    public Task exportAIdata(String str, int i, int i2, int i3, int i4, int i5, EncodeProjectAIDataParam encodeProjectAIDataParam) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        this.m_exportProjectDuration = i2;
        Size size = new Size(i3, i4);
        int i6 = i2;
        int i7 = i;
        String str2 = str;
        int encodeProjectToAIData = encodeProjectToAIData(str2, i7, i6, size, i5, encodeProjectAIDataParam);
        if (encodeProjectToAIData != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectToAIData));
            return task;
        }
        this.mExportPath = null;
        this.m_exportingTask = task;
        return task;
    }

    public Task exportGIF(File file, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float f, int i9, int i10, int i11, boolean z, boolean z2) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        int i12 = i2;
        this.m_exportProjectDuration = i12;
        this.mExportPath = getExportContentResolverPath(file);
        Log.d(LOG_TAG, "newPath=" + this.mExportPath);
        int encodeProjectToGIF = encodeProjectToGIF(this.mExportPath, i, i12, new Size(i3, i4), i5, i6, i7, i8, f, i9, i10, i11, z, z2);
        if (encodeProjectToGIF != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectToGIF));
            return task;
        }
        this.m_exportingTask = task;
        return task;
    }

    public ErrorCode exportJpeg(int i, int i2, int i3, OnCaptureListener onCaptureListener) {
        int i4;
        if (this.mCaptureInProgress) {
            return ErrorCode.INVALID_STATE;
        }
        this.mCaptureInProgress = true;
        if (!this.mCaptureCompleting) {
            if (this.m_isPendingSeek || this.m_isSeeking) {
                i4 = this.m_pendingSeekLocation;
            } else {
                i4 = this.m_currentTime;
            }
            this.mCaptureOriginalTime = i4;
        }
        f fVar = new f(this, onCaptureListener);
        if (this.nxcapture == null) {
            Log.d("NexEditor", "image Capture create ImageReader");
            this.nxcapture = ImageReader.newInstance(i, i2, 1, 2);
        }
        this.nxcapture.setOnImageAvailableListener(new g(this), (Handler) null);
        if (encodeProjectJpeg(this.nxcapture.getSurface(), " ", i, i2, 0, i3) == 0) {
            this.m_onCaptureListeners.add(fVar);
        }
        return ErrorCode.NONE;
    }

    public Task exportWEBP(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, boolean z) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        this.m_exportProjectDuration = i2;
        Size size = new Size(i3, i4);
        int i8 = i2;
        int i9 = i;
        String str2 = str;
        int encodeProjectToWEBP = encodeProjectToWEBP(str2, i9, i8, size, i5, i6, i7, z);
        if (encodeProjectToWEBP != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectToWEBP));
            return task;
        }
        this.mExportPath = null;
        this.m_exportingTask = task;
        return task;
    }

    public void fastPreview(Map<FastPreviewOption, String> map) {
        if (!this.mCaptureInProgress && this.transcodingType != VideoTranscodingType.REVERSE) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry next : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(((FastPreviewOption) next.getKey()).name());
                sb.append('=');
                sb.append((String) next.getValue());
            }
            fastOptionPreviewWrapper(sb.toString(), 1);
        }
    }

    public native int fastPreviewStart(int i, int i2, int i3, int i4);

    public native int fastPreviewStop();

    public native int fastPreviewTime(int i);

    public void finishAllCommands(OnCompletionListener onCompletionListener) {
        onCompletionListener.m_tag = this.m_nextTag;
        int commandMarker = commandMarker(this.m_nextTag);
        this.m_nextTag++;
        if (commandMarker == 0) {
            this.m_onCommandMarkerListeners.add(onCompletionListener);
        } else {
            onCompletionListener.onComplete(ErrorCode.fromValue(commandMarker));
        }
    }

    public int getAspectMode() {
        return this.mAspectMode;
    }

    public native NexAudioClip getAudioClip(int i);

    public int getAudioSessionID(boolean z) {
        return getAudioSessionID();
    }

    public boolean getBeatDetectionDoneResult(NexAudioFeatureResultInfo nexAudioFeatureResultInfo, int i) {
        if (getNativeBeatDetectionDoneResult(nexAudioFeatureResultInfo, i) == 0) {
            return true;
        }
        return false;
    }

    public native int getBrightness();

    public Bitmap getCacheThumbData(int i) {
        byte[] timeThumbData = getTimeThumbData(i);
        if (timeThumbData == null || timeThumbData.length == 0) {
            return null;
        }
        return Bitmap.createBitmap(decodeNV12(timeThumbData, 320, 240), 320, 240, Bitmap.Config.ARGB_8888);
    }

    public ErrorCode getClipInfoSync(String str, NexClipInfo nexClipInfo, boolean z, int i) {
        return ErrorCode.fromValue(getClipInfoSync(str, nexClipInfo, z ? 1 : 0, i));
    }

    public ErrorCode getClipInfoSyncFlags(String str, NexClipInfo nexClipInfo, int i, int i2) {
        return ErrorCode.fromValue(getClipInfoSync(str, nexClipInfo, i, i2));
    }

    public ErrorCode getClipInfoSyncStop(String str) {
        return ErrorCode.fromValue(getClipInfoSyncInternalStop(str));
    }

    public NexVisualClip[] getClipList() {
        ArrayList arrayList = new ArrayList();
        getClipList(new yi3(9));
        return (NexVisualClip[]) arrayList.toArray(new NexVisualClip[arrayList.size()]);
    }

    public ColorFormatChecker$ColorFormat getColorFormatTaskResult() {
        return this.mColorFormatTaskResult;
    }

    public int getConetentResolverTimeout() {
        this.mDeviceProfile.getClass();
        return 3000;
    }

    public native int getContrast();

    public String getCurrentThemeId() {
        return this.m_currentThemeId;
    }

    public LayerRenderCallback getCustomRenderCallback() {
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener != null) {
            return nexEditorEventListener.getCustomRenderCallback();
        }
        return null;
    }

    public native int getDuration();

    public HashMap<String, Integer> getEngineVersion() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        EngineVersion engineVersion = EngineVersion.MAJOR;
        int versionNumber = getVersionNumber(engineVersion.versionType);
        EngineVersion engineVersion2 = EngineVersion.MINOR;
        int versionNumber2 = getVersionNumber(engineVersion2.versionType);
        EngineVersion engineVersion3 = EngineVersion.PATCH;
        int versionNumber3 = getVersionNumber(engineVersion3.versionType);
        EngineVersion engineVersion4 = EngineVersion.BUILD;
        int versionNumber4 = getVersionNumber(engineVersion4.versionType);
        hashMap.put(engineVersion.name(), Integer.valueOf(versionNumber));
        hashMap.put(engineVersion2.name(), Integer.valueOf(versionNumber2));
        hashMap.put(engineVersion3.name(), Integer.valueOf(versionNumber3));
        hashMap.put(engineVersion4.name(), Integer.valueOf(versionNumber4));
        return hashMap;
    }

    public NexEditorListener getEventHandler() {
        return this.m_uiListener;
    }

    public String getExportContentResolverPath(File file) {
        try {
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Size getExportResolution() {
        return this.m_ExportResolution;
    }

    public native int getGain();

    public native int getGamma();

    public native int getHighlights();

    public native int getHue();

    public int getJPEGMaxHeight() {
        return this.mJPEGMaxHeight;
    }

    public int getJPEGMaxSize() {
        return this.mJPEGMaxSize;
    }

    public int getJPEGMaxWidth() {
        return this.mJPEGMaxWidth;
    }

    public ArrayList<KedlMediaInfo> getKedlMediaInfo(String str) {
        ArrayList<KedlMediaInfo> kedlMediaInfoList = getKedlMediaInfoList(str);
        if (v7c.b) {
            for (int i = 0; i < kedlMediaInfoList.size(); i++) {
                Log.i(LOG_TAG, "getKedlMediaInfo index:" + i);
                Log.i(LOG_TAG, "startTime:" + kedlMediaInfoList.get(i).startTime);
                Log.i(LOG_TAG, "endTime:" + kedlMediaInfoList.get(i).endTime);
                Log.i(LOG_TAG, "width:" + kedlMediaInfoList.get(i).width);
                Log.i(LOG_TAG, "height:" + kedlMediaInfoList.get(i).height);
                Log.i(LOG_TAG, "fps:" + kedlMediaInfoList.get(i).fps);
                Log.i(LOG_TAG, "speed:" + kedlMediaInfoList.get(i).speed);
                Log.i(LOG_TAG, "userfield:" + kedlMediaInfoList.get(i).userfield);
            }
        }
        return kedlMediaInfoList;
    }

    public native int getLift();

    public long getNativeHandle() {
        return this.nativeHandle;
    }

    public native String getProperty(String str);

    public boolean getPropertyBool(String str, boolean z) {
        String property = getProperty(str);
        if (property != null) {
            if (property.trim().equalsIgnoreCase(InneractiveMediationDefs.SHOW_HOUSE_AD_YES)) {
                return true;
            }
            if (property.trim().equalsIgnoreCase("false")) {
                return false;
            }
        }
        return z;
    }

    public int getPropertyInt(String str, int i) {
        String property = getProperty(str);
        if (property == null) {
            return i;
        }
        try {
            return Integer.parseInt(property);
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    public int getSDKInfo_WM() {
        return getNativeSDKInfoWM();
    }

    public native int getSaturation();

    public int getScreenMode() {
        return this.mScreenMode;
    }

    public native int getShadows();

    public native int getSharpness();

    public native int getTemperature();

    public int getTexNameForBlendJ(int i) {
        return getTexNameForBlend(i);
    }

    public int getTexNameForMaskJ(int i) {
        return getTexNameForMask(i);
    }

    public int getTexNameForVideoLayer(int i, int i2) {
        return getTexNameForClipID(i, i2);
    }

    public int getTexNameForVideoLayerExternal(int i, int i2, float[] fArr) {
        return getTexNameForClipIDExternal(i, i2, fArr);
    }

    public int getTexNameForWhiteJ(int i) {
        return getTexNameForWhite(i);
    }

    public ErrorCode getThumbStop(int i) {
        return ErrorCode.fromValue(getClipStopThumb(i));
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [java.lang.Object, com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditor$ThemeItemCallback] */
    public Effect[] getTitleEffectList() {
        if (!this.m_useThemeManager) {
            if (this.m_titleEffectList == null) {
                ArrayList arrayList = new ArrayList();
                getTitleEffectList(new Object());
                this.m_titleEffectList = (Effect[]) arrayList.toArray(new Effect[arrayList.size()]);
            }
            return this.m_titleEffectList;
        }
        y82.f("Not supported when using theme manager");
        return null;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [java.lang.Object, com.kinemaster.module.nexeditormodule.nexvideoeditor.NexEditor$ThemeItemCallback] */
    public Effect[] getTransitionEffectList() {
        if (!this.m_useThemeManager) {
            if (this.m_transitionEffectList == null) {
                ArrayList arrayList = new ArrayList();
                getClipEffectList(new Object());
                this.m_transitionEffectList = (Effect[]) arrayList.toArray(new Effect[arrayList.size()]);
            }
            return this.m_transitionEffectList;
        }
        y82.f("Not supported when using theme manager");
        return null;
    }

    public boolean getUseAbsolutePath() {
        return this.mDeviceProfile.g();
    }

    public native int getVersionNumber(int i);

    public native int getVibrance();

    public NexThemeView getView() {
        return this.mView;
    }

    public native int getVignette();

    public native int getVignetteRange();

    public native NexVisualClip getVisualClip(int i);

    public ErrorCode imageLabelingProcessStart(ImageLabelingParam imageLabelingParam, OnImageLabelingListener onImageLabelingListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(imageLabelingStart(imageLabelingParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.IMAGE_LABELING;
            this.m_ImageLabelingListener = onImageLabelingListener;
        }
        return fromValue;
    }

    public int imageLabelingProcessStop() {
        return imageLabelingStop();
    }

    public boolean isAddingVisualClip() {
        return !this.m_addVisualClipDoneListeners.isEmpty();
    }

    public boolean isCapture() {
        return this.mCaptureInProgress;
    }

    public boolean isPlayPending() {
        if (this.m_playRequested || this.m_playRequestPending) {
            return true;
        }
        return false;
    }

    public boolean isSeeking() {
        return this.m_isSeeking;
    }

    public boolean isTranscoding() {
        if (this.transcodingType != VideoTranscodingType.NONE) {
            return true;
        }
        return false;
    }

    public ErrorCode jumpCutProcessStart(JumpCutParam jumpCutParam, OnJumpCutListener onJumpCutListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(jumpCutStart(jumpCutParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.JUMP_CUT;
            this.m_JumpCutListener = onJumpCutListener;
        }
        return fromValue;
    }

    public int jumpCutProcessStop() {
        return jumpCutStop();
    }

    public native int jumpCutStop();

    public int loadClips(NexVisualClip[] nexVisualClipArr, NexAudioClip[] nexAudioClipArr, int i) {
        int i2;
        int i3;
        int i4;
        if (nexVisualClipArr != null) {
            i2 = 0;
            for (NexVisualClip nexVisualClip : nexVisualClipArr) {
                if (nexVisualClip != null) {
                    if (nexVisualClip.mClipSpeed == 1.0f && (i4 = nexVisualClip.mSpeedControl) != 100) {
                        nexVisualClip.mClipSpeed = ((float) i4) / 100.0f;
                    }
                    int i5 = nexVisualClip.mClipID;
                    if (i5 > i2) {
                        i2 = i5;
                    }
                }
            }
        } else {
            i2 = 0;
        }
        if (nexAudioClipArr != null) {
            for (NexAudioClip nexAudioClip : nexAudioClipArr) {
                if (nexAudioClip != null) {
                    if (nexAudioClip.mClipSpeed == 1.0f && (i3 = nexAudioClip.mSpeedControl) != 100) {
                        nexAudioClip.mClipSpeed = ((float) i3) / 100.0f;
                    }
                    int i6 = nexAudioClip.mClipID;
                    if (i6 > i2) {
                        i2 = i6;
                    }
                }
            }
        }
        this.m_iNextClipID = i2 + 1;
        return loadList(nexVisualClipArr, nexAudioClipArr, i);
    }

    public Task loadClipsAsync(NexVisualClip[] nexVisualClipArr, NexAudioClip[] nexAudioClipArr, int i) {
        int i2;
        int i3;
        int i4;
        Task task = new Task();
        if (nexVisualClipArr != null) {
            i2 = 0;
            for (NexVisualClip nexVisualClip : nexVisualClipArr) {
                if (nexVisualClip != null) {
                    if (nexVisualClip.mClipSpeed == 1.0f && (i4 = nexVisualClip.mSpeedControl) != 100) {
                        nexVisualClip.mClipSpeed = ((float) i4) / 100.0f;
                    }
                    int i5 = nexVisualClip.mClipID;
                    if (i5 > i2) {
                        i2 = i5;
                    }
                    if (nexVisualClip.mIsAlphaOn == 1) {
                        Log.d(LOG_TAG, "loadClipsAsync : mIsAlphaOn = " + nexVisualClip.mIsAlphaOn);
                    }
                }
            }
        } else {
            i2 = 0;
        }
        if (nexAudioClipArr != null) {
            for (NexAudioClip nexAudioClip : nexAudioClipArr) {
                if (nexAudioClip != null) {
                    if (nexAudioClip.mClipSpeed == 1.0f && (i3 = nexAudioClip.mSpeedControl) != 100) {
                        nexAudioClip.mClipSpeed = ((float) i3) / 100.0f;
                    }
                    int i6 = nexAudioClip.mClipID;
                    if (i6 > i2) {
                        i2 = i6;
                    }
                }
            }
        }
        this.m_iNextClipID = i2 + 1;
        this.m_loadListAsyncTasks.add(task);
        int asyncLoadList = asyncLoadList(nexVisualClipArr, nexAudioClipArr, i);
        if (asyncLoadList != 0) {
            this.m_loadListAsyncTasks.remove(task);
            task.sendFailure(ErrorCode.fromValue(asyncLoadList));
        }
        return task;
    }

    public ErrorCode makeAudioPCM(File file, File file2, File file3, int i, int i2, int i3) {
        String str;
        String str2 = null;
        if (file2 != null) {
            str = file2.getAbsolutePath();
        } else {
            str = null;
        }
        if (file3 != null) {
            str2 = file3.getAbsolutePath();
        }
        return ErrorCode.fromValue(getClipAudioThumb(file.getAbsolutePath(), str, str2, i, i2, i3));
    }

    public ErrorCode makeAudioPCMPartial(File file, File file2, File file3, int i, int i2, int i3, int i4, int i5, int i6) {
        String str;
        String str2 = null;
        if (file2 != null) {
            str = file2.getAbsolutePath();
        } else {
            str = null;
        }
        if (file3 != null) {
            str2 = file3.getAbsolutePath();
        }
        return ErrorCode.fromValue(getClipAudioThumbPartial(file.getAbsolutePath(), str, str2, i, i2, i3, i4, i5, i6));
    }

    public int[] makeCubeLUT(byte[] bArr, int i, int i2) {
        return createCubeLUT(bArr);
    }

    public int[] makeLGLUT(byte[] bArr, int i, int i2) {
        return createLGLUT(bArr);
    }

    public ErrorCode makeVideoThumbs(File file, File file2, int i, int i2, int i3, int i4, int i5, int i6, int i7) throws IOException {
        return ErrorCode.fromValue(getClipVideoThumb(file.getAbsolutePath(), file2.getAbsolutePath(), i, i2, i3, i4, i5, i6, i7));
    }

    public boolean modifyAudioClip(NexAudioClip nexAudioClip) {
        if (modifyAudioClipN(nexAudioClip) == 0) {
            return true;
        }
        return false;
    }

    public boolean modifyVisualClip(NexVisualClip nexVisualClip) {
        if (modifyVisualClipN(nexVisualClip) == 0) {
            return true;
        }
        return false;
    }

    public int moveAudioClip(int i, int i2, OnMoveClipDoneListener onMoveClipDoneListener) {
        int moveAudioClip = moveAudioClip(i, i2);
        if (moveAudioClip == 0) {
            this.m_moveClipDoneListeners.add(onMoveClipDoneListener);
            this.m_movedClipId.add(Integer.valueOf(i2));
        }
        return moveAudioClip;
    }

    public int moveVisualClip(int i, int i2, OnMoveClipDoneListener onMoveClipDoneListener) {
        int moveVisualClip = moveVisualClip(i, i2);
        if (moveVisualClip == 0) {
            this.m_moveClipDoneListeners.add(onMoveClipDoneListener);
            this.m_movedClipId.add(Integer.valueOf(i2));
        }
        return moveVisualClip;
    }

    public void notifySurfaceChanged() {
        OnSurfaceChangeListener onSurfaceChangeListener = this.m_onSurfaceChangeListener;
        if (onSurfaceChangeListener != null) {
            onSurfaceChangeListener.onSurfaceChanged();
        }
    }

    public void onAIVoiceRVCInfo(int i) {
        OnAIVoiceListener onAIVoiceListener = this.m_AIVoiceListener;
        if (onAIVoiceListener != null && this.transcodingType == VideoTranscodingType.AIVOICE_RVC_EM) {
            onAIVoiceListener.onAIVoiceRVCInfo(i);
        }
    }

    public void onActionRecognitionInfo(int i, int i2, int i3, int i4) {
        OnActionRecognitionListener onActionRecognitionListener = this.m_ActionRecognitionListener;
        if (onActionRecognitionListener != null) {
            onActionRecognitionListener.onActionRecognitionInfo(i, i2, i3, i4);
        }
    }

    public void onAddClipDone(int i, int i2, int i3, int i4) {
        Deque<OnAddClipDoneListener> deque;
        if (i4 == 1 || i4 == 4 || i4 == 0) {
            Deque<OnAddClipDoneListener> deque2 = this.m_addVisualClipDoneListeners;
            if (deque2 != null && deque2.size() >= 1) {
                for (OnAddClipDoneListener next : this.m_addVisualClipDoneListeners) {
                    if (next.clipId == i3) {
                        this.m_addVisualClipDoneListeners.remove(next);
                        if (i == 0) {
                            next.onAddClipDone(i3, i2, i4);
                            return;
                        } else {
                            next.onAddClipFail(ErrorCode.fromValue(i));
                            return;
                        }
                    }
                }
            } else {
                return;
            }
        }
        if ((i4 == 3 || i4 == 0) && (deque = this.m_addAudioClipDoneListeners) != null && deque.size() >= 1) {
            for (OnAddClipDoneListener next2 : this.m_addAudioClipDoneListeners) {
                if (next2.clipId == i3) {
                    this.m_addAudioClipDoneListeners.remove(next2);
                    if (i == 0) {
                        next2.onAddClipDone(i3, i2, i4);
                        return;
                    } else {
                        next2.onAddClipFail(ErrorCode.fromValue(i));
                        return;
                    }
                }
            }
        }
    }

    public void onAiFeatureMode(int i, int i2) {
        OnTranscodingListener onTranscodingListener = this.m_transcodingListener;
        if (onTranscodingListener != null) {
            onTranscodingListener.onAiFeatureMode(i, i2);
        }
    }

    public void onAsyncLoadListDone(int i) {
        if (this.m_loadListAsyncTasks.size() > 0) {
            Task remove = this.m_loadListAsyncTasks.remove();
            if (i == 0) {
                remove.signalEvent(Task.Event.COMPLETE, Task.Event.SUCCESS);
            } else {
                remove.sendFailure(ErrorCode.fromValue(i));
            }
        }
    }

    public void onBeatDetectionDone(ErrorCode errorCode, int i) {
        OnAudioFeatureListener onAudioFeatureListener2 = this.onAudioFeatureListener;
        if (onAudioFeatureListener2 != null) {
            onAudioFeatureListener2.onBeatDetectionDone(errorCode, i);
        }
    }

    public void onBeatDetectionProgress(int i, int i2, int i3) {
        OnAudioFeatureListener onAudioFeatureListener2 = this.onAudioFeatureListener;
        if (onAudioFeatureListener2 != null) {
            onAudioFeatureListener2.onProgress(i, i2, i3);
        }
    }

    public void onCaptureDone(int i, int i2, int i3, byte[] bArr, boolean z, int i4) {
        if (this.m_onCaptureListeners.size() >= 1) {
            if (i < 1 || i2 < 1 || i3 < 1 || bArr == null) {
                onCaptureFail(ErrorCode.CAPTURE_FAILED, i4);
                return;
            }
            try {
                OnCaptureListener remove = this.m_onCaptureListeners.remove();
                if (z) {
                    int i5 = i * 4;
                    try {
                        byte[] bArr2 = new byte[i5];
                        for (int i6 = 0; i6 < i2 / 2; i6++) {
                            int i7 = i * i6 * 4;
                            System.arraycopy(bArr, i7, bArr2, 0, i5);
                            int i8 = ((i2 - 1) - i6) * i * 4;
                            System.arraycopy(bArr, i8, bArr, i7, i5);
                            System.arraycopy(bArr2, 0, bArr, i8, i5);
                        }
                    } catch (OutOfMemoryError e) {
                        Log.e(LOG_TAG, qq2.l(i, "onCaptureDone: out of memory building ", "x", i2, " bitmap"), e);
                        remove.onCaptureFail(ErrorCode.CAPTURE_FAILED, i4);
                        return;
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        remove.onCaptureFail(ErrorCode.CAPTURE_FAILED, i4);
                        return;
                    }
                }
                Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
                createBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bArr));
                try {
                    remove.onCapture(createBitmap, i4);
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            } catch (NoSuchElementException e4) {
                e4.printStackTrace();
            }
        }
    }

    public void onCaptureFail(ErrorCode errorCode, int i) {
        if (this.m_onCaptureListeners.size() >= 1) {
            this.m_onCaptureListeners.remove().onCaptureFail(errorCode, i);
        }
    }

    public void onCommandMarker(ErrorCode errorCode, int i) {
        this.m_reachedTag = i;
        while (true) {
            OnCompletionListener peek = this.m_onCommandMarkerListeners.peek();
            if (peek != null && peek.m_tag <= i) {
                this.m_onCommandMarkerListeners.remove().onComplete(errorCode);
            } else {
                return;
            }
        }
    }

    public void onDeleteClipDone(int i) {
        OnDeleteClipDoneListener remove = this.m_deleteClipDoneListeners.remove();
        if (i == 0) {
            remove.onDeleteClipDone();
        } else {
            remove.onDeleteClipFail(ErrorCode.fromValue(i));
        }
    }

    public void onDestroy() {
        OnEditorDestroyedListener onEditorDestroyedListener = this.m_onEditorDestroyedListener;
        if (onEditorDestroyedListener != null) {
            onEditorDestroyedListener.onEditorDestroyed();
        }
    }

    public void onDiagnosticEvent(int i, int i2, int i3, int i4) {
        OnDiagnosticEventListener onDiagnosticEventListener2 = this.onDiagnosticEventListener;
        if (onDiagnosticEventListener2 != null) {
            onDiagnosticEventListener2.onDiagnosticEvent(i, i2, i3, i4);
        }
    }

    public void onEncodingDone(ErrorCode errorCode, int i) {
        if (this.m_exportingTask != null) {
            boolean isError = errorCode.isError();
            Task task = this.m_exportingTask;
            if (isError) {
                task.sendFailure(errorCode);
            } else {
                task.signalEvent(Task.Event.SUCCESS, Task.Event.COMPLETE);
            }
            this.m_exportingTask = null;
        }
    }

    public boolean onExportDone() {
        if (this.m_onExportListeners.size() < 1) {
            return false;
        }
        this.m_onExportListeners.remove().onExportDone();
        this.m_exportingTask = null;
        return true;
    }

    public boolean onExportFail(ErrorCode errorCode) {
        if (this.m_onExportListeners.size() < 1) {
            return false;
        }
        this.m_onExportListeners.remove().onExportFail(errorCode);
        this.m_exportingTask = null;
        return true;
    }

    public boolean onExportProgress(int i) {
        if (this.m_onExportListeners.size() < 1) {
            return false;
        }
        this.m_onExportListeners.getFirst().onExportProgress(i);
        return true;
    }

    public void onExportingThumbnail(int i, int i2, int i3, byte[] bArr, boolean z) {
        if (i < 1 || i2 < 1 || i3 < 1 || bArr == null) {
            onExportingThumbnailFail(ErrorCode.CAPTURE_FAILED);
            return;
        }
        if (z) {
            int i4 = i * 4;
            try {
                byte[] bArr2 = new byte[i4];
                for (int i5 = 0; i5 < i2 / 2; i5++) {
                    int i6 = i * i5 * 4;
                    System.arraycopy(bArr, i6, bArr2, 0, i4);
                    int i7 = ((i2 - 1) - i5) * i * 4;
                    System.arraycopy(bArr, i7, bArr, i6, i4);
                    System.arraycopy(bArr2, 0, bArr, i7, i4);
                }
            } catch (OutOfMemoryError e) {
                Log.e(LOG_TAG, qq2.l(i, "onExportingThumbnail: out of memory building ", "x", i2, " bitmap"), e);
                onExportingThumbnailFail(ErrorCode.CAPTURE_FAILED);
                return;
            } catch (Exception e2) {
                onExportingThumbnailFail(ErrorCode.CAPTURE_FAILED);
                e2.printStackTrace();
                return;
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        createBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bArr));
        NexEditorListener nexEditorListener = this.m_uiListener;
        if (nexEditorListener != null) {
            nexEditorListener.onExportingThumbnail(createBitmap);
        } else {
            onExportingThumbnailFail(ErrorCode.CAPTURE_FAILED);
        }
    }

    public void onExportingThumbnailFail(ErrorCode errorCode) {
        NexEditorListener nexEditorListener = this.m_uiListener;
        if (nexEditorListener != null) {
            nexEditorListener.onExportingThumbnailFail(errorCode);
        }
    }

    public void onFastOptionPreviewDone(ErrorCode errorCode) {
        Task task;
        Task task2;
        if (errorCode.isError()) {
            Log.d(LOG_TAG, "onFastOptionPreviewDone:" + errorCode.getMessage());
        }
        int i = this.mFastPreviewInProgressCount - 1;
        this.mFastPreviewInProgressCount = i;
        if (this.m_lastSetTimeResult == ErrorCode.NONE) {
            String str = this.mPendingFastPreview;
            if (str == null || i >= 2) {
                if (i < 1 && (task = this.fastOptionPreviewTask) != null) {
                    task.signalEvent(Task.Event.SUCCESS, Task.Event.COMPLETE);
                    this.fastOptionPreviewTask = null;
                }
            } else if (this.m_isSeeking) {
                Log.d(LOG_TAG, "onFastOptionPreviewDone: stat is seeking");
                this.mPendingFastPreview = null;
            } else {
                int fastOptionPreview = fastOptionPreview(str, this.mPendingFastDisplay);
                this.mPendingFastPreview = null;
                if (fastOptionPreview == 0) {
                    this.mFastPreviewInProgressCount++;
                } else {
                    pp1.k(fastOptionPreview, "onFastOptionPreviewDone: pending result=", LOG_TAG);
                }
            }
        } else if (i < 1 && (task2 = this.fastOptionPreviewTask) != null) {
            task2.sendFailure(ErrorCode.FAST_PREVIEW_IGNORED);
            this.fastOptionPreviewTask = null;
        }
    }

    public void onGetClipInfoDone(ErrorCode errorCode, int i) {
        OnGetClipInfoDoneListener onGetClipInfoDoneListener = this.m_onGetClipInfoDoneListener;
        if (onGetClipInfoDoneListener != null) {
            onGetClipInfoDoneListener.onGetClipInfoDone(errorCode, i);
        }
    }

    public void onHighLightDone(int i, int[] iArr) {
        OnHighLightDoneListener onHighLightDoneListener = this.OnHighLightDoneListener;
        if (onHighLightDoneListener != null) {
            onHighLightDoneListener.onHighLightDoneListener(i, iArr);
        }
    }

    public void onHighLightError(int i) {
        OnHighLightDoneListener onHighLightDoneListener = this.OnHighLightDoneListener;
        if (onHighLightDoneListener != null) {
            onHighLightDoneListener.onHighLightErrorListener(i);
        }
    }

    public void onIdle() {
        OnIdleListener onIdleListener = this.m_onIdleListener;
        if (onIdleListener != null) {
            onIdleListener.onIdle();
        }
    }

    public void onImageLabelingInfo(String str) {
        OnImageLabelingListener onImageLabelingListener = this.m_ImageLabelingListener;
        if (onImageLabelingListener != null) {
            onImageLabelingListener.onImageLabelingInfo(str);
        }
    }

    public void onJumpCutInfo(int i, int i2) {
        OnJumpCutListener onJumpCutListener = this.m_JumpCutListener;
        if (onJumpCutListener != null) {
            onJumpCutListener.onJumpCutInfo(i, i2);
        }
    }

    public void onMoveClipDone(int i, int i2) {
        if (this.m_movedClipId.size() >= 1 && this.m_moveClipDoneListeners.size() >= 1 && this.m_movedClipId.peek().intValue() == i2) {
            this.m_movedClipId.remove();
            OnMoveClipDoneListener remove = this.m_moveClipDoneListeners.remove();
            if (i == 0) {
                remove.onMoveClipDone(i2);
            } else {
                remove.onMoveClipFail(ErrorCode.fromValue(i));
            }
        }
    }

    public void onPlay(ErrorCode errorCode) {
        if (this.m_exportingTask == null || !errorCode.isError()) {
            this.m_isPlaying = true;
            this.m_playRequested = false;
            if (this.m_onPlayListeners.size() >= 1) {
                OnPlayListener remove = this.m_onPlayListeners.remove();
                remove.onPlayRequestProcessed(errorCode);
                if (errorCode == ErrorCode.NONE) {
                    this.m_onPlayStartListeners.add(remove);
                    return;
                }
                return;
            }
            return;
        }
        this.m_exportingTask.sendFailure(errorCode);
        this.m_exportingTask = null;
    }

    public void onPlayError(ErrorCode errorCode) {
        Log.w(LOG_TAG, "onPlayError:" + errorCode);
        if (this.m_exportingTask != null && errorCode.isError()) {
            this.m_exportingTask.sendFailure(errorCode);
            this.m_exportingTask = null;
        }
    }

    public void onPlayStart() {
        this.m_playRequested = false;
        if (this.m_onPlayStartListeners.size() >= 1) {
            this.m_onPlayStartListeners.remove().onPlayStarted();
        }
    }

    public void onRequestCallbackInRenderContext(int i) {
        OnCallbackInRendererContextListener onCallbackInRendererContextListener = this.m_onCallbackInRendererContextListener;
        if (onCallbackInRendererContextListener != null) {
            onCallbackInRendererContextListener.onCallbackInRendererContext(i);
            this.m_onCallbackInRendererContextListener = null;
        }
    }

    public void onSTTInfo(int i, int i2, String str) {
        OnSTTListener onSTTListener = this.m_STTListener;
        if (onSTTListener != null) {
            onSTTListener.onSTTInfo(i, i2, str);
        }
    }

    public void onSetTimeDone(int i, int i2, ErrorCode errorCode) {
        synchronized (this.seekSyncObject) {
            try {
                this.m_lastSetTimeResult = errorCode;
                this.m_currentTime = i;
                this.m_frameTime = i2;
                LinkedList<OnSetTimeDoneListener> linkedList = new LinkedList<>();
                for (OnSetTimeDoneListener next : this.m_setTimeDoneListeners) {
                    if (next.m_serialNumber < this.m_seekSerial) {
                        linkedList.add(next);
                    }
                }
                for (OnSetTimeDoneListener onSetTimeDoneListener : linkedList) {
                    if (errorCode == ErrorCode.NONE) {
                        onSetTimeDoneListener.onSetTimeDone(i, i2);
                    } else {
                        onSetTimeDoneListener.onSetTimeFail(errorCode);
                    }
                }
                this.m_setTimeDoneListeners.removeAll(linkedList);
                dumpSetTimeDoneLabels();
                setIsSeekingFlag(false);
            } finally {
                while (true) {
                }
            }
        }
        if (this.m_onSetTimeCancelListeners.size() > 0) {
            this.m_isPendingSeek = false;
            this.m_isPendingNonDisplaySeek = false;
            while (this.m_onSetTimeCancelListeners.size() > 0) {
                this.m_onSetTimeCancelListeners.remove().onComplete(ErrorCode.NONE);
            }
        }
        if (this.m_isPendingNonDisplaySeek) {
            this.m_isPendingNonDisplaySeek = false;
            seek_internal(this.m_pendingNonDisplaySeekLocation, false, 0, (OnSetTimeDoneListener) null);
        } else if (this.m_isPendingSeek) {
            this.m_isPendingSeek = false;
            int i3 = this.m_pendingSeekIDR;
            if (i3 == 0) {
                seek(this.m_pendingSeekLocation);
                return;
            }
            int i4 = this.m_pendingSeekLocation;
            if (i3 == 1) {
                seekIOnly(i4, (OnSetTimeDoneListener) null);
            } else if (i3 == 4) {
                seekFromCache(i4, (OnSetTimeDoneListener) null);
            } else {
                seekIDROnly(i4, (OnSetTimeDoneListener) null);
            }
        }
    }

    public void onSetTimeIgnored() {
        onSetTimeDone(0, 0, ErrorCode.SET_TIME_IGNORED);
    }

    public void onStop(ErrorCode errorCode) {
        this.m_isPlaying = false;
        if (this.m_exportingTask != null && errorCode.isError()) {
            this.m_exportingTask.sendFailure(errorCode);
            this.m_exportingTask = null;
        }
        while (true) {
            OnCompletionListener poll = this.m_onStopListeners.poll();
            if (poll != null) {
                poll.onComplete(errorCode);
            } else {
                return;
            }
        }
    }

    public void onTFMode(TFMode tFMode, int i) {
        OnTranscodingListener onTranscodingListener = this.m_transcodingListener;
        if (onTranscodingListener != null) {
            onTranscodingListener.onTFMode(tFMode, i);
        }
    }

    public void onTTSInfo(String str) {
        OnText2SpeechListener onText2SpeechListener = this.m_Text2SpeechListener;
        if (onText2SpeechListener != null) {
            onText2SpeechListener.onTTSInfo(str);
        }
    }

    public void onThumbDone(int i, int i2, int i3, int i4, int i5, byte[] bArr, int i6, int i7, int i8) {
        OnGetThumbDoneListener onGetThumbDoneListener = this.OnGetThumbDoneListener;
        if (onGetThumbDoneListener != null) {
            onGetThumbDoneListener.onGetThumbDoneListener(i, i2, i3, i4, i5, bArr, i6, i7, i8);
        }
    }

    public void onTimeChange(int i) {
        Task task = this.m_exportingTask;
        if (task != null) {
            task.setProgress(Math.min(this.m_exportProjectDuration, i), this.m_exportProjectDuration);
        }
        this.m_currentTime = i;
    }

    public void onTranscodingDone(ErrorCode errorCode, int i, int i2, int i3) {
        if (isTranscoding()) {
            VideoTranscodingType videoTranscodingType = this.transcodingType;
            releaseTranscodingParameter();
            switch (m.a[videoTranscodingType.ordinal()]) {
                case 1:
                    OnSTTListener onSTTListener = this.m_STTListener;
                    if (onSTTListener != null) {
                        onSTTListener.onSTTDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 2:
                    OnJumpCutListener onJumpCutListener = this.m_JumpCutListener;
                    if (onJumpCutListener != null) {
                        onJumpCutListener.onJumpCutDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 3:
                    OnActionRecognitionListener onActionRecognitionListener = this.m_ActionRecognitionListener;
                    if (onActionRecognitionListener != null) {
                        onActionRecognitionListener.onActionRecognitionDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 4:
                    OnText2SpeechListener onText2SpeechListener = this.m_Text2SpeechListener;
                    if (onText2SpeechListener != null) {
                        onText2SpeechListener.onText2SpeechDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 5:
                    OnImageLabelingListener onImageLabelingListener = this.m_ImageLabelingListener;
                    if (onImageLabelingListener != null) {
                        onImageLabelingListener.onImageLabelingDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 6:
                case 7:
                case 8:
                case 9:
                    OnAIVoiceListener onAIVoiceListener = this.m_AIVoiceListener;
                    if (onAIVoiceListener != null) {
                        onAIVoiceListener.onAIVoiceDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 10:
                    OnAIVocalSeparatorListener onAIVocalSeparatorListener = this.m_AIVocalSeparatorListener;
                    if (onAIVocalSeparatorListener != null) {
                        onAIVocalSeparatorListener.onAIVocalSeparatorDone(errorCode, i, i2, i3);
                        return;
                    }
                    return;
                case 11:
                    OnAIBoxTrackingListener onAIBoxTrackingListener = this.m_AIBoxTrackingListener;
                    if (onAIBoxTrackingListener != null) {
                        onAIBoxTrackingListener.onAIBoxTrackingDone(errorCode, i, i2, i3);
                        break;
                    }
                    break;
            }
            OnTranscodingListener onTranscodingListener = this.m_transcodingListener;
            if (onTranscodingListener != null) {
                onTranscodingListener.onTranscodingDone(errorCode, i, i2, i3);
            }
        }
    }

    public void onTranscodingProgress(int i, int i2, int i3, int i4) {
        switch (m.a[this.transcodingType.ordinal()]) {
            case 1:
                OnSTTListener onSTTListener = this.m_STTListener;
                if (onSTTListener != null) {
                    onSTTListener.onSTTProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 2:
                OnJumpCutListener onJumpCutListener = this.m_JumpCutListener;
                if (onJumpCutListener != null) {
                    onJumpCutListener.onJumpCutProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 3:
                OnActionRecognitionListener onActionRecognitionListener = this.m_ActionRecognitionListener;
                if (onActionRecognitionListener != null) {
                    onActionRecognitionListener.onActionRecognitionProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 4:
                OnText2SpeechListener onText2SpeechListener = this.m_Text2SpeechListener;
                if (onText2SpeechListener != null) {
                    onText2SpeechListener.onText2SpeechProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 5:
                OnImageLabelingListener onImageLabelingListener = this.m_ImageLabelingListener;
                if (onImageLabelingListener != null) {
                    onImageLabelingListener.onImageLabelingProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 6:
            case 7:
            case 8:
            case 9:
                OnAIVoiceListener onAIVoiceListener = this.m_AIVoiceListener;
                if (onAIVoiceListener != null) {
                    onAIVoiceListener.onAIVoiceProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 10:
                OnAIVocalSeparatorListener onAIVocalSeparatorListener = this.m_AIVocalSeparatorListener;
                if (onAIVocalSeparatorListener != null) {
                    onAIVocalSeparatorListener.onAIVocalSeparatorProgress(i, i2, i3, i4);
                    return;
                }
                return;
            case 11:
                OnAIBoxTrackingListener onAIBoxTrackingListener = this.m_AIBoxTrackingListener;
                if (onAIBoxTrackingListener != null) {
                    onAIBoxTrackingListener.onAIBoxTrackingProgress(i, i2, i3, i4);
                    return;
                }
                return;
            default:
                OnTranscodingListener onTranscodingListener = this.m_transcodingListener;
                if (onTranscodingListener != null) {
                    onTranscodingListener.onTranscodingProgress(i, i2, i3, i4);
                    return;
                }
                return;
        }
    }

    public void prepareSurfaceForView(Surface surface) {
        this.viewSurface = surface;
        if (this.mCaptureInProgress) {
            Log.d(LOG_TAG, "prepareSurface wait. image exporting...");
        } else if (!this.mCaptureFrameInProgress) {
            if (sPrepareSurfaceSetToNull) {
                prepareSurface((Surface) null);
            }
            if (surface != null) {
                setPreviewScaleFactor(this.mPreviewScaleFactor);
            }
            prepareSurface(surface);
        }
    }

    public native int processVoiceRecorder(byte[] bArr, int i);

    public int pushLoadedBitmapJ(String str, int[] iArr, int i, int i2, int i3) {
        return pushLoadedBitmap(str, iArr, i, i2, i3);
    }

    public ErrorCode readClipInfoSync(String str, NexClipInfo nexClipInfo, boolean z, int i) {
        Log.d(LOG_TAG, "readClipInfoSync: path:" + str);
        int clipInfoSync = getClipInfoSync(str, nexClipInfo, z ? 1 : 0, i);
        Log.d(LOG_TAG, "readClipInfoSync: result:" + clipInfoSync);
        return ErrorCode.fromValue(clipInfoSync);
    }

    public int releaseNexEDLJ(int i, int i2) {
        return releaseNexEDL(i, i2);
    }

    public int releaseRenderItemJ(int i, int i2) {
        return releaseRenderItem(i, i2);
    }

    public int removeBitmapJ(String str) {
        return removeBitmap(str);
    }

    public ErrorCode requestCallbackInRenderContext(int i, OnCallbackInRendererContextListener onCallbackInRendererContextListener) {
        if (this.m_onCallbackInRendererContextListener != null) {
            return ErrorCode.INVALID_STATE;
        }
        this.m_onCallbackInRendererContextListener = onCallbackInRendererContextListener;
        requestCallbackInRenderContext(i);
        return ErrorCode.NONE;
    }

    public int resetFaceDetectInfoJ(int i) {
        return resetFaceDetectInfo(i);
    }

    public int resetLUTTextuer(int i) {
        return releaseLUTTexture(i);
    }

    public void seek(int i, OnSetTimeDoneListener onSetTimeDoneListener) {
        seek_internal(i, true, 0, onSetTimeDoneListener);
    }

    public void seekFromCache(int i, OnSetTimeDoneListener onSetTimeDoneListener) {
        seek_internal(i, true, 4, onSetTimeDoneListener);
    }

    public void seekIDROnly(int i, OnSetTimeDoneListener onSetTimeDoneListener) {
        seek_internal(i, true, 2, onSetTimeDoneListener);
    }

    public void seekIOnly(int i, OnSetTimeDoneListener onSetTimeDoneListener) {
        seek_internal(i, true, 1, onSetTimeDoneListener);
    }

    public boolean seekedSinceLastPlay() {
        return this.m_didSetTimeSinceLastPlay;
    }

    public boolean set360VideoForceNormalView() {
        set360VideoTrackPosition(0, 0, 0);
        return true;
    }

    public boolean set360VideoViewPosition(int i, int i2) {
        set360VideoTrackPosition(i, i2, 1);
        return true;
    }

    public void setAspectMode(int i) {
        this.mAspectMode = i;
    }

    public void setBackgroundMusic(File file) {
        if (file == null) {
            setBackgroundMusic((String) null, 0);
            return;
        }
        String absolutePath = file.getAbsolutePath();
        int i = this.m_iNextClipID;
        this.m_iNextClipID = i + 1;
        setBackgroundMusic(absolutePath, i);
    }

    public native int setBackgroundMusicVolume(int i, int i2, int i3);

    public native int setBaseFilterRenderItem(String str);

    public native int setBrightness(int i);

    public void setCodecLimits(int i, int i2, long j) {
        if (di2.c) {
            setProperty("HardWareCodecMemSize", String.valueOf(Integer.MAX_VALUE));
            setProperty("HardWareDecMaxCount", String.valueOf(Integer.MAX_VALUE));
            setProperty("HardWareEncMaxCount", String.valueOf(Integer.MAX_VALUE));
            return;
        }
        if (i <= -1) {
            this.mDeviceProfile.getClass();
            boolean f = this.mDeviceProfile.f();
            n36 n36 = this.mDeviceProfile;
            if (f) {
                i = n36.d();
            } else {
                i = n36.d();
            }
        }
        if (j <= -1) {
            j = this.mDeviceProfile.c();
        }
        if (i2 <= -1) {
            this.mDeviceProfile.getClass();
            i2 = 1;
        }
        setProperty("HardWareCodecMemSize", String.valueOf(j));
        setProperty("HardWareDecMaxCount", String.valueOf(i));
        setProperty("HardWareEncMaxCount", String.valueOf(i2));
    }

    public native int setContrast(int i);

    public void setCustomRenderCallback(LayerRenderCallback layerRenderCallback) {
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener != null) {
            nexEditorEventListener.setCustomRenderCallback(layerRenderCallback);
        }
    }

    public native int setDeviceGamma(float f);

    public native int setDeviceLightLevel(int i);

    public void setEventHandler(NexEditorListener nexEditorListener) {
        this.m_uiListener = nexEditorListener;
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener != null) {
            nexEditorEventListener.setUIListener(nexEditorListener);
        }
    }

    public void setExportListener(OnExportListener onExportListener) {
        Deque<OnExportListener> deque = this.m_onExportListeners;
        if (onExportListener == null) {
            deque.clear();
        } else {
            deque.add(onExportListener);
        }
    }

    public void setExportVideoTrackUUID(int i, byte[] bArr) {
        setVideoTrackUUID(i, bArr);
    }

    public void setFirstFaceThread(Thread thread) {
        this.thread_ = thread;
    }

    public native int setForceRTT(int i);

    public native int setGain(int i);

    public native int setGamma(int i);

    public native int setHighlights(int i);

    public native int setHue(int i);

    public native int setLift(int i);

    public void setOnAudioFeatureListener(OnAudioFeatureListener onAudioFeatureListener2) {
        this.onAudioFeatureListener = onAudioFeatureListener2;
    }

    public void setOnDiagnosticEventListener(OnDiagnosticEventListener onDiagnosticEventListener2) {
        this.onDiagnosticEventListener = onDiagnosticEventListener2;
    }

    public void setOnEditorDestroyedListener(OnEditorDestroyedListener onEditorDestroyedListener) {
        this.m_onEditorDestroyedListener = onEditorDestroyedListener;
    }

    public void setOnGetClipInfoDoneListener(OnGetClipInfoDoneListener onGetClipInfoDoneListener) {
        this.m_onGetClipInfoDoneListener = onGetClipInfoDoneListener;
    }

    public void setOnGetThumbDoneListener(OnGetThumbDoneListener onGetThumbDoneListener) {
        this.OnGetThumbDoneListener = onGetThumbDoneListener;
    }

    public void setOnHighLightDoneListener(OnHighLightDoneListener onHighLightDoneListener) {
        this.OnHighLightDoneListener = onHighLightDoneListener;
    }

    public void setOnIdleListener(OnIdleListener onIdleListener) {
        this.m_onIdleListener = onIdleListener;
    }

    public void setOnSurfaceChangeListener(OnSurfaceChangeListener onSurfaceChangeListener) {
        this.m_onSurfaceChangeListener = onSurfaceChangeListener;
    }

    public void setOnTranscodingListener(OnTranscodingListener onTranscodingListener) {
        this.m_transcodingListener = onTranscodingListener;
    }

    public int setOpenGLVersion(String str) {
        return setGLVersion(getOpenGLIntVersionFromStrVersion(str));
    }

    public boolean setPreviewScale(float f) {
        if (f <= CropImageView.DEFAULT_ASPECT_RATIO || f > 1.0f) {
            return false;
        }
        this.mPreviewScaleFactor = f;
        return true;
    }

    public native int setProjectBackgroundColor(int i);

    public native int setProjectEffect(String str);

    public native int setProjectManualVolumeControl(int i);

    public native int setProjectVolume(int i);

    public native int setProjectVolumeFade(int i, int i2);

    public native int setProperty(String str, String str2);

    public void setRenderItem(String str, String str2, boolean z) {
        loadRenderItem(str, str2, z ^ true ? 1 : 0);
    }

    public int setRenderToDefaultJ(int i) {
        return setRenderToDefault(i);
    }

    public int setRenderToMaskJ(int i) {
        return setRenderToMask(i);
    }

    public native int setSaturation(int i);

    public void setScreenMode(int i) {
        this.mScreenMode = i;
    }

    public native int setShadows(int i);

    public native void setSharpness(int i);

    public void setSyncMode(boolean z) {
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener != null) {
            nexEditorEventListener.setSyncMode(z);
        }
    }

    public native int setTaskSleep(int i);

    public native int setTemperature(int i);

    public int setTexNameForLUTLayer(int i, int i2, float f, float f2) {
        return setTexNameForLUT(i, i2, f, f2);
    }

    public void setThemeData(String str, boolean z) {
        loadTheme("", str, z ^ true ? 1 : 0);
    }

    public int setThumbnailRoutineFirst(int i) {
        return setThumbnailRoutine(i);
    }

    public native int setVibrance(int i);

    public void setView(NexThemeView nexThemeView) {
        NexThemeView nexThemeView2 = this.mView;
        if (nexThemeView2 != nexThemeView) {
            Context context = null;
            if (nexThemeView2 != null) {
                nexThemeView2.linkToEditor((NexEditor) null);
            }
            if (nexThemeView != null) {
                nexThemeView.linkToEditor(this);
            }
            this.mView = nexThemeView;
            NexEditorEventListener nexEditorEventListener = this.m_listener;
            if (nexEditorEventListener != null) {
                if (nexThemeView != null) {
                    context = nexThemeView.getContext();
                }
                nexEditorEventListener.setContext(context);
            }
        }
    }

    public native void setVignette(int i);

    public native void setVignetteRange(int i);

    public void setWatermark(boolean z) {
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener != null) {
            nexEditorEventListener.setWatermark(z);
        }
    }

    public void setWatermarkConfiguration(Bitmap bitmap, RectF rectF, int i) {
        NexEditorEventListener nexEditorEventListener = this.m_listener;
        if (nexEditorEventListener != null) {
            nexEditorEventListener.setWatermarkConfiguration(bitmap, rectF, i);
        }
    }

    public ErrorCode startBeatDetection(String str, int i, int i2) {
        return ErrorCode.fromValue(startNativeBeatDetection(str, i, i2));
    }

    public void startPlayMuted() {
        startPlay(1, new OnPlayListener());
    }

    public void startSeekCollectCache(int i, OnSetTimeDoneListener onSetTimeDoneListener) {
        setTimeCancel(new h(this, i, onSetTimeDoneListener));
    }

    public native int startVoiceRecorder(String str, int i, int i2, int i3);

    public void stop(int i, OnCompletionListener onCompletionListener) {
        if (this.m_playRequestPending) {
            for (OnCompletionListener onComplete : this.m_onSetTimeCancelListeners) {
                onComplete.onComplete(ErrorCode.PLAY_SUPERCEEDED);
            }
            this.m_onSetTimeCancelListeners.clear();
        }
        this.m_playRequestPending = false;
        this.m_playRequested = false;
        if (this.m_isPlaying || this.m_exportingTask != null) {
            this.m_onPlayStartListeners.clear();
            this.m_onStopListeners.add(onCompletionListener);
            int stopPlay = stopPlay(i);
            Task task = this.m_exportingTask;
            if (task != null) {
                task.signalEvent(Task.Event.CANCEL);
                this.m_exportingTask = null;
            }
            if (stopPlay != 0) {
                this.m_onStopListeners.remove(onCompletionListener);
                onCompletionListener.onComplete(ErrorCode.fromValue(stopPlay));
                return;
            }
            return;
        }
        stopPlay(i);
        onCompletionListener.onComplete(ErrorCode.NONE);
    }

    public boolean stopCompressionMediaContents() {
        return compressVideoContentsStop();
    }

    public ErrorCode sttProcessStart(STTParam sTTParam, OnSTTListener onSTTListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(sttStartInternal(sTTParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.STT;
            this.m_STTListener = onSTTListener;
        }
        return fromValue;
    }

    public int sttProcessStop() {
        return sttStop();
    }

    public native int sttStop();

    public ErrorCode text2SpeechProcessStart(Text2SpeechParam text2SpeechParam, OnText2SpeechListener onText2SpeechListener) {
        ErrorCode errorCode = ErrorCode.NONE;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(text2SpeechStart(text2SpeechParam));
        if (!fromValue.isError()) {
            this.transcodingType = VideoTranscodingType.TTS;
            this.m_Text2SpeechListener = onText2SpeechListener;
        }
        return fromValue;
    }

    public int text2SpeechProcessStop() {
        return text2SpeechStop();
    }

    public native int text2SpeechStop();

    public native int updateDrawInfo(NexDrawInfo nexDrawInfo);

    public synchronized int updateRenderInfo(int i, int i2, Rect rect, Rect rect2, Rect rect3) {
        int updateRenderInfo;
        Rect rect4 = rect;
        Rect rect5 = rect2;
        Rect rect6 = rect3;
        synchronized (this) {
            updateRenderInfo = updateRenderInfo(i, rect4.left, rect4.top, rect4.right, rect4.bottom, rect5.left, rect5.top, rect5.right, rect5.bottom, i2, rect6.left, rect6.top, rect6.right, rect6.bottom);
        }
        return updateRenderInfo;
    }

    public ErrorCode videoTranscoding(BaseTranscodingParam baseTranscodingParam) {
        BaseTranscodingParam baseTranscodingParam2 = baseTranscodingParam;
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode errorCode = ErrorCode.NONE;
        switch (m.a[baseTranscodingParam2.m_eType.ordinal()]) {
            case 12:
                Log.e(LOG_TAG, "[NexEditor] videoTranscoding INVALID_INFO");
                return ErrorCode.INVALID_INFO;
            case 13:
                VideoTranscodingParam videoTranscodingParam = (VideoTranscodingParam) baseTranscodingParam2;
                String str = videoTranscodingParam.m_sDstPath;
                String str2 = videoTranscodingParam.m_sSrcClipPath;
                if (str == null) {
                    return transcodingStart(str2, videoTranscodingParam.m_fileOutput, videoTranscodingParam.m_iStartTime, videoTranscodingParam.m_iEndTime, videoTranscodingParam.m_iWidth, videoTranscodingParam.m_iHeight, videoTranscodingParam.m_iVideoBitrate, videoTranscodingParam.m_lMaxFileSize, videoTranscodingParam.m_iProfile, videoTranscodingParam.m_iVideoCodecType, videoTranscodingParam.m_iVideoFPS, videoTranscodingParam.m_iUserTag);
                }
                String str3 = str2;
                return transcodingStart(str3, str, videoTranscodingParam.m_iStartTime, videoTranscodingParam.m_iEndTime, videoTranscodingParam.m_iWidth, videoTranscodingParam.m_iHeight, videoTranscodingParam.m_iDisplayWidth, videoTranscodingParam.m_iDisplayHeight, videoTranscodingParam.m_iVideoBitrate, videoTranscodingParam.m_lMaxFileSize, videoTranscodingParam.m_iProfile, videoTranscodingParam.m_iVideoCodecType, videoTranscodingParam.m_iVideoFPS, videoTranscodingParam.m_iFlag, videoTranscodingParam.m_iUserTag);
            case 14:
                VideoReverseParam videoReverseParam = (VideoReverseParam) baseTranscodingParam2;
                String str4 = videoReverseParam.m_sDstPath;
                String str5 = videoReverseParam.m_sSrcClipPath;
                if (str4 == null) {
                    String str6 = str5;
                    String str7 = str6;
                    String str8 = str7;
                    String str9 = str8;
                    String str10 = str9;
                    String str11 = str10;
                    String str12 = str11;
                    String str13 = str12;
                    String str14 = str13;
                    return reverseTranscodingStart(str14, videoReverseParam.m_fileOutput, videoReverseParam.m_tmpClipOutput, videoReverseParam.m_iWidth, videoReverseParam.m_iHeight, videoReverseParam.m_iVideoBitrate, videoReverseParam.m_lMaxFileSize, videoReverseParam.m_iProfile, videoReverseParam.m_iVideoCodecType, videoReverseParam.m_iStartTime, videoReverseParam.m_iEndTime, videoReverseParam.m_iUserTag);
                }
                return reverseTranscodingStart(str5, str4, videoReverseParam.m_iWidth, videoReverseParam.m_iHeight, videoReverseParam.m_iVideoBitrate, videoReverseParam.m_lMaxFileSize, videoReverseParam.m_iProfile, videoReverseParam.m_iVideoCodecType, videoReverseParam.m_iStartTime, videoReverseParam.m_iEndTime, videoReverseParam.m_iUserTag);
            case 15:
                VideoSegmentationParam videoSegmentationParam = (VideoSegmentationParam) baseTranscodingParam2;
                String str15 = videoSegmentationParam.m_sTFLitePath;
                String str16 = str15;
                String str17 = str16;
                String str18 = str17;
                String str19 = str18;
                String str20 = str19;
                String str21 = str20;
                String str22 = str21;
                String str23 = str22;
                ErrorCode fromValue = ErrorCode.fromValue(segmentationStartInternal(str23, videoSegmentationParam.m_sCacheDirPath, videoSegmentationParam.m_sSrcClipPath, videoSegmentationParam.m_sDstPath, videoSegmentationParam.m_iStartTime, videoSegmentationParam.m_iEndTime, videoSegmentationParam.m_lMaxFileSize, videoSegmentationParam.m_iProfile, videoSegmentationParam.m_iVideoCodecType, videoSegmentationParam.m_iUserTag));
                if (!fromValue.isError()) {
                    this.transcodingType = VideoTranscodingType.SEGMENTATION;
                }
                return fromValue;
            case 16:
                ErrorCode fromValue2 = ErrorCode.fromValue(superResolutionStartInternal((VideoSuperResolutionParam) baseTranscodingParam2));
                if (!fromValue2.isError()) {
                    this.transcodingType = VideoTranscodingType.SUPER_RESOLUTION;
                }
                return fromValue2;
            case 17:
                ErrorCode fromValue3 = ErrorCode.fromValue(aiStyleStartInternal((VideoAiStyleParam) baseTranscodingParam2));
                if (!fromValue3.isError()) {
                    this.transcodingType = VideoTranscodingType.AI_STYLE;
                }
                return fromValue3;
            case 18:
                ErrorCode fromValue4 = ErrorCode.fromValue(noiseReductionStartInternal((VideoNoiseReductionParam) baseTranscodingParam2));
                if (!fromValue4.isError()) {
                    this.transcodingType = VideoTranscodingType.NOISE_REDUCTION;
                }
                return fromValue4;
            case 19:
                ErrorCode fromValue5 = ErrorCode.fromValue(videoStabilizerStartInternal((VideoStabilizerParam) baseTranscodingParam2));
                if (!fromValue5.isError()) {
                    this.transcodingType = VideoTranscodingType.VIDEO_STABILIZER;
                }
                return fromValue5;
            default:
                return errorCode;
        }
    }

    public int videoTranscodingStop(VideoTranscodingType videoTranscodingType) {
        switch (m.a[videoTranscodingType.ordinal()]) {
            case 13:
                return transcodingStop();
            case 14:
                return reverseStop();
            case 15:
                return segmentationStop();
            case 16:
                return superResolutionStop();
            case 17:
                return aiStyleStop();
            case 18:
                return noiseReductionStop();
            case 19:
                return videoStabilizerStop();
            default:
                return -1;
        }
    }

    public void waitFaceThread() {
        Thread thread = this.thread_;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.thread_ = null;
        }
    }

    public void waitForSeekDone(OnSetTimeDoneListener onSetTimeDoneListener) {
        if (onSetTimeDoneListener != null) {
            if (!isSeeking()) {
                onSetTimeDoneListener.onSetTimeDone(this.m_currentTime, this.m_frameTime);
                return;
            }
            boolean z = this.m_isPendingSeek;
            int i = this.m_seekSerial;
            if (!z) {
                i--;
            }
            onSetTimeDoneListener.m_serialNumber = i;
            this.m_setTimeDoneListeners.add(onSetTimeDoneListener);
            dumpSetTimeDoneLabels();
        }
    }

    public void seek(int i) {
        seek(i, (OnSetTimeDoneListener) null);
    }

    public void seek(int i, boolean z, OnSetTimeDoneListener onSetTimeDoneListener) {
        seek_internal(i, z, 0, onSetTimeDoneListener);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class EditorInitException extends Exception {
        private static final long serialVersionUID = 1;
        public final int errorCode;
        public final boolean hasErrorCode;

        public EditorInitException() {
            this.errorCode = 0;
            this.hasErrorCode = false;
        }

        public EditorInitException(String str, Throwable th) {
            super(str, th);
            this.errorCode = 0;
            this.hasErrorCode = false;
        }

        public EditorInitException(String str) {
            super(str);
            this.errorCode = 0;
            this.hasErrorCode = false;
        }

        public EditorInitException(String str, int i) {
            super(str);
            this.errorCode = i;
            this.hasErrorCode = true;
        }

        public EditorInitException(Throwable th) {
            super(th);
            this.errorCode = 0;
            this.hasErrorCode = false;
        }
    }

    public int directExport_internal(String str, long j, long j2, String str2, int i) {
        return directExport(str, j, j2, str2, i);
    }

    public int getClipInfoSync(String str, NexClipInfo nexClipInfo, int i, int i2) {
        return getClipInfoSyncInternal(str, nexClipInfo, i, i2);
    }

    public ErrorCode startBeatDetection(String str, int i) {
        return startBeatDetection(str, i, -1);
    }

    public Task exportGIF(File file, int i, int i2, int i3, int i4, int i5, int i6, float f, int i7, int i8, int i9, boolean z, boolean z2) {
        return exportGIF(file, 0, 0, i, i2, i3, i4, i5, i6, f, i7, i8, i9, z, z2);
    }

    public void startPlayMuted(OnPlayListener onPlayListener) {
        startPlay(1, onPlayListener);
    }

    public Task exportGIF(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, float f, int i9, int i10, int i11, boolean z, boolean z2) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        int i12 = i2;
        this.m_exportProjectDuration = i12;
        int encodeProjectToGIF = encodeProjectToGIF(str, i, i12, new Size(i3, i4), i5, i6, i7, i8, f, i9, i10, i11, z, z2);
        if (encodeProjectToGIF != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectToGIF));
            return task;
        }
        this.mExportPath = null;
        this.m_exportingTask = task;
        return task;
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public enum PerformanceCounter {
        FRAME_DROP(0),
        FRAME_WAIT_TIMEOUT(1);
        
        private final int value;

        private PerformanceCounter(int i) {
            this.value = i;
        }

        public long get(PerformanceCounterSnapshot performanceCounterSnapshot) {
            if (performanceCounterSnapshot == null) {
                return 0;
            }
            return performanceCounterSnapshot.snapshotValues[ordinal()];
        }

        public long since(PerformanceCounterSnapshot performanceCounterSnapshot) {
            return get() - performanceCounterSnapshot.initialValues[ordinal()];
        }

        public long get() {
            return NexEditor.getPerformanceCount(this.value);
        }
    }

    public ErrorCode makeVideoThumbs(String str, File file, int i, int i2, int i3, int i4, int i5, int i6, int i7) throws IOException {
        return ErrorCode.fromValue(getClipVideoThumb(str, file.getAbsolutePath(), i, i2, i3, i4, i5, i6, i7));
    }

    public ErrorCode makeVideoThumbs(File file, File file2, int i, int i2, int i3, int[] iArr, int i4, int i5) throws IOException {
        return ErrorCode.fromValue(getClipVideoThumbWithTimeTable(file.getAbsolutePath(), file2.getAbsolutePath(), i, i2, i3, iArr, i4, i5));
    }

    public void startPlay(OnPlayListener onPlayListener) {
        startPlay(0, onPlayListener);
    }

    public ErrorCode makeVideoThumbs(String str, File file, int i, int i2, int i3, int[] iArr, int i4, int i5) throws IOException {
        return ErrorCode.fromValue(getClipVideoThumbWithTimeTable(str, file.getAbsolutePath(), i, i2, i3, iArr, i4, i5));
    }

    public void startPlay() {
        startPlay(0, new OnPlayListener());
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoTranscodingParam extends BaseTranscodingParam {
        File m_fileOutput = null;
        public int m_iDisplayHeight = 0;
        public int m_iDisplayWidth = 0;
        public int m_iFlag = 0;
        int m_iHeight = 0;
        int m_iWidth = 0;

        public VideoTranscodingParam(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, long j, int i10, int i11, int i12) {
            this.m_eType = VideoTranscodingType.TRANSCODING;
            this.m_sSrcClipPath = str;
            this.m_sDstPath = str2;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iWidth = i3;
            this.m_iHeight = i4;
            this.m_iDisplayWidth = i5;
            this.m_iDisplayHeight = i6;
            this.m_iVideoBitrate = i7;
            this.m_iVideoFPS = i8;
            this.m_iFlag = i9;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i10;
            this.m_iVideoCodecType = i11;
            this.m_iUserTag = i12;
        }

        public VideoTranscodingParam(String str, File file, int i, int i2, int i3, int i4, int i5, int i6, long j, int i7, int i8, int i9) {
            this.m_eType = VideoTranscodingType.TRANSCODING;
            this.m_sSrcClipPath = str;
            this.m_fileOutput = file;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iWidth = i3;
            this.m_iHeight = i4;
            this.m_iVideoBitrate = i5;
            this.m_iVideoFPS = i6;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i7;
            this.m_iVideoCodecType = i8;
            this.m_iUserTag = i9;
        }
    }

    public int entitlementClearSubscription() {
        return entitlementClearSubscription(0);
    }

    public String entitlementSubscriptionState(long j) {
        return entitlementSubscriptionState(j, 0);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class ErrorCode implements Task.TaskError {
        public static final ErrorCode ADD_CLIP_FAIL = new ErrorCode("ADD_CLIP_FAIL", 26, "Unable to add clip");
        public static final ErrorCode ALREADY_EXPORTING = new ErrorCode("ALREADY_EXPORTING", 7015, false);
        public static final ErrorCode ARGUMENT_FAILED = new ErrorCode("ARGUMENT_FAILED", 9);
        public static final ErrorCode AUDIORESAMPLER_CREATE_FAIL = new ErrorCode("AUDIORESAMPLER_CREATE_FAIL", 16);
        public static final ErrorCode BLACKLISTED = new ErrorCode("BLACKLISTED", 7033);
        public static final ErrorCode CAPTURE_FAILED = new ErrorCode("CAPTURE_FAILED", AdError.CLEAR_TEXT_SUPPORT_NOT_ALLOWED, false, "Capture failed");
        public static final ErrorCode CAPTURE_FAIL_ENOSPC = new ErrorCode("CAPTURE_FAIL_ENOSPC", 7023, false, "Not enough space");
        public static final ErrorCode CAPTURE_FAIL_OTHER = new ErrorCode("CAPTURE_FAIL_OTHER", 7024, false, "Capture failed");
        public static final ErrorCode CAPTURE_FAIL_SCANNING = new ErrorCode("CAPTURE_FAIL_SCANNING", 7025, false, "Media scanner failed");
        public static final ErrorCode CLOUD_FILE_CREATION_FAILED = new ErrorCode("CLOUD_FILE_CREATION_FAILED", 8003);
        public static final ErrorCode CLOUD_FOLDER_CREATION_FAILED = new ErrorCode("CLOUD_FOLDER_CREATION_FAILED", 8002);
        public static final ErrorCode CLOUD_LOGIN_FAILED = new ErrorCode("CLOUD_LOGIN_FAILED", 8000);
        public static final ErrorCode CLOUD_LOGOUT_FAILED = new ErrorCode("CLOUD_LOGOUT_FAILED", 8001);
        public static final ErrorCode CODEC_INIT = new ErrorCode("CODEC_INIT", 23, "Codec init failed");
        public static final ErrorCode COMPRESS_PROJECT_BUSY = new ErrorCode("COMPRESS PROJECT BUSY", 98);
        public static final ErrorCode COMPRESS_PROJECT_USER_CANCEL = new ErrorCode("COMPRESS PROJECT USER CANCEL", 99);
        public static final ErrorCode CREATE_FAILED = new ErrorCode("CREATE_FAILED", 7);
        public static final ErrorCode DESTINATION_FILE_ALREADY_EXISTS = new ErrorCode("DESTINATION_FILE_ALREADY_EXISTS", (int) AdError.API_NOT_SUPPORTED, false);
        public static final ErrorCode DIRECTEXPORT_CHECK_ERROR = new ErrorCode("DIRECTEXPORT_CHECK_ERROR", 64);
        public static final ErrorCode DIRECTEXPORT_CLIPLIST_ERROR = new ErrorCode("DIRECTEXPORT_CLIPLIST_ERROR", 63);
        public static final ErrorCode DIRECTEXPORT_DEC_DECODE_ERROR = new ErrorCode("DIRECTEXPORT_DEC_DECODE_ERROR", 69);
        public static final ErrorCode DIRECTEXPORT_DEC_INIT_ERROR = new ErrorCode("DIRECTEXPORT_DEC_INIT_ERROR", 67);
        public static final ErrorCode DIRECTEXPORT_DEC_INIT_SURFACE_ERROR = new ErrorCode("DIRECTEXPORT_DEC_INIT_SURFACE_ERROR", 68);
        public static final ErrorCode DIRECTEXPORT_ENC_DSI_DIFF_ERROR = new ErrorCode("DIRECTEXPORT_ENC_DSI_DIFF_ERROR", 74);
        public static final ErrorCode DIRECTEXPORT_ENC_ENCODE_ERROR = new ErrorCode("DIRECTEXPORT_ENC_ENCODE_ERROR", 71);
        public static final ErrorCode DIRECTEXPORT_ENC_FRAME_CONVERT_ERROR = new ErrorCode("DIRECTEXPORT_ENC_FRAME_CONVERT_ERROR", 75);
        public static final ErrorCode DIRECTEXPORT_ENC_FUNCTION_ERROR = new ErrorCode("DIRECTEXPORT_ENC_FUNCTION_ERROR", 73);
        public static final ErrorCode DIRECTEXPORT_ENC_INIT_ERROR = new ErrorCode("DIRECTEXPORT_ENC_INIT_ERROR", 70);
        public static final ErrorCode DIRECTEXPORT_ENC_INPUT_SURFACE_ERROR = new ErrorCode("DIRECTEXPORT_ENC_INPUT_SURFACE_ERROR", 72);
        public static final ErrorCode DIRECTEXPORT_FILEREADER_INIT_ERROR = new ErrorCode("DIRECTEXPORT_FILEREADER_INIT_ERROR", 65);
        public static final ErrorCode DIRECTEXPORT_FILEWRITER_INIT_ERROR = new ErrorCode("DIRECTEXPORT_FILEWRITER_INIT_ERROR", 66);
        public static final ErrorCode DIRECTEXPORT_RENDER_INIT_ERROR = new ErrorCode("DIRECTEXPORT_RENDER_INIT_ERROR", 76);
        public static final ErrorCode DIRECTEXPORT_WRITER_UNKNOWN_ERROR = new ErrorCode("DIRECTEXPORT_WRITER_UNKNOWN_ERROR", 78);
        public static final ErrorCode DIRECTEXPORT_WRITER_WRITE_ERROR = new ErrorCode("DIRECTEXPORT_WRITER_WRITE_ERROR", 77);
        public static final ErrorCode DUMMY_ERROR = new ErrorCode("DUMMY_ERROR", 7022, false);
        public static final ErrorCode EDITOR_INSTANCE_DESTROYED = new ErrorCode("EDITOR_INSTANCE_DESTROYED", 7027, false);
        public static final ErrorCode EMPTY_PROJECT = new ErrorCode("EMPTY_PROJECT", 7016, false);
        public static final ErrorCode ENCODE_VIDEO_FAIL = new ErrorCode("ENCODE_VIDEO_FAIL", 27);
        public static final ErrorCode EVENTHANDLER = new ErrorCode("EVENTHANDLER", 11);
        public static final ErrorCode EXPORT_AUDIO_DEC_INIT_FAIL = new ErrorCode("EXPORT_AUDIO_DEC_INIT_FAIL", 44);
        public static final ErrorCode EXPORT_NOT_ENOUGHT_DISK_SPACE = new ErrorCode("EXPORT_NOT_ENOUGHT_DISK_SPACE", 48, "Not enough space");
        public static final ErrorCode EXPORT_NO_SUCCESS = new ErrorCode("EXPORT_NO_SUCCESS", 7009, false);
        public static final ErrorCode EXPORT_UNEXPECTED_STOP = new ErrorCode("EXPORT_UNEXPECTED_STOP", 7018, false);
        public static final ErrorCode EXPORT_USER_CANCEL = new ErrorCode("EXPORT_USER_CANCEL", 86);
        public static final ErrorCode EXPORT_VIDEO_DEC_INIT_FAIL = new ErrorCode("EXPORT_VIDEO_DEC_INIT_FAIL", 45);
        public static final ErrorCode EXPORT_VIDEO_ENC_FAIL = new ErrorCode("EXPORT_VIDEO_ENC_FAIL", 46);
        public static final ErrorCode EXPORT_VIDEO_RENDER_INIT_FAIL = new ErrorCode("EXPORT_VIDEO_RENDER_INIT_FAIL", 47);
        public static final ErrorCode EXPORT_WRITER_INIT_FAIL = new ErrorCode("EXPORT_WRITER_INIT_FAIL", 42);
        public static final ErrorCode EXPORT_WRITER_INVAILED_HANDLE = new ErrorCode("EXPORT_WRITER_INVAILED_HANDLE", 41);
        public static final ErrorCode EXPORT_WRITER_START_FAIL = new ErrorCode("EXPORT_WRITER_START_FAIL", 43);
        public static final ErrorCode FASTPREVIEW_CLIPLIST_ERROR = new ErrorCode("FASTPREVIEW_CLIPLIST_ERROR", 80);
        public static final ErrorCode FASTPREVIEW_DEC_INIT_ERROR = new ErrorCode("FASTPREVIEW_DEC_INIT_ERROR", 87);
        public static final ErrorCode FASTPREVIEW_DEC_INIT_SURFACE_ERROR = new ErrorCode("FASTPREVIEW_DEC_INIT_SURFACE_ERROR", 84);
        public static final ErrorCode FASTPREVIEW_FILEREADER_INIT_ERROR = new ErrorCode("FASTPREVIEW_FILEREADER_INIT_ERROR", 88);
        public static final ErrorCode FASTPREVIEW_FIND_CLIP_ERROR = new ErrorCode("FASTPREVIEW_FIND_CLIP_ERROR", 81);
        public static final ErrorCode FASTPREVIEW_FIND_READER_ERROR = new ErrorCode("FASTPREVIEW_FIND_READER_ERROR", 82);
        public static final ErrorCode FASTPREVIEW_OUTPUTSURFACE_INIT_ERROR = new ErrorCode("FASTPREVIEW_OUTPUTSURFACE_INIT_ERROR", 91);
        public static final ErrorCode FASTPREVIEW_RENDER_INIT_ERROR = new ErrorCode("FASTPREVIEW_RENDER_INIT_ERROR", 90);
        public static final ErrorCode FASTPREVIEW_TIME_ERROR = new ErrorCode("FASTPREVIEW_TIME_ERROR", 89);
        public static final ErrorCode FASTPREVIEW_USER_CANCEL = new ErrorCode("FASTPREVIEW_USER_CANCEL", 79);
        public static final ErrorCode FASTPREVIEW_VIDEO_RENDERER_ERROR = new ErrorCode("FASTPREVIEW_VIDEO_RENDERER_ERROR", 83);
        public static final ErrorCode FAST_OPTION_PREVIEW_INVALID_CLIP_ID = new ErrorCode("FAST_OPTION_PREVIEW_INVALID_CLIP_ID", 101);
        public static final ErrorCode FAST_PREVIEW_IGNORED = new ErrorCode("FAST_PREVIEW_IGNORED", 7021, false);
        public static final ErrorCode FILEREADER_CREATE_FAIL = new ErrorCode("FILEREADER_CREATE_FAIL", 14, "Could not open file");
        public static final ErrorCode FILEREADER_FAILED = new ErrorCode("FILEREADER_FAILED", 18, "Error reading file format");
        public static final ErrorCode FILEWRITER_CREATE_FAIL = new ErrorCode("FILEWRITER_CREATE_FAIL", 15);
        public static final ErrorCode FILE_INVALID_SYNTAX = new ErrorCode("FILE_INVALID_SYNTAX", 13);
        public static final ErrorCode FILE_IO_FAILED = new ErrorCode("FILE_IO_FAILED", 12, "Error accessing file");
        public static final ErrorCode FILE_MISSING = new ErrorCode("FILE_MISSING", 7028, false);
        public static final ErrorCode GENERAL = new ErrorCode("GENERAL", 1);
        public static final ErrorCode GETCLIPINFO_USER_CANCEL = new ErrorCode("GETCLIPINFO_USER_CANCEL", 62);
        public static final ErrorCode HIGHLIGHT_CODEC_DECODE_ERROR = new ErrorCode("HIGHLIGHT_CODEC_DECODE_ERROR", 56);
        public static final ErrorCode HIGHLIGHT_CODEC_INIT_ERROR = new ErrorCode("HIGHLIGHT_CODEC_INIT_ERROR", 55);
        public static final ErrorCode HIGHLIGHT_FILEREADER_INIT_ERROR = new ErrorCode("HIGHLIGHT_FILEREADER_INIT_ERROR", 53);
        public static final ErrorCode HIGHLIGHT_GET_INDEX_ERROR = new ErrorCode("HIGHLIGHT_GET_INDEX_ERROR", 60);
        public static final ErrorCode HIGHLIGHT_RENDER_INIT_ERROR = new ErrorCode("HIGHLIGHT_RENDER_INIT_ERROR", 57);
        public static final ErrorCode HIGHLIGHT_TOO_SHORT_CONTENTS = new ErrorCode("HIGHLIGHT_TOO_SHORT_CONTENTS", 54);
        public static final ErrorCode HIGHLIGHT_USER_CANCEL = new ErrorCode("HIGHLIGHT_USER_CANCEL", 61);
        public static final ErrorCode HIGHLIGHT_WRITER_INIT_ERROR = new ErrorCode("HIGHLIGHT_WRITER_INIT_ERROR", 58);
        public static final ErrorCode HIGHLIGHT_WRITER_WRITE_ERROR = new ErrorCode("HIGHLIGHT_WRITER_WRITE_ERROR", 59);
        public static final ErrorCode HW_NOT_ENOUGH_MEMORY = new ErrorCode("HW_NOT_ENOUGH_MEMORY", 85);
        public static final ErrorCode IMAGE_PROCESS = new ErrorCode("IMAGE_PROCESS", 4097);
        public static final ErrorCode INPROGRESS_GETCLIPINFO = new ErrorCode("INPROGRESS_GETCLIPINFO", 28);
        public static final ErrorCode INVALID_INFO = new ErrorCode("INVALID_INFO", 4);
        public static final ErrorCode INVALID_STATE = new ErrorCode("INVALID_STATE", 5);
        public static final ErrorCode IN_SIM_PLAY = new ErrorCode("IN_SIM_PLAY", 7029, false);
        public static final ErrorCode MEDIAINFO_EXCLUDED = new ErrorCode("MEDIAINFO_EXCLUDED", 7032);
        public static final ErrorCode MEDIAINFO_THUMBNAIL_EXCLUDED = new ErrorCode("MEDIAINFO_THUMBNAIL_EXCLUDED", 7035);
        public static final ErrorCode MEMALLOC_FAILED = new ErrorCode("MEMALLOC_FAILED", 8, "Memory allocation failed");
        public static final ErrorCode MISSING_RESOURCES = new ErrorCode("MISSING_RESOURCES", 7017, false);
        public static final ErrorCode NEXVIDEOEDITOR_ERROR_AUDIO_RENDERER = new ErrorCode("ERROR_AUDIO_RENDERER", 94);
        public static final ErrorCode NEXVIDEOEDITOR_ERROR_CACHE_MEMORY_NOT_ACCESS = new ErrorCode("CACHE_MEMORY_NOT_ACCESS", 97);
        public static final ErrorCode NEXVIDEOEDITOR_ERROR_CODEC_DECODE = new ErrorCode("ERROR_CODEC_DECODE", 93);
        public static final ErrorCode NEXVIDEOEDITOR_ERROR_FASTPREVIEW_BUSY = new ErrorCode("ERROR_FASTPREVIEW_BUSY", 92);
        public static final ErrorCode NEXVIDEOEDITOR_ERROR_VIDEO_RENDERER = new ErrorCode("ERROR_VIDEO_RENDERER", 96);
        public static final ErrorCode NONE = new ErrorCode("NONE", 0);
        public static final ErrorCode NOT_ENOUGH_NEMORY = new ErrorCode("NOT_ENOUGH_NEMORY", 10, "Insufficient memory");
        public static final ErrorCode NOT_READY_TO_PLAY = new ErrorCode("NOT_READY_TO_PLAY", 7012, false);
        public static final ErrorCode NO_ACTION = new ErrorCode("NO_ACTION", 3);
        public static final ErrorCode NO_INSTANCE_AVAILABLE = new ErrorCode("NO_INSTANCE_AVAILABLE", 7008, false);
        public static final ErrorCode NO_PROJECT_LOADED = new ErrorCode("NO_PROJECT_LOADED", 7014, false);
        public static final ErrorCode PLAYSTART_FAILED = new ErrorCode("PLAYSTART_FAILED", 19);
        public static final ErrorCode PLAYSTOP_FAILED = new ErrorCode("PLAYSTOP_FAILED", 20);
        public static final ErrorCode PLAY_SUPERCEEDED = new ErrorCode("PLAY_SUPERCEEDED", 7010, false);
        public static final ErrorCode PROJECT_LOAD_FAIL = new ErrorCode("PROJECT_LOAD_FAIL", 7019, false);
        public static final ErrorCode PROJECT_NOT_CREATE = new ErrorCode("PROJECT_NOT_CREATE", 21);
        public static final ErrorCode PROJECT_NOT_OPEN = new ErrorCode("PROJECT_NOT_OPEN", 22);
        public static final ErrorCode RENAME_FAIL = new ErrorCode("RENAME_FAIL", 7020, false);
        public static final ErrorCode RENDERER_INIT = new ErrorCode("RENDERER_INIT", 24);
        public static final ErrorCode SAVE_FAILED = new ErrorCode("SAVE_FAILED", 7031);
        public static final ErrorCode SAVE_NOT_ENOUGHT_DISK_SPACE = new ErrorCode("SAVE_NOT_ENOUGHT_DISK_SPACE", 7030, "Not enough space");
        public static final ErrorCode SEEKING_LOCKED = new ErrorCode("SEEKING_LOCKED", 7013, false);
        public static final ErrorCode SET_TIME_CANCELED = new ErrorCode("SET_TIME_CANCELED", AdError.LOAD_CALLED_WHILE_SHOWING_AD, false, "Set time canceled");
        public static final ErrorCode SET_TIME_IGNORED = new ErrorCode("SET_TIME_IGNORED", 7001, false, "Set time ignored");
        public static final ErrorCode SOURCE_FILE_NOT_FOUND = new ErrorCode("SOURCE_FILE_NOT_FOUND", (int) AdError.INCORRECT_STATE_ERROR, false);
        public static final ErrorCode TEMP_FILE_ALREADY_EXISTS = new ErrorCode("TEMP_FILE_ALREADY_EXISTS", (int) AdError.NATIVE_AD_IS_NOT_LOADED, false);
        public static final ErrorCode TEST_TIMEOUT = new ErrorCode("TEST_TIMEOUT", 7034);
        public static final ErrorCode THEMESET_CREATE_FAIL = new ErrorCode("THEMESET_CREATE_FAIL", 25);
        public static final ErrorCode THUMBNAIL_BUSY = new ErrorCode("THUMBNAIL_BUSY", 29);
        public static final ErrorCode THUMBNAIL_INIT_FAIL = new ErrorCode("THUMBNAIL_INIT_FAIL", 50);
        public static final ErrorCode TRANSCODING_ABORTED = new ErrorCode("TRANSCODING_ABORTED", (int) AdError.MISSING_DEPENDENCIES_ERROR, false);
        public static final ErrorCode TRANSCODING_BUSY = new ErrorCode("TRANSCODING_BUSY", 36);
        public static final ErrorCode TRANSCODING_CODEC_FAILED = new ErrorCode("TRANSCODING_CODEC_FAILED", 40);
        public static final ErrorCode TRANSCODING_NOT_ENOUGHT_DISK_SPACE = new ErrorCode("TRANSCODING_NOT_ENOUGHT_DISK_SPACE", 39);
        public static final ErrorCode TRANSCODING_NOT_SUPPORTED_FORMAT = new ErrorCode("TRANSCODING_NOT_SUPPORTED_FORMAT", 37);
        public static final ErrorCode TRANSCODING_USER_CANCEL = new ErrorCode("TRANSCODING_USER_CANCEL", 38);
        public static final ErrorCode UNKNOWN = new ErrorCode("UNKNOWN", 2);
        public static final ErrorCode UNRECOGNIZED_ERROR_CODE = new ErrorCode("UNRECOGNIZED_ERROR_CODE", 7026, false, "Unrecognized error code");
        public static final ErrorCode UNSUPPORT_AUDIO_CODEC = new ErrorCode("UNSUPPORT_AUDIO_CODEC", 51);
        public static final ErrorCode UNSUPPORT_AUDIO_PROFILE = new ErrorCode("UNSUPPORT_AUDIO_PROFILE", 49);
        public static final ErrorCode UNSUPPORT_AUDIO_SAMPLINGRATE = new ErrorCode("UNSUPPORT AUDIO SAMPLINGRATE", 95);
        public static final ErrorCode UNSUPPORT_FORMAT = new ErrorCode("UNSUPPORT_FORMAT", 17, "Unsupported format");
        public static final ErrorCode UNSUPPORT_MAX_RESOLUTION = new ErrorCode("UNSUPPORT_MAX_RESOLUTION", 31);
        public static final ErrorCode UNSUPPORT_MIN_DURATION = new ErrorCode("UNSUPPORT_MIN_DURATION", 30);
        public static final ErrorCode UNSUPPORT_MIN_RESOLUTION = new ErrorCode("UNSUPPORT_MIN_RESOLUTION", 32);
        public static final ErrorCode UNSUPPORT_VIDEIO_PROFILE = new ErrorCode("UNSUPPORT_VIDEIO_PROFILE", 33);
        public static final ErrorCode UNSUPPORT_VIDEO_CODEC = new ErrorCode("UNSUPPORT_VIDEO_CODEC", 52);
        public static final ErrorCode UNSUPPORT_VIDEO_FPS = new ErrorCode("UNSUPPORT_VIDEO_FPS", 35);
        public static final ErrorCode UNSUPPORT_VIDEO_LEVEL = new ErrorCode("UNSUPPORT_VIDEO_LEVEL", 34);
        public static final ErrorCode VERSION_MISMATCH = new ErrorCode("VERSION_MISMATCH", 6);
        public static final ErrorCode WRAPPER_BUSY = new ErrorCode("WRAPPER_BUSY", 7011, false);
        private static SparseArray<ErrorCode> errorCodes = new SparseArray<>();
        private final String mDescription;
        private final boolean mFromEngine;
        private final String mName;
        private final int mValue;

        public ErrorCode(String str, int i) {
            this.mValue = i;
            this.mDescription = null;
            this.mFromEngine = true;
            this.mName = str;
            if (errorCodes.get(i) == null) {
                errorCodes.put(i, this);
            } else {
                jka.c();
                throw null;
            }
        }

        public static ErrorCode fromValue(int i) {
            ErrorCode errorCode = errorCodes.get(i);
            if (errorCode != null) {
                return errorCode;
            }
            ErrorCode errorCode2 = new ErrorCode(qq2.k(i, "UNKNOWN_ERROR_"), i);
            errorCodes.put(i, errorCode2);
            return errorCode2;
        }

        public String getDescription() {
            String str = this.mDescription;
            if (str == null) {
                return name();
            }
            return str;
        }

        public Exception getException() {
            return null;
        }

        public int getIntErrorCode() {
            return getValue();
        }

        @Deprecated
        public String getLocalizedMessage(Context context) {
            return "";
        }

        public String getMessage() {
            return getDescription();
        }

        public int getValue() {
            return this.mValue;
        }

        public boolean isError() {
            if (this != NONE) {
                return true;
            }
            return false;
        }

        public String name() {
            return this.mName;
        }

        public String toString() {
            if (this == NONE) {
                return "NONE(0)";
            }
            if (this.mValue == 0) {
                return name();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(name());
            sb.append("(");
            return x5.l(sb, this.mValue, ")");
        }

        public ErrorCode(String str, int i, String str2) {
            this.mValue = i;
            this.mDescription = str2;
            this.mFromEngine = true;
            this.mName = str;
            if (errorCodes.get(i) == null) {
                errorCodes.put(i, this);
            } else {
                jka.c();
                throw null;
            }
        }

        public ErrorCode(String str, int i, boolean z, String str2) {
            this.mValue = 0;
            this.mDescription = str2;
            this.mFromEngine = z;
            this.mName = str;
            if (errorCodes.get(i) == null) {
                errorCodes.put(i, this);
            } else {
                jka.c();
                throw null;
            }
        }

        public ErrorCode(String str, int i, boolean z) {
            this.mValue = 0;
            this.mDescription = null;
            this.mFromEngine = z;
            this.mName = str;
            if (errorCodes.get(i) == null) {
                errorCodes.put(i, this);
            } else {
                jka.c();
                throw null;
            }
        }
    }

    public void setView(NexThemeView nexThemeView, Context context) {
        NexThemeView nexThemeView2 = this.mView;
        if (nexThemeView2 != nexThemeView) {
            if (nexThemeView2 != null) {
                nexThemeView2.linkToEditor((NexEditor) null);
            }
            if (nexThemeView != null) {
                nexThemeView.linkToEditor(this);
            }
            this.mView = nexThemeView;
            NexEditorEventListener nexEditorEventListener = this.m_listener;
            if (nexThemeView != null) {
                context = nexThemeView.getContext();
            }
            nexEditorEventListener.setContext(context);
        }
    }

    public int entitlementInstallSubscription(byte[] bArr, byte[] bArr2, long j) {
        return entitlementInstallSubscription(bArr, bArr2, j, 0);
    }

    public ErrorCode makeAudioPCM(File file, File file2, int i, int i2, int i3) {
        return makeAudioPCM(file, file2, (File) null, i, i2, i3);
    }

    public ErrorCode makeAudioPCM(File file, File file2, File file3, int i, int i2) {
        return makeAudioPCM(file, file2, file3, i, i2, -1);
    }

    public ErrorCode makeAudioPCM(File file, File file2, int i, int i2) {
        return makeAudioPCM(file, file2, (File) null, i, i2, -1);
    }

    public ErrorCode makeAudioPCM(String str, File file, int i, int i2) {
        return makeAudioPCM(str, file, (File) null, i, i2, -1);
    }

    public ErrorCode makeAudioPCM(String str, File file, int i, int i2, int i3) {
        return makeAudioPCM(str, file, (File) null, i, i2, i3);
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class VideoReverseParam extends BaseTranscodingParam {
        File m_fileOutput = null;
        int m_iHeight = 0;
        int m_iWidth = 0;
        File m_tmpClipOutput = null;

        public VideoReverseParam(String str, String str2, int i, int i2, int i3, int i4, int i5, long j, int i6, int i7, int i8) {
            this.m_eType = VideoTranscodingType.REVERSE;
            this.m_sSrcClipPath = str;
            this.m_sDstPath = str2;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iWidth = i3;
            this.m_iHeight = i4;
            this.m_iVideoBitrate = i5;
            this.m_lMaxFileSize = j;
            this.m_iProfile = i6;
            this.m_iVideoCodecType = i7;
            this.m_iUserTag = i8;
        }

        public VideoReverseParam(String str, File file, File file2, int i, int i2, int i3, int i4, int i5, long j, int i6) {
            this.m_eType = VideoTranscodingType.REVERSE;
            this.m_sSrcClipPath = str;
            this.m_fileOutput = file;
            this.m_tmpClipOutput = file2;
            this.m_iStartTime = i;
            this.m_iEndTime = i2;
            this.m_iWidth = i3;
            this.m_iHeight = i4;
            this.m_iVideoBitrate = i5;
            this.m_lMaxFileSize = j;
            this.m_iUserTag = i6;
        }
    }

    public ErrorCode makeAudioPCM(String str, File file, File file2, int i, int i2) {
        return makeAudioPCM(str, file, file2, i, i2, -1);
    }

    public ErrorCode makeAudioPCM(String str, File file, File file2, int i, int i2, int i3) {
        String str2 = null;
        String absolutePath = file != null ? file.getAbsolutePath() : null;
        if (file2 != null) {
            str2 = file2.getAbsolutePath();
        }
        return ErrorCode.fromValue(getClipAudioThumb(str, absolutePath, str2, i, i2, i3));
    }

    public ErrorCode makeAudioPCMPartial(File file, File file2, int i, int i2, int i3, int i4, int i5, int i6) {
        return makeAudioPCMPartial(file, file2, (File) null, i, i2, i3, i4, i5, i6);
    }

    public ErrorCode makeAudioPCMPartial(File file, File file2, File file3, int i, int i2, int i3, int i4, int i5) {
        return makeAudioPCMPartial(file, file2, file3, i, i2, i3, i4, i5, -1);
    }

    public ErrorCode makeAudioPCMPartial(File file, File file2, int i, int i2, int i3, int i4, int i5) {
        return makeAudioPCMPartial(file, file2, (File) null, i, i2, i3, i4, i5, -1);
    }

    public ErrorCode makeAudioPCMPartial(String str, File file, int i, int i2, int i3, int i4, int i5) {
        return makeAudioPCMPartial(str, file, (File) null, i, i2, i3, i4, i5, -1);
    }

    public ErrorCode makeAudioPCMPartial(String str, File file, int i, int i2, int i3, int i4, int i5, int i6) {
        return makeAudioPCMPartial(str, file, (File) null, i, i2, i3, i4, i5, i6);
    }

    public ErrorCode makeAudioPCMPartial(String str, File file, File file2, int i, int i2, int i3, int i4, int i5) {
        return makeAudioPCMPartial(str, file, file2, i, i2, i3, i4, i5, -1);
    }

    public ErrorCode makeAudioPCMPartial(String str, File file, File file2, int i, int i2, int i3, int i4, int i5, int i6) {
        String str2 = null;
        String absolutePath = file != null ? file.getAbsolutePath() : null;
        if (file2 != null) {
            str2 = file2.getAbsolutePath();
        }
        return ErrorCode.fromValue(getClipAudioThumbPartial(str, absolutePath, str2, i, i2, i3, i4, i5, i6));
    }

    private ErrorCode transcodingStart(String str, String str2, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j, int i8, int i9, int i10, int i11, int i12) {
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        ErrorCode fromValue = ErrorCode.fromValue(transcodingStartInternal(str, str2, i, i2, i3, i4, i5, i6, i7, j, i8, i9, i10, i11, i12));
        if (fromValue.isError()) {
            releaseTranscodingParameter();
            return fromValue;
        }
        this.transcodingType = VideoTranscodingType.TRANSCODING;
        return fromValue;
    }

    /* compiled from: r8-map-id-3e68633d48c8b3c1b09e58f90f664215cafffb19572dd9d133e797fefb5a09d6 */
    public static class PerformanceCounterSnapshot {
        /* access modifiers changed from: private */
        public long[] initialValues;
        /* access modifiers changed from: private */
        public long[] snapshotValues;

        public PerformanceCounterSnapshot(PerformanceCounterSnapshot performanceCounterSnapshot) {
            this.initialValues = new long[PerformanceCounter.values().length];
            this.snapshotValues = new long[PerformanceCounter.values().length];
            for (PerformanceCounter performanceCounter : PerformanceCounter.values()) {
                this.initialValues[performanceCounter.ordinal()] = performanceCounterSnapshot.initialValues[performanceCounter.ordinal()];
                this.snapshotValues[performanceCounter.ordinal()] = performanceCounter.get() - performanceCounterSnapshot.initialValues[performanceCounter.ordinal()];
            }
        }

        public PerformanceCounterSnapshot() {
            long[] jArr = new long[PerformanceCounter.values().length];
            this.initialValues = jArr;
            this.snapshotValues = jArr;
            for (PerformanceCounter performanceCounter : PerformanceCounter.values()) {
                this.initialValues[performanceCounter.ordinal()] = performanceCounter.get();
            }
        }
    }

    public void clearPendingEvents() {
        clearPendingEvents(new OnCompletionListener());
    }

    public void fastPreview(FastPreviewOption fastPreviewOption, int i) {
        fastPreview(fastPreviewOption, i, true);
    }

    public boolean fastPreview(FastPreviewOption fastPreviewOption, int i, boolean z) {
        if (this.mCaptureInProgress || this.transcodingType == VideoTranscodingType.REVERSE) {
            return false;
        }
        return fastOptionPreviewWrapper(fastPreviewOption.name() + "=" + i, z ? 1 : 0);
    }

    private ErrorCode reverseTranscodingStart(String str, String str2, int i, int i2, int i3, long j, int i4, int i5, int i6, int i7, int i8) {
        if (isTranscoding()) {
            return ErrorCode.TRANSCODING_BUSY;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mContext.getExternalFilesDir((String) null));
        String o = x5.o(sb, File.separator, ".reverse_temp.mp4");
        this.mTranscodingTempFile = new File(o);
        ErrorCode fromValue = ErrorCode.fromValue(reverseStartInternal(str, str2, o, i, i2, i3, j, i4, i5, i6, i7, i8));
        if (fromValue.isError()) {
            releaseTranscodingParameter();
            return fromValue;
        }
        this.transcodingType = VideoTranscodingType.REVERSE;
        return fromValue;
    }

    public Task export(File file, Size size, Rect rect, int i, long j, int i2, int i3, int i4, int i5, int i6, boolean z, int i7) {
        return export(file, (String) null, 0, 0, size, rect, i, j, i2, i3, i4, i5, i6 == 0 ? DEFAULT_EXPORT_CODEC : i6, z, i7);
    }

    public Task export(File file, String str, int i, int i2, Size size, Rect rect, int i3, long j, int i4, int i5, int i6, int i7, int i8, boolean z, int i9) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        int i10 = i2;
        this.m_exportProjectDuration = i10;
        String exportContentResolverPath = getExportContentResolverPath(file);
        this.mExportPath = exportContentResolverPath;
        int encodeProjectInternal = encodeProjectInternal(exportContentResolverPath, str, i, i10, size, rect, i3, j, i4, 1920, 1080, i7, DEFAULT_EXPORT_AUDIO_BITRATE, i5, i6, i8 == 0 ? DEFAULT_EXPORT_CODEC : i8, 0, z, i9);
        if (encodeProjectInternal != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectInternal));
            return task;
        }
        this.m_exportingTask = task;
        return task;
    }

    public Task export(String str, String str2, int i, int i2, Size size, Rect rect, int i3, long j, int i4, int i5, int i6, int i7, int i8, boolean z, int i9) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        int i10 = i2;
        this.m_exportProjectDuration = i10;
        int encodeProjectInternal = encodeProjectInternal(str, str2, i, i10, size, rect, i3, j, i4, 1920, 1080, i7, DEFAULT_EXPORT_AUDIO_BITRATE, i5, i6, i8 == 0 ? DEFAULT_EXPORT_CODEC : i8, 0, z, i9);
        if (encodeProjectInternal != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectInternal));
            return task;
        }
        this.m_exportingTask = task;
        return task;
    }

    public void stop(OnCompletionListener onCompletionListener) {
        stop(2, onCompletionListener);
    }

    public void stop() {
        stop(new OnCompletionListener());
    }

    public Task export(String str, String str2, int i, int i2, int i3, int i4, int i5, long j, int i6, int i7, int i8, int i9, int i10, int i11, boolean z, int i12) {
        Task task = new Task();
        if (this.m_exportingTask != null) {
            task.sendFailure(ErrorCode.ALREADY_EXPORTING);
            return task;
        }
        int i13 = i2;
        this.m_exportProjectDuration = i13;
        int encodeProjectInternal = encodeProjectInternal(str, str2, i, i13, new Size(i3, i4), (Rect) null, i5, j, i9, 1920, 1080, i6, DEFAULT_EXPORT_AUDIO_BITRATE, i7, i8, i10, i11, z, i12);
        if (encodeProjectInternal != 0) {
            task.sendFailure(ErrorCode.fromValue(encodeProjectInternal));
            return task;
        }
        this.mExportPath = null;
        this.m_exportingTask = task;
        return task;
    }

    public Task loadClipsAsync(NexVisualClip[] nexVisualClipArr, NexAudioClip[] nexAudioClipArr) {
        return loadClipsAsync(nexVisualClipArr, nexAudioClipArr, 0);
    }

    public void onSetTimeDone(int i, int i2) {
        onSetTimeDone(i, i2, ErrorCode.NONE);
    }
}
