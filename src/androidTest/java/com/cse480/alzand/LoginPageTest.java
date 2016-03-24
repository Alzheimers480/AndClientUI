package com.cse480.alzand;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.cse480.alzand.LoginPageTest \
 * com.cse480.alzand.tests/android.test.InstrumentationTestRunner
 */
public class LoginPageTest extends ActivityInstrumentationTestCase2<LoginPage> {

    public LoginPageTest() {
        super("com.cse480.alzand", LoginPage.class);
    }

}
