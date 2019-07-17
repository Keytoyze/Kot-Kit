/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.extension

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

fun View.setMarginTop(top: Int?) {
    top?.let {
        val param = layoutParams as ViewGroup.MarginLayoutParams
        param.topMargin = it
        layoutParams = param
    }
}

fun <T : ViewDataBinding> ViewGroup.inflateDataBinding(layoutRes: Int): T {
    return DataBindingUtil.inflate(
        LayoutInflater.from(context),
        layoutRes, this, false
    )
}

fun <T : ViewDataBinding> Activity.setBindingContentView(layoutRes: Int): T {
    return DataBindingUtil.setContentView(this, layoutRes)
}

fun View.setBackgroundCompat(res: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = context.getDrawableCompat(res)
    } else {
        @Suppress("DEPRECATION")
        setBackgroundDrawable(context.getDrawableCompat(res))
    }
}

fun Boolean.toVisibility(reverse: Boolean = false) =
    if (this && !reverse || !this && reverse) View.VISIBLE else View.INVISIBLE