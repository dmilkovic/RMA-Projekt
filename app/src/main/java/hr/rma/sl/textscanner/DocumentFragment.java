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
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;


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
                R.layout.content_document_list, new String[]{"name", "expireDate", "birthday", "address", "OIB", "document number", "sex", "dateOfIssue"},
                new int[]{R.id.name, R.id.expireDate, R.id.birthday, R.id.address, R.id.OIB, R.id.document_number, R.id.sex, R.id.dateOfIssue});
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
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                // TODO Auto-generated method stub
                Log.v("long clicked","pos: " + pos);
                return true;
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
                DocumentRegex d = new DocumentRegex();
                d.generateDocumentData(textBlocks);
                Document doc = new Document();
                if(d.getSide2Flag())
                {
                  dateOfIssue = d.getDateOfIssue();
                  OIB = d.getOIB();
                  address = d.getAddress();
                  doc.setDateOfIssue(dateOfIssue);
                  doc.setoib(OIB);
                  doc.setAddress(address);
                  Log.d("side2", fullText + "***" + dateOfIssue + side2Flag + "**OIB: " + OIB + "** adresa:" + address);
                }else{
                    birthday = d.getBirthday();
                    expireDate = d.getExpireDate();
                    documentNumber = d.getDocumentNumber();
                    ime = d.getIme();
                    spol = d.getSpol();
                    doc.setName(ime);
                    doc.setBirthday(birthday);
                    doc.setexpireDate(expireDate);
                    doc.setGender(spol);
                    doc.setDocumentNumber(documentNumber);
                    Log.d("ime", fullText+ "\n **"+ birthday + "***** " +expireDate +"**"+ ime +" **** " + spol +"\n***" + documentNumber);
                }
                myObjects.add(doc);
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
                refreshAdapter();
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
}