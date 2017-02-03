package com.lypeer.zybuluo.mixture.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugtags.library.Bugtags;
import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.mixture.core.HeadInfo;
import com.lypeer.zybuluo.mixture.core.HeadInfoManager;
import com.lypeer.zybuluo.mixture.core.MediaEditor;
import com.lypeer.zybuluo.mixture.core.MediaEditorTask;
import com.lypeer.zybuluo.mixture.core.MixtureKeys;
import com.lypeer.zybuluo.mixture.core.MixtureResult;
import com.lypeer.zybuluo.mixture.core.TextureRender;
import com.lypeer.zybuluo.mixture.core.VideoLoader;
import com.lypeer.zybuluo.mixture.util.FilePipelineHelper;
import com.lypeer.zybuluo.mixture.util.GLESUtil;
import com.lypeer.zybuluo.mixture.view.CircleProgressView;
import com.lypeer.zybuluo.mixture.view.WaveView;
import com.lypeer.zybuluo.model.bean.BodyBean;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.ui.activity.share.ShareActivity;
import com.lypeer.zybuluo.ui.custom.SubtitleView;
import com.lypeer.zybuluo.utils.ApiSignUtil;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.DeviceUuidFactory;
import com.lypeer.zybuluo.utils.FileUtil;
import com.lypeer.zybuluo.utils.RetrofitClient;
import com.lypeer.zybuluo.utils.SharePreferencesUtil;
import com.lypeer.zybuluo.utils.ZhugeUtil;
import com.lypeer.zybuluo.utils.meipai.MeiPai;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.squareup.picasso.Picasso;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import io.realm.Realm;
import jp.co.cyberagent.android.gpuimage.GPUImageNativeLibrary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GLSurfaceView.Renderer, MediaPlayer.OnCompletionListener, SurfaceTexture.OnFrameAvailableListener, VideoLoader.VideoDownloadCallback, MediaEditorTask.MediaEditorTaskCallback, Camera.PreviewCallback {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static boolean TEST = true;
    private final static String DOWNLOADED_VIDEO_PATH = FileUtil.getStorageDir() + "/bigbang.mp4";
    private final static String TEMP_VIDEO_PATH = FileUtil.getStorageDir() + "/~bigbang.mp4";
    private final static String PIPE_LINE_FILE_PATH = FileUtil.getStorageDir() + "bigbang.pipe";
    private final static int MAGIC_TEXTURE_ID = 10;

    private enum MixtureStage {Init, Training, RecordPrepare, RecordStart, RecordComplete, Preview}

    private GLSurfaceView mGLSurfaceView;
    private Button mStartButton;
    private Button mRedoButton;
    private Button mSaveButton;
    private RelativeLayout mSaveAndRedoLayout;
    private TextView mPrepareTextView;
    private ImageView mCloseImageView;
    private ImageView mIvSubtitle;
    private ImageView mIvCover;
    private TextView mTvTitle;
    private WaveView mWaveView;
    private CircleProgressView mProgressBar;
    private RelativeLayout mFrontLayout;
    private SubtitleView mSubtitleView;

    private TextureRender mRender;
    private SurfaceTexture mSurfaceTextureFromVideo;
    public SurfaceTexture mSurfaceTextureFromCamera;

    private MixtureStage mCurrentStage;

    private MediaPlayer mMediaPlayer;

    private Camera mCamera = null;

    private boolean mPaused;

    private VideoLoader mVideoLoader;

    private int mPreviewWidth;
    private int mPreviewHeight;
    private int mVideoWidth;
    private int mVideoHeight;

    private long mVideoDuration;

    private Bitmap mFilterBitmap = null;
    private Bitmap mPreviewBitmap = null;
    private Bitmap mBigHeadMask = null;
    private Bitmap mWatermark = null;
    private Bitmap mBigHead;
    private Bitmap mFirstFrame = null;
    public Paint mPaint;
    public Paint mBigheadPaint;
    private Paint mFirstFramePaint;

    private FileInputStream mPipeLineInput = null;
    private FileOutputStream mPipeLineOutput = null;

    private RecordStartCountDownTimer mRecordStartCountDownTimer = null;

    private HeadInfoManager mHeadInfoManager;

    private MediaEditorTask mEditTask;

    private IntBuffer mGLRgbBuffer;
    private ByteBuffer mBigheadBuffer;

    MixtureResult mMixtureResult;

    private String mVideoHttpUrl = "http://static.fibar.cn/laosiji.mp4";

    private String mDataHttpUrl = "http://dionysus.fibar.cn/api/v1/video/721?&timestamp=1473649587&sign=B280F6CC0C2D6F812133C6091B8BE57D&page=3&like=";

    private long mCameraPreviewTime = 0;

    private boolean mFirstTimeTraining = true;

    private CaptureThread mCaptureThread = null;

    private int mCurrentFrame = 0;

    private VideoResponse.BodyBean.VideoListBean mVideoBean;
    private ProgressDialog mProgressDialog;
    private String mFinalPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mixture_layout);

        Intent intent = getIntent();
        String videoHttpUrl = intent.getStringExtra(MixtureKeys.KEY_VIDEO_PATH);
        String dataHttpUrl = intent.getStringExtra(MixtureKeys.KEY_DATA_PATH);
        mVideoBean = (VideoResponse.BodyBean.VideoListBean) intent.getSerializableExtra(MixtureKeys.KEY_VIDEO);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.gl_mixture_surface);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(this);
        //mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mStartButton = (Button) findViewById(R.id.btn_mixture_start);
        mSaveAndRedoLayout = (RelativeLayout) findViewById(R.id.rv_mixture_save_and_redo);
        mRedoButton = (Button) findViewById(R.id.btn_mixture_redo);
        mSaveButton = (Button) findViewById(R.id.btn_mixture_save);
        mPrepareTextView = (TextView) findViewById(R.id.tv_mixture_prepare);
        mCloseImageView = (ImageView) findViewById(R.id.iv_mixture_close);
        mIvSubtitle = (ImageView) findViewById(R.id.iv_subtitle);
        mIvCover = (ImageView) findViewById(R.id.iv_cover);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mWaveView = (WaveView) findViewById(R.id.wv_mixture_wave);
        mProgressBar = (CircleProgressView) findViewById(R.id.lv_mixture_progress);
        mFrontLayout = (RelativeLayout) findViewById(R.id.rl_mixture_front);
        mSubtitleView = (SubtitleView) findViewById(R.id.sv_subtitle);

        mIvCover.bringToFront();
        mWaveView.bringToFront();
        mSubtitleView.bringToFront();
        mProgressBar.bringToFront();
        mProgressBar.setLlyContainerBg(R.color.colorEmpty);
        mProgressBar.setTextColor(R.color.colorWhite);
        Picasso.with(App.getAppContext()).load(mVideoBean.getThumb_nail()).fit().centerInside().into(mIvCover);

        mStartButton.setOnClickListener(this);
        mIvSubtitle.setOnClickListener(this);
        mRedoButton.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        mCloseImageView.setOnClickListener(this);
        mGLSurfaceView.setOnClickListener(this);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);

        mHeadInfoManager = new HeadInfoManager();

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDensity = getResources().getDisplayMetrics().densityDpi;
        opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
        mBigHeadMask = BitmapFactory.decodeResource(getResources(), R.raw.bigheadmask, opts);
        mBigHead = Bitmap.createBitmap(mBigHeadMask.getWidth(), mBigHeadMask.getHeight(), Bitmap.Config.ARGB_8888);
        mBigheadBuffer = ByteBuffer.allocate(mBigHeadMask.getWidth() * mBigHeadMask.getHeight() * 4);
        mWatermark = BitmapFactory.decodeResource(getResources(), R.raw.watermark, opts);

        mPaint = new Paint();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));//叠加重复的部分，显示下面的
        mFirstFramePaint = new Paint();
        mFirstFramePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        mBigheadPaint = new Paint();
        mSurfaceTextureFromCamera = new SurfaceTexture(MAGIC_TEXTURE_ID);

        mMixtureResult = new MixtureResult();
        if (TEST) {
            if (videoHttpUrl != null && !videoHttpUrl.isEmpty() && dataHttpUrl != null && !dataHttpUrl.isEmpty()) {
                mVideoHttpUrl = videoHttpUrl;
                mDataHttpUrl = dataHttpUrl;
            }
        } else {
            mVideoHttpUrl = videoHttpUrl;
            mDataHttpUrl = dataHttpUrl;
        }
        if (mVideoHttpUrl == null || mVideoHttpUrl.isEmpty() || mDataHttpUrl == null || mDataHttpUrl.isEmpty()) {
            mMixtureResult.state = MixtureResult.MixtureState.DOWNLOADERROR;
            backToNavActivity();
            return;
        }
        PackageManager pm = getPackageManager();
        boolean permission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission("android.permission.CAMERA", "packageName"));
        if (!permission) {
            mMixtureResult.state = MixtureResult.MixtureState.NOCAMERAPERMISSION;
            // backToNavActivity();
        }

        mCurrentStage = MixtureStage.Init;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ZhugeSDK.getInstance().init(getApplicationContext());
        Log.v(TAG, "onResume" + mCurrentStage);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras(); // get cameras number
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(camIdx);
                GLESUtil.setCameraDisplayOrientation(this, camIdx, mCamera);
            }
        }
        if (mCamera == null) {
            mMixtureResult.state = MixtureResult.MixtureState.NOCAMERA;
            backToNavActivity();
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> preSize = parameters.getSupportedPreviewSizes();
        mPreviewWidth = 0X0FFFFFFF;
        mPreviewHeight = 0X0FFFFFFF;
        for (int i = 0; i < preSize.size(); i++) {
            int tmpWidth = preSize.get(i).width;
            int tmpHeight = preSize.get(i).height;
            if (tmpWidth >= mBigHeadMask.getHeight() && tmpHeight >= mBigHeadMask.getWidth()) {
                if (tmpHeight < mPreviewWidth || tmpWidth < mPreviewHeight) {
                    mPreviewWidth = tmpWidth;
                    mPreviewHeight = tmpHeight;
                }
            }
        }
        mPreviewBitmap = Bitmap.createBitmap(mPreviewWidth, mPreviewHeight, Bitmap.Config.ARGB_8888);
        Log.v("MainActivity", "preview size" + mPreviewWidth + ", " + mPreviewHeight);
        parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
        Log.v(TAG, "max zoom size: " + parameters.getMaxZoom() + ", " + parameters.getZoom());
        if (parameters.isZoomSupported()) {
            parameters.setZoom((int) (parameters.getMaxZoom() * 0.08));
        }
        Log.v(TAG, "max zoom size: " + parameters.getMaxZoom());
        //parameters.setPreviewFpsRange(15, 30);
        mCamera.setParameters(parameters);
        int bufferSize = mPreviewWidth * mPreviewHeight;
        mGLRgbBuffer = IntBuffer.allocate(bufferSize);
        byte[] buffer = new byte[bufferSize * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8];
        mCamera.addCallbackBuffer(buffer);
        mCamera.setPreviewCallbackWithBuffer(this);
        try {
            mCamera.setPreviewTexture(mSurfaceTextureFromCamera);
        } catch (IOException e) {
            Toast.makeText(this, "camera open failed!", Toast.LENGTH_SHORT).show();
            mMixtureResult.state = MixtureResult.MixtureState.EXCEPTION;
            Bugtags.sendException(e);
            mMixtureResult.message = "camera preview error";
            backToNavActivity();
            return;
        }
        mCamera.startPreview();

        mCaptureThread = new CaptureThread();
        mCaptureThread.start();

        synchronized (this) {
            mPaused = false;
            if (mCurrentStage == MixtureStage.Init) {
                mStartButton.setVisibility(View.INVISIBLE);
                mSaveAndRedoLayout.setVisibility(View.INVISIBLE);
                mCloseImageView.setVisibility(View.INVISIBLE);
                mProgressBar.setProgress(0, "");
                mProgressBar.show();
                mProgressBar.setText("");
                mFrontLayout.setVisibility(View.VISIBLE);
                mFrontLayout.setBackgroundColor(Color.TRANSPARENT);
                mFrontLayout.setOnClickListener(null);
                mVideoLoader = new VideoLoader(mVideoHttpUrl, DOWNLOADED_VIDEO_PATH, mDataHttpUrl, mHeadInfoManager);
                mVideoLoader.setCallback(this);
                mVideoLoader.setProgressView(mProgressBar);
                mVideoLoader.execute();
            } else if (mCurrentStage == MixtureStage.Training || mCurrentStage == MixtureStage.Preview) {
                mMediaPlayer.start();
            } else {
                gotoStageTraining();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause" + mCurrentStage);
        if (mCaptureThread != null) {
            mCaptureThread.run = false;
            try {
                mCaptureThread.join();
                mCaptureThread = null;
            } catch (InterruptedException e) {
                //Log.e(TAG, e.getMessage());
                Bugtags.sendException(e);
            }
        }
        synchronized (this) {
            mPaused = true;
            mMediaPlayer.pause();
            if (mCurrentStage == MixtureStage.Preview || mCurrentStage == MixtureStage.Training) {
            } else if (mCurrentStage == MixtureStage.RecordPrepare) {
                mRecordStartCountDownTimer.cancelRecordStart();
            } else if (mCurrentStage == MixtureStage.RecordStart) {
                mEditTask.cancel(true);
                closePipeLine();
            }
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(DOWNLOADED_VIDEO_PATH);
        if (file.exists()) {
            file.delete();
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        synchronized (this) {
            if (v == mStartButton) {
                if (mCurrentStage == MixtureStage.Training) {
                    ZhugeUtil.upload("练习模式点击录制总量");
                    ZhugeUtil.upload("单个视频练习模式点击录制量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                    gotoStageRecordPrepare();
                } else {
                    ZhugeUtil.upload("开始录制后点击取消总量");
                    ZhugeUtil.upload("单个视频开始录制后点击取消量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                    if (mCurrentStage == MixtureStage.RecordPrepare) {
                        mRecordStartCountDownTimer.cancelRecordStart();
                        mRecordStartCountDownTimer = null;
                    } else if (mCurrentStage == MixtureStage.RecordStart) {
                        mEditTask.cancel(true);
                        closePipeLine();
                    }
                    gotoStageTraining();
                }
            } else if (v == mCloseImageView) {
                mMixtureResult.state = MixtureResult.MixtureState.CANCEL;

                if (mCurrentStage == MixtureStage.Training) {
                    ZhugeUtil.upload("练习模式点击退出总量");
                    ZhugeUtil.upload("单个视频练习模式点击退出量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                } else if (mCurrentStage == MixtureStage.Preview) {
                    ZhugeUtil.upload("预览模式点击退出总量");
                    ZhugeUtil.upload("单个视频预览模式点击退出量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                }

                backToNavActivity();
                return;
            } else if (v == mRedoButton) {
                ZhugeUtil.upload("录制完成后点击重录总量");
                ZhugeUtil.upload("单个视频录制完成后点击重录量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                File file = new File(TEMP_VIDEO_PATH);
                if (file.exists()) {
                    file.delete();
                }
                gotoStageTraining();
                mFinalPath = null;
            } else if (v == mSaveButton) {
                ZhugeUtil.upload("录制完成后点击保存总量");
                ZhugeUtil.upload("单个视频录制完成后点击保存量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");

                String timeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);
                File file = new File(TEMP_VIDEO_PATH);
                String newFilePath = FileUtil.getStorageDir() + "/" +
                        App.getAppContext().getString(R.string.datouxiu) + "_" +
                        mVideoBean.getId() + "_" +
                        mVideoBean.getTitle() + "_" +
                        mVideoBean.getAuthor() + "_" +
                        timeStamp + ".mp4";
                file.renameTo(new File(newFilePath));
                mMixtureResult.state = MixtureResult.MixtureState.SUCCESS;
                mMixtureResult.videoUrl = newFilePath;

                if (mFinalPath == null)
                    mFinalPath = newFilePath;

                FileUtil.saveToGallery(mFinalPath);

                share(mFinalPath, mVideoBean.getId());
                return;
            } else if (v == mGLSurfaceView) {
                if (mCurrentStage == MixtureStage.Training) {
                    if (mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                        mPaused = true;
                    } else {
                        mMediaPlayer.start();
                        mPaused = false;
                    }
                }
            } else if (v == mIvSubtitle) {
                ZhugeUtil.upload("单个视频字幕开关总点击量量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");

                if (!mSubtitleView.mEnable) {
                    Toast.makeText(MainActivity.this, R.string.error_no_subtitle, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mCurrentStage == MixtureStage.Training) {
                    if (mSubtitleView.getVisibility() == View.VISIBLE) {
                        mSubtitleView.setVisibility(View.INVISIBLE);
                        mIvSubtitle.setImageResource(R.drawable.ic_subtitle_close_practise);
                        SharePreferencesUtil.setIsUserLikeSubtitle(false);
                        ZhugeUtil.upload("练习模式字幕关闭状态总量");
                        ZhugeUtil.upload("练习模式单个视频字幕关闭状态量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                    } else {
                        mSubtitleView.setVisibility(View.VISIBLE);
                        mIvSubtitle.setImageResource(R.drawable.ic_subtitle_open_practise);
                        SharePreferencesUtil.setIsUserLikeSubtitle(true);
                        ZhugeUtil.upload("练习模式字幕打开状态总量");
                        ZhugeUtil.upload("练习模式单个视频字幕打开状态量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                    }
                } else if (mCurrentStage == MixtureStage.Preview) {
                    if (mSubtitleView.getVisibility() == View.VISIBLE) {
                        mSubtitleView.setVisibility(View.INVISIBLE);
                        mIvSubtitle.setImageResource(R.drawable.ic_subtitle_close_review);
                        SharePreferencesUtil.setIsUserLikeSubtitle(false);
                        ZhugeUtil.upload("录制模式字幕关闭状态总量");
                        ZhugeUtil.upload("录制模式单个视频字幕关闭状态量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                    } else {
                        mSubtitleView.setVisibility(View.VISIBLE);
                        mIvSubtitle.setImageResource(R.drawable.ic_subtitle_open_review);
                        SharePreferencesUtil.setIsUserLikeSubtitle(true);
                        ZhugeUtil.upload("录制模式字幕打开状态总量");
                        ZhugeUtil.upload("录制模式单个视频字幕打开状态量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                    }
                }
            }
        }
    }

    /**
     * 此方法已废弃，分享逻辑改为不提示直接先上传
     */
    @Deprecated
    private void showSaveDialog(final String path, final int id) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_prompt)
                .setMessage(R.string.message_save_success)
                .setPositiveButton(R.string.prompt_share, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        share(path, id);
                    }
                })
                .setNegativeButton(R.string.prompt_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void share(final String path, final int id) {
        Intent intent = new Intent(MainActivity.this, ShareActivity.class);
        intent.putExtra(ShareActivity.SHARE_KEY_PATH, path);
        intent.putExtra(ShareActivity.SHARE_KEY_ID, id);
        startActivity(intent);
        finish();
        /*mMediaPlayer.pause();
        mProgressDialog.show();
        mProgressDialog.setMessage(App.getAppContext().getString(R.string.prompt_saving));
        RetrofitClient.buildService(ApiService.class)
                .upload()
                .enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response == null ||
                                response.body() == null ||
                                response.body().getStatus() != Constants.StatusCode.STATUS_SUCCESS) {
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, App.getAppContext().getString(R.string.error_share_fail), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        upload(path, response.body().getBody().getToken(), id);
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });*/
    }

    /**
     * v2.0 改版，已弃用，改为在单独页面进行分享
     */
    @Deprecated
    private void upload(final String path, String uptoken, final int id) {
        DeviceUuidFactory factory = new DeviceUuidFactory(App.getAppContext());
        String uuid = factory.getDeviceUuid().toString();
        String time = String.valueOf(System.currentTimeMillis());

        Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
        UploadManager uploadManager = new UploadManager(config);
        try {

            uploadManager.put(path, ApiSignUtil.md5(uuid + time) + ".mp4", uptoken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info == null) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, App.getAppContext().getString(R.string.error_some_problem), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (info.isOK()) {
                        try {
                            Toast.makeText(MainActivity.this, App.getAppContext().getString(R.string.prompt_save_success), Toast.LENGTH_SHORT).show();
                            createLink(path, id, "static.fibar.cn/".concat(response.getString("key")));
                        } catch (JSONException e) {
                            //e.printStackTrace();
                            Bugtags.sendException(e);
                        }
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, info.error, Toast.LENGTH_SHORT).show();
                    }
                }

            }, new UploadOptions(null, null, false,
                    new UpProgressHandler() {
                        public void progress(String key, double percent) {
                            mProgressDialog.setMessage("保存中，当前进度为：" + (int) (percent * 100) + "%");
                        }
                    }, null));
        } catch (Exception e) {
            e.printStackTrace();
            Bugtags.sendException(e);
            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * v2.0 改版，已弃用，改为在单独页面进行分享
     */
    @Deprecated
    private void createLink(final String path, int id, final String url) {
        DeviceUuidFactory factory = new DeviceUuidFactory(App.getAppContext());
        String uuid = factory.getDeviceUuid().toString();

        if (TextUtils.isEmpty(uuid)) {
            mProgressDialog.dismiss();
            Toast.makeText(MainActivity.this, App.getAppContext().getString(R.string.error_uuid_null), Toast.LENGTH_SHORT).show();
            return;
        }

        String token = ApiSignUtil.md5(uuid.concat(path));

        RetrofitClient.buildService(ApiService.class)
                .createShareLink(url, uuid, id, token)
                .enqueue(new Callback<CreateShareLinkResponse>() {
                    @Override
                    public void onResponse(Call<CreateShareLinkResponse> call, Response<CreateShareLinkResponse> response) {
                        if (response == null || response.body() == null) {
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, App.getAppContext().getString(R.string.error_share_fail), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        CreateShareLinkResponse shareLinkResponse = response.body();

                        if (shareLinkResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            shareSuccess(shareLinkResponse, path);
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(MainActivity.this, App.getRes().getStringArray(R.array.status_error)[shareLinkResponse.getStatus()], Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CreateShareLinkResponse> call, Throwable t) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * v2.0 改版，已弃用，改为在单独页面进行分享
     */
    @Deprecated
    private void shareSuccess(CreateShareLinkResponse shareLinkResponse, String path) {
        mProgressDialog.dismiss();
        shareLinkResponse.getBody().setPath(path);
        insert(new BodyBean(shareLinkResponse.getBody()));
        showSharePanel(shareLinkResponse, path);
    }

    /**
     * v2.0 改版，已弃用，改为在单独页面进行分享
     */
    @Deprecated
    public void insert(final BodyBean bodyBean) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(bodyBean);
            }
        });
    }

    /**
     * v2.0 改版，已弃用，改为在单独页面进行分享
     */
    @Deprecated
    private void showSharePanel(final CreateShareLinkResponse response, final String videoUrl) {
        mMediaPlayer.pause();
        final OnekeyShare oks = new OnekeyShare();

        oks.setTitle(response.getBody().getWeibo_title());
        oks.setText(response.getBody().getWeibo_title());
        oks.setImageUrl(response.getBody().getThumb_nail());
        oks.setUrl(response.getBody().getUrl());

        Bitmap enableLogo = BitmapFactory.decodeResource(App.getAppContext().getResources(), R.drawable.ic_meipai);
        String label = "美拍";
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                MeiPai meiPai = new MeiPai(MainActivity.this);
                meiPai.share(videoUrl);
            }
        };
        oks.setCustomerLogo(enableLogo, label, listener);

        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if (platform.getName().equals(SinaWeibo.NAME)) {
                    oks.setTitle(response.getBody().getWeibo_title() + "\t\t" + "大头秀－分享-" + response.getBody().getUrl());
                    oks.setText(response.getBody().getWeibo_title() + "\t\t" + "大头秀－分享-" + response.getBody().getUrl());
                }
            }
        });

        oks.show(MainActivity.this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.v(TAG, "onCompletion" + mHeadInfoManager.maxFrame + ", " + mCurrentFrame);
        synchronized (this) {
            if (mCurrentStage == MixtureStage.Training || mCurrentStage == MixtureStage.Preview) {
                mCurrentFrame = 0;
                mMediaPlayer.start();
            } else if (mCurrentStage == MixtureStage.RecordStart) {
                gotoStageRecordComplete();
            }
        }
    }

    @Override
    public void onVideoDownload(boolean result) {
        Log.v(TAG, "onVideoDownload" + result);
        synchronized (this) {
            if (result == false) {
                mMixtureResult.state = MixtureResult.MixtureState.DOWNLOADERROR;
                backToNavActivity();
                return;
            }
            if (mCurrentStage != MixtureStage.Init) return;

            mSubtitleView.setData(mHeadInfoManager.getSubtitleInfoMap());
            if (mSubtitleView.mEnable) {
                mIvSubtitle.setImageResource(R.drawable.ic_subtitle_open_practise);

                if (!SharePreferencesUtil.isUserLikeSubtitle()) {
                    mSubtitleView.setVisibility(View.INVISIBLE);
                    mIvSubtitle.setImageResource(R.drawable.ic_subtitle_close_practise);
                    ZhugeUtil.upload("练习模式字幕关闭状态总量");
                    ZhugeUtil.upload("练习模式单个视频字幕关闭状态量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                } else {
                    ZhugeUtil.upload("练习模式字幕打开状态总量");
                    ZhugeUtil.upload("练习模式单个视频字幕打开状态量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
                }
            } else {
                mIvSubtitle.setImageResource(R.drawable.ic_subtitle_close_practise);
            }

            MediaMetadataRetriever m = new MediaMetadataRetriever();
            try {
                m.setDataSource(DOWNLOADED_VIDEO_PATH);
            } catch (Exception e) {
                mMixtureResult.state = MixtureResult.MixtureState.DOWNLOADERROR;
                backToNavActivity();
                return;
            }
            mFirstFrame = m.getFrameAtTime(mHeadInfoManager.getTimeByFrame(mHeadInfoManager.getPreparedFrame()) * 1000);
            mVideoWidth = Integer.parseInt(m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(m.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            m.release();
            mFilterBitmap = Bitmap.createBitmap(mVideoWidth, mVideoHeight, Bitmap.Config.ARGB_8888);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);

            RelativeLayout playArea = (RelativeLayout) findViewById(R.id.rv_mixture_play_area);
            ViewGroup.LayoutParams params = playArea.getLayoutParams();
            params.width = dm.widthPixels;
            params.height = dm.widthPixels * mVideoHeight / mVideoHeight;
            playArea.setLayoutParams(params);
            mVideoDuration = mWaveView.loadWaveFromFile(DOWNLOADED_VIDEO_PATH) * 1000;
            mWaveView.setPosition(0);
            mWaveView.invalidate();
            gotoStageTraining();
        }
    }

    @Override
    public void onMediaEditCompleted(boolean result) {
        Log.v(TAG, "onMediaEditCompleted" + result);
        synchronized (this) {
            if (result == false) {
                if (mCurrentStage == MixtureStage.RecordStart || mCurrentStage == MixtureStage.RecordComplete) {
                    mMixtureResult.message = "大头秀可能与您的手机不兼容，我们已收到反馈，工程师正在尽快修复，请关注新版本发布";
                    mMixtureResult.state = MixtureResult.MixtureState.EXCEPTION;
                    backToNavActivity();
                }
            } else if (mPaused != true){
                gotoStagePreview();
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        synchronized (mGLRgbBuffer) {
            GPUImageNativeLibrary.YUVtoRBGA(data, mPreviewWidth, mPreviewHeight,
                    mGLRgbBuffer.array());
        }
        camera.addCallbackBuffer(data);
        mGLSurfaceView.requestRender();
    }

    ////////////////////////////// GLSurfaceView callback ////////////////////////////////////////
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mRender = new TextureRender();
        mRender.surfaceCreated();
        mSurfaceTextureFromVideo = new SurfaceTexture(mRender.getTextureId());
        mMediaPlayer.setSurface(new Surface(mSurfaceTextureFromVideo));
        mSurfaceTextureFromVideo.setOnFrameAvailableListener(this);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        mRender.onOutputSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mSurfaceTextureFromVideo.updateTexImage();
        if (mFilterBitmap != null && mCurrentFrame > 0) {
            if (mCurrentStage == MixtureStage.Training
                    || mCurrentStage == MixtureStage.RecordStart
                    || mCurrentStage == MixtureStage.RecordPrepare) {
                HeadInfo headInfo = mHeadInfoManager.getHeadInfoByFrame(mCurrentFrame);
                if (headInfo != null && headInfo.size >= 5) {
                    synchronized (mGLRgbBuffer) {
                        mGLRgbBuffer.position(0);
                        mPreviewBitmap.copyPixelsFromBuffer(mGLRgbBuffer);
                    }
                    Matrix matrix = new Matrix();
                    matrix.postTranslate((mBigHead.getWidth() - mPreviewBitmap.getWidth()) / 2, (mBigHead.getHeight() - mPreviewBitmap.getHeight()) / 2);
                    matrix.postRotate(270, mBigHead.getWidth() / 2, mBigHead.getHeight() / 2);
                    matrix.postScale(-1, 1, mBigHead.getWidth() / 2, mBigHead.getHeight() / 2);
                    Canvas canvas = new Canvas(mBigHead);
                    synchronized (mBigHead) {
                        canvas.drawBitmap(mPreviewBitmap, matrix, null);
                        canvas.drawBitmap(mBigHeadMask, 0, 0, mPaint);
                    }
                    canvas = new Canvas(mFilterBitmap);
                    matrix = new Matrix();
                    float scale = (float) headInfo.size / mBigHead.getWidth();
                    matrix.postScale(scale, scale);
                    matrix.postTranslate((float) headInfo.x, (float) headInfo.y);
                    if (mHeadInfoManager.rotationOnTop) {
                        matrix.postRotate((float) headInfo.rotation, (float) headInfo.x + (float) headInfo.size / 2, (float) headInfo.y);
                    } else {
                        matrix.postRotate((float) headInfo.rotation, (float) headInfo.x + (float) headInfo.size / 2, (float) headInfo.y + (float) (headInfo.size * 1.33));
                    }
                    mFilterBitmap.eraseColor(Color.TRANSPARENT);
                    if (mFirstTimeTraining) {
                        long interval = System.currentTimeMillis() - mCameraPreviewTime;
                        if (interval > 500 && interval <= 1000) {
                            mBigheadPaint.setAlpha((int) ((interval - 500) * 255 / 500));
                        } else if (interval > 1000) {
                            mFirstTimeTraining = false;
                            mBigheadPaint.setAlpha(255);
                        } else {
                            mBigheadPaint.setAlpha(0);
                        }
                    } else {
                        mBigheadPaint.setAlpha(255);
                    }
                    canvas.drawBitmap(mBigHead, matrix, mBigheadPaint);
                    if (mCurrentStage == MixtureStage.RecordPrepare) {
                        canvas.drawBitmap(mFirstFrame, new Matrix(), mFirstFramePaint);
                    }
                } else {
                    mFilterBitmap.eraseColor(Color.TRANSPARENT);
                }
            } else if (mCurrentStage == MixtureStage.RecordComplete) {

            } else {
                mFilterBitmap.eraseColor(Color.TRANSPARENT);
            }
            mRender.drawFrame(mSurfaceTextureFromVideo, mFilterBitmap);
        }
    }

    /////////////////////////// surfacetexture callback from video //////////////////////////////
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        int frame = (mMediaPlayer.getCurrentPosition() * mHeadInfoManager.frameRate / 1000) + 1;
        mCurrentFrame = frame > mHeadInfoManager.maxFrame ? mHeadInfoManager.maxFrame : frame;
        if (mVideoDuration > 0) {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int pos = mCurrentFrame * WaveView.WAVE_COUNT / mHeadInfoManager.maxFrame;
                    mWaveView.setPosition(pos);
                    mWaveView.invalidate();

                    float currentTime = ((float) mCurrentFrame / mHeadInfoManager.maxFrame) * mHeadInfoManager.duration;
                    mSubtitleView.updateTime((long) currentTime + 500);
                }
            });
        }
    }

    private void gotoStageTraining() {
        mTvTitle.setText(R.string.title_practise);
        mCurrentStage = MixtureStage.Training;

        Log.v(TAG, "gotoStageTraining" + mCurrentStage);
        mCameraPreviewTime = System.currentTimeMillis();
        mProgressBar.dismiss();
        mIvCover.setVisibility(View.GONE);
        mPrepareTextView.setVisibility(View.INVISIBLE);
        mCloseImageView.setVisibility(View.VISIBLE);
        mSaveAndRedoLayout.setVisibility(View.INVISIBLE);
        mFrontLayout.setVisibility(View.GONE);
        mStartButton.setVisibility(View.VISIBLE);
        mStartButton.setText("开始");
        mStartButton.setBackgroundResource(R.drawable.bt_start_bk);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(DOWNLOADED_VIDEO_PATH);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Bugtags.sendException(e);
            e.printStackTrace();
        }
        mCurrentFrame = 0;
        mMediaPlayer.start();

        ZhugeUtil.upload("练习模式素材播放总量");
        ZhugeUtil.upload("练习模式单个素材播放量", "标题", mVideoBean.getTitle(), "id", mVideoBean.getId() + "");
    }

    private void gotoStageRecordPrepare() {
        mTvTitle.setText("");

        Log.v(TAG, "gotoStageRecordPrepare");
        mPaused = false;
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(DOWNLOADED_VIDEO_PATH);
            mMediaPlayer.prepare();
            mCurrentFrame = mHeadInfoManager.getPreparedFrame();
            mMediaPlayer.seekTo((int) (mHeadInfoManager.getTimeByFrame(mCurrentFrame) * 1000));
            Log.v(TAG, "gotoStageRecordPrepare " + mCurrentFrame);
        } catch (IOException e) {
            e.printStackTrace();
            Bugtags.sendException(e);
        }
        mCurrentStage = MixtureStage.RecordPrepare;
        //mMediaPlayer.seekTo(HeadInfoManager.getPreparedTime());
        mStartButton.setText("取消");
        mCloseImageView.setVisibility(View.GONE);
        mStartButton.setBackgroundResource(R.drawable.bt_cancel_bk);
        mRecordStartCountDownTimer = new RecordStartCountDownTimer();
        mRecordStartCountDownTimer.start();
    }

    private void gotoStageRecordStart() {
        mTvTitle.setText(R.string.title_recording);

        Log.v(TAG, "gotoStageRecordStart" + mCurrentStage);
        mHeadInfoManager.videoWidth = mVideoWidth;
        mHeadInfoManager.videoHeight = mVideoHeight;
        mHeadInfoManager.headWidth = mBigHeadMask.getWidth();
        mHeadInfoManager.headHeight = mBigHeadMask.getHeight();
        openPipeLine();
        MediaEditor editor = new MediaEditor(this, DOWNLOADED_VIDEO_PATH);
        editor.setBigHeadInfoControl(mHeadInfoManager);
        editor.setOutputPath(TEMP_VIDEO_PATH);
        editor.setPipeLineInput(mPipeLineInput);
        editor.setVideoDuration(mVideoDuration);
        editor.setWatermark(mWatermark);
        mEditTask = new MediaEditorTask(editor, mProgressBar);
        mEditTask.setCallback(this);
        mEditTask.execute();
        mMediaPlayer.start();
        mCurrentStage = MixtureStage.RecordStart;
    }

    private void gotoStageRecordComplete() {
        Log.v(TAG, "gotoStageRecordComplete" + mCurrentStage);
        mCurrentStage = MixtureStage.RecordComplete;
        try {
            mProgressBar.show();
            mProgressBar.setProgress(0, "");
            mProgressBar.setText("正在创建预览");
            mFrontLayout.setVisibility(View.VISIBLE);
            mFrontLayout.setBackgroundColor(Color.TRANSPARENT);
            mFrontLayout.setOnClickListener(null);
            FilePipelineHelper.writeLong(mPipeLineOutput, HeadInfoManager.MAX_RECORD_TIME);
        } catch (IOException e) {
            Log.v(TAG, e.getMessage());
            throw new RuntimeException("write complete failed!");
        }
    }

    private void gotoStagePreview() {
        mTvTitle.setText(R.string.title_preview);

        if (mSubtitleView.getVisibility() == View.VISIBLE) {
            mIvSubtitle.setImageResource(R.drawable.ic_subtitle_open_review);
        } else {
            mIvSubtitle.setImageResource(R.drawable.ic_subtitle_close_review);
        }

        Log.v(TAG, "gotoStagePreview" + mCurrentStage);
        mCurrentStage = MixtureStage.Preview;
        closePipeLine();
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(TEMP_VIDEO_PATH);
            mMediaPlayer.prepare();
            mCurrentFrame = 0;
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Bugtags.sendException(e);
        }
        mFrontLayout.setVisibility(View.GONE);
        mStartButton.setVisibility(View.INVISIBLE);
        mSaveAndRedoLayout.setVisibility(View.VISIBLE);
        mCloseImageView.setVisibility(View.VISIBLE);
        mProgressBar.dismiss();

    }

    private void openPipeLine() {
        try {
            File file = new File(PIPE_LINE_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
            mPipeLineOutput = new FileOutputStream(file);
            mPipeLineInput = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            Bugtags.sendException(e);
            throw new RuntimeException("open pipeline failed!");
        }
    }

    private void closePipeLine() {
        try {
            if (mPipeLineInput != null) {
                mPipeLineInput.close();
                mPipeLineInput = null;
            }
            if (mPipeLineOutput != null) {
                mPipeLineOutput.close();
                mPipeLineOutput = null;
            }
            File file = new File(PIPE_LINE_FILE_PATH);
            if (file.exists()) {
                file.delete();
            }
        } catch (IOException e) {
            Log.v(TAG, e.getMessage());
            Bugtags.sendException(e);
            throw new RuntimeException("close pipe line failed");
        }
    }

    private void backToNavActivity() {
        if (mMixtureResult.state == MixtureResult.MixtureState.EXCEPTION ||
                mMixtureResult.state == MixtureResult.MixtureState.DOWNLOADERROR ||
                mMixtureResult.state == MixtureResult.MixtureState.NOCAMERA) {
            String msg = "";
            if (mMixtureResult.state == MixtureResult.MixtureState.DOWNLOADERROR) {
                msg = "下载视频错误，请稍后重试";
            } else if (mMixtureResult.state == MixtureResult.MixtureState.NOCAMERA) {
                msg = "没有前置摄像头";
            } else if (mMixtureResult.state == MixtureResult.MixtureState.EXCEPTION) {
                msg = mMixtureResult.message;
            }
            if (msg == null || msg.isEmpty()) {
                msg = "未知错误";
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.finish();
                }
            }, 2000);
        } else {
            finish();
        }
    }

    protected class RecordStartCountDownTimer extends CountDownTimer {
        private boolean mRecordCancelled = false;

        public void cancelRecordStart() {
            mRecordCancelled = true;
            cancel();
        }

        public RecordStartCountDownTimer() {
            super(5000, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (millisUntilFinished / 1000 == 1) {
                if (mCurrentStage == MixtureStage.RecordPrepare && mRecordCancelled == false && mPaused != true) {
                    gotoStageRecordStart();
                }
            } else {
                if (mCurrentStage == MixtureStage.RecordPrepare && mRecordCancelled == false && mPaused != true) {
                    //mPrepareTextView.bringToFront();
                    mPrepareTextView.setText((millisUntilFinished - 1000) / 1000 + "");
                    final ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f, 0.5f, 0.1f, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(800);
                    final AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
                    alphaAnimation.setStartOffset(500);
                    alphaAnimation.setDuration(500);
                    AnimationSet as = new AnimationSet(true);
                    as.addAnimation(scaleAnimation);
                    as.addAnimation(alphaAnimation);
                    mPrepareTextView.setAnimation(as);
                    as.start();
                }
            }
        }

        @Override
        public void onFinish() {

        }
    }

    private class CaptureThread extends Thread {
        public boolean run = true;
        private static final int INTERVAL = 40;

        public void run() {
            while (run) {
                // 记录本次运算的开始时间，用于计算睡眠时间
                long startTime = System.currentTimeMillis();

                // 如果是训练模式、准备模式、开始模式就采集摄像头的数据
                if (mCurrentStage == MixtureStage.Training || mCurrentStage == MixtureStage.RecordStart ||
                        mCurrentStage == MixtureStage.RecordPrepare) {
                    HeadInfo headInfo = mHeadInfoManager.getHeadInfoByFrame(mCurrentFrame);
                    // 需要头像
                    if (headInfo != null && headInfo.size > 5) {
                        // create bithead

                        // write data to pipe if record start
                        synchronized (this) {
                            if (mCurrentStage == MixtureStage.RecordStart) {
                                try {
                                    FilePipelineHelper.writeLong(mPipeLineOutput, mHeadInfoManager.getTimeByFrame(mCurrentFrame) * 1000000);
                                    mBigheadBuffer.position(0);
                                    synchronized (mBigHead) {
                                        mBigHead.copyPixelsToBuffer(mBigheadBuffer);
                                    }
                                    FilePipelineHelper.writeBytes(mPipeLineOutput, mBigheadBuffer.array(), mBigheadBuffer.array().length);
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    Bugtags.sendException(e);
                                    //throw new RuntimeException("write pipeline failed");
                                }
                            }
                        }
                    }
                }

                // run once of interval
                long endTime = System.currentTimeMillis();
                //Log.v(TAG, "onFrameAvailable" + mCurrentTimestamp + ", " + startTime + ", " + endTime + ", " + (INTERVAL + startTime - endTime));
                if (endTime - startTime < INTERVAL) {
                    try {
                        sleep(INTERVAL + startTime - endTime);
                    } catch (InterruptedException e) {
                        Bugtags.sendException(e);
                        break;
                    }
                } else {
                    Log.e(TAG, "camera capture compute too slow!!!");
                }
            }
        }
    }
}
