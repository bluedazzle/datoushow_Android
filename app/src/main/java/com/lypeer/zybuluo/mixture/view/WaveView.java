package com.lypeer.zybuluo.mixture.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.lypeer.zybuluo.mixture.util.MediaEditorUtil;

import java.io.IOException;
import java.lang.annotation.Target;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Created by 游小光 on 2017/1/1.
 */

public class WaveView extends View {
    public final static int WAVE_COUNT = 120;

    private short[] mWave = new short[WAVE_COUNT];

    private Paint mPaint;

    private int mPosition = 0;

    private int mPlayedColor = 0xFFFF0F50;

    private int mNotPlayedColor = 0xFF5c5c5c;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(0xFFFF0F50);
    }

    public long loadWaveFromFile(String videoPath) {
        long duration = 0;
        MediaExtractor audioExtractor = null;
        MediaCodec audioDecoder = null;
        try {
            audioExtractor = MediaEditorUtil.createExtractor(videoPath);
            int audioInputTrack = MediaEditorUtil.getAndSelectAudioTrackIndex(audioExtractor);
            MediaFormat inputAudioFormat = audioExtractor.getTrackFormat(audioInputTrack);
            duration = inputAudioFormat.getLong(MediaFormat.KEY_DURATION);
            audioDecoder = MediaEditorUtil.createAudioDecoder(inputAudioFormat);
            ByteBuffer[] audioDecoderInputBuffers = audioDecoder.getInputBuffers();
            ByteBuffer[] audioDecoderOutputBuffers = audioDecoder.getOutputBuffers();

            boolean audioExtractorDone = false;
            MediaCodec.BufferInfo audioDecoderOutputBufferInfo = new MediaCodec.BufferInfo();
            Log.v("MainActivity", "loadWaveFromFile");
            ArrayList<short[]> pcms = new ArrayList<>();
            while (!audioExtractorDone) {
                int decoderInputBufferIndex = audioDecoder.dequeueInputBuffer(-1);
                if (decoderInputBufferIndex < 0) {
                    break;
                }
                ByteBuffer decoderInputBuffer = audioDecoderInputBuffers[decoderInputBufferIndex];
                int size = audioExtractor.readSampleData(decoderInputBuffer, 0);
                long presentationTime = audioExtractor.getSampleTime();
                if (size >= 0) {
                    audioDecoder.queueInputBuffer(
                            decoderInputBufferIndex,
                            0,
                            size,
                            presentationTime,
                            audioExtractor.getSampleFlags());
                    int decoderOutputBufferIndex = audioDecoder.dequeueOutputBuffer(audioDecoderOutputBufferInfo, MediaEditorUtil.TIMEOUT_USEC);
                    if (decoderOutputBufferIndex < 0) {
                        continue;
                    }
                    ByteBuffer decoderOutputBuffer;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        decoderOutputBuffer = audioDecoder.getOutputBuffer(decoderOutputBufferIndex);
                    } else {
                        decoderOutputBuffer = audioDecoderOutputBuffers[decoderOutputBufferIndex];
                    }
                    ShortBuffer samples = decoderOutputBuffer.order(ByteOrder.nativeOrder()).asShortBuffer();
                    int numChannels = inputAudioFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    short[] res = new short[samples.remaining() / numChannels];
                    for (int i = 0; i < res.length; ++i) {
                        res[i] = samples.get(i * numChannels);
                    }
                    pcms.add(res);
                    decoderOutputBuffer.clear();
                    audioDecoder.releaseOutputBuffer(decoderOutputBufferIndex, false);
                }
                audioExtractorDone = !audioExtractor.advance();
            }
            int total = 0;
            for (int i = 0; i < pcms.size(); i++)
                total += pcms.get(i).length;
            for (int i = 0; i < WAVE_COUNT; i++) {
                mWave[i] = 0;
            }

            int current = 0;
            for (int i = 0; i < pcms.size(); i++) {
                for (int j = 0; j < pcms.get(i).length; j ++) {
                    mWave[current * WAVE_COUNT / total] += pcms.get(i)[j];
                   current++;
                }
            }
            Log.v("WaveView", "loadWaveFromFile sample count" + current);
            for (int i = 0; i < WAVE_COUNT; i++) {
                mWave[i] /= (total / WAVE_COUNT);
            }

            return duration;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (audioDecoder != null) {
                audioDecoder.stop();
                audioDecoder.release();
            }
            if (audioExtractor != null) {
                audioExtractor.release();
            }
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        mPaint.setStrokeWidth((int) (width / WAVE_COUNT * 0.8));
        for (int i = 0; i < WAVE_COUNT; i++) {
            int x = i * width / WAVE_COUNT;
            int sy = height / 2 - mWave[i] * height / 32;
            int ey = height / 2 + mWave[i] * height / 32;
            if (sy < 0) sy = 0;
            if (ey > height) ey = height;
            if (i <= mPosition) {
                mPaint.setColor(mPlayedColor);
            } else {
                mPaint.setColor(mNotPlayedColor);
            }
            canvas.drawLine(x, sy, x, ey, mPaint);
        }
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public short[] byteArray2ShortArray(byte[] data, int items) {
        short[] retVal = new short[items];
        for (int i = 0; i < retVal.length; i++)
            retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
        return retVal;
    }
}
