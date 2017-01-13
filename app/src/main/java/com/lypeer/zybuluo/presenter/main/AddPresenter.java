package com.lypeer.zybuluo.presenter.main;

import com.lypeer.zybuluo.model.main.AddModel;
import com.lypeer.zybuluo.presenter.base.BasePresenter;
import com.lypeer.zybuluo.ui.fragment.AddFragment;

/**
 * Created by lypeer on 2017/1/4.
 */

public class AddPresenter extends BasePresenter<AddFragment , AddModel> {
    @Override
    protected AddModel createModel() {
        return new AddModel(this);
    }
}
