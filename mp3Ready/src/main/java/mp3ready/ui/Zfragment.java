package mp3ready.ui;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import mp3ready.api.NewApiCalls;
import mp3ready.db.DataSource;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.util.AppCache;
import mp3ready.views.MaterialSection;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.google.gson.Gson;
import com.zgm.mp3ready.R;

/**
 * <p>
 * this class is the parent of all screen
 * </p>
 * <p>
 * we put in it all things that shared with all screen such as
 * </p>
 * <li>initialize the items base</li> <li>initialize the reference of
 * MainActivity</li> <li>initialize the gson object</li> <li>initialize the app
 * cache (shared Preferences)</li> <li>create custome toast with animation</li>
 * 
 * @author mhmd
 * 
 */

public class Zfragment extends Fragment {

	public MainActivity mainActivity;
	public NewApiCalls apiCalls;

	public final static int CACHED_SEARCH_RESULTS = 10;

	public static final String CACHE_KEY = "CACHE_KEY";

	public DataSource db;
	public Gson gson;
	public Menu myMenu;

	public <T> List<T> array2List(T[] array)
	{
		List<T> list = new ArrayList<>(array.length);
		for (int i=0;i<array.length;i++)
			list.add(i,array[i]);
		return list;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity) getActivity();
		apiCalls = mainActivity.apiCalls;
		gson = mainActivity.gson;

		if (mainActivity.mem == null) {
			mainActivity.mem = new AppCache(
					mainActivity.getApplicationContext());
		}
		db = mainActivity.db;

		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.main, menu);
		;
		super.onCreateOptionsMenu(menu, inflater);
		if (mainActivity == null) {
			mainActivity = (MainActivity) getActivity();
		}

		myMenu = menu;

	}

	public void toast(String txt) {
		
		// super.toast(txt);
		niftyNotification(txt, Effects.flip);
	}

	/**
	 * <p>
	 * render custom toast with animation
	 * 
	 * @param msg
	 *            : message that you want to show
	 * @param effect
	 *            : animation type
	 */
	public void niftyNotification(String msg, Effects effect) {
		mainActivity.snack(msg);
//		Configuration cfg = new Configuration.Builder().setAnimDuration(700)
//				.setDispalyDuration(2000).setBackgroundColor("#0A0A2A")
//				.setTextColor("#FFFFFFFF").setIconBackgroundColor("#FFFFFFFF")
//				.setTextPadding(5) // dp
//				.setViewHeight(60) // dp
//				.setTextLines(2) // You had better use setViewHeight and
//									// setTextLines together
//				.setTextGravity(Gravity.CENTER) // only text def
//												// Gravity.CENTER,contain icon
//												// Gravity.CENTER_VERTICAL
//				.build();
//		if (getActivity() != null) {
//			NiftyNotificationView
//					.build(getActivity(), msg, effect, R.id.mLyout, cfg)
//					// .setIcon(R.drawable.lion) //remove this line ,only text
//					.setOnClickListener(new View.OnClickListener() {
//						@Override
//						public void onClick(View view) {
//							// add your code
//						}
//					}).show();
//		}

	}

	@Override
	public boolean onOptionsItemSelected(
			MenuItem item) {

		switch (item.getItemId()) {

		}
		return false;
	}

	@Override
	public void onStart() {
		
		super.onStart();

		// mainActivity.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		// mainActivity.getSupportActionBar().setDisplayShowTitleEnabled(true);
		// mainActivity.invalidateOptionsMenu();

		if (!mainActivity.getSupportActionBar().isShowing()
				&& !mainActivity.am_i_in_full_player_screen ) {
			mainActivity.getSupportActionBar().show();
		}
		if (mainActivity.am_i_in_full_player_screen) {
			mainActivity.am_i_in_full_player_screen = false;
		}

		if (mainActivity.drawer != null) {
			mainActivity.drawer
					.closeDrawer(GravityCompat.START);
			mainActivity.drawer
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}
		mainActivity.currentFrag = "";
		mainActivity.am_i_in_home = false;
		mainActivity.am_i_in_login = false;

		if (mainActivity.playedSong == null) {
			mainActivity.mediacontroller_ll.setVisibility(View.GONE);
		}
		mainActivity.setSupportProgressBarIndeterminateVisibility(false);

	}

	/**
	 * <p>
	 * render song cell with its info in given section with given txt color
	 * </p>
	 * 
	 * @param section
	 *            the ViewGroup that we want to adding song cell view to it
	 * @param songs
	 *            items source of songs to create song cell for each song
	 * @param txtColor
	 *            the color of text , red for recommend songs , otherwise white
	 * @param withSimilarity
	 *            true should render the red bar of similarity , false should
	 *            not
	 * @param withAlbum
	 *            true should render album name instead of artist name , fase
	 *            should not
	 */
	protected void renderSongsCell(ViewGroup section, List<Song> songs,
			Integer txtColor, boolean withSimilarity, boolean withAlbum) {
		LinearLayout songs_ll = (LinearLayout) section
				.findViewById(R.id.songs_ll);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(10, 0, 0, 0);
		songs_ll.removeAllViews();
		songs_ll.invalidate();
		songs_ll.refreshDrawableState();

		for (Song song : songs) {
			ViewHolder holder;
			View songView = null;

			holder = new ViewHolder();
			songView = LayoutInflater.from(mainActivity).inflate(
					R.layout.song_item_cell, null);
			holder.iv_songCover = (ImageView) songView
					.findViewById(R.id.song_cover);
			holder.tv_song_duration = (TextView) songView
					.findViewById(R.id.song_duration);
			holder.tv_song_listeners = (TextView) songView
					.findViewById(R.id.song_listners);
			holder.tv_song_name = (TextView) songView
					.findViewById(R.id.song_name);
			holder.tv_artist_name = (TextView) songView
					.findViewById(R.id.artist_name);
			holder.iv_action_dislike = (ImageView) songView
					.findViewById(R.id.action_dislike);
			holder.iv_action_like = (ImageView) songView
					.findViewById(R.id.action_like);
			holder.iv_play_song = (ImageView) songView
					.findViewById(R.id.action_play);
			holder.similarity_bar = (SeekBar) songView
					.findViewById(R.id.similarity_seekbar);
			songView.setTag(holder);

			if (mainActivity.mem.isAppAuthed()) {
				holder.iv_action_dislike.setVisibility(View.VISIBLE);
				holder.iv_action_like.setVisibility(View.VISIBLE);
			} else {
				holder.iv_action_dislike.setVisibility(View.INVISIBLE);
				holder.iv_action_like.setVisibility(View.INVISIBLE);
			}
			holder.iv_songCover.setImageDrawable(mainActivity.getResources()
					.getDrawable(R.drawable.default_song_cover));
			if (song.songcover != null && !song.songcover.equals("")) {
				mainActivity.mPicasso.load(song.songcover).fit()
						.into(holder.iv_songCover);
			} else {
				holder.iv_songCover.setImageDrawable(mainActivity
						.getResources().getDrawable(
								R.drawable.default_song_cover));
			}
			if (song.duration != null) {
				holder.tv_song_duration.setText(song.duration);
			}
			if (song.listeners != null) {

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
			}
			if (withAlbum) {
				if (song.album != null) {
					holder.tv_artist_name.setText(song.album);
					holder.tv_artist_name.setTag(song);
					holder.tv_artist_name
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									
									// Song s = (Song)v.getTag();
									((TextView) v).setTextColor(mainActivity
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
									((TextView) v).setTextColor(mainActivity
											.getResources().getColor(
													R.color.dark_blue));
									mainActivity.gotoFragment(
											ArtistScreenFragment
													.newInstance(s.ArtistID),
											ArtistScreenFragment.TAG, true);// is
																			// child
								}
							});
				}
			}
