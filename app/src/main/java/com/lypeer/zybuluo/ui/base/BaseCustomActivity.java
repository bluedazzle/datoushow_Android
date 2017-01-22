package com.lypeer.zybuluo.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Toast;

import com.bugtags.library.Bugtags;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.utils.ActivityController;
import com.zhuge.analysis.stat.ZhugeSDK;

import butterknife.ButterKnife;

/**
 * Created by lypeer on 16/9/8.
 */
public abstract class BaseCustomActivity extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;
    private Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.dialog_waiting));
        initView(savedInstanceState);
    }

    protected abstract void initView(@Nullable Bundle savedInstanceState);

    /**
     * set resource id
     *
     * @return layout id of current activity
     */
    @LayoutRes
    protected abstract int getLayoutResId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(this);
    }

    /**
     * called when an AVException received
     */
    public void showMessage(Exception e) {
        if (null == e || TextUtils.isEmpty(e.getMessage())) {
            return;
        }
        showMessage(e.getMessage());
    }

    public void showMessage(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        }
        mToast.setText(s);
        mToast.show();
    }

    public void showMessage(int stringId) {
        showMessage(getString(stringId));
    }

    /**
     * An interface of the success event.
     *
     * @param statusCode in the Constant.StatusCode
     * @param s          maybe there need some param
     */
    public <S> void onSuccess(int statusCode, S s) {
    }

    public void onSuccess(int statusCode) {
        onSuccess(statusCode, null);
    }

    public <F> void onFail(int statusCode, F f) {
    }

    public void onFail(int statusCode) {
        onFail(statusCode, null);
    }

    public void showProgressBar() {
        if (mProgressDialog != null)
            mProgressDialog.show();
    }

    public void hideProgressBar() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    public void setProgressMessage(String message) {
        if (!TextUtils.isEmpty(message))
            mProgressDialog.setMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
        ZhugeSDK.getInstance().init(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Bugtags.onDispatchTouchEvent(this, ev);
        return super.dispatchTouchEvent(ev);
    }
}
