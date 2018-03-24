package mp3ready.adapters;

import java.util.ArrayList;
import java.util.List;

import mp3ready.entities.Song;
import mp3ready.player.services.Output;
import mp3ready.player.services.OutputCommand;
import mp3ready.ui.MainActivity;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.zgm.mp3ready.R;

public abstract class SongItemsAdatper extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Song> songs;
	private Runnable song_equalizer_runnable;
	private MainActivity context;
	private boolean cachTheCovers;
	private int equalizer_indx = 0;
	private TypedArray equalizer_imgs;

	public SongItemsAdatper(MainActivity context, boolean cachTheConvers) {
		super();
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.songs = new ArrayList<Song>();
		this.cachTheCovers = cachTheConvers;
		equalizer_imgs = this.context.getResources().obtainTypedArray(
				R.array.equalizer_imgs);

	}

	public int getCount() {
		return songs.size();
	}

	public Object getItem(int position) {
		return this.songs.get(position);
	}

	public long getItemId(int position) {
		return this.songs.get(position).hashCode();
	}

	public void deleteSong(Song song) {
		this.songs.remove(song);
		notifyDataSetChanged();
	}

	public void loadMoreSongs(List<Song> newSongs) {
		if (newSongs != null && newSongs.size() > 0 && getCount()==0) {
			this.songs.addAll(newSongs);
		}
		notifyDataSetChanged();
	}

	static class ViewHolder {

		ImageView iv_songCover;
		TextView tv_song_duration;
		TextView tv_song_listeners;
		TextView tv_song_name;
		TextView tv_artist_name;
		ImageView tv_equalizar;
		ImageButton ib_popupmenu_options;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.song_item, null);
			holder = new ViewHolder();
			holder.iv_songCover = (ImageView) convertView
					.findViewById(R.id.song_cover);
			holder.tv_song_duration = (TextView) convertView
					.findViewById(R.id.tv_song_duration);
			holder.tv_song_listeners = (TextView) convertView
					.findViewById(R.id.tv_song_listeners);
			holder.tv_song_name = (TextView) convertView
					.findViewById(R.id.tv_song_title);
			holder.tv_artist_name = (TextView) convertView
					.findViewById(R.id.tv_song_artist);
			holder.tv_equalizar = (ImageView) convertView
					.findViewById(R.id.equalizer_imgs);
			holder.ib_popupmenu_options = (ImageButton) convertView
					.findViewById(R.id.popup_menu);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Song song = (Song) getItem(position);
		// holder.iv_songCover.setImageDrawable(context.getResources().getDrawable(R.drawable.default_song_cover));
		if (song.songcover != null && !song.songcover.equals("")) {
			if (this.cachTheCovers) {
				((MainActivity) context).mPicasso
						.load(song.songcover)
						.error(context.getResources().getDrawable(
								R.drawable.default_song_cover)).fit()
						.into(holder.iv_songCover);
			} else {
				((MainActivity) context).mPicasso
						.load(song.songcover)
						.error(context.getResources().getDrawable(
								R.drawable.default_song_cover))
						.networkPolicy(NetworkPolicy.NO_STORE,
								NetworkPolicy.NO_CACHE)
						// .memoryPolicy(MemoryPolicy.NO_CACHE,
						// MemoryPolicy.NO_STORE)
						.fit().into(holder.iv_songCover);
			}
		} else {
			holder.iv_songCover.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.default_song_cover));
		}
		holder.ib_popupmenu_options.setTag(song);
		holder.ib_popupmenu_options
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						
						// Song s =(Song)v.getTag();
						popUpMenu(v);
					}
				});
		holder.iv_songCover.setTag(song);
		holder.iv_songCover.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Song s = (Song) v.getTag();
				goToDetails(s);
			}
		});
		if (song.duration != null) {
			holder.tv_song_duration.setText(song.duration.trim());
		}
		if (song.listeners != null) {
			holder.tv_song_listeners.setText(song.listeners);
		}
		if (song.SName != null) {
			holder.tv_song_name.setText(song.SName);

		}
		if (song.Artist != null) {
			holder.tv_artist_name.setText(song.Artist.trim());
		}

		/**
		 * connect to player to getting current played song and change the
		 * equalizer imags for played song item run thread every 100 mili
		 * seconds to change equalizer image
		 */
//		context.player.connectPlayer(new OutputCommand() {
//
//			@Override
//			public void connected(final Output output) {
//
//				if (output != null && output.getCurrSong() != null
//						&& output.getCurrSong().SID.equals(song.SID)) {
//					holder.tv_equalizar.setVisibility(View.VISIBLE);
//					song_equalizer_runnable = new Runnable() {
//
//						@Override
//						public void run() {
//
//							if (output != null
//									&& output.getCurrSong() != null
//									&& output.getCurrSong().SID
//											.equals(song.SID)) {
//								holder.tv_equalizar.setVisibility(View.VISIBLE);
//								if (output.isPLaying()) {
//									Log.e("SongItemAdapter", "equalizer_indx:"
//											+ equalizer_indx);
//									holder.tv_equalizar
//											.setBackgroundResource(equalizer_imgs
//													.getResourceId(
//															equalizer_indx, -1));
//									SongItemsAdatper.this
//											.notifyDataSetChanged();
//									// context.mPicasso.load(equalizer_imgs.getResourceId(equalizer_indx,
//									// -1)).fit().into(holder.tv_equalizar);
//									equalizer_indx++;
//									if (equalizer_indx > 12) {
//										equalizer_indx = 0;
//									}
//								} else {
//									holder.tv_equalizar
//											.setBackgroundResource(R.drawable.cover_ic_equalizer_pause);
//								}
//								holder.tv_equalizar.postDelayed(
//										song_equalizer_runnable, 100);
//							} else {
//								holder.tv_equalizar.setVisibility(View.GONE);
//								SongItemsAdatper.this.notifyDataSetChanged();
//							}
//						}
//					};
//					holder.tv_equalizar.postDelayed(song_equalizer_runnable, 0);
//				} else {
//					holder.tv_equalizar.setVisibility(View.GONE);
//				}
//			}
//		});

		return convertView;
	}

	protected abstract void goToDetails(Song song);

	protected abstract void popUpMenu(View btn);

}