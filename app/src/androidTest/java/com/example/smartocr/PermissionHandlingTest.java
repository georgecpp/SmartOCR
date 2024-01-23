package com.example.smartocr;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.view.View;


@RunWith(AndroidJUnit4.class)
public class PermissionHandlingTest {

    @Before
    public void setUp() {
        ActivityScenario.launch(MainActivity.class);
    }

    @Test
    public void testPermissionDenied() throws UiObjectNotFoundException {
        // Revoke the READ_MEDIA_IMAGES permission
        revokeReadMediaImagesPermission();
        // Click the select image button -- now you will be prompted with Dialog for granting permission
        Espresso.onView(withId(R.id.selectImageButton)).perform(ViewActions.click());
        // Deny the permission for accessing media images.
        denyPermissionWhenAskedFor();
        // Check that no Image is therefore displayed in the view.
        Espresso.onView(withId(R.id.imageView)).check(ViewAssertions.matches(withNoDrawable()));
    }

    private void revokeReadMediaImagesPermission() {
        // Revoke the READ_MEDIA_IMAGES permission using instrumentation
        // This is specific to AndroidJUnitRunner and might require specific handling
        // (Note: This is a simplified example and may need adaptation based on actual app behavior)
        getInstrumentation().getUiAutomation()
                .revokeRuntimePermission(
                        getInstrumentation().getTargetContext().getPackageName(),
                        "android.permission.READ_MEDIA_IMAGES");
    }

    private void denyPermissionWhenAskedFor() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        UiObject denyPermission = device.findObject(new UiSelector()
                .clickable(true)
                .checkable(false)
                .index(2));
        if (denyPermission.exists()) {
            denyPermission.click();
        }
    }

    // Custom Espresso Matcher for asserting that an ImageView
    // does not have a drawable resource associated.
    private Matcher<View> withNoDrawable() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("No drawable resource");
            }

            @Override
            protected boolean matchesSafely(View item) {
                return item.getBackground() == null;
            }
        };
    }

}
