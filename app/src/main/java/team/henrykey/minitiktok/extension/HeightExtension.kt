/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.extension

import android.content.Context
import android.graphics.Point
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.WindowManager

fun Context.getStatusBarHeight(): Int? {
    val resId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resId > 0) {
        return resources.getDimensionPixelSize(resId)
    }
    return null
}

fun Context.checkDeviceHasNavigationBar(): Boolean {
    val hasMenuKey = ViewConfiguration.get(this)
        .hasPermanentMenuKey()
    val hasBackKey = KeyCharacterMap
        .deviceHasKey(KeyEvent.KEYCODE_BACK)
    return !hasMenuKey and !hasBackKey
}

fun Context.getNavigationBarHeight(): Int {
    var result = 0
    if (checkDeviceHasNavigationBar()) {
        val res = resources
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = res.getDimensionPixelSize(resourceId)
        }
    }
    return result
}

fun Context.getScreenWidth(): Int {
    val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    wm.defaultDisplay.getSize(point)
    return point.y
}
