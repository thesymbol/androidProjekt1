package se.orw.projekt1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Marcus on 2014-10-23.
 */
public class TwitterController {
    // server configuration
    private Twitter twitter;
    private RequestToken requestToken;
    private SharedPreferences sharedPreferences;
    private Activity activity;
    private Controller controller;

    // Access token

    public TwitterController (Controller controller) {
        this.controller = controller;
        activity = controller.getActivity();
        sharedPreferences = activity.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void onResume() {
        if(isConnected()) {
            String oAuthAccessToken = sharedPreferences.getString(Constants.PREF_KEY_TOKEN, "");
            String oAuthAccessTokenSecret = sharedPreferences.getString(Constants.PREF_KEY_SECRET, "");

            ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
            Configuration configuration = configurationBuilder.setOAuthConsumerKey(Constants.CONSUMER_KEY).setOAuthConsumerSecret(Constants.CONSUMER_SECRET).setOAuthAccessToken(oAuthAccessToken).setOAuthAccessTokenSecret(oAuthAccessTokenSecret).build();
            //@TODO: Make use of configuration onResume
        }
    }

    /**
     * Check if we are connected with an authorized account
     * @return true if connected else false
     */
    public boolean isConnected() {
        return sharedPreferences.getString(Constants.PREF_KEY_TOKEN, null) != null;
    }


    public void askOAuth() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
        configurationBuilder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
                } catch (Exception e) {
                    final String errorString = e.toString();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity, errorString, Toast.LENGTH_SHORT).show();
                            activity.finish();
                        }
                    });
                    e.printStackTrace();
                    return;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Projekt1.TwitterController", "LOADING AUTH URL");
                        controller.loadUrl(requestToken.getAuthenticationURL());
                    }
                });
            }
        }).start();
    }

    public void saveAccessTokenAndFinish(final Uri uri) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String verifier = uri.getQueryParameter(Constants.IEXTRA_OAUTH_VERIFIER);
                try {
                    SharedPreferences sharedPrefs = activity.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    SharedPreferences.Editor e = sharedPrefs.edit();
                    e.putString(Constants.PREF_KEY_TOKEN, accessToken.getToken());
                    e.putString(Constants.PREF_KEY_SECRET, accessToken.getTokenSecret());
                    e.commit();
                    Log.d("Projekt1.Controller", "TWITTER LOGIN SUCCESS!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                    if(e.getMessage() != null) Log.e("Projekt1.Controller", e.getMessage());
                    else Log.e("Projekt1.Controller", "ERROR: Twitter callback failed");
                }
                activity.finish();
            }
        }).start();
    }

    public void disconnectTwitter() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(Constants.PREF_KEY_TOKEN);
        editor.remove(Constants.PREF_KEY_SECRET);
        editor.apply();
    }

}
