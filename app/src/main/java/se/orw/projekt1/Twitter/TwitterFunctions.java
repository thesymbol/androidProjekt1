package se.orw.projekt1.Twitter;

import android.app.Activity;
import android.content.Context;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Marcus on 2014-10-23.
 */
public class TwitterFunctions {

    public TwitterFunctions() {
    }

    public static void postToTwitter(Context c, final Activity callingActivity, final String consumerKey, final String consumerSecret, final String message, final TwitterPostResponse postResponse) {
        if (!TwitterController.isConnected(c)) {
            postResponse.OnResult(false);
            return;
        }

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(consumerKey);
        configurationBuilder.setOAuthConsumerSecret(consumerSecret);
        configurationBuilder.setOAuthAccessToken(TwitterController.getAccessToken((c)));
        configurationBuilder.setOAuthAccessTokenSecret(TwitterController.getAccessTokenSecret(c));
        Configuration configuration = configurationBuilder.build();
        final Twitter twitter = new TwitterFactory(configuration).getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                try {
                    twitter.updateStatus(message);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    success = false;
                }

                final boolean finalSuccess = success;

                callingActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        postResponse.OnResult(finalSuccess);
                    }
                });

            }
        }).start();
    }

    public static abstract class TwitterPostResponse {
        public abstract void OnResult(Boolean success);
    }
}