package com.getmemorease.memorease;

import android.text.format.Time;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Tommy on 8/3/2014.
 */
public class SingletonCardList {
    private static SingletonCardList objSingletonCardList = null;
    private ArrayList<Card> cards;

    private SingletonCardList() {
        this.cards = new ArrayList<Card>();
    }

    public static SingletonCardList getInstance() {
        if (objSingletonCardList == null) {
            objSingletonCardList = new SingletonCardList();
        }
        return objSingletonCardList;
    }

    public ArrayList<Card> getCardList() {
        return this.cards;
    }

    public void add(MemorizeCard card) {
        this.cards.add(card);
    }

    public boolean contains(MemorizeCard card) {
        return this.cards.contains(card);
    }

    public boolean remove(Card card) {
        if (this.cards.contains(card)) {
            this.cards.remove(card);
            return true;
        }else
            return false;
    }

    public void clear() {
        this.cards.clear();
    }

    public void updateTimes() {
        for (Card card : this.cards) {
            MemorizeCard memorizeCard = (MemorizeCard) card;
            Time currentTime = new Time();
            currentTime.setToNow();
            memorizeCard.updateTime(currentTime);
        }
    }
}

