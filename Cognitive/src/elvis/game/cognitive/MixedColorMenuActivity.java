package elvis.game.cognitive;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.CurCell;
import elvis.game.cognitive.data.TimeRecorder;
import elvis.game.cognitive.utils.MixedConstant;

public class MixedColorMenuActivity extends Activity implements OnClickListener {

	private long exitTime = 0;
	private DBManager mgr;
	
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

		mgr = new DBManager(this);
	}

	@Override
	public void finish() {
		super.finish();
		mgr.closeDB();
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.start_game:
				i = new Intent(this, Go.class);
			break;
		case R.id.data:
			//i = new Intent(this, Data_Set_Trials_Activity.class);
			Log.i("database", mgr.queryForSubjects("Good").toString());
			List<TimeRecorder> list = mgr.queryAll();
			if(!list.isEmpty()){
				Log.i("in", "in");
				List<CurCell> data = new ArrayList<CurCell>();
				for(TimeRecorder t : list){
						data.add(new CurCell(list.indexOf(t)+1, 0, t.getSubjectID()));
						data.add(new CurCell(list.indexOf(t)+1, 1, t.getBlockCounter()+1+""));
						data.add(new CurCell(list.indexOf(t)+1, 2, t.getHyperCounter()+1+""));
						data.add(new CurCell(list.indexOf(t)+1, 3, t.getHomeKeyTime()+""));
						data.add(new CurCell(list.indexOf(t)+1, 4, t.getSetCounter()+1+""));
						data.add(new CurCell(list.indexOf(t)+1, 5, t.getSetLedOn()+""));
						data.add(new CurCell(list.indexOf(t)+1, 6, t.getChT()+""));
						data.add(new CurCell(list.indexOf(t)+1, 7, t.getMvT()+""));
				}
				MixedConstant.writeExcel(data, "all");
			}
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
	        if((System.currentTimeMillis()-exitTime) > 2000){  
	            Toast.makeText(getApplicationContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();                                
	            exitTime = System.currentTimeMillis();   
	        } else {
	            finish();
	            System.exit(0);
	        }
	        return true;   
	    }
	    return super.onKeyDown(keyCode, event);
	}

}