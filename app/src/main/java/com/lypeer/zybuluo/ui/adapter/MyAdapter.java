package com.lypeer.zybuluo.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.lypeer.zybuluo.model.bean.Video;
import com.lypeer.zybuluo.ui.adapter.viewholder.MyVH;
import com.lypeer.zybuluo.ui.base.BaseAdapter;
import com.lypeer.zybuluo.ui.base.BaseViewHolder;

/**
 * Created by lypeer on 2017/1/9.
 */

public class MyAdapter extends BaseAdapter<Video>{
    @Override
    protected BaseViewHolder createViewHolder(Context context, ViewGroup parent) {
        return new MyVH(context , parent);
    }

    public void removeData(int position) {
        mValueList.remove(position);

        if (isHasHeader()) {
            notifyItemRemoved(position + 1);
            notifyItemRangeChanged(1, mValueList.size() + 1);
        } else {
            notifyItemRemoved(position);
            notifyItemRangeChanged(0, mValueList.size());
        }
    }
}
