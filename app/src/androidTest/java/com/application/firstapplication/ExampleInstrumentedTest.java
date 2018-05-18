package com.application.firstapplication;

import android.content.Context;
import android.graphics.Point;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;
/*import com.android.UIAutomator.core.UiObject;
import com.android.UIAutomator.core.UiObjectNotFoundException;
import com.android.UIAutomator.core.UiScrollable;
import com.android.UIAutomator.core.UiSelector;
import com.android.UIAutomator.testrunner.UiAutomatorTestCase;*/

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private UiDevice device;

    @Before
    public void init() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    }

    public void unlockBySwipe() throws Exception {
        int x1 = device.getDisplayWidth() / 2;
        int y1 = device.getDisplayHeight() / 2;
        int x2 = device.getDisplayWidth() / 3;
        int y2 = device.getDisplayHeight() / 3;
        device.swipe(x1, y1, x2, y2, 10);
        device.waitForIdle();

        Point[] coordinates = new Point[9];
        coordinates[0] = new Point(190, 766);//1
        coordinates[1] = new Point(358, 766);//2
        coordinates[2] = new Point(530, 766);//3

        coordinates[3] = new Point(360, 940);//5
        coordinates[4] = new Point(190, 940);//4

        coordinates[5] = new Point(190, 1100);//7
        coordinates[6] = new Point(360, 1100);//8
        coordinates[7] = new Point(530, 1100);//9

        coordinates[8] = new Point(530, 940);//6

        device.wakeUp();
        device.swipe(coordinates, 5);
    }

    @Test
    public void openApp() throws Exception {
        //1. unlock
        unlockBySwipe();
        device.pressHome();
        UiObject programButton = device.findObject(new UiSelector().text("Програми"));
        //2. Open programs list
        programButton.clickAndWaitForNewWindow();
        UiObject programIcon = device.findObject(new UiSelector()
                .text("PC volume changer"));
        //3. Click on program button and wait new window
        programIcon.clickAndWaitForNewWindow();
        Log.i("openApp", "Launcher package name: " + device.getLauncherPackageName());
        Log.i("openApp", device.getProductName());
        device.takeScreenshot(new File("/storage/extSdCard/my_app/app_screen.jpg"));
    }
}
