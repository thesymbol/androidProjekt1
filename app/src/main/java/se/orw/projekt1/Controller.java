package se.orw.projekt1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
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
    private ListView drawerList;

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
     * Connect to twitter
     */
    public void twitterConnect() {
        if(TwitterController.isConnected(activity)) {
            TwitterController.logOutOfTwitter(activity);
        } else {
            switchToFragment(twitterFragment, null);
        }
    }

    public void twitterDisconnect() {
        Log.d("se.orw.projekt1.Controller", "before logOutOfTwitter isConnected: " + TwitterController.isConnected(activity));
        //send test tweet
        TwitterFunctions.postToTwitter(activity, activity, Secrets.CONSUMER_KEY, Secrets.CONSUMER_SECRET, "Test tweet", new TwitterFunctions.TwitterPostResponse() {
            @Override
            public void OnResult(Boolean success) {
                Log.d(Constants.TWITTER_TAG, "Success: " + success);
            }
        });
    }

    public void switchToDefaultFragment() {
        switchToFragment(mainFragment, null);
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
     * Used to sync the state of the button
     */
    public void onPostCreate() {
        drawerToggle.syncState();
    }

    /**
     * Once the app changes configuration (eg rotation).
     *
     * @param newConfig The config
     */
    public void onConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Handles the drawer on the left sides button
     *
     * @param item Item in the menu
     * @return true if item is selected else false
     */
    public boolean onDrawerToggle(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item);
    }

    /**
     * Initialize the navigation drawer
     */
    private void initNavigationDrawer() {
        final String[] menu = {"Home", "Connect"};
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.layout_drawer);
        drawerList = (ListView) activity.findViewById(R.id.left_drawer);

        drawerList.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, menu));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(menu[position].equals("Connect")) {
                    switchToFragment(connectFragment, null);
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

    public int updateTwitterButtonText() {
        if(TwitterController.isConnected(activity)) {
            return R.string.logoutTwitter;
        }
        return R.string.loginWithTwitter;
    }

    public void onFacebookStateChange(SessionState state) {
        if (state.isOpened()) {
            Log.i(Constants.FB_TAG, "Logged in...");
        } else if (state.isClosed()) {
            Log.i(Constants.FB_TAG, "Logged out...");
        }
    }


    public void publishTestStory(String message) {

        Session session = Session.getActiveSession();

        if(session != null) {
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

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
}
