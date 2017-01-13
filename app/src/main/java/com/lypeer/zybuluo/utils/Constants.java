package com.lypeer.zybuluo.utils;

/**
 * Created by lypeer on 2017/1/4.
 */

public class Constants {

    public interface FragmentId {
        int
                ADD = 0,
                MY = 1;
    }

    public interface ContentType {
        String
                JSON = "application/json",
                PNG = "image/png",
                JPEG = "image/jpeg",
                FROM_DATA = "multipart/form-data";
    }

    public interface ApiSign {
        String
                K_TIMESTAMP = "timestamp",
                K_DEVICE_TYPE = "deviceType",
                K_SIGN = "sign",
                K_WECHAT = "wxf191b8af9283c36c",
                K_WEIBO = "3564446011",
                K_QQ = "1105947674",
                V_DEVICE_TYPE = "android",
                V_SECRET = "eKxzhDfGkvcv9MaLQdeWgSlqnX4CosiIkR17Z0oAPmNUjBOw6nlHcTfupzbFhupy",
                V_WECHAT = "a80d2b79afff7ddc298ecd4d01cfc183",
                V_WEIBO = "d39dbb40fd1a75e4c5972089a0078ebd",
                V_QQ = "zGuM3I5lrFMBLxDZ";
    }

    public interface RequestParam {
        String
                K_PAGE = "page",
                K_TYPE = "type",
                K_SEARCH = "search",
                K_LIKE = "like",
                K_URL = "url",
                K_UID = "uid",
                K_VID = "vid",
                K_TOKEN = "token";
    }

    public interface VideosType {
        int
                TYPE_FILM_TV = 1,
                TYPE_MV = 2,
                TYPE_FUNNY = 3,
                TYPE_VARIETY = 4;
    }

    public interface StatusCode {
        int
                STATUS_UNKNOWN = 0,
                STATUS_SUCCESS = 1,
                STATUS_PERMISSION_DENIED = 2,
                STATUS_ACCOUNT_MISSED = 3,
                STATUS_DATA_WRONG = 4,
                STATUS_PASSWORD_WRONG = 5,
                STATUS_EXISTED = 6,
                STATUS_NOT_EXISTED = 7,
                STATUS_OVERDUE = 8,
                STATUS_VERIFY_CODE_EMPTY = 9,
                STATUS_VERIFY_CODE_WRONG = 10;
    }


}
