package mp3ready.download.services;

import mp3ready.util.AppCache;
import mp3ready.util.MyIntents;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class DownloadService extends Service {

	private DownloadManager mDownloadManager;
	private AppCache mem;

	/*
	 * we shouldn't allow this service to bind to MainActivity so we return null
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		mem = new AppCache(this);
		Log.e("DownloadService", "onCreate");
		if (mDownloadManager == null) {
			mDownloadManager = new DownloadManager(this);
		}
		super.onCreate();

	}

	@Override
	public void onDestroy() {
		
		Log.e("Service", "onDestroy");
		super.onDestroy();
	}

	/**
	 * on start the service
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		if (intent != null
				&& intent.getAction().equals(
						"mp3ready.download.services.IDownloadService")) {
			int type = intent.getIntExtra(MyIntents.TYPE, -1);
			String url;
			String title;
			String artist;
			switch (type) {
			case MyIntents.Types.START:
				if (!mDownloadManager.isRunning()) {
					Log.e("DownloadService", "isNotRunning");
					mDownloadManager.startManage();
				} else {
					Log.e("DownloadService", "isRunning");
					mDownloadManager.reBroadcastAddAllTask();
				}
				break;
			case MyIntents.Types.ADD:
				url = intent.getStringExtra(MyIntents.URL);
				title = intent.getStringExtra(MyIntents.TITLE);
				artist = intent.getStringExtra(MyIntents.ARTIST);
				if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
					Log.e("Downlaod Service", "add in service");
					mDownloadManager.addTask(url, title, artist);
				}
				break;
			case MyIntents.Types.CONTINUE:
				url = intent.getStringExtra(MyIntents.URL);
				if (!TextUtils.isEmpty(url)) {
					mDownloadManager.continueTask(url);
				}
				break;
			case MyIntents.Types.DELETE:
				Log.e("DownloadService", "delete");
				url = intent.getStringExtra(MyIntents.URL);

				if (!TextUtils.isEmpty(url)) {
					mDownloadManager.deleteTask(url, true);
				}
				break;
			case MyIntents.Types.PAUSE:
				url = intent.getStringExtra(MyIntents.URL);
				if (!TextUtils.isEmpty(url)) {
					mDownloadManager.pauseTask(url);
				}
				break;
			case MyIntents.Types.RETRY:
				url = intent.getStringExtra(MyIntents.URL);
				title = intent.getStringExtra(MyIntents.TITLE);
				artist = intent.getStringExtra(MyIntents.ARTIST);
				if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
					Log.e("Downlaod Service", "add in service");
					mDownloadManager.retryTask(url, title, artist);
				}
				break;
			// case MyIntents.Types.STOP:
			// mDownloadManager.finish();
			// // mDownloadManager = null;
			// break;

			default:
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private class DownloadServiceImpl extends IDownloadService.Stub {

		@Override
		public void startManage() throws RemoteException {

			if (mDownloadManager.isRunning() == false) {
				Log.e("DownloadService", "isNotRunning");
				mDownloadManager.startManage();
			} else {
				Log.e("DownloadService", "isRunning");
				mDownloadManager.reBroadcastAddAllTask();
			}
		}

		@Override
		public void addTask(String url, String title) throws RemoteException {

			// mDownloadManager.addTask(url,title);
		}

		@Override
		public void pauseTask(String url) throws RemoteException {
			if (!TextUtils.isEmpty(url)) {
				mDownloadManager.pauseTask(url);
			}
		}

		@Override
		public void deleteTask(String url) throws RemoteException {
			if (!TextUtils.isEmpty(url)) {

			}
		}

		@Override
		public void continueTask(String url) throws RemoteException {
			if (!TextUtils.isEmpty(url)) {
				mDownloadManager.continueTask(url);
			}
		}

	}

}
