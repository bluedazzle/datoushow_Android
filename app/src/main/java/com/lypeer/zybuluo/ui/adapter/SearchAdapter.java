package com.lypeer.zybuluo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.ui.adapter.viewholder.HotVH;
import com.lypeer.zybuluo.ui.adapter.viewholder.SearchVH;
import com.lypeer.zybuluo.ui.base.BaseAdapter;
import com.lypeer.zybuluo.ui.base.BaseViewHolder;

/**
 * Created by lypeer on 2017/1/5.
 */

public class SearchAdapter extends BaseAdapter<VideoResponse.BodyBean.VideoListBean> {
    @Override
    protected BaseViewHolder createViewHolder(Context context, ViewGroup parent) {
        return new SearchVH(context , parent);
    }
}
