package com.lypeer.zybuluo.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/18.
 */

public class BannerResponse {


    /**
     * body : {"banner_list":[{"picture":"sss","remark":"sss","create_time":"2017-01-18 10:45:43","nav":0,"modify_time":"2017-01-18 10:45:43","active":true,"id":1}],"page_obj":{},"is_paginated":false}
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
         * banner_list : [{"picture":"sss","remark":"sss","create_time":"2017-01-18 10:45:43","nav":0,"modify_time":"2017-01-18 10:45:43","active":true,"id":1}]
         * page_obj : {}
         * is_paginated : false
         */

        private PageObjBean page_obj;
        private boolean is_paginated;
        private List<BannerListBean> banner_list;

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

        public List<BannerListBean> getBanner_list() {
            if(banner_list == null){
                return new ArrayList<>();
            }
            return banner_list;
        }

        public void setBanner_list(List<BannerListBean> banner_list) {
            this.banner_list = banner_list;
        }

        public static class PageObjBean {
        }

        public static class BannerListBean {
            /**
             * picture : sss
             * remark : sss
             * create_time : 2017-01-18 10:45:43
             * nav : 0
             * modify_time : 2017-01-18 10:45:43
             * active : true
             * id : 1
             */

            private String picture;
            private String remark;
            private String create_time;
            private int nav;
            private String modify_time;
            private boolean active;
            private int id;

            public String getPicture() {
                if(picture == null){
                    return "";
                }
                return picture;
            }

            public void setPicture(String picture) {
                this.picture = picture;
            }

            public String getRemark() {
                if(remark == null){
                    return "";
                }
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
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

            public int getNav() {
                return nav;
            }

            public void setNav(int nav) {
                this.nav = nav;
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

            public boolean isActive() {
                return active;
            }

            public void setActive(boolean active) {
                this.active = active;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }
        }
    }
}
