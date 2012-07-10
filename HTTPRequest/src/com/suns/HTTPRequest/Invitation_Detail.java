package com.suns.HTTPRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Invitation_Detail extends Activity {

	private TextView Title=null;
	private TextView Content=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invitation_detail);
		Title=(TextView)findViewById(R.id.bigtitle);
		Content=(TextView)findViewById(R.id.contentdetail);
		Intent intent=getIntent();
		String titletext=intent.getStringExtra("title");
		String contenttext=intent.getStringExtra("content");
		Title.setText(titletext);
		Content.setText(contenttext);
		
	}
	
	
	

}
