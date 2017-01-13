package com.lypeer.zybuluo.mixture.core;

import android.os.AsyncTask;
import android.util.Log;

import com.lypeer.zybuluo.mixture.view.CircleProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by 游小光 on 2016/12/31.
 */

public class VideoLoader extends AsyncTask<Void, Integer, Boolean> {
    private String TAG = VideoLoader.class.getSimpleName();

    private VideoDownloadCallback mCallback;

    String videoUrl = null;
    String videoPath = null;
    String jsonUrl;
    HeadInfoManager manager;

    private CircleProgressView mProgressBar;

    public VideoLoader(String videoUrl, String outputPath, String jsonUrl, HeadInfoManager manager) {
        this.videoUrl = videoUrl;
        this.videoPath = outputPath;
        this.jsonUrl = jsonUrl;
        this.manager = manager;
    }

    public void setCallback(VideoDownloadCallback callback) {
        this.mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Log.v(TAG, "doInbackground！");
        OutputStream output = null;
        try {
            manager.loadHeadInfoFromHttpUrl(jsonUrl);
            URL url = new URL(videoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            File file = new File(videoPath);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
            file.createNewFile();//新建文件
            InputStream input = conn.getInputStream();
            int totalLength = conn.getContentLength();
            output = new FileOutputStream(file);
            byte[] buffer = new byte[100 * 1024];
            int readTotalLength = 0;
            while (true) {
                int readLength = input.read(buffer);
                if (readLength == -1) {
                    break;
                }
                output.write(buffer, 0, readLength);
                readTotalLength += readLength;
                publishProgress((int)(readTotalLength * 100/ totalLength));
            }
            output.flush();
            output.close();
            input.close();
            conn.disconnect();
            if (readTotalLength == totalLength) return true; else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (mCallback != null) {
            mCallback.onVideoDownload(aBoolean);
        }
    }

    public void setProgressView(CircleProgressView progressBar) {
        mProgressBar = progressBar;
    }

    public interface VideoDownloadCallback {
        void onVideoDownload(boolean result);
    }

    //onProgressUpdate方法用于更新进度信息
    @Override
    protected void onProgressUpdate(Integer... progresses) {
        mProgressBar.setProgress(progresses[0]);
    }
}
