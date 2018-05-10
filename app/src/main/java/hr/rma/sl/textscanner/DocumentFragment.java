package hr.rma.sl.textscanner;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DocumentFragment extends ListFragment{
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    ArrayList<HashMap<String, String>> contactList;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        contactList = new ArrayList<>();
        lv = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        try {
            InputStream is = getResources().openRawResource(R.raw.documents);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            byte[] jsonData =  new String(buffer, "UTF-8").getBytes();
            ObjectMapper objectMapper = new ObjectMapper();
            Log.d("tag", "tu sam");
         //   Document doc = objectMapper.readValue(jsonData, Document.class);
            List<Document> myObjects = objectMapper.readValue(jsonData, new TypeReference<List<Document>>(){});
            for(int i = 0; i < myObjects.size();i++){
               // Document doc = myObjects.get(i);
                //HashMap<Integer, Document> doc = new HashMap<Integer, Document>();
               // doc.put(myObjects.get(i).getId(), myObjects.get(i));
                // tmp hash map for single contact
                HashMap<String, String> dokument = new HashMap<>();

                // adding each child node to HashMap key => value
                dokument.put("id", myObjects.get(i).getId());
                dokument.put("name", "Name: " + myObjects.get(i).getName());
                dokument.put("surname", "Surname: " + myObjects.get(i).getSurname());
                dokument.put("birthday","Birthday: " + myObjects.get(i).getBirthday());
                dokument.put("address", "Address: " + myObjects.get(i).getAddress());
                dokument.put("document number", "Document number: " + myObjects.get(i).getDocumentNumber());
                dokument.put("OIB", "OIB: " + myObjects.get(i).getoib());

                // adding contact to contact list
                contactList.add(dokument);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        /*String jsonStr;
        InputStream is = getResources().openRawResource(R.raw.documents);
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonStr = new String(buffer, "UTF-8");
            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray osobne = jsonObj.getJSONArray("osobna_iskaznica");

                    // looping through All Contacts
                    for (int i = 0; i < osobne.length(); i++) {
                        JSONObject c = osobne.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("ime");
                        String surname = c.getString("prezime");
						String documentNumber = c.getString("broj_osobne");
						String vrijediDo = c.getString("vrijedi_do");
						String gender = c.getString("spol");
						String birthday = c.getString("datum_rodenja");
                        String address = c.getString("adresa");
                        String dateOfIssue = c.getString("datum_izdavanja");
						String OIB = c.getString("OIB");

                        // tmp hash map for single contact
                        HashMap<String, String> dokument = new HashMap<>();

                        // adding each child node to HashMap key => value
                        dokument.put("id", id);
                        dokument.put("name", "Name: " + name);
						dokument.put("surname", "Surname: " + surname);
                        dokument.put("birthday","Birthday: " + birthday);
                        dokument.put("address", "Address: " + address);
						dokument.put("document number", "Document number: " + documentNumber);
						dokument.put("OIB", "OIB: " + OIB);

                        // adding contact to contact list
                        contactList.add(dokument);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), contactList,
                R.layout.fragment_document, new String[]{"name", "surname", "birthday", "address", "OIB", "document number"},
                new int[]{R.id.name, R.id.surname, R.id.birthday, R.id.address, R.id.OIB, R.id.document_number});
        lv.setAdapter(adapter);



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Item: " , Toast.LENGTH_SHORT).show();
            }
        });

      /* ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Planets, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
/*
        InputStream is = getResources().openRawResource(R.raw.documents);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int n;
            try {
                n = reader.read(buffer);
                while ((n != -1)) {
                    try {
                        writer.write(buffer, 0, n);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String jsonString = writer.toString();*/
    }



   /* private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity().getApplicationContext(), "Json Data is downloading", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            InputStream is = getResources().openRawResource(R.raw.documents);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                int n;
                try {
                    n = reader.read(buffer);
                    while ((n != -1)) {
                        try {
                            writer.write(buffer, 0, n);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String jsonStr = writer.toString();

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("contacts");

                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        String address = c.getString("address");
                        String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        String home = phone.getString("home");
                        String office = phone.getString("office");

                        // tmp hash map for single contact
                        HashMap<String, String> contact = new HashMap<>();

                        // adding each child node to HashMap key => value
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(getActivity().getApplicationContext(), contactList,
                    R.layout.fragment_document, new String[]{"email", "mobile"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);
        }
    }*/
}