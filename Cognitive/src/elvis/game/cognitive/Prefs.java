package elvis.game.cognitive;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import elvis.game.cognitive.R;
import elvis.game.cognitive.utils.MixedConstant;

public class Prefs extends Activity {

	private SharedPreferences mBaseSettings;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.options);

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

		CheckBox showTipsCheckbox = (CheckBox) findViewById(R.id.options_showtips_checkbox);
		showTipsCheckbox.setChecked(mBaseSettings.getBoolean(
				MixedConstant.PREFERENCE_KEY_SHOWTIPS, true));
		showTipsCheckbox
				.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_SHOWTIPS,
											true).commit();
						} else {
							mBaseSettings
									.edit()
									.putBoolean(
											MixedConstant.PREFERENCE_KEY_SHOWTIPS,
											false).commit();
						}
					}
				});

	}

}
