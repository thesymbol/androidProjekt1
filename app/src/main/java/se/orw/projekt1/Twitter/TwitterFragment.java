package se.orw.projekt1.Twitter;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.orw.projekt1.Controller;
import se.orw.projekt1.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TwitterFragment extends android.support.v4.app.Fragment {
    private View view;
    private TwitterController twitterController;
    private Controller controller;

    public TwitterFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the controller
     *
     * @param controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_twitter, container, false);
        twitterController = new TwitterController(view, this, controller);
        return view;
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        twitterController.onDestroy();
    }
}
