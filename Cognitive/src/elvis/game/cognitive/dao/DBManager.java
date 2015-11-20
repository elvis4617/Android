package elvis.game.cognitive.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import elvis.game.cognitive.data.TimeRecorder;

public class DBManager {
	private DatabaseHelper dbHelper;  
    private SQLiteDatabase db;  
    
    public DBManager(Context context) {  
        dbHelper = new DatabaseHelper(context,DatabaseHelper.getDbName(),null,DatabaseHelper.getVersion());  
        db = dbHelper.getWritableDatabase();  
    }  
    
    public void add(TimeRecorder subject) {  
        db.beginTransaction();  
        try {   
            db.execSQL("INSERT INTO subjects VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{subject.getSubjectID(), subject.getBlockCounter(),
            		subject.getHyperCounter(), subject.getHomeKeyTime(), subject.getSetCounter(), subject.getSetLedOn(), subject.getChT(), subject.getMvT()});  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
        }  
    }
    
   public void add(List<TimeRecorder> subjects) {  
        db.beginTransaction();  
        try {  
            for (TimeRecorder subject : subjects)
            	db.execSQL("INSERT INTO subjects VALUES(?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{subject.getSubjectID(), subject.getBlockCounter(),
                		subject.getHyperCounter(), subject.getHomeKeyTime(), subject.getSetCounter(), subject.getSetLedOn(), subject.getChT(), subject.getMvT()});  
            db.setTransactionSuccessful();  
        } finally {  
            db.endTransaction();  
        }  
    }
    
    public void clear() {  
    	db.delete("subjects", null, null);
    }  
    
    public List<TimeRecorder> queryForSubjects(String subject_ID) {  
        List<TimeRecorder> subjects = new ArrayList<TimeRecorder>();  
        Cursor c = queryTheSubject(subject_ID);  
        while (c.moveToNext()) {  
        	TimeRecorder trial = new TimeRecorder(
        			c.getString(c.getColumnIndex("subject_ID")),
        			c.getString(c.getColumnIndex("home_Key_time")),
        			c.getInt(c.getColumnIndex("block_Counter")),
        			c.getInt(c.getColumnIndex("hyper_Counter")),
        			c.getInt(c.getColumnIndex("set_Counter")),
        			c.getString(c.getColumnIndex("set_led_on")),
        			c.getInt(c.getColumnIndex("chT")),
        			c.getInt(c.getColumnIndex("mvT"))
        			);   
            subjects.add(trial);  
        }  
        c.close();  
        return subjects;  
    }  
    
    public Cursor queryTheCursor() {  
        Cursor c = db.rawQuery("SELECT * FROM subjects", null);  
        return c;  
    }  
    
    public Cursor queryTheSubject(String subject_ID) {  
        Cursor c = db.rawQuery("SELECT * FROM subjects where subject_ID = ? order by set_led_on ASC", new String[]{subject_ID});  
        return c;  
    }  
    
    public void closeDB() {  
        db.close();  
    }

    public List<TimeRecorder> queryAll() {  
        List<TimeRecorder> subjects = new ArrayList<TimeRecorder>();  
        Cursor c = queryTheCursor();  
        while (c.moveToNext()) {  
        	TimeRecorder trial = new TimeRecorder(
        			c.getString(c.getColumnIndex("subject_ID")),
        			c.getString(c.getColumnIndex("home_Key_time")),
        			c.getInt(c.getColumnIndex("block_Counter")),
        			c.getInt(c.getColumnIndex("hyper_Counter")),
        			c.getInt(c.getColumnIndex("set_Counter")),
        			c.getString(c.getColumnIndex("set_led_on")),
        			c.getInt(c.getColumnIndex("chT")),
        			c.getInt(c.getColumnIndex("mvT"))
        			);   
            subjects.add(trial);  
        }  
        c.close();  
        return subjects;
    }
}
