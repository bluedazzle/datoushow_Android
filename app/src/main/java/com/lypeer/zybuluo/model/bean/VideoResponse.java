package com.lypeer.zybuluo.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/5.
 */

public class VideoResponse {

    /**
     * body : {"page_obj":{},"is_paginated":false,"video_list":[{"classification":1,"thumb_nail":"http://oda176fz0.bkt.clouddn.com/WeChatSight2.mp4?vframe/jpg/offset/1/w/200/h/200/","reference":"dd","title":"WeChatSight2","url":"http://oda176fz0.bkt.clouddn.com/WeChatSight2.mp4","author":"pp","create_time":"2016-09-12 15:31:52","modify_time":"2016-09-12 15:31:52","id":1,"like":0},{"classification":1,"thumb_nail":"http://oda176fz0.bkt.clouddn.com/别担心，我罩着你呢.avi?vframe/jpg/offset/1/w/200/h/200/","reference":"女间谍","title":"别担心，我罩着你呢","url":"http://oda176fz0.bkt.clouddn.com/别担心，我罩着你呢.avi","author":"皮皮","create_time":"2016-09-12 15:44:27","modify_time":"2016-09-12 15:44:27","id":2,"like":0}]}
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
        if(msg == null)
            return "";
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class BodyBean {
        /**
         * page_obj : {}
         * is_paginated : false
         * video_list : [{"classification":1,"thumb_nail":"http://oda176fz0.bkt.clouddn.com/WeChatSight2.mp4?vframe/jpg/offset/1/w/200/h/200/","reference":"dd","title":"WeChatSight2","url":"http://oda176fz0.bkt.clouddn.com/WeChatSight2.mp4","author":"pp","create_time":"2016-09-12 15:31:52","modify_time":"2016-09-12 15:31:52","id":1,"like":0},{"classification":1,"thumb_nail":"http://oda176fz0.bkt.clouddn.com/别担心，我罩着你呢.avi?vframe/jpg/offset/1/w/200/h/200/","reference":"女间谍","title":"别担心，我罩着你呢","url":"http://oda176fz0.bkt.clouddn.com/别担心，我罩着你呢.avi","author":"皮皮","create_time":"2016-09-12 15:44:27","modify_time":"2016-09-12 15:44:27","id":2,"like":0}]
         */

        private PageObjBean page_obj;
        private boolean is_paginated;
        private List<VideoListBean> video_list;

        public PageObjBean getPage_obj() {
            if(page_obj == null){
                return new PageObjBean();
            }
            return page_obj;
        }

        public void setPage_obj(PageObjBean page_obj) {
            this.page_obj = page_obj;
        }

        public boolean isIs_paginated() {
            return is_paginated;
        }

        public void setIs_paginated(boolean is_paginated) {
            this.is_paginated = is_paginated;
        }

        public List<VideoListBean> getVideo_list() {
            if(video_list == null){
                return new ArrayList<>();
            }
            return video_list;
        }

        public void setVideo_list(List<VideoListBean> video_list) {
            this.video_list = video_list;
        }

        public static class PageObjBean {
        }

        public static class VideoListBean {
            /**
             * classification : 1
             * thumb_nail : http://oda176fz0.bkt.clouddn.com/WeChatSight2.mp4?vframe/jpg/offset/1/w/200/h/200/
             * reference : dd
             * title : WeChatSight2
             * url : http://oda176fz0.bkt.clouddn.com/WeChatSight2.mp4
             * author : pp
             * create_time : 2016-09-12 15:31:52
             * modify_time : 2016-09-12 15:31:52
             * id : 1
             * like : 0
             */

            private int classification;
            private String thumb_nail;
            private String reference;
            private String title;
            private String url;
            private String author;
            private String create_time;
            private String modify_time;
            private int id;
            private int like;

            public int getClassification() {
                return classification;
            }

            public void setClassification(int classification) {
                this.classification = classification;
            }

            public String getThumb_nail() {
                if(thumb_nail == null)
                    return "";
                return thumb_nail;
            }

            public void setThumb_nail(String thumb_nail) {
                this.thumb_nail = thumb_nail;
            }

            public String getReference() {
                if(reference == null)
                    return "";
                return reference;
            }

            public void setReference(String reference) {
                this.reference = reference;
            }

            public String getTitle() {
                if(title == null)
                    return "";
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                if(url == null)
                    return "";
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getAuthor() {
                if(author == null)
                    return "";
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getCreate_time() {
                if(create_time == null)
                    return "";
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getModify_time() {
                if(modify_time == null)
                    return "";
                return modify_time;
            }

            public void setModify_time(String modify_time) {
                this.modify_time = modify_time;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getLike() {
                return like;
            }

            public void setLike(int like) {
                this.like = like;
            }
        }
    }
}
