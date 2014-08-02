package com.getmemorease.memorease;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.ui.ParseLoginBuilder;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class MainPageActivity extends Activity {

    private ArrayList<Card> cards = new ArrayList<Card>();
    private ArrayList<String> objectIds = new ArrayList<String>();

    public Activity getActivity(){
        return MainPageActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Card");
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects != null) {
                    for (ParseObject parseObject : parseObjects) {
                        try {
                            parseObject.pin();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    };
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        setTitle(ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Card");
        query.whereEqualTo("user", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects != null) {
                    for (ParseObject parseObject : parseObjects) {
                        if (!objectIds.contains(parseObject.getObjectId())){
                            MemorizeCard card = new MemorizeCard(getApplicationContext(), parseObject.getString("info"), parseObject.getInt("level"), new Time(parseObject.getString("time")));
                            card.setObjectId(parseObject.getObjectId());
                            objectIds.add(card.getObjectId());
                            cards.add(card);
                        }
                    };
                    cardListCreate();
                }
            }
        });

        ParseQuery<ParseObject> localQuery = new ParseQuery<ParseObject>("Card");
        localQuery.fromLocalDatastore();

        localQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects != null) {
                    for (ParseObject parseObject : parseObjects) {
                        if (!objectIds.contains(parseObject.getObjectId())){
                            MemorizeCard card = new MemorizeCard(getApplicationContext(), parseObject.getString("info"), parseObject.getInt("level"), new Time(parseObject.getString("time")));
                            card.setObjectId(parseObject.getObjectId());
                            objectIds.add(card.getObjectId());
                            cards.add(card);
                        }
                    }
                    cardListCreate();
                }
            }
        });
    }

    public void cardListCreate(){
        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);
        mCardArrayAdapter.setEnableUndo(true);
        mCardArrayAdapter.setInnerViewTypeCount(1);

        CardListView listView = (CardListView) getActivity().findViewById(R.id.myList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        if (ParseUser.getCurrentUser() != null) {
            if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser()))
                menu.findItem(R.id.action_login).setTitle("Login");
            else
                menu.findItem(R.id.action_login).setTitle("Logout");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if(id == R.id.action_login){
            if (ParseUser.getCurrentUser() != null) {
                if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
                    ParseLoginBuilder builder = new ParseLoginBuilder(getActivity());
                    startActivityForResult(builder.build(), 0);
                    item.setTitle("Logout");
                }
                else {
                    ParseUser.logOut();
                    item.setTitle("Login");
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                if (!cards.isEmpty()){
                    for (Card card : cards){
                        final MemorizeCard memorizeCard = (MemorizeCard) card;
                        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
                        try {
                            ParseObject object = query.get(memorizeCard.getObjectId());
                            object.put("user", ParseUser.getCurrentUser());
                            object.saveEventually();
                        } catch (ParseException e) {
                            ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Card");
                            localQuery.fromLocalDatastore();
                            try {
                                ParseObject localObject = localQuery.get(memorizeCard.getObjectId());
                                localObject.put("user", ParseUser.getCurrentUser());
                                localObject.saveEventually();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }*/
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
                        query.getInBackground(memorizeCard.getObjectId(), new GetCallback<ParseObject>() {
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    parseObject.put("user", ParseUser.getCurrentUser());
                                    parseObject.saveEventually();
                                } else {
                                    ParseQuery<ParseObject> localQuery = ParseQuery.getQuery("Card");
                                    localQuery.fromLocalDatastore();
                                    localQuery.getInBackground(memorizeCard.getObjectId(), new GetCallback<ParseObject>() {
                                        public void done(ParseObject localParseObject, ParseException e) {
                                            if (e == null) {
                                                localParseObject.put("user", ParseUser.getCurrentUser());
                                                localParseObject.saveEventually();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
                setTitle(ParseUser.getCurrentUser().toString());
                cardListCreate();
            }
        }
    }

    public void addOnClick(View view) {
        final EditText input = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.popupAddItem);
        builder.setView(input);
        builder.setPositiveButton(R.string.popupOkayButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().isEmpty()) {
                    Time now = new Time();
                    now.setToNow();

                    final MemorizeCard card = new MemorizeCard(getActivity(), input.getText().toString(), 1, now);
                    cards.add(card);

                    final ParseObject parseCard = new ParseObject("Card");
                    parseCard.put("user", ParseUser.getCurrentUser());
                    parseCard.put("info", card.getmTitleHeader());
                    parseCard.put("level", 1);
                    parseCard.put("time", card.getNextTimer().toString());
                    //if (cards.size() == 1 && ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
                        /*try {
                            parseCard.save();
                            card.setObjectId(parseCard.getObjectId());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                    //}else {
                    parseCard.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                card.setObjectId(parseCard.getObjectId());
                                objectIds.add(card.getObjectId());
                            }
                        }
                    });
                    cardListCreate();
                }
                 else {
                    Toast.makeText(getApplicationContext(), "Please enter an item", Toast.LENGTH_SHORT).show();
                }
            }
        }).setNegativeButton(R.string.popupCancelButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        });
        AlertDialog dialog = builder.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
        dialog.show();
        dialog.getWindow().setLayout(450, 350);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }
}