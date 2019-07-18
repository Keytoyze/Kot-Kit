/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.widget.FragmentPagerImpl

@RuntimePermissions
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

        addButton.setOnClickListener {
            toCameraWithPermissionCheck()
        }
    }

    @NeedsPermission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    fun toCamera() {
        startActivity(Intent(this, CustomCameraActivity::class.java))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
