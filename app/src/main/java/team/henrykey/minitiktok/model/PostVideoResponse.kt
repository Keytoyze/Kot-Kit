/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.model

import com.google.gson.annotations.SerializedName

data class PostVideoResponse(
    @SerializedName("url")
    var url: String,
    @SerializedName("success")
    var isSuccess: Boolean
)