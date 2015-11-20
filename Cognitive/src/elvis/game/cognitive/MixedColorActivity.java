package elvis.game.cognitive;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.CurCell;
import elvis.game.cognitive.data.TimeRecorder;
import elvis.game.cognitive.utils.MixedConstant;

public class MixedColorActivity extends Activity implements OnClickListener{

	private Context mContext;
	private SharedPreferences mGameSettings;
	//private BroadcastReceiver mBatInfoReceiver;
	
	private DBManager mgr;
	private Button backToMenu;
	
	private int orientation;

	private MixedColorView view;
	
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
		
		setContentView(R.layout.game_layout);
		
		mContext = this;
		
		view = (MixedColorView) findViewById(R.id.mixed_color);
		
		 final IntentFilter filter = new IntentFilter();  
		 filter.addAction(Intent.ACTION_SCREEN_OFF);  
		 filter.addAction(Intent.ACTION_USER_PRESENT);  
		 
		/* mBatInfoReceiver = new BroadcastReceiver() {  

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
		    };  */
		    
		    //registerReceiver(mBatInfoReceiver, filter);  
		    backToMenu = (Button) findViewById(R.id.BackToMenu);
		    backToMenu.setOnClickListener(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i("key", keyCode+"");
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			   return true;
		 }
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		ActivityManager activityManager = (ActivityManager) getApplicationContext()
		        .getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.moveTaskToFront(getTaskId(), 0);
	}

	@Override
	protected void onResume() {  
		orientation = ActivityInfo.SCREEN_ORIENTATION_USER;  
		this.setRequestedOrientation(orientation);  
		super.onResume();  
	}  
	
	@Override
	protected void onDestroy() {
		Log.i("Mixed Color A destrop", "destroy");
		super.onDestroy();
		mgr.closeDB();  
	}
	
	@Override
	public void finish() {
		//unregisterReceiver(mBatInfoReceiver);  
		System.exit(0);
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {  
			super.onConfigurationChanged(newConfig);  
			this.setRequestedOrientation(orientation);  
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View dialogView = factory.inflate(
				R.layout.alertdialog, null);
		dialogView.setFocusableInTouchMode(true);
		dialogView.requestFocus();
		
		final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
		dialog.setCancelable(false);
		
		view.getmUIThread().setPause(true);
		view.getmUIThread().saveSubject();
		
		
		dialog.show();
		
		dialogView.findViewById(R.id.password_exit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						final EditText pw = (EditText) dialogView.findViewById(R.id.password);
						if(pw.getText().toString().equals(MixedConstant.PREFERENCE_KEY_PASSWORD)){
							List<TimeRecorder> list = (List<TimeRecorder>) MixedConstant.getObjectInfo(mContext);
							mgr.add(list);
							
							List<TimeRecorder> eles = mgr.queryForSubjects(mGameSettings.getString("subject_name", "unknow"));
							Log.i("subject name+++", mGameSettings.getString("subject_name", "unknow"));
							Log.i("eles", eles.toString());
							
							if(!eles.isEmpty()){
								Log.i("in", "in");
								List<CurCell> data = new ArrayList<CurCell>();
								for(TimeRecorder t : eles){
										data.add(new CurCell(eles.indexOf(t)+1, 0, t.getSubjectID()));
										data.add(new CurCell(eles.indexOf(t)+1, 1, t.getBlockCounter()+1+""));
										data.add(new CurCell(eles.indexOf(t)+1, 2, t.getHyperCounter()+1+""));
										data.add(new CurCell(eles.indexOf(t)+1, 3, t.getHomeKeyTime()));
										data.add(new CurCell(eles.indexOf(t)+1, 4, t.getSetCounter()+1+""));
										data.add(new CurCell(eles.indexOf(t)+1, 5, t.getSetLedOn()));
										data.add(new CurCell(eles.indexOf(t)+1, 6, t.getChT()+""));
										data.add(new CurCell(eles.indexOf(t)+1, 7, t.getMvT()+""));
								}
								MixedConstant.writeExcel(data, mGameSettings.getString("subject_name", "unknow"));
							}
							
							mGameSettings.edit().putString("subjectBase64", "").commit();
							mGameSettings.edit().putInt("blockCounter", 0).commit();
							mGameSettings.edit().putInt("hyperCounter", 0).commit();
							mGameSettings.edit().putInt("setCounter", 0).commit();
							
							dialog.dismiss();
							Intent i = new Intent(mContext, Go.class);
							i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							mContext.startActivity(i);
						}else Toast.makeText(mContext, "Password invalid.", Toast.LENGTH_SHORT).show(); 
					}
					
				});
		dialogView.findViewById(R.id.password_cancel).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						view.getmUIThread().getmUIModel().resume();
						view.getmUIThread().setPause(false);
					}
					
				});
				
		
	}  
	
	
}
