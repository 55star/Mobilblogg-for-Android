package com.fivestar.mobilblogg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class Utils {
	final String TAG = "Utils.java";
	final static String SHAREDPREFFILE = "mb_cred";
	private static int LOGLEVEL = 1;

	public static void log(String tag, String mess) {
		if (LOGLEVEL > 1) {
			Log.e(tag, mess);
		} else {
			if(LOGLEVEL > 0) {
				Log.w(tag, mess);
			}
		}
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size=1024;
		try {
			byte[] bytes=new byte[buffer_size];
			for(;;) {
				int count=is.read(bytes, 0, buffer_size);
				if(count==-1)
					break;
				os.write(bytes, 0, count);
			}
		}
		catch(Exception ex){}
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

	public static boolean StoreByteImage(Context mContext, byte[] imageData, int quality, String expName) {

		File sdImageMainDirectory = new File("/sdcard/Mobilblogg/latest");
		if(!sdImageMainDirectory.exists()) {
			sdImageMainDirectory.mkdirs();
		}
		FileOutputStream fileOutputStream = null;
		String fileName = expName;

		try {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 5;

			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() +"/" + fileName + ".jpg");

			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	private static SharedPreferences getPrefs(Context c) {
		SharedPreferences sp = c.getSharedPreferences(SHAREDPREFFILE, c.MODE_PRIVATE);
		return sp;
	}

	public static void saveCredentials(Context c, String userName, String passWord) {
		Editor e = getPrefs(c).edit();
		e.putString("mb_cred_usr", userName);
		e.putString("mb_cred_pwd", passWord);
		e.commit();
	}

	public static String getSavedCredentials(Context c) {
		SharedPreferences sp = getPrefs(c);
		if(sp.contains("mb_cred_usr") && sp.contains("mb_cred_pwd")) {
			return sp.getString("mb_cred_usr", "default") + "|" + sp.getString("mb_cred_pwd", "default");
		} else {
			return null;
		}
	}

	public static void saveSecretWord(Context c, String secretWord) {
		Editor e = getPrefs(c).edit();
		e.putString("secret", secretWord);
		e.commit();
	}

	public static String getSecretWord(Context c) {
		SharedPreferences sp = getPrefs(c);
		if(sp.contains("secret")) {
			return sp.getString("secret", "default");
		} else {
			return null;
		}
	}


	public static void removeSavedCredentials(Context c) {
		Editor e = getPrefs(c).edit();
		e.clear();
		e.commit();
	}


	public static String PrettyDate(String cmpDate, Context ctx){    
		Calendar calNow = Calendar.getInstance();
		Calendar calCmp = Calendar.getInstance();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String month[] = {"jan","feb","mar","apr","maj","jun","jul","aug","sep","okt","nov","dec"};

		try {
			calCmp.setTime(df.parse(cmpDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long milliseconds1 = calCmp.getTimeInMillis();
		long milliseconds2 = calNow.getTimeInMillis();
		long diff = milliseconds2 - milliseconds1;
		long diffSeconds = diff / 1000;
		long diffMinutes = diff / (60 * 1000);
		long diffHours = diff / (60 * 60 * 1000);

		if(diffSeconds < 60) {
			if(diffSeconds < 0) {
				return ("0 " + ctx.getString(R.string.secondssince));
			}
			return (diffSeconds + " " + ctx.getString(R.string.secondssince));
		}
		if(diffMinutes < 60) {
			return (diffMinutes + " " + ctx.getString(R.string.minutessince));
		}
		if(diffHours < 24) {
			if(diffHours == 1) {
				return (diffHours + " " + ctx.getString(R.string.hoursince));				
			}
			return (diffHours + " " + ctx.getString(R.string.hourssince));
		}
		String longdate = calCmp.get(Calendar.DATE) + " ";
		longdate += month[calCmp.get(Calendar.MONTH)] + " ";
		int hour = calCmp.get(Calendar.HOUR_OF_DAY);
		if(hour < 10) {
			longdate += "0" + hour + ":";
		} else {
			longdate += hour + ":";
		}
		int min = calCmp.get(Calendar.MINUTE);
		if(min < 10) {
			longdate += "0" + min + " ";
		} else {
			longdate += min + " ";
		}
		longdate += calCmp.get(Calendar.YEAR);

		return longdate;
	}
}