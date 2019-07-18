/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.view.adapter.StaggeredAdapter
import team.henrykey.minitiktok.viewmodel.VideoViewModel

class StreamFragment : BaseStreamFragment() {

    private val mMineAdapter: StaggeredAdapter by lazy {
        StaggeredAdapter(ArrayList())
    }

    override fun onLoad(viewModel: VideoViewModel): LiveData<List<Video>> {
        return viewModel.getVideos()
    }

    override fun onFinish(data: List<Video>) {
        mMineAdapter.setList(data)
    }

    override fun onCreate(recyclerView: RecyclerView) {
        recyclerView.run {
            adapter = mMineAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}