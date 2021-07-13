package com.webserva.wings.android.pl2_spread;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ResultMapTest extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.webserva.wings.android.pl2_spread", appContext.getPackageName());
    }

    @Test
    public void receiveMessage() {
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void onMapReady() {
    }

    @Test
    public void calcAngle() {
    }

    @Test
    public void calcDist() {
    }
}