package com.lypeer.zybuluo.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/13.
 */

public class VideoDetailResponse {

    /**
     * body : {"video":{"classification":3,"thumb_nail":"http://static.fibar.cn/nishihouzi_0.mp4?vframe/jpg/offset/0/w/200/h/200/","reference":"","author":"红孩儿","url":"http://static.fibar.cn/nishihouzi_0.mp4","weibo_title":"变身热门短视频中的主角，释放你的洪荒之力，快来一起玩吧~","title":"你是猴子请来的救兵吗","tag":"","wechat_sub_title":"变身热门短视频中的主角，释放你的洪荒之力，快来一起玩吧~","order":9,"tracks":[{"frame":1,"time":0,"y":100,"x":237,"rotation":0,"size":0},{"frame":177,"time":7.08,"y":19,"x":114,"rotation":0,"size":328}],"create_time":"2016-11-01 11:52:18","fps":25,"duration":7.08,"hidden":true,"total_frames":177,"wechat_title":"","id":135,"like":7}}
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
         * video : {"classification":3,"thumb_nail":"http://static.fibar.cn/nishihouzi_0.mp4?vframe/jpg/offset/0/w/200/h/200/","reference":"","author":"红孩儿","url":"http://static.fibar.cn/nishihouzi_0.mp4","weibo_title":"变身热门短视频中的主角，释放你的洪荒之力，快来一起玩吧~","title":"你是猴子请来的救兵吗","tag":"","wechat_sub_title":"变身热门短视频中的主角，释放你的洪荒之力，快来一起玩吧~","order":9,"tracks":[{"frame":1,"time":0,"y":100,"x":237,"rotation":0,"size":0},{"frame":177,"time":7.08,"y":19,"x":114,"rotation":0,"size":328}],"create_time":"2016-11-01 11:52:18","fps":25,"duration":7.08,"hidden":true,"total_frames":177,"wechat_title":"","id":135,"like":7}
         */

        private VideoBean video;

        public VideoBean getVideo() {
            if(video == null){
                return new VideoBean();
            }
            return video;
        }

        public void setVideo(VideoBean video) {
            this.video = video;
        }

        public static class VideoBean {
            /**
             * classification : 3
             * thumb_nail : http://static.fibar.cn/nishihouzi_0.mp4?vframe/jpg/offset/0/w/200/h/200/
             * reference :
             * author : 红孩儿
             * url : http://static.fibar.cn/nishihouzi_0.mp4
             * weibo_title : 变身热门短视频中的主角，释放你的洪荒之力，快来一起玩吧~
             * title : 你是猴子请来的救兵吗
             * tag :
             * wechat_sub_title : 变身热门短视频中的主角，释放你的洪荒之力，快来一起玩吧~
             * order : 9
             * tracks : [{"frame":1,"time":0,"y":100,"x":237,"rotation":0,"size":0},{"frame":177,"time":7.08,"y":19,"x":114,"rotation":0,"size":328}]
             * create_time : 2016-11-01 11:52:18
             * fps : 25
             * duration : 7.08
             * hidden : true
             * total_frames : 177
             * wechat_title :
             * id : 135
             * like : 7
             */

            private int classification;
            private String thumb_nail;
            private String reference;
            private String author;
            private String url;
            private String weibo_title;
            private String title;
            private String tag;
            private String wechat_sub_title;
            private int order;
            private String create_time;
            private int fps;
            private double duration;
            private boolean hidden;
            private int total_frames;
            private String wechat_title;
            private int id;
            private int like;
            private List<TracksBean> tracks;

            public int getClassification() {
                return classification;
            }

            public void setClassification(int classification) {
                this.classification = classification;
            }

            public String getThumb_nail() {
                if(thumb_nail == null){
                    return "";
                }
                return thumb_nail;
            }

            public void setThumb_nail(String thumb_nail) {
                this.thumb_nail = thumb_nail;
            }

            public String getReference() {
                if(reference == null){
                    return "";
                }
                return reference;
            }

            public void setReference(String reference) {
                this.reference = reference;
            }

            public String getAuthor() {
                if(author == null){
                    return "";
                }
                return author;
            }

            public void setAuthor(String author) {
                this.author = author;
            }

            public String getUrl() {
                if(url == null){
                    return "";
                }
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getWeibo_title() {
                if(weibo_title == null){
                    return "";
                }
                return weibo_title;
            }

            public void setWeibo_title(String weibo_title) {
                this.weibo_title = weibo_title;
            }

            public String getTitle() {
                if(title == null){
                    return "";
                }
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTag() {
                if(tag == null){
                    return "";
                }
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public String getWechat_sub_title() {
                if(wechat_sub_title == null){
                    return "";
                }
                return wechat_sub_title;
            }

            public void setWechat_sub_title(String wechat_sub_title) {
                this.wechat_sub_title = wechat_sub_title;
            }

            public int getOrder() {
                return order;
            }

            public void setOrder(int order) {
                this.order = order;
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

            public int getFps() {
                return fps;
            }

            public void setFps(int fps) {
                this.fps = fps;
            }

            public double getDuration() {
                return duration;
            }

            public void setDuration(double duration) {
                this.duration = duration;
            }

            public boolean isHidden() {
                return hidden;
            }

            public void setHidden(boolean hidden) {
                this.hidden = hidden;
            }

            public int getTotal_frames() {
                return total_frames;
            }

            public void setTotal_frames(int total_frames) {
                this.total_frames = total_frames;
            }

            public String getWechat_title() {
                if(wechat_title == null){
                    return "";
                }
                return wechat_title;
            }

            public void setWechat_title(String wechat_title) {
                this.wechat_title = wechat_title;
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

            public List<TracksBean> getTracks() {
                if(tracks == null){
                    new ArrayList<>();
                }
                return tracks;
            }

            public void setTracks(List<TracksBean> tracks) {
                this.tracks = tracks;
            }

            public static class TracksBean {
                /**
                 * frame : 1
                 * time : 0
                 * y : 100
                 * x : 237
                 * rotation : 0
                 * size : 0
                 */

                private int frame;
                private int time;
                private int y;
                private int x;
                private int rotation;
                private int size;

                public int getFrame() {
                    return frame;
                }

                public void setFrame(int frame) {
                    this.frame = frame;
                }

                public int getTime() {
                    return time;
                }

                public void setTime(int time) {
                    this.time = time;
                }

                public int getY() {
                    return y;
                }

                public void setY(int y) {
                    this.y = y;
                }

                public int getX() {
                    return x;
                }

                public void setX(int x) {
                    this.x = x;
                }

                public int getRotation() {
                    return rotation;
                }

                public void setRotation(int rotation) {
                    this.rotation = rotation;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }
            }
        }
    }
}
