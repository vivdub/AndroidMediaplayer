package com.mymedia;

import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.mymedia.R;

import com.Audio.AudioPlayer;
import com.Audio.AudioPlayer.AudioPlayerListener;

//==================================================================================
public class MyMediaPlayer extends Activity {
    /** Called when the activity is first created. */
    
	public static final String AudioUrls = "audioUrls";
	
	private String[] audioArr  = {
			
			"http://sound25.mp3pk.com/indian/singham/singham04(www.songs.pk).mp3",
			"http://sound25.mp3pk.com/indian/singham/singham03(www.songs.pk).mp3",
			"http://sound25.mp3pk.com/indian/singham/singham06(www.songs.pk).mp3"
		};
	
	private SeekBar _seekbar;
	private TextView _title;
	private Button _play, _next, _prev, _stop;
	
	
	/*============= used for cal purpose ========================== */
	private int _trackTime, _currentTrackPost;
	private Thread _seekMonitoringThread;
	private boolean _interuptSeek = false, _playing = true, _stopped = false;
	
	
	private int _tmpPlayerPost = 0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_media_layout);
        
        
        initialize();
        startAudioPool();
        checkNextTrackAvailability();
        checkPrevTrackAvailability();
    }
    
	//===============================================
	private void initialize(){
		
		try{
			
			initializeAudioUrl();
			
			_seekbar = (SeekBar)findViewById(R.id.seekbar);
			_title = (TextView)findViewById(R.id.title);
			_play = (Button)findViewById(R.id.play);
			_next = (Button)findViewById(R.id.next);
			_prev = (Button)findViewById(R.id.prev);
			_stop = (Button)findViewById(R.id.stop);
			
			_prev.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					playPrevTrack();
				}
			});
			
			_next.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					playNextTrack();
				}
			});
			
			_play.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					playPausePlayback();
				}
			});
			
			_stop.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					_stopped = true;
					_playing = false;
					stopAudioTrackplayback();
				}
			});
			
			int length = audioArr.length;
			for(int i=0; i<length; i++)
				AudioPlayer.addAudioInPool(i+"", audioArr[i]);
			
			_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					
					if(fromUser)
						performActionOnSeekChanged(progress);
				}
			});
			
			_seekMonitoringThread = new Thread(){
				public void run(){
					
					try{
						
						while(true){
							
							monitorSeek();
							Thread.sleep(1000);
							
							if(_interuptSeek)
								break;
						}
						
					}catch(Exception e){e.printStackTrace();}
				}
			};
			
			_seekMonitoringThread.start();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void initializeAudioUrl(){
		
		try{
			
			Bundle b = getIntent().getExtras();
			if(b.containsKey(AudioUrls))
				audioArr = b.getStringArray(AudioUrls);
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void monitorSeek(){
		
		try{
			
			int pos = AudioPlayer.getMediaPlayer().getCurrentPosition();
			
			if(pos!=_tmpPlayerPost){
				
				_tmpPlayerPost = pos;
				
				if(_trackTime!=0){
					
					final int seek = (_tmpPlayerPost*100)/_trackTime;
					
					//Log.i("----- pos = --------", pos+"        track time = "+_trackTime+"  calc seek = "+seek);				
					
					runOnUiThread(new Runnable(){
						public void run(){
							
							_seekbar.setProgress(seek);
						}
					});
				}
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void checkNextTrackAvailability(){
		
		try{
			
			if(AudioPlayer.isNextAudioAvailable())
				_next.setEnabled(true);
			else
				_next.setEnabled(false);
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void playNextTrack(){
		
		try{
			
			AudioPlayer.playNextAudio();
			checkNextTrackAvailability();
	        checkPrevTrackAvailability();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void stopAudioTrackplayback(){
		
		try{
			
			AudioPlayer.getMediaPlayer().stop();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void checkPrevTrackAvailability(){
		
		try{
			
			if(AudioPlayer.isPreviousAudioAvailable())
				_prev.setEnabled(true);
			else
				_prev.setEnabled(false);
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void playPrevTrack(){
		
		try{
			
			AudioPlayer.playPreviousAudio();
			checkNextTrackAvailability();
	        checkPrevTrackAvailability();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void playPausePlayback(){
		
		try{
			
			Log.i("............................... player", "starting from index 0  playing = "+_playing+"   stopped =  "+_stopped);
			
			
			if(_playing){
				
				AudioPlayer.getMediaPlayer().pause();
				_playing = false;
			}else{
				
				if(_stopped){
					
					AudioPlayer.playAllAudioPool();
					_stopped = false;
				}
				else{
					
					AudioPlayer.getMediaPlayer().start();
					_playing = true;
				}
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void performActionOnSeekChanged(int seekpos){
		
		try{
			
			int trackSeek = (seekpos*_trackTime)/100;
			
			Log.i("advanced to = ", trackSeek+"           ;");
			
			AudioPlayer.getMediaPlayer().seekTo(trackSeek);
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void startAudioPool(){
		
		try{
			
			AudioPlayer.setAudioPlayerListener(new AudioPlayerListener() {
				
				@Override
				public void onAudioPoolPlaybackCompleted() {
					
					
				}
				
				@Override
				public void onAudioPlaybackStarted(String url) {
					
					performActionOnPlaybackStarted(url);
				}
				
				@Override
				public void onAudioPlaybackCompleted(String url) {
					
					
				}
			});
			
			AudioPlayer.playAllAudioPool();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	private void performActionOnPlaybackStarted(String url){
		
		try{
			
			_trackTime = AudioPlayer.getMediaPlayer().getDuration();
			_seekbar.setMax(100);
			_seekbar.setProgress(0);
			_title.setText(url.substring( url.lastIndexOf('/')+1, url.length()));
			checkNextTrackAvailability();
	        checkPrevTrackAvailability();
			
		}catch(Exception e){e.printStackTrace();}
	}
	
	//===============================================
	@Override
	public void onBackPressed(){
		
		super.onBackPressed();
		
		try{
			
			AudioPlayer.stopAllAudioPool();
			if(_seekMonitoringThread!=null){
				
				_interuptSeek = true;
				_seekMonitoringThread.interrupt();
			}
			
		}catch(Exception e){e.printStackTrace();}
	}
}