package se.orw.projekt1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AppEventsLogger;

/**
 * The main activity for the app
 *
 * Created by Marcus on 2014-10-22.
 */
public class MainActivity extends FragmentActivity {
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = new Controller(this, savedInstanceState);
        controller.onCreate();
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handles the drawer on the left sides button
        return controller.getDrawerToggle().onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // used to sync the drawer toggle state (opened/closed)
        controller.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Once the app changes configuration (eg rotation).
        controller.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    /**
     * @author Viktor Saltarski
     *
     * @param requestCode -
     * @param resultCode -
     * @param data -
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        controller.onActivityResult(requestCode, resultCode);
    }

    @Override
    protected void onStart(){
        super.onStart();
        controller.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
        controller.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        controller.onSaveInstanceState(outState);
    }
}
