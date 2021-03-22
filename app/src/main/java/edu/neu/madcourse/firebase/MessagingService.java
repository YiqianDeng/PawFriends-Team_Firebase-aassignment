package edu.neu.madcourse.firebase;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        remoteMessage.getData();
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTag());
        }
    }

    /**
     * Citation: https://developer.android.com/training/notify-user/expanded
     */
    private void showNotification(String messageBody, String imageName) {
        Intent intent = new Intent(this, StickerReceivedActivity.class);
        intent.putExtra("sticker", imageName);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), Integer.parseInt(imageName));
        //set notification
        Notification notification =
                new NotificationCompat.Builder(this, "CHANNEL_ID")
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                        .setContentTitle("A New Sticker!")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(bitmap)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(bitmap)
                                .bigLargeIcon(null))
                        .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel("CHANNEL_ID",
                "newName", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(0, notification);
    }

}
