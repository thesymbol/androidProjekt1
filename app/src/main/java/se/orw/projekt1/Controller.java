package se.orw.projekt1;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import se.orw.projekt1.Twitter.TwitterController;
import se.orw.projekt1.Twitter.TwitterFragment;
import se.orw.projekt1.Twitter.TwitterFunctions;

/**
 * Created by Marcus on 2014-10-22.
 */
public class Controller {
    private Activity activity;
    private TestFragment testFragment;
    private TwitterFragment twitterFragment;

    public Controller(Activity activity) {
        this.activity = activity;

        testFragment = new TestFragment();
        testFragment.setController(this);

        twitterFragment = new TwitterFragment();
        twitterFragment.setController(this);

        switchToFragment(testFragment, null);
    }

    public void twitterConnect() {
        switchToFragment(twitterFragment, null);
    }

    public void twitterDisconnect() {
        Log.d("se.orw.projekt1.Controller", "before logOutOfTwitter isConnected: " + TwitterController.isConnected(activity));
        //send test tweet
        TwitterFunctions.postToTwitter(activity, activity, Secrets.CONSUMER_KEY, Secrets.CONSUMER_SECRET, "Test tweet", new TwitterFunctions.TwitterPostResponse() {
            @Override
            public void OnResult(Boolean success) {
                Log.d(Constants.TAG + ".Controller.TwitterPostResponse", "Success: " + success);
            }
        });
    }

    public void switchToDefaultFragment() {
        switchToFragment(testFragment, null);
    }

    /**
     * Switch to specified fragment
     *
     * @param fragment The fragment to switch to
     * @param tag      The tag for the fragment
     */
    public void switchToFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (tag != null && tag.length() > 0) {
            transaction.replace(R.id.activity_main, fragment, tag);
        } else {
            transaction.replace(R.id.activity_main, fragment);
        }
        transaction.commit();
    }
}
