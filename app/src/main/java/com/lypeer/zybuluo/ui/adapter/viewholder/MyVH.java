package com.lypeer.zybuluo.ui.adapter.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.OnItemClickListener;
import com.lypeer.zybuluo.model.bean.Video;
import com.lypeer.zybuluo.ui.base.BaseViewHolder;

import butterknife.BindView;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by lypeer on 2017/1/9.
 */

public class MyVH extends BaseViewHolder<Video> {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_author)
    TextView mTvAuthor;
    @BindView(R.id.video_player)
    JCVideoPlayerStandard mVideoPlayer;
    @BindView(R.id.lly_save)
    LinearLayout mLlySave;
    @BindView(R.id.lly_share)
    LinearLayout mLlyShare;
    @BindView(R.id.lly_delete)
    LinearLayout mLlyDelete;

    public MyVH(Context context, ViewGroup root) {
        super(context, root, R.layout.item_my);
    }

    @Override
    protected void bindData(final Video itemValue, final int position, final OnItemClickListener listener) {
        mTvTitle.setText(itemValue.getTitle());
        mTvAuthor.setText(itemValue.getArtist());
        mVideoPlayer.setUp(itemValue.getPath()
                , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
        mVideoPlayer.coverImageView.setVisibility(View.GONE);
        if (itemValue.getThumbnail() != null) {
            mVideoPlayer.thumbImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mVideoPlayer.thumbImageView.setImageBitmap(itemValue.getThumbnail());
/*            try {
                File f = new File(App.getAppContext().getCacheDir(), itemValue.getTitle());
                f.createNewFile();


                Bitmap bitmap = itemValue.getThumbnail();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 *//*ignored for PNG*//*, bos);
                byte[] bitmapdata = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();

                Picasso.with(App.getAppContext()).load(f).fit().into(mVideoPlayer.thumbImageView);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(itemValue, view.getId(), position);
                }
            }
        };

        mLlySave.setOnClickListener(onClickListener);
        mLlyShare.setOnClickListener(onClickListener);
        mLlyDelete.setOnClickListener(onClickListener);
    }
}
