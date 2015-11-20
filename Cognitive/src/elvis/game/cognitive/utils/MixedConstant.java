package elvis.game.cognitive.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import elvis.game.cognitive.data.CurCell;

public class MixedConstant {

	public static final String PREFERENCE_MIXEDCOLOR_BASE_INFO = "BASE_INFOS";
	public static final String PREFERENCE_MIXEDCOLOR_GAME_INFO = "GAME_INFOS";
	public static final String SUBJECT_NAME = "subjectBase64";
	
	public static final String PREFERENCE_KEY_SOUNDS = "elvis.game.cognitive.sounds";
	public static final String PREFERENCE_KEY_VIBRATE = "elvis.game.cognitive.vibrate";
	public static final String PREFERENCE_KEY_SHOWTIPS = "elvis.game.cognitive.showtips";
	public static final String PREFERENCE_KEY_HARDMODE = "elvis.game.cognitive.hardmode";
	public static final String PREFERENCE_KEY_SEQUENCE = "elvis.game.cognitive.sequence";
	public static final String PREFERENCE_KEY_PASSWORD = "neuro";
	
	public static final String PREFERENCE_KEY_DEBUG = "elvis.game.cognitive.debug";
	
	public static final String GAME_STATUS_COMPLETE_SET = "GAME_STATUS_COMPLETE_SET";
	
	public static final int BLOCK_NUMBER = 5;
	public static final int HYPER_NUMBER = 5; 
	public static final int SET_NUMBER = 7; 
	
	public static final int PAINT_DELAY = 30;
	
	
	
	/*public static final int[][] HYPERSET1 = {{4,5}, {2,1}, {3,7}, {1,8}, {4,3}, {9,2}, {8,4}};
	public static final int[][] HYPERSET2 = {{9,2}, {2,7}, {9,8}, {1,7}, {8,6}, {7,4}, {3,1}};*/
	
	public static final int[][] HYPERSET1 = {{3,4}, {1,0}, {2,6}, {0,7}, {3,2}, {8,1}, {7,3}};
	public static final int[][] HYPERSET2 = {{8,1}, {1,6}, {8,7}, {0,6}, {7,5}, {6,3}, {2,0}};
	
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
	
	public static Object getObjectInfo(Context mContext) {  
        try {  
            SharedPreferences mSharedPreferences = mContext.getSharedPreferences(MixedConstant.PREFERENCE_MIXEDCOLOR_GAME_INFO, Context.MODE_PRIVATE);  
            String personBase64 = mSharedPreferences.getString(SUBJECT_NAME, "");  
            byte[] base64Bytes = Base64.decodeBase64(personBase64.getBytes());  
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);  
            ObjectInputStream ois = new ObjectInputStream(bais);  
            return ois.readObject();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return null;
          
    }
	
	public static void writeExcel(List<CurCell> mArrayList, String fileName){
        try {
        	File textsDir = new File(Environment.getExternalStorageDirectory() + File.separator + "tests");
        	if(!textsDir.exists()){
        	  textsDir.mkdir();
        	}
            WritableWorkbook mWorkbook = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory() + "/tests/" + fileName + ".xls"));
            WritableSheet mSheet = mWorkbook.createSheet("hello", 0);
            
            //header
            List<CurCell> header = new ArrayList<CurCell>();
            header.add(new CurCell(0, 0, "Subject_ID"));
            header.add(new CurCell(0, 1, "Block Number"));
            header.add(new CurCell(0, 2, "Hyper Number"));
            header.add(new CurCell(0, 3, "Home Key Time"));
            header.add(new CurCell(0, 4, "Set Number"));
            header.add(new CurCell(0, 5, "Set LED On"));
            header.add(new CurCell(0, 6, "ChT (ms)"));
            header.add(new CurCell(0, 7, "MvT (ms)"));
            
            
            for(CurCell mCurCell : header){
                Label mLabel = new Label(mCurCell.getCol(), mCurCell.getRow(), mCurCell.getContent());
                mSheet.addCell(mLabel);
            }
            
            for(CurCell mCurCell : mArrayList){
                Label mLabel = new Label(mCurCell.getCol(), mCurCell.getRow(), mCurCell.getContent());
                mSheet.addCell(mLabel);
            }
            mWorkbook.write();
            mWorkbook.close();
        } catch (jxl.write.biff.RowsExceededException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (jxl.write.WriteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
	
	public static String parseTime(long time){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");  
        Date dateString = new Date(time); 
        return df.format(dateString); 
	}
}
