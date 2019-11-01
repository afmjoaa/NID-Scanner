package io.joaa.myapplication.activities.splash;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.ReaderException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.joaa.myapplication.R;
import io.joaa.myapplication.util.ScannerUtils;

public class SplashActivity extends AppCompatActivity {

    private Button getImageBtn;
    private int IMAGE_RESULT_CODE = 3333;
    private Uri outputFileUri = null;
    private Uri selectedImageUri = null;
    private TextView infoTextView;
    private String infoText;
    private String sortedInfo = "";
    private static final String TAG = "joaa";
    private ImageView showImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getImageBtn = findViewById(R.id.get_image_btn);
        infoTextView = findViewById(R.id.info_text);
        showImage = findViewById(R.id.show_bitmap);
        getImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageIntent();
            }
        });
    }

    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "nid.jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, IMAGE_RESULT_CODE);
    }


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_RESULT_CODE) {
                infoText = null;
                sortedInfo = "";
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data.getData();
                }
                if (selectedImageUri != null) {
                    Toast.makeText(this, "got the image", Toast.LENGTH_LONG).show();
                    try {
                        Bitmap mainBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        int nh = (int) (mainBitmap.getHeight() * (512.0 / mainBitmap.getWidth()));
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(mainBitmap, 512, nh, true);
                        showImage.setImageBitmap(scaledBitmap);

                        try {
                            infoText = ScannerUtils.processImage(mainBitmap);
                            if (infoText == null) {
                                infoText = "Not a NID barcode";
                            } else {
                                infoText = infoText.substring(4, infoText.length());
                                int tempStart = infoText.indexOf("NW");
                                int tempStop = infoText.indexOf("OL");
                                sortedInfo = "Name : " + infoText.substring(2,tempStart);
                                sortedInfo = sortedInfo + "\nNID No(New) : " + infoText.substring(tempStart+2 , tempStop);

                                tempStart = infoText.indexOf("OL");
                                tempStop = infoText.indexOf("BR");
                                sortedInfo = sortedInfo + "\nNID No(Old) : " + infoText.substring(tempStart+2 , tempStop);

                                tempStart = infoText.indexOf("BR");
                                tempStop = infoText.indexOf("PE");
                                sortedInfo = sortedInfo + "\nBirth Date : " + infoText.substring(tempStart+2 , tempStart+6) + "/" +
                                        infoText.substring( tempStart+6 , tempStart+8) + "/" +infoText.substring(tempStart+8 , tempStop);


                                tempStart = infoText.indexOf("PE");
                                tempStop = infoText.indexOf("PR");
                                sortedInfo = sortedInfo + "\nPresent Address : " + infoText.substring(tempStart+2 , tempStop);

                                tempStart = infoText.indexOf("PR");
                                tempStop = infoText.indexOf("VA");
                                sortedInfo = sortedInfo + "\nPermanent Address : " + infoText.substring(tempStart+2 , tempStop);

                                tempStart = infoText.indexOf("VA");
                                tempStop = infoText.indexOf("DT");
                                sortedInfo = sortedInfo + "\nVoting Area : " + infoText.substring(tempStart+2 , tempStop);

                                tempStart = infoText.indexOf("DT");
                                tempStop = infoText.indexOf("PK");
                                sortedInfo = sortedInfo + "\nIssue Date : " +  infoText.substring(tempStart+2 , tempStart+6) + "/" +
                                        infoText.substring( tempStart+6 , tempStart+8) + "/" +infoText.substring(tempStart+8 , tempStop);

                                tempStart = infoText.indexOf("PK");
                                tempStop = infoText.indexOf("SG");
                                sortedInfo = sortedInfo + "\nPK(Unknown) : " + infoText.substring(tempStart+2 , tempStop);

                                tempStart = infoText.indexOf("SG");
                                tempStop = infoText.length();
                                sortedInfo = sortedInfo + "\nSignature : " + infoText.substring(tempStart+2 , tempStop);

                            }


                            infoTextView.setText(sortedInfo);
                        } catch (ReaderException e) {
                            e.printStackTrace();
                            if (infoText == null) {
                                infoText = "Not a NID barcode";
                            }
                            infoTextView.setText(infoText);
                        }


                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(SplashActivity.this, "null found", Toast.LENGTH_LONG).show();
                }

                //Glide.with(this).load(selectedImageUri).apply(new RequestOptions().override(100, 100)).into(profileUserDP);
            }
        }
        //super.onActivityResult(requestCode, resultCode, data);
    }
}
