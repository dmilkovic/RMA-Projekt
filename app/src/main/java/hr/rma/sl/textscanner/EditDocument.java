package hr.rma.sl.textscanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class EditDocument extends AppCompatActivity {
    private EditText name, surname, address, birthday, oib, documentNumber;
    private Document document;
    private Intent intent;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        address = findViewById(R.id.address);
        birthday = findViewById(R.id.birthday);
        documentNumber = findViewById(R.id.document_number);
        oib = findViewById(R.id.OIB);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        document = (Document)bundle.getSerializable("doc");
        position = intent.getIntExtra("pos", 0);

        name.setText(document.getName());
        documentNumber.setText(document.getDocumentNumber());
        oib.setText(document.getoib());
        birthday.setText(document.getBirthday());
        address.setText(document.getAddress());
        surname.setText(document.getSurname());

        Log.d("tag", "poruka:" + intent.getStringExtra("mess") + document.toString());

     //   DocumentFragment.setMyObject(document, position);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                document.setName(name.getText().toString());
                document.setSurname(surname.getText().toString());
                document.setBirthday(birthday.getText().toString());
                document.setAddress(address.getText().toString());
                document.setoib(oib.getText().toString());
                document.setDocumentNumber(documentNumber.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("doc", document);
                intent.putExtras(bundle);
                intent.putExtra("pos", position);
                setResult(RESULT_OK, intent);
                finish();
                Log.d("tag", document.toString());
            }
        });
    }

}
