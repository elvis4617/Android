package elvis.game.cognitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import elvis.game.cognitive.dao.DBManager;
import elvis.game.cognitive.data.Trials;

public class Data_Set_Trials_Activity extends ListActivity implements
		OnClickListener {

	private boolean[] isToggle;
	private Button clear;
	private DBManager mgr;
	List<HashMap<String, Object>> data;

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.set_trials);
		clear = (Button) this.findViewById(R.id.clearData);
		clear.setOnClickListener(this);
		mgr = new DBManager(this);
		show();
		for (boolean b : isToggle) {
			b = false;
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		HashMap<String, Object> hm;
		TextView trial = (TextView) l.getChildAt(position).findViewById(
				R.id.trials);
		if (!isToggle[Integer.parseInt(trial.getText().toString())]) {
			List<Trials> trials = mgr.queryForTrial(trial.getText().toString());
			++position;
			for (int i = 1; i < trials.size(); i++) {
				hm = new HashMap<String, Object>();
				hm.put("Trial", trials.get(i).getTrial());
				hm.put("Set", trials.get(i).getSets());
				hm.put("ChT", trials.get(i).getChT());
				hm.put("MvT", trials.get(i).getMvT());
				data.add(position, hm);
				position++;
			}
			isToggle[Integer.parseInt(trial.getText().toString())] = true;
		} else if (isToggle[Integer.parseInt(trial.getText().toString())]) {
			List<HashMap<String, Object>> trials = toMap(mgr
					.queryForTrial(trial.getText().toString()));
			hm = new HashMap<String, Object>();
			trials.remove(0);
			data.removeAll(trials);
			isToggle[Integer.parseInt(trial.getText().toString())] = false;
		}

		SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
		adapter.notifyDataSetChanged();

	}

	private void show() {
		List<Trials> trials = mgr.query();
		isToggle = new boolean[trials.size() == 0 ? 0 : trials.size()];
		data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm;
		for (int i = 0; i < trials.size(); i++) {
			if (data.isEmpty()
					|| !data.get(data.size() - 1).get("Trial")
							.equals(trials.get(i).getTrial())) {
				hm = new HashMap<String, Object>();
				hm.put("Trial", trials.get(i).getTrial());
				hm.put("Set", trials.get(i).getSets());
				hm.put("ChT", trials.get(i).getChT());
				hm.put("MvT", trials.get(i).getMvT());
				data.add(hm);
			}
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.grid_item,
				new String[] { "Trial", "Set", "ChT", "MvT" }, new int[] {
						R.id.trials, R.id.sets, R.id.chT, R.id.mvT });
		setListAdapter(adapter);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//case R.id.GridStyle:
			// i = new Intent(this, Data_Set_Trials_Activity.class);
			//break;
		case R.id.GraphStyle:
			// i = new Intent(this, Data_ChT_Trials_Activity.class);
			break;
		case R.id.clearData:
			mgr.clear();
			data.clear();
			SimpleAdapter adapter = (SimpleAdapter) getListAdapter();
			adapter.notifyDataSetChanged();
			break;
		}
	}

	private List<HashMap<String, Object>> toMap(List<Trials> list) {
		List<HashMap<String, Object>> rList = new ArrayList<HashMap<String, Object>>();
		for (Trials trial : list) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Trial", trial.getTrial());
			map.put("Set", trial.getSets());
			map.put("ChT", trial.getChT());
			map.put("MvT", trial.getMvT());
			rList.add(map);
		}
		return rList;
	}

}
