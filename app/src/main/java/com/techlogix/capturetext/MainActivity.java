package com.techlogix.capturetext;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudText;
import com.google.firebase.ml.vision.cloud.text.FirebaseVisionCloudTextDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_CODE = 123;
    public static final int PICK_IMAGE_REQUEST = 2;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static final String TAG = "Rotation Value Error";
    private static final String TAG2 = "Image Creation Error";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    ImageView capturedImage;
    String mCurrentPhotoPath;
    Uri savedPhotoUri;
    Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        capturedImage = (ImageView) findViewById(R.id.capturedImage);
    }

    @TargetApi(27)
    public void onClickScan(View view) {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)
            dispatchTakePictureIntent();
        else {
            String[] permissionRequest = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_NETWORK_STATE};
            requestPermissions(permissionRequest, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                dispatchTakePictureIntent();
            else
                Toast.makeText(this, "Required Permissions Not Granted", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickDetect(View v) {
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(MainActivity.this);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(selectedBitmap);

        // Progress dialog pops up here
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_progress);
        dialog.show();

        if (isNetworkAvailable()) {
            // NETWORK AVAILABLE ------- ONLINE DETECTOR

            Toast.makeText(this, "Network Available.", Toast.LENGTH_SHORT).show();
            FirebaseVisionCloudTextDetector detector = FirebaseVision.getInstance().getVisionCloudTextDetector();
            Task<FirebaseVisionCloudText> result = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionCloudText>() {
                        @Override
                        public void onSuccess(FirebaseVisionCloudText firebaseVisionCloudText) {
                            Intent intent = new Intent(getApplicationContext(), DetectedTextActivity.class);
                            String text = firebaseVisionCloudText.getText();
                            Log.d("Detected Text: ", text);

                            double avgBlockConfidence = 0.0;
                            int numBlocks = 0;
                            List<FirebaseVisionCloudText.DetectedLanguage> detectedLanguages = new ArrayList<>();

                            for (FirebaseVisionCloudText.Page page : firebaseVisionCloudText.getPages()) {
                                List<FirebaseVisionCloudText.DetectedLanguage> languages = page.getTextProperty().getDetectedLanguages();
                                int height = page.getHeight();
                                int width = page.getWidth();
                                float confidence = page.getConfidence();
                                Log.d("Page Confidence", "Page Confidence: " + confidence);

                                for (FirebaseVisionCloudText.Block block : page.getBlocks()) {
                                    numBlocks++;
                                    Rect boundingBox = block.getBoundingBox();

                                    List<FirebaseVisionCloudText.DetectedLanguage> blockLanguages = block.getTextProperty().getDetectedLanguages();
                                    float blockConfidence = block.getConfidence();
                                    avgBlockConfidence += blockConfidence;
                                    Log.d("Block Confidence", "Block Confidence: " + blockConfidence);

                                    detectedLanguages.addAll(blockLanguages);
                                    // And so on: Paragraph, Word, Symbol

                                }
                            }

                            avgBlockConfidence /= numBlocks;
                            String confidenceText;
                            if (avgBlockConfidence <= 0.5)
                                confidenceText = avgBlockConfidence + " --- Low Confidence";
                            else if (avgBlockConfidence > 0.5 && avgBlockConfidence < 0.8)
                                confidenceText = avgBlockConfidence + " --- Moderate Confidence";
                            else
                                confidenceText = avgBlockConfidence + " --- High Confidence";

                            String languagesText = "";
                            for (FirebaseVisionCloudText.DetectedLanguage lang : detectedLanguages)
                                languagesText = languagesText.concat(lang.getLanguageCode() + ", ");

                            intent.putExtra("TranslatedText", text);
                            intent.putExtra("ConfidenceText", confidenceText);
                            intent.putExtra("LanguagesText", languagesText);
                            dialog.dismiss();
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Text Detection Failed", Toast.LENGTH_SHORT).show();
                            Log.d("Text Detection Error", "Failed to detect text from image");
                        }
                    });
        } else {
            // NETWORK UNAVAILABLE ------- OFFLINE DETECTOR

            Toast.makeText(this, "Network Unavailable", Toast.LENGTH_SHORT).show();
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance(firebaseApp).getVisionTextDetector();
            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    Intent intent = new Intent(getApplicationContext(), DetectedTextActivity.class);
                    List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
                    String text = "";
                    for (FirebaseVisionText.Block block : blockList) {
                        Log.d("Detected Text: ", block.getText());
                        text = text.concat(block.getText() + "\n");
                    }
                    intent.putExtra("TranslatedText", text);
                    dialog.dismiss();
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Text Detection Failed", Toast.LENGTH_SHORT).show();
                    Log.d("Text Detection Error", "Failed to detect text from image");
                }
            });
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG2, "Error Creating Image File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.techlogix.capturetext.fileprovider", photoFile);
                savedPhotoUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                capturedImage.setImageBitmap(imageBitmap);
            } else {
                Log.d("Null Data", "Null Data in Intent");
                try {
                    setImageView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                capturedImage.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void onClickGallery(View view) {
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void setImageView() throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bounds);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(mCurrentPhotoPath, opts);
        ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        selectedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

        capturedImage.setImageBitmap(selectedBitmap);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void onClickCrash(View view) {
        Crashlytics.getInstance().crash();
    }
}
