/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.view.adapter.StreamAdapter
import team.henrykey.minitiktok.viewmodel.VideoViewModel

class MineStreamFragment: BaseStreamFragment() {

    private val mAdapter: StreamAdapter by lazy {
        StreamAdapter(ArrayList())
    }

    override fun onLoad(viewModel: VideoViewModel): LiveData<List<Video>> {
        return viewModel.getMyVideos()
    }

    override fun onCreate(recyclerView: RecyclerView) {
        recyclerView.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onFinish(data: List<Video>) {
        mAdapter.setList(data)
    }
}