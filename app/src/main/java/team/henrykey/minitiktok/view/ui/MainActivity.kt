/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.widget.FragmentPagerImpl

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setTitle(R.string.app_name)

        viewPager.adapter = FragmentPagerImpl(
            supportFragmentManager, arrayOf(
            StreamFragment(), MineStreamFragment()
        ), listOf(
            R.string.stream_stream,
            R.string.stream_mine
        ).map { getString(it) }.toTypedArray()
        )

        tabLayout.setupWithViewPager(viewPager)
    }
}
