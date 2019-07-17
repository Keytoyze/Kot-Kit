/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.util

import java.util.Random

object RandomUtils {

    private val random: Random by lazy {
        Random()
    }

    fun getRangeRandomInt(min: Int, max: Int): Int {
        return Math.abs(random.nextInt()) % (max - min + 1) + min
    }

    fun getRangeRandomFloat(min: Float, max: Float): Float {
        return Math.abs(random.nextInt()).toFloat() / Integer.MAX_VALUE * (max - min) + min
    }

    fun getRangeRandomLong(min: Long, max: Long): Long {
        return Math.abs(random.nextLong()) % (max - min + 1) + min
    }

    fun getRandomBytes(length: Int): ByteArray {
        val bytes = ByteArray(length)
        random.nextBytes(bytes)
        return bytes
    }
}
