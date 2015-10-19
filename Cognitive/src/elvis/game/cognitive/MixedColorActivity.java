package elvis.game.cognitive;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.TimeRecorder;
import elvis.game.cognitive.utils.MixedConstant;

public class MixedColorActivity extends Activity {

	private SharedPreferences mGameSettings;
	private BroadcastReceiver mBatInfoReceiver;
	
	private DBManager mgr;
	
	private int orientation;
	private int blockCounter;
	private int hyperCounter;
	private int setCounter;
	
	private TimeRecorder subject;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mgr = new DBManager(this);
		mGameSettings = getSharedPreferences(
					MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, 0);
		try{
			String personBase64 = mGameSettings.getString("subjectBase64", ""); 
			byte[] base64Bytes = Base64.decodeBase64(personBase64.getBytes()); 
			ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes); 
			ObjectInputStream ois = new ObjectInputStream(bais);  
			subject = (TimeRecorder) ois.readObject();  
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
		Bundle bundle = this.getIntent().getExtras();
		
		Log.i("bundle", (bundle == null)+"");
		
		if(bundle == null){
			blockCounter = 0;
			hyperCounter= 0;
			setCounter = 0;
		}else{
			blockCounter = bundle.getString("blockCounter") == null ? 0 : Integer.parseInt(bundle.getString("blockCounter"));
			hyperCounter = bundle.getString("hyperCounter") == null ? 0 : Integer.parseInt(bundle.getString("hyperCounter"));
			setCounter = bundle.getString("setCounter") == null ? 0 : Integer.parseInt(bundle.getString("setCounter"));
		}
		
		Log.i("hyper number", hyperCounter+"");
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		float rate = /*screenWidth / 800*/1f;
		
		if (screenWidth > screenHeight) {  
		      orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;  
		} else {  
			orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;  
		}  
		
		//final MixedColorView view = new MixedColorView(MixedColorActivity.this, rate, orientation, blockCounter, hyperCounter, setCounter);
		setContentView(R.layout.game_layout/*view*/);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
		
		mgr.clear();
		
		 final IntentFilter filter = new IntentFilter();  
		 filter.addAction(Intent.ACTION_SCREEN_OFF);  
		 filter.addAction(Intent.ACTION_USER_PRESENT);  
		 
		 mBatInfoReceiver = new BroadcastReceiver() {  

				@Override
				public void onReceive(Context context, Intent intent) {
					// TODO Auto-generated method stub
					String action = intent.getAction(); 

		            if (Intent.ACTION_SCREEN_OFF.equals(action)) {  
		            	/*mgr.add(new Trials(view.getTrialCounter(), view.getmUIThread().getmUIModel().getmStageCounter() + 1,
								view.getmUIThread().getmUIModel().getChT()[view.getmUIThread().getmUIModel().getmStageCounter()], 
								view.getmUIThread().getmUIModel().getMvT()[view.getmUIThread().getmUIModel().getmStageCounter()]));*/
		            	
		            	finish();
		            }
				}  
		    };  
		    
		    registerReceiver(mBatInfoReceiver, filter);  
		   
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			   return true;
		 }
		return super.onKeyDown(keyCode, event);
	}



	@Override
	protected void onResume() {  
		orientation = ActivityInfo.SCREEN_ORIENTATION_USER;  
		this.setRequestedOrientation(orientation);  
		Display display = getWindowManager().getDefaultDisplay();  
		int width = display.getWidth();  
		int height = display.getHeight();  
		if (width > height) {  
			orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;  
		} else {  
			orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;  
		}  
		super.onResume();  
	}  
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("destrop", "destroy");
		super.onDestroy();
		mgr.closeDB();  
	}
	
	@Override
	public void finish() {
		unregisterReceiver(mBatInfoReceiver);  
		super.finish();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {  
			super.onConfigurationChanged(newConfig);  
			this.setRequestedOrientation(orientation);  
	}  
}
