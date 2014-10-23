package se.orw.projekt1;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class TestFragment extends Fragment {
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
