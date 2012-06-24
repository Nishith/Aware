package cmu.nishith.aware;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class AwareActivity extends Activity {
	
	MediaRecorder recorder = new MediaRecorder();
	TextView t;
	Timer inputMonitorTimer;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile("/dev/null");
        try {
			recorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called when the start button is clicked
    public void startMonitor(View view) {
//        These are the listeners that are called when either an error or a warning occurs
//        recorder.setOnErrorListener(errorListener);
//        recorder.setOnInfoListener(infoListener);
	
        try {
        	recorder.start();
        } catch (IllegalStateException e) {
        	e.printStackTrace();
        }
        
        
        
        class monitorInputVolume extends TimerTask {

			@Override
			public void run() {
				int vol = recorder.getMaxAmplitude();
				SeekBar cutoff = (SeekBar)findViewById(R.id.cutoffVolume);
				int cutoffVolume = cutoff.getProgress(); 
				Log.d(getLocalClassName(), "cutoffVolume: " + cutoffVolume + ", current Volume: " + vol);
			    if (vol > cutoffVolume){
			    	Log.d(getLocalClassName(), "Pausing the player. " + vol);
			    	pausePlayer();
			    }
			}
        }
        
        t = new TextView(this);
        t = (TextView) findViewById(R.id.status);
        t.setText("Started");
        
        TimerTask displayVolume = new monitorInputVolume();
        //inputMonitorTimer.cancel();
        inputMonitorTimer = new Timer();
        long delay = 100;
        inputMonitorTimer.schedule(displayVolume, 0, delay);
    }
    
    public void stopMonitor(View view) {
    	recorder.stop();
    	recorder.reset();
    	recorder.release();
    	recorder = null;
    	
    	inputMonitorTimer.cancel();
    	t = new TextView(this);
        t = (TextView) findViewById(R.id.status);
        t.setText("Stopped");
    }    
    
    public void pausePlayer()
    {
    	 AudioManager mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);    

    	 if (mAudioManager.isMusicActive()) {

    		 Intent i = new Intent("com.android.music.musicservicecommand");

    		 i.putExtra("command", "pause");
    		 AwareActivity.this.sendBroadcast(i);
    	 }
    }
}