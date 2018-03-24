package mp3ready.db;

import java.util.ArrayList;
import java.util.List;

import mp3ready.entities.DownloadLink;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.util.MyIntents;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataSource {

	private SQLiteDatabase database;
	private DatabaseHandler dbHelper;

	public DataSource(Context context) {
		dbHelper = new DatabaseHandler(context);
	}

	public synchronized SQLiteDatabase prepareDB() {
		try {

			if (database == null || !database.isOpen())
				database = dbHelper.getWritableDatabase();
			return database;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized void open() {
		try {

			if (database == null || !database.isOpen())
				database = dbHelper.getWritableDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void close() {
		if (database != null && database.isOpen())
			database.close();
	}

	/**
	 * cound how many pending(not finished) downloads user has
	 * 
	 * @return pending downloads count
	 */
	public int getPendingDownloadsCount() {
		int count = 0;
		Cursor cursor = database.rawQuery("select count(url) from "
				+ DatabaseHandler.TABLE_Song + " where "
				+ DatabaseHandler.KEY_song_download_state + " != "
				+ MyIntents.Types.COMPLETE + " and ("
				+ DatabaseHandler.KEY_song_isInPL + " = "
				+ Enums.PLAYLIST_DOWNLOAD_LIST + " or "
				+ DatabaseHandler.KEY_song_isInPL + " = "
				+ Enums.PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST + ")", null);
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			count = cursor.getInt(0);
		}
		cursor.close();
		return count;
	}

	/**
	 * delete song from given play list delete song by its url or by its song id
	 * 
	 * @param url
	 *            song url
	 * @param sid
	 *            song id
	 * @param whichPlayList
	 *            playlist the we want to delete song from it
	 * @return flag to know if deleting process successful or not
	 */
	public int deleteSongFromPL(String url, String sid, int whichPlayList) {

		Song songIfExist = isSongExistIn(url, sid);
		int resId = -1;
		if (songIfExist != null) {
			String whereClause = DatabaseHandler.KEY_song_url + " = ? or "
					+ DatabaseHandler.KEY_song_SID + " = ?";
			String[] whereArgs = { url, sid };
			if (songIfExist.whichPlayList == whichPlayList) {
				resId = this.database.delete(DatabaseHandler.TABLE_Song,
						whereClause, whereArgs);
				Log.e("DataSource", resId + "");
			} else if (songIfExist.whichPlayList == Enums.PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST) {
				Log.e("DataSource", songIfExist.whichPlayList + "");
				ContentValues values = new ContentValues();
				values.put(DatabaseHandler.KEY_song_isInPL,
						songIfExist.whichPlayList - whichPlayList);
				resId = this.database.update(DatabaseHandler.TABLE_Song,
						values, whereClause, whereArgs);
			} else {
				Log.e("DataSource", "no delete:" + songIfExist.whichPlayList);
			}
		}

		return resId;
	}

	/**
	 * search for song by its url or by its song id to check if exist or not
	 * 
	 * @param url
	 *            song url
	 * @param sid
	 *            song id
	 * @return Song object if it exist otherwise null
	 */
	public Song isSongExistIn(String url, String sid) {
		Cursor cursor = database.rawQuery("select * from "
				+ DatabaseHandler.TABLE_Song + " where "
				+ DatabaseHandler.KEY_song_url + " = \"" + url + "\" or "
				+ DatabaseHandler.KEY_song_SID + " = \"" + sid + "\"", null);
		cursor.moveToFirst();
		Song song = null;
		if (cursor.getCount() > 0) {
			while (!cursor.isAfterLast()) {
				song = new Song();
				song.dbid = cursor.getInt(0);
				song.SID = cursor.getString(1);
//				song.link = new DownloadLink();
//				song.link.id = cursor.getString(1);
				song.file = cursor.getString(2);
//				song.link.title = cursor.getString(3);
//				song.link.artist = cursor.getString(10);
//				song.link.songcover = cursor.getString(11);
				song.SName = cursor.getString(3);
//				song.link.bitrate = cursor.getString(4);
//				song.link.downloaded_path = cursor.getString(5);
//				song.likeState = cursor.getString(6);
//				song.rate = cursor.getString(7);
//				song.link.download_state = cursor.getInt(8);
//				song.link.download_progress = cursor.getInt(9);
				song.Artist = cursor.getString(10);
				song.songcover = cursor.getString(11);
				song.duration = cursor.getString(12);
				song.listeners = cursor.getString(13);
				song.whichPlayList = cursor.getInt(14);
				cursor.moveToNext();
			}
			cursor.close();
		}
		return song;
	}

	/**
	 * update song info in its playlist
	 * 
	 * @param url
	 *            song url
	 * @param sid
	 *            song id
	 * @param path
	 *            download path for downloaded song
	 * @param download_state
	 *            state of download (start, complete or paused)
	 * @param download_progress
	 *            download progress so far
	 * @return -1 updating failds , 1 updating successful
	 */
	public long updateSongInPlayList(String url, String sid, String path,
			Integer download_state, Integer download_progress) {
		ContentValues values = new ContentValues();
		long insertId = -1;
		values.put(DatabaseHandler.KEY_song_downloaded_path, path);
		values.put(DatabaseHandler.KEY_song_download_progress,
				download_progress);
		values.put(DatabaseHandler.KEY_song_download_state, download_state);
		String whereClause = DatabaseHandler.KEY_song_url + " = ? or "
				+ DatabaseHandler.KEY_song_SID + " = ?";
		String[] whereArgs = { url, sid };
		insertId = database.update(DatabaseHandler.TABLE_Song, values,
				whereClause, whereArgs);
		return insertId;
	}

	/**
	 * update song info in its playlist with other parameters
	 * 
	 * @param url
	 *            song url
	 * @param sid
	 *            song id
	 * @param rate
	 *            user rate of song
	 * @param like_state
	 *            enum to know if user like this song or not
	 * @return -1 updating failds , 1 updating successful
	 */
	public long updateSongInPlayList(String url, String sid, int rate,
			int like_state) {
		ContentValues values = new ContentValues();
		long insertId = -1;
		values.put(DatabaseHandler.KEY_song_rate, rate);
		values.put(DatabaseHandler.KEY_song_likestate, like_state);
		String whereClause = DatabaseHandler.KEY_song_url + " = ? or "
				+ DatabaseHandler.KEY_song_SID + " = ?";
		String[] whereArgs = { url, sid };
		insertId = database.update(DatabaseHandler.TABLE_Song, values,
				whereClause, whereArgs);
		return insertId;
	}

	/**
	 * add song to given play list if not exist , otherwise update it if the
	 * song is exist and its playlist not the given playlist then we sum them to
	 * represent that song is exist in both playlist (recently played and
	 * downloads)
	 * 
	 * @param sid
	 *            song id
	 * @param d
	 *            duration of song
	 * @param listeners
	 *            listeners count
	 * @param cover
	 *            cover photo url
	 * @param likeState
	 *            enum to know if user like this song or not
	 * @param rate
	 *            user rate on song
	 * @param url
	 *            song url
	 * @param title
	 *            song title
	 * @param artist
	 *            song artist
	 * @param bitrate
	 *            song bitrate
	 * @param whichPlayList
	 *            playlist we want to add song to it
	 * @return true adding song , false updating song
	 */
	public boolean addSongToPlayList(String sid, String d, String listeners,
			String cover, String likeState, String rate, String url,
			String title, String artist, String bitrate, Integer whichPlayList) {
		Song songIfExist = isSongExistIn(url, sid);
		ContentValues values = new ContentValues();
		boolean insertId = false;
		values.put(DatabaseHandler.KEY_song_url, url);
		values.put(DatabaseHandler.KEY_song_title, title);
		values.put(DatabaseHandler.KEY_song_bitrate, bitrate);
		values.put(DatabaseHandler.KEY_song_downloaded_path, "");
		values.put(DatabaseHandler.KEY_song_artist, artist);
		values.put(DatabaseHandler.KEY_song_likestate, likeState);
		values.put(DatabaseHandler.KEY_song_rate, rate);
		values.put(DatabaseHandler.KEY_song_download_progress, 0);
		values.put(DatabaseHandler.KEY_song_download_state,
				MyIntents.Types.START);

		if (songIfExist != null) {
			// if (songIfExist.SID == null || songIfExist.SID.equals("")){
			values.put(DatabaseHandler.KEY_song_SID, sid);
			// }
			if (songIfExist.songcover == null
					|| songIfExist.songcover.equals("")) {
				values.put(DatabaseHandler.KEY_song_cover, cover);
			}
			if (songIfExist.duration == null || songIfExist.duration.equals("")) {
				values.put(DatabaseHandler.KEY_song_d, d);
			}
			if (songIfExist.listeners == null
					|| songIfExist.listeners.equals("")) {
				values.put(DatabaseHandler.KEY_song_listeners, listeners);
			}
			if (songIfExist.whichPlayList != whichPlayList) {
				values.put(DatabaseHandler.KEY_song_isInPL,
						Enums.PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST);
			} else {
				values.put(DatabaseHandler.KEY_song_isInPL, whichPlayList);
			}
			String whereClause = DatabaseHandler.KEY_song_url + " = ? or "
					+ DatabaseHandler.KEY_song_SID + " = ?";
			String[] whereArgs = { url, sid };
			long res = database.update(DatabaseHandler.TABLE_Song, values,
					whereClause, whereArgs);

		} else {
			values.put(DatabaseHandler.KEY_song_SID, sid);
			values.put(DatabaseHandler.KEY_song_cover, cover);
			values.put(DatabaseHandler.KEY_song_d, d);
			values.put(DatabaseHandler.KEY_song_listeners, listeners);
			values.put(DatabaseHandler.KEY_song_isInPL, whichPlayList);
			long res = database
					.insert(DatabaseHandler.TABLE_Song, null, values);
			insertId = true;
		}
		return insertId;
	}

	/**
	 * get songs of given playlist
	 * 
	 * @param type
	 *            playlist type (recently played or downloads)
	 * @return list of songs
	 */
	public List<Song> getPlayList(int type) {
		List<Song> res = new ArrayList<Song>();
		Cursor cursor = database.rawQuery("select * from "
				+ DatabaseHandler.TABLE_Song + " where "
				+ DatabaseHandler.KEY_song_isInPL + " = " + type + " or "
				+ DatabaseHandler.KEY_song_isInPL + " = "
				+ Enums.PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Song song = new Song();
			song.dbid = cursor.getInt(0);
			song.SID = cursor.getString(1);
//			song.link = new DownloadLink();
//			song.link.id = cursor.getString(1);
			song.file = cursor.getString(2);
//			song.link.title = cursor.getString(3);
			song.SName = cursor.getString(3);
//			song.link.bitrate = cursor.getString(4);
//			song.link.downloaded_path = cursor.getString(5);
//			song.likeState = cursor.getString(6);
//			song.rate = cursor.getString(7);
//			song.link.download_state = cursor.getInt(8);
//			song.link.download_progress = cursor.getInt(9);
			song.Artist = cursor.getString(10);
			song.songcover = cursor.getString(11);
			song.duration = cursor.getString(12);
			song.listeners = cursor.getString(13);
			song.whichPlayList = cursor.getInt(14);
			res.add(song);
			cursor.moveToNext();
		}
		cursor.close();
		return res;
	}
}
