package com.lypeer.zybuluo.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lypeer on 2017/1/21.
 */

public class ClassificationsBean {

    /**
     * body : {"classification_list":[{"index":1,"select_icon":"http://static.fibar.cn/nsprings@3x.png","type":5,"name":"春节","icon":"http://static.fibar.cn/nspring@3x.png"},{"index":2,"select_icon":"http://static.fibar.cn/nfunnys@3x.png","type":3,"name":"搞笑","icon":"http://static.fibar.cn/nfunny@3x.png"},{"index":3,"select_icon":"http://static.fibar.cn/nMVs@3x.png","type":2,"name":"MV","icon":"http://static.fibar.cn/nMV@3x.png"},{"index":4,"select_icon":"http://static.fibar.cn/nvideos@3x.png","type":1,"name":"影视","icon":"http://static.fibar.cn/nvideo@3x.png"},{"index":5,"select_icon":"http://static.fibar.cn/nvarietys@3x.png","type":4,"name":"综艺","icon":"http://static.fibar.cn/nvariety@3x.png"}],"page_obj":{},"is_paginated":false}
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
         * classification_list : [{"index":1,"select_icon":"http://static.fibar.cn/nsprings@3x.png","type":5,"name":"春节","icon":"http://static.fibar.cn/nspring@3x.png"},{"index":2,"select_icon":"http://static.fibar.cn/nfunnys@3x.png","type":3,"name":"搞笑","icon":"http://static.fibar.cn/nfunny@3x.png"},{"index":3,"select_icon":"http://static.fibar.cn/nMVs@3x.png","type":2,"name":"MV","icon":"http://static.fibar.cn/nMV@3x.png"},{"index":4,"select_icon":"http://static.fibar.cn/nvideos@3x.png","type":1,"name":"影视","icon":"http://static.fibar.cn/nvideo@3x.png"},{"index":5,"select_icon":"http://static.fibar.cn/nvarietys@3x.png","type":4,"name":"综艺","icon":"http://static.fibar.cn/nvariety@3x.png"}]
         * page_obj : {}
         * is_paginated : false
         */

        private PageObjBean page_obj;
        private boolean is_paginated;
        private List<ClassificationListBean> classification_list;

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

        public List<ClassificationListBean> getClassification_list() {
            if(classification_list == null){
                return new ArrayList<>();
            }
            return classification_list;
        }

        public void setClassification_list(List<ClassificationListBean> classification_list) {
            this.classification_list = classification_list;
        }

        public static class PageObjBean {
        }

        public static class ClassificationListBean {
            /**
             * index : 1
             * select_icon : http://static.fibar.cn/nsprings@3x.png
             * type : 5
             * name : 春节
             * icon : http://static.fibar.cn/nspring@3x.png
             */

            private int index;
            private String select_icon;
            private int type;
            private String name;
            private String icon;

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public String getSelect_icon() {
                if(select_icon == null){
                    return "";
                }
                return select_icon;
            }

            public void setSelect_icon(String select_icon) {
                this.select_icon = select_icon;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getName() {
                if(name == null){
                    return "";
                }
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getIcon() {
                if(icon == null){
                    return "";
                }
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }
    }
}
