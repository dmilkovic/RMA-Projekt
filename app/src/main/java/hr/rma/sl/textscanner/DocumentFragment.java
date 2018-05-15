package hr.rma.sl.textscanner;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DocumentFragment extends ListFragment {
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private ImageButton saveButton, camButton;
    ArrayList<HashMap<String, String>> contactList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        contactList = new ArrayList<>();
        saveButton = view.findViewById(R.id.saveButton);
        camButton = view.findViewById(R.id.camera_button);
        lv = (ListView) view.findViewById(android.R.id.list);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                /// create();
            }
        });
        //  saveButton = getActivity().findViewById(R.id.saveButton);
       /*saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when play is clicked show stop button and hide play button
               Log.d("tag", "uspio");
            }
        });*/
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Document> myObjects = null;
            if (isFilePresent(getActivity(), "storage.json")) {
                // String jsonString = read(getActivity(), "storage.json");
                myObjects = objectMapper.readValue(read(getActivity(), "storage.json"), new TypeReference<List<Document>>() {
                });
                //do the json parsing here and do the rest of functionality of app
            } else {
                boolean isFileCreated = create(getActivity(), "{}");
                if (isFileCreated) {
                    //proceed with storing the first todo  or show ui

                    InputStream is = getResources().openRawResource(R.raw.documents);
                    int size = is.available();
                    byte[] buffer = new byte[size];
                    is.read(buffer);
                    is.close();
                    byte[] jsonData = new String(buffer, "UTF-8").getBytes();
                    //    ObjectMapper objectMapper = new ObjectMapper();
                    Log.d("tag", "tu sam");
                    List<Document> myObjects1 = objectMapper.readValue(jsonData, new TypeReference<List<Document>>() {
                    });

                    Document dokument = new Document();
                    // adding each child node to HashMap key => value
                    dokument.setId("4");
                    dokument.setName("Zorro");

                    myObjects1.add(dokument);
                    String jsonInString = objectMapper.writeValueAsString(myObjects1);

                    Log.d("tag1", "*****" + jsonInString);
                    create(getActivity(), jsonInString);

                    myObjects = objectMapper.readValue(read(getActivity(), "storage.json"), new TypeReference<List<Document>>() {
                    });
                } else {
                    //show error or try again.
                }
            }

            for (int i = 0; i < myObjects.size(); i++) {
                contactList.add(myObjects.get(i).createHashMap());
                Log.d("tag1", myObjects.get(i).toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), contactList,
                R.layout.content_document_list, new String[]{"name", "surname", "birthday", "address", "OIB", "document number"},
                new int[]{R.id.name, R.id.surname, R.id.birthday, R.id.address, R.id.OIB, R.id.document_number});
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Item: ", Toast.LENGTH_SHORT).show();
            }
        });
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
    }


    //upravljanje json-om
    private byte[] read(Context context, String fileName) {
        try {
            InputStream is = context.openFileInput(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            byte[] jsonData = new String(buffer, "UTF-8").getBytes();
            Log.d("tag", new String(buffer, "UTF-8"));
            return jsonData;

         /*   FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();*/
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean create(Context context, String jsonString) {
        String FILENAME = "storage.json";
        FileOutputStream fos;
        try {
          /*  fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            if (jsonString != null) {
                oos.writeObject(jsonString.getBytes());
            }
            oos.close();
            Log.d("tag", "Spremio u ");
            return true;*/
            Writer output = null;
            File file = new File(context.getFilesDir().getAbsolutePath() + "/" + FILENAME);
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonString);
            output.close();
            Toast.makeText(getActivity().getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

}