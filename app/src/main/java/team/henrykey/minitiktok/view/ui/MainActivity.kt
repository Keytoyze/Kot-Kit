/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.extension.showToast
import team.henrykey.minitiktok.view.adapter.StreamAdapter
import team.henrykey.minitiktok.viewmodel.VideoViewModel

class MainActivity : AppCompatActivity() {

    private val mAdapter: StreamAdapter by lazy {
        StreamAdapter(ArrayList())
    }

    private val mViewModel by lazy {
        ViewModelProviders.of(this)[VideoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        streamList.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        mViewModel.getVideos().observe(this, Observer {
            mAdapter.setList(it)
            refreshLayout.isRefreshing = false
        })

        mViewModel.error().observe(this, Observer {
            showToast(it.message)
            refreshLayout.isRefreshing = false
        })

        mViewModel.fetchVideoList()

        refreshLayout.setOnRefreshListener {
            mViewModel.fetchVideoList()
        }
    }
}
