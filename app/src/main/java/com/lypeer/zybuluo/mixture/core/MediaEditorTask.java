package com.lypeer.zybuluo.mixture.core;

import android.os.AsyncTask;
import android.util.Log;

import com.bugtags.library.Bugtags;
import com.lypeer.zybuluo.mixture.view.CircleProgressView;


/**
 * Created by 游小光 on 2016/12/14.
 */

public class MediaEditorTask extends AsyncTask<Void, Integer, Boolean> {
    private static final String TAG = MediaEditorTask.class.getSimpleName();

    private final MediaEditor mEditor;
    private final CircleProgressView mProgressView;

    private MediaEditorTaskCallback mCallback = null;

    private int mCurrentProgress = 0;

    public MediaEditorTask(MediaEditor editor, CircleProgressView progressView) {
        mEditor = editor;
        mProgressView = progressView;
    }

    public void setCallback(MediaEditorTaskCallback callback) {
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            mEditor.setAsyncTask(this);
            mEditor.setup();
            mEditor.start();
            Log.v(TAG, "MediaEditorTask 1 :" + System.currentTimeMillis());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Bugtags.sendException(e);
            return false;
        } finally {
            try {
                mEditor.release();
                Log.v(TAG, "MediaEditorTask 2 :" + System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
                Bugtags.sendException(e);
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.v(TAG, "MediaEditorTask 3 :" + System.currentTimeMillis());
        if (mCallback != null) {
            mCallback.onMediaEditCompleted(aBoolean);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.v(TAG, "onProgressUpdate " + values[0]);
        super.onProgressUpdate(values);
        if (values[0] - mCurrentProgress > 0) {
            mCurrentProgress = values[0];
            mProgressView.setProgress(mCurrentProgress , "");
        }
    }

    public void changeProgress(int l) {
        Log.v(TAG, "progress " + l);
        publishProgress(l);
    }

    public interface MediaEditorTaskCallback {
        void onMediaEditCompleted(boolean result);
    }

}
