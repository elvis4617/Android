package elvis.game.cognitive;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Data_MvT_Trials_Activity extends Activity {

	/*private ListView listView;
	private DBManager mgr;*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.set_trials);
		//listView = (ListView) this.findViewById(R.id.listview);
		//mgr = new DBManager(this);
		//show();
	}
	
	/*private void show() {
		List<Trials> trials = mgr.query();
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm = new HashMap<String, Object>();;
		hm.put("Trial", "Trial");
		hm.put("MvT", "MvT");
		data.add(hm);
		for (int i = 0; i < trials.size(); i++) {
			hm = new HashMap<String, Object>();
			hm.put("Trial", trials.get(i).getTrial());
			hm.put("MvT", trials.get(i).getMvT());
			data.add(hm);
		}
		
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.grid_item, new String[] { "Trial", "MvT" }, new int[] {
						R.id.trials, R.id.sets });
		listView.setAdapter(adapter);
		
	}*/
}
