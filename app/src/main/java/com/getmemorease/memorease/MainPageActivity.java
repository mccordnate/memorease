package com.getmemorease.memorease;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


public class MainPageActivity extends Activity {

    private ArrayList<Card> cards = new ArrayList<Card>();
    private FrameLayout layout_MainMenu;

    public Activity getActivity(){
        return MainPageActivity.this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        layout_MainMenu = (FrameLayout) findViewById(R.id.mainmenu);
        layout_MainMenu.getForeground().setAlpha(0);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Card");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for(ParseObject parseObject : parseObjects){
                    MemorizeCard card = new MemorizeCard(getApplicationContext(), parseObject.getString("info"), parseObject.getInt("level"), new Time(parseObject.getString("time")));
                    card.setObjectId(parseObject.getObjectId());
                    cards.add(card);
                }
                cardListCreate();
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
        } else if(id == R.id.action_logout){
            ParseUser.getCurrentUser().logOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("localDatastoreEnabled", true);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addOnClick(View view) {
        /*LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.activity_popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        final EditText editText = (EditText)popupView.findViewById(R.id.editText);

        final Button okayButton = (Button)popupView.findViewById(R.id.okayButton);
        okayButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().isEmpty()) {
                    popupWindow.dismiss();
                    layout_MainMenu.getForeground().setAlpha(0);
                    Time now = new Time();
                    now.setToNow();

                    MemorizeCard card = new MemorizeCard(getActivity(), editText.getText().toString(), 1, now);
                    cards.add(card);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("Must enter an item")
                            .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).show();
                }
            }
        });

        final Button cancelButton = (Button)popupView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.update();
        layout_MainMenu.getForeground().setAlpha(150);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);*/
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
                    parseCard.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                card.setObjectId(parseCard.getObjectId());
                            } else {
                                // something went wrong
                            }
                        }
                    });


                    cardListCreate();

                } else {
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