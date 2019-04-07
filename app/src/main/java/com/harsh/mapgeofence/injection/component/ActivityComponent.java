package com.harsh.mapgeofence.injection.component;

import com.harsh.mapgeofence.injection.module.ActivityModule;
import com.harsh.mapgeofence.ui.main.MainActivity;
import com.harsh.mapgeofence.injection.annotation.PerActivity;
import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}