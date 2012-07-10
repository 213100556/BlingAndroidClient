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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;




public class HTTPRequestActivity extends ListActivity {
    /** Called when the activity is first created. */
	private ListView listview=null;
	private String result="";
	private String url="http://www.bling0.com/all/latest.json";
	private String tag="Your LogCat tag";
	private ArrayList<HashMap<String,String>> list;
	private HashMap<String,String> map=null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout lin=new LinearLayout(this);
        listview=new ListView(this);
        CallWebService();	
        SimpleAdapter listadapter=new SimpleAdapter(this,list ,R.layout.user,new String[]{"title","content"},new int []{R.id.title,R.id.content});
        setListAdapter(listadapter);   
    }
    

    
	public void CallWebService()
	{
		HttpClient client=new DefaultHttpClient();
		HttpGet request=new HttpGet(url);
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
		Log.i(tag, result);
		  HandleJson();
	}
	public void HandleJson() 
	{
		list=new ArrayList<HashMap<String,String>>();
		try{
		JSONTokener tokener=new JSONTokener(result);
		JSONObject object= (JSONObject)tokener.nextValue();
		Integer page=object.getInt("num_pages");
		String pages=page.toString();
		Log.i(tag,pages);
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
				map.put("title","нч");
			}
			else
			{
			map.put("title",title);
			}
			map.put("content", content);
			list.add(map);
			

		}
		
		
		
	}catch(JSONException e)
	{
		e.printStackTrace();
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
}