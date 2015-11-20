package elvis.game.cognitive;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.utils.MixedConstant;

public class Prefs extends Activity implements OnClickListener{

	private SharedPreferences mBaseSettings;
	private Button clear;
	private DBManager mgr;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.options);

		clear = (Button) findViewById(R.id.clear_data);
		mgr = new DBManager(this);
		clear.setOnClickListener(this);
		
		mBaseSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, 0);
		
		CheckBox vibrateCheckbox = (CheckBox) findViewById(R.id.options_vibrate_checkbox);
		vibrateCheckbox.setChecked(mBaseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_VIBRATE, true));
		vibrateCheckbox
				.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_VIBRATE,
											true).commit();
						} else {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_VIBRATE,
											false).commit();
						}
					}
				});

		CheckBox soundsCheckbox = (CheckBox) findViewById(R.id.options_sounds_checkbox);
		soundsCheckbox.setChecked(mBaseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_SOUNDS, true));
		soundsCheckbox
				.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_SOUNDS,
											true).commit();
						} else {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_SOUNDS,
											false).commit();
						}
					}
				});
		
		CheckBox debug = (CheckBox) findViewById(R.id.debug);
		debug.setChecked(mBaseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_DEBUG, true));
		debug.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_DEBUG,
											true).commit();
						} else {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_DEBUG,
											false).commit();
						}
					}
				});
		
		RadioGroup modeGroup = (RadioGroup)this.findViewById(R.id.modeRadioGroup);
		
		final RadioButton easy = (RadioButton)findViewById(R.id.radioEasy);
		final RadioButton hard = (RadioButton)findViewById(R.id.radioHard);
		
		if(mBaseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_HARDMODE, true)) hard.setChecked(true);
		else easy.setChecked(true);
		
		modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.i("onChange","ONCHANGE");
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) findViewById(radioButtonId);
				if(rb.getText().equals("Easy Mode")){
					mBaseSettings.edit().putBoolean(
							MixedConstant.PREFERENCE_KEY_HARDMODE, false).commit();
				}else if(rb.getText().equals("Hard Mode")){
					mBaseSettings.edit().putBoolean(
							MixedConstant.PREFERENCE_KEY_HARDMODE, true).commit();
				}
				
			}
			
		});
		
		RadioGroup sequanceGroup = (RadioGroup)this.findViewById(R.id.sequanceRadioGroup);
		
		final RadioButton s1 = (RadioButton)findViewById(R.id.radioSet1);
		final RadioButton s2 = (RadioButton)findViewById(R.id.radioSet2);
		
		if(mBaseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_SEQUENCE, true)) s1.setChecked(true);
		else s2.setChecked(true);
		
		sequanceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) findViewById(radioButtonId);
				if(rb.getText().equals("Sequance 1")){
					mBaseSettings.edit().putBoolean(
							MixedConstant.PREFERENCE_KEY_SEQUENCE, true).commit();
				}else if(rb.getText().equals("Sequance 2")){
					mBaseSettings.edit().putBoolean(
							MixedConstant.PREFERENCE_KEY_SEQUENCE, false).commit();
				}
				
			}
			
		});

	}

	@SuppressLint("InflateParams")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.clear_data:
			LayoutInflater factory = LayoutInflater.from(this);
			final View dialogView = factory.inflate(
					R.layout.alertdialog, null);
			dialogView.setFocusableInTouchMode(true);
			dialogView.requestFocus();
			
			final AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();
			dialog.setCancelable(false);
			
			dialog.show();
			final Button text = (Button) dialogView.findViewById(R.id.password_exit);
			text.setText("clear");
			
			dialogView.findViewById(R.id.password_exit).setOnClickListener(
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							final EditText pw = (EditText) dialogView.findViewById(R.id.password);
							
							if(pw.getText().toString().equals(MixedConstant.PREFERENCE_KEY_PASSWORD)){
								mgr.clear();
								Toast.makeText(getApplicationContext(), "Database cleared.", Toast.LENGTH_SHORT).show(); 
								dialog.dismiss();
							}
						}
					});
			
			dialogView.findViewById(R.id.password_cancel).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			break;
		}
		
	}

}
