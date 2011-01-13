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
import java.util.Date;
import java.text.DateFormat;
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

	public static String prettyDate(String d) {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
		Date myDate = null;
		try {
			myDate = formatter.parse(d);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return d;
		}
		System.out.println("Time: " + d);
		return (DateFormat.getDateInstance().format(myDate));
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
}