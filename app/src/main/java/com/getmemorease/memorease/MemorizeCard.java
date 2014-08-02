package com.getmemorease.memorease;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.Time;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Calendar;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Tommy on 7/22/2014.
 */
public class MemorizeCard extends Card {

    protected String mTitleHeader;
    protected String mTitleMain;
    private Time nextTimer;
    private int currentLevel;
    private String objectId;
    private Context _context;

    public MemorizeCard(Context context, String titleHeader, int currentLevel, Time startTime) {
        //add to db
        super(context);
        this._context = context;
        this.mTitleHeader = titleHeader;
        this.currentLevel = currentLevel;
        this.nextTimer = startTime;
        this.mTitleMain = "  Time until next memorization: " + timeOfNextTimer(currentLevel, startTime);
        init();

        //need to figure out how to push at certain time
        pushNotification();
    }

    private void pushNotification() {
        int notificationId = 001;

        Intent memorizeScreen = new Intent();
        memorizeScreen.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle extras = new Bundle();
        extras.putString("item", mTitleHeader);
        extras.putBoolean("dismiss", false);
        extras.putString("objectId", objectId);
        memorizeScreen.putExtras(extras);
        memorizeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(getContext(), 0, memorizeScreen,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create an intent for the reply action
        Intent actionIntent = new Intent();
        actionIntent.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle actionExtras = new Bundle();
        actionExtras.putString("item", mTitleHeader);
        actionExtras.putBoolean("dismiss", true);
        actionExtras.putString("objectId", objectId);
        actionIntent.putExtras(actionExtras);
        actionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(getContext(), 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_circle,
                        "Got it!", actionPendingIntent)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_circle)
                        .setContentTitle("Memorease")
                        .setContentText("1 card ready for memorization")
                        .setContentIntent(viewPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().addAction(action));

        // Create a big text style for the second page
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle("Page 2")
                .bigText(mTitleHeader);

        // Create second page notification
        Notification secondPageNotification =
                new NotificationCompat.Builder(getContext())
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
                NotificationManagerCompat.from(getContext());

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, twoPageNotification);
    }

    private void init() {

        //Create a CardHeader
        CardHeader header = new CardHeader(_context);

        //Set the header title
        header.setTitle(mTitleHeader);

        addCardHeader(header);

        setSwipeable(true);

        setOnSwipeListener(new Card.OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            object.deleteInBackground();
                        } else {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
                            query.fromLocalDatastore();
                            query.getInBackground(objectId, new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        object.deleteInBackground();
                                    } else {
                                        // something went wrong
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                gotoMemorizationPage();
            }
        });

        setClickable(true);

        //Set the card inner text
        setTitle(mTitleMain);
    }

    private void gotoMemorizationPage() {
        Intent memorizeScreen = new Intent();
        memorizeScreen.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
        Bundle actionExtras = new Bundle();
        actionExtras.putString("item", mTitleHeader);
        actionExtras.putBoolean("dismiss", false);
        actionExtras.putString("objectId", objectId);
        memorizeScreen.putExtras(actionExtras);
        memorizeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(memorizeScreen);
    }

    private String timeOfNextTimer(int level, Time time){
        switch (level){
            case 1:
                nextTimer.set(time.second, time.minute + 2, time.hour, time.monthDay, time.month, time.year);
                return "2 minutes";
            case 2:
                nextTimer.set(time.second, time.minute + 10, time.hour, time.monthDay, time.month, time.year);
                return "10 minutes";
            case 3:
                nextTimer.set(time.second, time.minute, time.hour + 1, time.monthDay, time.month, time.year);
                return "1 hour";
            case 4:
                nextTimer.set(time.second, time.minute, time.hour + 5, time.monthDay, time.month, time.year);
                return "5 hours";
            case 5:
                nextTimer.set(time.second + 5, time.minute, time.hour + 24, time.monthDay, time.month, time.year);
                return "1 day";
            case 6:
                nextTimer.set(time.second + 5, time.minute, time.hour, time.monthDay + 5, time.month, time.year);
                return "5 days";
            case 7:
                nextTimer.set(time.second + 5, time.minute, time.hour, time.monthDay + 25, time.month, time.year);
                return "25 days";
            case 8:
                nextTimer.set(time.second, time.minute, time.hour, time.monthDay, time.month + 4, time.year);
                return "4 months";
        }
        return "Completed!";
    }

    public Time getNextTimer() {
        return nextTimer;
    }

    public void setNextTimer(Time nextTimer) {
        this.nextTimer = nextTimer;
    }

    public String getmTitleHeader() {
        return mTitleHeader;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}