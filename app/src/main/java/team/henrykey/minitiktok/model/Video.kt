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

    fun getAvatarUrl() = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1563453306992&di=f5f617ac9abb155bff7eeb670193d0e7&imgtype=0&src=http%3A%2F%2Fb4-q.mafengwo.net%2Fs9%2FM00%2FBD%2F6E%2FwKgBs1ePxUSAS1prAAvmNsjxe2801.jpeg%3FimageView2%2F2%2Fw%2F680%2Fq%2F90"

    fun getAtAuthor() = "@$userName"
}