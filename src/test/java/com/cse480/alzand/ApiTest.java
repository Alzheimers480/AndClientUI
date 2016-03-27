package com.cse480.alzand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.test.suitebuilder.annotation.SmallTest;
import static org.junit.Assert.*;

import com.squareup.okhttp.*;
import java.io.*;
import org.json.*;

@SmallTest
public class ApiTest {
    private OkHttpClient client;
    private String ip = "http://141.210.25.46";
    private String response;
    private JSONObject jsonParser;
    private static Float cutoff = 100.0f;
    private String picPath;
	
    @Before
    public void setUp() {
	client = new OkHttpClient();
	try {
	    picPath = new File( "." ).getCanonicalPath()+"\\testdata";
	} catch (Exception e) {fail("relate exception thrown: "+e.toString());}
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
	response = relate("dnkeller", "mZbrg", "Grandson", " My least favorite grandson", "\\model\\s01\\1.pgm", "\\model\\s01\\2.pgm", "\\model\\s01\\3.pgm");
	assertEquals(response, "True");

	File picture;
	try {
	    picture = new File(picPath+"\\test\\s01\\4.pgm");
	    response = predict("dnkeller", picture);
	    jsonParser = new JSONObject(response);
	    assertEquals(jsonParser.getString("ACQUAINTANCE_FNAME"),"Mark");
	    assertEquals(jsonParser.getString("ACQUAINTANCE_LNAME"),"Zineberg");
	    assertEquals(jsonParser.getString("RELATION"),"Grandson");
	    assertEquals(jsonParser.getString("GENDER"),"female");
	    assertEquals(jsonParser.getString("DESCRIPTION")," My least favorite grandson");
	    Float distance = Float.parseFloat(jsonParser.getString("DISTANCE"));
	    assertTrue(distance > 0 && distance < cutoff);
	} catch (Exception e) {fail("predict exception thrown: "+e.toString());}
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

    private String relate(String username, String acquname, String relation, String message, String pic1, String pic2, String pic3) {
	File picture1, picture2, picture3;
	picture1 = new File(picPath+pic1);
	picture2 = new File(picPath+pic2);
	picture3 = new File(picPath+pic3);
	    
	MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("ACQUNAME", acquname)
	    .addFormDataPart("RELATION", relation)
	    .addFormDataPart("MESSAGE", message)
	    .addFormDataPart("pics[]", "1.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture1))
	    .addFormDataPart("pics[]", "2.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture2))
	    .addFormDataPart("pics[]", "3.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture3))
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

    private String predict(String username, File picture) {
	MediaType MEDIA_TYPE_PGM = MediaType.parse("image/x-portable-graymap");
	RequestBody requestBody = new MultipartBuilder()
	    .type(MultipartBuilder.FORM)
	    .addFormDataPart("USERNAME", username)
	    .addFormDataPart("pic", "7.bmp", RequestBody.create(MEDIA_TYPE_PGM, picture))
	    .build();
	Request request = new Request.Builder()
	    .url(ip+"/predict.php")
	    .post(requestBody)
	    .build();
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    return response.body().string();
	} catch (Exception e) {fail("predict exception: "+e.toString());}
	return "";	
    }
}
    
