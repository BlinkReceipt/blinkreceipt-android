package com.blinkreceipt.linking

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.microblink.core.InitializeCallback
import com.microblink.linking.BlinkReceiptLinkingSdk.initialize
import com.microblink.messaging.MessagingClient
import com.microblink.messaging.MessagingResults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class BlinkApplication : Application() {

    private val parentJob = SupervisorJob()

    private val applicationScope = CoroutineScope(parentJob + Dispatchers.Default)

    val messagingClient by lazy {
        MessagingClient(applicationContext, applicationScope)
    }

    override fun onCreate() {
        super.onCreate()

        initialize(this, object : InitializeCallback {
            override fun onComplete() {
                Log.d(TAG, "account linking initialization complete")
            }

            override fun onException(throwable: Throwable) {
                Log.e(TAG, "failure in initialize", throwable)
            }
        })

        createNotificationChannel()

        collectBackgroundRefreshOrders()
    }

    private fun collectBackgroundRefreshOrders() {
        try {
            applicationScope.launch {
                var orderCount = 0

                messagingClient.results.collect { messagingResults: MessagingResults ->
                    when (messagingResults) {
                        is MessagingResults.Linking -> {
                            val (retailerId, scanResults, remaining, uuid) = messagingResults

                            orderCount++

                            Log.d(
                                TAG,
                                "Background order retailer[$retailerId] uuid[$uuid] scanResult: $scanResults"
                            )

                            if (remaining == 0) {
                                showNotification("Retailer[${retailerId}] $orderCount new orders available!")

                                orderCount = 0
                            }
                        }
                        is MessagingResults.Error -> {
                            showNotification(
                                "Error: retailer[${messagingResults.retailerId}] ${messagingResults.throwable}"
                            )

                            orderCount = 0
                        }
                        else -> {
                            Log.d(TAG, "Unsupported Messaging Result Type")
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception at collectBackgroundRefreshOrders", e)
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = NOTIFICATION_CHANNEL_NAME
            val description = NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                name,
                importance
            ).apply {
                this.description = description
            }

            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(channel)
        }
    }

    private fun showNotification(message: String) {
        val notificationId = 1

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.mipmap.sym_def_app_icon)
            .setContentTitle("Account Linking Background Refresh")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            notificationManager.notify(notificationId, notification)
        }
    }

    private companion object {
        private const val TAG = "AccountLinking"

        const val NOTIFICATION_CHANNEL_NAME: String = "Background Refresh"

        const val NOTIFICATION_CHANNEL_ID: String = "account_linking_background_refresh"

        const val NOTIFICATION_CHANNEL_DESCRIPTION: String = "Alert for orders collected via Background Refresh"
    }
}
