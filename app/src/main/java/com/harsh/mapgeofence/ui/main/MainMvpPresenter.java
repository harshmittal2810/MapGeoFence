package com.harsh.mapgeofence.ui.main;

import com.harsh.mapgeofence.ui.base.MvpPresenter;

public interface MainMvpPresenter<V extends MainMvpView> extends MvpPresenter<V> {

    void getData();

}
