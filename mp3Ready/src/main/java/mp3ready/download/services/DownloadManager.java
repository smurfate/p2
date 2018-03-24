package mp3ready.download.services;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import mp3ready.db.DataSource;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.util.MyIntents;
import mp3ready.util.StorageUtils;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * <p>
 * Download Manager Thread manage the following:
 * </p>
 * <li>read all downloads from sqlite db</li> <li>send broadcast to run max 3
 * #MAX_DOWNLOAD_THREAD_COUNT downloads and sleep the rest</li> <li>send
 * broadcast to retry the download if error happened</li> <li>send broadcast
 * with error if error happened</li> <li>send broadcast to pause the downloads
 * and resume it</li>
 * 
 * @author mhmd
 * 
 */
public class DownloadManager extends Thread {

	private static final int MAX_TASK_COUNT = 100;
	private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;
	private Context mContext;

	private TaskQueue mTaskQueue;
	private List<DownloadTask> mDownloadingTasks;
	private List<DownloadTask> mPausingTasks;
	private DataSource db;
	private Boolean isRunning = false;

	public DownloadManager(Context context) {

		mContext = context;

		mTaskQueue = new TaskQueue();
		mDownloadingTasks = new ArrayList<DownloadTask>();
		mPausingTasks = new ArrayList<DownloadTask>();
		db = new DataSource(context);
		db.prepareDB();
		db.open();
	}

	/**
	 * <p>
	 * start manager thread
	 * </p>
	 * <p
	 * read uncomplete tasks from database and process/render them
	 * </p>
	 */
	public void startManage() {

		Log.e("DownloadManager", "startManage()");
		isRunning = true;
		this.start();
		checkUncompleteTasks();
	}

