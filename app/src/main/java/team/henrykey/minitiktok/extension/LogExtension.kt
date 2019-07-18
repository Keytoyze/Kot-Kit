/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

@file:Suppress("ConstantConditionIf")

package team.henrykey.minitiktok.extension

import android.util.Log

const val TAG = "MiniTikTok"
const val ENABLE_LOG = true

fun <T> T.i() {
    if (ENABLE_LOG) {
        Log.i(TAG, buildMessage(this))
    }
}

fun <T> T.e() {
    if (ENABLE_LOG) {
        Log.e(TAG, buildMessage(this))
    }
}

fun <T> T.d() {
    if (ENABLE_LOG) {
        Log.d(TAG, buildMessage(this))
    }
}

fun <T> T.v() {
    if (ENABLE_LOG) {
        Log.v(TAG, buildMessage(this))
    }
}

fun <T> T.w() {
    if (ENABLE_LOG) {
        Log.w(TAG, buildMessage(this))
    }
}

fun <T> T.wtf() {
    if (ENABLE_LOG) {
        Log.wtf(TAG, buildMessage(this))
    }
}

fun <T> T.println() {
    if (ENABLE_LOG) {
        System.out.println(TAG + ": " + buildMessage(this))
    }
}

private fun <T> buildMessage(rawMessage: T): String {
    val caller = Throwable().stackTrace[2]
    val fullClassName = caller.className
    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    return "[${Thread.currentThread().name}] $className.${caller.methodName}(): $rawMessage"
}