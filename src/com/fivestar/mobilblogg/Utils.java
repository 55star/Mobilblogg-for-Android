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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class Utils {

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

	public static String getSavedCredentials(Context cntx) {
		String FILENAME = "mb_cred";
		int ch;
		StringBuffer strContent = new StringBuffer("");

		FileInputStream fin = null;
		try {
			fin = cntx.openFileInput(FILENAME);
		} catch (FileNotFoundException fnfe) {
			// TODO Auto-generated catch block
			fnfe.printStackTrace();
			return null;
		}
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
			return (diffHours + " " + ctx.getString(R.string.hoursince));
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