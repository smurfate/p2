package mp3ready.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import mp3ready.ui.MainActivity;
import mp3ready.util.MyIntents;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zgm.mp3ready.R;

public class DownloadsAdatper extends BaseAdapter {
	public static final int KEY_URL = 0;
	public static final int KEY_SPEED = 1;
	public static final int KEY_PROGRESS = 2;
	public static final int KEY_IS_PAUSED = 3;
	public static final int KEY_TITLE = 4;
	public static final int KEY_ERROR = 5;

	private LayoutInflater inflater;
	private ArrayList<HashMap<Integer, String>> dataList;
	private MainActivity context;

	public DownloadsAdatper(MainActivity context) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		dataList = new ArrayList<HashMap<Integer, String>>();

	}

	public int getCount() {
		return dataList.size();
	}

	public Object getItem(int position) {
		return this.dataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * adding download item into list of downloads
	 * 
	 * @param url
	 *            the url of song that we want to download
	 * @param title
	 *            song title
	 * @param progress
	 *            the progress of download
	 * @param isPaused
	 *            flag to know if the download paused or not
	 * @param error
	 *            error text if exist
	 */
	public void addItem(String url, String title, String progress,
			boolean isPaused, String error) {

		HashMap<Integer, String> item = ViewHolder.getItemDataMap(url, null,
				progress, isPaused, title, error);
		if (item != null) {
			Log.i("DownloadApadpter", "addItem");
			dataList.add(item);
			this.notifyDataSetChanged();
		}
	}

	/**
	 * update download item to change the progress
	 * 
	 * @param url
	 *            the url of song that we want to download
	 * @param speed
	 *            unused
	 * @param progress
	 *            the progress that we want to update it
	 * @param error
	 *            error text if exist
	 */
	public void updateItem(String url, String speed, String progress,
			String error) {
		String tmp;
		for (int i = 0; i < dataList.size(); i++) {
			tmp = dataList.get(i).get(KEY_URL);
			if (tmp.equals(url)) {
				HashMap<Integer, String> updateditem = dataList.get(i);
				dataList.remove(i);
				updateditem.put(KEY_SPEED, speed);
				updateditem.put(KEY_PROGRESS, progress);
				updateditem.put(KEY_ERROR, error);
				dataList.add(i, updateditem);
				this.notifyDataSetChanged();
			}
		}
	}

	public void removeItem(String url) {
		String tmp;
		for (int i = 0; i < dataList.size(); i++) {
			tmp = dataList.get(i).get(KEY_URL);
			if (tmp.equals(url)) {
				dataList.remove(i);
				this.notifyDataSetChanged();
			}
		}
	}

	/**
	 * holder the views of download item and build hash map on them
	 * 
	 * @author mhmd
	 * 
	 */
	public static class ViewHolder {

		public TextView tv_song_title;
		public ProgressBar song_download_pb;
		public TextView tv_progress;
		public TextView tv_msg;
		public ImageButton pauseplayButton;
		public ImageButton delete_task;
		private boolean hasInited = false;

		public ViewHolder(View parentView) {
			if (parentView != null) {
				tv_song_title = (TextView) parentView
						.findViewById(R.id.tv_link_title);
				pauseplayButton = (ImageButton) parentView
						.findViewById(R.id.play_pause);
				delete_task = (ImageButton) parentView
						.findViewById(R.id.delete_task);
				song_download_pb = (ProgressBar) parentView
						.findViewById(R.id.song_download_progressBar);
				tv_progress = (TextView) parentView
						.findViewById(R.id.tv_progress);
				tv_msg = (TextView) parentView.findViewById(R.id.tv_msg);
				hasInited = true;
			}
		}

		/**
		 * build hash map of download item's items
		 * 
		 * @param url
		 *            the url of song that we want to download
		 * @param speed
		 *            unused
		 * @param progress
		 *            the progress of download
		 * @param isPaused
		 *            flag to know if download paused or not
		 * @param title
		 *            song title
		 * @param error
		 *            error text if exist
		 * @return hash map of all above items
		 */
		public static HashMap<Integer, String> getItemDataMap(String url,
				String speed, String progress, boolean isPaused, String title,
				String error) {
			HashMap<Integer, String> item = new HashMap<Integer, String>();
			item.put(KEY_URL, url);
			item.put(KEY_SPEED, speed);
			item.put(KEY_PROGRESS, progress);
			if (isPaused) {

				Log.e("DownloadAdapter", "set paused 1");
				item.put(KEY_IS_PAUSED, "1");
			} else {

				Log.e("DownloadAdapter", "set paused 0");
				item.put(KEY_IS_PAUSED, "0");
			}
			item.put(KEY_TITLE, title);
			item.put(KEY_ERROR, error);
			return item;
		}

	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.download_item, null);

		}
		final ViewHolder holder = new ViewHolder(convertView);
		final HashMap<Integer, String> itemData = dataList.get(position);
		if (itemData != null) {
			holder.tv_song_title.setText(itemData.get(KEY_TITLE));
			String progress = itemData.get(KEY_PROGRESS);
			String error = itemData.get(KEY_ERROR);
			if (error != null && !error.equals("")) {
				holder.song_download_pb.setVisibility(View.GONE);
				holder.tv_progress.setVisibility(View.GONE);
				holder.tv_msg.setVisibility(View.VISIBLE);
				holder.tv_msg.setText("Error");
				holder.tv_msg.setTextColor(context.getResources().getColor(
						R.color.red));
				holder.pauseplayButton
						.setBackgroundResource(R.drawable.btn_download_retry_bg);

			} else {
				holder.song_download_pb.setVisibility(View.VISIBLE);
				holder.tv_progress.setVisibility(View.VISIBLE);
				holder.tv_msg.setVisibility(View.GONE);
				if (TextUtils.isEmpty(progress)) {
					holder.tv_progress.setText("0%");
					holder.song_download_pb.setProgress(0);

				} else {
					if (progress.equals("100")) {
						holder.song_download_pb.setVisibility(View.GONE);
						holder.pauseplayButton.setVisibility(View.GONE);
						holder.tv_progress.setVisibility(View.GONE);
						holder.tv_msg.setVisibility(View.VISIBLE);
						holder.tv_msg.setText("Complete");
						holder.tv_msg.setTextColor(context.getResources()
								.getColor(R.color.green_toggle_btn));
					} else {
						holder.tv_progress.setText(progress + "%");
						holder.song_download_pb.setProgress(Integer
								.parseInt(progress));
					}

				}
			}
			holder.pauseplayButton.setTag(position);
			String intentType = dataList.get(position).get(KEY_IS_PAUSED);
			String isError = dataList.get(position).get(KEY_ERROR);
			if (isError != null && !isError.equals("")) {
				holder.pauseplayButton
						.setBackgroundResource(R.drawable.btn_download_retry_bg);
			} else if (intentType.equals("0")) {
				holder.pauseplayButton
						.setBackgroundResource(R.drawable.btn_download_pause_bg);
			} else if (intentType.equals("1")) {
				holder.pauseplayButton
						.setBackgroundResource(R.drawable.btn_download_play_bg);
			}
			/**
			 * play and pause the download send request to download service to
			 * execute the download (start/resume it) or pause it if download
			 * show error then we show retry button to let user to retry the
			 * download from begening
			 */
			holder.pauseplayButton
					.setOnClickListener(new View.OnClickListener() {

						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						@Override
						public void onClick(View v) {

							Integer pos = (Integer) v.getTag();
							String intentType = dataList.get(pos).get(
									KEY_IS_PAUSED);
							String isError = dataList.get(pos).get(KEY_ERROR);
							Intent downloadIntent = new Intent(
									"mp3ready.download.services.IDownloadService");
							if (isError != null && !isError.equals("")) {

								downloadIntent.putExtra(MyIntents.TYPE,
										MyIntents.Types.RETRY);
								downloadIntent.putExtra(MyIntents.URL,
										itemData.get(KEY_URL));
								context.startService(downloadIntent);
								holder.pauseplayButton
										.setBackgroundResource(R.drawable.btn_download_pause_bg);

							} else {

								Log.i("DownloadAdapter", intentType + "");
								if (intentType.equals("0")) {
									Log.i("DownloadAdapter", "pause");
									holder.pauseplayButton
											.setBackgroundResource(R.drawable.btn_download_play_bg);
									dataList.get(pos).put(KEY_IS_PAUSED, "1");
									String intentTypesss = (dataList.get(pos)
											.get(KEY_IS_PAUSED));
									Log.i("DownloadAdapter", intentTypesss + "");

									downloadIntent.putExtra(MyIntents.TYPE,
											MyIntents.Types.PAUSE);
									downloadIntent.putExtra(MyIntents.URL,
											itemData.get(KEY_URL));
									context.startService(downloadIntent);

								} else if (intentType.equals("1")) {
									Log.i("DownloadAdapter", "CONTINUE");
									holder.pauseplayButton
											.setBackgroundResource(R.drawable.btn_download_pause_bg);
									dataList.get(pos).put(KEY_IS_PAUSED, "0");
									String intentTypesss = (dataList.get(pos)
											.get(KEY_IS_PAUSED));
									Log.i("DownloadAdapter", intentTypesss + "");
									downloadIntent.putExtra(MyIntents.TYPE,
											MyIntents.Types.CONTINUE);
									downloadIntent.putExtra(MyIntents.URL,
											itemData.get(KEY_URL));
									context.startService(downloadIntent);

								}
							}
						}
					});
			/**
			 * delete download send request to download service to cancel the
			 * download
			 */
			holder.delete_task.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					removeItem(itemData.get(KEY_URL));
					Intent deleteIntent = new Intent(
							"mp3ready.download.services.IDownloadService");
					deleteIntent.putExtra(MyIntents.TYPE,
							MyIntents.Types.DELETE);
					deleteIntent.putExtra(MyIntents.URL, itemData.get(KEY_URL));
					context.startService(deleteIntent);

				}
			});
		}

		//

		return convertView;
	}

}