package com.dhgg.appusagemonitor_tests;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.dhgg.appusagemonitor.DbHandler;

/**
 * Created by dhan on 7/4/14.
 */
public class DbHandlerTest extends AndroidTestCase {
    private DbHandler m_db_handler;

    @Override
    public void setUp() {
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        m_db_handler = new DbHandler(context);
    }

    @Override
    public void tearDown() throws Exception {
        m_db_handler.close();
        super.tearDown();
    }

    public void testDoStuff() {
        // test db. not connected to standard db.

        String[] dbList = mContext.databaseList();
        int numDb = dbList.length;
        for (int i = 0; i < numDb; i++) {
            Log.d("DHGG", dbList[i]);
        }
    }
}