	/**
	 * finish the download manager
	 */
	public void finish() {

		isRunning = false;
		if (db != null) {
			db.close();
		}
		Log.i("DownloadManager", "closed");
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isRunning() {

		return isRunning;
	}

	@Override
	public void run() {

		super.run();
		Log.e("DownloadManager", "run()");
		/**
		 * <p>
		 * run #MAX_DOWNLOAD_THREAD_COUNT and sleep the rest
		 * </p>
		 */
		while (isRunning) {
			DownloadTask task = mTaskQueue.poll();
			Log.e("DownloadManager", task.getTitle() + ":" + task.getUrl());
			mDownloadingTasks.add(task);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		}
	}

	/**
	 * <p>
	 * create task for song object
	 * </p>
	 * 
	 * @param song
	 */
	public void addTask(Song song) {

		if (!StorageUtils.isSDCardPresent()) {

			return;
		}

		if (!StorageUtils.isSdCardWrittenable()) {

			return;
		}

		try {
			DownloadTask task = newDownloadTask(song.getSongURL(), song.SName,
					song.Artist);
//			task.setDownloadPercent(song.link.download_progress);
//			if (song.link.download_state == MyIntents.Types.START) {
//				addTask(task);
//			} else if (song.link.download_state == MyIntents.Types.PAUSE) {
//				mPausingTasks.add(task);
//				broadcastAddTask(task, true);
//			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * <p>
	 * retry the download for single task if there's error happend
	 * </p>
	 * 
	 * @param url
	 * @param title
	 * @param artist
	 */
	public void retryTask(String url, String title, String artist) {
		if (!StorageUtils.isSDCardPresent()) {

			return;
		}

		if (!StorageUtils.isSdCardWrittenable()) {

			return;
		}

		if (getTotalTaskCount() >= MAX_TASK_COUNT) {
			Toast.makeText(mContext, "no more downloads", Toast.LENGTH_LONG)
					.show();
			return;
		}

		try {
			DownloadTask task = newDownloadTask(url, title, artist);
			broadcastRetryTask(task);

			mTaskQueue.offer(task);

			if (!this.isAlive()) {
				this.startManage();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * <p>
	 * create task for some items
	 * </p>
	 * 
	 * @param url
	 *            download url
	 * @param title
	 *            title of download item
	 * @param artist
	 *            the artist of song if exist
	 */
	public void addTask(String url, String title, String artist) {

		if (!StorageUtils.isSDCardPresent()) {

			return;
		}

		if (!StorageUtils.isSdCardWrittenable()) {

			return;
		}

		if (getTotalTaskCount() >= MAX_TASK_COUNT) {
			Toast.makeText(mContext, "no more downloads", Toast.LENGTH_LONG)
					.show();
			return;
		}

		try {
			addTask(newDownloadTask(url, title, artist));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * <p>
	 * adding task to download queue
	 * </p>
	 * 
	 * @param task
	 */
	private void addTask(DownloadTask task) {

		broadcastAddTask(task);

		mTaskQueue.offer(task);

		if (!this.isAlive()) {
			this.startManage();
		}
	}

	/**
	 * send broadcast to notifiy the app that this download task show retry
	 * 
	 * @param task
	 */
	private void broadcastRetryTask(DownloadTask task) {
		long updated = db.updateSongInPlayList(task.getUrl(), "", "",
				MyIntents.Types.START, 0);
		if (updated != -1) {
			Intent nofityIntent = new Intent("mp3ready.ui.ListFragment");
			nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.RETRY);
			nofityIntent.putExtra(MyIntents.URL, task.getUrl());
			nofityIntent.putExtra(MyIntents.TITLE, task.getTitle());
			nofityIntent.putExtra(MyIntents.IS_PAUSED, false);
			mContext.sendBroadcast(nofityIntent);
		}
	}

	private void broadcastAddTask(DownloadTask task) {

		broadcastAddTask(task, false);
	}

	// private void broadcastAddTask(String url, String title,boolean
	// isInterrupt) {
	//
	// Intent nofityIntent = new Intent("mp3ready.ui.ListFragment");
	// nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
	// nofityIntent.putExtra(MyIntents.URL, url);
	// nofityIntent.putExtra(MyIntents.TITLE, title);
	// nofityIntent.putExtra(MyIntents.IS_PAUSED, isInterrupt);
	// nofityIntent.putExtra(MyIntents.PROCESS_PROGRESS, "0");
	// mContext.sendBroadcast(nofityIntent);
	// }

	/**
	 * send broadcast to notify the app there's new download task and is paused
	 * or not
	 * 
	 * @param task
	 * @param isPaused
	 */
	private void broadcastAddTask(DownloadTask task, boolean isPaused) {

		Intent nofityIntent = new Intent("mp3ready.ui.ListFragment");
		nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
		nofityIntent.putExtra(MyIntents.URL, task.getUrl());
		nofityIntent.putExtra(MyIntents.TITLE, task.getTitle());
		nofityIntent.putExtra(MyIntents.IS_PAUSED, isPaused);
		nofityIntent.putExtra(MyIntents.PROCESS_PROGRESS,
				task.getDownloadPercent() + "");
		mContext.sendBroadcast(nofityIntent);
	}

	public void reBroadcastAddAllTask() {

		DownloadTask task;
		Log.e("DownloadManager",
				"mDownloadingTasks:" + mDownloadingTasks.size() + "");
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			broadcastAddTask(task, task.isInterrupt());

		}
		Log.e("DownloadManager", "mTaskQueue:" + mTaskQueue.size() + "");
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			broadcastAddTask(task);

		}
		Log.e("DownloadManager", "mPausingTasks:" + mPausingTasks.size() + "");
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			broadcastAddTask(task, true);

		}
	}

	/**
	 * check if song url exist in queue of downloaded tasks
	 * 
	 * @param url
	 *            song url
	 * @return true song exist , false song doesn't exist
	 */
	public boolean hasTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task.getUrl().equals(url)) {
				return true;
			}
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
		}

		return false;
	}

	public DownloadTask getTask(int position) {

		if (position >= mDownloadingTasks.size()) {
			return mTaskQueue.get(position - mDownloadingTasks.size());
		} else {
			return mDownloadingTasks.get(position);
		}
	}

	public int getQueueTaskCount() {

		return mTaskQueue.size();
	}

	public int getDownloadingTaskCount() {

		return mDownloadingTasks.size();
	}

	public int getPausingTaskCount() {

		return mPausingTasks.size();
	}

	public int getTotalTaskCount() {

		return getQueueTaskCount() + getDownloadingTaskCount()
				+ getPausingTaskCount();
	}

	/**
	 * read all downloads tasks from sqlite db and do the following: if download
	 * task is complete/error then send broadcast to notify the app that
	 * download task is complete/error if download paused or still running then
	 * we create AsyncTask for it
	 */
	public void checkUncompleteTasks() {

		List<Song> songsLinks = db.getPlayList(Enums.PLAYLIST_DOWNLOAD_LIST);
		if (songsLinks != null && songsLinks.size() > 0) {
//			for (Song songLink : songsLinks) {
////				Log.e("DownloadManager(checkUncompleteTasks)",
////						songLink.link.download_state + ":" + songLink.link.url);
//				if (songLink.link.download_state != MyIntents.Types.COMPLETE
//						&& songLink.link.download_state != MyIntents.Types.ERROR) {
//					addTask(songLink);
//				} else {
//					if (songLink.link.download_state == MyIntents.Types.COMPLETE) {
//						Intent nofityIntent = new Intent(
//								"mp3ready.ui.ListFragment");
//						nofityIntent.putExtra(MyIntents.TYPE,
//								MyIntents.Types.ADD);
//						nofityIntent.putExtra(MyIntents.URL, songLink.link.url);
//						nofityIntent.putExtra(MyIntents.TITLE,
//								songLink.link.title);
//						nofityIntent.putExtra(MyIntents.PROCESS_PROGRESS,
//								songLink.link.download_progress + "");
//						Log.e("DownloadManager", songLink.link.downloaded_path);
//						nofityIntent.putExtra(MyIntents.PATH,
//								songLink.link.downloaded_path);
//						nofityIntent.putExtra(MyIntents.ERROR_CODE, "");
//						mContext.sendBroadcast(nofityIntent);
//					} else if (songLink.link.download_state == MyIntents.Types.ERROR) {
//						Intent nofityIntent = new Intent(
//								"mp3ready.ui.ListFragment");
//						nofityIntent.putExtra(MyIntents.TYPE,
//								MyIntents.Types.ADD);
//						nofityIntent.putExtra(MyIntents.URL, songLink.link.url);
//						nofityIntent.putExtra(MyIntents.TITLE,
//								songLink.link.title);
//						nofityIntent.putExtra(MyIntents.PROCESS_PROGRESS,
//								songLink.link.download_progress + "");
//						Log.e("DownloadManager", songLink.link.downloaded_path);
//						nofityIntent.putExtra(MyIntents.ERROR_CODE, "Error");
//						mContext.sendBroadcast(nofityIntent);
//					}
//
//				}
//			}
		}
	}

	// public void checkUncompleteTasks() {
	// //List<String> urlList = new ArrayList<String>();
	// for (int j = 0; j < URL_COUNT; j++) {
	// String url = mem.getUrl(j);
	// String title = mem.getTitle(j);
	//
	// if (!TextUtils.isEmpty(url)) {
	// Log.e("DownloadManager(checkUncompleteTasks)", url);
	// addTask(url,title);
	// }
	// }
	// }

	public synchronized void pauseTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				pauseTask(task);
			}
		}
	}

	public synchronized void pauseAllTask() {

		DownloadTask task;

		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			mTaskQueue.remove(task);
			mPausingTasks.add(task);
		}

		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null) {
				pauseTask(task);
			}
		}
	}

	/**
	 * delete download task from queues and stop its asyncTask delete song file
	 * if exist in device delete download item from downloads playlist
	 * 
	 * @param url
	 * @param deleteItFromDB
	 *            true should delete it from db , false shouldn't delete it from
	 *            db
	 */
	public synchronized void deleteTask(String url, boolean deleteItFromDB) {

		DownloadTask task = null;
		boolean taskFound = false;
		Log.e("DownloadManager", "delete task from db:" + deleteItFromDB);
		int res;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				File file = task.getFile();
				if (file.exists())
					file.delete();

				taskFound = true;
				mDownloadingTasks.remove(task);
				if (deleteItFromDB) {
					Log.e("DownloadManager", "delete from mDownloadingTasks");
					res = db.deleteSongFromPL(task.getUrl(), "",
							Enums.PLAYLIST_DOWNLOAD_LIST);
					if (res != -1) {
						Log.e("DownloadManager",
								"delete from mDownloadingTasks");
					}
				}
			}
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			if (task != null && task.getUrl().equals(url)) {
				File file = task.getFile();
				if (file.exists())
					file.delete();
				mTaskQueue.remove(task);
				taskFound = true;
				if (deleteItFromDB) {
					Log.e("DownloadManager", "delete from mTaskQueue");
					res = db.deleteSongFromPL(task.getUrl(), "",
							Enums.PLAYLIST_DOWNLOAD_LIST);
					if (res != -1) {
						Log.e("DownloadManager", "delete from mTaskQueue");
					}
				}

			}
		}
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				File file = task.getFile();
				if (file.exists())
					file.delete();
				mPausingTasks.remove(task);
				taskFound = true;
				if (deleteItFromDB) {
					Log.e("DownloadManager", "delete from mPausingTasks");
					res = db.deleteSongFromPL(task.getUrl(), "",
							Enums.PLAYLIST_DOWNLOAD_LIST);
					if (res != -1) {
						Log.e("DownloadManager", "delete from mPausingTasks");
					}
				}

			}
		}
		if (taskFound && task != null) {
			task.onCancelled();
		} else {

			try {
				File file = new File(StorageUtils.FILE_ROOT, new File(new URL(
						url).getFile()).getName());
				if (file.exists())
					file.delete();
				if (deleteItFromDB) {
					Log.e("DownloadManager", "delete from db");
					res = db.deleteSongFromPL(url, "",
							Enums.PLAYLIST_DOWNLOAD_LIST);
					if (res != -1) {
						Log.e("DownloadManager", "delete from db");
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public synchronized void continueTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				continueTask(task);
			}

		}
	}

	public synchronized void pauseTask(DownloadTask task) {

		if (task != null) {
			long progress = task.getDownloadPercent();
			task.onCancelled();

			// move to pausing list
			String url = task.getUrl();
			String title = task.getTitle();
			String artist = task.getArtist();
			try {
				mDownloadingTasks.remove(task);
				task = newDownloadTask(url, title, artist);
				task.setDownloadPercent(progress);
				mPausingTasks.add(task);
				Log.e("DownloadManger", "pauseTask");
				long updated = db.updateSongInPlayList(url, "", task.getFile()
						.getAbsolutePath(), MyIntents.Types.PAUSE,
						(int) progress);
				Log.e("DownloadManager", "paused:" + updated);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void continueTask(DownloadTask task) {

		if (task != null) {
			mPausingTasks.remove(task);
			Log.e("DownloadManger", "continueTask");
			db.updateSongInPlayList(task.getUrl(), "", task.getFile()
					.getAbsolutePath(), MyIntents.Types.START, (int) task
					.getDownloadPercent());
			mTaskQueue.offer(task);
		}
	}

	public synchronized void completeTask(DownloadTask task) {

		if (mDownloadingTasks.contains(task)) {

			mDownloadingTasks.remove(task);

			long updated = db
					.updateSongInPlayList(task.getUrl(), "", task.getFile()
							.getAbsolutePath(), MyIntents.Types.COMPLETE, 100);
			if (updated != -1) {
				Log.e("DownloadManager", "complete:" + task.getUrl());
			}
			// notify list changed
			Intent nofityIntent = new Intent("mp3ready.ui.ListFragment");
			nofityIntent.putExtra(MyIntents.TYPE, MyIntents.Types.COMPLETE);
			nofityIntent.putExtra(MyIntents.URL, task.getUrl());
			Log.e("DownloadManager", task.getFile().getAbsolutePath());
			nofityIntent.putExtra(MyIntents.PATH, task.getFile()
					.getAbsolutePath());
			mContext.sendBroadcast(nofityIntent);
		}
	}

	/**
	 * Create a new download task with default config
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	private DownloadTask newDownloadTask(String url, String title, String artist)
			throws MalformedURLException {

		DownloadTaskListener taskListener = new DownloadTaskListener() {

			@Override
			public void updateProcess(DownloadTask task) {
				// Log.e("DownloadManger", "updateProcess:"+task.getUrl());
				Intent updateIntent = new Intent("mp3ready.ui.ListFragment");
				updateIntent.putExtra(MyIntents.TYPE, MyIntents.Types.PROCESS);
				// updateIntent.putExtra(MyIntents.PROCESS_SPEED,
				// task.getDownloadSpeed() + "kbps | "
				// + task.getDownloadSize() + " / " + task.getTotalSize());
				updateIntent.putExtra(MyIntents.PROCESS_PROGRESS,
						task.getDownloadPercent() + "");
				updateIntent.putExtra(MyIntents.URL, task.getUrl());
				mContext.sendBroadcast(updateIntent);
			}

			@Override
			public void preDownload(DownloadTask task) {
				Log.e("DownloadManger", "preDownload:" + task.getUrl());
				// mem.seturl(task.getUrl(), mDownloadingTasks.indexOf(task));
				// mem.setTitle(task.getTitle(),
				// mDownloadingTasks.indexOf(task));
				// ConfigUtils.storeURL(mContext,
				// mDownloadingTasks.indexOf(task), task.getUrl());
			}

			@Override
			public void finishDownload(DownloadTask task) {
				Log.e("DownloadManger", "finishDownload:" + task.getUrl());
				completeTask(task);
			}

			@Override
			public void errorDownload(DownloadTask task, Throwable error) {

				long errorupdated = db.updateSongInPlayList(task.getUrl(), "",
						"", MyIntents.Types.ERROR,
						(int) task.getDownloadPercent());
				if (errorupdated != -1) {
					Log.e("DownloadManager", "Error:" + task.getUrl());
				}
				deleteTask(task.getUrl(), false);
				if (error != null) {
					Log.e("DownloadManager",
							error.getMessage() + ":" + task.getUrl());
				}
				Intent errorIntent = new Intent("mp3ready.ui.ListFragment");
				errorIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ERROR);
				// errorIntent.putExtra(MyIntents.ERROR_CODE, error);
				// errorIntent.putExtra(MyIntents.ERROR_INFO,error.getMessage());
				errorIntent.putExtra(MyIntents.PROCESS_PROGRESS,
						task.getDownloadPercent() + "");
				errorIntent.putExtra(MyIntents.URL, task.getUrl());
				mContext.sendBroadcast(errorIntent);

			}
		};
		return new DownloadTask(mContext, url, title, artist,
				StorageUtils.FILE_ROOT, taskListener);
	}

	/**
	 * A obstructed task queue
	 * 
	 * @author Yingyi Xu
	 */
	private class TaskQueue {
		private Queue<DownloadTask> taskQueue;

		public TaskQueue() {

			taskQueue = new LinkedList<DownloadTask>();
		}

		public void offer(DownloadTask task) {

			taskQueue.offer(task);
		}

		public DownloadTask poll() {

			DownloadTask task = null;
			// task = taskQueue.poll();
			while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
					|| (task = taskQueue.poll()) == null) {
				try {
					Thread.sleep(1000); // sleep
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return task;
		}

		public DownloadTask get(int position) {

			if (position >= size()) {
				return null;
			}
			return ((LinkedList<DownloadTask>) taskQueue).get(position);
		}

		public int size() {

			return taskQueue.size();
		}

		@SuppressWarnings("unused")
		public boolean remove(int position) {

			return taskQueue.remove(get(position));
		}

		public boolean remove(DownloadTask task) {

			return taskQueue.remove(task);
		}
	}

}
