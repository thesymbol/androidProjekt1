package se.orw.projekt1.Twitter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import se.orw.projekt1.Constants;
import se.orw.projekt1.Controller;
import se.orw.projekt1.R;
import se.orw.projekt1.Secrets;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Twitter Controller
 *
 * Created by Marcus on 2014-10-23.
 */
@SuppressLint("CommitPrefEdits")
public class TwitterController {
    private static Twitter twitter;
    private static RequestToken requestToken;
    private WebView twitterLoginWebView;
    private ProgressDialog mProgressDialog;
    private String twitterConsumerKey;
    private String twitterConsumerSecret;
    private android.support.v4.app.Fragment fragment;
    private Controller controller;

    /**
     * Constructor to handle the Twitter Login
     * @author Marcus
     *
     * @param fragmentView -
     * @param fragment     -
     * @param controller   -
     */
    public TwitterController(final View fragmentView, android.support.v4.app.Fragment fragment, Controller controller) {
        this.fragment = fragment;
        this.controller = controller;

        twitterConsumerKey = Secrets.TWITTER_CONSUMER_KEY;
        twitterConsumerSecret = Secrets.TWITTER_CONSUMER_SECRET;
        if (twitterConsumerKey.length() <= 0 || twitterConsumerSecret.length() <= 0) {
            Log.e(Constants.TWITTER_TAG, "Consumer Key and Consumer Secret required");
            controller.switchToConnectFragment();
        }

        mProgressDialog = new ProgressDialog(fragment.getActivity());
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        twitterLoginWebView = (WebView) fragmentView.findViewById(R.id.twitter_login_web_view);
        twitterLoginWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains(Constants.TWITTER_CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    saveAccessTokenAndFinish(uri);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                if (mProgressDialog != null) {
                    mProgressDialog.show();
                }
            }
        });


        Log.d(Constants.TWITTER_TAG, "Asking for OAuth");
        askOAuth();
    }

    /**
     * Destroy the dialog once we are done
     * @author Marcus
     */
    public void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    //Helper methods
    /**
     * Check if we are connected to twitter
     * @author Marcus
     *
     * @param context The context
     * @return true if we are connected to twitter else false
     */
    public static boolean isConnected(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_TOKEN, null) != null;
    }

    /**
     * Logout of twitter
     * @author Marcus
     *
     * @param context The context
     */
    public static void logOutOfTwitter(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.PREF_KEY_TOKEN, null);
        editor.putString(Constants.PREF_KEY_SECRET, null);
        editor.commit();
    }

    /**
     * Get the Access Token
     * @author Marcus
     *
     * @param context The context
     * @return The access token
     */
    public static String getAccessToken(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_TOKEN, null);
    }

    /**
     * Get the Secret Access Token
     * @author Marcus
     *
     * @param context The context
     * @return The access token secret
     */
    public static String getAccessTokenSecret(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_SECRET, null);
    }

    /**
     * Save the token received from the user
     * @author Marcus
     *
     * @param uri The uri to use
     */
    private void saveAccessTokenAndFinish(final Uri uri) {
        new Thread(new Runnable() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void run() {
                String verifier = uri.getQueryParameter(Constants.IEXTRA_OAUTH_VERIFIER);
                try {
                    SharedPreferences sharedPrefs = fragment.getActivity().getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
                    SharedPreferences.Editor e = sharedPrefs.edit();
                    e.putString(Constants.PREF_KEY_TOKEN, accessToken.getToken());
                    e.putString(Constants.PREF_KEY_SECRET, accessToken.getTokenSecret());
                    e.commit();
                    Log.d(Constants.TWITTER_TAG, "Login Successful");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage() != null) Log.e(Constants.TWITTER_TAG, e.getMessage());
                    else
                        Log.e(Constants.TWITTER_TAG, "Callback Failed");
                }
                controller.switchToConnectFragment();
            }
        }).start();
    }

    /**
     * Getting the OAuth key's etc
     * @author Marcus
     */
    private void askOAuth() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(twitterConsumerKey);
        configurationBuilder.setOAuthConsumerSecret(twitterConsumerSecret);
        Configuration configuration = configurationBuilder.build();
        twitter = new TwitterFactory(configuration).getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
                } catch (Exception e) {
                    final String errorString = e.toString();
                    fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.cancel();
                            Toast.makeText(fragment.getActivity(), errorString, Toast.LENGTH_SHORT).show();
                            controller.switchToConnectFragment();
                        }
                    });
                    e.printStackTrace();
                    return;
                }

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Constants.TWITTER_TAG, "Loading Auth URL");
                        twitterLoginWebView.loadUrl(requestToken.getAuthenticationURL());
                    }
                });
            }
        }).start();
    }
}
