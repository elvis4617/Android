package elvis.game.cognitive;

import elvis.game.cognitive.utils.MixedConstant;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class SetCongratulation extends Activity implements OnClickListener, OnErrorListener, OnCompletionListener{

	private Button toNextBlock;
	private TextView congra;
	private VideoView mVideoView;
	private MediaController mMediaController;
	private SharedPreferences mGameSettings;
	
	private Uri mUri;
    private int mPositionWhenPaused = -1;
    
	private int blockCounter;
	private int hyperCounter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mGameSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, 0);
		
		setContentView(R.layout.set_congratulation);
		
		toNextBlock = (Button) findViewById(R.id.toNextBlock);
		toNextBlock.setOnClickListener(this);
		
		congra = (TextView) findViewById(R.id.hyperCongra);
		
		mVideoView = (VideoView)findViewById(R.id.video);
		mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);
        blockCounter = mGameSettings.getInt("blockCounter", 0);
        
        if(blockCounter == 1)
        	mUri = Uri.parse("android.resource://elvis.game.cognitive/" + R.raw.movie_01);
        else if(blockCounter == 2)
        	mUri = Uri.parse("android.resource://elvis.game.cognitive/" + R.raw.movie_02);
        else if(blockCounter == 3)
        	mUri = Uri.parse("android.resource://elvis.game.cognitive/" + R.raw.movie_03);
        else if(blockCounter == 4)
        	mUri = Uri.parse("android.resource://elvis.game.cognitive/" + R.raw.movie_04);
        else if(blockCounter == 5)
        	mUri = Uri.parse("android.resource://elvis.game.cognitive/" + R.raw.movie_05);
        
		mVideoView.setVisibility(VideoView.VISIBLE);
		congra.setText("final congratulation");
		
		if(blockCounter == MixedConstant.BLOCK_NUMBER)
			congra.setText("finished experimence");
		
		Log.i("block number congra", blockCounter+"");
		
	}

	@Override
	public void onClick(View v) {
		/*if(blockCounter == 4 && hyperCounter == 4){
			//final complete activity request
			Intent i = new Intent(this, MixedColorMenuActivity.class);
			startActivity(i);
		}else{*/
		Intent i;
		if(blockCounter == MixedConstant.BLOCK_NUMBER) {
			i = new Intent(this, Go.class);	
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			mGameSettings.edit().putString("subjectBase64", "").commit();
			mGameSettings.edit().putInt("blockCounter", 0).commit();
			mGameSettings.edit().putInt("hyperCounter", 0).commit();
			mGameSettings.edit().putInt("setCounter", 0).commit();
			
		}else {
			i = new Intent(this, MixedColorActivity.class);	
		}
		startActivity(i);
	}
	

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		mVideoView.setVideoURI(mUri);
        mVideoView.start();
 
		super.onStart();
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		 if(mPositionWhenPaused >= 0) {
	            mVideoView.seekTo(mPositionWhenPaused);
	            mPositionWhenPaused = -1;
	        }
		 
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(hyperCounter == 0){
			mPositionWhenPaused = mVideoView.getCurrentPosition();
	        mVideoView.stopPlayback();
	        Log.d("OnStop", "OnStop: mPositionWhenPaused = " + mPositionWhenPaused);
	        Log.d("OnStop", "OnStop: getDuration  = " + mVideoView.getDuration());
		}
        
		super.onPause();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		 this.finish();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

}
