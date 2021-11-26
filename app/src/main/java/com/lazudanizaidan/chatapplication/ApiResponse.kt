package com.lazudanizaidan.chatapplication


import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("response")
    val response: String,
    @SerializedName("status")
    val status: String
)