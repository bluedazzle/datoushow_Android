package com.lypeer.zybuluo.impl;

import com.lypeer.zybuluo.model.bean.UpdateInfoBean;

/**
 * Created by lypeer on 2017/1/20.
 */

public interface OnCheckUpdateInfoListener {

    void success(boolean hasUpdate, UpdateInfoBean updateBean);

    void fail(String errorMessage);
}
