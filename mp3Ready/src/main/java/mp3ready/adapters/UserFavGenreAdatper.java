package mp3ready.adapters;

import java.util.ArrayList;
import java.util.List;

import mp3ready.response.TopTagSongsResponse;
import mp3ready.ui.MainActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zgm.mp3ready.R;

public class UserFavGenreAdatper extends BaseAdapter {
	private LayoutInflater inflater;
	private List<TopTagSongsResponse> mTopTagSongsResponse;
	private MainActivity context;
	private List<String> selectedItems;

	public UserFavGenreAdatper(MainActivity context,
			List<TopTagSongsResponse> mTopTagSongsResponse) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);

		this.mTopTagSongsResponse = mTopTagSongsResponse;
		selectedItems = new ArrayList<String>();

	}

	public boolean setSelectedItem(int pos) {
		try {
			String val = String.valueOf(pos);
			if (!selectedItems.contains(val)) {
				selectedItems.add(val);
				notifyDataSetChanged();
				return true;
			} else {
				selectedItems.remove(val);
				notifyDataSetChanged();
				return false;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return true;
		}

	}

	public int getCount() {
		return mTopTagSongsResponse.size();
	}

	public Object getItem(int position) {
		return this.mTopTagSongsResponse.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	static class ViewHolder {

		ImageView iv_genreCover;
		TextView tv_genreName;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fav_genre_cell, null);
			holder = new ViewHolder();
			holder.iv_genreCover = (ImageView) convertView
					.findViewById(R.id.genree_cover);
			holder.tv_genreName = (TextView) convertView
					.findViewById(R.id.genre_name);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String val = String.valueOf(position);
		if (this.selectedItems.contains(val)) {
			convertView.setBackgroundResource(R.drawable.listviewselector);

		} else {
			convertView.setBackgroundColor(context.getResources().getColor(
					R.color.light_gray));
			;
		}
		TopTagSongsResponse t = (TopTagSongsResponse) getItem(position);
		holder.tv_genreName.setText(t.tag_name);
		return convertView;
	}

}