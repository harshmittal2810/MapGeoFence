package com.harsh.mapgeofence.data.remote;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.interfaces.StringRequestListener;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ApiHelper {

    @Inject
    ApiHelper() {
    }

    public void getData(StringRequestListener listener) {
        AndroidNetworking.get(ApiEndPoint.GET_DATA)
                .addQueryParameter("limit", "3")
                .build()
                .getAsString(listener);
    }
}
