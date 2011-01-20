package com.fivestar.mobilblogg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

public class Communicator extends Thread {
	private String protocoll = "http://";
	private String host = "api.mobilblogg.nu";
	private String api  = "api_android_1.0.t";
	private DefaultHttpClient client;

	public Communicator() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

		client = new DefaultHttpClient(conMgr, params);
	} 

	public void shutdownHttpClient() {
		if(client!=null && client.getConnectionManager()!= null) {
			client.getConnectionManager().shutdown();
		}
	}

	public int doLogin(String userName, String passWord) {
		String url = protocoll+host+"/o.o.i.s";
		String jsonresponse = "";
		String hashedPassword = "";
		int loginStatus = -1;

		HttpPost postMethod = new HttpPost(url);

		try {
			hashedPassword = Utils.SHA1(getSalt(userName)+passWord);
		} catch (Exception e) {}

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

				StringBuilder sb = new StringBuilder();
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
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			jsonresponse = client.execute(getMethod, responseHandler);
		} catch (Throwable t) {
			System.out.println("Request failed:"+t.toString());
			return null;
		}
		try {
			JSONArray json = new JSONArray(jsonresponse);
			salt = json.getJSONObject(0).optString("salt");
		} catch (JSONException j) {
			System.out.println("JSON error:" + j.toString());
		}
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

	public String getStartPage() {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listStartpage";
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

	public String getFirstPage() {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listFirstpage";
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
	
	public String getComments(int imgid) {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listComments&imgid="+imgid;
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

	public String getProfileAvatar(String userName) {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=profile&user="+userName;
		String jsonresponse = "";
		String urlToAvatar = "";
		HttpGet getMethod = new HttpGet(url);
		System.out.println("URL:"+url);
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			jsonresponse = client.execute(getMethod, responseHandler);

			if (jsonresponse != null && jsonresponse.length()>0) {
				try {
					JSONArray json = new JSONArray(jsonresponse);
					urlToAvatar = json.getJSONObject(0).get("avatar").toString();
					System.out.println("avatar:"+urlToAvatar);
				} catch (JSONException j) {
					System.out.println("JSON error:" + j.toString());
				}
			}
		} catch (Throwable t) {
			System.out.println("Request failed:"+t.toString());
			return null;
		}
		return urlToAvatar;
	}	


	public String doUpload(String username, String secret, String caption, String text, String showfor, String filePath) throws Throwable {

		File f = new File(filePath);

		try {
			String url = protocoll+host+"/o.o.i.s";
			HttpPost postMethod = new HttpPost(url);

			FileBody bin = new FileBody(f);			
			StringBody sb1 = new StringBody("ladda_upp");
			StringBody sb2 = new StringBody(caption);
			StringBody sb3 = new StringBody(text);
			StringBody sb4 = new StringBody(secret);
			StringBody sb5 = new StringBody(showfor);
			StringBody sb6 = new StringBody("upload");
			StringBody sb7 = new StringBody("/files/"+username);
			StringBody sb8 = new StringBody(api);

			MultipartEntity multipartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartContent.addPart("file", bin);
			multipartContent.addPart("wtd", sb1);
			multipartContent.addPart("header", sb2);
			multipartContent.addPart("text", sb3);
			multipartContent.addPart("secretword", sb4);
			multipartContent.addPart("rights", sb5);
			multipartContent.addPart("func", sb6);
			multipartContent.addPart("path", sb7);
			multipartContent.addPart("template", sb8);


			postMethod.setEntity(multipartContent);
			HttpResponse resp = client.execute(postMethod);

			InputStream is = resp.getEntity().getContent();
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			is.close();

			System.out.println("RESPONSE: "+total.toString());

			return total.toString();
		} catch (Throwable e) {
			throw e;
		}
	}
}