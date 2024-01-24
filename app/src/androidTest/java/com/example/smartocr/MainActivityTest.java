package com.example.smartocr;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
        Intents.init(); // Initialize Espresso Intents
    }

    @After
    public void tearDown() {
        Intents.release(); // Release Espresso Intents
    }

    @Test
    public void testSelectImageButtonClick_opensGallery() {
        // Grant the READ_MEDIA_IMAGES permission
        grantReadMediaImagesPermission();

        // Click the select image button
        onView(withId(R.id.selectImageButton)).perform(click());

        // Verify that the gallery Intent is launched
        intended(hasAction(Intent.ACTION_PICK));
        intended(hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    @Test
    public void testImageSelected_displaysInImageView() throws Exception {
        // Grant the READ_MEDIA_IMAGES permission
        grantReadMediaImagesPermission();

        // Simulate selecting the first image from the gallery
        simulateSelectPictureFromCameraRoll();

        // Verify that the selected image is displayed in the ImageView
        onView(withId(R.id.imageView)).check(ViewAssertions.matches(withDrawable()));
    }

    public static void simulateSelectPictureFromCameraRoll() throws  Exception {
        Exception returnException = null;
        Intent resultData = new Intent();

        int drawableResourceId = R.drawable.ic_launcher_background;

        Uri imageUri = Uri.parse("android.resource://" + getInstrumentation().getTargetContext().getPackageName() + "/" + drawableResourceId);
        resultData.setData(imageUri);

        try {
            Matcher<Intent> expectedIntent = hasAction(Intent.ACTION_PICK);
            intending(expectedIntent).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));
            onView(withId(R.id.selectImageButton)).perform(click());
            intended(expectedIntent);
        }
        catch (Exception e) {
            returnException = e;
        }

        if (returnException != null) {
            throw returnException;
        }
    }

    private void grantReadMediaImagesPermission() {
        // Grant the READ_MEDIA_IMAGES permission using instrumentation
        // This is specific to AndroidJUnitRunner and might require specific handling
        // (Note: This is a simplified example and may need adaptation based on actual app behavior)
        getInstrumentation().getUiAutomation()
                .grantRuntimePermission(
                        getInstrumentation().getTargetContext().getPackageName(),
                        "android.permission.READ_MEDIA_IMAGES");
    }

    // Custom Espresso Matcher for asserting that an ImageView has a drawable resource associated.
    private Matcher<View> withDrawable() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Has drawable resource");
            }

            @Override
            protected boolean matchesSafely(View item) {
                if (item instanceof ImageView) {
                    ImageView imageView = (ImageView) item;
                    return imageView.getDrawable() != null || imageView.getBackground() != null;
                }
                return false;
            }
        };
    }
}
