package com.lypeer.zybuluo.model.remote.viewpager;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.BannerResponse;
import com.lypeer.zybuluo.model.bean.UploadResponse;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.HotPresenter;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/4.
 */

public class HotModel extends BaseModel<HotPresenter> {
    public HotModel(HotPresenter hotPresenter) {
        super(hotPresenter);
    }

    @Override
    protected HotPresenter createPresenter() {
        return new HotPresenter();
    }

    public void refreshVideos(int currentPage) {
        RetrofitClient.buildService(ApiService.class)
                .getHotVideos(currentPage, 1)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                        if (response == null || response.body() == null) {
                            getPresenter().refreshVideosFail(App.getAppContext().getString(R.string.prompt_no_more));
                            return;
                        }
                        VideoResponse videoResponse = response.body();

                        if (videoResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().refreshVideosSuccess(videoResponse);
                        } else {
                            getPresenter().refreshVideosFail(App.getRes().getStringArray(R.array.status_error)[videoResponse.getStatus()]);
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        getPresenter().refreshVideosFail(App.getAppContext().getString(R.string.error_network));
                    }
                });
    }

    public void loadMoreVideos(int currentPage) {
        RetrofitClient.buildService(ApiService.class)
                .getHotVideos(currentPage + 1, 1)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                        if (response == null || response.body() == null) {
                            getPresenter().refreshVideosFail(App.getAppContext().getString(R.string.prompt_no_more));
                            return;
                        }
                        VideoResponse videoResponse = response.body();

                        if (videoResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().loadMoreVideosSuccess(videoResponse);
                        } else {
                            getPresenter().loadMoreVideosFail(App.getRes().getStringArray(R.array.status_error)[videoResponse.getStatus()]);
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        getPresenter().loadMoreVideosFail(App.getAppContext().getString(R.string.error_network));
                    }
                });
    }

    public void refreshBanner() {
        RetrofitClient.buildService(ApiService.class)
                .getBanner()
                .enqueue(new Callback<BannerResponse>() {
                    @Override
                    public void onResponse(Call<BannerResponse> call, Response<BannerResponse> response) {
                        if (response == null || response.body() == null) {
                            getPresenter().refreshBannerSuccess(new BannerResponse());
                            return;
                        }
                        BannerResponse bannerResponse = response.body();

                        if (bannerResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().refreshBannerSuccess(bannerResponse);
                        } else {
                            getPresenter().refreshBannerFail(App.getRes().getStringArray(R.array.status_error)[bannerResponse.getStatus()]);
                        }
                    }

                    @Override
                    public void onFailure(Call<BannerResponse> call, Throwable t) {
                        getPresenter().refreshBannerFail(App.getAppContext().getString(R.string.error_network));
                    }
                });
    }
}
