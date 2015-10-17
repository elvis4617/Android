package elvis.game.cognitive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class Go extends Activity implements OnClickListener {

	private EditText name;
	private Button go;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.go);
		name = (EditText) findViewById(R.id.name);
		go = (Button) findViewById(R.id.go_button);
		go.setOnClickListener(this);
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.go_button:
				Intent i = new Intent(this, MixedColorActivity.class);
				i.putExtra("subject", name.getText().toString());
				Log.i("start", "start activity");
				startActivity(i);
		}
	}
	
}
