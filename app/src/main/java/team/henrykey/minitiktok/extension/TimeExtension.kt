/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.extension

import android.content.Context
import team.henrykey.minitiktok.R
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(formatString: String): String {
    val formatter = SimpleDateFormat(formatString, Locale.US)
    return formatter.format(this)
}

fun Date.formatFriendly(context: Context): String {
    val epoch = time
    val now = System.currentTimeMillis()
    var delta = (now - epoch) / 1000
    if (delta < 60) {
        return context.getString(R.string.time_seconds_ago, delta.toInt())
    }
    delta /= 60
    if (delta < 60) {
        return context.getString(R.string.time_minutes_ago, delta.toInt())
    }
    delta /= 24
    if (delta < 7) {
        return context.getString(R.string.time_days_ago, delta.toInt())
    }
    return format(context.getString(R.string.time_year_month_day))
}

//fun String.toZonedDateTime(formatString: String): ZonedDateTime {
//    val formatter = DateTimeFormatter.ofPattern(formatString)
//    return ZonedDateTime.of(formatter.parse(this, LocalDate.FROM),
//        LocalTime.MIDNIGHT, ZoneId.systemDefault())
//}
