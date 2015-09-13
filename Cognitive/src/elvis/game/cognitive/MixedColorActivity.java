package elvis.game.cognitive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.Trials;

public class MixedColorActivity extends Activity {

	private DBManager mgr;
	private BroadcastReceiver mBatInfoReceiver;

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

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenWidth = display.getWidth();
		float rate = screenWidth / 320f;

		final MixedColorView view = new MixedColorView(MixedColorActivity.this, rate);

		setContentView(view);
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
		            	mgr.add(new Trials(view.getTrialCounter(), view.getmUIThread().getmUIModel().getmStageCounter() + 1,
								view.getmUIThread().getmUIModel().getChT()[view.getmUIThread().getmUIModel().getmStageCounter()], 
								view.getmUIThread().getmUIModel().getMvT()[view.getmUIThread().getmUIModel().getmStageCounter()]));
		            	
		            	finish();
		            }
				}  
		    };  
		    
		    registerReceiver(mBatInfoReceiver, filter);  
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
}
