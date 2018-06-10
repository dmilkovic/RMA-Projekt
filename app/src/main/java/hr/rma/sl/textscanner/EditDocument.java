package hr.rma.sl.textscanner;

import android.Manifest;
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
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EditDocument extends AppCompatActivity {
    private EditText nameEditText, expireDateEditText, addressEditText, birthdayEditText, oibEditText, documentNumberEditText, dateOfIssueEditText, genderEditText;
    private Document document;
    private Intent intent;
    private int position;
    final int REQUEST_TAKE_PHOTO = 1;
    final int PICK_IMAGE_REQUEST = 2;
    //final int RESULT_OK = 0;
    public static boolean storage_flag;
    public static final int EXTERNAL_MEMORY = 2;
    Uri photoURI = null;
    String imageFileName;
    File photoFile = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_document);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nameEditText = findViewById(R.id.name);
        expireDateEditText = findViewById(R.id.expireDate);
        addressEditText = findViewById(R.id.address);
        birthdayEditText = findViewById(R.id.birthday);
        documentNumberEditText = findViewById(R.id.document_number);
        dateOfIssueEditText = findViewById(R.id.dateOfIssue);
        genderEditText = findViewById(R.id.sex);
        oibEditText = findViewById(R.id.OIB);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        intent = getIntent();
        Bundle bundle = intent.getExtras();
        document = (Document) bundle.getSerializable("doc");
        position = intent.getIntExtra("pos", 0);

        nameEditText.setText(document.getName());
        documentNumberEditText.setText(document.getDocumentNumber());
        oibEditText.setText(document.getoib());
        birthdayEditText.setText(document.getBirthday());
        addressEditText.setText(document.getAddress());
        expireDateEditText.setText(document.getexpireDate());
        dateOfIssueEditText.setText(document.getDateOfIssue());
        genderEditText.setText(document.getGender());

        Log.d("tag", "poruka:" + intent.getStringExtra("mess") + document.toString());

        //   DocumentFragment.setMyObject(document, position);

        FloatingActionButton shareFab = (FloatingActionButton) findViewById(R.id.shareFab);
        FloatingActionButton cameraFab = (FloatingActionButton) findViewById(R.id.cameraFab);
        FloatingActionButton galleryFab = (FloatingActionButton) findViewById(R.id.galleryFab);
        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);

        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s ="Ime: " + nameEditText.getText().toString() + "\n";
                s +="Spol: " + genderEditText.getText().toString() + "\n";
                s +="Vrijedi do: " + expireDateEditText.getText().toString() + "\n";
                s +="Datum izdavanja: " + dateOfIssueEditText.getText().toString() + "\n";
                s +="Datum rođenja: " + birthdayEditText.getText().toString() + "\n";
                s +="Broj dokumenta: " + documentNumberEditText.getText().toString() + "\n";
                s +="Adresa: " + addressEditText.getText().toString() + "\n";
                s +="OIB: " + oibEditText.getText().toString() + "\n";

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, s);
                startActivity(Intent.createChooser(sharingIntent, getResources().getText(R.string.send_to)));
            }
        });

        cameraFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission();
                } else {
                    takePicture();
                }
            }
        });

        galleryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFromGallery();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                document.setName(nameEditText.getText().toString());
                document.setexpireDate(expireDateEditText.getText().toString());
                document.setBirthday(birthdayEditText.getText().toString());
                document.setAddress(addressEditText.getText().toString());
                document.setoib(oibEditText.getText().toString());
                document.setDocumentNumber(documentNumberEditText.getText().toString());
                document.setGender(genderEditText.getText().toString());
                document.setDateOfIssue(dateOfIssueEditText.getText().toString());

                Bundle bundle = new Bundle();
                bundle.putSerializable("doc", document);
                intent.putExtras(bundle);
                intent.putExtra("pos", position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
    //obrada slike
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            Glide.with(getApplicationContext()).load(noviURI).into(target2);
        }else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            Glide.with(getApplicationContext()).load(uri).into(target2);
        }
    }

    //OCR

    //upali kameru i uslikaj
    private void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            List<ResolveInfo> resolvedIntentActivities = getApplicationContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        "hr.rma.fileprovider",
                        photoFile);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private BaseTarget target2 = new BaseTarget<BitmapDrawable>() {
        //varijable za spremanje datuma
        String birthday = null, expireDate = null, dateOfIssue = null, OIB = null;
        String ime = "", prezime = null, spol = null, address = null;
        String documentNumber = null;
        Boolean side2Flag=false;
        @Override
        public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
            // do something with the bitmap
            if(bitmap != null) {
                //shareText.setText("");
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                Frame imageFrame = new Frame.Builder().setBitmap(bitmap.getBitmap()).build();
                String imageText = "";
                String fullText = "";
                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                DocumentRegex d = new DocumentRegex();
                d.generateDocumentData(textBlocks);
                if(d.getSide2Flag())
                {
                    dateOfIssue = d.getDateOfIssue();
                    OIB = d.getOIB();
                    address = d.getAddress();
                    oibEditText.setText(OIB);
                    addressEditText.setText(address);
                    dateOfIssueEditText.setText(dateOfIssue);
                    Log.d("side2", fullText + "***" + dateOfIssue + side2Flag + "**OIB: " + OIB + "** adresa:" + address);
                }else{
                    birthday = d.getBirthday();
                    expireDate = d.getExpireDate();
                    documentNumber = d.getDocumentNumber();
                    ime = d.getIme();
                    spol = d.getSpol();
                    nameEditText.setText(ime);
                    documentNumberEditText.setText(documentNumber);
                    birthdayEditText.setText(birthday);
                    expireDateEditText.setText(expireDate);
                    genderEditText.setText(spol);
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
        if (mediaScanIntent.resolveActivity(getApplication().getPackageManager()) != null) {
            sendBroadcast(mediaScanIntent);
            Log.d("pic", "Dobro");
        }else{
            // "Rucno" dodavanje u MediaStore:
            System.out.println("***** There is no app which would handle this intent. Updating MediaStore manually...");
            try {
                MediaStore.Images.Media.insertImage(
                        getApplication().getContentResolver(), String.valueOf(photoFile),
                        imageFileName, null);
                getApplication().sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        contentUri));
            } catch (FileNotFoundException e) {
                System.out.println("***** Error updating MediaStore manually...");
            }
        }

    }

    //check for camera permission
    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_MEMORY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_MEMORY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast storageEnable = Toast.makeText(getApplicationContext(), "Please enable storage", Toast.LENGTH_LONG);
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
