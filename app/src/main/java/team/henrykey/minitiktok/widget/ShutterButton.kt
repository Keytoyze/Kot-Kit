/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import team.henrykey.minitiktok.extension.dp2px
import team.henrykey.minitiktok.extension.e

class ShutterButton : View {

    private val mStrokeWidth = context.dp2px(4f).toFloat()
    private val mPaint = Paint()
    private val mStrokePaint = Paint()
    private val mRectPaint = Paint()
    private var mDegree = 0f
    var recording = false
    private val mRect = RectF()
    private val mStopRect = RectF()

    init {
        mPaint.run {
            color = Color.RED
            style = Paint.Style.FILL
            strokeWidth = mStrokeWidth * 2
            isAntiAlias = true
        }
        mStrokePaint.run {
            color = Color.RED
            strokeWidth = mStrokeWidth
            style = Paint.Style.STROKE
            alpha = 127
            isAntiAlias = true
        }
        mRectPaint.run {
            color = Color.RED
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        isClickable = true
    }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setDegree(degree: Float) {
        mDegree = degree
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = Math.min(width, height).toFloat()
        val center = w / 2
        canvas.run {
            mRect.run {
                left = mStrokeWidth
                top = mStrokeWidth
                right = w - mStrokeWidth
                bottom = w - mStrokeWidth
            }
            drawArc(mRect, mDegree - 90f, 360f - mDegree, false, mStrokePaint)
            if (!recording) {
                mPaint.style = Paint.Style.FILL
                drawCircle(center, center, w / 2 - mStrokeWidth * 2f, mPaint)
            } else {
                mPaint.style = Paint.Style.STROKE
                drawCircle(center, center, w / 2 - mStrokeWidth * 3f, mPaint)
                mStopRect.run {
                    left = center * 0.7f
                    top = center * 0.7f
                    right = center * 1.3f
                    bottom = center * 1.3f
                }
                drawRect(mStopRect, mRectPaint)
            }
        }
    }
}