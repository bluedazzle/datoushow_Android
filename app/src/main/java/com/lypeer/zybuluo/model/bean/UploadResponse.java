package com.lypeer.zybuluo.model.bean;

/**
 * Created by lypeer on 2017/1/11.
 */

public class UploadResponse {


    /**
     * body : {"token":"vh2MauzMWeMyo87-WSrcvN46JBU3WWqpZdgtZypl:pF-nSmeVKUCb2kn8rXtbFNmukLU=:eyJzY29wZSI6ImZsaXBweSIsImRlYWRsaW5lIjoxNDg0MTQwNDc2fQ=="}
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
         * token : vh2MauzMWeMyo87-WSrcvN46JBU3WWqpZdgtZypl:pF-nSmeVKUCb2kn8rXtbFNmukLU=:eyJzY29wZSI6ImZsaXBweSIsImRlYWRsaW5lIjoxNDg0MTQwNDc2fQ==
         */

        private String token;

        public String getToken() {
            if(token == null){
                return "";
            }
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
