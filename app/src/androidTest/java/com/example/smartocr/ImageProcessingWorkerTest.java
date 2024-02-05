package com.example.smartocr;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.smartocr.util.ImageUtils;
import com.example.smartocr.worker.ImageProcessingWorker;

@RunWith(AndroidJUnit4.class)
public class ImageProcessingWorkerTest {

    @Test
    public void testDoWork_Success() {
        // Generate a test image bitmap
        Bitmap testBitmap = ImageUtils.generateTestBitmap();

        // Save the test image to a file
        String imagePath = ImageUtils.saveImageToFile(testBitmap);

        // Create input data with the image path
        Data inputData = new Data.Builder().putString("imagePath", imagePath).build();

        // Use TestListenableWorkerBuilder to create the worker
        ImageProcessingWorker worker = TestListenableWorkerBuilder.from(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                ImageProcessingWorker.class
        ).setInputData(inputData).build();

        // Start the worker synchronously
        ListenableWorker.Result result = worker.doWork();

        // Assert that the result is success
        assertEquals(ListenableWorker.Result.success(), result);
    }

    @Test
    public void testDoWork_Failure() {
        // Create input data with an invalid image path
        Data inputData = new Data.Builder().putString("imagePath", "invalid_path").build();

        // Use TestListenableWorkerBuilder to create the worker
        ImageProcessingWorker worker = TestListenableWorkerBuilder.from(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                ImageProcessingWorker.class
        ).setInputData(inputData).build();

        // Start the worker synchronously
        ListenableWorker.Result result = worker.doWork();

        // Assert that the result is failure
        assertNotEquals(ListenableWorker.Result.success(), result);
    }

    @Test
    public void testDoWork_EmptyInputData() {
        // Create empty input data
        Data inputData = new Data.Builder().build();

        // Use TestListenableWorkerBuilder to create the worker
        ImageProcessingWorker worker = TestListenableWorkerBuilder.from(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                ImageProcessingWorker.class
        ).setInputData(inputData).build();

        // Start the worker synchronously
        ListenableWorker.Result result = worker.doWork();

        // Assert that the result is failure because input data is required
        assertEquals(ListenableWorker.Result.failure(), result);
    }

    @Test
    public void testDoWork_NoInputData() {
        // Do not provide any input data

        // Use TestListenableWorkerBuilder to create the worker
        ImageProcessingWorker worker = TestListenableWorkerBuilder.from(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                ImageProcessingWorker.class
        ).build();

        // Start the worker synchronously
        ListenableWorker.Result result = worker.doWork();

        // Assert that the result is failure because input data is required
        assertEquals(ListenableWorker.Result.failure(), result);
    }

}