//			if (withSimilarity) {
//				holder.similarity_bar.setVisibility(View.VISIBLE);
//				if (song.similarity != null) {
//					holder.similarity_bar.setProgress(Math
//							.round(song.similarity * 100));
//					holder.similarity_bar.setEnabled(false);
//					holder.similarity_bar.setFocusable(false);
//					holder.similarity_bar.setFocusableInTouchMode(false);
//				}
//			}
			holder.tv_song_duration.setBackgroundColor(txtColor);
			holder.tv_song_listeners.setBackgroundColor(txtColor);
			holder.iv_action_like.setTag(song);

			holder.iv_action_like
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							Song s = (Song) v.getTag();
							setLikeState(s.SID, Enums.LIKE_STATE, (ImageView) v);
						}
					});
			holder.iv_action_dislike.setTag(song);

			holder.iv_action_dislike
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							Song s = (Song) v.getTag();
							setLikeState(s.SID, Enums.DISLIKE_STATE,
									(ImageView) v);
						}
					});
			holder.tv_song_name.setTag(song);
			holder.tv_song_name.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Song s = (Song) v.getTag();
					((TextView) v).setTextColor(mainActivity.getResources()
							.getColor(R.color.dark_blue));
					mainActivity.gotoFragment(
							SongDetailsFragment.newInstance(s),
							SongDetailsFragment.TAG, true);// is child
				}
			});

			holder.iv_songCover.setTag(song);
			holder.iv_songCover.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Song s = (Song) v.getTag();
					mainActivity.gotoFragment(
							SongDetailsFragment.newInstance(s),
							SongDetailsFragment.TAG, true);// is child
				}
			});

			holder.iv_play_song.setTag(song);
			holder.iv_play_song.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					
					Song s = (Song) v.getTag();
					if (getActivity() != null && mainActivity != null) {
						// mainActivity.getMp3Urls(s);
						mainActivity.playSong(s);
//						boolean insert = mainActivity.db
//								.addSongToPlayList(
//										s.SID,
//										s.duration,
//										s.listeners,
//										s.songcover,
//										s.likeState,
//										s.rate,
//										s.url,
//										s.SName,
//										s.Artist,
//										(s.link != null
//												&& s.link.bitrate != null ? s.link.bitrate
//												: "0"),
//										Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//						if (insert) {
//							Log.e("ZFragment",
//									"song inserted to recently playlist");
//							toast("song added to Recently Played List");
//						} else {
//							toast("song is already exist in Recently Played List");
//						}
						mainActivity.playSongIfNoCurrentSongExist(s);
					}
				}
			});
			/*
			 * ViewGroup parent = (ViewGroup)songView.getParent(); if
			 * (parent!=null){ parent.removeView(songView); }
			 */

			songs_ll.addView(songView, lp);
		}
	}

	/**
	 * <p>
	 * render recommended songs section with its songs by calling
	 * {@link #renderSongsCell(ViewGroup, List, Integer, boolean, boolean)}
	 * 
	 * @param recommended
	 *            the recommend object that contains recommended songs
	 * @param title
	 *            the title of section
	 * @param section
	 *            the section that we should render recommend songs ot it
	 * @param withAlbum
	 *            true we should render songs cell with album name , false we
	 *            shouldn't
	 * @param l
	 *            the callback of clicking on more button to show all
	 *            recommended songs
	 */
	protected void renderRecommendedSection(
			final List<Song> recommended, String title,
			ViewGroup section, boolean withAlbum, OnNORecommendedSongsListener l) {
		TextView section_title = (TextView) section
				.findViewById(R.id.genre_section_title);
		TextView more_songs = (TextView) section.findViewById(R.id.more_songs);
		section_title.setText(title);
		section_title.setTextColor(mainActivity.getResources().getColor(
				R.color.red));
		more_songs.setVisibility(View.VISIBLE);
		more_songs.setBackgroundColor(mainActivity.getResources().getColor(
				R.color.red));
		try {
			more_songs.setText(NumberFormat.getInstance().format(
					Double.valueOf(recommended.size())));
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			e.printStackTrace();
			more_songs.setText(recommended.size() + "");
		}

		if (recommended != null && recommended.size() > 0
				&& section != null) {
			RelativeLayout more_apps_contr = (RelativeLayout) section
					.findViewById(R.id.more_songs_rl);
			more_apps_contr.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// mainActivity.gotoFragment(PagerFragment.newInstance(PagerFragment.PAGER_RECOMMENDATIONS,
					// 0), PagerFragment.TAG,false);//is not child
					MaterialSection s = mainActivity
							.getSectionByTitle(Enums.RECOMMENDED_SCREEN_POS);
					mainActivity.setSection(mainActivity
							.getSectionByTitle(Enums.RECOMMENDED_SCREEN_POS),false);// is not child
				}
			});
			renderSongsCell(section,
					recommended.subList(0, Enums.SONGS_IN_SECTION),
					mainActivity.getResources().getColor(R.color.red), true,
					withAlbum);
		} else {
			// more_songs.setText("no recommended");
			l.onNORecommendedSongs();
		}
	}

	/**
	 * <p>
	 * render TopGenre songs section with its songs by calling
	 * {@link #renderSongsCell(ViewGroup, List, Integer, boolean, boolean)}
	 * 
	 * @param mSongs
	 *            the songs of each genre
	 * @param title
	 *            the title of genre
	 * @param section
	 *            the ViewGroup to add genre's section to it
	 * @param withSimilarity
	 *            true should render the red bar of similarity , false should
	 *            not
	 * @param withMore
	 *            true render more button
	 * @param withAlbum
	 *            true should render album name instead of artist name , fase
	 *            should not
	 * @param clickCallback
	 *            the callback of clicking on more button to show all genre
	 *            songs
	 */
	protected void renderSection(List<Song> mSongs, String title,
			ViewGroup section, boolean withSimilarity, boolean withMore,
			boolean withAlbum, View.OnClickListener clickCallback) {
		TextView section_title = (TextView) section
				.findViewById(R.id.genre_section_title);
		section_title.setText(title);
		section_title.setTextColor(mainActivity.getResources().getColor(
				R.color.dark_blue));
		if (withMore) {
			TextView more_songs = (TextView) section
					.findViewById(R.id.more_songs);
			more_songs.setVisibility(View.VISIBLE);

			more_songs.setBackgroundColor(mainActivity.getResources().getColor(
					R.color.dark_blue));
			more_songs.setText("More");
			RelativeLayout more_apps_contr = (RelativeLayout) section
					.findViewById(R.id.more_songs_rl);
			more_apps_contr.setOnClickListener(clickCallback);
		}

		if (mSongs != null && mSongs.size() > 0 && section != null) {

			renderSongsCell(section, mSongs, mainActivity.getResources()
					.getColor(R.color.black_transparent), withSimilarity,
					withAlbum);
		}
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();

	}

	/**
	 * this method will override in each fragment contains like/dislike button
	 * to send like/dislike song request
	 * 
	 * @param song_id
	 *            song id that we want to like/dislike it
	 * @param like_State
	 *            the state that we want to apply on song and it's like or
	 *            dislike
	 * @param v
	 *            the view that we pressed down to send the action we use it to
	 *            enable/disable it
	 */
	protected void setLikeState(String song_id, int like_State, View v) {
	}

	public interface OnNORecommendedSongsListener {
		public void onNORecommendedSongs();
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
}
