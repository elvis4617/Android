package elvis.game.cognitive;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import elvis.game.cognitive.data.TimeRecorder;
import elvis.game.cognitive.utils.MixedConstant;

public class Go extends Activity implements OnClickListener {

	private EditText name;
	private Button go;
	private SharedPreferences mGameSettings;
	private SharedPreferences mBaseSettings;
	private TimeRecorder subject;
	
	private int BLOCK_NUMBER = 5;
	private int HYPER_NUMBER = 5;
	private int SET_NUMBER = 5;
	
	private int blockCounter;
	private int hyperCounter;
	private int setCounter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.go);
		
		mGameSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, 0);
		mBaseSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, 0);
		
		this.setCounter = mGameSettings.getInt("setCounter", 0);
		this.hyperCounter = mGameSettings.getInt("hyperCounter", 0);
		this.blockCounter = mGameSettings.getInt("blockCounter", 0);
		
		name = (EditText) findViewById(R.id.name);
		go = (Button) findViewById(R.id.go_button);
		go.setOnClickListener(this);
		
		if(blockCounter == 0)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingrain1));
		else if(blockCounter == 1)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingfood1));
		else if(blockCounter == 2)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingfriend1));
		else if(blockCounter == 3)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingcold1));
		else if(blockCounter == 4)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settinghome));
		
		
		if(mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_HARDMODE, false) && !mGameSettings.getString("subjectBase64", "").equals("")){
			subject = (TimeRecorder) MixedConstant.getObjectInfo(this);
			name.setText(subject.getSubjectID());
		}else subject = new TimeRecorder(BLOCK_NUMBER, HYPER_NUMBER, SET_NUMBER);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("Go destroy", "destroy");
		super.onDestroy();
		mGameSettings.edit().putString("subjectBase64", "").commit();
		mGameSettings.edit().putInt("blockCounter", 0).commit();
		mGameSettings.edit().putInt("hyperCounter", 0).commit();
		mGameSettings.edit().putInt("setCounter", 0).commit();
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.go_button:
				subject.setSubjectID(name.getText().toString());
				
				Log.i("edittext name", name.getText().toString());
				Log.i("subject initial", subject.toString());
				
				MixedConstant.saveObject(this, subject, MixedConstant.SUBJECT_NAME);
				
				Intent i = new Intent(this, MixedColorActivity.class);
				startActivity(i);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == KeyEvent.KEYCODE_BACK){
			 onDestroy();
			 startActivity(new Intent(this, MixedColorMenuActivity.class));
			 return true;
		 }
		return super.onKeyDown(keyCode, event);
	}
	
}
