package com.getmemorease.memorease;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.view.View;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Tommy on 7/22/2014.
 */
    public class MemorizeCard extends Card {

    protected String mTitleHeader;
    protected String mTitleMain;
    private Time nextTime;
    private Context _context;

    public MemorizeCard(Context context, String titleHeader, int currentLevel, Time startTime) {
        //add to db
        super(context);
        this._context = context;
        this.mTitleHeader = titleHeader;
        nextTime = startTime;
        this.mTitleMain = "  Time until next memorization: " + timeOfNextTimer(currentLevel, startTime);
        init();
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
                //remove from db
            }
        });

        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent memorizeScreen = new Intent();
                memorizeScreen.setClassName("com.getmemorease.memorease", "com.getmemorease.memorease.MemorizeCardActivity");
                memorizeScreen.putExtra("item", mTitleHeader);
                getContext().startActivity(memorizeScreen);
            }
        });

        //Set the card inner text
        setTitle(mTitleMain);
    }

    private String timeOfNextTimer(int level, Time time){
        switch (level){
            case 1:
                nextTime.set(time.second, time.minute+2, time.hour, time.monthDay, time.month, time.year);
                return "2 minutes";
            case 2:
                nextTime.set(time.second, time.minute+10, time.hour, time.monthDay, time.month, time.year);
                return "10 minutes";
            case 3:
                nextTime.set(time.second, time.minute, time.hour+1, time.monthDay, time.month, time.year);
                return "1 hour";
            case 4:
                nextTime.set(time.second, time.minute, time.hour+5, time.monthDay, time.month, time.year);
                return "5 hours";
            case 5:
                nextTime.set(time.second+5, time.minute, time.hour+24, time.monthDay, time.month, time.year);
                return "1 day";
            case 6:
                nextTime.set(time.second+5, time.minute, time.hour, time.monthDay+5, time.month, time.year);
                return "5 days";
            case 7:
                nextTime.set(time.second+5, time.minute, time.hour, time.monthDay+25, time.month, time.year);
                return "25 days";
            case 8:
                nextTime.set(time.second, time.minute, time.hour, time.monthDay, time.month+4, time.year);
                return "4 months";
        }
        return "Completed!";
    }
}

