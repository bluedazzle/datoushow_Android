package com.lypeer.zybuluo.mixture.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.lypeer.zybuluo.mixture.util.FilePipelineHelper;
import com.lypeer.zybuluo.mixture.util.MediaEditorUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

import static com.lypeer.zybuluo.mixture.util.MediaEditorUtil.TIMEOUT_USEC;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Created by 游小光 on 2016/12/14.
 */

public class MediaEditor {

    private static final String TAG = MediaEditor.class.getSimpleName();
    private static final boolean VERBOSE = false;

    private Context mContext;

    private String mInputVideoPath = null;
    private String mOutputVideoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/edit.mp4";

    // scale video size (percent)
    private float mScale = 1;

    /////////////////// parameters for the video encoder /////////////////////////////
    private String mVideoMimeType = "video/avc";      // H.264 Advanced Video Coding
    private int mVideoBitRate = 2000000;              // 2Mbps
    private int mVideoFrameRate = 25;                 // 15 frame per second
    private int mVideoIFrame = 10;                    // 10 seconds between I-frames
    private int mVideoColorFormat = MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface;

    //////////////////// parameters for the audio encoder ////////////////////////////
    private String mAudioMimeType = "audio/mp4a-latm"; // Advanced Audio Coding
    private int mAudioChannelCount = 2;                // Must match the input stream.
    private int mAudioBitRate = 128 * 1024;            //
    private int mAudioProfile = MediaCodecInfo.CodecProfileLevel.AACObjectHE;
    private int mAudioSampleRate = 44100;              // Must match the input stream.


    //////////////////// encoder, decoder ////////////////////////////////////////////
    private MediaExtractor mVideoExtractor = null;
    private MediaExtractor mAudioExtractor = null;
    private MediaCodec mVideoDecoder = null;
    private MediaCodec mAudioDecoder = null;
    private MediaCodec mVideoEncoder = null;
    private MediaCodec mAudioEncoder = null;
    private MediaMuxer mMuxer = null;
    private OutputSurface mVideoDecoderOutputSurface = null;
    private InputSurface mVideoEncoderinputSurface = null;

    private MediaFormat mInputVideoFormat;
    private MediaFormat mInputAudioFormat;


    //////////////////////////// video related ///////////////////////
    private ByteBuffer[] mVideoDecoderInputBuffers = null;
    private ByteBuffer[] mVideoDecoderOutputBuffers = null;
    private ByteBuffer[] mVideoEncoderOutputBuffers = null;
    private MediaCodec.BufferInfo mVideoDecoderOutputBufferInfo = null;
    private MediaCodec.BufferInfo mVideoEncoderOutputBufferInfo = null;

    ////////////////////////// audio buffer related ////////////////////
    private ByteBuffer[] mAudioDecoderInputBuffers = null;
    private ByteBuffer[] mAudioDecoderOutputBuffers = null;
    private ByteBuffer[] mAudioEncoderInputBuffers = null;
    private ByteBuffer[] mAudioEncoderOutputBuffers = null;
    private MediaCodec.BufferInfo mAudioDecoderOutputBufferInfo = null;
    private MediaCodec.BufferInfo mAudioEncoderOutputBufferInfo = null;

    private MediaEditorTask mMediaEditorTask;

    boolean muxing = false;

    private FileInputStream mPipeLineInput;

    private Bitmap mBigheadBitmap = null;
    private Bitmap mFilterBitmap = null;
    private long mTimeStamp = -1;

    private ByteBuffer mBigheadBuffer;

    private HeadInfoManager mHeadInfoManager;

    private long mVideoDuration;

    public MediaEditor(Context context, String videoPath) {
        mContext = context;
        mInputVideoPath = videoPath;
    }

    public void setup() throws IOException {
        Exception exception = null;

        MediaCodecInfo videoCodecInfo = MediaEditorUtil.selectCodec(mVideoMimeType);
        if (videoCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + mVideoMimeType);
            return;
        }
        if (VERBOSE) Log.d(TAG, "video found codec: " + videoCodecInfo.getName());

