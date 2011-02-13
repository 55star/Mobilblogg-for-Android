package com.fivestar.mobilblogg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.james.mime4j.field.datetime.DateTime;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class Utils {

	final static String TAG = "Utils";

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

	public static void saveCredentials(Context c, String userName, String passWord) {
		String FILENAME = "mb_cred";
		String delimiter = "|";
		String string = userName + delimiter + passWord;

		FileOutputStream fos = null;
		try {
			fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(fos != null) {
			try {
				fos.write(string.getBytes());
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void removeSavedCredentials(Context c) {
		File dir = c.getFilesDir();
		File file = new File(dir, "mb_cred");
		file.delete();
	}

	public static String getSavedCredentials(Context c) {
		String FILENAME = "mb_cred";
		int ch;
		StringBuffer strContent = new StringBuffer("");

		FileInputStream fin;
		try {
			fin = c.openFileInput(FILENAME);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		if(fin != null) {
			try {
				while((ch = fin.read()) > -1) {
					strContent.append((char)ch);
				}
				fin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			return strContent.toString();
		}
		return null;
	}

	public static String PrettyDate(String cmpDate){    
		Log.i(TAG, "Make " + cmpDate + " pretty");

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
			return diffSeconds + " sekunder sen";
		}
		if(diffMinutes < 60) {
			return diffMinutes + " minuter sen";
		}
		if(diffHours < 24) {
			return diffHours + " timmar sen";
		}
		return calCmp.get(Calendar.DATE) + " " 
		+ month[calCmp.get(Calendar.MONTH)] + " " 
		+ calCmp.get(Calendar.HOUR) + ":" 
		+ calCmp.get(Calendar.MINUTE) + " " 
		+ calCmp.get(Calendar.YEAR);
	}
}