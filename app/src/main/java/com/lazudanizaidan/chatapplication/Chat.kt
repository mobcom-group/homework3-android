package com.lazudanizaidan.chatapplication

data class Chat(
    var message: String? = null,
    var imagebase64: String? = null,
    var time: String? = null,
    var senderUUID: String? = null,
    var imageURL: String? = null
)
