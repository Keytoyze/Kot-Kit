/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.viewmodel.VideoViewModel

class StreamFragment: BaseStreamFragment() {

    override fun onLoad(viewModel: VideoViewModel): LiveData<List<Video>> {
        return viewModel.getVideos()
    }
}