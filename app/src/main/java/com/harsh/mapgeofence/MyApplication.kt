package com.harsh.mapgeofence

import android.app.Application
import android.content.Context
import com.androidnetworking.AndroidNetworking
import com.harsh.mapgeofence.injection.component.ApplicationComponent
import com.harsh.mapgeofence.injection.component.DaggerApplicationComponent
import com.harsh.mapgeofence.injection.module.ApplicationModule

class MyApplication : Application() {

    lateinit var mApplicationComponent: ApplicationComponent


    override fun onCreate() {
        super.onCreate()
        AndroidNetworking.initialize(applicationContext)
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
        mApplicationComponent.inject(this)
    }


    companion object {
        fun get(context: Context): MyApplication {
            return context.applicationContext as MyApplication
        }
    }

    fun getComponent(): ApplicationComponent {
        return mApplicationComponent
    }


    // Needed to replace the component with a test specific one
    fun setComponent(applicationComponent: ApplicationComponent) {
        mApplicationComponent = applicationComponent
    }
}