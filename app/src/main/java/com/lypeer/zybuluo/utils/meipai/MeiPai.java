package com.lypeer.zybuluo.utils.meipai;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.meitu.meipaimv.sdk.modelmsg.MeipaiMessage;
import com.meitu.meipaimv.sdk.modelmsg.MeipaiSendMessageRequest;
import com.meitu.meipaimv.sdk.modelmsg.MeipaiVideoObject;

/**
 * Created by lypeer on 2017/1/13.
 */

public class MeiPai {

    private final Activity mActivity;

    public MeiPai(Activity activity) {
        this.mActivity = activity;
    }


    public void share(String videoUrl) {
        //@todo 美拍的分享，老说文件异常
        if(TextUtils.isEmpty(videoUrl)){
            return;
        }

        if (!MeiPaiFactory.getInstance().isMeipaiAppInstalled()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse("http://www.coolapk.com/apk/com.meitu.meipaimv");
            intent.setData(content_url);
            mActivity.startActivity(intent);
            return;
        }

        MeipaiMessage message = new MeipaiMessage();
        MeipaiVideoObject videoObject = new MeipaiVideoObject();
        videoObject.videoPath = videoUrl;

        message.setMediaObject(videoObject);

        MeipaiSendMessageRequest request = new MeipaiSendMessageRequest();
        request.setMessage(message);

        request.setTransaction(String.valueOf(System.currentTimeMillis()));
        request.setScene(MeipaiSendMessageRequest.MP_SCENE_VIDEO);


        MeiPaiFactory.getInstance().sendRequest(mActivity, request);
    }
}
