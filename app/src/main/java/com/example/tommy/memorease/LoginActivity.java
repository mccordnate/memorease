package com.example.tommy.memorease;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tommy.memorease.R;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "jBdBeXxeExKcNqhA8Z1cO3b1EUpUP28bHNIbAdzm", "af9JUMCI74K8ouSnFzWTCAb7jXNF9DdkGXxOLlDO");
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Go to the user info activity
            startActivity(new Intent(this, MyActivity.class));
            finish();
        }
        else{
            setContentView(R.layout.activity_login);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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

    public void login(View view){
        EditText emailEditText = (EditText) findViewById(R.id.email);
        EditText passEditText = (EditText) findViewById(R.id.password);

        ParseUser.logInInBackground(emailEditText.getText().toString(), passEditText.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, MyActivity.class));
                    Toast.makeText(getApplicationContext(),
                            "Login Successful!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(
                            getApplicationContext(),
                            "Password/Username incorrect. Try again or Sign up",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void signUp(View view){
        ParseUser user = new ParseUser();

        EditText emailEditText = (EditText) findViewById(R.id.email);
        EditText passEditText = (EditText) findViewById(R.id.password);

        user.setUsername(emailEditText.getText().toString());
        user.setPassword(passEditText.getText().toString());
        user.setEmail(emailEditText.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    startActivity(new Intent(LoginActivity.this, MyActivity.class));
                    Toast.makeText(getApplicationContext(), "Sign up successful!", Toast.LENGTH_LONG);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(getApplicationContext(), "Sign up didn't work :(", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
