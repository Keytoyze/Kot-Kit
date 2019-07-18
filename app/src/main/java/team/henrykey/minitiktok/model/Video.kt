/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.model

import com.google.gson.annotations.SerializedName
import team.henrykey.minitiktok.extension.e
import team.henrykey.minitiktok.util.RandomUtils
import java.io.Serializable
import java.util.*


data class Video constructor(
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
    @SerializedName("image_w")
    var imageWidth: Float,
    @SerializedName("image_h")
    var imageHeight: Float
) : Serializable {

    var isLoved = false
    // mock
    fun getThumbCount(): Int {
        if (isLoved) {
            return Math.abs(hashCode()) % 5000 + 1
        } else {
            return Math.abs(hashCode()) % 5000
        }
    }

    val avatarUrl = "http://img4.imgtn.bdimg.com/it/u=3521439206,3191058990&amp;fm=26&amp;gp=0.jpg"
}