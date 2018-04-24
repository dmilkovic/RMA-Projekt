package hr.rma.sl.textscanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentShareText extends Fragment {


    public FragmentShareText() {
        // Required empty public constructor
    }
    private FloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_share_text, container, false);
       // final EditText shareText = getView().findViewById(R.id.shareText);

        // Get the Intent that started this activity and extract the string
     //   Intent intent = getIntent();
      //  final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
     //   shareText.setText(message);

        /*Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

       //fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //String shareBody = shareText.getText().toString();
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
              //  sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.send_to)));
            }
        });*/
        Log.d("text", "pozvan je");

        return rootView;
    }

}
