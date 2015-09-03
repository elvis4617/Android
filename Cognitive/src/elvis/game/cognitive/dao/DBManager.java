package elvis.game.cognitive.dao;

import java.util.ArrayList;
import java.util.List;

import elvis.game.cognitive.data.Trials;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	private DatabaseHelper dbHelper;  
    private SQLiteDatabase db;  
    
    public DBManager(Context context) {  
        dbHelper = new DatabaseHelper(context,DatabaseHelper.getDbName(),null,DatabaseHelper.getVersion());  
        db = dbHelper.getWritableDatabase();  
    }  
    
    public void add(Trials trials) {  
        db.beginTransaction();  
        try {   
            db.execSQL("INSERT INTO trial VALUES(?, ?, ?, ?)", new Object[]{trials.getTrial(), trials.getSets(),trials.getChT(),trials.getMvT()});  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
        }  
    }
    
    public void add(List<Trials> trials) {  
        db.beginTransaction();  
        try {  
            for (Trials trial : trials) {  
                db.execSQL("INSERT INTO trial VALUES(?, ?, ?, ?)", new Object[]{trial.getTrial(), trial.getSets(),trial.getChT(),trial.getMvT()});  
            }  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
        }  
    }
    
    public void updateAge(Trials trials) {  
        ContentValues cv = new ContentValues();  
        cv.put("_Set", trials.getSets());  
        cv.put("ChT", trials.getChT());
        cv.put("MvT", trials.getMvT());
        db.update("trial", cv, "Trial = ?", new String[]{trials.getTrial()+""});  
    }  
    
    public void clear() {  
    	db.delete("trial", null, null);
    }  
    
    public List<Trials> query() {  
        ArrayList<Trials> trials = new ArrayList<Trials>();  
        Cursor c = queryTheCursor();  
        while (c.moveToNext()) {  
        	Trials trial = new Trials(
        			c.getInt(c.getColumnIndex("Trial")),
        			c.getInt(c.getColumnIndex("_Set")),
        			c.getDouble(c.getColumnIndex("ChT")),
        			c.getDouble(c.getColumnIndex("MvT"))
        			);   
            trials.add(trial);  
        }  
        c.close();  
        return trials;  
    }  
    
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM trial", null);  
        return c;  
    }  
    
    public Cursor queryTheTrial(String trial) {  
        Cursor c = db.rawQuery("SELECT * FROM trial where Trial = ?", new String[]{trial});  
        return c;  
    }  
    
    public void closeDB() {  
        db.close();  
    }

	public List<Trials> queryForTrial(String trialNum) {
		ArrayList<Trials> trials = new ArrayList<Trials>();  
        Cursor c = queryTheTrial(trialNum);  
        while (c.moveToNext()) {  
        	Trials trial = new Trials(
        			c.getInt(c.getColumnIndex("Trial")),
        			c.getInt(c.getColumnIndex("_Set")),
        			c.getDouble(c.getColumnIndex("ChT")),
        			c.getDouble(c.getColumnIndex("MvT"))
        			);   
            trials.add(trial);  
        }  
        c.close();  
        return trials;  
	}  
}
