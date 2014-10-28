package se.orw.projekt1;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 *
 */
public class MainFragment extends android.support.v4.app.Fragment {
    private CheckBox cbFacebook, cbTwitter, cbGoogle;
    private EditText etTextBox;
    private TextView tvPhrase;
    private Controller controller;
    private String test="";

    public MainFragment() {
        // Required empty public constructor
    }

    public void setController() {
        this.controller = controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        cbFacebook = (CheckBox) view.findViewById(R.id.cbFacebook);
        cbTwitter = (CheckBox) view.findViewById(R.id.cbTwitter);
        cbGoogle = (CheckBox) view.findViewById(R.id.cbGoogle);
        etTextBox = (EditText) view.findViewById(R.id.etTextBox);
        tvPhrase = (TextView) view.findViewById(R.id.tvPhrase);
        return view;
    }
}
