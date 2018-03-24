package mp3ready.adapters;

import java.util.List;

import mp3ready.entities.Song;
import mp3ready.ui.MainActivity;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zgm.mp3ready.R;

public final class SongsCoverAdapter extends PagerAdapter {

	private List<Song> songs;
	private MainActivity ctx;

	public SongsCoverAdapter(List<Song> songs, MainActivity ctx) {
		// super(fm);
		this.ctx = ctx;

		this.songs = songs;

	}

	@Override
	public int getCount() {

		if (this.songs != null) {
			return this.songs.size();
		} else {
			return 0;
		}
	}

	public Song getItem(int pos) {
		return this.songs.get(pos);
	}

	public void updateSong(int pos, String rate, String like_state,
			Integer download_state) {
		Song newSong = this.songs.get(pos);
		this.songs.remove(pos);
		newSong.RateValue = rate;
//		newSong.likeState = like_state;
//		newSong.link.download_state = download_state;
		this.songs.add(pos, newSong);
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		

		View photo_view = LayoutInflater.from(this.ctx).inflate(
				R.layout.full_player_song_cover_layout, null);
		ImageView photoimg = (ImageView) photo_view.findViewById(R.id.photo);
		photoimg.setImageDrawable(ctx.getResources().getDrawable(
				R.drawable.default_song_cover));
		if (this.songs.get(position).songcover != null
				&& !this.songs.get(position).songcover.equals("")) {
			ctx.mPicasso.load(this.songs.get(position).songcover)
					.into(photoimg);
		} else {
			photoimg.setImageDrawable(ctx.getResources().getDrawable(
					R.drawable.default_song_cover));
		}
		// photoimg.setImageDrawable(this.ctx.getResources().getDrawable(this.mHelpImags.getResourceId(position,
		// -1)));
		container.addView(photo_view, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		return photo_view;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		container.removeView((View) object);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		
		return arg0 == ((View) arg1);
	}

}