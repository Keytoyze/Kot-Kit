/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.player

import tv.danmaku.ijk.media.player.IMediaPlayer

open class VideoPlayerListener : IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnCompletionListener,
    IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnVideoSizeChangedListener,
    IMediaPlayer.OnErrorListener, IMediaPlayer.OnSeekCompleteListener {
    override fun onBufferingUpdate(iMediaPlayer: IMediaPlayer, percent: Int) {
        println(percent)
    }

    override fun onCompletion(iMediaPlayer: IMediaPlayer) {

    }

    override fun onError(iMediaPlayer: IMediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onInfo(iMediaPlayer: IMediaPlayer, what: Int, extra: Int): Boolean {
        return false
    }

    override fun onPrepared(iMediaPlayer: IMediaPlayer) {}

    override fun onSeekComplete(iMediaPlayer: IMediaPlayer) {

    }

    override fun onVideoSizeChanged(iMediaPlayer: IMediaPlayer, width: Int, height: Int, sar_num: Int, sar_den: Int) {

    }
}
