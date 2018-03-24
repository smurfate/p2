package mp3ready.adapters;

import java.util.ArrayList;
import java.util.List;

import mp3ready.entities.PlayList;
import mp3ready.ui.MainActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgm.mp3ready.R;

public abstract class PlayListsAdatper extends BaseAdapter {
	private LayoutInflater inflater;
	private List<PlayList> mPlayLists;
	private MainActivity context;
	private String desiredType = null;
	private boolean isOptionsVisible;

	public PlayListsAdatper(MainActivity context, String desiredType,
			boolean isOptionsVisible) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.desiredType = desiredType;
		this.mPlayLists = new ArrayList<PlayList>();
		this.isOptionsVisible = isOptionsVisible;
	}

	public int getCount() {
		return mPlayLists.size();
	}

	public Object getItem(int position) {
		return this.mPlayLists.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public void deletePlayList(PlayList pl) {
		this.mPlayLists.remove(pl);
		notifyDataSetChanged();

	}

	public void updatePlayList(PlayList newPl) {
		int i = 0;
		PlayList oldPL = null;
		for (PlayList pl : mPlayLists) {
			if (pl.id.equals(newPl.id)) {
				oldPL = pl;
				break;
			}
			i++;
		}
		if (oldPL != null) {
			Log.i("PlayListAdapter", this.mPlayLists.get(i).name);
			this.mPlayLists.get(i).name = newPl.name;
			Log.i("PlayListAdapter", this.mPlayLists.get(i).name);
			this.mPlayLists.remove(oldPL);
			this.mPlayLists.add(i, newPl);
			notifyDataSetChanged();
		}

	}

	public void loadMorePLS(List<PlayList> newPLS) {
		if (newPLS != null && newPLS.size() > 0) {
			if (this.desiredType != null) {
				for (PlayList pl : newPLS) {
					if ( pl.type!=null && pl.type.equals(this.desiredType)) {
						this.mPlayLists.add(pl);
					}
				}
			} else {
				this.mPlayLists.addAll(newPLS);
			}
		}
		notifyDataSetChanged();
	}

	static class ViewHolder {

		ImageView iv_plCover;
		TextView tv_pl_name;
		TextView tv_pl_track_count;
		ImageButton ib_popupmenu_options;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.play_list_item, null);
			holder = new ViewHolder();
			holder.iv_plCover = (ImageView) convertView
					.findViewById(R.id.pl_cover);
			holder.tv_pl_name = (TextView) convertView
					.findViewById(R.id.tv_pl_title);
			holder.tv_pl_track_count = (TextView) convertView
					.findViewById(R.id.tv_track_count);
			holder.ib_popupmenu_options = (ImageButton) convertView
					.findViewById(R.id.popup_menu);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlayList playlist = (PlayList) getItem(position);
		if (this.isOptionsVisible) {
			holder.ib_popupmenu_options.setVisibility(View.VISIBLE);
			holder.ib_popupmenu_options.setTag(playlist);
			holder.ib_popupmenu_options
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							// Song s =(Song)v.getTag();
							popUpMenu(v);
						}
					});
		} else {
			holder.ib_popupmenu_options.setVisibility(View.GONE);
		}
		if (playlist.name != null) {
			holder.tv_pl_name.setText(playlist.name);
		}
		if (playlist.name != null) {
//			holder.tv_pl_track_count.setText(playlist.total_count + " Tracks");
			holder.tv_pl_track_count.setText("");
		} else {
			holder.tv_pl_track_count.setText("0 Tracks");
		}
		return convertView;
	}

	protected abstract void popUpMenu(View btn);

}