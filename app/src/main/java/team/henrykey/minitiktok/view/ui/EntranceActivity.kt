/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.view.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_entrance.*
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.util.RandomUtils

class EntranceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_entrance)

        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                nameText.setTextColor(RandomUtils.getRangeRandomLong(0xFF000000, 0xFFFFFFFF).toInt())
                nameText.translationX = RandomUtils.getRangeRandomFloat(0f, 20f)
                nameText.translationY = RandomUtils.getRangeRandomFloat(0f, 20f)
                nameText.setTextSize(TypedValue.COMPLEX_UNIT_PX, nameText.textSize
                    + RandomUtils.getRangeRandomFloat(-5f, 5f))
                nameText.rotation += RandomUtils.getRangeRandomFloat(-0.5f, 0.5f)
                nameText.rotationX += RandomUtils.getRangeRandomFloat(-0.5f, 0.5f)
                nameText.rotationY += RandomUtils.getRangeRandomFloat(-0.5f, 0.5f)
            }
            start()
        }
        Handler().postDelayed({
            if (LoginActivity.hasLogin(this)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }, 3000)
    }
}
