package com.lypeer.zybuluo.model.main;

import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.presenter.main.AddPresenter;

/**
 * Created by lypeer on 2017/1/4.
 */

public class AddModel extends BaseModel<AddPresenter> {
    public AddModel(AddPresenter addPresenter) {
        super(addPresenter);
    }

    @Override
    protected AddPresenter createPresenter() {
        return new AddPresenter();
    }
}
