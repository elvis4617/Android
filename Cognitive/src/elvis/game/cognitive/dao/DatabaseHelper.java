package elvis.game.cognitive.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "time_Recorder.db";
	private static final int VERSION = 1;
			
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS subjects" +  
                "(subject_ID varchar(20) not null, block_Counter INTEGER, "
                + "hyper_Counter INTEGER, home_Key_time varchar(20), set_Counter INTEGER, "
                + "set_led_on varchar(20), chT INTEGER, mvT INTEGER)");  
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public static int getVersion() {
		return VERSION;
	}

	public static String getDbName() {
		return DB_NAME;
	}

}
