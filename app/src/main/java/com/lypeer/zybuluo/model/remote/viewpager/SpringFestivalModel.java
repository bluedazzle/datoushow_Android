package com.lypeer.zybuluo.model.remote.viewpager;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.MvPresenter;
import com.lypeer.zybuluo.presenter.viewpager.SpringFestivalPresenter;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/18.
 */

public class SpringFestivalModel extends BaseModel<SpringFestivalPresenter> {
    public SpringFestivalModel(SpringFestivalPresenter sfPresenter) {
        super(sfPresenter);
    }

    @Override
    protected SpringFestivalPresenter createPresenter() {
        return new SpringFestivalPresenter();
    }


    public void refreshVideos(int currentPage) {
        //@todo 这里暂时是用的mv的数据，因为还没给春节的接口，记得修改
        RetrofitClient.buildService(ApiService.class)
                .getTypeVideos(currentPage, Constants.VideosType.TYPE_MV)
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
                .getTypeVideos(currentPage + 1, Constants.VideosType.TYPE_MV)
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
}
