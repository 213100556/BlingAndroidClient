package com.suns.HTTPRequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
//import android.util.Log;

public class Invitation_Detail extends Activity {

	private TextView Title=null;
	private TextView Content=null;
	private TextView Username=null;
	private ImageView Photo=null;
	private ImageView large_picture=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.invitation_detail); 
		Title=(TextView)findViewById(R.id.bigtitle);
		Username=(TextView)findViewById(R.id.username);
		Photo=(ImageView)findViewById(R.id.image);
		large_picture=(ImageView)findViewById(R.id.large_picture);
		Content=(TextView)findViewById(R.id.contentdetail);
		Content.setMovementMethod(ScrollingMovementMethod.getInstance());
		Intent intent=getIntent();
		String titletext=intent.getStringExtra("title");
		String contenttext=intent.getStringExtra("content");
		String username=intent.getStringExtra("username"); 
		String photo=intent.getStringExtra("photourl");
		String picture_large_url=intent.getStringExtra("picture_large_url");
		HandlePhoto handlephoto=new HandlePhoto();
		Bitmap bitmap=handlephoto.returnBitMap(photo);
		
		Title.setText(titletext);
		Content.setText(contenttext);
		Username.setText(username);
		Photo.setImageBitmap(bitmap);
		if(picture_large_url=="No_url")
		{
			large_picture.setVisibility(View.GONE);
		}
		else 
		{
			Bitmap largepic=handlephoto.returnBitMap(picture_large_url);
			large_picture.setImageBitmap(largepic);
		}
	}
}
