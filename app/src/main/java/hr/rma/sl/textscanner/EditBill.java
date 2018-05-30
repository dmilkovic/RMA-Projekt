package hr.rma.sl.textscanner;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
public class EditBill extends AppCompatActivity {
    private Intent intent;
    public ArrayList<ListItem> myItems = new ArrayList <ListItem>();
    private Bill bill;
    private int position;
    final int REQUEST_TAKE_PHOTO = 1;
    final int PICK_IMAGE_REQUEST = 2;
    //final int RESULT_OK = 0;
    public static final int EXTERNAL_MEMORY = 2;
    Uri photoURI = null;
    String imageFileName;
    File photoFile = null;
    private ListView myList;
    private TextView totalText;
    private MyAdapter myAdapter;
    private ImageButton addItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill);
        intent = getIntent();
        Bundle bundle = intent.getExtras();

        bill = (Bill) bundle.getSerializable("bill");
        position = intent.getIntExtra("pos", 0);

        myList = (ListView) findViewById(R.id.MyList);
        myList.setItemsCanFocus(true);
        myAdapter = new MyAdapter();
        myList.setAdapter(myAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d("tag","Evo Kite u Billa:" + bill.toString() + "**" + bill.getItems().toString() + bill.getItems().size());

        totalText = (TextView)findViewById(R.id.total);
        myAdapter.setTotal();
        final FloatingActionButton saveFab = (FloatingActionButton) findViewById(R.id.saveFab);
        FloatingActionButton cameraFab = (FloatingActionButton) findViewById(R.id.cameraFab);
        FloatingActionButton galleryFab = (FloatingActionButton) findViewById(R.id.galleryFab);
        saveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                bill = new Bill();
                List <BillItem> itemsList  = new ArrayList<BillItem>();
                for(int i = 0; i < myItems.size();i++)
                {
                    BillItem b = new BillItem();
                    b.setName(myItems.get(i).name);
                    b.setAmount(Double.parseDouble(myItems.get(i).amount));
                    b.setCost(Double.parseDouble(myItems.get(i).price));
                    itemsList.add(b);
                }
                bill.setItems(itemsList);
                bill.setTotal(totalText.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("bill", bill);
                intent.putExtras(bundle);
                intent.putExtra("pos", position);
                setResult(RESULT_OK, intent);
                finish();
                Log.d("tag", bill.toString());
            }
        });
        addItem = (ImageButton)findViewById(R.id.addItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                BillItem b = new BillItem("", 0 , 0);
                ListItem listItem = new ListItem();
                listItem.name = "";
                listItem.amount = "0.0";
                listItem.price = "0.0";
                myItems.add(listItem);
               // bill.addItem(new BillItem("", 0.0, 0.0));
              //  myAdapter.setTotal();
                myAdapter.notifyDataSetChanged();
            }
        });


       /* cameraFab.setOnClickListener(new View.OnClickListener() {
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
        });*/
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
               /* DocumentRegex d = new DocumentRegex();
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
                }*/
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

    //adapter za listu
    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public MyAdapter() {
            //ne znam zasto ali bez ovoga bi preskakao prvi element liste
           // ListItem listItem2 = new ListItem();
           // myItems.add(listItem2);
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < bill.getItems().size(); i++) {
                ListItem listItem = new ListItem();
                listItem.name = bill.getItems().get(i).getName();
                listItem.amount = String.valueOf(bill.getItems().get(i).getAmount());
                listItem.price = String.valueOf(bill.getItems().get(i).getCost());
                myItems.add(listItem);
             //   Log.d("broj", "i:" + i + bill.getItems().get(i).getName() + myItems.size());
            }
            notifyDataSetChanged();
        }

        public int getCount() {
            return myItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item, null);
                holder.name = (EditText) convertView
                        .findViewById(R.id.name);
                holder.price = (EditText) convertView
                        .findViewById(R.id.cost);
                holder.amount = (EditText) convertView
                        .findViewById(R.id.amount);
                holder.deleteItem = (ImageButton)convertView.findViewById(R.id.deleteIten);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Fill EditText with the value you have in data source
            holder.name.setText(myItems.get(position).name);
            holder.name.setId(position);
            holder.price.setText(myItems.get(position).price);
            holder.price.setId(position);
            holder.amount.setText(myItems.get(position).amount);
            holder.amount.setId(position);
            holder.deleteItem.setId(position);

            holder.deleteItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    if(myItems.size() > 1)
                    {
                        myItems.remove(position);
                        setTotal();
                        notifyDataSetChanged();
                    }else{
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(EditBill.this);
                        builder.setTitle("Brisanje računa")
                                .setMessage("Sigurno želite ukloniti ovaj račun?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("bill", null);
                                        intent.putExtras(bundle);
                                        intent.putExtra("pos", position);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                }).show();
                    }
                 }
            });

            //we need to update adapter once we finish with editing
            holder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        myItems.get(position).name = Caption.getText().toString();
                        setTotal();
                    }
                }
            });

          holder.price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        myItems.get(position).price = Caption.getText().toString();
                        setTotal();
                    }
                }
            });

            holder.amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        myItems.get(position).amount = Caption.getText().toString();
                        setTotal();
                    }
                }
            });
            return convertView;
        }

        private void setTotal(){
            double total = 0;
            for(int i = 0; i < myItems.size(); i++)
            {
                total += Double.parseDouble(myItems.get(i).amount) * Double.parseDouble(myItems.get(i).price);
            }
            totalText.setText(String.valueOf(total));
            Log.d("total", "poz" + total + bill.getItems().size());
        }
    }

    class ViewHolder {
        EditText name;
        EditText price;
        EditText amount;
        ImageButton deleteItem;
    }

    class ListItem {
        String name;
        String price;
        String amount;
    }
}