package hr.rma.sl.textscanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class MainActivity extends AppCompatActivity {
    //Bitmap bitmap;
    final int REQUEST_TAKE_PHOTO = 1;
    //final int RESULT_OK = 0;
    TextView myText;
    Uri photoURI = null;
    File photoFile = null;
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton camButton = findViewById(R.id.camera_button);
        myText = (TextView) findViewById(R.id.text_view);
        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sdf);

        camButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go

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
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        setResult(RESULT_OK, takePictureIntent);
                        //Bitmap bitmap = null;
                            /* final InputStream imageStream;
                             try {
                                 imageStream = getContentResolver().openInputStream(photoURI);
                                 if(imageStream != null){
                                     bitmap = BitmapFactory.decodeStream(imageStream);
                                 }
                             } catch (FileNotFoundException e) {
                                 e.printStackTrace();
                             }
                             //try {
                                 //ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(MainActivity.this).addStream(sharedFileUri);
                                 //Intent chooserIntent = intentBuilder.createChooserIntent();
                                 //startActivity(chooserIntent);
                                 //bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), photoURI);
                                 //File imgFile = new  File("storage/emulated/0/Android/data/Pictures/JPEG_20180322_175224_457744141.jpg");
                                 //bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                                 Log.d("tag" ,"Bitmap" + photoURI + "abs" + photoFile.getAbsolutePath() + "" +photoFile.getPath());
                             /*}catch (IOException e) {
                                 e.printStackTrace();
                             }*/
                             /*if(bitmap != null) {
                                 TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                                 Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
                                 String imageText = "";
                                 SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                                 Log.d("tag" ,"Bitmapfsd" + bitmap.toString()+ " " +textBlocks);
                                 for (int i = 0; i < textBlocks.size(); i++) {
                                     TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                                     imageText = textBlock.getValue();                   // return string
                                     myText.setText(imageText);
                                     Log.d("tag", "Ovo je" + imageText);
                                 }
                             }*/
                    }
                }

            }
        });
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        @Override
        public void onResourceReady(BitmapDrawable bitmap, Transition<? super BitmapDrawable> transition) {
            // do something with the bitmap
            // for demonstration purposes, let's set it to an imageview
            if(bitmap != null) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                Frame imageFrame = new Frame.Builder().setBitmap(bitmap.getBitmap()).build();
                String imageText = "";
                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                Log.d("tag" ,"Bitmapfsd" + bitmap.toString()+ " " +textBlocks);
                for (int i = 0; i < textBlocks.size(); i++) {
                    TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                    imageText = textBlock.getValue();                   // return string
                    myText.setText(imageText);
                    Log.d("tag", "Ovo je" + imageText);
                }
            }
        }
        @Override
        public void getSize(SizeReadyCallback cb) {
            cb.onSizeReady(1024, 720);
        }
        @Override
        public void removeCallback(SizeReadyCallback cb) {}
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag", "dfg1");
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.d("tag", "dfg2");
            Uri noviURI = Uri.fromFile(photoFile);
            Bitmap bitmap = null;


            Glide.with(getApplicationContext()).load(noviURI).into(target2);
            //OVO RADI!!!!!!!!!!!!!!!
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), noviURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            if(bitmap != null) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                Frame imageFrame = new Frame.Builder().setBitmap(bitmap).build();
                String imageText = "";
                SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);
                Log.d("tag" ,"Bitmapfsd" + bitmap.toString()+ " " +textBlocks);
                for (int i = 0; i < textBlocks.size(); i++) {
                    TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
                    imageText = textBlock.getValue();                   // return string
                    myText.setText(imageText);
                    Log.d("tag", "Ovo je" + imageText);
                }
            }*/
            Log.d("tag", noviURI.toString()+" gs "+ bitmap.toString());
        }
    }

    private void galleryAddPic() throws IOException {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}