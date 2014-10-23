package se.orw.projekt1.Twitter;

import android.app.Fragment;
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
    private static final int TWITTER_LOGIN_RESULT_CODE_SUCCESS = 1;
    private static final int TWITTER_LOGIN_RESULT_CODE_FAILURE = 2;

    private WebView twitterLoginWebView;
    private ProgressDialog mProgressDialog;
    private String twitterConsumerKey;
    private String twitterConsumerSecret;

    private static Twitter twitter;
    private static RequestToken requestToken;
    private View fragmentView;
    private Fragment fragment;
    private Controller controller;

    public TwitterController(final View fragmentView, Fragment fragment, Controller controller) {
        this.fragmentView = fragmentView;
        this.fragment = fragment;
        this.controller = controller;

        twitterConsumerKey = controller.getConsumerKey();
        twitterConsumerSecret = controller.getConsumerSecret();
        if (twitterConsumerKey == null || twitterConsumerSecret == null) {
            Log.e(Constants.TAG + ".TwitterActivity.TwitterController", "ERROR: Consumer Key and Consumer Secret required!");
            controller.switchToDefaultFragment();
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


        Log.d(Constants.TAG + ".TwitterActivity.TwitterController", "ASK OAUTH");
        askOAuth();
    }

    public void onDestroy() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    //Helper methods
    public static boolean isConnected(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_TOKEN, null) != null;
    }

    public static void logOutOfTwitter(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Constants.PREF_KEY_TOKEN, null);
        editor.putString(Constants.PREF_KEY_SECRET, null);
        editor.commit();
    }

    public static String getAccessToken(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_TOKEN, null);
    }

    public static String getAccessTokenSecret(Context ctx) {
        SharedPreferences sharedPrefs = ctx.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(Constants.PREF_KEY_SECRET, null);
    }

    private void saveAccessTokenAndFinish(final Uri uri) {
        new Thread(new Runnable() {
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
                    Log.d(Constants.TAG + ".TwitterActivity.TwitterController", "TWITTER LOGIN SUCCESS!!!");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage() != null) Log.e(Constants.TAG, e.getMessage());
                    else Log.e(Constants.TAG + ".TwitterActivity.TwitterController", "ERROR: Twitter callback failed");
                }
                controller.switchToDefaultFragment();
            }
        }).start();
    }

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
                            Toast.makeText(fragment.getActivity(), errorString.toString(), Toast.LENGTH_SHORT).show();
                            controller.switchToDefaultFragment();
                        }
                    });
                    e.printStackTrace();
                    return;
                }

                fragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(Constants.TAG + ".TwitterActivity.TwitterController", "LOADING AUTH URL");
                        twitterLoginWebView.loadUrl(requestToken.getAuthenticationURL());
                    }
                });
            }
        }).start();
    }
}