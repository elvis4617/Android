package elvis.game.cognitive;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

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
		
		mGameSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, 0);
		mBaseSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_MIXEDCOLOR_BASE_INFO, 0);
		
		
		if(mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_HARDMODE, false) && !mGameSettings.getString("subjectBase64", "").equals("")){
			getObjectInfo();
			name.setText(subject.getSubjectID());
		}else subject = new TimeRecorder(BLOCK_NUMBER, HYPER_NUMBER, SET_NUMBER);
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mGameSettings.edit().putString("subjectBase64", "").commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.go_button:
				subject.setSubjectID(name.getText().toString());
				
				Log.i("edittext name", name.getText().toString());
				Log.i("subject initial", subject.toString());
				
				try { 
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos); 
					oos.writeObject(subject); 
					
					String subjectBase64 = new String(Base64.encodeBase64(baos.toByteArray())); 
					mGameSettings.edit().putString("subjectBase64", subjectBase64).commit();
				}catch (IOException e) {  
		            e.printStackTrace();  
		        }  
				
				Intent i = new Intent(this, MixedColorActivity.class);
/*				Bundle bundle = new Bundle();
				bundle.putString("subject", name.getText().toString());
				i.putExtras(bundle);
				Log.i("start", "start activity");*/
				startActivity(i);
		}
	}
	
	protected void getObjectInfo() {  
        try {  
            SharedPreferences mSharedPreferences = getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, Context.MODE_PRIVATE);  
            String personBase64 = mSharedPreferences.getString("subjectBase64", "");  
            byte[] base64Bytes = Base64.decodeBase64(personBase64.getBytes());  
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);  
            ObjectInputStream ois = new ObjectInputStream(bais);  
            subject = (TimeRecorder) ois.readObject();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
          
    }  
	
}
