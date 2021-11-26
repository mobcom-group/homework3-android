package com.lazudanizaidan.chatapplication.api

import com.lazudanizaidan.chatapplication.Chat
import com.lazudanizaidan.chatapplication.ApiResponse
import com.lazudanizaidan.chatapplication.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    @Headers("Content-Type: application/json")
    @POST("send-message")
    fun sendMessage(@Body chat: Chat): Call<ApiResponse>

    @Headers("Content-Type: application/json")
    @POST("register-token")
    fun registerToken(@Body token: Token?): Call<ApiResponse>

}