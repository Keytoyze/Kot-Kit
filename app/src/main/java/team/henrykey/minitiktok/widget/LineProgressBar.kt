/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import team.henrykey.minitiktok.extension.e

class LineProgressBar : View {

    private var mProgress: Float = 0f
    private var mRect = Rect(0, 0, 0, 0)
    private var mPaint = Paint()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setProgress(progress: Float) {
        mProgress = progress
        invalidate()
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val drawWidth = width * mProgress
        mRect.right = drawWidth.toInt()
        mRect.bottom = height
        canvas.drawRect(mRect, mPaint)
    }
}