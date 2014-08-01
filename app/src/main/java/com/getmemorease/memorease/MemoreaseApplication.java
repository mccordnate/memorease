package com.getmemorease.memorease;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

/**
 * Created by Tommy on 7/31/2014.
 */
public class MemoreaseApplication extends Application {
    public void onCreate() {
        super.onCreate();
        // Required - Initialize the Parse SDK
        Parse.initialize(this, "jBdBeXxeExKcNqhA8Z1cO3b1EUpUP28bHNIbAdzm", "af9JUMCI74K8ouSnFzWTCAb7jXNF9DdkGXxOLlDO");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Optional - If you don't want to allow Facebook login, you can
        // remove this line (and other related ParseFacebookUtils calls)
        //ParseFacebookUtils.initialize(getString(R.string.facebook_app_id));

        // Optional - If you don't want to allow Twitter login, you can
        // remove this line (and other related ParseTwitterUtils calls)
        //ParseTwitterUtils.initialize(getString(R.string.twitter_consumer_key),
                //getString(R.string.twitter_consumer_secret));
    }
}
