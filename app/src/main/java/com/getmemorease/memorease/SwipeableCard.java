package com.getmemorease.memorease;

import android.app.Activity;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.base.BaseCard;

/**
 * Created by Tommy on 7/22/2014.
 */
    public class SwipeableCard extends Card {

        protected String mTitleHeader;
        protected String mTitleMain;
        private Context _context;

        public SwipeableCard(Context context, String titleHeader, String titleMain) {
            super(context);
            this._context = context;
            this.mTitleHeader=titleHeader;
            this.mTitleMain=titleMain;
            init();
        }

        private void init(){

            //Create a CardHeader
            CardHeader header = new CardHeader(_context);

            //Set the header title
            header.setTitle(mTitleHeader);

            addCardHeader(header);

            setSwipeable(true);

            //Set the card inner text
            setTitle(mTitleMain);
        }

    }

