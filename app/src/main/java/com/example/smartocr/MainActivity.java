package com.example.smartocr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.smartocr.util.ImageUtils;
import com.example.smartocr.worker.ImageProcessingWorker;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private ImageView imageView;

    private BroadcastReceiver processingResultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getBooleanExtra(ImageProcessingWorker.EXTRA_RESULT, false);
            if (result) {
                // Processing succeeded, update image in ImageView
                String imagePath = intent.getStringExtra("imagePath");
                Bitmap processedBitmap = BitmapFactory.decodeFile(imagePath);
                imageView.setImageBitmap(processedBitmap);
            } else {
                // Processing failed, show toast
                Toast.makeText(MainActivity.this, "Image Processing failed...", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        Button selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter(ImageProcessingWorker.ACTION_PROCESSING_RESULT);
        registerReceiver(processingResultReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver
        unregisterReceiver(processingResultReceiver);
    }

    private void openGallery() {
        // Check if the app has the required permission
        if ((checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, open the gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        } else {
            // Permission not granted, request it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            } else {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the gallery
                openGallery();
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
                Log.e("Permission", "READ_MEDIA_IMAGES permission denied");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                // Load the selected image using Android SDK
                Uri imageUri = data.getData();
                try {
                    Bitmap selectedBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                    // Save the selected image to a file
                    String imagePath = ImageUtils.saveImageToFile(selectedBitmap);

                    // Pass the file path to the worker for processing
                    processImageInBackground(imagePath);

                    // Display the selected image in the ImageView
                    imageView.setImageBitmap(selectedBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processImageInBackground(String imagePath) {
        // Create a Data object with the file path
        Data inputData = new Data.Builder()
                .putString("imagePath", imagePath)
                .build();

        // Create a OneTimeWorkRequest for ImageProcessingWorker
        OneTimeWorkRequest imageProcessingRequest =
                new OneTimeWorkRequest.Builder(ImageProcessingWorker.class)
                        .setInputData(inputData)
                        .build();

        // Enqueue the work request
        WorkManager.getInstance(this).enqueue(imageProcessingRequest);
    }
}
