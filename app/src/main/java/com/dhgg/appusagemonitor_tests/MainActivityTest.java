/*
 * A trivial test. Used to test if I can run a test.
 */

package com.dhgg.appusagemonitor_tests;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.util.Log;


import com.dhgg.appusagemonitor.MainActivity;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {

    private Intent m_LaunchIntent;
    private MainActivity m_activity;
    private String m_logTag = "DHGG::MainActivityTest";


    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        Log.d(m_logTag, "setUp");
        super.setUp();

        m_LaunchIntent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
        m_LaunchIntent.putExtra("startAdmob",false);
        //setActivityInitialTouchMode(false);
        //m_activity = getActivity();
    }

    public void testPreconditions() {
        startActivity(m_LaunchIntent, null, null);
        m_activity = getActivity();
        assertNotNull("m_activity is null", m_activity);

        //final Button launchNextButton = (Button) getActivity().findViewById(R.id.launch_next_activity_button);
        //assertNotNull("mLaunchNextButton is null", launchNextButton);
    }

    public void testLaunchNextActivityButton_labelText() {
        startActivity(m_LaunchIntent, null, null);

        /*
        // TODO: checkout layout of our fragments in different environments.
        //

        int list_fragment_id = R.id.list_fragment_container;
        FrameLayout list_layout = (FrameLayout) m_activity.findViewById( list_fragment_id );
        // list_layout.getLayoutParams();
        */

        assertEquals("Not doing this test. Just leave for in to read more about later.",
                     "hello", "hello");
    }

    public void testNextActivityWasLaunchedWithIntent() {
        startActivity(m_LaunchIntent, null, null);

        /*
        // TODO: click main menu item to share data and check that we open the correct intent.
        //

        final Button launchNextButton = (Button) getActivity().findViewById(R.id.launch_next_activity_button);
        //Because this is an isolated ActivityUnitTestCase we have to directly click the
        //button from code
        launchNextButton.performClick();

        // Get the intent for the next started activity
        final Intent launchIntent = getStartedActivityIntent();
        //Verify the intent was not null.
        assertNotNull("Intent was null", launchIntent);
        //Verify that LaunchActivity was finished after button click
        assertTrue(isFinishCalled());


        final String payload = launchIntent.getStringExtra(NextActivity.EXTRAS_PAYLOAD_KEY);
        //Verify that payload data was added to the intent
        assertEquals("Payload is empty", LaunchActivity.STRING_PAYLOAD
                , payload);
        */
    }
}