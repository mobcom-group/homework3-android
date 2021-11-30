package com.lazudanizaidan.chatapplication.api

import com.lazudanizaidan.chatapplication.Chat
import com.lazudanizaidan.chatapplication.ApiResponse
import com.lazudanizaidan.chatapplication.Token
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @Headers("Content-Type: application/json")
    @POST("send-message")
    fun sendMessage(@Body chat: Chat): Call<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("register-token")
    fun registerToken(@Body token: Token?): Call<ApiResponse>

    @Multipart
    @POST("send-image")
    fun sendImage(@Part("senderUUID") senderUUID: RequestBody, @Part("time") time:RequestBody, @Part image: MultipartBody.Part): Call<ApiResponse>

}