        MediaCodecInfo audioCodecInfo = MediaEditorUtil.selectCodec(mAudioMimeType);
        if (audioCodecInfo == null) {
            Log.e(TAG, "Unable to find an appropriate codec for " + mAudioMimeType);
            return;
        }
        if (VERBOSE) Log.d(TAG, "audio found codec: " + audioCodecInfo.getName());


        ///////////////////////////// video /////////////////////////////////////
        mVideoExtractor = MediaEditorUtil.createExtractor(mInputVideoPath);
        int videoInputTrack = MediaEditorUtil.getAndSelectVideoTrackIndex(mVideoExtractor);
        assertTrue("missing video track in test video", videoInputTrack != -1);
        mInputVideoFormat = mVideoExtractor.getTrackFormat(videoInputTrack);
        Log.v(TAG, "video input format" + mInputVideoFormat);
        if (mInputVideoFormat.containsKey("rotation-degrees")) {
            // Decoded video is rotated automatically in Android 5.0 lollipop.
            // Turn off here because we don't want to encode rotated one.
            // refer: https://android.googlesource.com/platform/frameworks/av/+blame/lollipop-release/media/libstagefright/Utils.cpp
            mInputVideoFormat.setInteger("rotation-degrees", 0);
        }
        // We avoid the device-specific limitations on width and height by using values
        // that are multiples of 16, which all tested devices seem to be able to handle.
        int width = mInputVideoFormat.getInteger(MediaFormat.KEY_WIDTH);
        int height = mInputVideoFormat.getInteger(MediaFormat.KEY_HEIGHT);


