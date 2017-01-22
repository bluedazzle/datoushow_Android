package com.lypeer.zybuluo.ui.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnCheckUpdateInfoListener;
import com.lypeer.zybuluo.model.bean.UpdateInfoBean;
import com.lypeer.zybuluo.ui.base.BaseCustomActivity;
import com.lypeer.zybuluo.ui.fragment.AddFragment;
import com.lypeer.zybuluo.ui.fragment.MyFragment;
import com.lypeer.zybuluo.utils.ActivityController;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.FileUtil;
import com.lypeer.zybuluo.utils.SharePreferencesUtil;
import com.lypeer.zybuluo.utils.UpdateUtil;
import com.zhuge.analysis.stat.ZhugeSDK;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class MainActivity extends BaseCustomActivity {

    public static String TAG = "MainActivity";

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
    private PopupWindow mPopupWindow;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        ActivityController.finishAllExceptNow(MainActivity.this);
        SharePreferencesUtil.launched();
        initList();
        initMap();
        mManager = getSupportFragmentManager();
        setCurrentSelection(Constants.FragmentId.ADD);
        try {
            FileUtil.copy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkUpdate();
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
            mTextViewMap.get(id).setTextColor(getResources().getColor(R.color.colorPink));
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

    private void checkUpdate() {
        UpdateUtil.checkUpdateInfo(new OnCheckUpdateInfoListener() {
            @Override
            public void success(boolean hasUpdate, UpdateInfoBean updateBean) {
                if (hasUpdate) {
                    showPpw(updateBean);
                }
            }

            @Override
            public void fail(String errorMessage) {

            }
        });
    }

    private void showPpw(UpdateInfoBean updateInfoBean) {
        View view = LayoutInflater.from(this).inflate(R.layout.ppw_update, null);
        initPpwView(view, updateInfoBean.getBody().getUpdate());

        mPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.showAtLocation(mNbMain, Gravity.BOTTOM, 0, 0);
    }

    private void initPpwView(View view, final UpdateInfoBean.BodyBean.UpdateBean updateBean) {
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvLog = (TextView) view.findViewById(R.id.tv_log);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvUpdate = (TextView) view.findViewById(R.id.tv_update);

        tvTitle.setText(updateBean.getAndroid_title());
        tvLog.setText(updateBean.getAndroid_log());

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
            }
        });

        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateBean.getAndroid_download()));
                startActivity(intent);
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
        ZhugeSDK.getInstance().flush(getApplicationContext());
    }
}
