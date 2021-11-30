package com.lazudanizaidan.chatapplication

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        val intent = Intent("message")
        if(remoteMessage.notification!!.body == "image") {
            intent.putExtra("message", remoteMessage.notification!!.imageUrl.toString())
            intent.putExtra("type", "image")
        } else {
            intent.putExtra("message", remoteMessage.notification!!.body)
            intent.putExtra("type", "text")
        }
        intent.putExtra("time", remoteMessage.data["time"])
        intent.putExtra("senderUUID", remoteMessage.data["senderUUID"])
        LocalBroadcastManager.getInstance(this@MyFirebaseMessagingService).sendBroadcast(intent)
    }
    // [END receive_message]
}