package com.getmemorease.memorease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.tommy.memorease.R;

import java.util.ArrayList;

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
        }
        return super.onOptionsItemSelected(item);
    }

    public void addOnClick(View view) {
        LayoutInflater layoutInflater
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
                popupWindow.dismiss();
                layout_MainMenu.getForeground().setAlpha(0);
                Time now = new Time();
                now.setToNow();

                MemorizeCard card = new MemorizeCard(getActivity(), editText.getText().toString(), 1, now);
                cards.add(card);
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
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }
}