package com.lypeer.zybuluo.model.remote.main;

import android.text.TextUtils;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.BodyBean;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.UploadResponse;
import com.lypeer.zybuluo.model.bean.Video;
import com.lypeer.zybuluo.model.local.main.MyModelLocal;
import com.lypeer.zybuluo.presenter.main.MyPresenter;
import com.lypeer.zybuluo.utils.ApiSignUtil;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.DeviceUuidFactory;
import com.lypeer.zybuluo.utils.RetrofitClient;
import com.lypeer.zybuluo.utils.VideoProvider;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/4.
 */

public class MyModel extends BaseModel<MyPresenter> {

    private MyModelLocal mModelLocal;

    public MyModel(MyPresenter myPresenter) {
        super(myPresenter);
        mModelLocal = new MyModelLocal();
    }

    @Override
    protected MyPresenter createPresenter() {
        return new MyPresenter();
    }

    public void refreshVideos(final int currentPage) {
        VideoProvider provider = new VideoProvider(App.getAppContext());
        provider.getList(new VideoProvider.OnLoadFinishListener() {
            @Override
            public void onSuccess(List<Video> videoList) {
                if (videoList == null || videoList.size() == 0) {
                    getPresenter().refreshVideosFail(App.getAppContext().getString(R.string.error_no_video));
                    return;
                }

                if (videoList.size() <= 20 * currentPage) {
                    getPresenter().refreshVideosSuccess(videoList);
                    return;
                }
                List<Video> finalVideoList = new ArrayList<>();
                for (int i = 0; i < 20 * currentPage; i++) {
                    finalVideoList.add(videoList.get(i));
                }

                getPresenter().refreshVideosSuccess(finalVideoList);
            }

            @Override
            public void onFail(String errorMessage) {
                getPresenter().refreshVideosFail(errorMessage);
            }
        });


    }

    public void loadMoreVideos(final int currentPage) {
        VideoProvider provider = new VideoProvider(App.getAppContext());
        provider.getList(new VideoProvider.OnLoadFinishListener() {
            @Override
            public void onSuccess(List<Video> videoList) {
                if (videoList == null || videoList.size() == 0) {
                    getPresenter().loadMoreVideosFail(App.getAppContext().getString(R.string.error_no_video));
                    return;
                }

                if (videoList.size() <= 20 * currentPage) {
                    getPresenter().loadMoreVideosFail(App.getAppContext().getString(R.string.prompt_no_more));
                    return;
                }

                List<Video> finalVideoList = new ArrayList<>();
                for (int i = 20 * currentPage; i < 20 * (currentPage + 1); i++) {
                    finalVideoList.add(videoList.get(i));
                }

                getPresenter().loadMoreVideosSuccess(finalVideoList);
            }

            @Override
            public void onFail(String errorMessage) {
                getPresenter().loadMoreVideosFail(errorMessage);
            }
        });
    }

    public void share(final Video target, final OnProgressChangedListener listener) {
        BodyBean bodyBean = mModelLocal.get(target.getPath());
        if (bodyBean != null && bodyBean.isValid()) {
            CreateShareLinkResponse response = bodyBean.getCreateShareLinkResponse();
            getPresenter().shareSuccess(response, target.getPath());
            return;
        }

        RetrofitClient.buildService(ApiService.class)
                .upload()
                .enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response == null ||
                                response.body() == null ||
                                response.body().getStatus() != Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().shareFail(App.getAppContext().getString(R.string.error_share_fail));
                            return;
                        }

                        upload(target, response.body().getBody().getToken(), listener);
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        getPresenter().shareFail(t.getMessage());
                    }
                });
    }

    private void upload(final Video target, String uptoken, final OnProgressChangedListener listener) {
        DeviceUuidFactory factory = new DeviceUuidFactory(App.getAppContext());
        String uuid = factory.getDeviceUuid().toString();
        String time = String.valueOf(System.currentTimeMillis());

        Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
        UploadManager uploadManager = new UploadManager(config);
        try {

            uploadManager.put(target.getPath(), ApiSignUtil.md5(uuid + time) + ".mp4", uptoken, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info == null) {
                        getPresenter().shareFail(App.getAppContext().getString(R.string.error_some_problem));
                        return;
                    }

                    if (info.isOK()) {
                        try {
                            createLink(target, "static.fibar.cn/".concat(response.getString("key")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        getPresenter().shareFail(info.error);
                    }
                }

            }, new UploadOptions(null, null, false,
                    new UpProgressHandler() {
                        public void progress(String key, double percent) {
                            listener.onProgressChanged(percent);
                        }
                    }, null));
        } catch (Exception e) {
            e.printStackTrace();
            getPresenter().shareFail(e.getMessage());
        }
    }

    private void createLink(final Video target, final String url) {
        DeviceUuidFactory factory = new DeviceUuidFactory(App.getAppContext());
        String uuid = factory.getDeviceUuid().toString();

        if (TextUtils.isEmpty(uuid)) {
            getPresenter().shareFail(App.getAppContext().getString(R.string.error_uuid_null));
            return;
        }

        String token = ApiSignUtil.md5(uuid.concat(String.valueOf(target.getId())));

        RetrofitClient.buildService(ApiService.class)
                .createShareLink(url, uuid, target.getId(), token)
                .enqueue(new Callback<CreateShareLinkResponse>() {
                    @Override
                    public void onResponse(Call<CreateShareLinkResponse> call, Response<CreateShareLinkResponse> response) {
                        if (response == null || response.body() == null) {
                            getPresenter().shareFail(App.getAppContext().getString(R.string.error_share_fail));
                            return;
                        }

                        CreateShareLinkResponse shareLinkResponse = response.body();

                        if (shareLinkResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().shareSuccess(shareLinkResponse, target.getPath());
                            shareLinkResponse.getBody().setPath(target.getPath());
                            mModelLocal.insert(new BodyBean(shareLinkResponse.getBody()));
                        } else {
                            getPresenter().shareFail(App.getRes().getStringArray(R.array.status_error)[shareLinkResponse.getStatus()]);
                        }

                    }

                    @Override
                    public void onFailure(Call<CreateShareLinkResponse> call, Throwable t) {
                        getPresenter().shareFail(t.getMessage());
                    }
                });
    }

    public void delete(Video itemValue, int position) {
        File file = new File(itemValue.getPath());
        if (file.exists()) {
            if (file.delete()) {
                mModelLocal.delete(itemValue.getPath());
                getPresenter().deleteSuccess(position);
            } else {
                getPresenter().deleteFail(App.getAppContext().getString(R.string.error_delete_fail));
            }
        } else {
            getPresenter().deleteFail(App.getAppContext().getString(R.string.error_not_exist));
        }
    }
}
