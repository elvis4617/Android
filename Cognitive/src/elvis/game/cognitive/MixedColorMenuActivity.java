package elvis.game.cognitive;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import elvis.game.cognitive.utils.MixedConstant;

public class MixedColorMenuActivity extends Activity implements OnClickListener {

	private SharedPreferences mBaseSettings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main);

		Button startButton = (Button) findViewById(R.id.start_game);
		startButton.setOnClickListener(this);

		Button optionButton = (Button) findViewById(R.id.options);
		optionButton.setOnClickListener(this);
		
		Button dataButton = (Button) findViewById(R.id.data);
		dataButton.setOnClickListener(this);

		Button exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(this);

		mBaseSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, 0);
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.start_game:
			if (mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_SHOWTIPS,
					true)) {
				i = new Intent(this, TipsActivity.class);
			} else {
				i = new Intent(this, MixedColorActivity.class);
			}
			break;
		case R.id.data:
			i = new Intent(this, Data_Set_Trials_Activity.class);
			break;
		case R.id.options:
			i = new Intent(this, Prefs.class);
			break;
		case R.id.exit:
			finish();
			return;
		}
		if (i != null) {
			startActivity(i);
		}
	}

}