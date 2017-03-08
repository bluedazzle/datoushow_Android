package com.lypeer.zybuluo.model.bean;

import io.realm.RealmObject;

/**
 * Created by lypeer on 2017/1/16.
 */

public class BodyBean extends RealmObject {

    public BodyBean() {
        super();
    }

    public BodyBean(CreateShareLinkResponse.BodyBean bodyBean) {
        super();
        this.setPath(bodyBean.getPath());
        this.setThumb_nail(bodyBean.getThumb_nail());
        this.setUrl(bodyBean.getUrl());
        this.setWechat_title(bodyBean.getWechat_title());
        this.setWechat_sub_title(bodyBean.getWechat_sub_title());
        this.setWeibo_title(bodyBean.getWeibo_title());
    }

    /**
     * url : xxxx
     * thumb_nail : xxxx
     * weibo_title : xxxx
     * wechat_title : xxxx
     * wechat_sub_title : xxxx
     */

    private String url;
    private String thumb_nail;
    private String weibo_title;
    private String wechat_title;
    private String wechat_sub_title;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb_nail() {
        return thumb_nail;
    }

    public void setThumb_nail(String thumb_nail) {
        this.thumb_nail = thumb_nail;
    }

    public String getWeibo_title() {
        return weibo_title;
    }

    public void setWeibo_title(String weibo_title) {
        this.weibo_title = weibo_title;
    }

    public String getWechat_title() {
        return wechat_title;
    }

    public void setWechat_title(String wechat_title) {
        this.wechat_title = wechat_title;
    }

    public String getWechat_sub_title() {
        return wechat_sub_title;
    }

    public void setWechat_sub_title(String wechat_sub_title) {
        this.wechat_sub_title = wechat_sub_title;
    }

    public CreateShareLinkResponse getCreateShareLinkResponse() {
        CreateShareLinkResponse response = new CreateShareLinkResponse();

        CreateShareLinkResponse.BodyBean bodyBean = response.getBody();
        bodyBean.setPath(this.getPath());
        bodyBean.setThumb_nail(this.getThumb_nail());
        bodyBean.setUrl(this.getUrl());
        bodyBean.setWechat_title(this.getWechat_title());
        bodyBean.setWechat_sub_title(this.getWechat_sub_title());
        bodyBean.setWeibo_title(this.getWeibo_title());


        response.setBody(bodyBean);
        return response;
    }
}
