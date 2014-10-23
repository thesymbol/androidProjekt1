package se.orw.projekt1;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;

/**
 * Created by Marcus on 2014-10-22.
 */
public class Controller {
    private Activity activity;
    private TwitterController twitterController;
    private TestFragment testFragment;

    public Controller(Activity activity) {
        this.activity = activity;

        testFragment = new TestFragment();
        testFragment.setController(this);

        twitterController = new TwitterController(this);

        switchToFragment(testFragment, "");
    }

    public void twitterConnect() {
        if(twitterController.isConnected()) {
            twitterController.disconnectTwitter();
        } else {
            twitterController.askOAuth();
        }
    }

    public void twitterDisconnect() {
        twitterController.disconnectTwitter();
    }

    public Activity getActivity() {
        return activity;
    }

    public void onResume() {
        twitterController.onResume();
    }

    public void saveAccessTokenAndFinish(Uri uri) {
        twitterController.saveAccessTokenAndFinish(uri);
    }

    public void loadUrl(String authenticationURL) {
        testFragment.loadUrl(authenticationURL);
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
