package com.suns.HTTPRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;




public class HTTPRequestActivity extends ListActivity {
    /** Called when the activity is first created. */
	private ListView listview=null;
	private ListView hotlist=null;
	private String result="";
	private String url="http://www.bling0.com/all/latest.json";
	private ArrayList<HashMap<String,Object>> newlist=new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String,Object>> Hotlist=new ArrayList<HashMap<String,Object>>();
	private HashMap<String,Object> map=null;
	private Button more=null;
	private Button newest=null;
	private Button hotest=null;
	private ProgressBar bar =null;
	private Integer page=null;
	private Integer count=1;
	private int judge=0;
	private int buttontouch=1;
	private SimpleAdapter listadapter=null;
	private ButtonBroadcast buttonchange=null;
	private BackBroadcast buttonback=null;
	private IntentFilter filter1;
	private IntentFilter filter2;
	private LinearLayout lin=null;
	private Handler handler=null;
	private Intent intent1=null;
	private Intent intent2=null;
	private HttpClient client=null;
	private HttpGet request=null;
	private Handler dialoghandle=null;
//	private Bitmap bmimg=null;
	private ProgressDialog progressdialog=null;
//	private String avatar_url=null;
	private LayoutParams FFlayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
	private LayoutParams mLayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	private LayoutParams BLayoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
	private static final String ButtonAction="com.suns.HTTPRequestActivity.BUTTONCHANGE";
	private static final String BarAction="com.suns.HTTPRequestActivity.BARCHANGE";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        handler=new Handler();
        lin=new LinearLayout(this);      
        lin.setOrientation(LinearLayout.HORIZONTAL);
   
        newest=(Button)findViewById(R.id.newest);
        newest.setClickable(false);
        newest.setBackgroundColor(Color.YELLOW);
        
        hotest=(Button)findViewById(R.id.hotest);
        hotest.setBackgroundColor(Color.GRAY);
        
        more=new Button(this);
        more.setText("更多");
        more.setGravity(Gravity.CENTER);
        
        bar=new ProgressBar(this);
        bar.setVisibility(View.GONE);
        bar.setMax(5);
        bar.setMinimumHeight(5);
        bar.setMinimumWidth(5);
        
        lin.addView(more,FFlayoutParams);
        lin.addView(bar, BLayoutParams);
        lin.setGravity(Gravity.CENTER);
        
        LinearLayout loadingLayout = new LinearLayout(this);
        loadingLayout.addView(lin,mLayoutParams);
        loadingLayout.setGravity(Gravity.CENTER);
        
        hotlist=(ListView)findViewById(R.id.list);
        hotlist.addFooterView(loadingLayout);
        
        listview=getListView();
        listview.addFooterView(loadingLayout);
        CallWebService(geturl());	
        listadapter=new SimpleAdapter(this,newlist ,R.layout.user,new String[]{"title","photo","username"},new int []{R.id.title,R.id.photo,R.id.username});
        listadapter.setViewBinder(new ViewBinder(){

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// TODO Auto-generated method stub
				if(view instanceof ImageView && data instanceof Bitmap){
				ImageView iv=(ImageView)view;
				iv.setImageBitmap((Bitmap)data);
				return true;
				}
				return false;
			}
        	
        });
        setListAdapter(listadapter);   
       
        more.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buttonchange=new ButtonBroadcast();
				buttonback=new BackBroadcast();
				filter1=new IntentFilter();
				filter2=new IntentFilter();
				
				filter1.addAction(ButtonAction);
				filter2.addAction(BarAction);

				intent1=new Intent();
				intent2=new Intent();
				intent1.setAction(ButtonAction);
				intent2.setAction(BarAction);
				
				new Thread(new Runnable()
				{
					public void run()
					{
						showmore();
						handler.post(new Runnable()
						{
							public void run()
							{
								listadapter.notifyDataSetChanged();
								
								HTTPRequestActivity.this.registerReceiver(buttonback, filter2);
								HTTPRequestActivity.this.sendBroadcast(intent2);
								HTTPRequestActivity.this.unregisterReceiver(buttonback);
							}
						});
					}
				}).start();
				
				HTTPRequestActivity.this.registerReceiver(buttonchange, filter1);
				HTTPRequestActivity.this.sendBroadcast(intent1);
				HTTPRequestActivity.this.unregisterReceiver(buttonchange);
			}
        }
        );
        
        dialoghandle=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				progressdialog.dismiss();
			}
        };
        
        newest.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				judge=0;
				newest.setClickable(false);
				hotest.setClickable(true);
				progressdialog=ProgressDialog.show(HTTPRequestActivity.this, "Loading...", "Please wait...",true,false);
				seturl("http://www.bling0.com/all/latest.json");
