package com.screentest.videoplayer;

import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.widget.MediaController;
import android.widget.VideoView;

public class ScreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_screen);
//		
//    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//   	 final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");

		VideoView vidView = (VideoView)findViewById(R.id.videoView1);
		
//		vidView.setPlayPauseListener(new MyVideoView.PlayPauseListener() {
//
//		    @Override
//		    public void onPlay() {
//		    	
//		    	 wl.acquire();
//		    	
//		    }
//
//		    @Override
//		    public void onPause() {
//		    	
//		    }
//		});

			
		
		String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
		Uri vidUri = Uri.parse(vidAddress);
		vidView.setVideoURI(vidUri);
		
		MyMediaController vidControl = new MyMediaController(this);
		
		vidControl.setAnchorView(vidView);
		vidView.setMediaController(vidControl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.screen, menu);
		return true;
	}

}


 class MyMediaController extends MediaController {
    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MyMediaController(Context context) {
        super(context);
    }

    @Override
    public void show(int timeout) {
        super.show(0);
    }
    
  

}
 
  class MyVideoView extends VideoView {

	    private PlayPauseListener mListener;

	    public MyVideoView(Context context) {
	        super(context);
	    }

	    public MyVideoView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	    }

	    public void setPlayPauseListener(PlayPauseListener listener) {
	        mListener = listener;
	    }

	    @Override
	    public void pause() {
	        super.pause();
	        if (mListener != null) {
	            mListener.onPause();
	        }
	    }

	    @Override
	    public void start() {
	        super.start();
	        if (mListener != null) {
	            mListener.onPlay();
	        }
	    }

	    public static interface PlayPauseListener {
	        void onPlay();
	        void onPause();
	    }

	}
 
 
