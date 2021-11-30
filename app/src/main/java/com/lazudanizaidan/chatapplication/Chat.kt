package com.lazudanizaidan.chatapplication

data class Chat(
    var message: String? = null,
    var imagePath: String? = null,
    var time: String? = null,
    var senderUUID: String? = null,
    var imageURL: String? = null
)
