/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.player

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.widget.FrameLayout

import java.io.IOException

import androidx.annotation.AttrRes
import team.henrykey.minitiktok.widget.LoveLayout
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoPlayerIJK : LoveLayout {
    /**
     * 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView
     */
    var mMediaPlayer: IMediaPlayer? = null

    private var hasCreateSurfaceView = false
    /**
     * 视频文件地址
     */
    private var mPath = ""
    private var resId = 0

    private var surfaceView: SurfaceView? = null

    private var listener: VideoPlayerListener? = null
    private var mContext: Context? = null


    val duration: Long
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.duration
        } else {
            0
        }


    val currentPosition: Long
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.currentPosition
        } else {
            0
        }

    val isPlaying: Boolean
        get() = if (mMediaPlayer != null) {
            mMediaPlayer!!.isPlaying
        } else false

    constructor(context: Context) : super(context) {
        initVideoView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initVideoView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initVideoView(context)
    }

    private fun initVideoView(context: Context) {
        mContext = context
        isFocusable = true
    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    fun setVideoPath(path: String) {
        mPath = path
        load()
        createSurfaceView()
    }

    fun setVideoResource(resourceId: Int) {
        resId = resourceId
        load(resId)
        createSurfaceView()
    }

    /**
     * 新建一个surfaceview
     */
    private fun createSurfaceView() {
        if (hasCreateSurfaceView) {
            return
        }
        //生成一个新的surface view
        surfaceView = SurfaceView(mContext)
        surfaceView!!.holder.addCallback(PlayerSurfaceCallback())
        val layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        surfaceView!!.layoutParams = layoutParams
        this.addView(surfaceView)
        hasCreateSurfaceView = true
    }

    /**
     * surfaceView的监听器
     */
    private inner class PlayerSurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            //surfaceview创建成功后，加载视频
            //给mediaPlayer设置视图
            mMediaPlayer!!.setDisplay(holder)
            mMediaPlayer!!.prepareAsync()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }

    /**
     * 加载视频
     */
    private fun load() {
        //每次都要重新创建IMediaPlayer
        createPlayer()
        try {
            mMediaPlayer!!.dataSource = mPath
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    /**
     * 加载视频
     */
    private fun load(resourceId: Int) {
        //每次都要重新创建IMediaPlayer
        createPlayer()
        val fileDescriptor = mContext!!.resources.openRawResourceFd(resourceId)
        val provider = RawDataSourceProvider(fileDescriptor)
        mMediaPlayer?.run {
            setDataSource(provider)
            isLooping = true
            surfaceView?.run {
                setDisplay(holder)
                prepareAsync()
            }
        }

    }

    /**
     * 创建一个新的player
     */
    private fun createPlayer() {
        mMediaPlayer?.run {
            stop()
            setDisplay(null)
            release()
        }
        val ijkMediaPlayer = IjkMediaPlayer()

        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)

        mMediaPlayer = ijkMediaPlayer
        (mMediaPlayer as IjkMediaPlayer).setSpeed(1f)
        mMediaPlayer?.run {
            setOnPreparedListener(listener)
            setOnInfoListener(listener)
            setOnSeekCompleteListener(listener)
            setOnBufferingUpdateListener(listener)
            setOnErrorListener(listener)
        }
    }


    fun setListener(listener: VideoPlayerListener) {
        this.listener = listener
        mMediaPlayer?.run {
            setOnPreparedListener(listener)
            setOnInfoListener(listener)
            setOnSeekCompleteListener(listener)
            setOnBufferingUpdateListener(listener)
            setOnErrorListener(listener)
            setOnCompletionListener(listener)
        }
    }

    /**
     * -------======--------- 下面封装了一下控制视频的方法
     */

    fun start() {
            mMediaPlayer?.start()
    }

    fun release() {
        mMediaPlayer?.run {
            reset()
            release()
        }
        mMediaPlayer = null
    }

    fun pause() {
        mMediaPlayer?.pause()

    }

    fun stop() {
        mMediaPlayer?.stop()

    }


    fun reset() {
        mMediaPlayer?.reset()

    }

    fun seekTo(l: Long) {
        mMediaPlayer?.seekTo(l)
    }
}
