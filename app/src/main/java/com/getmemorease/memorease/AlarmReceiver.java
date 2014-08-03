package com.getmemorease.memorease;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by Tommy on 8/2/2014.
 */
public class AlarmReceiver extends BroadcastReceiver {
    /*@Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, AlarmService.class);
        context.startService(service1);
    }*/

    @Override
    public void onReceive(Context context, Intent intent) {
        String item = intent.getStringExtra("item");
        String objectId = intent.getStringExtra("objectId");

        int notificationId = 001;

        Intent memorizeScreen = new Intent();
        memorizeScreen.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle extras = new Bundle();
        extras.putString("item", item);
        extras.putBoolean("dismiss", false);
        extras.putString("objectId", objectId);
        memorizeScreen.putExtras(extras);
        memorizeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, memorizeScreen,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create an intent for the reply action
        Intent actionIntent = new Intent();
        actionIntent.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle actionExtras = new Bundle();
        actionExtras.putString("item", item);
        actionExtras.putBoolean("dismiss", false);
        actionExtras.putString("objectId", objectId);
        actionIntent.putExtras(actionExtras);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(context, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_circle,
                        "Got it!", actionPendingIntent)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_circle)
                        .setContentTitle("Memorease")
                        .setContentText("1 card ready for memorization")
                        .setContentIntent(viewPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().addAction(action));

        // Create a big text style for the second page
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText(item);

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(context)
                        .setStyle(secondPageStyle)
                        .build();

        // Add second page with wearable extender and extend the main notification
        Notification twoPageNotification =
                new NotificationCompat.WearableExtender()
                        .addPage(secondPageNotification)
                        .extend(notificationBuilder)
                        .build();

        twoPageNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, twoPageNotification);
    }
}