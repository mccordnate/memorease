package com.getmemorease.memorease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class MemorizeCardActivity extends Activity {
    private String item = null;
    private String objectId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize_card);

        Intent inIntent = getIntent();

        TextView textView = (TextView) findViewById(R.id.displayItem);

        Boolean finish = inIntent.getBooleanExtra("dimiss", false);
        if (finish) {
            gotitOnClick(new View(getApplicationContext()));
        }

        item = inIntent.getStringExtra("item");
        textView.setText(item);

        objectId = inIntent.getStringExtra("objectId");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.memorize_card, menu);
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

    public void gotitOnClick(View view) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
        query.fromLocalDatastore();
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.increment("level");
                    Time newTime = new Time();
                    newTime.set(timeOfNextTimer(object.getInt("level")));
                    object.put("time", newTime.toString());
                    object.pinInBackground(null);
                    object.saveEventually();

                    Bundle extras = new Bundle();
                    extras.putString("item", item);
                    extras.putBoolean("dismiss", false);
                    extras.putString("objectId", objectId);
                    extras.putLong("time", newTime.normalize(false));

                    AlarmService as = new AlarmService(getApplicationContext(), extras);
                    as.startAlarm();
                } else {
                    // something went wrong
                }
            }
        });

        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private Time timeOfNextTimer(int level){
        Time newTime = new Time();
        newTime.setToNow();
        switch (level){
            case 1:
                newTime.set(newTime.second, newTime.minute + 2, newTime.hour, newTime.monthDay, newTime.month, newTime.year);
                return newTime;
            case 2:
                newTime.set(newTime.second, newTime.minute + 10, newTime.hour, newTime.monthDay, newTime.month, newTime.year);
                return newTime;
            case 3:
                newTime.set(newTime.second, newTime.minute, newTime.hour + 1, newTime.monthDay, newTime.month, newTime.year);
                return newTime;
            case 4:
                newTime.set(newTime.second, newTime.minute, newTime.hour + 5, newTime.monthDay, newTime.month, newTime.year);
                return newTime;
            case 5:
                newTime.set(newTime.second, newTime.minute, newTime.hour + 24, newTime.monthDay, newTime.month, newTime.year);
                return newTime;
            case 6:
                newTime.set(newTime.second, newTime.minute, newTime.hour, newTime.monthDay + 5, newTime.month, newTime.year);
                return newTime;
            case 7:
                newTime.set(newTime.second, newTime.minute, newTime.hour, newTime.monthDay + 25, newTime.month, newTime.year);
                return newTime;
            case 8:
                newTime.set(newTime.second, newTime.minute, newTime.hour, newTime.monthDay, newTime.month + 4, newTime.year);
                return newTime;
        }
        return newTime;
    }
}
