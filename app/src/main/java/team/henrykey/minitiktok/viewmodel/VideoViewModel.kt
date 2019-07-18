/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import team.henrykey.minitiktok.api.ApiContract
import team.henrykey.minitiktok.api.IMiniDouyinService
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.view.ui.LoginActivity
import java.util.concurrent.TimeUnit

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val disposable = CompositeDisposable()
    private val getLiveData = MutableLiveData<List<Video>>()
    private val myLiveData = MutableLiveData<List<Video>>()
    private val errorLiveData = MutableLiveData<Throwable>()

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

    fun getVideos() = getLiveData

    fun getMyVideos() = myLiveData

    fun error() = errorLiveData

    fun fetchVideoList() {
        disposable.add(service.videos
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                getLiveData.value = it.feeds
                myLiveData.value = it.feeds.filter {
                    it.studentId == LoginActivity.getCode(getApplication())
                }
            }, {
                errorLiveData.value = it
            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}