//package mp3ready.http;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.net.URL;
//import java.net.URLConnection;
//
//import zgm.http.CallBack;
//import zgm.zdlib.httpcaching.DataSource;
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.zgm.mp3ready.R;
//
///**
// * AsynTask request to allow the user to download file from http url and notify
// * the user when the download finish
// *
// * @author mhmd
// *
// */
//public class DownloadFile extends AsyncTask<String, String, String> {
//	CharSequence contentText;
//	CharSequence contentTitle;
//	PendingIntent contentIntent;
//	int HELLO_ID = 1;
//	long time;
//	int icon;
//	private Context ctx;
//	private String name;
//	int downloadProgress = 0;
//	CallBack cb;
//	CharSequence tickerText;
//	File file;
//	private NotificationCompat.Builder mBuilder;
//	NotificationManager notificationManager;
//	Notification notification;
//
//	public DownloadFile(Context ctx, String name, CallBack cb) {
//		this.ctx = ctx;
//		this.name = name;
//		this.cb = cb;
//	}
//
//	public void downloadNotification() {
//
//		notificationManager = (NotificationManager) ctx
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//
//		// the text that appears first on the status bar
//		tickerText = "Downloading...";
//		time = System.currentTimeMillis();
//		icon = R.drawable.ic_launcher;
//		// notification = new Notification(icon, tickerText, time);
//		// the bold font
//		contentTitle = this.name;
//		// the text that needs to change
//		contentText = "0%";
//		mBuilder = new NotificationCompat.Builder(ctx);
//		mBuilder.setSmallIcon(icon);
//
//		mBuilder.setContentTitle(contentTitle);
//		mBuilder.setStyle(new NotificationCompat.BigTextStyle()
//				.bigText(contentText));
//		mBuilder.setContentText(contentText);
//
//		Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
//
//		notificationIntent.setType("text/*");
//		// notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
//		// Intent.FLAG_ACTIVITY_CLEAR_TOP );
//
//		contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent,
//				PendingIntent.FLAG_UPDATE_CURRENT);
//		mBuilder.setContentIntent(contentIntent);
//		// notification.setLatestEventInfo(ctx, contentTitle, contentText,
//		// contentIntent);
//
//		notificationManager.notify(HELLO_ID, mBuilder.build());
//
//	}
//
//	@Override
//	protected void onPreExecute() {
//		// execute the status bar notification
//		downloadNotification();
//		super.onPreExecute();
//	}
//
//	@Override
//	protected String doInBackground(String... param) {
//		int count;
//		String urlStr = param[0];
//		String dirPath = param[1];
//		String name = param[2];
//		String savePath = dirPath + name;
//		try {
//			Log.i("DownloadLecture(url)", urlStr);
//			Log.i("DownloadLecture(savePath)", savePath);
//			URL url = new URL(urlStr);
//
//			URLConnection conection = url.openConnection();
//			conection.connect();
//			// getting file length
//			int lenghtOfFile = conection.getContentLength();
//			try {
//				String dbName = ctx.getPackageName().replace(".", "_")
//						+ "_cache";
//				DataSource dblib = new DataSource(ctx, dbName);
//				dblib.prepareDB();
//				dblib.open();
//				dblib.updateBandwidth(0, lenghtOfFile);
//				dblib.close();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			Log.i("DownloadLecture", lenghtOfFile + "");
//			// input stream to read file - with 8k buffer
//			InputStream input = new BufferedInputStream(url.openStream(), 8192);
//
//			// Output stream to write file
//			OutputStream output = new FileOutputStream(savePath);
//
//			byte items[] = new byte[1024];
//
//			long total = 0;
//			while ((count = input.read(items)) != -1) {
//				total += count;
//				int currentProgress = (int) ((total * 100) / lenghtOfFile);
//				// Log.i("DownloadLecture", total+"");
//				if ((currentProgress % 5 == 0)
//						&& (currentProgress > downloadProgress)) {
//					Log.i("Download progress after", currentProgress + "");
//					downloadProgress = currentProgress;
//					publishProgress("" + currentProgress);
//				}
//				output.write(items, 0, count);
//			}
//
//			output.flush();
//			output.close();
//			input.close();
//
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		return savePath;
//	}
//
//	@Override
//	protected void onPostExecute(String result) {
//
//		notificationManager.cancel(HELLO_ID);
//		cb.onFinished(result);
//	}
//
//	@Override
//	public void onProgressUpdate(String... progress) {
//		contentText = Integer.parseInt(progress[0]) + "%";
//		if (Integer.parseInt(progress[0]) == 100) {
//			// contentText = "download complete";
//			mBuilder.setAutoCancel(true);
//			//
//		}
//		// Log.i("Download progress", progress[0]);
//
//		mBuilder.setProgress(100, Integer.valueOf(progress[0]).intValue(),
//				false);
//        mBuilder.setContentTitle(contentTitle);
//        mBuilder.setContentText(contentText);
//        mBuilder.setContentIntent(contentIntent);
//
////		 notification.setLatestEventInfo(ctx, contentTitle, contentText,
////		 contentIntent);
//		mBuilder.setContentText(contentText);
//		notificationManager.notify(HELLO_ID, mBuilder.build());
//		// super.onProgressUpdate(progress);
//	}
//}