package se.orw.projekt1;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;

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
    private String[] menu;
    //Googlevariabler
    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient googleApiClient;
    private boolean googleIntentInProgress;
    private boolean googleSignInClicked;
    private ConnectionResult googleConnectionResult;

    /**
     * Constructor
     * @author Marcus
     *
     * @param activity The FragmentActivity of the app
     */
    public Controller(FragmentActivity activity) {
        this.activity = activity;
        menu = new String[]{activity.getResources().getString(R.string.menuHome), activity.getResources().getString(R.string.menuConnect)};

        mainFragment = new MainFragment();
        mainFragment.setController(this);

        connectFragment = new ConnectFragment();
        connectFragment.setController(this);

        twitterFragment = new TwitterFragment();
        twitterFragment.setController(this);

        switchToFragment(mainFragment, null);
        initNavigationDrawer();
    }

    /**
     * Switch to specified fragment
     * @author Marcus
     *
     * @param fragment The fragment to switch to
     * @param tag      The tag for the fragment
     */
    public void switchToFragment(android.support.v4.app.Fragment fragment, String tag) {
        android.support.v4.app.FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (tag != null && tag.length() > 0) {
            transaction.replace(R.id.activity_main, fragment, tag);
        } else {
            transaction.replace(R.id.activity_main, fragment);
        }
        transaction.commit();
    }

    /**
     * Switch back to the default fragment
     * @author Marcus
     */
    public void switchToConnectFragment() {
        switchToFragment(connectFragment, null);
    }

    // Twitter Methods

    /**
     * Updates the text on the twitter button.
     * @author Marcus
     *
     * @return Id of the strings text.
     */
    public int updateTwitterButtonText() {
        if (TwitterController.isConnected(activity)) {
            return R.string.logout;
        }
        return R.string.loginWithTwitter;
    }

    /**
     * Connect to twitter
     * @author Marcus
     */
    public void twitterConnect() {
        if (TwitterController.isConnected(activity)) {
            TwitterController.logOutOfTwitter(activity);
        } else {
            switchToFragment(twitterFragment, null);
        }
    }

    /**
     * A simple method which collects all the checkboxes then calling the correct method to publish.
     *
     * @author Mattias
     * @param message , The message to publish
     * @param cbFacebook , Facebook checkbox
     * @param cbTwitter , Twitter checkbox
     * @param cbGoogle , Google+ checkbox
     */
    public void publishToSelected(String message, boolean cbFacebook, boolean cbTwitter, boolean cbGoogle){
        if(cbFacebook){
            publishToFacebook(message);
        }
        if(cbTwitter){
            publishToTwitter(message);
        }
        if(cbGoogle){
            publishToGPlus(message);
        }
    }

    /**
     * Send test message to twitter
     * @author Marcus
     */
    public void publishToTwitter(String message) {
        //send test tweet
        TwitterFunctions.postToTwitter(activity, activity, Secrets.TWITTER_CONSUMER_KEY, Secrets.TWITTER_CONSUMER_SECRET, message, new TwitterFunctions.TwitterPostResponse() {
            @Override
            public void onResult(Boolean success) {
                Toast.makeText(activity, R.string.publishedToTwitter, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Facebook Methods

    /**
     * Publish story to facebook
     * will need publish_access permission
     * @author Marcus
     *
     * @param message The message to publish
     */
    public void publishToFacebook(String message) {
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            //Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(Constants.PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(connectFragment, Constants.PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
                return;
            }

            Bundle postParams = new Bundle();
            postParams.putString("message", message);

            Request.Callback callback = new Request.Callback() {
                @Override
                public void onCompleted(Response response) {
                    FacebookRequestError error = response.getError();
                    if (error != null) {
                        Toast.makeText(activity, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, R.string.publishedToFacebook, Toast.LENGTH_LONG).show();
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
     * @author Marcus
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
     * @author Marcus
     *
     * @return drawerToggle
     */
    protected ActionBarDrawerToggle getDrawerToggle() {
        return drawerToggle;
    }

    // Private Methods

    /**
     * Compare two collections if they are the same return true
     * @author Marcus
     *
     * @param subset   The set you want to find in the superset
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
     * @author Marcus
     */
    private void initNavigationDrawer() {
        Log.d(Constants.TAG, "Init navigation drawer");
        drawerLayout = (DrawerLayout) activity.findViewById(R.id.layout_drawer);
        ListView drawerList = (ListView) activity.findViewById(R.id.left_drawer);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        drawerList.setAdapter(new ArrayAdapter<String>(activity, R.layout.drawer_list_item, menu));
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (menu[position].equals(activity.getResources().getString(R.string.menuConnect))) {
                    switchToFragment(connectFragment, null);
                } else if (menu[position].equals(activity.getResources().getString(R.string.menuHome))) {
                    switchToFragment(mainFragment, null);
                }

                drawerLayout.closeDrawers();
            }
        });
        drawerToggle = new ActionBarDrawerToggle(activity, drawerLayout, R.drawable.ic_drawer, R.string.drawerOpen, R.string.drawerClose);
        drawerLayout.setDrawerListener(drawerToggle);
        if (activity.getActionBar() != null) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getActionBar().setHomeButtonEnabled(true);
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void onCreate(){
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(new GoogleCallbacks())
                .addOnConnectionFailedListener(new ConnectionListener())
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }
    /**
     * @author Viktor Saltarski
     */
    public void onStart(){
        googleApiClient.connect();
    }
    /**
     * @author Viktor Saltarski
     */
    public void onStop(){
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void googleClick(int choice){
        if(choice==1){
            signInWithGPlus();
        }
        if(choice==2){
            signOutFromGPlus();
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void publishToGPlus(String message){
        Intent shareIntent = new PlusShare.Builder(activity)
                .setType("text/plain")
                .setText(message)
                .getIntent();
        activity.startActivityForResult(shareIntent,0);
    }
    /**
     * @author Viktor Saltarski
     */
    public void onActivityResult(int requestCode, int responseCode){
        if (requestCode == RC_SIGN_IN){
            if (responseCode != Activity.RESULT_OK){
                googleSignInClicked = false;
            }
            googleIntentInProgress = false;
            if (!googleApiClient.isConnecting()){
                googleApiClient.connect();
            }
        }
    }
    /**
     * @author Viktor Saltarski
     */
    //workinprogress
    public void updateGoogleButtons(boolean signedIn){
        if(signedIn){
            connectFragment.googleSignedIn();
        }else{
            connectFragment.googleSignedOut();
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void signInWithGPlus(){
        if(!googleApiClient.isConnecting()){
            googleSignInClicked = true;
            resolveSignInError();
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void signOutFromGPlus(){
        if(googleApiClient.isConnected()){
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
            updateGoogleButtons(false);
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void resolveSignInError(){
        if(googleConnectionResult.hasResolution()){
            try {
                googleIntentInProgress = true;
                googleConnectionResult.startResolutionForResult(activity, RC_SIGN_IN);
            }catch (IntentSender.SendIntentException e){
                googleIntentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    /**
     * Helpes resolve error when switching between google login and logout buttons
     * @return -
     */
    public boolean isGoogleConnected(){
        return googleApiClient.isConnected();
    }

    /**
     * @author Viktor Saltarski
     */
    private class ConnectionListener implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if(!connectionResult.hasResolution()){
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), activity, 0).show();
                return;
            }

            if (!googleIntentInProgress){
                googleConnectionResult = connectionResult;
                if (googleSignInClicked){
                    //trying to resolve errors as user has clicked sign in
                    resolveSignInError();
                }
            }
        }
    }
    /**
     * @author Viktor Saltarski
     */
    private class GoogleCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle bundle) {
            googleSignInClicked = false;
            Toast.makeText(activity, "Connected to Google+!", Toast.LENGTH_SHORT).show();
            updateGoogleButtons(true);
        }

        @Override
        public void onConnectionSuspended(int i) {
            googleApiClient.connect();
            updateGoogleButtons(false);
        }
    }
}
