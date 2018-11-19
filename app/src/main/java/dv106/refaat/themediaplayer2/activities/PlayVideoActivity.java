
package dv106.refaat.themediaplayer2.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import dv106.refaat.themediaplayer2.R;

public class PlayVideoActivity extends Activity implements OnCompletionListener {

	private VideoView video;
	private MediaController controller;


	private InterstitialAd interstitial;

	private void displayInterstitial() {
		if (interstitial.isLoaded()) {
			interstitial.show();

		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play_video);
		video = (VideoView) findViewById(R.id.vwPlayVideo);
		controller = new MediaController(this);


		AdRequest adRequest1 = new AdRequest.Builder().build();
		interstitial = new InterstitialAd(PlayVideoActivity.this);
		interstitial.setAdUnitId("ca-app-pub-8493173606515444/6527547509");
		interstitial.loadAd(adRequest1);
		interstitial.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				displayInterstitial();
			}
		});





		Bundle extras = getIntent().getExtras();
		if(extras != null){
			String videoPath = extras.getString("videoPath");
			video.setVideoPath(videoPath);
			controller.setMediaPlayer(video);
			video.setMediaController(controller);
			video.requestFocus();
			video.start();
			
			if(savedInstanceState != null){
				video.seekTo(savedInstanceState.getInt("currentPos"));
			}
		}	
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(video.isPlaying()){
			outState.putInt("currentPos", video.getCurrentPosition());
		}	
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
	}

}
