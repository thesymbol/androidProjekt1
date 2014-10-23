package se.orw.projekt1;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TestFragment extends Fragment {
    private WebView twitterLoginWebView;
    private View view;
    private Button btnConnect;
    private Button btnDisconnect;
    private Controller controller;

    public TestFragment() {
        // Required empty public constructor
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void loadUrl(String url) {
        twitterLoginWebView.loadUrl(url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_test, container, false);
        }
        init();
        registerListeners();
        return view;
    }

    private void init() {
        btnConnect = (Button) view.findViewById(R.id.btnConnect);
        btnDisconnect = (Button) view.findViewById(R.id.btnDisconnect);

        twitterLoginWebView = (WebView)view.findViewById(R.id.twitter_login_web_view);
        twitterLoginWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if( url.contains(Constants.TWITTER_CALLBACK_URL))
                {
                    Uri uri = Uri.parse(url);
                    controller.saveAccessTokenAndFinish(uri);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
    }

    private void registerListeners() {
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.twitterConnect();
            }
        });

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.twitterDisconnect();
            }
        });
    }
}
