package mp3ready.download.services;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import mp3ready.exceptions.FileAlreadyExistException;
import mp3ready.exceptions.NoMemoryException;
import mp3ready.http.AndroidHttpClient;
import mp3ready.util.StorageUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.zgm.zlib.http.HttpAPI;

public class DownloadTask extends AsyncTask<Void, Integer, Long> {

	public final static int TIME_OUT = 30000;
	private final static int BUFFER_SIZE = 1024 * 8;

	private static final String TAG = "DownloadTask";
	private static final boolean DEBUG = true;
	private static final String TEMP_SUFFIX = ".download";

	private URL URL;
	private File file;
	private File tempFile;
	private String url;
	private String title;
	private String artist;
	private RandomAccessFile outputStream;
	private DownloadTaskListener listener;
	private Context context;

	private long downloadSize;
	private long previousFileSize;
	private long totalSize;
	private long downloadPercent;
	private long networkSpeed;
	private long previousTime;
	private long totalTime;
	private Throwable error = null;
	private boolean interrupt = false;

	private HttpAPI api;

	private final class ProgressReportingRandomAccessFile extends
			RandomAccessFile {
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {

			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int offset, int count)
				throws IOException {

			super.write(buffer, offset, count);
			progress += count;
			publishProgress(progress);
		}
	}

	public DownloadTask(Context context, String url, String title,
			String artist, String path) throws MalformedURLException {

		this(context, url, title, artist, path, null);
	}

	public DownloadTask(Context context, String url, String title,
			String artist, String path, DownloadTaskListener listener)
			throws MalformedURLException {

		super();
		this.url = url;
		this.title = title;
		this.artist = artist;
		this.URL = new URL(url);
		this.listener = listener;
		if (artist != null && !artist.equals("")) {
			String fileName = this.artist + " - " + this.title + ".mp3";
			this.file = new File(path, fileName);
			// TEMP_SUFFIX
			this.tempFile = new File(path, fileName + TEMP_SUFFIX);
		} else {
			String fileName = new File(URL.getFile()).getName();
			this.file = new File(path, fileName);
			// TEMP_SUFFIX
			this.tempFile = new File(path, fileName + TEMP_SUFFIX);
		}
		this.context = context;
		api = new HttpAPI(context,true);
	}

	public File getFile() {
		return this.file;
	}

	public String getUrl() {

		return url;
	}

	public String getTitle() {

		return title;
	}

	public String getArtist() {

		return artist;
	}

	public boolean isInterrupt() {

		return interrupt;
	}

	public long getDownloadPercent() {

		return downloadPercent;
	}

	public void setDownloadPercent(long mdownloadPercent) {

		downloadPercent = mdownloadPercent;
		;
	}

	public long getDownloadSize() {

		return downloadSize + previousFileSize;
	}

	public long getTotalSize() {

		return totalSize;
	}

	// public long getDownloadSpeed() {
	//
	// return this.networkSpeed;
	// }

	public long getTotalTime() {

		return this.totalTime;
	}

	public DownloadTaskListener getListener() {

		return this.listener;
	}

	@Override
	protected void onPreExecute() {

		previousTime = System.currentTimeMillis();
		Log.e("DownlaodTask", "onPreExecute()");
		if (listener != null)
			listener.preDownload(this);
	}

