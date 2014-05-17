/*
 * A trivial test. Used to test if I can run a test.
 */

package com.dhgg.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.dhgg.appusagemonitor.MyFirstActivity;

public class TrivialTest extends ActivityInstrumentationTestCase2<MyFirstActivity> {
    public TrivialTest() {
        super("com.dhgg.appusagemonitor", MyFirstActivity.class);
    }

    public void test() throws Exception {
        final int expected = 123;
        final int reality = 123;
        assertEquals(expected, reality);
    }
}