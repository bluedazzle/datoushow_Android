package com.lypeer.zybuluo.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.model.bean.Video;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/9.
 */

public class VideoProvider {
    private final Context context;

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
                        File file = new File(FileUtil.getStorageDir());
                        File[] files = file.listFiles();

                        for (File fileX : files) {

                            String[] data = fileX.getName().split("_");
                            if (data.length <= 4 || !data[0].equals(App.getAppContext().getString(R.string.datouxiu))) {
                                continue;
                            }
                            try {
                                Video video = new Video(Integer.valueOf(data[1]), data[2], data[3], fileX.getPath());
                                list.add(video);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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