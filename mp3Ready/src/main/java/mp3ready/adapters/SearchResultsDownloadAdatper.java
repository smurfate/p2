package mp3ready.adapters;

import java.util.ArrayList;
import java.util.List;

import mp3ready.entities.DownloadLink;
import mp3ready.util.MyIntents;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zgm.mp3ready.R;

public abstract class SearchResultsDownloadAdatper extends BaseAdapter {
	private LayoutInflater inflater;
	private List<DownloadLink> urls;
	private Activity context;

	public SearchResultsDownloadAdatper(Activity context) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.urls = new ArrayList<DownloadLink>();

	}

	public int getCount() {
		return urls.size();
	}

	public Object getItem(int position) {
		return this.urls.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public void loadMoreUrls(List<DownloadLink> newUrls) {
		if (newUrls != null && newUrls.size() > 0) {
			this.urls.addAll(newUrls);
		}
		notifyDataSetChanged();
	}

	static class ViewHolder {

		TextView tv_song_name;

		TextView tv_song_duration;
		TextView tv_song_size;
		TextView tv_song_bitrate;
		ImageButton ib_download;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.link_item, null);
			holder = new ViewHolder();

			holder.tv_song_duration = (TextView) convertView
					.findViewById(R.id.tv_song_duration);

			holder.tv_song_name = (TextView) convertView
					.findViewById(R.id.tv_song_title);
			holder.tv_song_size = (TextView) convertView
					.findViewById(R.id.tv_song_size);
			holder.tv_song_bitrate = (TextView) convertView
					.findViewById(R.id.tv_song_bitrate);
			holder.ib_download = (ImageButton) convertView
					.findViewById(R.id.download_link);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DownloadLink link = (DownloadLink) getItem(position);

		if (link.duration != null) {
			Integer dsec = Integer.valueOf(link.duration);
			Integer minutes = (dsec % 3600) / 60;
			Integer seconds = dsec % 60;

			String timeString = String.format("%02d:%02d", minutes, seconds);
			holder.tv_song_duration.setText(timeString);
		} else {
			holder.tv_song_duration.setText("00:00");
		}
		if (link.title != null) {
			holder.tv_song_name.setText(link.title);

		}
		if (link.download_state == -1) {// no downloaded yet
			holder.ib_download.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.mobayle_dl_free));
			holder.ib_download.setTag(link);
			holder.ib_download.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					v.setEnabled(false);
					((ImageButton) v).setImageDrawable(context.getResources()
							.getDrawable(R.drawable.mobayle_dl_free_run));
					DownloadLink link = (DownloadLink) v.getTag();

					download(link);
				}
			});
		} else if (link.download_state == MyIntents.Types.COMPLETE) {
			holder.ib_download.setEnabled(false);
			holder.ib_download.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.mobayle_dl_done));
		} else {
			holder.ib_download.setEnabled(false);
			holder.ib_download.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.mobayle_dl_free_run));
		}
		if (link.size != null) {
			holder.tv_song_size.setText(link.size);
		}
		if (link.bitrate != null) {
			holder.tv_song_bitrate.setText(link.bitrate);
		}
		return convertView;
	}

	protected abstract void download(DownloadLink link);

}