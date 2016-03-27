package com.cse480.alzand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.test.suitebuilder.annotation.SmallTest;
import static org.junit.Assert.*;

import com.squareup.okhttp.*;
import java.io.*;


@SmallTest
public class ApiTest {
    private OkHttpClient client;

    @Before
    public void setUp() {
	 client = new OkHttpClient();
    }

    @Test
    public void test1() {
	assertEquals("bong","bong");
    }
}
    
