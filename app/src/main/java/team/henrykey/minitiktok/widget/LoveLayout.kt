/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.widget

import android.animation.*
import android.content.Context
import android.os.Handler
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.postDelayed
import team.henrykey.minitiktok.R
import team.henrykey.minitiktok.extension.e
import team.henrykey.minitiktok.extension.getDrawableCompat
import team.henrykey.minitiktok.util.RandomUtils

// Reference: https://github.com/PangHaHa12138/DouyinDemo/blob/master/app/src/main/java/com/example/administrator/douyin/Love2.kt
open class LoveLayout : FrameLayout {

    companion object {
        const val INTERVAL_THRESHOLD = 400L
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //动画中随机❤的旋转角度
    private var num = floatArrayOf(-35f, -25f, 0f, 25f, 35f)

    //用来判断是否是连续的点击事件
    private val mHits = LongArray(3)
    private val mHandler = Handler()
    private var mCallback: (() -> Unit)? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {

        event.e()
        if (event.action != MotionEvent.ACTION_DOWN) {
            return super.onTouchEvent(event)
        }
        System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
        mHits[mHits.size - 1] = SystemClock.uptimeMillis()

        //用这个来判断是否是3击事件，判断数组中pos=2的点击事件的时间与数组中pos=0的点
        // 击事件的时间差值是否小于INTERVAL_THRESHOLD，若是小于500认为是3击事件，这时需要绘制爱心图片
        if (mHits[0] >= (SystemClock.uptimeMillis() - INTERVAL_THRESHOLD)) {
            //点击是触发心形的图片add到整个view中，然后执行动画

            //有连续触摸的时候，创建一个展示心形的图片
            val iv = ImageView(context)

            //设置展示的位置，需要在手指触摸的位置上方，即触摸点是心形的右下角的位置
            val lp = LayoutParams(300, 300)
            lp.leftMargin = (event.x - 150F).toInt()
            lp.topMargin = (event.y - 300F).toInt()
            //设置图片资源
            iv.setImageDrawable(context.getDrawableCompat(R.mipmap.icon_home_like_after))
            iv.layoutParams = lp

            //把IV添加到父布局当中
            addView(iv)

            //设置控件的动画
            val animatorSet = AnimatorSet()
            animatorSet.play(
                //缩放动画，X轴2倍缩小至0.9倍
                scaleAni(iv, "scaleX", 2f, 0.9f, 100, 0))
                //缩放动画，Y轴2倍缩放至0.9倍
                .with(scaleAni(iv, "scaleY", 2f, 0.9f, 100, 0))
                //旋转动画，随机旋转角
                .with(rotation(iv, 0, 0, num[RandomUtils.getRangeRandomInt(0, 4)]))
                //渐变透明动画，透明度从0-1
                .with(alphaAni(iv, 0F, 1F, 100, 0))
                //缩放动画，X轴0.9倍缩小至
                .with(scaleAni(iv, "scaleX", 0.9f, 1F, 50, 150))
                //缩放动画，Y轴0.9倍缩放至
                .with(scaleAni(iv, "scaleY", 0.9f, 1F, 50, 150))
                //位移动画，Y轴从0上移至600
                .with(translationY(iv, 0F, -600F, 800, 400))
                //透明动画，从1-0
                .with(alphaAni(iv, 1F, 0F, 300, 400))
                //缩放动画，X轴1至3倍
                .with(scaleAni(iv, "scaleX", 1F, 3f, 700, 400))
                //缩放动画，Y轴1至3倍
                .with(scaleAni(iv, "scaleY", 1F, 3f, 700, 400))
            //开始动画
            animatorSet.start()
            //设置动画结束监听
            animatorSet.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    //当动画结束以后，需要把控件从父布局移除
                    removeViewInLayout(iv)
                }
            })

            mCallback?.invoke()
        } else {
            val pre = mHits[2]
            mHandler.postDelayed({
                if (mHits[2] == pre) { // No new touch events occur
                    performClick()
                }
            }, INTERVAL_THRESHOLD + 50)
        }
        return true
    }

    fun setOnLovedListener(callback: () -> Unit) {
        mCallback = callback
    }

    private fun scaleAni(view: View, propertyName: String, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
        val ani: ObjectAnimator = ObjectAnimator.ofFloat(view, propertyName, from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }

    private fun translationX(view: View, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
        val ani: ObjectAnimator = ObjectAnimator.ofFloat(view, "translationX", from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }

    private fun translationY(view: View, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
        val ani: ObjectAnimator = ObjectAnimator.ofFloat(view, "translationY", from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }

    private fun alphaAni(view: View, from: Float, to: Float, time: Long, delayTime: Long): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "alpha", from, to)
        ani.interpolator = LinearInterpolator()
        ani.startDelay = delayTime
        ani.duration = time
        return ani
    }

    private fun rotation(view: View, time: Long, delayTime: Long, vararg values: Float): ObjectAnimator {
        val ani = ObjectAnimator.ofFloat(view, "rotation", *values)
        ani.duration = time
        ani.startDelay = delayTime
        ani.interpolator = TimeInterpolator { input -> input }
        return ani
    }
}