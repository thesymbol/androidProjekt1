package se.orw.projekt1;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marcus on 2014-10-23.
 */
public class Constants {
    public static final String TAG = "General";
    public static final String FB_TAG = "Facebook";
    public static final String TWITTER_TAG = "Twitter";

    // Twitter Constants
    public static final String PREFERENCE_NAME = "twitter_oauth";
    public static final String PREF_KEY_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TOKEN = "oauth_token";
    public static final String TWITTER_CALLBACK_URL = "x-oauthflow-twitter://twitterlogin";
    public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";

    // Facebook Constants
    public static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
}
