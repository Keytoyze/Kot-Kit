/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.model

import com.google.gson.annotations.SerializedName
import team.henrykey.minitiktok.extension.e
import team.henrykey.minitiktok.util.RandomUtils
import java.util.*


data class Video @JvmOverloads constructor(
    @SerializedName("student_id")
    var studentId: String,
    @SerializedName("user_name")
    var userName: String,
    @SerializedName("image_url")
    var imageUrl: String,
    @SerializedName("_id")
    var id: String,
    @SerializedName("video_url")
    var videoUrl: String,
    @SerializedName("createdAt")
    var createdAt: Date,
    @SerializedName("updatedAt")
    var updatedAt: Date,
    // mock
    private var thumbCount: Int = 0
) {
    fun getThumbCount() : Int {
        if (thumbCount == 0) {
            thumbCount = RandomUtils.getRangeRandomInt(1, 500)
        }
        return thumbCount
    }
}