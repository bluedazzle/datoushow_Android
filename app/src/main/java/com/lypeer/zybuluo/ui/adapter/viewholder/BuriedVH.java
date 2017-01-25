package com.lypeer.zybuluo.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.ui.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/21.
 */

public class BuriedVH extends BaseViewHolder<VideoResponse.BodyBean.VideoListBean> {
    @BindView(R.id.iv_cover)
    ImageView mIvCover;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_author)
    TextView mTvAuthor;
    @BindView(R.id.lly_container)
    LinearLayout mLlyContainer;

    public BuriedVH(Context context, ViewGroup root) {
        super(context, root, R.layout.item_buried);
    }

    @Override
    protected void bindData(final VideoResponse.BodyBean.VideoListBean itemValue, final int position, final OnItemClickListener listener) {
        Picasso.with(App.getAppContext()).load(itemValue.getThumb_nail()).fit().centerInside().into(mIvCover);
        mTvTitle.setText(itemValue.getTitle());
        mTvAuthor.setText(itemValue.getAuthor());

        mLlyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(itemValue, view.getId(), position);
                }
            }
        });
    }
}
