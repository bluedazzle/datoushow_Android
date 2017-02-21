package com.lypeer.zybuluo.model.remote.viewpager;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.BuriedPresenter;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/21.
 */

public class BuriedModel extends BaseModel<BuriedPresenter> {
    public BuriedModel(BuriedPresenter buriedPresenter) {
        super(buriedPresenter);
    }

    @Override
    protected BuriedPresenter createPresenter() {
        return new BuriedPresenter();
    }

    public void refreshVideos(int currentPage , int type) {
        RetrofitClient.buildService(ApiService.class)
                .getTypeVideos(currentPage, type)
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

    public void loadMoreVideos(int currentPage , int type) {
        RetrofitClient.buildService(ApiService.class)
                .getTypeVideos(currentPage + 1, type)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                        if (response == null || response.body() == null) {
                            getPresenter().loadMoreVideosFail(App.getAppContext().getString(R.string.prompt_no_more));
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
}

