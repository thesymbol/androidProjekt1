package se.orw.projekt1.Twitter;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.orw.projekt1.Controller;
import se.orw.projekt1.R;

/**
 * The Twitter login fragment
 *
 * Created by Marcus on 2014-10-23.
 */
public class TwitterFragment extends android.support.v4.app.Fragment {
    private TwitterController twitterController;
    private Controller controller;

    public TwitterFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the controller
     *
     * @param controller The controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * @param inflater           -
     * @param container          -
     * @param savedInstanceState -
     * @return -
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);
        twitterController = new TwitterController(view, this, controller);
        return view;
    }

    /**
     * Called once the login is completed/failed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        twitterController.onDestroy();
    }
}
