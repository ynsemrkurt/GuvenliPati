package com.example.guvenlipati

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.guvenlipati.advert.AdvertActivity
import com.example.guvenlipati.chat.ChatActivity
import com.example.guvenlipati.myjobs.MyJobsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FirebaseService : FirebaseMessagingService() {

    val CHANNEL_ID = "my_notification_channel"

    companion object {
        var sharedPref: SharedPreferences? = null
        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    override fun onMessageReceived(newToken: RemoteMessage) {
        super.onMessageReceived(newToken)
        val intentMessage = Intent(this, ChatActivity::class.java)
        intentMessage.putExtra("userId", newToken.data["userId"])
        val intentAdvert = Intent(this, AdvertActivity::class.java)
        val intentMyJob = Intent(this, MyJobsActivity::class.java)
        val intentMyJobComplete = Intent(this, MyJobsActivity::class.java)
        intentMyJobComplete.putExtra("status", true)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intentMessage.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intentMessage,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        intentMyJob.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val myJobIntent = PendingIntent.getActivity(
            this,
            0,
            intentMyJob,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        intentAdvert.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val advertIntent = PendingIntent.getActivity(
            this,
            0,
            intentAdvert,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        intentMyJobComplete.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val myJobCompleteIntent = PendingIntent.getActivity(
            this,
            0,
            intentMyJobComplete,
            FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val profileImageUrl = newToken.data["profileImageUrl"]

        val notificationType = newToken.data["notificationType"]

        if (notificationType == "0" || notificationType == null) {
            notificationSystem(
                notificationManager,
                notificationId,
                pendingIntent,
                profileImageUrl ?: "",
                newToken.data["title"] ?: "",
                newToken.data["message"] ?: ""
            )
        } else if (notificationType == "1") {
            notificationSystem(
                notificationManager,
                notificationId,
                advertIntent,
                profileImageUrl ?: "",
                newToken.data["title"] ?: "",
                newToken.data["message"] ?: ""
            )
        } else if (notificationType == "2") {
            notificationSystem(
                notificationManager,
                notificationId,
                myJobIntent,
                profileImageUrl ?: "",
                newToken.data["title"] ?: "",
                newToken.data["message"] ?: ""
            )
        }else if (notificationType=="3"){
            notificationSystem(
                notificationManager,
                notificationId,
                myJobCompleteIntent,
                profileImageUrl ?: "",
                newToken.data["title"] ?: "",
                newToken.data["message"] ?: ""
            )
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelFirebaseChat"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun notificationSystem(
        notificationManager: NotificationManager,
        notificationId: Int,
        intent: PendingIntent,
        photo: String,
        title: String,
        message: String
    ) {
        Glide.with(this)
            .asBitmap()
            .load(photo)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    val notification =
                        NotificationCompat.Builder(this@FirebaseService, CHANNEL_ID)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setSmallIcon(R.drawable.baseline_notifications_24)
                            .setLargeIcon(resource)
                            .setAutoCancel(true)
                            .setContentIntent(intent)
                            .build()

                    notificationManager.notify(notificationId, notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }
}
