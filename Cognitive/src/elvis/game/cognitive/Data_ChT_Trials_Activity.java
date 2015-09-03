package elvis.game.cognitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.Trials;

public class Data_ChT_Trials_Activity extends Activity {

	private ListView listView;
	private DBManager mgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.set_trials);
		//listView = (ListView) this.findViewById(R.id.listview);
		mgr = new DBManager(this);
		show();
	}
	
	private void show() {
		List<Trials> trials = mgr.query();
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm = new HashMap<String, Object>();;
		hm.put("Trial", "Trial");
		hm.put("ChT", "ChT");
		data.add(hm);
		for (int i = 0; i < trials.size(); i++) {
			hm = new HashMap<String, Object>();
			hm.put("Trial", trials.get(i).getTrial());
			hm.put("ChT", trials.get(i).getChT());
			data.add(hm);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.grid_item, new String[] { "Trial", "ChT" }, new int[] {
						R.id.trials, R.id.sets });
		listView.setAdapter(adapter);
		
	}

	
}
