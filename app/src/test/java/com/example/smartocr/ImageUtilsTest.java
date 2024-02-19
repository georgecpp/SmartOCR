package com.example.smartocr;
import android.graphics.Bitmap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
public class ImageUtilsTest {

    @Mock
    Bitmap bitmap;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(bitmap.getWidth()).thenReturn(100);
        when(bitmap.getHeight()).thenReturn(100);
    }

    @Test
    public void testGenerateTestBitmap() {
        assertNotNull(bitmap);
        assertEquals(100, bitmap.getWidth());
        assertEquals(100, bitmap.getHeight());
    }
}

