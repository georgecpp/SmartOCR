#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_smartocr_jni_SmartOCR_1JNI_applySauvolaThreshold(JNIEnv *env, jclass _this, jstring imagePath) {
    // Convert jstring to const char*
    const char *path = env->GetStringUTFChars(imagePath, nullptr);

    // Load the image from the file
    Mat img = imread(path, IMREAD_GRAYSCALE);

    // Check if the image is loaded successfully
    if (img.empty()) {
        // Release the allocated memory for the path string
        env->ReleaseStringUTFChars(imagePath, path);
        return JNI_FALSE;
    }

    // Apply Sauvola thresholding algorithm
    double k = 0.3;
    int windowSize = 25;
    adaptiveThreshold(img, img, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, windowSize, k);

    // Save the processed image to the same file
    bool success = imwrite(path, img);

    // Release the allocated memory for the path string
    env->ReleaseStringUTFChars(imagePath, path);

    // Return true if the image was processed and saved successfully, otherwise false
    return success ? JNI_TRUE : JNI_FALSE;
}