	@Override
	protected Long doInBackground(Void... params) {

		long result = -1;
		try {
			result = download();
		} catch (NetworkErrorException e) {
			error = e;
		} catch (FileAlreadyExistException e) {
			error = e;
		} catch (NoMemoryException e) {
			error = e;
		} catch (IOException e) {
			error = e;
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {

		if (progress.length > 1) {
			totalSize = progress[1];
			if (totalSize == -1) {
				if (listener != null)
					listener.errorDownload(this, error);
			} else {

			}
		} else {
			totalTime = Math.abs(System.currentTimeMillis() - previousTime);
			downloadSize = progress[0];
			downloadPercent = (downloadSize + previousFileSize) * 100
					/ totalSize;
			networkSpeed = downloadSize / totalTime;
			if (listener != null)
				listener.updateProcess(this);
		}
	}

	@Override
	protected void onPostExecute(Long result) {

		if (result <= 1024 || error != null) {
			Log.e(TAG, "result:" + result);
			if (DEBUG && error != null) {
				Log.e(TAG, "Download failed." + error.getMessage());
			}
			if (listener != null) {
				listener.errorDownload(this, error);
			}
			return;
		}
		if (interrupt) {
			return;
		}
		// finish download
		tempFile.renameTo(file);
		if (listener != null)
			if (getDownloadPercent() >= 100) {
				listener.finishDownload(this);
			} else {
				Log.e(TAG, "result:" + result);
				listener.errorDownload(this, new NetworkErrorException());
			}
	}

	@Override
	public void onCancelled() {

		super.onCancelled();
		interrupt = true;
	}

	private AndroidHttpClient client;
	private HttpGet httpGet;
	private HttpResponse response;

	private long download() throws NetworkErrorException, IOException,
			FileAlreadyExistException, NoMemoryException {

		/*
		 * check net work
		 */
		// if (!NetworkUtils.isNetworkAvailable(context)) {
		// throw new NetworkErrorException("Network blocked.");
		// }

		/*
		 * check file length
		 */
		client = AndroidHttpClient.newInstance("DownloadTask");

		httpGet = new HttpGet(url);
		response = client.execute(httpGet);
		totalSize = response.getEntity().getContentLength();
		if (DEBUG) {
			Log.e(TAG, "totalSize: " + totalSize);
		}
		if (file.exists() && totalSize == file.length()) {
			if (DEBUG) {
				Log.e(null, "Output file already exists. Skipping download.");
			}

			throw new FileAlreadyExistException(
					"Output file already exists. Skipping download.");
		} else if (tempFile.exists()) {
			httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
			previousFileSize = tempFile.length();

			client.close();
			client = AndroidHttpClient.newInstance("DownloadTask");
			response = client.execute(httpGet);

			if (DEBUG) {
				Log.e(TAG, "File is not complete, download now.");
				Log.e(TAG, "File length:" + tempFile.length() + " totalSize:"
						+ totalSize);
			}
		}

		/*
		 * check memory
		 */
		long storage = StorageUtils.getAvailableStorage();
		if (DEBUG) {
			Log.i(null, "storage:" + storage + " totalSize:" + totalSize);
		}

		if (totalSize - tempFile.length() > storage) {
			throw new NoMemoryException("SD card no memory.");
		}

		/*
		 * start download
		 */
		outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");

		publishProgress(0, (int) totalSize);

		InputStream input = response.getEntity().getContent();
		int bytesCopied = copy(input, outputStream);

		if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1
				&& !interrupt) {
			throw new IOException("Download incomplete: " + bytesCopied
					+ " != " + totalSize);
		}

		if (DEBUG) {
			Log.e(TAG, "Download completed successfully.");
		}

		return bytesCopied;

	}

	public int copy(InputStream input, RandomAccessFile out)
			throws IOException, NetworkErrorException {

		if (input == null || out == null) {
			return -1;
		}

		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		if (DEBUG) {
			Log.v(TAG, "length" + out.length());
		}

		int count = 0, n = 0;
		long errorBlockTimePreviousTime = -1, expireTime = 0;

		try {

			out.seek(out.length());

			while (!interrupt) {
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;

				/*
				 * check network
				 */
				// if (!NetworkUtils.isNetworkAvailable(context)) {
				// throw new NetworkErrorException("Network blocked.");
				// }

				if (networkSpeed == 0) {
					if (errorBlockTimePreviousTime > 0) {
						expireTime = System.currentTimeMillis()
								- errorBlockTimePreviousTime;
						if (expireTime > TIME_OUT) {
							throw new ConnectTimeoutException(
									"connection time out.");
						}
					} else {
						errorBlockTimePreviousTime = System.currentTimeMillis();
					}
				} else {
					expireTime = 0;
					errorBlockTimePreviousTime = -1;
				}
			}
		} finally {
			client.close(); // must close client first
			client = null;
			out.close();
			in.close();
			input.close();
		}
		return count;

	}

}
