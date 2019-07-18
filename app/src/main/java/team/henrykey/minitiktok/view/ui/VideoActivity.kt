/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.model.Video

class VideoActivity : AppCompatActivity() {

    companion object {
        const val KEY_MODEL = "video"
        fun newIntent(context: Context, model: Video) = Intent(context, VideoActivity::class.java)
            .putExtra(KEY_MODEL, model)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<team.henrykey.minitiktok.databinding.ActivityVideoBinding>(this, R.layout.activity_video)
    }
}
