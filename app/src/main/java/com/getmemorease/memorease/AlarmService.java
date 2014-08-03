package com.getmemorease.memorease;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;

/**
 * Created by Tommy on 8/2/2014.
 */
/*public class AlarmService extends Service {

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    @SuppressWarnings("static-access")
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);

        int notificationId = 001;

        Intent memorizeScreen = new Intent();
        memorizeScreen.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle extras = new Bundle();
        extras.putString("item", "item");
        extras.putBoolean("dismiss", false);
        extras.putString("objectId", "item");
        memorizeScreen.putExtras(extras);
        memorizeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, memorizeScreen,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create an intent for the reply action
        Intent actionIntent = new Intent();
        actionIntent.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle actionExtras = new Bundle();
        actionExtras.putString("item", "item");
        actionExtras.putBoolean("dismiss", true);
        actionExtras.putString("objectId", "item");
        actionIntent.putExtras(actionExtras);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_circle,
                        "Got it!", actionPendingIntent)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_circle)
                        .setContentTitle("Memorease")
                        .setContentText("1 card ready for memorization")
                        .setContentIntent(viewPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().addAction(action));

        // Create a big text style for the second page
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText("item");

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(getApplicationContext())
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
                NotificationManagerCompat.from(getApplicationContext());

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, twoPageNotification);
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}*/

public class AlarmService {
    private Context context;
    private PendingIntent mAlarmSender;
    private long time;
    public AlarmService(Context context, Bundle cardInfo) {
        this.context = context;
        this.time = cardInfo.getLong("time");
        Intent intent = new Intent();
        intent.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.AlarmReceiver");
        Bundle extras = new Bundle();
        extras.putString("item", cardInfo.getString("item"));
        extras.putString("objectId", cardInfo.getString("objectId"));
        intent.putExtras(extras);
        mAlarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public void startAlarm(){
        //Set the alarm to 10 seconds from now
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        long firstTime = c.getTimeInMillis();
        // Schedule the alarm!
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, firstTime, mAlarmSender);
    }
}