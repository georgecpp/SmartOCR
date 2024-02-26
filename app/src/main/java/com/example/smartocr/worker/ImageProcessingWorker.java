package com.example.smartocr.worker;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.smartocr.jni.SmartOCR_JNI;

public class ImageProcessingWorker extends Worker {

    public static final String ACTION_PROCESSING_RESULT = "com.example.smartocr.ACTION_PROCESSING_RESULT";
    public static final String EXTRA_RESULT = "result";

    public static final String EXTRA_IMAGE_PATH = "imagePath";

    public ImageProcessingWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get the file path from input data
        String imagePath = getInputData().getString("imagePath");

        // Check if imagePath is null
        if (imagePath == null) {
            // Log an error or handle the situation as appropriate
            return Result.failure();
        }

        // Perform image processing using JNI
        boolean sauvolaThresholdApplied = SmartOCR_JNI.applySauvolaThreshold(imagePath);

        // Send broadcast with the processing result and imagePath
        sendResultBroadcast(sauvolaThresholdApplied, imagePath);

        // Return success or failure based on processing result
        return sauvolaThresholdApplied ? Result.success() : Result.failure();
    }

    public void sendResultBroadcast(boolean result, String imagePath) {
        Intent intent = new Intent(ACTION_PROCESSING_RESULT);
        intent.putExtra(EXTRA_RESULT, result);
        intent.putExtra(EXTRA_IMAGE_PATH, imagePath); // Include the imagePath in the broadcast
        getApplicationContext().sendBroadcast(intent);
    }
}
