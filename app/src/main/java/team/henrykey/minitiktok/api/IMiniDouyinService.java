/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.api;

import team.henrykey.minitiktok.model.GetVideoResponse;
import team.henrykey.minitiktok.model.PostVideoResponse;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import retrofit2.http.*;

public interface IMiniDouyinService {

    @Multipart
    @POST("video")
    Single<PostVideoResponse> postVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image,
            @Part MultipartBody.Part video
    );

    @GET("video")
    Single<GetVideoResponse> getVideos();
}
