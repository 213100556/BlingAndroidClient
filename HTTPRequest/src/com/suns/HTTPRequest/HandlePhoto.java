package com.suns.HTTPRequest;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class HandlePhoto {
	
	public Bitmap returnBitMap(String url) {
		// TODO Auto-generated method stub
		URL myurl=null;
		Bitmap bitmap=null;
		try{
			myurl=new URL(url);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		try{
			HttpURLConnection conn=(HttpURLConnection)myurl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is =conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
    	return bitmap;
	}
}
