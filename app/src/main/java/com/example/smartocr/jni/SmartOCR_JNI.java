package com.example.smartocr.jni;

public class SmartOCR_JNI {

    // Load native library
    static {
        System.loadLibrary("smartocr");
    }

    // native method for applying Sauvola threshold
    public static native boolean applySauvolaThreshold(String imagePath);

}

