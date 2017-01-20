package com.lypeer.zybuluo.mixture.core;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lypeer.zybuluo.mixture.core.HeadInfo;
import com.lypeer.zybuluo.utils.DataFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 游小光 on 2016/12/27.
 */

public class HeadInfoManager {
    public final static Bitmap defaultBitmap = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888);
    private final String TAG = HeadInfoManager.class.getSimpleName();

    private Map<Integer, HeadInfo> mTrackInfoMap = null;

    private List<SubtitleInfo> mSubtitleInfoMap = null;

    public int frameRate;
    public long duration;

    public static final long MAX_RECORD_TIME = Long.MAX_VALUE;

    public static final int MAX_DATA_SIZE = 1024 * 1024 * 10;

    public static final int ROTATION_DIFFERENCE_FRAME = 574;

    public int videoWidth;

    public int videoHeight;

    public int headWidth;

    public int headHeight;

    public int maxFrame = 0;

    public boolean rotationOnTop = true;

    public void loadHeadInfoFromHttpUrl(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        InputStream input = conn.getInputStream();
        byte[] buffer = new byte[MAX_DATA_SIZE];
        int readTotalLength = 0;
        int len;
        while ((len = input.read(buffer, readTotalLength, MAX_DATA_SIZE - readTotalLength)) != -1) {
            readTotalLength += len;
        }
        input.close();
        conn.disconnect();

        JSONObject root = new JSONObject(new String(buffer, 0, readTotalLength));

        JSONArray tracks = root.getJSONObject("body").getJSONObject("video").getJSONArray("tracks");
        mTrackInfoMap = new HashMap<>(tracks.length());
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            mTrackInfoMap.put(track.getInt("frame"), new HeadInfo(
                    track.getInt("frame"), track.getDouble("x"), track.getDouble("y"),
                    track.getDouble("rotation"), track.getDouble("size") == -1 ? 144 : track.getDouble("size"), track.getDouble("time")));
            if (track.getInt("frame") > maxFrame) {
                maxFrame = track.getInt("frame");
            }
        }

        if (root.getJSONObject("body").getJSONObject("video").getBoolean("has_subtitle")) {
            JSONArray subtitles = root.getJSONObject("body").getJSONObject("video").getJSONArray("subtitle");
            mSubtitleInfoMap = new ArrayList<>();
            for (int i = 0; i < subtitles.length(); i++) {
                JSONObject subtitle = subtitles.getJSONObject(i);
                SubtitleInfo subtitleInfo = new Gson().fromJson(subtitle.toString(), SubtitleInfo.class);
                DataFormatter.string2Millisecond(subtitleInfo.getStart_time());
                mSubtitleInfoMap.add(subtitleInfo);
            }
        }

        duration = DataFormatter.string2Millisecond(root.getJSONObject("body").getJSONObject("video").getDouble("duration"));
        frameRate = root.getJSONObject("body").getJSONObject("video").getInt("fps");
        int id = root.getJSONObject("body").getJSONObject("video").getInt("id");
        rotationOnTop = id < ROTATION_DIFFERENCE_FRAME;
        Log.v(TAG, "loadHeadInfoFromHttpUrl " + mTrackInfoMap.size());
    }

    public HeadInfo getTrackInfoByTime(long time) {
        int frame = (int) (time * frameRate / 1000000000) + 1;
        return mTrackInfoMap.get(frame > maxFrame ? maxFrame : frame);
    }

    public HeadInfo getHeadInfoByFrame(int frame) {
        return mTrackInfoMap.get(frame);
    }

    public int getPreparedFrame() {
        for (int i = 1; i <= mTrackInfoMap.size(); i++) {
            HeadInfo headInfo = mTrackInfoMap.get(i);
            if (headInfo != null && headInfo.size >= 5) {
                return i;
            }
        }
        return 0;
    }

    public long getTimeByFrame(int mCurrentFrame) {
        HeadInfo headInfo = mTrackInfoMap.get(mCurrentFrame);
        return (long) (headInfo.time * 1000);
    }

    public List<SubtitleInfo> getSubtitleInfoMap() {
        return mSubtitleInfoMap;
    }

    public void setSubtitleInfoMap(List<SubtitleInfo> subtitleInfoMap) {
        mSubtitleInfoMap = subtitleInfoMap;
    }
}
