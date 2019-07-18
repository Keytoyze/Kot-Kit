/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import io.reactivex.plugins.RxJavaPlugins

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)

        RxJavaPlugins.setErrorHandler {
            it.printStackTrace()
        }
    }
}