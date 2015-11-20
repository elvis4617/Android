package elvis.game.cognitive;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.TimeRecorder;
import elvis.game.cognitive.utils.MixedConstant;

public class Go extends Activity implements OnClickListener {

	private EditText name;
	private Button go;
	private Button go_Back;
	private SharedPreferences mGameSettings;
	private List<TimeRecorder> subjects;
	
	private int blockCounter;
	
	private DBManager mgr;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.go);
		mgr = new DBManager(this);
		
		mGameSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, 0);
		this.blockCounter = mGameSettings.getInt("blockCounter", 0);
		
		name = (EditText) findViewById(R.id.name);
		go = (Button) findViewById(R.id.go_button);
		go_Back = (Button) findViewById(R.id.go_Back);
		go.setOnClickListener(this);
		go_Back.setOnClickListener(this);
		
		findViewById(R.id.go_layout).setOnClickListener(this);
		//name.setOnTouchListener(this);
		
		if(blockCounter == 0)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_backup));
		else if(blockCounter == 1)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingfood1));
		else if(blockCounter == 2)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingfriend1));
		else if(blockCounter == 3)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settingcold1));
		else if(blockCounter == 4)
			this.getWindow().getDecorView().setBackgroundDrawable(getResources().getDrawable(R.drawable.settinghome));
		
		subjects = new ArrayList<TimeRecorder>();
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("Go destroy", "destroy");
		super.onDestroy();
		mGameSettings.edit().putString("subjectBase64", "").commit();
		mGameSettings.edit().putString("subject_name", "").commit();
		mGameSettings.edit().putInt("blockCounter", 0).commit();
		mGameSettings.edit().putInt("hyperCounter", 0).commit();
		mGameSettings.edit().putInt("setCounter", 0).commit();
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.go_layout:
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
		        imm.hideSoftInputFromWindow(name.getWindowToken(), 0);  
		        break;
			case R.id.go_button:
				if(!name.getText().toString().trim().isEmpty()){
					Log.i("edittext name", name.getText().toString());
					Log.i("subject initial", subjects.toString());
					
					List<TimeRecorder> list = mgr.queryForSubjects(name.getText().toString());
					Log.i("list",list.toString());
					Log.i("list empty",list.isEmpty()+"");
					if(!list.isEmpty())
						Toast.makeText(getApplicationContext(), "Subject already exist.", Toast.LENGTH_SHORT).show(); 
					else{
						MixedConstant.saveObject(this, subjects, MixedConstant.SUBJECT_NAME);
						mGameSettings.edit().putString("subject_name", name.getText().toString()).commit();
						Intent i = new Intent(this, MixedColorActivity.class);
						startActivity(i);
					}
				}else{
					Toast.makeText(getApplicationContext(), "Please enter subject name.", Toast.LENGTH_SHORT).show();     
				}
				break;
			case R.id.go_Back:
				finish();
				break;
		}
	}
	
	@Override
	public void finish() {  
		super.finish();
	}
	
}
