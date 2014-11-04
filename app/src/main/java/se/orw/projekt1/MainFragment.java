package se.orw.projekt1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * The main fragment, where the "magic" is happening!
 *
 * Created by Mattias on 2014-10-28.
 */
public class MainFragment extends android.support.v4.app.Fragment {
    private CheckBox cbFacebook, cbTwitter, cbGoogle;
    private EditText etTextBox;
    private Button btnSend;
    private Controller controller;
    private String message = "";

    public MainFragment() {
        // Required empty public constructor
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEtText() {
        return etTextBox.getText().toString();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        cbFacebook = (CheckBox) view.findViewById(R.id.cbFacebook);
        cbTwitter = (CheckBox) view.findViewById(R.id.cbTwitter);
        cbGoogle = (CheckBox) view.findViewById(R.id.cbGoogle);
        etTextBox = (EditText) view.findViewById(R.id.etTextBox);
        btnSend = (Button)view.findViewById(R.id.btnSend);
        etTextBox.setText(message);
        btnSend.setOnClickListener(new ButtonClickListener());
        return view;
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.publishToSelected(etTextBox.getText().toString(),cbFacebook.isChecked(),cbTwitter.isChecked(),cbGoogle.isChecked());
        }
    }
}
