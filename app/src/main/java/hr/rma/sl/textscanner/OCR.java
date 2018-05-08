package hr.rma.sl.textscanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.EditText;
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

import static android.app.Activity.RESULT_OK;

public class OCR extends AppCompatActivity{
    /*
    Activity currentActivity;
    final int REQUEST_TAKE_PHOTO = 1;
    final int PICK_IMAGE_REQUEST = 2;
    //final int RESULT_OK = 0;
    public static boolean storage_flag;
    public static final int EXTERNAL_MEMORY = 2;
    private String scanResult;
    Uri photoURI = null;
    String imageFileName;
    File photoFile = null;
    public static final String EXTRA_MESSAGE = "hr.rma.textscanner.MESSAGE";

    public OCR(Activity current){
        this.currentActivity = current;
    }

    public String scanCameraImage(){
        takePicture();
        return scanResult;
    }

    public String scanGalleryImage(){
        chooseFromGallery();
        return scanResult;
    }

    //upali kameru i uslikaj
    protected void takePicture()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(currentActivity.getPackageManager()) != null) {
            // Create the File where the photo should go
            List<ResolveInfo> resolvedIntentActivities = currentActivity.getApplicationContext().getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(currentActivity.getApplicationContext(),
                        "hr.rma.fileprovider",
                        photoFile);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    currentActivity.grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                setResult(RESULT_OK, takePictureIntent);
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
        File storageDir = currentActivity.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private BaseTarget target2 = new BaseTarget<BitmapDrawable>() {
        @Override
        public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
            // do something with the bitmap
            if(bitmap != null) {
                //shareText.setText("");
                TextRecognizer textRecognizer = new TextRecognizer.Builder(currentActivity.getApplicationContext()).build();
                Frame imageFrame = new Frame.Builder().setBitmap(bitmap.getBitmap()).build();
                String imageText = "";
                String fullText = "";
                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                Log.d("tag" ,"Bitmapfsd" + bitmap.toString()+ " " +textBlocks);
                for (int i = 0; i < textBlocks.size(); i++) {
                    TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                    imageText = textBlock.getValue();                   // return string
                    // myText.append(imageText);
                    fullText += imageText;
                    Log.d("tag", "Ovo je" + imageText);
                }
              //  shareText.append(fullText);
                //Trebat ce ti kasnije!!!!!****
                 Intent intent = new Intent(currentActivity, FragmentShareText.class);
                 intent.putExtra(EXTRA_MESSAGE, fullText);
                 startActivity(intent);
                 scanResult = fullText;
            }
        }
        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(1024, 720);
        }
        @Override
        public void removeCallback(SizeReadyCallback cb) {}
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag", "dfg1");
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                galleryAddPic();
                Log.d("pic", "Dobro");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("pic", "Nije dobro");
            }
            Log.d("tag", "dfg2" + mCurrentPhotoPath);
            Uri noviURI = Uri.fromFile(photoFile);
            Bitmap bitmap = null;
            Glide.with(currentActivity.getApplicationContext()).load(noviURI).into(target2);
        }else if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri uri = data.getData();
            Glide.with(currentActivity.getApplicationContext()).load(uri).into(target2);
        }`
    }

    private void galleryAddPic() throws IOException {
        Uri contentUri = Uri.fromFile(photoFile);
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        if (mediaScanIntent.resolveActivity(currentActivity.getApplication().getPackageManager()) != null) {
            currentActivity.sendBroadcast(mediaScanIntent);
            Log.d("pic", "Dobro");
        }else{
            // "Rucno" dodavanje u MediaStore:
            System.out.println("***** There is no app which would handle this intent. Updating MediaStore manually...");
            try {
                MediaStore.Images.Media.insertImage(
                        currentActivity.getApplication().getContentResolver(), String.valueOf(photoFile),
                        imageFileName, null);
                currentActivity.getApplication().sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        contentUri));
            } catch (FileNotFoundException e) {
                System.out.println("***** Error updating MediaStore manually...");
            }
        }

    }

    //check for camera permission
    protected void requestStoragePermission() {
        ActivityCompat.requestPermissions(currentActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_MEMORY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_MEMORY: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast storageEnable = Toast.makeText(currentActivity.getApplicationContext(), "Please enable storage", Toast.LENGTH_LONG);
                    storageEnable.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
    // end of check permission
    */
}