        MediaFormat outputVideoFormat =
                MediaFormat.createVideoFormat(mVideoMimeType, Math.round(width * mScale), Math.round(height * mScale));
        outputVideoFormat.setInteger(
                MediaFormat.KEY_COLOR_FORMAT, mVideoColorFormat);
        outputVideoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mVideoBitRate);
        outputVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mVideoFrameRate);
        outputVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, mVideoIFrame);
        //TODO... set profile level ... and....
        Log.v(TAG, "video output format: " + outputVideoFormat);

        // Create a MediaCodec for the desired codec, then configure it as an encoder with
        // our desired properties. Request a Surface to use for input.
        AtomicReference<Surface> inputSurfaceReference = new AtomicReference<Surface>();
        mVideoEncoder = MediaEditorUtil.createVideoEncoder(videoCodecInfo, outputVideoFormat, inputSurfaceReference);
        mVideoEncoderinputSurface = new InputSurface(inputSurfaceReference.get());
        mVideoEncoderinputSurface.makeCurrent();
        Log.v(TAG, "encoder created");

        // Create a MediaCodec for the decoder, based on the extractor's format.
        mVideoDecoderOutputSurface = new OutputSurface();
        mVideoDecoderOutputSurface.getRender().onOutputSizeChanged(mVideoEncoderinputSurface.getWidth(), mVideoEncoderinputSurface.getHeight());
        Log.v(TAG, "surface size: " + mVideoEncoderinputSurface.getWidth() + ", " + mVideoEncoderinputSurface.getHeight());
        mVideoDecoder = MediaEditorUtil.createVideoDecoder(mInputVideoFormat, mVideoDecoderOutputSurface.getSurface());
        Log.v(TAG, "decoder created");

        ///////////////////////////// audio //////////////////////////////
        mAudioExtractor = MediaEditorUtil.createExtractor(mInputVideoPath);
        int audioInputTrack = MediaEditorUtil.getAndSelectAudioTrackIndex(mAudioExtractor);
        assertTrue("missing audio track in test video", audioInputTrack != -1);
        mInputAudioFormat = mAudioExtractor.getTrackFormat(audioInputTrack);

        MediaFormat outputAudioFormat = MediaFormat.createAudioFormat(mAudioMimeType,
                mInputAudioFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE), mInputAudioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
        outputAudioFormat.setInteger(MediaFormat.KEY_BIT_RATE, mAudioBitRate);
        outputAudioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, mAudioProfile);

        // Create a MediaCodec for the desired codec, then configure it as an encoder with
        // our desired properties. Request a Surface to use for input.
        mAudioEncoder = MediaEditorUtil.createAudioEncoder(audioCodecInfo, outputAudioFormat);
        // Create a MediaCodec for the decoder, based on the extractor's format.
        mAudioDecoder = MediaEditorUtil.createAudioDecoder(mInputAudioFormat);

        ///////////////////////////// muxer //////////////////////////////////
        // Creates a muxer but do not start or add tracks just yet.
        mMuxer = MediaEditorUtil.createMuxer(mOutputVideoPath);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(mInputVideoPath);
        String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        if (!rotation.isEmpty()) {
            mMuxer.setOrientationHint(Integer.parseInt(rotation));
        }
        mediaMetadataRetriever.release();
    }

    public void start() throws Exception {
        mVideoDecoderInputBuffers = mVideoDecoder.getInputBuffers();
        mVideoDecoderOutputBuffers = mVideoDecoder.getOutputBuffers();
        mVideoEncoderOutputBuffers = mVideoEncoder.getOutputBuffers();
        mVideoDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
        mVideoEncoderOutputBufferInfo = new MediaCodec.BufferInfo();

        mAudioDecoderInputBuffers = mAudioDecoder.getInputBuffers();
        mAudioDecoderOutputBuffers = mAudioDecoder.getOutputBuffers();
        mAudioEncoderInputBuffers = mAudioEncoder.getInputBuffers();
        mAudioEncoderOutputBuffers = mAudioEncoder.getOutputBuffers();
        mAudioDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
        mAudioEncoderOutputBufferInfo = new MediaCodec.BufferInfo();

        // We will get these from the decoders when notified of a format change.
        MediaFormat decoderOutputVideoFormat = null;
        MediaFormat decoderOutputAudioFormat = null;
        // We will get these from the encoders when notified of a format change.
        MediaFormat encoderOutputVideoFormat = null;
        MediaFormat encoderOutputAudioFormat = null;
        // We will determine these once we have the output format.
        int outputVideoTrack = -1;
        int outputAudioTrack = -1;
        // Whether things are done on the video side.
        boolean videoExtractorDone = false;
        boolean videoDecoderDone = false;
        boolean videoEncoderDone = false;
        // Whether things are done on the audio side.
        boolean audioExtractorDone = false;
        boolean audioDecoderDone = false;
        boolean audioEncoderDone = false;
        // The audio decoder output buffer to process, -1 if none.
        int pendingAudioDecoderOutputBufferIndex = -1;

        int videoExtractedFrameCount = 0;
        int videoDecodedFrameCount = 0;
        int videoEncodedFrameCount = 0;

        int audioExtractedFrameCount = 0;
        int audioDecodedFrameCount = 0;
        int audioEncodedFrameCount = 0;

        while (!videoEncoderDone || !audioEncoderDone) {
            if (mMediaEditorTask.isCancelled()) {
                Log.v(TAG, "media edit cancelled!");
                throw new Exception("media editor canceled!");
            }
            if (VERBOSE) {
                Log.d(TAG, String.format(
                        "loop: "
                                + "extracted:%d(done:%b) "
                                + "decoded:%d(done:%b) "
                                + "encoded:%d(done:%b)} "

                                + "extracted:%d(done:%b) "
                                + "decoded:%d(done:%b) "
                                + "encoded:%d(done:%b) "
                                + "pending:%d} "

                                + "muxing:%b(V:%d,A:%d)",

                        videoExtractedFrameCount, videoExtractorDone,
                        videoDecodedFrameCount, videoDecoderDone,
                        videoEncodedFrameCount, videoEncoderDone,

                        audioExtractedFrameCount, audioExtractorDone,
                        audioDecodedFrameCount, audioDecoderDone,
                        audioEncodedFrameCount, audioEncoderDone,
                        pendingAudioDecoderOutputBufferIndex,

                        muxing, outputVideoTrack, outputAudioTrack));
            }

            // Extract video from file and feed to decoder.
            // Do not extract video if we have determined the output format but we are not yet
            // ready to mux the frames.
            while (!videoExtractorDone && (encoderOutputVideoFormat == null || muxing)) {
                int decoderInputBufferIndex = mVideoDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no video decoder input buffer");
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "video decoder: returned input buffer: " + decoderInputBufferIndex);
                }
                ByteBuffer decoderInputBuffer = mVideoDecoderInputBuffers[decoderInputBufferIndex];
                int size = mVideoExtractor.readSampleData(decoderInputBuffer, 0);
                long presentationTime = mVideoExtractor.getSampleTime();
                if (VERBOSE) {
                    Log.d(TAG, "video extractor: returned buffer of size " + size);
                    Log.d(TAG, "video extractor: returned buffer for time " + presentationTime);
                }
                if (size >= 0) {
                    mVideoDecoder.queueInputBuffer(
                            decoderInputBufferIndex,
                            0,
                            size,
                            presentationTime,
                            mVideoExtractor.getSampleFlags());
                }
                videoExtractorDone = !mVideoExtractor.advance();
                if (videoExtractorDone) {
                    if (VERBOSE) Log.d(TAG, "video extractor: EOS");
                    mVideoDecoder.queueInputBuffer(
                            decoderInputBufferIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }
                videoExtractedFrameCount++;
                // We extracted a frame, let's try something else next.
                break;
            }

            // Extract audio from file and feed to decoder.
            // Do not extract audio if we have determined the output format but we are not yet
            // ready to mux the frames.
            while (!audioExtractorDone && (encoderOutputAudioFormat == null || muxing)) {
                int decoderInputBufferIndex = mAudioDecoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (decoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no audio decoder input buffer");
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: returned input buffer: " + decoderInputBufferIndex);
                }
                ByteBuffer decoderInputBuffer = mAudioDecoderInputBuffers[decoderInputBufferIndex];
                int size = mAudioExtractor.readSampleData(decoderInputBuffer, 0);
                long presentationTime = mAudioExtractor.getSampleTime();
                if (VERBOSE) {
                    Log.d(TAG, "audio extractor: returned buffer of size " + size);
                    Log.d(TAG, "audio extractor: returned buffer for time " + presentationTime);
                }
                if (size >= 0) {
                    mAudioDecoder.queueInputBuffer(
                            decoderInputBufferIndex,
                            0,
                            size,
                            presentationTime,
                            mAudioExtractor.getSampleFlags());
                }
                audioExtractorDone = !mAudioExtractor.advance();
                if (audioExtractorDone) {
                    if (VERBOSE) Log.d(TAG, "audio extractor: EOS");
                    mAudioDecoder.queueInputBuffer(
                            decoderInputBufferIndex,
                            0,
                            0,
                            0,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }
                audioExtractedFrameCount++;
                // We extracted a frame, let's try something else next.
                break;
            }

            // Poll output frames from the video decoder and feed the encoder.
            while (!videoDecoderDone && (encoderOutputVideoFormat == null || muxing)) {
                int decoderOutputBufferIndex =
                        mVideoDecoder.dequeueOutputBuffer(
                                mVideoDecoderOutputBufferInfo, TIMEOUT_USEC);
                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no video decoder output buffer");
                    break;
                }
                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    if (VERBOSE) Log.d(TAG, "video decoder: output buffers changed");
                    mVideoDecoderOutputBuffers = mVideoDecoder.getOutputBuffers();
                    break;
                }
                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    decoderOutputVideoFormat = mVideoDecoder.getOutputFormat();
                    if (VERBOSE) {
                        Log.d(TAG, "video decoder: output format changed: "
                                + decoderOutputVideoFormat);
                    }
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "video decoder: returned output buffer: "
                            + decoderOutputBufferIndex);
                    Log.d(TAG, "video decoder: returned buffer of size "
                            + mVideoDecoderOutputBufferInfo.size);
                }
                ByteBuffer decoderOutputBuffer =
                        mVideoDecoderOutputBuffers[decoderOutputBufferIndex];
                if ((mVideoDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
                        != 0) {
                    if (VERBOSE) Log.d(TAG, "video decoder: codec config buffer");
                    mVideoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "video decoder: returned buffer for time "
                            + mVideoDecoderOutputBufferInfo.presentationTimeUs);
                }
                boolean render = mVideoDecoderOutputBufferInfo.size != 0;
                mVideoDecoder.releaseOutputBuffer(decoderOutputBufferIndex, render);
                if (render) {
                    if (VERBOSE) Log.d(TAG, "output surface: await new image");
                    mVideoDecoderOutputSurface.awaitNewImage();
                    // Edit the frame and send it to the encoder.
                    if (VERBOSE) Log.d(TAG, "output surface: draw image");
                    ////////////// read sample time ///////////////////////
                    long currentTimeStamp = mVideoDecoderOutputBufferInfo.presentationTimeUs * 1000;
                    Log.v(TAG, "++++++ " + currentTimeStamp + ", " + mVideoDuration);
                    HeadInfo headInfo = mHeadInfoManager.getTrackInfoByTime(currentTimeStamp);
                    if (headInfo.size <= 5) {
                        mFilterBitmap.eraseColor(Color.TRANSPARENT);
                    } else {
                        if (mTimeStamp < currentTimeStamp) {
                            while (mTimeStamp < currentTimeStamp) {
                                if (mMediaEditorTask.isCancelled()) {
                                    throw new Exception("canceled!");
                                }
                                while (mMediaEditorTask.isCancelled() != true) {
                                    mTimeStamp = FilePipelineHelper.readLong(mPipeLineInput);
                                    if (mTimeStamp >= 0) break;
                                    Thread.sleep(100);
                                }
                                if (mTimeStamp == HeadInfoManager.MAX_RECORD_TIME) {
                                    break;
                                }
                                while (mMediaEditorTask.isCancelled() != true) {
                                    boolean flag = FilePipelineHelper.readBytes(mPipeLineInput, mBigheadBuffer.array(), mBigheadBuffer.array().length);
                                    if (flag) break;
                                    Thread.sleep(100);
                                }
                            }
                        }
                        mBigheadBuffer.position(0);
                        mBigheadBitmap.copyPixelsFromBuffer(mBigheadBuffer);
                        Canvas canvas = new Canvas(mFilterBitmap);
                        Matrix matrix = new Matrix();
                        float scale = (float) headInfo.size / mBigheadBitmap.getWidth();
                        matrix.postScale(scale, scale);
                        matrix.postTranslate((float) headInfo.x, (float) headInfo.y);
                        if (mHeadInfoManager.rotationOnTop) {
                            matrix.postRotate((float) headInfo.rotation, (float) headInfo.x + (float)headInfo.size / 2, (float) headInfo.y);
                        }
                        else {
                            matrix.postRotate((float) headInfo.rotation, (float) headInfo.x +  (float)headInfo.size / 2, (float) headInfo.y + (float)(headInfo.size * 1.33));
                        }
                        mFilterBitmap.eraseColor(Color.TRANSPARENT);
                        canvas.drawBitmap(mBigheadBitmap, matrix, null);
                    }
                    mVideoDecoderOutputSurface.drawImage(mFilterBitmap);
                    mVideoEncoderinputSurface.setPresentationTime(
                            mVideoDecoderOutputBufferInfo.presentationTimeUs * 1000);
                    if (VERBOSE) Log.d(TAG, "input surface: swap buffers");
                    mVideoEncoderinputSurface.swapBuffers();
                    if (VERBOSE) Log.d(TAG, "video encoder: notified of new frame");
                }
                if ((mVideoDecoderOutputBufferInfo.flags
                        & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (VERBOSE) Log.d(TAG, "video decoder: EOS");
                    videoDecoderDone = true;
                    mVideoEncoder.signalEndOfInputStream();
                }
                videoDecodedFrameCount++;
                // We extracted a pending frame, let's try something else next.
                break;
            }

            // Poll output frames from the audio decoder.
            // Do not poll if we already have a pending buffer to feed to the encoder.
            while (!audioDecoderDone && pendingAudioDecoderOutputBufferIndex == -1
                    && (encoderOutputAudioFormat == null || muxing)) {
                int decoderOutputBufferIndex =
                        mAudioDecoder.dequeueOutputBuffer(
                                mAudioDecoderOutputBufferInfo, TIMEOUT_USEC);
                if (decoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no audio decoder output buffer");
                    break;
                }
                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    if (VERBOSE) Log.d(TAG, "audio decoder: output buffers changed");
                    mAudioDecoderOutputBuffers = mAudioDecoder.getOutputBuffers();
                    break;
                }
                if (decoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    decoderOutputAudioFormat = mAudioDecoder.getOutputFormat();
                    if (VERBOSE) {
                        Log.d(TAG, "audio decoder: output format changed: "
                                + decoderOutputAudioFormat);
                    }
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: returned output buffer: "
                            + decoderOutputBufferIndex);
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: returned buffer of size "
                            + mAudioDecoderOutputBufferInfo.size);
                }
                ByteBuffer decoderOutputBuffer =
                        mAudioDecoderOutputBuffers[decoderOutputBufferIndex];
                if ((mAudioDecoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
                        != 0) {
                    if (VERBOSE) Log.d(TAG, "audio decoder: codec config buffer");
                    mAudioDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: returned buffer for time "
                            + mAudioDecoderOutputBufferInfo.presentationTimeUs);
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: output buffer is now pending: "
                            + pendingAudioDecoderOutputBufferIndex);
                }
                pendingAudioDecoderOutputBufferIndex = decoderOutputBufferIndex;
                audioDecodedFrameCount++;
                // We extracted a pending frame, let's try something else next.
                break;
            }

            // Feed the pending decoded audio buffer to the audio encoder.
            while (pendingAudioDecoderOutputBufferIndex != -1) {
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: attempting to process pending buffer: "
                            + pendingAudioDecoderOutputBufferIndex);
                }
                int encoderInputBufferIndex = mAudioEncoder.dequeueInputBuffer(TIMEOUT_USEC);
                if (encoderInputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no audio encoder input buffer");
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio encoder: returned input buffer: " + encoderInputBufferIndex);
                }
                ByteBuffer encoderInputBuffer = mAudioEncoderInputBuffers[encoderInputBufferIndex];
                int size = mAudioDecoderOutputBufferInfo.size;
                long presentationTime = mAudioDecoderOutputBufferInfo.presentationTimeUs;
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: processing pending buffer: "
                            + pendingAudioDecoderOutputBufferIndex);
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio decoder: pending buffer of size " + size);
                    Log.d(TAG, "audio decoder: pending buffer for time " + presentationTime);
                }
                if (size >= 0) {
                    ByteBuffer decoderOutputBuffer =
                            mAudioDecoderOutputBuffers[pendingAudioDecoderOutputBufferIndex]
                                    .duplicate();
                    decoderOutputBuffer.position(mAudioDecoderOutputBufferInfo.offset);
                    decoderOutputBuffer.limit(mAudioDecoderOutputBufferInfo.offset + size);
                    encoderInputBuffer.position(0);
                    encoderInputBuffer.put(decoderOutputBuffer);

                    mAudioEncoder.queueInputBuffer(
                            encoderInputBufferIndex,
                            0,
                            size,
                            presentationTime,
                            mAudioDecoderOutputBufferInfo.flags);
                }
                mAudioDecoder.releaseOutputBuffer(pendingAudioDecoderOutputBufferIndex, false);
                pendingAudioDecoderOutputBufferIndex = -1;
                if ((mAudioDecoderOutputBufferInfo.flags
                        & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (VERBOSE) Log.d(TAG, "audio decoder: EOS");
                    audioDecoderDone = true;
                }
                // We enqueued a pending frame, let's try something else next.
                break;
            }

            // Poll frames from the video encoder and send them to the muxer.
            while (!videoEncoderDone && (encoderOutputVideoFormat == null || muxing)) {
                int encoderOutputBufferIndex = mVideoEncoder.dequeueOutputBuffer(
                        mVideoEncoderOutputBufferInfo, TIMEOUT_USEC);
                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no video encoder output buffer");
                    break;
                }
                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    if (VERBOSE) Log.d(TAG, "video encoder: output buffers changed");
                    mVideoEncoderOutputBuffers = mVideoEncoder.getOutputBuffers();
                    break;
                }
                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (VERBOSE) Log.d(TAG, "video encoder: output format changed");
                    if (outputVideoTrack >= 0) {
                        fail("video encoder changed its output format again?");
                    }
                    encoderOutputVideoFormat = mVideoEncoder.getOutputFormat();
                    break;
                }
                assertTrue("should have added track before processing output", muxing);
                if (VERBOSE) {
                    Log.d(TAG, "video encoder: returned output buffer: "
                            + encoderOutputBufferIndex);
                    Log.d(TAG, "video encoder: returned buffer of size "
                            + mVideoEncoderOutputBufferInfo.size);
                }
                ByteBuffer encoderOutputBuffer =
                        mVideoEncoderOutputBuffers[encoderOutputBufferIndex];
                if ((mVideoEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
                        != 0) {
                    if (VERBOSE) Log.d(TAG, "video encoder: codec config buffer");
                    // Simply ignore codec config buffers.
                    mVideoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "video encoder: returned buffer for time "
                            + mVideoEncoderOutputBufferInfo.presentationTimeUs);
                }
                if (mVideoEncoderOutputBufferInfo.size != 0) {
                    mMuxer.writeSampleData(
                            outputVideoTrack, encoderOutputBuffer, mVideoEncoderOutputBufferInfo);
                }
                if ((mVideoEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        != 0) {
                    if (VERBOSE) Log.d(TAG, "video encoder: EOS");
                    videoEncoderDone = true;
                }
                mVideoEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                videoEncodedFrameCount++;
                // We enqueued an encoded frame, let's try something else next.
                break;
            }

            // Poll frames from the audio encoder and send them to the muxer.
            while (!audioEncoderDone
                    && (encoderOutputAudioFormat == null || muxing)) {
                int encoderOutputBufferIndex = mAudioEncoder.dequeueOutputBuffer(
                        mAudioEncoderOutputBufferInfo, TIMEOUT_USEC);
                if (encoderOutputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    if (VERBOSE) Log.d(TAG, "no audio encoder output buffer");
                    break;
                }
                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    if (VERBOSE) Log.d(TAG, "audio encoder: output buffers changed");
                    mAudioEncoderOutputBuffers = mAudioEncoder.getOutputBuffers();
                    break;
                }
                if (encoderOutputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    if (VERBOSE) Log.d(TAG, "audio encoder: output format changed");
                    if (outputAudioTrack >= 0) {
                        fail("audio encoder changed its output format again?");
                    }

                    encoderOutputAudioFormat = mAudioEncoder.getOutputFormat();
                    break;
                }
                assertTrue("should have added track before processing output", muxing);
                if (VERBOSE) {
                    Log.d(TAG, "audio encoder: returned output buffer: "
                            + encoderOutputBufferIndex);
                    Log.d(TAG, "audio encoder: returned buffer of size "
                            + mAudioEncoderOutputBufferInfo.size);
                }
                ByteBuffer encoderOutputBuffer =
                        mAudioEncoderOutputBuffers[encoderOutputBufferIndex];
                if ((mAudioEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG)
                        != 0) {
                    if (VERBOSE) Log.d(TAG, "audio encoder: codec config buffer");
                    // Simply ignore codec config buffers.
                    mAudioEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                    break;
                }
                if (VERBOSE) {
                    Log.d(TAG, "audio encoder: returned buffer for time "
                            + mAudioEncoderOutputBufferInfo.presentationTimeUs);
                }
                if (mAudioEncoderOutputBufferInfo.size != 0) {
                    mMuxer.writeSampleData(
                            outputAudioTrack, encoderOutputBuffer, mAudioEncoderOutputBufferInfo);
                }
                if ((mAudioEncoderOutputBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        != 0) {
                    if (VERBOSE) Log.d(TAG, "audio encoder: EOS");
                    audioEncoderDone = true;
                }
                mAudioEncoder.releaseOutputBuffer(encoderOutputBufferIndex, false);
                audioEncodedFrameCount++;
                // We enqueued an encoded frame, let's try something else next.
                break;
            }

            if (!muxing && (encoderOutputAudioFormat != null) && (encoderOutputVideoFormat != null)) {
                Log.d(TAG, "muxer: adding video track.");
                outputVideoTrack = mMuxer.addTrack(encoderOutputVideoFormat);
                Log.d(TAG, "muxer: adding audio track.");
                outputAudioTrack = mMuxer.addTrack(encoderOutputAudioFormat);
                Log.d(TAG, "muxer: starting");
                mMuxer.start();
                muxing = true;
            }
            mMediaEditorTask.changeProgress(videoDecodedFrameCount * 95 / mHeadInfoManager.maxFrame);
            Log.v(TAG, "changeProgress " + System.currentTimeMillis());
        }
        Log.v(TAG, "changeProgress 90 " + System.currentTimeMillis());
        assertEquals("encoded and decoded video frame counts should match",
                videoDecodedFrameCount, videoEncodedFrameCount);

        assertTrue("decoded frame count should be less than extracted frame count",
                videoDecodedFrameCount <= videoExtractedFrameCount);

        assertEquals("no frame should be pending", -1, pendingAudioDecoderOutputBufferIndex);

    }


    public void release() throws Exception {
        Exception exception = null;
        try {
            if (mVideoExtractor != null) {
                mVideoExtractor.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing videoExtractor", e);
            if (exception == null) {
                exception = e;
            }
        }
        try {
            if (mAudioExtractor != null) {
                mAudioExtractor.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing audioExtractor", e);
            if (exception == null) {
                exception = e;
            }
        }
        mMediaEditorTask.changeProgress(97);
        Log.v(TAG, "changeProgress 92 " + System.currentTimeMillis());
        try {
            if (mVideoDecoder != null) {
                mVideoDecoder.stop();
                mVideoDecoder.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing videoDecoder", e);
            if (exception == null) {
                exception = e;
            }
        }
        try {
            if (mVideoDecoderOutputSurface != null) {
                mVideoDecoderOutputSurface.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing outputSurface", e);
            if (exception == null) {
                exception = e;
            }
        }
        mMediaEditorTask.changeProgress(98);
        Log.v(TAG, "changeProgress 95 " + System.currentTimeMillis());
        try {
            if (mVideoEncoder != null) {
                mVideoEncoder.stop();
                mVideoEncoder.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing videoEncoder", e);
            if (exception == null) {
                exception = e;
            }
        }
        try {
            if (mAudioDecoder != null) {
                mAudioDecoder.stop();
                mAudioDecoder.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing audioDecoder", e);
            if (exception == null) {
                exception = e;
            }
        }
        try {
            if (mAudioEncoder != null) {
                mAudioEncoder.stop();
                mAudioEncoder.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing audioEncoder", e);
            if (exception == null) {
                exception = e;
            }
        }
        try {
            if (mMuxer != null) {
                if (muxing) {
                    mMuxer.stop();
                }
                mMuxer.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing muxer", e);
            if (exception == null) {
                exception = e;
            }
        }
        mMediaEditorTask.changeProgress(99);
        Log.v(TAG, "changeProgress 98 " + System.currentTimeMillis());
        try {
            if (mVideoEncoderinputSurface != null) {
                mVideoEncoderinputSurface.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while releasing inputSurface", e);
            if (exception == null) {
                exception = e;
            }
        }

        if (exception != null) {
            throw exception;
        }
        mMediaEditorTask.changeProgress(100);
        Log.v(TAG, "changeProgress 99 " + System.currentTimeMillis());
    }

    /**
     * @param headInfoManager
     */
    public void setBigHeadInfoControl(HeadInfoManager headInfoManager) {
        mHeadInfoManager = headInfoManager;
        mBigheadBitmap = Bitmap.createBitmap(mHeadInfoManager.headWidth, mHeadInfoManager.headHeight, Bitmap.Config.ARGB_8888);
        mFilterBitmap = Bitmap.createBitmap(mHeadInfoManager.videoWidth, mHeadInfoManager.videoHeight, Bitmap.Config.ARGB_8888);
        mBigheadBuffer = ByteBuffer.allocate(mHeadInfoManager.headWidth * mHeadInfoManager.headHeight * 4);
    }

    /**
     * @param bitRate output video bitrate
     */
    public void setBitRate(int bitRate) {
        mVideoBitRate = bitRate;
    }

    /**
     * @param scale scale video frame size(both width and height) default is 1, no scale
     */
    public void setScale(float scale) {
        mScale = scale;
    }

    /**
     * @param path outpout path
     */
    public void setOutputPath(String path) {
        mOutputVideoPath = path;
    }

    public String getOutputPath() {
        return mOutputVideoPath;
    }

    public void setAsyncTask(MediaEditorTask mediaEditorTask) {
        mMediaEditorTask = mediaEditorTask;
    }

    public void setPipeLineInput(FileInputStream pipeLineInput) {
        mPipeLineInput = pipeLineInput;
    }

    public void setVideoDuration(long videoDuration) {
        mVideoDuration = videoDuration;
    }
}