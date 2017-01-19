package com.lypeer.zybuluo.model.local.main;

import com.lypeer.zybuluo.model.bean.BodyBean;

import io.realm.Realm;

/**
 * Created by lypeer on 2017/1/16.
 */

public class MyModelLocal {

    public void insert(final BodyBean bodyBean) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(bodyBean);
            }
        });
    }

    public BodyBean get(String path) {

        return Realm
                .getDefaultInstance()
                .where(BodyBean.class)
                .equalTo("path", path)
                .findFirst();
    }

    public void delete(final String path) {
        Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                BodyBean bodyBean = realm.where(BodyBean.class)
                        .equalTo("path", path)
                        .findFirst();
                if (bodyBean != null && bodyBean.isValid()) {
                    bodyBean.deleteFromRealm();
                }
            }
        } , null , null);
    }
}
