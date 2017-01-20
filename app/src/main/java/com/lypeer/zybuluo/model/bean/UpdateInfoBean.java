package com.lypeer.zybuluo.model.bean;

/**
 * Created by lypeer on 2017/1/20.
 */

public class UpdateInfoBean {


    /**
     * body : {"update":{"android_verison":"1.1.0","android_download":"http://static.fibar.cn/datoushow1-1-0.apk","create_time":"2017-01-20 14:46:12","modify_time":"2017-01-20 14:50:28","android_title":"a","id":1,"android_log":"a"}}
     * status : 1
     * msg : success
     */

    private BodyBean body;
    private int status;
    private String msg;

    public BodyBean getBody() {
        if(body == null){
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
        if(msg == null){
            return "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class BodyBean {
        /**
         * update : {"android_verison":"1.1.0","android_download":"http://static.fibar.cn/datoushow1-1-0.apk","create_time":"2017-01-20 14:46:12","modify_time":"2017-01-20 14:50:28","android_title":"a","id":1,"android_log":"a"}
         */

        private UpdateBean update;

        public UpdateBean getUpdate() {
            if(update == null){
                return new UpdateBean();
            }
            return update;
        }

        public void setUpdate(UpdateBean update) {
            this.update = update;
        }

        public static class UpdateBean {
            /**
             * android_verison : 1.1.0
             * android_download : http://static.fibar.cn/datoushow1-1-0.apk
             * create_time : 2017-01-20 14:46:12
             * modify_time : 2017-01-20 14:50:28
             * android_title : a
             * id : 1
             * android_log : a
             */

            private String android_verison;
            private String android_download;
            private String create_time;
            private String modify_time;
            private String android_title;
            private int id;
            private String android_log;

            public String getAndroid_verison() {
                if(android_verison == null){
                    return "";
                }
                return android_verison;
            }

            public void setAndroid_verison(String android_verison) {
                this.android_verison = android_verison;
            }

            public String getAndroid_download() {
                if(android_download == null){
                    return "";
                }
                return android_download;
            }

            public void setAndroid_download(String android_download) {
                this.android_download = android_download;
            }

            public String getCreate_time() {
                if(create_time == null){
                    return "";
                }
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getModify_time() {
                if(modify_time == null){
                    return "";
                }
                return modify_time;
            }

            public void setModify_time(String modify_time) {
                this.modify_time = modify_time;
            }

            public String getAndroid_title() {
                if(android_title == null){
                    return "";
                }
                return android_title;
            }

            public void setAndroid_title(String android_title) {
                this.android_title = android_title;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getAndroid_log() {
                if(android_log == null){
                    return "";
                }
                return android_log;
            }

            public void setAndroid_log(String android_log) {
                this.android_log = android_log;
            }
        }
    }
}
