package com.lypeer.zybuluo.mixture.util;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by 游小光 on 2016/12/14.
 */

public class MediaEditorUtil {

    private static final String TAG = MediaEditorUtil.class.getSimpleName();

    /**
     * How long to wait for the next buffer to become available.
     */
    public static final int TIMEOUT_USEC = 10000;

    /**
     * Creates an extractor that reads its frames from input video.
     */
    public static MediaExtractor createExtractor(String inputVideoPath) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(inputVideoPath);
        return extractor;
    }

    /**
     * Creates a decoder for the given format, which outputs to the given surface.
     *
     * @param inputFormat the format of the stream to decode
     * @param surface     into which to decode the frames
     */
    public static MediaCodec createVideoDecoder(MediaFormat inputFormat, Surface surface) throws IOException {
        MediaCodec decoder = MediaCodec.createDecoderByType(MediaEditorUtil.getMimeTypeFor(inputFormat));
        decoder.configure(inputFormat, surface, null, 0);
        decoder.start();
        return decoder;
    }

    /**
     * Creates an encoder for the given format using the specified codec, taking input from a
     * surface.
     * <p>
     * <p>The surface to use as input is stored in the given reference.
     *
     * @param codecInfo        of the codec to use
     * @param format           of the stream to be produced
     * @param surfaceReference to store the surface to use as input
     */
    public static MediaCodec createVideoEncoder(
            MediaCodecInfo codecInfo,
            MediaFormat format,
            AtomicReference<Surface> surfaceReference) throws IOException {
        MediaCodec encoder = MediaCodec.createByCodecName(codecInfo.getName());

        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // Must be called before start() is.
        surfaceReference.set(encoder.createInputSurface());
        encoder.start();
        return encoder;
    }

    /**
     * Creates a decoder for the given format.
     *
     * @param inputFormat the format of the stream to decode
     */
    public static MediaCodec createAudioDecoder(MediaFormat inputFormat) throws IOException {
        MediaCodec decoder = MediaCodec.createDecoderByType(MediaEditorUtil.getMimeTypeFor(inputFormat));
        decoder.configure(inputFormat, null, null, 0);
        decoder.start();
        return decoder;
    }

    /**
     * Creates an encoder for the given format using the specified codec.
     *
     * @param codecInfo of the codec to use
     * @param format    of the stream to be produced
     */
    public static MediaCodec createAudioEncoder(MediaCodecInfo codecInfo, MediaFormat format) throws IOException {
        MediaCodec encoder = MediaCodec.createByCodecName(codecInfo.getName());
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();
        return encoder;
    }

    /**
     * Creates a muxer to write the encoded frames.
     * <p>
     * <p>The muxer is not started as it needs to be started only after all streams have been added.
     */
    public static MediaMuxer createMuxer(String outputVideoPath) throws IOException {
        return new MediaMuxer(outputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    }

    public static int getAndSelectVideoTrackIndex(MediaExtractor extractor) {
        for (int index = 0; index < extractor.getTrackCount(); ++index) {

            Log.d(TAG, "format for track " + index + " is "
                    + MediaEditorUtil.getMimeTypeFor(extractor.getTrackFormat(index)));

            if (MediaEditorUtil.isVideoFormat(extractor.getTrackFormat(index))) {
                extractor.selectTrack(index);
                return index;
            }
        }
        return -1;
    }

    public static int getAndSelectAudioTrackIndex(MediaExtractor extractor) {
        for (int index = 0; index < extractor.getTrackCount(); ++index) {
            Log.d(TAG, "format for track " + index + " is "
                    + MediaEditorUtil.getMimeTypeFor(extractor.getTrackFormat(index)));

            if (MediaEditorUtil.isAudioFormat(extractor.getTrackFormat(index))) {
                extractor.selectTrack(index);
                return index;
            }
        }
        return -1;
    }

    public static boolean isVideoFormat(MediaFormat format) {
        return getMimeTypeFor(format).startsWith("video/");
    }

    public static boolean isAudioFormat(MediaFormat format) {
        return getMimeTypeFor(format).startsWith("audio/");
    }

    public static String getMimeTypeFor(MediaFormat format) {
        return format.getString(MediaFormat.KEY_MIME);
    }


    /**
     * Returns the first codec capable of encoding the specified MIME type, or null if no match was
     * found.
     */
    public static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (!codecInfo.isEncoder()) {
                continue;
            }

            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }
}
