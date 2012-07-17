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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
//import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;




public class HTTPRequestActivity extends ListActivity {
    /** Called when the activity is first created. */
	private ListView listview=null;
	private String result="";
	private String url="http://www.bling0.com/all/latest.json";
	private ArrayList<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();
	private HashMap<String,String> map=null;
	private Button more=null;
	private ProgressBar bar =null;
	private Integer page=null;
	private Integer count=1;   
	private SimpleAdapter listadapter=null;
	private ButtonBroadcast buttonchange=null;
	private BackBroadcast buttonback=null;
	private IntentFilter filter1;
	private IntentFilter filter2;
	private LinearLayout lin=null;
	private Handler handler=null;
	private Intent intent1=null;
	private Intent intent2=null;
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
        
        listview=getListView();
        listview.addFooterView(loadingLayout);
        CallWebService();	
        listadapter=new SimpleAdapter(this,list ,R.layout.user,new String[]{"title","content"},new int []{R.id.title,R.id.content});
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
    }
       
   @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		HTTPRequestActivity.this.unregisterReceiver(buttonchange);
		HTTPRequestActivity.this.unregisterReceiver(buttonback);
	}
    
	public void CallWebService()
	{
		HttpClient client=new DefaultHttpClient();
		HttpGet request=new HttpGet(geturl());
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
		  HandleJson();
	}
	public void HandleJson() 
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
			String content=top_post.getString("content");
			
			map=new HashMap<String,String>();
			if(title=="null")
			{
				map.put("title","无");
			}
			else
			{
			map.put("title",title);
			}
			map.put("content", content);
			list.add(map);	
//			if(count!=1)
//			{
//				listadapter.notifyDataSetChanged();
//			}
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
		else
		{
		String pagecount=count.toString();
		String url="http://www.bling0.com/all/latest/page/"+pagecount+".json";
		seturl(url);
		CallWebService();
		}
		
	}

	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent=new Intent();
		intent.setClass(HTTPRequestActivity.this,Invitation_Detail.class );
		HashMap <String,String> temp =list.get(position);
		String titletext=temp.get("title");
		String contenttext=temp.get("content");
		
		intent.putExtra("title", titletext); 
		intent.putExtra("content", contenttext);
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
//				Log.i("aaaaaaaaaaaaaaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaa");
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
//				Log.i("bbbbbbbbbbbbbbbbb", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
				bar.setVisibility(View.GONE);
				more.setVisibility(View.VISIBLE);
			}		
		}
	}
}