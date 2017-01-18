package com.lypeer.zybuluo.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.model.bean.BannerResponse;
import com.lypeer.zybuluo.ui.base.BaseViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by lypeer on 2017/1/18.
 */

public class HotBannerVH extends BaseViewHolder<BannerResponse> {
    @BindView(R.id.cb_banner)
    ConvenientBanner mCbBanner;

    public HotBannerVH(Context context, ViewGroup root) {
        super(context, root, R.layout.item_hot_banner);
    }

    @Override
    protected void bindData(final BannerResponse itemValue, int position, final OnItemClickListener listener) {
        if (itemValue == null || itemValue.getBody().getBanner_list().size() == 0) {
            mCbBanner.setVisibility(View.GONE);
            return;
        } else {
            mCbBanner.setVisibility(View.VISIBLE);
        }

        mCbBanner.startTurning(2500);
        mCbBanner.setCanLoop(true);

        List<String> picData = new ArrayList<>();
        final List<BannerResponse.BodyBean.BannerListBean> bannerListBeen = itemValue.getBody().getBanner_list();
        for (BannerResponse.BodyBean.BannerListBean bannerItem : bannerListBeen) {
            picData.add(bannerItem.getPicture());
        }

        mCbBanner.setPages(new CBViewHolderCreator<ImageHolderView>() {
            @Override
            public ImageHolderView createHolder() {
                return new ImageHolderView();
            }
        }, picData)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});

        mCbBanner.setOnItemClickListener(new com.bigkoo.convenientbanner.listener.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                listener.onItemClick(bannerListBeen.get(position), mCbBanner.getId(), position);
            }
        });
    }

    private class ImageHolderView implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, String url) {
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.bg_banner_place_holder)
                    .fit()
                    .centerInside()
                    .into(imageView);
        }
    }
}