package com.fivestar.mobilblogg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;

public class Communicator extends Thread {
	final String TAG = "Communicator";
	final int TIMEOUTCONNECTION = 20000; // timeout ms
	final int TIMEOUTSOCKET = 15000; // timeout ms
	final int TRIALS = 3; 
	private String protocoll = "http://";
	private String host = "api.mobilblogg.nu";
	private String api  = "api_android_2.0.t";
	private DefaultHttpClient client;

	public Communicator() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpConnectionParams.setConnectionTimeout(params, TIMEOUTCONNECTION);
		HttpConnectionParams.setSoTimeout(params, TIMEOUTSOCKET);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		//		schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

		client = new DefaultHttpClient(conMgr, params);
		client.setParams(params);
	} 

	private String getRequestResponse(HttpPost post, HttpGet get) throws CommunicatorException  {
		BasicHttpResponse httpResponse = null;
		int retry = TRIALS;
		int count = 0;
		while(count < retry) {
			count += 1;
			try {
				if(post != null) {
					httpResponse = (BasicHttpResponse) client.execute(post);
				} else if(get != null) {
					httpResponse = (BasicHttpResponse) client.execute(get);	
				}
			} catch (Exception e) {
				count += 1;
				e.printStackTrace();
				if(count < retry) {
					Utils.log(TAG, "Exception, but let's try again");
				} else {
					throw new CommunicatorException("Network error");
				}
			}
			if(httpResponse != null) {
				break;
			}
		}
		if(httpResponse != null) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));

				StringBuilder sb = new StringBuilder();
				String line = "";
				while ((line = in.readLine()) != null) {
					sb.append(line);
				}
				in.close();
				return sb.toString();
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return null;
	}

	public void shutdownHttpClient() {
		if(client!=null && client.getConnectionManager()!= null) {
			client.getConnectionManager().shutdown();
		}
	}

	public int doLogin(String userName, String passWord) throws CommunicatorException {
		String url = protocoll+host+"/o.o.i.s";
		String jsonresponse = null;
		String hashedPassword = null;
		int loginStatus = 0;

		HttpPost postMethod = new HttpPost(url);

		try {
			hashedPassword = Utils.SHA1(getSalt(userName)+passWord);
		} catch (Exception e) {
			return 0;
		}

		if(hashedPassword != null) {
			try {
				List<NameValuePair> uri = new ArrayList<NameValuePair>(2);  
				uri.add(new BasicNameValuePair("template", api));  
				uri.add(new BasicNameValuePair("func", "login"));  
				uri.add(new BasicNameValuePair("username", userName));  
				uri.add(new BasicNameValuePair("password", hashedPassword));  
				postMethod.setEntity(new UrlEncodedFormEntity(uri));  
				jsonresponse = getRequestResponse(postMethod, null);
			} catch (IOException e) {  
				// TODO Auto-generated catch block  
				return 0;
			}
		} else {
			return 0;
		}
		try {
			JSONArray json = new JSONArray(jsonresponse);
			loginStatus = json.getJSONObject(0).optInt("status");
		} catch (JSONException j) {
			return 0;
		} catch (NullPointerException ne) {
			return 0;
		}
		return loginStatus;
	}

	public String getSalt(String userName) {
		String url = protocoll+host+"/o.o.i.s";
		String jsonresponse = "";
		String salt = "";
		HttpPost postMethod = new HttpPost(url);
		
		try {
			List<NameValuePair> uri = new ArrayList<NameValuePair>(2);  
			uri.add(new BasicNameValuePair("template", api));  
			uri.add(new BasicNameValuePair("func", "getSalt"));  
			uri.add(new BasicNameValuePair("user", userName));
			postMethod.setEntity(new UrlEncodedFormEntity(uri));  
			jsonresponse = getRequestResponse(postMethod, null);
		} catch (Throwable t) {
			return null;
		}
		try {
			JSONArray json = new JSONArray(jsonresponse);
			salt = json.getJSONObject(0).optString("salt");
		} catch (JSONException j) {
			return null;
		}
		return salt;
	}	

	public boolean foundUser(String userName) throws CommunicatorException {
		String url = protocoll+host+"/o.o.i.s";
		String jsonresponse = "";
		HttpPost postMethod = new HttpPost(url);
		
		try {
			List<NameValuePair> uri = new ArrayList<NameValuePair>(2);  
			uri.add(new BasicNameValuePair("template", api));  
			uri.add(new BasicNameValuePair("func", "findUser"));  
			uri.add(new BasicNameValuePair("username", userName));
			postMethod.setEntity(new UrlEncodedFormEntity(uri));  
			jsonresponse = getRequestResponse(postMethod, null);
		} catch (Throwable t) {
			return false;
		}
		try {
			JSONArray json = new JSONArray(jsonresponse);
			int foundUserName = json.getJSONObject(0).optInt("found");
			Utils.log(TAG, "FoundUsername json:" + foundUserName);
			if(json.getJSONObject(0).optInt("found") == 1) {
				Utils.log(TAG,"return true");
				return true;
			}
		} catch (JSONException j) {
			throw new CommunicatorException(j.getMessage());
		}
		Utils.log(TAG,"return false");
		return false;
	}	

	public int register(String userName, String passWord, String email, String secret) {
		String url = protocoll+host+"/o.o.i.s";
		String jsonresponse = "";
		String hashedPassword = "";
		String salt = "";
		int registerStatus = 0;

		salt = Utils.createSalt();
		try {
			hashedPassword = Utils.SHA1(salt + passWord);
		} catch (Exception e) {
			return 0;
		}

		HttpPost postMethod = new HttpPost(url);
		try {
			Utils.log(TAG, "Register:");
			Utils.log(TAG, "template " + api);
			Utils.log(TAG, "usr " + userName);
			Utils.log(TAG, "psw " + hashedPassword);
			Utils.log(TAG, "sal " + salt);
			Utils.log(TAG, "sec " + secret);
			Utils.log(TAG, "email " + email);
			
			List<NameValuePair> uri = new ArrayList<NameValuePair>(2);  
			uri.add(new BasicNameValuePair("template", api));  
			uri.add(new BasicNameValuePair("func", "register"));  
			uri.add(new BasicNameValuePair("username", userName));  
			uri.add(new BasicNameValuePair("passhash", hashedPassword));  
			uri.add(new BasicNameValuePair("salt", salt));  
			uri.add(new BasicNameValuePair("secret", secret));  
			uri.add(new BasicNameValuePair("email", email));  
			postMethod.setEntity(new UrlEncodedFormEntity(uri));  
			jsonresponse = getRequestResponse(postMethod, null);
		} catch (CommunicatorException ce) {  
			// TODO Auto-generated catch block  
			return 0;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return 0;
		}
		try {
			JSONArray json = new JSONArray(jsonresponse);
			registerStatus = json.getJSONObject(0).optInt("userid");
		} catch (JSONException j) {
			return 0;
		} catch (NullPointerException ne) {
			return 0;
		}
		return registerStatus;
	}


	/*
	 * Load Blog Posts
	 * Returns number of posts collected
	 */
	public int loadBloggs(MobilbloggApp app, int listNum, String username) throws CommunicatorException {
		String[] funcs = {"listBlogg","listStartpage","listFirstpage"};
		String jsonresponse = "";
		String url = "";
		if(username != null) {
			url = protocoll+host+"/o.o.i.s?template="+api+"&func="+funcs[listNum]+"&user="+username+"&page="+app.bc.getPage(listNum,username);
		} else {
			url = protocoll+host+"/o.o.i.s?template="+api+"&func="+funcs[listNum]+"&page="+app.bc.getPage(listNum,username);
		}

		HttpGet getMethod = new HttpGet(url);
		app.bc.increasePage(listNum, username);

		jsonresponse = getRequestResponse(null, getMethod);

		//		if(app.bc.size(listNum, username) == 0) {
		//			origIndex = 0;
		//		} else {
		//			origIndex = app.bc.size(listNum, username) - 1;
		//		}
		if (jsonresponse != null && jsonresponse.length()>0) {
			try {
				JSONArray json = new JSONArray(jsonresponse);
				int len = json.length();
				if (len == 0) {
					if(app.bc.size(listNum, username) == 0) {
						// Inga inlägg alls
						return -2;
					}
					return 0;
				}
				for(int i=0; i<len; i++) {
					try {
						PostInfo pi = new PostInfo();
						pi.img      = json.getJSONObject(i).get("picture_large").toString();
						pi.imgX     = Integer.parseInt(json.getJSONObject(i).get("picture_large_x").toString());
						pi.imgY     = Integer.parseInt(json.getJSONObject(i).get("picture_large_y").toString());
						pi.thumb    = json.getJSONObject(i).get("picture_small").toString();
						pi.thumbX   = Integer.parseInt(json.getJSONObject(i).get("picture_small_x").toString());
						pi.thumbY   = Integer.parseInt(json.getJSONObject(i).get("picture_small_y").toString());
						pi.headline = json.getJSONObject(i).get("caption").toString();
						pi.text     = json.getJSONObject(i).get("body").toString();
						pi.user     = json.getJSONObject(i).get("user").toString();
						pi.createdate = json.getJSONObject(i).get("createdate").toString();
						pi.imgid    = json.getJSONObject(i).get("id").toString();
						pi.numComment = json.getJSONObject(i).getInt("nbr_comments");
						pi.avatar   = json.getJSONObject(i).get("avatar").toString();

						app.bc.add(listNum, pi, username);
					} catch (NumberFormatException ne) {
						continue;
					} catch (JSONException j) {
						Utils.log(TAG, "Fel i json parsningen: "+j.getStackTrace());
						Utils.log(TAG, "Response: "+jsonresponse);

						throw new CommunicatorException(j.getMessage());
					}
				}
				return len;
			} catch (JSONException j) {
				Utils.log(TAG, "JSON parsing failure: "+jsonresponse);
				Utils.log(TAG, "Fångade Jsonexception, skickar comexc.");
				throw new CommunicatorException(j.getMessage());
			}
		}
		return 0;
	}

	public String postComment(String imgid, String comment) {
		try {
			String url = protocoll+host+"/o.o.i.s";
			HttpPost postMethod = new HttpPost(url);

			MultipartEntity multipartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartContent.addPart("func", new StringBody("writeComment"));
			multipartContent.addPart("imgid", new StringBody(imgid));
			multipartContent.addPart("message", new StringBody(comment));
			multipartContent.addPart("wtd", new StringBody("comment"));
			multipartContent.addPart("template", new StringBody(api));

			postMethod.setEntity(multipartContent);

			return getRequestResponse(postMethod, null);
		} catch (Throwable e) {
			return null;
		}
	}	


	public String getStartPage() {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listStartpage";
		String jsonresponse = "";
		HttpGet getMethod = new HttpGet(url);
		try {
			jsonresponse = getRequestResponse(null, getMethod);
		} catch (Throwable t) {
			return null;
		}
		return jsonresponse;
	}	

	public String getFirstPage() {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listFirstpage";
		String jsonresponse = "";
		HttpGet getMethod = new HttpGet(url);
		try {
			jsonresponse = getRequestResponse(null, getMethod);
		} catch (Throwable t) {
			return null;
		}
		return jsonresponse;
	}		

	public String getComments(String imgid) throws CommunicatorException {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=listComments&imgid="+imgid;
		String jsonresponse = "";
		HttpGet getMethod = new HttpGet(url);
		try {
			jsonresponse = getRequestResponse(null, getMethod);
		} catch (Throwable t) {
			throw new CommunicatorException(t.getMessage());
		}
		return jsonresponse;
	}	

	public String getProfileAvatar(String userName) {
		String url = protocoll+host+"/o.o.i.s?template="+api+"&func=profile&user="+userName;
		String jsonresponse = "";
		String urlToAvatar = "";
		HttpGet getMethod;
		try {
			getMethod = new HttpGet(url);
		} catch(IllegalArgumentException ie) {
			return null;
		}
		try {			
			jsonresponse = getRequestResponse(null, getMethod);

			if (jsonresponse != null && jsonresponse.length()>0) {
				try {
					JSONArray json = new JSONArray(jsonresponse);
					urlToAvatar = json.getJSONObject(0).get("avatar").toString();
				} catch (JSONException j) {
					Utils.log(TAG,"JSON error:" + j.toString());
					return null;
				}
			}
		} catch (Throwable t) {
			return null;
		}
		return urlToAvatar;
	}	


	public String doUpload(String username, String secret, String caption, String text, String showfor, String tags, String filePath) throws Throwable {
		File f = new File(filePath);

		try {
			String url = protocoll+host+"/o.o.i.s";
			HttpPost postMethod = new HttpPost(url);

			MultipartEntity multipartContent = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartContent.addPart("file", new FileBody(f));
			multipartContent.addPart("wtd", new StringBody("ladda_upp"));
			multipartContent.addPart("header", new StringBody(caption));
			multipartContent.addPart("text", new StringBody(text));
			multipartContent.addPart("secretword", new StringBody(secret));
			multipartContent.addPart("rights", new StringBody(showfor));
			multipartContent.addPart("func", new StringBody("upload"));
			multipartContent.addPart("path", new StringBody("/files/"+username));
			multipartContent.addPart("template", new StringBody(api));
			multipartContent.addPart("tags", new StringBody(tags));

			postMethod.setEntity(multipartContent);

			return getRequestResponse(postMethod, null);
		} catch (Throwable e) {
			throw e;
		}
	}
}