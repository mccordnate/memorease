package com.getmemorease.memorease;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.Time;
import android.view.View;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
        // Build intent for notification content
        Intent viewIntent = new Intent(getContext(), MainPageActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(getContext(), 0, viewIntent, 0);

        // Create an intent for the reply action
        Intent actionIntent = new Intent(getContext(), MainPageActivity.class);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(getContext(), 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_circle,
                        "Add", actionPendingIntent)
                        .build();

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.drawable.ic_circle)
                        .setLargeIcon(BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_cab_done_holo_light))
                        .setContentTitle("Memorease")
                        .setContentText("1 card ready for memorization")
                        .setContentIntent(viewPendingIntent);

        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.setBigContentTitle("Memorize")
                .bigText(mTitleHeader);

        Notification secondPage =
                new NotificationCompat.Builder(getContext())
                        .setStyle(bigStyle)
                        .addAction(action)
                        .build();

        Notification twoPage =
                new NotificationCompat.WearableExtender()
                        .addPage(secondPage)
                        .extend(notificationBuilder)
                        .build();

        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getContext());

        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, twoPage);
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
                            // something went wrong
                        }
                    }
                });
            }
        });

        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent memorizeScreen = new Intent();
                memorizeScreen.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
                memorizeScreen.putExtra("item", mTitleHeader);
                memorizeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(memorizeScreen);
            }
        });

        setClickable(true);

        //Set the card inner text
        setTitle(mTitleMain);
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