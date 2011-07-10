package com.mymedia;

import java.net.URLEncoder;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//==================================================================================
public class AudioPlayerActivity {

	private HashMap<String, String> _urls = new HashMap<String, String>();
	private Context _context;
	
	//===============================================
	public AudioPlayerActivity(Context context){
		
		_context = context;
	}
	
	//===============================================
	public void add(String url){
		
		try{
			
			_urls.put(_urls.keySet().size()+"", url);
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public void clear(){
		
		_urls = new HashMap<String, String>();
	}
	
	//===============================================
	public void start(){
		
		try{
			
			Bundle b = new Bundle();
			
			String[] urls = _urls.values().toArray(new String[]{});
			b.putStringArray(MyMediaPlayer.AudioUrls, urls);
			
			Intent i = new Intent(_context, MyMediaPlayer.class);
			i.putExtras(b);
			_context.startActivity(i);
			
		}catch(Exception e){e.printStackTrace();}
	}
}
