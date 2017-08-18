package com.glit.communication_android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;
import android.widget.TextView;

import org.junit.Rule;
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
    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.glit.communication_android", appContext.getPackageName());
    }

    @Test
    public void testPostMultipleFilesTask() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        MainActivity mainActivity = mainActivityActivityTestRule.getActivity();
        Button buttonPostFiles = (Button)mainActivity.findViewById(R.id.button_post_files);
        buttonPostFiles.performClick();
        TextView textResult = (TextView)mainActivity.findViewById(R.id.text_result);
        assertEquals("success", textResult.getText());
    }
}
