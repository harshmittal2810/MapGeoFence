package com.harsh.mapgeofence.ui.main;

import com.harsh.mapgeofence.data.DataManager;
import com.harsh.mapgeofence.data.listners.DataListener;
import com.harsh.mapgeofence.ui.base.BasePresenter;

import javax.inject.Inject;

public class MainPresenter<V extends MainMvpView> extends BasePresenter<V> implements MainMvpPresenter<V> {

    private final DataManager mDataManager;

    @Inject
    MainPresenter(DataManager dataManager) {
        this.mDataManager = dataManager;
    }

    @Override
    public void getData() {

//        getMvpView().showLoading();
//
//        mDataManager.getData(new DataListener() {
//            @Override
//            public void onResponse(String data) {
//                getMvpView().hideLoading();
//                getMvpView().showData(data);
//            }
//
//            @Override
//            public void onError(String error) {
//                getMvpView().hideLoading();
//                getMvpView().showData(error);
//            }
//        });
    }
}
