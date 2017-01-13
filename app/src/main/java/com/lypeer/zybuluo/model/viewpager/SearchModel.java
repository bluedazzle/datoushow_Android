package com.lypeer.zybuluo.model.viewpager;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.presenter.viewpager.SearchPresenter;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/4.
 */

public class SearchModel extends BaseModel<SearchPresenter> {
    public SearchModel(SearchPresenter searchPresenter) {
        super(searchPresenter);
    }

    @Override
    protected SearchPresenter createPresenter() {
        return new SearchPresenter();
    }

    public void search(String searchContent) {
        RetrofitClient.buildService(ApiService.class)
                .search(searchContent)
                .enqueue(new Callback<VideoResponse>() {
                    @Override
                    public void onResponse(Call<VideoResponse> call, Response<VideoResponse> response) {

                        if (response == null || response.body() == null) {
                            getPresenter().searchFail(App.getAppContext().getString(R.string.prompt_no_result));
                            return;
                        }
                        VideoResponse videoResponse = response.body();

                        if (videoResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().searchSuccess(videoResponse);
                        } else {
                            getPresenter().searchFail(App.getRes().getStringArray(R.array.status_error)[videoResponse.getStatus()]);
                        }
                    }

                    @Override
                    public void onFailure(Call<VideoResponse> call, Throwable t) {
                        getPresenter().searchFail(App.getAppContext().getString(R.string.error_netword));
                    }
                });
    }
}
