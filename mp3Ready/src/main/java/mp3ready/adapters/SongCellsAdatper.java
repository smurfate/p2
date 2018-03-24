package mp3ready.adapters;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.ui.MainActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zgm.mp3ready.R;

public abstract class SongCellsAdatper extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Song> songs;
	private MainActivity context;
	private boolean withSimilarity;// render song cell with/without similarity
									// bar
	private boolean withAlbum; // render song cell with/wihtout album name

	public SongCellsAdatper(MainActivity context, boolean withSimilarity,
			boolean withAlbum) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.songs = new ArrayList<Song>();
		this.withSimilarity = withSimilarity;
		this.withAlbum = withAlbum;
	}

	public int getCount() {
		return songs.size();
	}

	public Object getItem(int position) {
		return this.songs.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public void loadMoreSongs(List<Song> newSongs) {
		if (newSongs != null && newSongs.size() > 0) {
			this.songs.addAll(newSongs);
		}
		// notifyDataSetChanged();
	}

	static class ViewHolder {

		ImageView iv_songCover;
		TextView tv_song_duration;
		TextView tv_song_listeners;
		TextView tv_song_name;
		TextView tv_artist_name;
		ImageView iv_action_dislike;
		ImageView iv_action_like;
		ImageView iv_play_song;
		SeekBar similarity_bar;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.song_item_cell, null);
			holder = new ViewHolder();
			holder.iv_songCover = (ImageView) convertView
					.findViewById(R.id.song_cover);
			holder.tv_song_duration = (TextView) convertView
					.findViewById(R.id.song_duration);
			holder.tv_song_listeners = (TextView) convertView
					.findViewById(R.id.song_listners);
			holder.tv_song_name = (TextView) convertView
					.findViewById(R.id.song_name);
			holder.tv_artist_name = (TextView) convertView
					.findViewById(R.id.artist_name);
			holder.iv_action_dislike = (ImageView) convertView
					.findViewById(R.id.action_dislike);
			holder.iv_action_like = (ImageView) convertView
					.findViewById(R.id.action_like);
			holder.iv_play_song = (ImageView) convertView
					.findViewById(R.id.action_play);
			holder.similarity_bar = (SeekBar) convertView
					.findViewById(R.id.similarity_seekbar);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (context.mem.isAppAuthed()) {
			holder.iv_action_dislike.setVisibility(View.VISIBLE);
			holder.iv_action_like.setVisibility(View.VISIBLE);
		} else {
			holder.iv_action_dislike.setVisibility(View.INVISIBLE);
			holder.iv_action_like.setVisibility(View.INVISIBLE);
		}
		Song song = (Song) getItem(position);
		holder.iv_songCover.setImageDrawable(context.getResources()
				.getDrawable(R.drawable.default_song_cover));
		if (song.songcover != null && !song.songcover.equals("")) {
			((MainActivity) context).mPicasso
					.load(song.songcover)
					.error(context.getResources().getDrawable(
							R.drawable.default_song_cover)).fit()
					.into(holder.iv_songCover);
		} else {
			holder.iv_songCover.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.default_song_cover));
		}
		if (song.duration != null) {
			holder.tv_song_duration.setText(song.duration);
		}
		if (song.listeners != null) {
			// Log.i("ZFragment", song.listeners);
			try {
				holder.tv_song_listeners.setText(NumberFormat.getInstance()
						.format(Double.valueOf(song.listeners)));
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
				e.printStackTrace();
				holder.tv_song_listeners.setText(song.listeners);
			}

		}
		if (song.SName != null) {
			holder.tv_song_name.setText(song.SName);
			holder.tv_song_name.setTag(song);
			holder.tv_song_name.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					Song s = (Song) v.getTag();
					((TextView) v).setTextColor(context.getResources()
							.getColor(R.color.dark_blue));
					songDetails(s);
				}
			});
		}
		if (this.withAlbum) {
			if (song.album != null) {
				holder.tv_artist_name.setText(song.album);
				holder.tv_artist_name.setTag(song);
				holder.tv_artist_name
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								Song s = (Song) v.getTag();
								((TextView) v).setTextColor(context
										.getResources().getColor(
												R.color.dark_blue));
							}
						});
			}
		} else {
			if (song.Artist != null) {
				holder.tv_artist_name.setText(song.Artist);
				holder.tv_artist_name.setTag(song);
				holder.tv_artist_name
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								Song s = (Song) v.getTag();
								((TextView) v).setTextColor(context
										.getResources().getColor(
												R.color.dark_blue));
								artistPage(s);
							}
						});
			}
		}
		holder.iv_play_song.setTag(song);
		holder.iv_play_song.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Song s = (Song) v.getTag();
				playSong(s);
			}
		});
		if (this.withSimilarity) {
			holder.similarity_bar.setVisibility(View.VISIBLE);
			if (song.SimilarSong1 != null) {
//				holder.similarity_bar.setProgress(Math
//						.round(song.similarity * 100));
				holder.similarity_bar.setEnabled(false);
				holder.similarity_bar.setFocusable(false);
				holder.similarity_bar.setFocusableInTouchMode(false);
			}
		} else {
			holder.similarity_bar.setVisibility(View.GONE);
		}
		holder.iv_action_like.setTag(song);
		holder.iv_action_like.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Song s = (Song) v.getTag();
				setLikeState(s.SID, Enums.LIKE_STATE, (ImageView) v);
			}
		});
		holder.iv_action_dislike.setTag(song);
		holder.iv_action_dislike.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Song s = (Song) v.getTag();
				setLikeState(s.SID, Enums.DISLIKE_STATE, (ImageView) v);
			}
		});
		if (withSimilarity) {
			holder.tv_song_duration.setBackgroundColor(context.getResources()
					.getColor(R.color.red));
			holder.tv_song_listeners.setBackgroundColor(context.getResources()
					.getColor(R.color.red));
		}
		return convertView;
	}

	protected abstract void setLikeState(String song_id, int like_State,
			ImageView v);

	protected abstract void playSong(Song song);

	protected abstract void songDetails(Song song);

	protected abstract void artistPage(Song song);
}