//				CallWebService(geturl());
//				listadapter=new SimpleAdapter(HTTPRequestActivity.this,newlist ,R.layout.user,new String[]{"title","content"},new int []{R.id.title,R.id.content});
//				setListAdapter(listadapter); 
				new Thread(){
					public void run()
					{
						CallWebService(geturl());
						listadapter=new SimpleAdapter(HTTPRequestActivity.this,newlist ,R.layout.user,new String[]{"title","photo","username"},new int []{R.id.title,R.id.photo,R.id.username});
				        listadapter.setViewBinder(new ViewBinder(){

							@Override
							public boolean setViewValue(View view, Object data,
									String textRepresentation) {
								// TODO Auto-generated method stub
								if(view instanceof ImageView && data instanceof Bitmap){
								ImageView iv=(ImageView)view;
								iv.setImageBitmap((Bitmap)data);
								return true;
								}
								return false;
							}
				        	
				        });
						dialoghandle.post(new Runnable()
						{
							public void run()
							{
								setListAdapter(listadapter);
								dialoghandle.sendEmptyMessage(0);
							}
						});
					}
				}.start();
			}
        }
        );
        
        
        newest.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
					if(arg1.getAction()==MotionEvent.ACTION_DOWN && buttontouch==-1)
					{
						arg0.setBackgroundColor(Color.YELLOW);
						hotest.setBackgroundColor(Color.GRAY);
						buttontouch=-buttontouch;
					}
				
				return false;
			}
        }
        );
        
        hotest.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				judge=1;
				newest.setClickable(true);
				hotest.setClickable(false);
				progressdialog=ProgressDialog.show(HTTPRequestActivity.this, "Loading...", "Please wait...",true,false);
				seturl("http://www.bling0.com/all/hottest/day.json");
				new Thread(){
					public void run(){
						CallWebService(geturl());
						listadapter=new SimpleAdapter(HTTPRequestActivity.this,Hotlist ,R.layout.user,new String[]{"title","photo","username"},new int []{R.id.title,R.id.photo,R.id.username});
				       
						listadapter.setViewBinder(new ViewBinder(){

							@Override
							public boolean setViewValue(View view, Object data,
									String textRepresentation) {
								// TODO Auto-generated method stub
								if(view instanceof ImageView && data instanceof Bitmap){
								ImageView iv=(ImageView)view;
								iv.setImageBitmap((Bitmap)data);
								return true;
								}
								return false;
							}
							});
							
						dialoghandle.post(new Runnable(){
							public void run()
							{
								setListAdapter(listadapter);  
								dialoghandle.sendEmptyMessage(0);
							}
						});
					}
				}.start();
			}
        });
        
        hotest.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
					if(event.getAction()==MotionEvent.ACTION_DOWN && buttontouch==1)
					{
						v.setBackgroundColor(Color.YELLOW);
						newest.setBackgroundColor(Color.GRAY); 
						buttontouch=-buttontouch;
					}
				return false;
			} 
        });
    }
    

