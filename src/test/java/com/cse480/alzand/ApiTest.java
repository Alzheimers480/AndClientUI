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
    private String ip, response;

    @Before
    public void setUp() {
	client = new OkHttpClient();
	ip = "http://141.210.25.46";
    }

    @Test
    public void test() {
	response = newUser("dnkeller","shpongle","shpongle","David","Keller","dnkeller@oakland.edu");
	assertEquals(response, "True");
	response = newUser("dnkeller","dingle","dingle","Dolnak","Smeller","dolnakSMELL@yahoo.com");
	assertEquals(response, "user already exists False");
	response = newUser("nkeller","shpongle","shpongle","David","Keller","dnkeller@oakland.edu");
	assertEquals(response, "True");
	response = newAcq("mZbrg", "Mark", "Zineberg", "female");
	assertEquals(response, "True");
	response = auth("dnkeller","shpongle");
	assertEquals(response, "True");
	response = newAcq("lrMD", "Larry", "McDermin", "male");
	assertEquals(response, "True");
	response = newAcq("smmcd", "Smiles", "McDermin", "male");
	assertEquals(response, "True");
	String current = "";
	File picture1, picture2, picture3;
	try {
	    current = new File( "." ).getCanonicalPath();
	    picture1 = new File(current+"\\relater\\1.bmp");
	    picture2 = new File(current+"\\relater\\2.bmp");
	    picture3 = new File(current+"\\relater\\3.bmp");
	    assertEquals(current,"C:\\Users\\dnkeller\\dev\\AndClientUI");
	    response = relate("dnkeller", "mZbrg", "Grandson", " My least favorite grandson", picture1, picture2, picture3);
	    assertEquals(response, "True");
	} catch (Exception e) {fail("file exception thrown: "+e.toString());}
    }

    private String newUser(String username, String password, String password2, String fname, String lname, String email) {
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("PASSWORD", password)
	    .addFormDataPart("PASSWORD2", password2)
	    .addFormDataPart("FNAME", fname)
	    .addFormDataPart("LNAME", lname)
	    .addFormDataPart("EMAIL", email)
	    .build();
	Request request = new Request.Builder()
	    .url(ip+"/newuser.php")
	    .post(requestBody)
	    .build();
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    return response.body().string();
	} catch (Exception e) {}
	return "";
    }

    private String newAcq(String username, String fname, String lname, String gender) {
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("FNAME", fname)
	    .addFormDataPart("LNAME", lname)
	    .addFormDataPart("GENDER", gender)
	    .build();
	Request request = new Request.Builder()
	    .url(ip+"/newacqu.php")
	    .post(requestBody)
	    .build();
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    return response.body().string();
	} catch (Exception e) {}
	return "";
    }

    private String auth(String username, String password) {
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("PASSWORD", password)
	    .build();
	Request request = new Request.Builder()
	    .url(ip+"/auth.php")
	    .post(requestBody)
	    .build();
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    return response.body().string();
	} catch (Exception e) {}
	return "";
    }

    private String relate(String username, String acquname, String relation, String message, File pic1, File pic2, File pic3) {
	MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("ACQUNAME", acquname)
	    .addFormDataPart("RELATION", relation)
	    .addFormDataPart("MESSAGE", message)
	    .addFormDataPart("pics[]", "1.bmp", RequestBody.create(MEDIA_TYPE_PGM, pic1))
	    .addFormDataPart("pics[]", "2.bmp", RequestBody.create(MEDIA_TYPE_PGM, pic2))
	    .addFormDataPart("pics[]", "3.bmp", RequestBody.create(MEDIA_TYPE_PGM, pic3))
	    .build();
	Request request = new Request.Builder()
	    .url(ip+"/relate.php")
	    .post(requestBody)
	    .build();
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    return response.body().string();
	} catch (Exception e) {fail("relate exception: "+e.toString());}
	return "";
    }
}
    
