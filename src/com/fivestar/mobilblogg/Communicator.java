package com.fivestar.mobilblogg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;


public class Communicator extends Thread {

	private String protocoll = "http://";
	private String host = "api.mobilblogg.nu";
	private String api  = "api_v2.2.t";
	private DefaultHttpClient client;
	
	public Communicator() {
		client = new DefaultHttpClient();
	}
	
	public int doLogin(String userName, String passWord) {
		String url = protocoll+host+"/o.o.i.s";
		String jsonresponse = "";
		String hashedPassword = "";
		int loginStatus = -1;

		HttpPost postMethod = new HttpPost(url);
		
		try {
			hashedPassword = SHA1(getSalt(userName)+passWord);
		} catch (Exception e) {}

		System.out.println("hashpwd:"+hashedPassword);
		
		if(hashedPassword.length() > 10) {
			try {
				BufferedReader in = null;
				List<NameValuePair> uri = new ArrayList<NameValuePair>(2);  
				uri.add(new BasicNameValuePair("template", api));  
				uri.add(new BasicNameValuePair("func", "login"));  
				uri.add(new BasicNameValuePair("username", userName));  
				uri.add(new BasicNameValuePair("password", hashedPassword));  
				postMethod.setEntity(new UrlEncodedFormEntity(uri));  

				HttpResponse response = client.execute(postMethod);
				in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	 
				StringBuffer sb = new StringBuffer("");
	            String line = "";
	            while ((line = in.readLine()) != null) {
	                sb.append(line);
	            }
	            in.close();
	            jsonresponse = sb.toString();
				
			} catch (ClientProtocolException e) {  
				// TODO Auto-generated catch block  
			} catch (IOException e) {  
				// TODO Auto-generated catch block  
			}  
		}
		
		System.out.println("Resp:"+jsonresponse);
		try {
			JSONArray json = new JSONArray(jsonresponse);
			loginStatus = json.getJSONObject(0).optInt("status");
		} catch (JSONException j) {
			System.out.println("JSON error:" + j.toString());
		}
		return loginStatus;
	}	
	
	public String getSalt(String userName) {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=getSalt&user="+userName;
		String jsonresponse = "";
		String salt = "";
		HttpGet getMethod = new HttpGet(url);
		System.out.println("URL:"+url);
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			jsonresponse = client.execute(getMethod, responseHandler);
		} catch (Throwable t) {
			System.out.println("Request failed:"+t.toString());
			return null;
		}
		System.out.println("Resp:"+jsonresponse);
		try {
			JSONArray json = new JSONArray(jsonresponse);
			salt = json.getJSONObject(0).optString("salt");
		} catch (JSONException j) {
			System.out.println("JSON error:" + j.toString());
		}
		System.out.println("salt:"+salt);
		return salt;
	}	

	public String getBlogg(String userName) {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listBlogg&user="+userName;
		String jsonresponse = "";
		HttpGet getMethod = new HttpGet(url);
		System.out.println("URL:"+url);
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			jsonresponse = client.execute(getMethod, responseHandler);
		} catch (Throwable t) {
			System.out.println("Request failed:"+t.toString());
			return null;
		}
		return jsonresponse;
	}	

	
	
	private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 
 
	public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);
	}
}
