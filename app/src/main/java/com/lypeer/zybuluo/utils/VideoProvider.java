package com.lypeer.zybuluo.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.model.bean.Video;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/9.
 */

public class VideoProvider {
    private Context context;

    public VideoProvider(Context context) {
        this.context = context;
    }

    public void getList(final OnLoadFinishListener loadFinishListener) {
        final List<Video> list = new ArrayList<>();

        AsyncTask<Void, Void, List<Video>> asyncTask = new AsyncTask<Void, Void, List<Video>>() {
            @Override
            protected List<Video> doInBackground(Void... voids) {
                try {
                    if (context != null) {
                        Cursor cursor = context.getContentResolver().query(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                                null, null);
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                int id = cursor.getInt(cursor
                                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                                String title = cursor
                                        .getString(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                                String album = cursor
                                        .getString(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                                String artist = cursor
                                        .getString(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                                String displayName = cursor
                                        .getString(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                                String mimeType = cursor
                                        .getString(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                                String path = cursor
                                        .getString(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                                long duration = cursor
                                        .getInt(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                                long size = cursor
                                        .getLong(cursor
                                                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                                Video video = new Video(id, title, album, artist, displayName, mimeType, path, size, duration);
                                list.add(video);
                            }
                            cursor.close();
                        }
                    }
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Video> videoList) {
                super.onPostExecute(videoList);
                if (videoList == null) {
                    loadFinishListener.onFail(context.getString(R.string.error_some_problem));
                } else {
                    loadFinishListener.onSuccess(videoList);
                }
            }
        };
        asyncTask.execute();
    }

    public interface OnLoadFinishListener {

        void onSuccess(List<Video> videoList);

        void onFail(String errorMessage);
    }

}