package mp3ready.adapters;

import mp3ready.ui.MainActivity;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zgm.mp3ready.R;

public class DrawerAdapter extends BaseAdapter {
	final static String TAG = "DrawerAdapter";
	private MainActivity context;
	private String[] Dtitle;
	private TypedArray mNavIcons;
	private TypedArray mNavPressedIcons;
	private int selectedItemPos = -1;
	private int downloadsPos = 0;
	private int recommendPos = 0;
	private int recommenedSongs = 0;
	private int pendingDownloads = 0;
	private boolean isExportReportVisible = false;

	public DrawerAdapter(MainActivity context) {
		this.context = context;

	}

	@Override
	public int getCount() {
		
		return Dtitle.length;
	}

	@Override
	public Object getItem(int arg0) {
		
		return Dtitle[arg0];
	}

	public void refreshDataSource() {
		if (this.context.mem.isAppAuthed()) {
			this.Dtitle = this.context.getResources().getStringArray(
					R.array.user_nav_array);
			// this.mNavIcons =
			// this.context.getResources().obtainTypedArray(R.array.nav_array_icons
			// ); ;
			// this.mNavPressedIcons =
			// this.context.getResources().obtainTypedArray(R.array.nav_array_pressed_icons);
		} else {
			this.Dtitle = this.context.getResources().getStringArray(
					R.array.nav_array);
			// this.mNavIcons =
			// this.context.getResources().obtainTypedArray(R.array.nav_array_icons
			// ); ;
			// this.mNavPressedIcons =
			// this.context.getResources().obtainTypedArray(R.array.nav_array_pressed_icons);
		}
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int arg0) {
		
		return Long.valueOf(arg0);
	}

	public int getSelectedItemPos() {
		return selectedItemPos;
	}

	public void setSelectedItemPos(int pos) {
		selectedItemPos = pos;
		notifyDataSetChanged();
	}

	public void setPendingDownloadsCountInDrawer(int p, int nc) {
		this.downloadsPos = p;
		this.pendingDownloads = nc;
	}

	public void setRecommendedSongsCountInDrawer(int p, int nc) {
		this.recommendPos = p;
		this.recommenedSongs = nc;
	}

	@Override
	public View getView(int pos, View view, ViewGroup parent) {
		

		try {
			view = LayoutInflater.from(this.context).inflate(
					R.layout.drawer_list_item, null);
			TextView tv = (TextView) view.findViewById(android.R.id.text1);
			TextView tv2 = (TextView) view.findViewById(android.R.id.text2);
			if (tv != null) {
				tv.setText(this.Dtitle[pos]);
				if ((pos == this.downloadsPos) && this.pendingDownloads > 0) {
					tv2.setVisibility(View.VISIBLE);
					tv2.setText(String.valueOf(this.pendingDownloads));
				} else if ((pos == this.recommendPos)
						&& this.recommenedSongs > 0) {
					tv2.setVisibility(View.VISIBLE);
					tv2.setText(String.valueOf(this.recommenedSongs));
				} else {
					tv2.setVisibility(View.GONE);
					tv2.setText("");
				}
				// if (pos == selectedItemPos)
				// tv.setCompoundDrawablesWithIntrinsicBounds(mNavPressedIcons.getResourceId(pos,
				// -1),0 , 0, 0 );
				// else
				// tv.setCompoundDrawablesWithIntrinsicBounds(mNavIcons.getResourceId(pos,
				// -1),0 , 0, 0 );
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return view;
	}

	public void setExportReportVisibility(boolean isVisible) {
		isExportReportVisible = isVisible;
		notifyDataSetChanged();
	}

}