@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
    
	public void CallWebService(String url)

	{
		client=new DefaultHttpClient();
		request=new HttpGet(url);
		ResponseHandler<String> handler=new BasicResponseHandler();
		try{
			result=client.execute(request,handler);
		}catch(ClientProtocolException e)
		{
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.getConnectionManager().shutdown();
		if(judge==0)
		{
			HandleJson(newlist);
		}
		else 
			HandleJson(Hotlist);
		  
	}
	
	public void HandleJson( ArrayList<HashMap<String,Object>> list) 
	{
		try{
		JSONTokener tokener=new JSONTokener(result);
		JSONObject object= (JSONObject)tokener.nextValue();
		page=object.getInt("num_pages");
		JSONArray articles=object.getJSONArray("articles");
		for(Integer i=0;i<articles.length();i++)
		{
			JSONObject article=(JSONObject)articles.opt(i);
			String title=article.getString("title");
			JSONObject top_post=article.getJSONObject("top_post");
			Integer articleid=top_post.getInt("article_id");
			String content=top_post.getString("content");
			String bigph_url="http://www.bling0.com"+(String)top_post.opt("picture_large_url");
			if(bigph_url=="http://www.bling0.comnull")
			{
				bigph_url="No_url";
			}
			JSONObject user=top_post.getJSONObject("user");
			String avatar_url="http://www.bling0.com"+user.getString("avatar_url");
			String username=user.getString("name");
			HandlePhoto handlephoto=new HandlePhoto();
			Bitmap bitmap=handlephoto.returnBitMap(avatar_url);
			
			map=new HashMap<String,Object>();
			if(title=="null")
			{
				map.put("title",articleid.toString());
			}
			else
			{
			map.put("title",title); 
			}
			map.put("photo", bitmap);
			map.put("username",username);
			map.put("content", content);
			map.put("photourl", avatar_url);
			map.put("picture_large_url", bigph_url);
			list.add(map);	
		}
		
	}catch(JSONException e)
	{
		e.printStackTrace();
	}
	}
	
	


	public void seturl(String Url){
		url=Url;
	}
	
	public String geturl()
	{
		return url;
	}
	
	
	public void showmore()
	{
		count+=1;
		if(count>page)
		{
			Toast.makeText(HTTPRequestActivity.this, "已是最后一页", Toast.LENGTH_SHORT);
		}
		else if(judge==0)
		{
		String pagecount=count.toString();
		String url="http://www.bling0.com/all/latest/page/"+pagecount+".json";
		seturl(url);
		CallWebService(geturl());
		}
		else if(judge==1)
		{
			String pagecount=count.toString();
			String url="http://www.bling0.com/all/hottest/day/page/"+pagecount+".json";
			seturl(url);
			CallWebService(geturl());
		}
		else
			Toast.makeText(HTTPRequestActivity.this, "访问出错", Toast.LENGTH_SHORT);
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent=new Intent();
		intent.setClass(HTTPRequestActivity.this,Invitation_Detail.class );
		HashMap <String,Object> temp=null;
		if(judge==0){
		 temp =newlist.get(position);
		}
		else if(judge==1)
		{
			 temp =Hotlist.get(position);
		}
		String titletext=(String)temp.get("title");
		String contenttext=(String)temp.get("content");
		String username=(String)temp.get("username");
		String photo=(String)temp.get("photourl");
		String picture_large_url=(String)temp.get("picture_large_url");
		intent.putExtra("title", titletext); 
		intent.putExtra("content", contenttext);
		intent.putExtra("username", username);
		intent.putExtra("photourl", photo);
		intent.putExtra("picture_large_url", picture_large_url);
		
		HTTPRequestActivity.this.startActivity(intent);
	}
	
	private class ButtonBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			
			if(action.equals(ButtonAction))
			{			
				more.setVisibility(View.GONE);
				bar.setVisibility(View.VISIBLE);
			}   		
		}
	}
	
	private class BackBroadcast extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action=intent.getAction();
			
			if(action.equals(BarAction))
			{ 
				bar.setVisibility(View.GONE);
				more.setVisibility(View.VISIBLE);   
			}		
		}
	}
}