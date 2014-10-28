package se.orw.projekt1;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

import se.orw.projekt1.Twitter.TwitterController;
import se.orw.projekt1.Twitter.TwitterFragment;
import se.orw.projekt1.Twitter.TwitterFunctions;

/**
 * Controller for app
 *
 * Created by Marcus on 2014-10-22.
 */
@SuppressWarnings("deprecation")
public class Controller {
    private FragmentActivity activity;
    private MainFragment mainFragment;
    private ConnectFragment connectFragment;
    private TwitterFragment twitterFragment;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private String[] menu = {"Home", "Connect"};

    /**
     * Constructor
     *
     * @param activity The FragmentActivity of the app
     */
    public Controller(FragmentActivity activity) {
        this.activity = activity;

        mainFragment = new MainFragment();
        //mainFragment.setController(this);

        connectFragment = new ConnectFragment();
        connectFragment.setController(this);

        twitterFragment = new TwitterFragment();
        twitterFragment.setController(this);

        switchToFragment(mainFragment, null);
        initNavigationDrawer();
    }

    /**
     * Switch to specified fragment
     *
     * @param fragment The fragment to switch to
     * @param tag      The tag for the fragment
     */
    public void switchToFragment(android.support.v4.app.Fragment fragment, String tag) {
        android.support.v4.app.FragmentManager fragmentManager = activity.getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (tag != null && tag.length() > 0) {
            transaction.replace(R.id.activity_main, fragment, tag);
        } else {
            transaction.replace(R.id.activity_main, fragment);
        }
        transaction.commit();
    }

    /**
     * Switch back to the default fragment
     */
    public void switchToDefaultFragment() {
        switchToFragment(mainFragment, null);
    }

    // Twitter Methods
    /**
     * Updates the text on the twitter button.
     *
     * @return Id of the strings text.
     */
    public int updateTwitterButtonText() {
        if(TwitterController.isConnected(activity)) {
            return R.string.logoutTwitter;
        }
        return R.string.loginWithTwitter;
    }

    /**
     * Connect to twitter
     */
    public void twitterConnect() {
        if(TwitterController.isConnected(activity)) {
            TwitterController.logOutOfTwitter(activity);
        } else {
            switchToFragment(twitterFragment, null);
        }
    }

    /**
     * Send test message to twitter
     */
    public void publishToTwitter(String message) {
        //send test tweet
        TwitterFunctions.postToTwitter(activity, activity, Secrets.CONSUMER_KEY, Secrets.CONSUMER_SECRET, message, new TwitterFunctions.TwitterPostResponse() {
            @Override
            public void OnResult(Boolean success) {
                Log.d(Constants.TWITTER_TAG, "Success: " + success);
            }
        });
    }

    //Facebook Methods
    /**
     * Publish story to facebook
     * will need publish_access permission
     *
     * @param message The message to publish
     */
    public void publishToFacebook(String message) {
        Session session = Session.getActiveSession();

        if(session != null && session.isOpened()) {
            //Check for publish permissions
            List<String> permissions = session.getPermissions();
            if(!isSubsetOf(Constants.PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(connectFragment, Constants.PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString("message", message);

            Request.Callback callback = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
                    String postId = null;
                    try {
                        postId = graphResponse.getString("id");
                    } catch (JSONException e) {
                        Log.i(Constants.FB_TAG, "JSON error " + e.getMessage());
                    }
                    FacebookRequestError error = response.getError();
                    if(error != null) {
                        Toast.makeText(activity, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, postId, Toast.LENGTH_SHORT).show();
                    }
                }
            };

            Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);

            RequestAsyncTask task = new RequestAsyncTask(request);
            task.execute();
        }
    }

    /**
     * Handle what will happen if facebook is logged in or out
     *
     * @param state The session state (logged in or out).
     */
    public void onFacebookStateChange(SessionState state) {
        if (state.isOpened()) {
            Log.i(Constants.FB_TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(Constants.FB_TAG, "Logged out...");
        }
    }

    // Protected Methods
    /**
     * Get the drawerToggle (to be used in Activity)
     *
     * @return drawerToggle
     */
    protected ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    // Private Methods
    /**
     * Compare two collections if they are the same return true
     *
     * @param subset The set you want to find in the superset
     * @param superset The set to search in
     * @return true if the superset contains the subset (eg, asd contains an a) else false.
     */
    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Initialize the navigation drawer
     */
    private void initNavigationDrawer() {
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.layout_drawer);
        ListView drawerList = (ListView) activity.findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, menu));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (menu[position].equals("Connect")) {
                    switchToFragment(connectFragment, null);
                } else if(menu[position].equals("Home")) {
                    switchToFragment(mainFragment, null);
                }

                drawerLayout.closeDrawers();
            }
        });
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.drawable.ic_drawer, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.setDrawerListener(drawerToggle);
        if(activity.getActionBar() != null) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setHomeButtonEnabled(true);
        }
    }
}
