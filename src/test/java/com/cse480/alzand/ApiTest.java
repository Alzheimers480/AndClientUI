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
    private static Float cutoff = 150.0f;
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
	assertEquals("True", response);
	response = newUser("dnkeller","dingle","dingle","Dolnak","Smeller","dolnakSMELL@yahoo.com");
	assertEquals("user already exists False", response);
	response = newUser("nkeller","shpongle","shpongle","David","Keller","dnkeller@oakland.edu");
	assertEquals("True", response);
	response = auth("dnkeller","shpongle");
	assertEquals("True", response);
	
	response = newAcq("mZbrg", "Mark", "Zineberg", "female");
	assertEquals("True", response);
	response = relate("dnkeller", "mZbrg", "Grandson", " My least favorite grandson", "\\model\\s01\\1.pgm", "\\model\\s01\\2.pgm", "\\model\\s01\\3.pgm");
	assertEquals("True", response);

	response = newAcq("lrMD", "Larry", "McDermin", "male");
	assertEquals("True", response);
	response = relate("dnkeller", "lrMD", "Brother", " My coolest brother. He is so extreme", "\\model\\s02\\1.pgm", "\\model\\s02\\2.pgm", "\\model\\s02\\3.pgm");
	assertEquals("True", response);
	
	response = newAcq("smmcd", "Smiles", "McDermin", "male");
	assertEquals("True", response);
	response = relate("dnkeller", "smmcd", "Son", " My favorite son. He makes me so happy", "\\model\\s03\\1.pgm", "\\model\\s03\\2.pgm", "\\model\\s03\\3.pgm");
	assertEquals("True", response);

	response = newAcq("gjm", "Grimy", "Jim", "male");
	assertEquals("True", response);
	response = relate("dnkeller", "gjm", "Neighbor", " He steals my newspapers", "\\model\\s06\\1.pgm", "\\model\\s06\\2.pgm", "\\model\\s06\\3.pgm");
	assertEquals("True", response);


	response = newAcq("hj", "Happy", "Johnson", "male");
	assertEquals("True", response);
	response = relate("dnkeller", "hj", "Caretaker", " He gives me my meds", "\\model\\s07\\1.pgm", "\\model\\s07\\2.pgm", "\\model\\s07\\3.pgm");
	

	response = newAcq("sr", "Shelly", "Ripples", "female");
	assertEquals("True", response);
	response = relate("dnkeller", "sr", "Caretaker", " She looks after me", "\\model\\s08\\1.pgm", "\\model\\s08\\2.pgm", "\\model\\s08\\3.pgm");
	assertEquals("True", response);
	
	assertTrue(predict("dnkeller", "\\test\\s01\\4.pgm", "Mark Zineberg", "Grandson", "female", " My least favorite grandson"));
	assertTrue(predict("dnkeller", "\\test\\s02\\4.pgm", "Larry McDermin", "Brother", "male", " My coolest brother. He is so extreme"));
	assertTrue(predict("dnkeller", "\\test\\s03\\4.pgm", "Smiles McDermin", "Son", "male", " My favorite son. He makes me so happy"));
	assertTrue(predict("dnkeller", "\\test\\s06\\4.pgm", "Grimy Jim", "Neighbor", "male", " He steals my newspapers"));
	assertTrue(predict("dnkeller", "\\test\\s07\\4.pgm", "Happy Johnson", "Caretaker", "male", " He gives me my meds"));
	assertTrue(predict("dnkeller", "\\test\\s08\\4.pgm", "Shelly Ripples", "Caretaker", "female", " She looks after me"));
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

    private Boolean predict(String username, String path, String fullname, String relation, String gender, String description) {
	File picture = new File(picPath+path);
	
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
	
	String sResponse = "";
	try {
	    Response response = client.newCall(request).execute();
	    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
	    sResponse = response.body().string();
	} catch (Exception e) {fail("predict exception: "+e.toString());}
	try {
	jsonParser = new JSONObject(sResponse);
	assertEquals(fullname, jsonParser.getString("ACQUAINTANCE_FNAME")+" "+jsonParser.getString("ACQUAINTANCE_LNAME"));
	assertEquals(relation, jsonParser.getString("RELATION"));
	assertEquals(gender, jsonParser.getString("GENDER"));
	assertEquals(description, jsonParser.getString("DESCRIPTION"));
	Float distance = Float.parseFloat(jsonParser.getString("DISTANCE"));
	assertTrue(distance > 0 && distance < cutoff);
	} catch (Exception e) {fail("JSON exception: "+e.toString());}
	return true;	
    }
}
    
