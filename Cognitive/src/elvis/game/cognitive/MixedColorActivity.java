package elvis.game.cognitive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.Trials;

public class MixedColorActivity extends Activity {

	private DBManager mgr;
	private BroadcastReceiver mBatInfoReceiver;
	private int orientation;

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
		
		Intent i = getIntent();
		String name = i.getStringExtra("subject");
		
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();
		float rate = /*screenWidth / 800*/1f;
		
		Log.i("width", screenWidth+"");
		Log.i("height", display.getHeight()+"");
		Log.i("rate", rate+"");
		if (screenWidth > screenHeight) {  
		      orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;  
		} else {  
			orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;  
		}  
		
		final MixedColorView view = new MixedColorView(MixedColorActivity.this, rate, orientation, name);
		Log.i("orientation", orientation+"");
		setContentView(view);
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
