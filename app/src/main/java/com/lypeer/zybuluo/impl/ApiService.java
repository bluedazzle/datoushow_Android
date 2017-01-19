package com.lypeer.zybuluo.impl;


import com.lypeer.zybuluo.model.bean.BannerResponse;
import com.lypeer.zybuluo.model.bean.CreateShareLinkResponse;
import com.lypeer.zybuluo.model.bean.UploadResponse;
import com.lypeer.zybuluo.model.bean.VideoDetailResponse;
import com.lypeer.zybuluo.model.bean.VideoResponse;
import com.lypeer.zybuluo.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lypeer on 2017/1/5.
 */

public interface ApiService {

    @GET("videos/")
    Call<VideoResponse> getHotVideos(
            @Query(Constants.RequestParam.K_PAGE) int currentPage,
            @Query(Constants.RequestParam.K_LIKE) int like
    );

    @GET("videos/")
    Call<VideoResponse> getTypeVideos(
            @Query(Constants.RequestParam.K_PAGE) int currentPage,
            @Query(Constants.RequestParam.K_TYPE) int videosType
    );

    @GET("/video/{id}/")
    Call<VideoDetailResponse> getVideoDetail(
            @Path(Constants.RequestParam.K_PAGE) int videoId
    );

    @GET("videos/")
    Call<VideoResponse> search(
            @Query(Constants.RequestParam.K_SEARCH) String searchContent
    );

    @GET("upload")
    Call<UploadResponse> upload();


    @GET("banners")
    Call<BannerResponse> getBanner();

    @FormUrlEncoded
    @POST("share/")
    Call<CreateShareLinkResponse> createShareLink(
            @Field(Constants.RequestParam.K_URL) String url,
            @Field(Constants.RequestParam.K_UID) String uid,
            @Field(Constants.RequestParam.K_VID) int vid,
            @Field(Constants.RequestParam.K_TOKEN) String token
    );
}
