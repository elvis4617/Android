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
	private int setCounter;
	
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
		
		/*Bundle bundle = this.getIntent().getExtras();
		
		blockCounter = Integer.parseInt(bundle.getString("blockCounter"));
		hyperCounter = Integer.parseInt(bundle.getString("hyperCounter"));
		setCounter = Integer.parseInt(bundle.getString("setCounter"));*/
/*		Log.i("text set", bundle.getString("setCounter"));
		Log.i("text block", bundle.getString("blockCounter"));
		*/
		toNextBlock = (Button) findViewById(R.id.toNextBlock);
		toNextBlock.setOnClickListener(this);
		
		congra = (TextView) findViewById(R.id.hyperCongra);
		
		mVideoView = (VideoView)findViewById(R.id.video);
		mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);
        
        mUri = Uri.parse("android.resource://elvis.game.cognitive/" + R.raw.movie_01);
        
		/*Log.i("set number congra", setCounter+"");*/
        /*if(blockCounter == 4 && hyperCounter == 4){
        	Log.i("first", "first");
        	congra.setText("Congratulation, You secceed!");
        }else if(hyperCounter == 4) {
        	Log.i("Second", "Second");*/
        blockCounter = mGameSettings.getInt("blockCounter", 0);
			mVideoView.setVisibility(VideoView.VISIBLE);
			congra.setText("final congratulation");
			if(blockCounter == MixedConstant.BLOCK_NUMBER)
				congra.setText("finished experimence");
			/*blockCounter++;
			hyperCounter = 0;
			setCounter = 0;
		}else if(setCounter == 4) {
			Log.i("third", "third");
			congra.setText("congratulation");
			hyperCounter++;
			setCounter = 0;
		}*/
		
		/*Log.i("hyper number congra", hyperCounter+"");
		Log.i("block number congra", blockCounter+"");*/
		
	}

	@Override
	public void onClick(View v) {
		/*if(blockCounter == 4 && hyperCounter == 4){
			//final complete activity request
			Intent i = new Intent(this, MixedColorMenuActivity.class);
			startActivity(i);
		}else{*/
		Intent i;
		if(blockCounter == MixedConstant.BLOCK_NUMBER) i = new Intent(this, Go.class);		
		else i = new Intent(this, MixedColorActivity.class);		
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
