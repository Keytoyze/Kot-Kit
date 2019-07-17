/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.extension

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat


fun Context.showToast(msg: String?, duration: Int = Toast.LENGTH_SHORT) {
    msg?.let { Toast.makeText(this, msg, duration).show() }
}

fun Context.showToast(msgRes: Int?, duration: Int = Toast.LENGTH_SHORT) {
    msgRes?.let { Toast.makeText(this, msgRes, duration).show() }
}

fun Context.getDrawableCompat(resId: Int): Drawable {
    return ContextCompat.getDrawable(this, resId)!!
}

fun Context.getColorCompat(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun Context.dp2px(dp: Float): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun Activity.openUrl(url: String) {
    try {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.showInput(et: EditText) {
    et.post {
        et.requestFocus()
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)?.run {
            showSoftInput(et, SHOW_IMPLICIT)
        }
    }
}


fun Activity.hideInput() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    window.peekDecorView()?.run {
        imm!!.hideSoftInputFromWindow(windowToken, 0)
    }
}
