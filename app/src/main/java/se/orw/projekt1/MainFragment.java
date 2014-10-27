package se.orw.projekt1;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 *
 */
public class MainFragment extends Fragment {
    private Button btnTranslate;
    private EditText etTextBox;
    private TextView tvPhrase;
    private Controller controller;

    public MainFragment() {
        // Required empty public constructor
    }

    public void setController(){
        this.controller = controller;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        btnTranslate = (Button)view.findViewById(R.id.btnTranslate);
        etTextBox = (EditText)view.findViewById(R.id.etTextBox);
        tvPhrase = (TextView)view.findViewById(R.id.tvPhrase);
        btnTranslate.setOnClickListener(new ButtonclickListener());
        return view;
    }


    private class ButtonclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //           controller.
        }
    }
}