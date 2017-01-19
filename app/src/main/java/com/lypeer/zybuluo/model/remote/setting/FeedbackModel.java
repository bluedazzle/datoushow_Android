package com.lypeer.zybuluo.model.remote.setting;

import com.bugtags.library.Bugtags;
import com.lypeer.zybuluo.model.base.BaseModel;
import com.lypeer.zybuluo.presenter.setting.FeedbackPresenter;

/**
 * Created by lypeer on 2017/1/18.
 */

public class FeedbackModel extends BaseModel<FeedbackPresenter> {
    public FeedbackModel(FeedbackPresenter feedbackPresenter) {
        super(feedbackPresenter);
    }

    @Override
    protected FeedbackPresenter createPresenter() {
        return new FeedbackPresenter();
    }

    public void commit(String content, String contactWay) {
        Bugtags.setUserData("联系方式", contactWay);
        Bugtags.sendFeedback(content);
        getPresenter().commitSuccess();
    }
}
