/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.extension

import android.net.Uri

fun String?.toCompatUri(): Uri? {
    return this?.let {
        when {
            it.startsWith("www") -> return@let Uri.parse("https://$it")
            it.startsWith("//") -> return@let Uri.parse("https:$it")
            else -> return@let Uri.parse(it)
        }
    }
}