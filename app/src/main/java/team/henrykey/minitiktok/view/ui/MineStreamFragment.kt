/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import androidx.lifecycle.LiveData
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.viewmodel.VideoViewModel

class MineStreamFragment: BaseStreamFragment() {

    override fun onLoad(viewModel: VideoViewModel): LiveData<List<Video>> {
        return viewModel.getMyVideos()
    }
}