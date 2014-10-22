package se.orw.projekt1;

import android.app.Activity;
import android.util.Log;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Marcus on 2014-10-22.
 */
public class Controller {
    private Activity activity;

    public Controller(Activity activity) {
        this.activity = activity;
        new Thread() {
            public void run() {
                try {
                    String pin= "";
                    Twitter twitter = TwitterFactory.getSingleton();
                    twitter.setOAuthConsumer("CONSUMER_KEY", "CONSUMER_SECRET");
                    RequestToken requestToken = twitter.getOAuthRequestToken();
                    AccessToken accessToken = null;
                    if(pin.length() > 0) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken();
                    }
                    twitter.verifyCredentials().getId();
                    //storeAccessToken(twitter.verifyCredentials().getId(), accessToken);
                    Status status = twitter.updateStatus(new StatusUpdate("Test"));
                    Log.d("TwitterDebug", "Successfully updated the status to [" + status.getText() + "].");
                } catch(Exception e) {
                    Log.d("TwitterDebug", "twitter could not get OAuth");
                    e.printStackTrace();
                }
            }
        };
    }
}
