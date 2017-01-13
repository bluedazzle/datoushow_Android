package com.lypeer.zybuluo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;
import com.lypeer.zybuluo.ui.fragment.AddFragment;
import com.lypeer.zybuluo.ui.fragment.MyFragment;
import com.lypeer.zybuluo.utils.ActivityController;
import com.lypeer.zybuluo.utils.Constants;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class MainActivity extends BaseCustomActivity {


    @BindView(R.id.fl_container)
    FrameLayout mFlContainer;
    @BindView(R.id.iv_add)
    ImageView mIvAdd;
    @BindView(R.id.lly_add)
    LinearLayout mLlyAdd;
    @BindView(R.id.iv_my)
    ImageView mIvMy;
    @BindView(R.id.lly_my)
    LinearLayout mLlyMy;
    @BindView(R.id.nb_main)
    LinearLayout mNbMain;
    @BindView(R.id.tv_add)
    TextView mTvAdd;
    @BindView(R.id.tv_my)
    TextView mTvMy;

    private List<Integer> mFragmentIdList;
    private Map<Integer, Fragment> mFragmentMap;
    private Map<Integer, Fragment> mRealFragmentMap;
    private Map<Integer, ImageView> mImageViewMap;
    private Map<Integer, TextView> mTextViewMap;
    private Map<Integer, Integer> mImgResNormalMap;
    private Map<Integer, Integer> mImgResSelectedMap;

    private FragmentManager mManager;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        ActivityController.finishAllExceptNow(MainActivity.this);
        initList();
        initMap();
        mManager = getSupportFragmentManager();
        setCurrentSelection(Constants.FragmentId.ADD);
    }

    private void setCurrentSelection(int fragmentId) {
        clearSelection();

        FragmentTransaction transaction = mManager.beginTransaction();
        hideAllFragment(transaction);

        if (mRealFragmentMap.get(fragmentId) == null) {
            mRealFragmentMap.put(fragmentId, mFragmentMap.get(fragmentId));
            transaction.add(R.id.fl_container, mFragmentMap.get(fragmentId));
        }
        transaction.show(mRealFragmentMap.get(fragmentId));

        mImageViewMap.get(fragmentId).setImageResource(mImgResSelectedMap.get(fragmentId));
        mTextViewMap.get(fragmentId).setTextColor(getResources().getColor(R.color.colorRed));

        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction) {
        for (int id : mFragmentIdList) {
            transaction.hide(mFragmentMap.get(id));
        }
    }

    private void clearSelection() {
        for (int id : mFragmentIdList) {
            mImageViewMap.get(id).setImageResource(mImgResNormalMap.get(id));
            mTextViewMap.get(id).setTextColor(getResources().getColor(R.color.colorGray));
        }
    }

    private void initList() {
        mFragmentIdList = new ArrayList<>();
        mFragmentIdList.add(Constants.FragmentId.ADD);
        mFragmentIdList.add(Constants.FragmentId.MY);
    }

    private void initMap() {
        mRealFragmentMap = new HashMap<>();

        mFragmentMap = new HashMap<>();
        mFragmentMap.put(Constants.FragmentId.ADD, new AddFragment());
        mFragmentMap.put(Constants.FragmentId.MY, new MyFragment());

        mImageViewMap = new HashMap<>();
        mImageViewMap.put(Constants.FragmentId.ADD, mIvAdd);
        mImageViewMap.put(Constants.FragmentId.MY, mIvMy);

        mTextViewMap = new HashMap<>();
        mTextViewMap.put(Constants.FragmentId.ADD, mTvAdd);
        mTextViewMap.put(Constants.FragmentId.MY, mTvMy);

        mImgResNormalMap = new HashMap<>();
        mImgResNormalMap.put(Constants.FragmentId.ADD, R.drawable.ic_tab_add_normal);
        mImgResNormalMap.put(Constants.FragmentId.MY, R.drawable.ic_tab_user_normal);

        mImgResSelectedMap = new HashMap<>();
        mImgResSelectedMap.put(Constants.FragmentId.ADD, R.drawable.ic_tab_add_selected);
        mImgResSelectedMap.put(Constants.FragmentId.MY, R.drawable.ic_tab_user_selected);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @OnClick({R.id.lly_add, R.id.lly_my})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lly_add:
                setCurrentSelection(Constants.FragmentId.ADD);
                break;
            case R.id.lly_my:
                setCurrentSelection(Constants.FragmentId.MY);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UMShareAPI.get(this).fetchAuthResultWithBundle(this, savedInstanceState, new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {

            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
    }
}
