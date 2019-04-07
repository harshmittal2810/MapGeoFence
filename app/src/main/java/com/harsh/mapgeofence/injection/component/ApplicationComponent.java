package com.harsh.mapgeofence.injection.component;

import android.app.Application;
import android.content.Context;
import com.harsh.mapgeofence.MyApplication;
import com.harsh.mapgeofence.data.DataManager;
import com.harsh.mapgeofence.injection.annotation.ApplicationContext;
import com.harsh.mapgeofence.injection.module.ApplicationModule;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MyApplication myApplication);

    @ApplicationContext
    Context context();

    Application application();
    DataManager dataManager();

}
