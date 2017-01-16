package com.lypeer.zybuluo.model.remote.viewpager;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.VarietyPresenter;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/4.
 */

public class VarietyModel extends BaseModel<VarietyPresenter> {
    public VarietyModel(VarietyPresenter varietyPresenter) {
        super(varietyPresenter);
    }

    @Override
    protected VarietyPresenter createPresenter() {
        return new VarietyPresenter();
    }


    public void refreshVideos(int currentPage) {
        RetrofitClient.buildService(ApiService.class)
                .getTypeVideos(currentPage, Constants.VideosType.TYPE_VARIETY)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        VideoResponse videoResponse = response.body();

                        if (videoResponse == null) {
                            getPresenter().refreshVideosFail(App.getAppContext().getString(R.string.prompt_no_more));
                            return;
                        }

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
                .getTypeVideos(currentPage + 1, Constants.VideosType.TYPE_VARIETY)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {
                        VideoResponse videoResponse = response.body();

                        if (videoResponse == null) {
                            getPresenter().loadMoreVideosFail(App.getAppContext().getString(R.string.prompt_no_more));
                            return;
                        }

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

