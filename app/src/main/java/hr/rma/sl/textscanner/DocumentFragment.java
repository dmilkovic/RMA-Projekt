package hr.rma.sl.textscanner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

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
import java.io.Serializable;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static hr.rma.sl.textscanner.MainActivity.EXTERNAL_MEMORY;


public class DocumentFragment extends ListFragment {
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private List<Document> myObjects = null;
    private ImageButton camButton, galleryButton;
    private int int_identifier = 3;
    private ObjectMapper objectMapper;
    public Document changeDocument;
    private SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> contactList;
    //ocr
    //Bitmap bitmap;
    final int REQUEST_TAKE_PHOTO = 1;
    final int PICK_IMAGE_REQUEST = 2;
    //final int RESULT_OK = 0;
    public static boolean storage_flag;
    public static final int EXTERNAL_MEMORY = 2;
    Uri photoURI = null;
    String imageFileName;
    File photoFile = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_document, container, false);
        contactList = new ArrayList<>();
        camButton = view.findViewById(R.id.camera_button);
        galleryButton = view.findViewById(R.id.galleryButton);
        camButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestStoragePermission();
                }else{
                    takePicture();
                }
            }
        });
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                chooseFromGallery();
            }
        });
        lv = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        objectMapper = new ObjectMapper();
        try {
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
                    myObjects = objectMapper.readValue(jsonData, new TypeReference<List<Document>>() {
                    });

                    Document dokument = new Document();
                    // adding each child node to HashMap key => value
                    dokument.setId("4");
                    dokument.setName("Zorro");

                    myObjects.add(dokument);
                    String jsonInString = objectMapper.writeValueAsString(myObjects);

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
                Log.d("tag1", myObjects.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        refreshAdapter();
    }
    private void refreshAdapter(){
        adapter = new SimpleAdapter(getActivity().getApplicationContext(), contactList,
                R.layout.content_document_list, new String[]{"name", "surname", "birthday", "address", "OIB", "document number"},
                new int[]{R.id.name, R.id.surname, R.id.birthday, R.id.address, R.id.OIB, R.id.document_number});
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent appInfo = new Intent(getActivity(), EditDocument.class);
                changeDocument = myObjects.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("doc", changeDocument);
                appInfo.putExtras(bundle);
                appInfo.putExtra("pos", position);
                startActivityForResult(appInfo, int_identifier);
                Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT).show();
            }
        });
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
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private static boolean create(Context context, String jsonString) {
        String FILENAME = "storage.json";
        FileOutputStream fos;
        try {
            Writer output = null;
            File file = new File(context.getFilesDir().getAbsolutePath() + "/" + FILENAME);
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonString);
            output.close();
           Toast.makeText(context.getApplicationContext(), "Composition saved", Toast.LENGTH_LONG).show();
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

   /* public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }*/

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == int_identifier) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                Document doc = (Document)bundle.getSerializable("doc");
                int position = data.getIntExtra("pos", 0);
                myObjects.set(position, doc);
                String jsonInString = null;
                try {
                    jsonInString = objectMapper.writeValueAsString(myObjects);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                Log.d("tag1", "*****" + jsonInString);
                create(getActivity(), jsonInString);
                contactList.clear();
                for (int i = 0; i < myObjects.size(); i++) {
                    contactList.add(myObjects.get(i).createHashMap());
                    Log.d("tag1", myObjects.toString());
                }
                 Log.d("tag3", doc.toString());
            }
            adapter.notifyDataSetChanged();
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                galleryAddPic();
                Log.d("pic", "Dobros");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("pic", "Nije dobro");
            }
            Log.d("tag", "dfg2" + mCurrentPhotoPath);
            Uri noviURI = Uri.fromFile(photoFile);
            Bitmap bitmap = null;
            Glide.with(getActivity().getApplicationContext()).load(noviURI).into(target2);
        }else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            Glide.with(getActivity().getApplicationContext()).load(uri).into(target2);
        }
    }

    //OCR

    //upali kameru i uslikaj
    private void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            List<ResolveInfo> resolvedIntentActivities = getActivity().getApplicationContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(),
                        "hr.rma.fileprovider",
                        photoFile);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                //setResult(RESULT_OK, takePictureIntent);
            }
        }
    }
    private void chooseFromGallery() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //varijable za spremanje datuma
    String birthday = null, expireDate = null, dateOfIssue = null, OIB = null;
    String ime = "", prezime = null, spol = null, address = null;
    String documentNumber = null;
    Boolean side2Flag=false;
    int nameCnt = 0;
    private BaseTarget target2 = new BaseTarget<BitmapDrawable>() {
        @Override
        public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
            // do something with the bitmap
            if(bitmap != null) {
                //shareText.setText("");
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity().getApplicationContext()).build();
                Frame imageFrame = new Frame.Builder().setBitmap(bitmap.getBitmap()).build();
                String imageText = "";
                String fullText = "";
                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                //Log.d("tag" ,"Bitmapfsd" + bitmap.toString()+ " " +textBlocks);
                boolean surnameFlag = false, nameFlag = false, nameFlag1 = false;
                for (int i = 0; i < textBlocks.size(); i++)
                {

                    TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                    imageText = textBlock.getValue();                   // return string
                    Log.d("tag", "ovo je" + imageText);
                    // myText.append(imageText);
                    if(imageText.contains("I0HRV") || imageText.contains("<"))
                    {
                        side2Flag = true;
                        continue;
                    }
                   // if(imageText.contains("RH REPUBLIC REPUBLIKA OF HRVATSKA CROATIA")) continue;
                   // imageText =  imageText.replaceAll("[a-z]", "");
                    if(imageText.contains("Residence") /*|| imageText.contains("identification")*/) side2Flag = true;

                    /*else if(imageText.contains("Ime/Name"))
                    {
                        nameFlag1 = true;
                        continue;
                    }

                  /* if(surnameFlag && !nameFlag)
                    {
                        prezime = imageText;
                        nameFlag = true;
                        continue;
                    }*/
                    if(!side2Flag)
                    {
                        if(imageText.contains("M/M"))
                        {
                            spol = imageText.substring(imageText.indexOf("M/M"),imageText.indexOf("M/M")+3);
                        }else if(imageText.contains("Ž/F"))
                        {
                            spol = imageText.substring(imageText.indexOf("Ž/F"),imageText.indexOf("Ž/F")+3);
                        }
                        imageText = imageText.replaceAll("[A-Z][a-z]", "");
                        imageText = imageText.replaceAll("[a-z]", "");
                        imageText = imageText.replaceAll("OSOBNA", "");
                        imageText = imageText.replaceAll("ISKAZNICA", "");
                        imageText = imageText.replaceAll("REPUBLIC", "");
                        imageText = imageText.replaceAll("REPUBLIKA", "");
                        imageText = imageText.replaceAll("IDENTITY", "");
                        imageText = imageText.replaceAll("DENTITY", "");
                        imageText = imageText.replaceAll("CARD", "");
                        imageText = imageText.replaceAll("HRVATSKA", "");
                        imageText = imageText.replaceAll("CROATIA", "");
                        imageText = imageText.replaceAll("\\bHRV\\b", "");
                        imageText = imageText.replaceAll("\\bRH\\b", "");
                        imageText = imageText.replaceAll("\\bOF\\b", "");
                        imageText = imageText.replaceAll("/", "");
                        String check = getDate(imageText, "name");
                        if(check.length() > 2 && nameCnt<2)
                        {
                            Log.d("tag", "check:" + getDate(imageText, "name"));
                            if(ime.contains(check)) continue;

                            ime += " "+ getDate(imageText, "name");
                            //nameCnt++;
                        }
                    }
                    fullText += imageText;
                    Log.d("tag", "Ovo je" + imageText + side2Flag);
                }
                //shareText.append(fullText);
                /****Trebat ce ti kasnije!!!!!****
                 //   Intent intent = new Intent(getActivity(), ShareText.class);
                 //   intent.putExtra(EXTRA_MESSAGE, fullText);
                 //   startActivity(intent);*/

                if(side2Flag)
                {
                    getDates(fullText, "dateOfIssue");
                   /* if(fullText.contains("Prebiv"))
                    {
                      //  if(fullText.indexOf("Prebiv") - 11 > -1) getDates((fullText.substring(fullText.indexOf("Prebiv") - 11, fullText.indexOf("Prebiv"))), "OIB");
                        //if(fullText.contains("OIB/Personal identification number") || fullText.contains("OI8/Personal identification number") || fullText.contains("OIE/Personal identification number")|| fullText.contains("OIBPersonal identification number")) getDates(fullText.substring(fullText.indexOf("OIB/Personal identification number")+"OIB/Personal identification number".length(), fullText.length()), "OIB");
                        getDates(fullText, "OIB");
                    }*/
                    getDates(fullText, "OIB");
                   // if(fullText.contains(fullText.substring(fullText.indexOf("Residence")+"Residence".length(), fullText.indexOf("zdala"))))
                  if(fullText.indexOf("Residence") < fullText.indexOf("zdala"))
                    {
                        Log.d("tag", fullText.indexOf("Residence") +" "+ fullText.indexOf("zdala"));
                        address = fullText.substring(fullText.indexOf("Residence")+"Residence".length(), fullText.indexOf("zdala")).replaceAll("[A-Z][a-z]", "").replaceAll("[a-z]", "").replaceAll(dateOfIssue+".", "").replaceAll(OIB, "").replaceAll("\bOIB\b", "");
                    }
                    Log.d("side2", fullText + "***" + dateOfIssue + side2Flag + "**OIB: " + OIB + "** adresa:" + address);
                    //  Log.d("address", "adresa:" + address);
                }
                else{
                    getDates(fullText, "date");
                    //   ime = fullText.substring((fullText.indexOf(expireDate)+11), fullText.length());
                    getDates(fullText, "documentnumber");
                  /* fullText = fullText.replaceAll(birthday, "");
                    fullText = fullText.replaceAll(expireDate, "");
                    fullText = fullText.replaceAll(documentNumber, "");
                    fullText = fullText.replaceAll(spol, "");
                    fullText = fullText.replaceAll("[A-Z][a-z]", "");
                    fullText = fullText.replaceAll("[a-z]", "");
                    fullText = fullText.replaceAll("OSOBNA", "");
                    fullText = fullText.replaceAll("ISKAZNICA", "");

                    fullText = fullText.replaceAll("REPUBLIC", "");
                    fullText = fullText.replaceAll("REPUBLIKA", "");
                    //fullText = fullText.replaceAll("OF", "");
                    fullText = fullText.replaceAll("IDENTITY", "");
                    fullText = fullText.replaceAll("CARD", "");
                    fullText = fullText.replaceAll("HRVATSKA", "");
                    fullText = fullText.replaceAll("CROATIA", "");
                    fullText = fullText.replaceAll("\\bHRV\\b", "");
                    fullText = fullText.replaceAll("\\bRH\\b", "");
                    fullText = fullText.replaceAll("\\bOF\\b", "");
                    fullText = fullText.replaceAll("/", "");*/
                    Log.d("ime", fullText+ "\n **"+ birthday + "***** " +expireDate +"**"+ ime +" **** " + spol +"\n***" + documentNumber);
                }
            }
        }
        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(1440, 1080);
        }
        @Override
        public void removeCallback(SizeReadyCallback cb) {}
    };

    private void galleryAddPic() throws IOException {
        Uri contentUri = Uri.fromFile(photoFile);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        if (mediaScanIntent.resolveActivity(getActivity().getApplication().getPackageManager()) != null) {
            getActivity().sendBroadcast(mediaScanIntent);
            Log.d("pic", "Dobro");
        }else{
            // "Rucno" dodavanje u MediaStore:
            System.out.println("***** There is no app which would handle this intent. Updating MediaStore manually...");
            try {
                MediaStore.Images.Media.insertImage(
                        getActivity().getApplication().getContentResolver(), String.valueOf(photoFile),
                        imageFileName, null);
                getActivity().getApplication().sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        contentUri));
            } catch (FileNotFoundException e) {
                System.out.println("***** Error updating MediaStore manually...");
            }
        }

    }

    //check for camera permission
    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_MEMORY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_MEMORY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast storageEnable = Toast.makeText(getActivity().getApplicationContext(), "Please enable storage", Toast.LENGTH_LONG);
                    storageEnable.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
        // end of check permission
    }

    private void getDates(String fullText, String type)
    {
        List<String> dates = new ArrayList<>();
        int len = 10, i  = 0, cnt = 0;
        if(type.toLowerCase().equals("documentnumber")) len = 9;
        if(type.toLowerCase().equals("oib")) len = 11;
        while(len+i <= fullText.length()) {
            String check = fullText.substring(i, len + i);
            //   if(check.contains(s1)) cnt++;
            String date1 = new String();
           /* if (type.equals("date")){
                date1 = getDate(check, "date");
                if(date1.length()==10)
                {
                    dates.add(date1);
                    Log.d("ime", date1);
                }*/
            if (type.equals("date")) {
                date1 = getDate(check, "date");
                if (date1.length() == 10) {
                 //   Log.d("side2", "datum6" + getDate(check, "date") + dates.toString());
                    dates.add(date1);
                }
            }
            else if(type.equals("documentnumber"))
            {
                String str = getDate(check, "documentnumber");
                if(str.length() == 9) {
                    documentNumber = str;
                    return;
                }
            }else if(type.equals("dateOfIssue")){
                String str = getDate(check, "date");
                if(str.length() == 10)
                {
                    //Log.d("side2", "datum" + getDate(check, "date"));
                    dateOfIssue = str;
                    return;
                }
            }else if(type.equals("OIB"))
            {
               // Log.d("side2", "check" + check);
               // Log.d("side2", "OIB" + getDate(check, "OIB"));
                String str = getDate(check, "OIB");
                if(str.length() == 11)
                {
                    //Log.d("side2", "OIB" + getDate(check, "OIB"));
                    OIB = str;
                    return;
                }
            }
            //ima li string 11 znakova(koliko ima i datum
            i++;
        }
       Log.d("side2", "gtzf"  +dates.size());
        //usporedi datume i vidi koji je rođendan
        if(dates.size() >1 ) {
            try {
                DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
                Date tempDate = null;
                Date tempDate1 = null;
                dates.set(0, dates.get(0) + ".");
                dates.set(1, dates.get(1) + ".");
                tempDate = format.parse(dates.get(0));
                tempDate1 = format.parse(dates.get(1));
                Log.d("side2", "efe" + tempDate.toString()+ tempDate1.toString());
                if (tempDate.before(tempDate1)) {
                    birthday = dates.get(0);
                    expireDate = dates.get(1);
                } else {
                    birthday = dates.get(1);
                    expireDate = dates.get(0);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private  String getDate(String desc, String type) {
        int count=0;
        Matcher m = null;
        String allMatches = new String();
        //desc = "19.01.1998.";
        //Matcher m = Pattern.compile("\\d\\d.\\d\\d.\\d\\d\\d\\d.").matcher(desc);
        if(type.toLowerCase().equals("date")) m = Pattern.compile("[0-9]{2}\\.[0-9]{2}\\.[0-9]{4}").matcher(desc);
        if(type.toLowerCase().equals("documentnumber")){
            m = Pattern.compile("[0-9]{9}").matcher(desc);
        }
        if(type.toLowerCase().equals("oib")) m = Pattern.compile("[0-9]{11}").matcher(desc);
        if(type.toLowerCase().equals("name")) m = Pattern.compile("[A-Z]{3,}").matcher(desc);

        while (m.find()) {
            allMatches = m.group();
        }
        return allMatches;
    }



}