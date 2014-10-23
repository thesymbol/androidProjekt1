package se.orw.projekt1;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.util.Log;

import se.orw.projekt1.TwitterActivity.TwitterActivity;
import se.orw.projekt1.TwitterActivity.TwitterController;
import se.orw.projekt1.TwitterActivity.TwitterFunctions;

/**
 * Created by Marcus on 2014-10-22.
 */
public class Controller {
    private Activity activity;
    private TestFragment testFragment;

    public Controller(Activity activity) {
        this.activity = activity;

        testFragment = new TestFragment();
        testFragment.setController(this);

        switchToFragment(testFragment, "");
    }

    public void twitterConnect() {
        Intent intent = new Intent(activity, TwitterActivity.class);
        intent.putExtra("twitter_consumer_key", "DYkm3hP3Yfi9XY4QMjKeLjI6f");
        intent.putExtra("twitter_consumer_secret", "uDefXqUeClhOazvnr48owY3wOyBEnc0dwUbhoCykjol0yxq2CN");
        activity.startActivity(intent);
    }

    public void twitterDisconnect() {
        Log.d("se.orw.projekt1.Controller", "before logOutOfTwitter isConnected: " + TwitterController.isConnected(activity));
        //send test tweet
        TwitterFunctions.postToTwitter(activity, activity, "DYkm3hP3Yfi9XY4QMjKeLjI6f", "uDefXqUeClhOazvnr48owY3wOyBEnc0dwUbhoCykjol0yxq2CN", "Test tweet", new TwitterFunctions.TwitterPostResponse() {
            @Override
            public void OnResult(Boolean success) {
                Log.d("se.orw.projekt1.Controller.TwitterPostResponse", "Success: " + success);
            }
        });
    }

    /**
     * Switch to specified fragment
     *
     * @param fragment The fragment to switch to
     * @param tag The tag for the fragment
     */
    public void switchToFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(tag.equals("")) {
            transaction.replace(R.id.activity_main, fragment);
        } else {
            transaction.replace(R.id.activity_main, fragment, tag);
        }
        transaction.commit();
    }
}
