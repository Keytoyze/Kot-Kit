/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.databinding.DataBindingUtil
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_video.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.databinding.ActivityVideoBinding
import team.henrykey.minitiktok.extension.e
import team.henrykey.minitiktok.extension.getColorCompat
import team.henrykey.minitiktok.extension.showToast
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.player.VideoPlayerListener
import team.henrykey.minitiktok.util.PictureUtils
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.File
import java.util.concurrent.TimeUnit

class VideoActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val KEY_MODEL = "video"
        fun newIntent(context: Context, model: Video): Intent = Intent(context, VideoActivity::class.java)
            .putExtra(KEY_MODEL, model)
    }

    private val mModel: Video by lazy {
        intent.getSerializableExtra(KEY_MODEL) as Video
    }

    private lateinit var binding: ActivityVideoBinding

    private var disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityVideoBinding>(
            this,
            R.layout.activity_video
        )
            .apply {
                model = mModel
                isPlaying = mIsPlaying
                isLoading = true
            }

        coverView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation))
        progressBar.setColor(getColorCompat(R.color.green_600))
        disposable.add(Observable.interval(20, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                refresh()
            }
        )

        ijkPlayer.setOnLovedListener {
            mModel.isLoved = true
            binding.model = mModel
        }

        init()
        initIJKPlayer()
    }

    internal var mVideoWidth = 0
    internal var mVideoHeight = 0

    internal var isPlayFinish = false
    var mIsPlaying = true


//    override fun onWindowFocusChanged(hasFocus: Boolean) {
////        super.onWindowFocusChanged(hasFocus)
////        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
////            val decorView = window.decorView
////            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
////                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
////                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                or View.SYSTEM_UI_FLAG_FULLSCREEN
////                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
////        }
////    }

    private fun init() {
        ijkPlayer.setOnClickListener {
            if (mIsPlaying) {
                ijkPlayer.pause()
            } else {
                ijkPlayer.start()
            }
            mIsPlaying = !mIsPlaying
            binding.isPlaying = mIsPlaying
        }
    }

    private fun refresh() {

//        val duration = ijkPlayer.duration / 1000
//
//        val currentSeconds = current / 1000
//        val currentSecond = String.format("%02d", currentSeconds % 60)
//        val currentMinute = String.format("%02d", currentSeconds / 60)
//        val totalSecond = String.format("%02d", duration % 60)
//        val totalMinute = String.format("%02d", duration / 60)
//        val time = "$currentMinute:$currentSecond/$totalMinute:$totalSecond"
//        tv_time.text = time
//        if (duration != 0L) {
//            seekBar.progress = (currentSeconds * seekBar.max / duration).toInt()
//        }
        progressBar.setProgress(ijkPlayer.currentPosition.toFloat() / ijkPlayer.duration)

    }

    private fun initIJKPlayer() {
        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null)
            IjkMediaPlayer.native_profileBegin("libijkplayer.so")
        } catch (e: Exception) {
            this.finish()
        }

//        ijkPlayer.setListener(VideoPlayerListener())
        //ijkPlayer.setVideoResource(R.raw.yuminhong);

        val file = File(cacheDir, "${mModel.videoUrl.hashCode()}.mp4")
        val markFile = File(cacheDir, "${mModel.videoUrl.hashCode()}.download")
        if (markFile.exists()) { // download stops before
            file.delete()
            markFile.delete()
        }
        markFile.createNewFile()
        disposable.add(PictureUtils.downloadVideo(mModel.videoUrl, file)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                binding.loadingProgress = "$it%"
            }, {
                it.printStackTrace()
                showToast(it.message)
            }, {
                ijkPlayer.setVideoPath(file.absolutePath)
                binding.isLoading = false
                markFile.delete()
            })
        )


        /*ijkPlayer.setVideoResource(R.raw.big_buck_bunny);
        ijkPlayer.setVideoPath("https://media.w3.org/2010/05/sintel/trailer.mp4");
        ijkPlayer.setVideoPath("http://vjs.zencdn.net/v/oceans.mp4");*/

        ijkPlayer.setListener(object : VideoPlayerListener() {
            override fun onBufferingUpdate(iMediaPlayer: IMediaPlayer, percent: Int) {}

            override fun onCompletion(iMediaPlayer: IMediaPlayer) {
//                seekBar.progress = 100
//                btn_play.text = "播放"
//                btn_stop.text = "播放"
            }

            override fun onError(iMediaPlayer: IMediaPlayer, what: Int, extra: Int): Boolean {
                return false
            }

            override fun onInfo(iMediaPlayer: IMediaPlayer, what: Int, extra: Int): Boolean {
                return false
            }

            override fun onPrepared(iMediaPlayer: IMediaPlayer) {
                refresh()
                isPlayFinish = false
                iMediaPlayer.isLooping = true

                mVideoWidth = iMediaPlayer.videoWidth
                mVideoHeight = iMediaPlayer.videoHeight
                videoScreenInit()
                iMediaPlayer.start()
            }

            override fun onSeekComplete(iMediaPlayer: IMediaPlayer) {}

            override fun onVideoSizeChanged(
                iMediaPlayer: IMediaPlayer, width: Int, height: Int,
                sar_num: Int, sar_den: Int
            ) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        if (ijkPlayer != null && ijkPlayer.isPlaying) {
            ijkPlayer!!.stop()
        }
        IjkMediaPlayer.native_profileEnd()
    }

    override fun onDestroy() {
        ijkPlayer.stop()
        ijkPlayer.release()
        disposable?.dispose()
        super.onDestroy()
    }

    override fun onClick(view: View) {

    }

    private fun videoScreenInit() {
        val wm = this
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outSize = Point()
        wm.defaultDisplay.getSize(outSize)
        val windowWidth = outSize.x.toFloat()
        val windowHeight = outSize.y.toFloat()
        val winRatio = windowWidth / windowHeight
        val videoRatio = mVideoWidth.toFloat() / mVideoHeight

        "win: ${winRatio}, video: ${videoRatio} $mVideoWidth $mVideoHeight".e()

        ijkPlayer.updateLayoutParams {
            if (winRatio > videoRatio) {
                width = (windowHeight * videoRatio).toInt()
            } else {
                height = (windowWidth / videoRatio).toInt()
            }
        }
    }
}
