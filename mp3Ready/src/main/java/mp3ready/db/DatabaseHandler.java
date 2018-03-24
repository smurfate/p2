package mp3ready.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	public static final int DATABASE_VERSION = 2;

	// Database Name
	public static final String DATABASE_NAME = "mp3readydb";
	public static String DATABASE_PATH;
	// Contacts table name
	public static final String TABLE_Song = "Song";

	// Settings Song Table Columns names
	public static final String KEY_song_ID = "id";
	public static final String KEY_song_SID = "sid";
	public static final String KEY_song_url = "url";
	public static final String KEY_song_bitrate = "bitrate";
	public static final String KEY_song_title = "title";
	public static final String KEY_song_artist = "artist";
	public static final String KEY_song_cover = "cover";
	public static final String KEY_song_d = "duration";
	public static final String KEY_song_listeners = "listeners";
	public static final String KEY_song_downloaded_path = "device_path";
	public static final String KEY_song_likestate = "likestate";
	public static final String KEY_song_rate = "rate";
	public static final String KEY_song_download_state = "download_state";
	public static final String KEY_song_download_progress = "download_progress";
	public static final String KEY_song_isInPL = "isInPL";

	// //////////////////////////////////////////////////////////////////////////////////

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// final String packageName = context.getPackageName();
		DATABASE_PATH = context.getDatabasePath(DATABASE_NAME)
				.getAbsolutePath();
		// DATABASE_PATH = context.getFilesDir().getAbsolutePath() + packageName
		// + "/databases/"+DATABASE_NAME;
		Log.i("DB Handler", DATABASE_PATH);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v("DB OnCreate", "started");
		String CREATE_TABLE_NEWS = "CREATE TABLE IF NOT EXISTS " + TABLE_Song
				+ "(" + KEY_song_ID + " INTEGER PRIMARY KEY  autoincrement ,"
				+ KEY_song_SID + " TEXT," + KEY_song_url + " TEXT,"
				+ KEY_song_title + " TEXT," + KEY_song_bitrate + " INTEGER,"
				+ KEY_song_downloaded_path + " TEXT ," + KEY_song_likestate
				+ " TEXT ," + KEY_song_rate + " TEXT ,"
				+ KEY_song_download_state + " INTEGER ,"
				+ KEY_song_download_progress + " INTEGER ," + KEY_song_artist
				+ " TEXT ," + KEY_song_cover + " TEXT ," + KEY_song_d
				+ " TEXT ," + KEY_song_listeners + " TEXT ," + KEY_song_isInPL
				+ " INTEGER   );";

		Log.v("DB OnCreate", CREATE_TABLE_NEWS);
		db.execSQL(CREATE_TABLE_NEWS);

		String createSongsIndeces = "CREATE INDEX " + TABLE_Song + "_"
				+ KEY_song_url + "_idx " + " ON " + TABLE_Song + "("
				+ KEY_song_url + ");";
		db.execSQL(createSongsIndeces);
		// ////////////////////////////////////

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_Song);

		// Create tables again
		onCreate(db);
	}

}