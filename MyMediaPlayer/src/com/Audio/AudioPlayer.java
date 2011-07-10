package com.Audio;

import java.util.HashMap;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

//==================================================================================
public class AudioPlayer {

	private static MediaPlayer _mediaPlayerCommon;
	private static String _audioUrl = null;
	private static HashMap<String, String> _multipleUrls ;
	private static AudioPlayerListener _commonplayerListener;
	private static boolean _interruptedStop = false;
	private static int _currentCommonUrlIndex = -1;
	
	private MediaPlayer _mediaPlayer;
	
	//===============================================
	public AudioPlayer(){
		
		_mediaPlayer = new MediaPlayer();
	}
	
	//===============================================
	public static MediaPlayer getMediaPlayer(){
		
		if(_mediaPlayerCommon == null)
			_mediaPlayerCommon = new MediaPlayer();
		
		return _mediaPlayerCommon;
	}
	
	//===============================================
	public static boolean isPlayingUrl(String audioUrl){
		
		if(_audioUrl!=null && _audioUrl.equals(audioUrl))
			return true;
		else
			return false;
	}
	
	//===============================================
	public void play(String audioUrl){
		
		try{
			
			if(_mediaPlayer!=null && _mediaPlayer.isPlaying())
				_mediaPlayer.stop();
			
			if(_mediaPlayer==null || !_mediaPlayer.isPlaying()){
				
				_mediaPlayer = new MediaPlayer();
				_mediaPlayer.setDataSource(audioUrl);
				_mediaPlayer.prepareAsync();
				_mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						
						mp.start();
					}
				});
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public void stop(){
		
		try{
			
			if(_mediaPlayer!=null && _mediaPlayer.isPlaying()){
			
				_mediaPlayer.stop();
				_mediaPlayer.release();
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void setAudioPlayerListener(AudioPlayerListener onAudioComplete){
		
		_commonplayerListener = onAudioComplete;
	}
	
	//===============================================
	public static void playAudio(final String audioUrl){
		
		try{
			
			if(_mediaPlayerCommon!=null && _mediaPlayerCommon.isPlaying())
				_mediaPlayerCommon.stop();
			
			if(_mediaPlayerCommon==null || !_mediaPlayerCommon.isPlaying()){
				
				_audioUrl = audioUrl;
				 _interruptedStop = false;
				_mediaPlayerCommon = new MediaPlayer();
				_mediaPlayerCommon.setDataSource(audioUrl);
				_mediaPlayerCommon.prepareAsync();
				_mediaPlayerCommon.setOnPreparedListener(new OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer mp) {
						
						if(!_interruptedStop)
							mp.start();
						else
							stopAudio();
					}
				});
				_mediaPlayerCommon.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						
						_audioUrl = null;
						if(_commonplayerListener!=null)
							_commonplayerListener.onAudioPlaybackCompleted(audioUrl);
					}
				});
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void stopAudio(){
		
		try{
			
			 _interruptedStop = true;
			 
			if(_mediaPlayerCommon!=null && _mediaPlayerCommon.isPlaying())
				_mediaPlayerCommon.stop();
			
			_audioUrl = null;
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void addAudioInPool(String key, String url){
		
		try{
			
			if(_multipleUrls == null)
				_multipleUrls = new HashMap<String, String>();
			
			_multipleUrls.put(key, url);
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void playAllAudioPool(){
		
		try{
			
			_interruptedStop = false;
			
			if(_multipleUrls!=null && _multipleUrls.size()>0){
				
				final String[] urls = _multipleUrls.values().toArray(new String[]{});
				int size = urls.length;				
				
				playUrlFromPool(urls, 0);
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void playAllAudioPool(int fromIndex){
		
		try{
			
			_interruptedStop = false;
			
			if(_multipleUrls!=null && _multipleUrls.size()>0){
				
				final String[] urls = _multipleUrls.values().toArray(new String[]{});
				int size = urls.length;				
				
				playUrlFromPool(urls, fromIndex);
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void playPreviousAudio(){
		
		try{
			
			if(_currentCommonUrlIndex!=0){
				
				_currentCommonUrlIndex--;
				playAllAudioPool(_currentCommonUrlIndex);
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static boolean isPreviousAudioAvailable(){
		
		try{
			
			if(_currentCommonUrlIndex!=0)
				return true;
			
		}catch(Exception e){e.printStackTrace();}
		
		return false;
	}
	
	//===============================================
	public static void playNextAudio(){
		
		try{
			
			if(_currentCommonUrlIndex<_multipleUrls.size()-1){
				
				_currentCommonUrlIndex++;
				playAllAudioPool(_currentCommonUrlIndex);
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static boolean isNextAudioAvailable(){
		
		try{
			
			if(_currentCommonUrlIndex<_multipleUrls.size()-1)
				return true;
			
		}catch(Exception e){e.printStackTrace();}
		
		return false;
	}
	
	//===============================================
	private static void playUrlFromPool(final String[] urls, final int index){
		
		try{
			
			if(index==urls.length){
			
				if(_commonplayerListener!=null)
					_commonplayerListener.onAudioPoolPlaybackCompleted();
				
				//stopAllAudioPool();
				return ;
			}
			
			MediaPlayer player = getMediaPlayer();
			if(player.isPlaying())
				player.stop();
			
			_currentCommonUrlIndex = index;
			
			player.reset();
			player.setDataSource(urls[index]);
			player.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					
					Log.i("00000000000000000000now going to play,   ", index+"        "+_interruptedStop);
					if(!_interruptedStop){
					
						if(_commonplayerListener!=null)
							_commonplayerListener.onAudioPlaybackStarted(urls[index]);
						
						mp.start();
					}
					else
						stopAudio();
				}
			});
			
			player.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					
					if(_commonplayerListener!=null)
						_commonplayerListener.onAudioPlaybackCompleted(urls[index]);
					
					if(index<urls.length)	
						playUrlFromPool(urls, index+1);
				}
			});
			
			player.prepareAsync();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static void stopAllAudioPool(){
		
		try{
			
			_interruptedStop = true;
			stopAudio();
			_multipleUrls = null;
			_audioUrl = null;
			_commonplayerListener = null;
			_interruptedStop = false;
			_currentCommonUrlIndex = -1;
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	public static boolean isAudioPoolRunning(){
		
		Log.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>multiple urls, mediaplayer comon, isplaying", _multipleUrls==null?"null":"not null"+"   "+_mediaPlayerCommon==null?"null":"not null"+"    "+_mediaPlayerCommon.isPlaying());
		
		if(_multipleUrls!=null && _mediaPlayerCommon!=null && _mediaPlayerCommon.isPlaying())
			return true;
		else
			return false;
	}
	
	
	//==================================================================================
	public interface AudioPlayerListener{
		
		public void onAudioPlaybackCompleted(String url);
		public void onAudioPoolPlaybackCompleted();
		public void onAudioPlaybackStarted(String url);
	}
}
