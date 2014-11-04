package se.orw.projekt1;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.SignInButton;


/**
 * Connect fragment that handles the Sign into * buttons
 *
 * Created by Marcus on 2014-10-23.
 */
public class ConnectFragment extends android.support.v4.app.Fragment {
    private View view;
    private Controller controller;
    private UiLifecycleHelper uiHelper;
    private Button btnTwitterConnect;
    private SignInButton btnGoogleConnect;
    private Button btnGoogleDisconnect;

    public ConnectFragment() {
        // Required empty public constructor
    }

    /**
     * Sets the controller
     * @author Marcus
     *
     * @param controller The controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }

    /**
     * The fragment's initial view
     * @author Marcus
     *
     * @param inflater -
     * @param container -
     * @param savedInstanceState -
     * @return -
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);
        if(savedInstanceState != null) {
            MainActivity activity = (MainActivity) getActivity();
            controller = activity.getController();
            controller.switchToConnectFragment();
        }
        if (controller.isGoogleConnected()){
            googleSignedIn();
        }
        init();
        registerListeners();
        return view;
    }

    /**
     * The onCreate method of the Fragment Overridden for facebook API.
     * @author Marcus
     *
     * @param savedInstanceState -
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session.StatusCallback callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(state);
            }
        };
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    /**
     * Facebook onResume Override
     * @author Marcus
     */
    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session.getState());
        }
        uiHelper.onResume();
        if (controller.isGoogleConnected()){
            googleSignedIn();
        }
    }

    /**
     * Facebook onActivityResult Override
     * @author Marcus
     *
     * @param requestCode -
     * @param resultCode -
     * @param data -
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Facebook onPause Override
     * @author Marcus
     */
    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    /**
     * Facebook onDestroy Override
     * @author Marcus
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    /**
     * Facebook onSaveInstanceState Override
     * @author Marcus
     *
     * @param outState -
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    /**
     * @author Viktor Saltarski
     */
    public void googleSignedIn(){
        if (btnGoogleConnect!=null && btnGoogleDisconnect!=null){
            btnGoogleConnect.setVisibility(View.GONE);
            btnGoogleDisconnect.setVisibility(View.VISIBLE);
        }
    }
    /**
     * @author Viktor Saltarski
     */
    public void googleSignedOut(){
        if (btnGoogleConnect!=null && btnGoogleDisconnect!=null) {
            btnGoogleConnect.setVisibility(View.VISIBLE);
            btnGoogleDisconnect.setVisibility(View.GONE);
        }
    }

    /**
     * Initialize the fragment
     * @author Marcus
     */
    private void init() {
        btnTwitterConnect = (Button) view.findViewById(R.id.btnTwitterConnect);
        LoginButton btnFacebookConnect = (LoginButton) view.findViewById(R.id.btnFacebookConnect);
        btnFacebookConnect.setFragment(this);
        btnFacebookConnect.setPublishPermissions(Constants.PERMISSIONS);
        btnTwitterConnect.setText(controller.updateTwitterButtonText());
        btnGoogleConnect = (SignInButton)view.findViewById(R.id.btnGoogleConnect);
        btnGoogleDisconnect = (Button)view.findViewById(R.id.btnGoogleDisconnect);
    }

    /**
     * Register listeners for buttons
     * @author Marcus
     */
    private void registerListeners() {
        btnTwitterConnect.setOnClickListener(new TwitterClickListener());
        btnGoogleConnect.setOnClickListener(new GoogleConnectClickListener());
        btnGoogleDisconnect.setOnClickListener(new GoogleDisconnectClickListener());
    }

    /**
     * Handles facebook log in/log outs
     * @author Marcus
     *
     * @param state The state the Facebook API is in.
     * */
    private void onSessionStateChange(SessionState state) {
        controller.onFacebookStateChange(state);
    }

    /**
     * Handles Twitter button presses
     * @author Marcus
     */
    private class TwitterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.twitterConnect();
            btnTwitterConnect.setText(controller.updateTwitterButtonText());
        }
    }

    /**
     * Handles Google button presses on connect
     * @author Viktor Saltarski
     */
    private class GoogleConnectClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.googleClick(1);
        }
    }

    /**
     * Handles Google button presses on disconnect
     * @author Viktor Saltarski
     */
    private class GoogleDisconnectClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.googleClick(2);
        }
    }

}
