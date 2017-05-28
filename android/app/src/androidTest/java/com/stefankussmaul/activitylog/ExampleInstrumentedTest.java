package com.stefankussmaul.activitylog;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    // todo: test database
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        manager.insertEntry(new LogEntry("TestInsert", System.currentTimeMillis(), 3_600_000));
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        long id = manager.insertEntry(new LogEntry("TestModify", System.currentTimeMillis(), 7_200_000));
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        manager.updateEntry(id, new LogEntry("TestModify2", System.currentTimeMillis(), 0));
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        manager.deleteEntry(id);
//        Log.d("MainActivity", DBUtil.dbToString(manager));
//        Log.d("MainActivity", (new LogEntry("Hi", System.currentTimeMillis(), 1000)).toString());

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.stefankussmaul.activitylog", appContext.getPackageName());
    }
}
