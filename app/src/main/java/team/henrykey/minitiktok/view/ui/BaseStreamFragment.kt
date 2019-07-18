/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_stream.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.extension.showToast
import team.henrykey.minitiktok.model.Video
import team.henrykey.minitiktok.view.adapter.StreamAdapter
import team.henrykey.minitiktok.viewmodel.VideoViewModel

abstract class BaseStreamFragment : Fragment() {

    private val mViewModel by lazy {
        // different fragments share the same viewmodel
        ViewModelProviders.of(activity!!)[VideoViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stream, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCreate(streamList)

        onLoad(mViewModel).observe(this, Observer {
            onFinish(it)
            refreshLayout.isRefreshing = false
        })

        mViewModel.error().observe(this, Observer {
            context?.showToast(it.message)
            refreshLayout.isRefreshing = false
        })

        mViewModel.fetchVideoList()

        refreshLayout.setOnRefreshListener {
            mViewModel.fetchVideoList()
        }
    }

    abstract fun onCreate(recyclerView: RecyclerView)

    abstract fun onLoad(viewModel: VideoViewModel): LiveData<List<Video>>

    abstract fun onFinish(data: List<Video>)
}