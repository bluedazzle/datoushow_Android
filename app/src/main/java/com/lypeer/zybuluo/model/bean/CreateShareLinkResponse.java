package com.lypeer.zybuluo.model.bean;

/**
 * Created by lypeer on 2017/1/11.
 */

public class CreateShareLinkResponse {

    /**
     * body : {"url":"xxxx","thumb_nail":"xxxx","weibo_title":"xxxx","wechat_title":"xxxx","wechat_sub_title":"xxxx"}
     * status : 1
     * msg : success
     */

    private BodyBean body;
    private int status;
    private String msg;

    public BodyBean getBody() {
        if (body == null) {
            return new BodyBean();
        }
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        if (msg == null) {
            return "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class BodyBean {
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

        public String getUrl() {
            if (url == null) {
                return "";
            }
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getThumb_nail() {
            if (thumb_nail == null) {
                return "";
            }
            return thumb_nail;
        }

        public void setThumb_nail(String thumb_nail) {
            this.thumb_nail = thumb_nail;
        }

        public String getWeibo_title() {
            if (weibo_title == null) {
                return "";
            }
            return weibo_title;
        }

        public void setWeibo_title(String weibo_title) {
            this.weibo_title = weibo_title;
        }

        public String getWechat_title() {
            if (wechat_title == null) {
                return "";
            }
            return wechat_title;
        }

        public void setWechat_title(String wechat_title) {
            this.wechat_title = wechat_title;
        }

        public String getWechat_sub_title() {
            if (wechat_sub_title == null) {
                return "";
            }
            return wechat_sub_title;
        }

        public void setWechat_sub_title(String wechat_sub_title) {
            this.wechat_sub_title = wechat_sub_title;
        }
    }
}
