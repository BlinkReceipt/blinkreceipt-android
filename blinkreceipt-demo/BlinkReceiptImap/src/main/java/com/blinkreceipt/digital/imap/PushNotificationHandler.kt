package com.blinkreceipt.digital.imap

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationHandler: FirebaseMessagingService() {

    private val messagingClient
        get() = (application as BlinkApplication).messagingClient

    override fun onMessageReceived(message: RemoteMessage) {
        messagingClient.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        messagingClient.onNewToken(token)
    }
}