package com.harsh.mapgeofence.ui.main;

import com.harsh.mapgeofence.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showData(String data);

    void showError(String error);
}
