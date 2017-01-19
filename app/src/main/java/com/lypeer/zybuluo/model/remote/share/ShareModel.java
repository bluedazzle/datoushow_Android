package com.lypeer.zybuluo.model.remote.share;

import android.text.TextUtils;

import com.lypeer.zybuluo.App;
import com.lypeer.zybuluo.R;
import com.lypeer.zybuluo.impl.ApiService;
import com.lypeer.zybuluo.impl.OnProgressChangedListener;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.model.bean.BodyBean;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.UploadResponse;
import com.lypeer.zybuluo.model.local.share.ShareModelLocal;
import com.lypeer.zybuluo.presenter.share.SharePresenter;
import com.lypeer.zybuluo.utils.ApiSignUtil;
import com.lypeer.zybuluo.utils.Constants;
import com.lypeer.zybuluo.utils.DeviceUuidFactory;
import com.lypeer.zybuluo.utils.RetrofitClient;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lypeer on 2017/1/17.
 */

public class ShareModel extends BaseModel<SharePresenter> {

    private final ShareModelLocal mModelLocal;

    public ShareModel(SharePresenter sharePresenter) {
        super(sharePresenter);
        mModelLocal = new ShareModelLocal();
    }

    @Override
    protected SharePresenter createPresenter() {
        return new SharePresenter();
    }

    public void share(final String path, final int id, final OnProgressChangedListener listener) {
        BodyBean bodyBean = mModelLocal.get(path);
        if (bodyBean != null && bodyBean.isValid()) {
            CreateShareLinkResponse response = bodyBean.getCreateShareLinkResponse();
            getPresenter().shareSuccess(response, path);
            return;
        }

        RetrofitClient.buildService(ApiService.class)
                .upload()
                .enqueue(new Callback<UploadResponse>() {
                    @Override
                    public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                        if (response == null || response.body() == null) {
                            getPresenter().shareFail(App.getAppContext().getString(R.string.prompt_no_more));
                            return;
                        }
                        UploadResponse uploadResponse = response.body();

                        if (uploadResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            upload(path, response.body().getBody().getToken(), id, listener);
                        } else {
                            getPresenter().shareFail(App.getRes().getStringArray(R.array.status_error)[uploadResponse.getStatus()]);
                        }
                    }

                    @Override
                    public void onFailure(Call<UploadResponse> call, Throwable t) {
                        getPresenter().shareFail(App.getAppContext().getString(R.string.error_network));
                    }
                });
    }

    private void upload(final String path, String token, final int id, final OnProgressChangedListener listener) {
        DeviceUuidFactory factory = new DeviceUuidFactory(App.getAppContext());
        String uuid = factory.getDeviceUuid().toString();
        String time = String.valueOf(System.currentTimeMillis());

        Configuration config = new Configuration.Builder().zone(Zone.httpAutoZone).build();
        UploadManager uploadManager = new UploadManager(config);
        try {

            uploadManager.put(path, ApiSignUtil.md5(uuid + time) + ".mp4", token, new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    if (info == null) {
                        getPresenter().shareFail(App.getAppContext().getString(R.string.error_network));
                        return;
                    }

                    if (info.isOK()) {
                        try {
                            createLink(path, id, "static.fibar.cn/".concat(response.getString("key")));
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

    private void createLink(final String path, int id, String url) {
        DeviceUuidFactory factory = new DeviceUuidFactory(App.getAppContext());
        String uuid = factory.getDeviceUuid().toString();

        if (TextUtils.isEmpty(uuid)) {
            getPresenter().shareFail(App.getAppContext().getString(R.string.error_uuid_null));
            return;
        }

        String token = ApiSignUtil.md5(uuid.concat(path));

        RetrofitClient.buildService(ApiService.class)
                .createShareLink(url, uuid, id, token)
                .enqueue(new Callback<CreateShareLinkResponse>() {
                    @Override
                    public void onResponse(Call<CreateShareLinkResponse> call, Response<CreateShareLinkResponse> response) {
                        if (response == null || response.body() == null) {
                            getPresenter().shareFail(App.getAppContext().getString(R.string.error_share_fail));
                            return;
                        }

                        CreateShareLinkResponse shareLinkResponse = response.body();

                        if (shareLinkResponse.getStatus() == Constants.StatusCode.STATUS_SUCCESS) {
                            getPresenter().shareSuccess(shareLinkResponse, path);
                            shareLinkResponse.getBody().setPath(path);
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
}
