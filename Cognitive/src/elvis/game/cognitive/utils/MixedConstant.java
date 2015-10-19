package elvis.game.cognitive.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import elvis.game.cognitive.data.TimeRecorder;

public class MixedConstant {

	public static final String PREFERENCE_MIXEDCOLOR_BASE_INFO = "BASE_INFOS";
	public static final String PREFERENCE_MIXEDCOLOR_GAME_INFO = "GAME_INFOS";
	
	public static final String PREFERENCE_KEY_SOUNDS = "elvis.game.cognitive.sounds";
	public static final String PREFERENCE_KEY_VIBRATE = "elvis.game.cognitive.vibrate";
	public static final String PREFERENCE_KEY_SHOWTIPS = "elvis.game.cognitive.showtips";
	public static final String PREFERENCE_KEY_HARDMODE = "elvis.game.cognitive.hardmode";
	public static final String PREFERENCE_KEY_SEQUENCE = "elvis.game.cognitive.hardmode";
	
	public static final int[][] HYPERSET1 = {{0,1}, {1,2}, {2,3}, {3,4}, {4,5}};
	public static final int[][] HYPERSET2 = {{5,4}, {4,3}, {3,2}, {2,1}, {1,0}};
	
	public static void saveObject(Context mContext, Object subject, String name) {  
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, Context.MODE_PRIVATE);  
        try {  
            ByteArrayOutputStream baos = new ByteArrayOutputStream();  
            ObjectOutputStream oos = new ObjectOutputStream(baos);  
            oos.writeObject(subject);  
  
            String subjectBase64 = new String(Base64.encodeBase64(baos.toByteArray()));  
            SharedPreferences.Editor editor = mSharedPreferences.edit();  
            editor.putString(name, subjectBase64);  
            editor.commit();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    } 
}
