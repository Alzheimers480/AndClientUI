package com.cse480.alzand;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.test.suitebuilder.annotation.*;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginPageTest {

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<LoginPage> mActivityRule = new ActivityTestRule<LoginPage>(
            LoginPage.class);

    // @Before
    // public void initValidString() {
    //     // Specify a valid string.
    //     mStringToBetyped = "Espresso";
    // }

    @Test
    public void changeText_sameActivity() {
	onView(withId(R.id.etUsername))
	    .perform(typeText("switch202"), closeSoftKeyboard());
	onView(withId(R.id.etPassword))
	    .perform(typeText("password"), closeSoftKeyboard());
	onView(withId(R.id.bLogin))
	    .perform(click());
        // // Type text and then press the button.
        // onView(withId(R.id.editTextUserInput))
        //         .perform(typeText(mStringToBetyped), closeSoftKeyboard());
        // onView(withId(R.id.changeTextBt)).perform(click());

        // // Check that the text was changed.
        // onView(withId(R.id.textToBeChanged))
        //         .check(matches(withText(mStringToBetyped)));
    }
}
