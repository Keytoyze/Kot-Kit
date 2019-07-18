/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import team.henrykey.minitiktok.api.ApiContract
import team.henrykey.minitiktok.api.IMiniDouyinService
import team.henrykey.minitiktok.util.UriUtils
import java.util.concurrent.TimeUnit

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable = CompositeDisposable()

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(ApiContract.BASE_URL)
            .client(OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).build())
            .build()
    }

    private val service by lazy {
        retrofit.create(IMiniDouyinService::class.java)
    }


    fun postVideo(context: Context, mSelectedImage: Uri, mSelectedVideo: Uri, callback: (Boolean) -> Unit) {
        val coverImagePart = UriUtils.getMultipartFromUri(context, "cover_image", mSelectedImage)
        val videoPart = UriUtils.getMultipartFromUri(context, "video", mSelectedVideo)

        disposable.add(
            service.postVideo("42", "The Answer", coverImagePart, videoPart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response ->
                    response.also {
                        callback(it.isSuccess)
                    }
                }.subscribe()
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}