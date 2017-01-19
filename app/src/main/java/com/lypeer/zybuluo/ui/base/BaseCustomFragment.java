package com.lypeer.zybuluo.ui.base;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lypeer.zybuluo.R;

import butterknife.ButterKnife;

/**
 * Created by lypeer on 2017/1/18.
 */

public abstract class BaseCustomFragment extends Fragment {

    protected View mRootView;
    protected ProgressDialog mProgressDialog;
    private Toast mToast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null)
            mRootView = inflater.inflate(getResId(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, mRootView);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.dialog_waiting));
        initView(savedInstanceState);
    }

    @LayoutRes
    protected abstract int getResId();

    protected abstract void initView(@Nullable Bundle savedInstanceState);

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

    /**
     * called when an AVException received
     */
    public void showMessage(Exception e) {
        if (null == e || TextUtils.isEmpty(e.getMessage())) {
            return;
        }
        Log.e(getActivity().getLocalClassName(), e.getMessage());
        showMessage(e.getMessage());
    }

    public void showMessage(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT);
        }
        mToast.setText(s);
        mToast.show();
    }

    public void showMessage(int stringId) {
        showMessage(getString(stringId));
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

    public ProgressDialog getProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getString(R.string.dialog_waiting));
        }

        return mProgressDialog;
    }
}
