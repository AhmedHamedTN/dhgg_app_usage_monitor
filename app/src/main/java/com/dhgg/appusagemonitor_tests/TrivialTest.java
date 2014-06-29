/*
 * A trivial test. Used to test if I can run a test.
 */

package com.dhgg.appusagemonitor_tests;

import android.test.ActivityInstrumentationTestCase2;

import com.dhgg.appusagemonitor.MainActivity;

public class TrivialTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public TrivialTest() {
        super("com.dhgg.appusagemonitor", MainActivity.class);
    }

    public void test() throws Exception {
        final int expected = 123;
        final int reality = 123;
        assertEquals(expected, reality);
    }
}