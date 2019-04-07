package com.harsh.mapgeofence.ui.base

import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity

import com.harsh.mapgeofence.MyApplication
import com.harsh.mapgeofence.injection.component.ActivityComponent
import com.harsh.mapgeofence.injection.component.DaggerActivityComponent
import com.harsh.mapgeofence.injection.module.ActivityModule
import com.harsh.mapgeofence.utils.DialogUtils

abstract class BaseActivity : AppCompatActivity(), MvpView {

    private var mActivityComponent: ActivityComponent? = null
    private var progressDialog: ProgressDialog? = null

    fun activityComponent(): ActivityComponent? {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(ActivityModule(this))
                    .applicationComponent(MyApplication.get(this).getComponent())
                    .build()
        }
        return mActivityComponent
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun showLoading() {
        hideLoading()
        progressDialog = DialogUtils.showLoadingDialog(this)
    }

    override fun hideLoading() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }

}