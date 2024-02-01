package com.example.smartocr.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    // Method to generate a test bitmap (replace this with your own test image generation logic)
    public static Bitmap generateTestBitmap() {
        // Example: Create a dummy bitmap (replace this with your actual image loading logic)
        int width = 100;
        int height = 100;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        return Bitmap.createBitmap(width, height, config);
    }

    // Method to save the image bitmap to a file
    public static String saveImageToFile(Bitmap imageBitmap) {
        // Get the Downloads directory
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Generate a unique file name with the format "smartocr_currenttimestamp.jpg"
        String fileName = "smartocr_" + System.currentTimeMillis() + ".jpg";

        // Create a file in the Downloads directory
        File imageFile = new File(downloadsDir, fileName);

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
