package mp3ready.ui;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import mp3ready.adapters.SongsCoverAdapter;
import mp3ready.api.NewApiCalls;
import mp3ready.entities.PlayList;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.player.services.ObservableOutput;
import mp3ready.player.services.Output;
import mp3ready.player.services.OutputCommand;
import mp3ready.player.services.OutputUsingOnClickListener;
import mp3ready.serializer.ZdParser;
import mp3ready.util.ICallBack;
import mp3ready.util.MyIntents;
import mp3ready.util.Utilities;
import mp3ready.views.MyViewPager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zgm.mp3ready.R;

/**
 * this screen represent the full Music player that contains all actions user
 * can do - swipe songs to play another song in recently played - like/dislike
 * the song - download/pin the song from url - add to play list - rate the song
 * - song's urls contoller to play next/previous url - songs controller to play
 * next/previous song - play/pause - repeat/shuffle
 * 
 * @author mhmd
 * 
 */
public class FullPlayerFragment extends Zfragment implements
		OnPageChangeListener, OnClickListener, OnRatingBarChangeListener {

	public final static String TAG = FullPlayerFragment.class.getName();

	private static MyViewPager pager;// songs pager to swipe to another song in
										// recently played songs
	private SongsCoverAdapter adapter;
	private LinearLayout media_controller_ll;// to include the media player
												// controller
	private SeekBar fp_mediacontroller_seekbar;
	private TextView fp_mediacontroller_progresstime;
	private TextView fp_mediacontroller_totaltime;
	private ImageButton fp_mediacontroller_play_pause;
	private ImageButton fp_mediacontroller_prev;
	private ImageButton fp_mediacontroller_next;
	private ImageButton fp_mediacontroller_repeat;
	private ImageButton fp_mediacontroller_shuffle;
	private TextView fp_mediacontroller_msg;
	private ImageView fp_mediacontroller_nxt_url;
	private ImageView fp_mediacontroller_prev_url;
	private Runnable progressBarRunnable;
	private TextView tv_song_name;
	private ProgressBar pb_try_url_loading;
	private TextView tv_artist_name;
	private RatingBar rate_song;
	private ImageButton action_dislike;
	private ImageButton action_like;
	private ImageButton action_download;
	private ImageButton action_add_to_pl;
	private ImageButton action_pin_song_url;
	private Song playedSong;
	private int playedSongLikeState = -1;
	private Output output;// current output of player that contains current
							// played song
	private int playedSongPos = -1;
	private ImageView song_cover_bg;
	private List<Song> songs;

	public static FullPlayerFragment newInstance(Output output) {
		FullPlayerFragment efrag = new FullPlayerFragment();
		efrag.output = output;
		return efrag;
	}

	/**
	 * initliaze the view by infaltrate them add visibility to all actions views
	 * according to if user logged in or not
	 */
	private void initView() {

		tv_song_name = (TextView) getView().findViewById(R.id.song_name);
		tv_artist_name = (TextView) getView().findViewById(R.id.artist_name);
		rate_song = (RatingBar) getView().findViewById(R.id.rate_bar);
		rate_song.setOnRatingBarChangeListener(this);
		action_like = (ImageButton) getView().findViewById(
				R.id.song_action_like);
		action_dislike = (ImageButton) getView().findViewById(
				R.id.song_action_dislike);
		action_add_to_pl = (ImageButton) getView().findViewById(
				R.id.song_action_add_to_pl);
		action_download = (ImageButton) getView().findViewById(
				R.id.song_action_download);
		action_pin_song_url = (ImageButton) getView().findViewById(
				R.id.song_action_pin);

		action_like.setOnClickListener(this);
		action_dislike.setOnClickListener(this);
		action_add_to_pl.setOnClickListener(this);
		action_download.setOnClickListener(this);
		
		action_pin_song_url.setOnClickListener(this);
		if (mainActivity.mem.isAppAuthed()) {
			rate_song.setVisibility(View.VISIBLE);
			action_like.setVisibility(View.VISIBLE);
			action_dislike.setVisibility(View.VISIBLE);
			action_add_to_pl.setVisibility(View.VISIBLE);
			action_pin_song_url.setVisibility(View.VISIBLE);
		} else {
			rate_song.setVisibility(View.GONE);
			action_like.setVisibility(View.INVISIBLE);
			action_dislike.setVisibility(View.INVISIBLE);
			action_add_to_pl.setVisibility(View.INVISIBLE);
			action_pin_song_url.setVisibility(View.INVISIBLE);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

	}

	/**
	 * after create the layout of fragment we should initialize the media player
	 * controller
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		pager = (MyViewPager) getView().findViewById(R.id.songs_pager);
		pager.setOnPageChangeListener(this);
		media_controller_ll = (LinearLayout) getView().findViewById(
				R.id.bottom_part);
		LinearLayout media_controller = (LinearLayout) LayoutInflater.from(
				mainActivity).inflate(R.layout.full_player_media_controller,
				null);
		fp_mediacontroller_msg = (TextView) media_controller
				.findViewById(R.id.fp_mediacontroller_msg);
		fp_mediacontroller_next = (ImageButton) media_controller
				.findViewById(R.id.fp_mediacontroller_nxt);
		fp_mediacontroller_play_pause = (ImageButton) media_controller
				.findViewById(R.id.fp_mediacontroller_play_pause);
		fp_mediacontroller_prev = (ImageButton) media_controller
				.findViewById(R.id.fp_mediacontroller_prev);
		fp_mediacontroller_progresstime = (TextView) media_controller
				.findViewById(R.id.fp_mediacontroller_time_current);
		fp_mediacontroller_repeat = (ImageButton) media_controller
				.findViewById(R.id.fp_mediacontroller_repeat);
		fp_mediacontroller_seekbar = (SeekBar) media_controller
				.findViewById(R.id.fp_mediacontroller_seekbar);
		fp_mediacontroller_shuffle = (ImageButton) media_controller
				.findViewById(R.id.fp_mediacontroller_shuffle);
		
		pb_try_url_loading = (ProgressBar) media_controller.findViewById(R.id.pb_try_url_loading);
		
		// initialize the shuffle button according to its previously state
		if (mainActivity.isShuffle) {
			fp_mediacontroller_shuffle.setTag("on");
			fp_mediacontroller_shuffle.setImageDrawable(mainActivity
					.getResources().getDrawable(
							R.drawable.player_ic_shuffle_active));
		} else {
			fp_mediacontroller_shuffle.setTag("off");
			fp_mediacontroller_shuffle.setImageDrawable(mainActivity
					.getResources().getDrawable(R.drawable.player_ic_shuffle));
		}
		fp_mediacontroller_totaltime = (TextView) media_controller
				.findViewById(R.id.fp_mediacontroller_time_total);
		fp_mediacontroller_nxt_url = (ImageView) media_controller
				.findViewById(R.id.fp_mediacontroller_next_url);
		fp_mediacontroller_prev_url = (ImageView) media_controller
				.findViewById(R.id.fp_mediacontroller_prev_url);
		fp_mediacontroller_nxt_url.setOnClickListener(this);
		fp_mediacontroller_prev_url.setOnClickListener(this);
		song_cover_bg = (ImageView) getView().findViewById(R.id.song_cover_bg);
		if (getActivity() != null && mainActivity != null) {
			setupClickListeners();
			setupObservers();
		}

		media_controller_ll.removeAllViews();
		media_controller_ll.invalidate();
		media_controller_ll.refreshDrawableState();
		media_controller_ll.addView(media_controller);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.full_player_layout, container,
				false);
		return view;

	}

	/**
	 * adding observer to listener and do action when the player change the song
	 * , stopped, started or has invalid url
	 */
	private void setupObservers() {
//		mainActivity.player.addObserver(new ObservableOutput.PlayerObserver() {
//
//			@Override
//			public String getId() {
//
//				return "PlayViewComponentsUpdater2";
//			}
//
//			@Override
//			public void trackChanged(Song track, int lengthInMillis) {
//
//				fp_mediacontroller_prev.setEnabled(true);
//				fp_mediacontroller_next.setEnabled(true);
//				fp_mediacontroller_seekbar.setMax(lengthInMillis);
//				fp_mediacontroller_seekbar.setSecondaryProgress(0);
//				fp_mediacontroller_totaltime.setText(Utilities
//						.ConvertToMinutes(lengthInMillis));
////				fp_mediacontroller_msg.setText(track.link.bitrate + " KBps");
//				fp_mediacontroller_play_pause.setEnabled(true);
//				playedSong = track;
//				action_pin_song_url.setEnabled(true);
//				action_download.setEnabled(true);
//				fp_mediacontroller_nxt_url.setVisibility(View.VISIBLE);
//				fp_mediacontroller_prev_url.setVisibility(View.VISIBLE);
//				pb_try_url_loading.setVisibility(View.GONE);
//
//			}
//
//			@Override
//			public void stopped() {
//
//				fp_mediacontroller_play_pause.setEnabled(true);
//				pb_try_url_loading.setVisibility(View.GONE);
//				fp_mediacontroller_play_pause.setImageDrawable(getResources()
//						.getDrawable(R.drawable.player_ic_play_full_player));
//			}
//
//			@Override
//			public void started() {
//
//				pb_try_url_loading.setVisibility(View.VISIBLE);
//				pb_try_url_loading.setVisibility(View.GONE);
//				fp_mediacontroller_play_pause.setEnabled(true);
//				fp_mediacontroller_play_pause.setImageDrawable(getResources()
//						.getDrawable(R.drawable.player_ic_pause_full_player));
//			}
//
//			@Override
//			public void TrackHasInvalidUrl(Song track) {
//
//				fp_mediacontroller_prev.setEnabled(true);
//				fp_mediacontroller_next.setEnabled(true);
//				fp_mediacontroller_play_pause.setEnabled(false);
//				fp_mediacontroller_seekbar.setSecondaryProgress(0);
//				Log.i(TAG, "TrackHasInvalidUrl:" + track.file);
////				fp_mediacontroller_msg.setText((track.current_url_indx + 1)
////						+ " of " + track.urls.size());
//				fp_mediacontroller_nxt_url.setVisibility(View.VISIBLE);
//				fp_mediacontroller_prev_url.setVisibility(View.VISIBLE);
//				pb_try_url_loading.setVisibility(View.VISIBLE);
//			}
//
//			@Override
//			public void onMediaPlayerBufferingonUpdate(int duration, int pos) {
//
//				Log.e("FullPlayer", duration + ":" + pos);
//				fp_mediacontroller_seekbar
//						.setSecondaryProgress((duration / 100) * pos);
//			}
//
//			@Override
//			public void onMediaPlayerCompletion(Output output) {
//
//				if (!output.isMPLooping()) {
//					fp_mediacontroller_seekbar.setSecondaryProgress(0);
//					Log.e(TAG, "onCompletion:" + output.getCurrentMillis());
////					Song nxt = mainActivity.playListFactory.getNext(
////							mainActivity.isShuffle, output.getCurrSong());
////					if (nxt != null) {
////						playedSong = nxt;
////						playedSongPos = getCurrSongPos(songs, nxt);
////						pager.setCurrentItem(playedSongPos, true);
////					}
//				}
//			}
//
//		});

		mainActivity.addObserver(new MainActivity.IObserver() {

			@Override
			public void onTrackAdded(PlayList pl) {
				

			}

			@Override
			public void onFinished(String response) {
				

			}

			@Override
			public void onDoActionOnSong() {
				

			}

			@Override
			public String getId() {
				
				return "FullPlayerListeners";
			}

		});
	}

	/**
	 * - setup the click listeners for next/previous song button in media
	 * controller - setup the click listeners for repeat/shuffle buttons - setup
	 * the click listeners for play/pause buttons - refresh the seekbar every
	 * 100 ms
	 */
	private void setupClickListeners() {

//		fp_mediacontroller_next
//				.setOnClickListener(new OutputUsingOnClickListener(
//						mainActivity.player) {
//
//					@Override
//					public void onClick(View v, Output output) {
//
//						if (mainActivity.player == null) {
//							return;
//						}
//						if (mainActivity.isShuffle) {
//							pager.setCurrentItem(Utilities.randInt(
//									pager.getCurrentItem() + 1,
//									adapter.getCount() - 1));
//						} else {
//							pager.setCurrentItem(pager.getCurrentItem() + 1);
//						}
//					}
//				});
//		fp_mediacontroller_prev
//				.setOnClickListener(new OutputUsingOnClickListener(
//						mainActivity.player) {
//
//					@Override
//					public void onClick(View v, Output output) {
//
//						if (mainActivity.player == null) {
//							return;
//						}
//						if (mainActivity.isShuffle) {
//							pager.setCurrentItem(Utilities.randInt(0,
//									pager.getCurrentItem() - 1));
//						} else {
//							pager.setCurrentItem(pager.getCurrentItem() - 1);
//						}
//
//					}
//				});
//		if (this.output.isPLaying()) {
//			fp_mediacontroller_play_pause.setImageDrawable(getResources()
//					.getDrawable(R.drawable.player_ic_pause_full_player));
//		} else {
//			fp_mediacontroller_play_pause.setImageDrawable(getResources()
//					.getDrawable(R.drawable.player_ic_play_full_player));
//		}
//		mainActivity.mPicasso.load(this.output.getCurrSong().songcover).fit()
//				.into(song_cover_bg);
//		fp_mediacontroller_play_pause
//				.setOnClickListener(new OutputUsingOnClickListener(
//						mainActivity.player) {
//
//					@Override
//					public void onClick(View v, Output output) {
//
//						output.toggle();
//					}
//				});

//		if (this.output.isMPLooping()) {
//			fp_mediacontroller_repeat.setImageDrawable(mainActivity
//					.getResources().getDrawable(
//							R.drawable.player_ic_repeat_all_active));
//			fp_mediacontroller_repeat.setTag("Looping");
//		} else {
//			fp_mediacontroller_repeat.setTag("unLooping");
//			fp_mediacontroller_repeat.setImageDrawable(mainActivity
//					.getResources().getDrawable(R.drawable.player_ic_repeat));
//		}
//
//		fp_mediacontroller_repeat
//				.setOnClickListener(new OutputUsingOnClickListener(
//						mainActivity.player) {
//
//					@Override
//					public void onClick(View v, Output output) {
//
//						String tag = (String) v.getTag();
//						if (tag.equals("unLooping")) {
//							fp_mediacontroller_repeat
//									.setImageDrawable(mainActivity
//											.getResources()
//											.getDrawable(
//													R.drawable.player_ic_repeat_all_active));
//							output.setLooping(true);
//							fp_mediacontroller_repeat.setTag("Looping");
//						} else if (tag.equals("Looping")) {
//							fp_mediacontroller_repeat
//									.setImageDrawable(mainActivity
//											.getResources()
//											.getDrawable(
//													R.drawable.player_ic_repeat));
//							output.setLooping(false);
//							fp_mediacontroller_repeat.setTag("unLooping");
//						}
//					}
//				});
//		fp_mediacontroller_shuffle.setOnClickListener(this);
//
//		fp_mediacontroller_seekbar.setMax(this.output.getLengthInMillis());
//		fp_mediacontroller_seekbar
//				.setSecondaryProgress(mainActivity.mediacontroller_seekbar
//						.getSecondaryProgress());
//		fp_mediacontroller_totaltime.setText(Utilities.ConvertToMinutes(output
//				.getLengthInMillis()));
////		fp_mediacontroller_msg.setText(output.getCurrSong().link.bitrate
////				+ " KBps");
//		fp_mediacontroller_play_pause.setEnabled(true);
//		progressBarRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//
//				mainActivity.player.connectPlayer(new OutputCommand() {
//
//					@Override
//					public void connected(Output output) {
//
//						fp_mediacontroller_seekbar.setProgress(output
//								.getCurrentMillis());
//						fp_mediacontroller_progresstime.setText(Utilities
//								.ConvertToMinutes(output.getCurrentMillis()));
//						fp_mediacontroller_seekbar.postDelayed(
//								progressBarRunnable, 100);
//					}
//				});
//			}
//		};
//		fp_mediacontroller_seekbar.postDelayed(progressBarRunnable, 0);
//		fp_mediacontroller_seekbar
//				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//					@Override
//					public void onStopTrackingTouch(final SeekBar seekBar) {
//
//						mainActivity.player.connectPlayer(new OutputCommand() {
//							public void connected(Output output) {
//								output.goToMillis(seekBar.getProgress());
//								fp_mediacontroller_seekbar.postDelayed(
//										progressBarRunnable, 100);
//							}
//						});
//					}
//
//					@Override
//					public void onStartTrackingTouch(SeekBar seekBar) {
//
//						fp_mediacontroller_seekbar
//								.removeCallbacks(progressBarRunnable);
//					}
//
//					@Override
//					public void onProgressChanged(SeekBar seekBar, int arg1,
//							boolean arg2) {
//
//
//					}
//				});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	/**
	 * - lock the drawer - initialize the view of fragement - initialize current
	 * played song - hide/show the song's urls controller according to current
	 * song's urls if available or not
	 */
	@Override
	public void onStart() {
		
		super.onStart();
		if (mainActivity.drawer != null) {
			mainActivity.drawer
					.closeDrawer(GravityCompat.START);
			mainActivity.drawer
					.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}
		/*
		 * if (mainActivity.getSupportActionBar().isShowing()) {
		 * mainActivity.getSupportActionBar().hide(); }
		 */
		mainActivity.mediacontroller_ll.setVisibility(View.GONE);
		mainActivity.song_name_artist_ll.setEnabled(true);
		mainActivity.mediacontroller_song_cover.setEnabled(true);
		initView();
		playedSong = this.output.getCurrSong();
		songs = db.getPlayList(Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
		adapter = new SongsCoverAdapter(songs, mainActivity);
		pager.setAdapter(adapter);
		playedSongPos = getCurrSongPos(songs, output.getCurrSong());
		pager.setCurrentItem(playedSongPos, true);
		tv_song_name.setText(this.output.getCurrSong().SName);
		tv_artist_name.setText(this.output.getCurrSong().Artist);
		action_download.setEnabled(false);
//		if (this.output.getCurrSong() != null
//				&& this.output.getCurrSong().urls != null
//				&& this.output.getCurrSong().urls.size() > 0) {
//			fp_mediacontroller_nxt_url.setVisibility(View.VISIBLE);
//			fp_mediacontroller_prev_url.setVisibility(View.VISIBLE);
//			pb_try_url_loading.setVisibility(View.GONE);
//			if (output.isPLaying()){
//				action_download.setEnabled(true);
//			}
//		} else {
//			fp_mediacontroller_nxt_url.setVisibility(View.GONE);
//			fp_mediacontroller_prev_url.setVisibility(View.GONE);
//			pb_try_url_loading.setVisibility(View.GONE);
//		}
	}

	/**
	 * getting the song's position in pager adapter
	 * 
	 * @param songs
	 *            all songs in pager adapter
	 * @param song
	 *            the song that we want to know its position in pager adapter
	 * @return the position of given song in pager adapter
	 */
	private int getCurrSongPos(List<Song> songs, Song song) {
		int i = 0;
		for (Song s : songs) {
			if (s.SID.equals(song.SID)) {
				break;
			}
			i++;
		}
		return i;
	}

	@Override
	public void onPause() {
		
		super.onPause();

	}

	/**
	 * - when we go out the screen we should to remove all observers in this
	 * screen - show the small media controller
	 */
	@Override
	public void onDetach() {
		
		super.onDetach();
		Log.e(TAG, "onDetach");
		mainActivity.mediacontroller_ll.setVisibility(View.VISIBLE);
//		mainActivity.player.removeObserver("PlayViewComponentsUpdater2");
		mainActivity.removeObserver("FullPlayerListeners");
	}

	/**
	 * - when we go out the screen we should to remove all observers in this
	 * screen - show the small media controller
	 */
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		Log.e(TAG, "onDestroy");
		mainActivity.mediacontroller_ll.setVisibility(View.VISIBLE);
//		mainActivity.player.removeObserver("PlayViewComponentsUpdater2");
		mainActivity.removeObserver("FullPlayerListeners");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onPageScrollStateChanged(int pos) {
		
		Log.i(TAG, "onPageScrollStateChanged at:" + pos);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
		Log.i(TAG, "onPageScrolled at:" + arg0 + "," + arg1 + "," + arg2);
	}

	/**
	 * - this listener run when we swipe the pager to play the next/previous
	 * song - getting the song that we want to play it in given pos - update the
	 * song info with new song info - if we still in the song 5 seconds then we
	 * should to play it
	 */
	@Override
	public void onPageSelected(final int pos) {
		
		Log.i(TAG, "onPageSelected at:" + pos);
		final Song newSong = adapter.getItem(pos);
		if (newSong != null) {
			tv_song_name.setText(newSong.SName);
			tv_artist_name.setText(newSong.Artist);
			mainActivity.mPicasso.load(newSong.songcover).fit()
					.into(song_cover_bg);
			if (newSong.RateValue != null && !newSong.RateValue.equals("0")) {
				rate_song.setRating(Float.valueOf(newSong.RateValue));
			} else {
				rate_song.setRating(0);
			}
//			if (true) {
//				if (Enums.LIKE_STATE == Integer.valueOf(newSong.likeState)) {
//					action_like.setImageDrawable(mainActivity.getResources()
//							.getDrawable(
//									R.drawable.love_circle_onboarding_active));
//					action_like.setEnabled(false);
//				} else if (Enums.DISLIKE_STATE == Integer
//						.valueOf(newSong.likeState)) {
//					action_dislike
//							.setImageDrawable(mainActivity
//									.getResources()
//									.getDrawable(
//											R.drawable.unlove_circle_onboarding_active));
//					action_dislike.setEnabled(false);
//				}
//			} else {
//				action_dislike.setEnabled(true);
//				action_like.setEnabled(true);
//				action_like.setImageDrawable(mainActivity.getResources()
//						.getDrawable(R.drawable.love_circle_onboarding));
//				action_dislike.setImageDrawable(mainActivity.getResources()
//						.getDrawable(R.drawable.unlove_circle_onboarding));
//			}
//			if (newSong.link.download_state != null) {
//				if (newSong.link.download_state != MyIntents.Types.COMPLETE) {
//
//					if (newSong.link.download_progress <= 0) {
//						action_download.setImageDrawable(mainActivity
//								.getResources().getDrawable(
//										R.drawable.settings_ic_sync));
//					} else {
//						action_download.setEnabled(false);
//						action_download.setImageDrawable(mainActivity
//								.getResources().getDrawable(
//										R.drawable.settings_ic_sync_run));
//					}
//				} else {
//					action_download.setEnabled(false);
//					action_download.setImageDrawable(mainActivity
//							.getResources().getDrawable(
//									R.drawable.settings_ic_sync_done));
//				}
//			}
		}
		if (playedSong != null && !playedSong.SID.equals(newSong.SID)) {
			Log.i(TAG, "Play New Song");
			myHandler my = new myHandler();
			my.triggerHandler(pos, new OnPlayNewSongListener() {

				@Override
				public void onPlayNewSong(int position) {
					
					playNewSong(newSong, position);
				}
			});

		}
	}

	/**
	 * - getting song's urls from server to play the song - disable all buttons
	 * while the app getting the urls from server
	 * 
	 * @param newSong
	 *            the new song that we wanna to play it
	 * @param pos
	 *            the position of song that we wannat to play it in pager
	 *            adapter
	 */
	private void playNewSong(final Song newSong, final int pos) {
		fp_mediacontroller_prev.setEnabled(false);
		fp_mediacontroller_next.setEnabled(false);
		fp_mediacontroller_play_pause.setEnabled(false);
		rate_song.setEnabled(false);
		action_add_to_pl.setEnabled(false);
		action_dislike.setEnabled(false);
		action_download.setEnabled(false);
		action_like.setEnabled(false);
		action_pin_song_url.setEnabled(false);
		fp_mediacontroller_seekbar.setProgress(0);
		fp_mediacontroller_seekbar.setSecondaryProgress(0);
		fp_mediacontroller_prev_url.setVisibility(View.GONE);
		fp_mediacontroller_nxt_url.setVisibility(View.GONE);
		pb_try_url_loading.setVisibility(View.GONE);
		pager.setPagingEnabled(false);
//		mainActivity.getMp3Urls(newSong, new NewApiCalls.Callback2() {
//					@Override
//					public void onFinished(String response) {
//						fp_mediacontroller_prev.setEnabled(true);
//						fp_mediacontroller_next.setEnabled(true);
//						fp_mediacontroller_play_pause.setEnabled(true);
//						playedSong = newSong;
//						playedSongPos = pos;
//						action_add_to_pl.setEnabled(true);
//						rate_song.setEnabled(true);
//						action_dislike.setEnabled(true);
//						action_like.setEnabled(true);
//						fp_mediacontroller_msg.setText("1 of 200");
//						fp_mediacontroller_prev_url.setVisibility(View.VISIBLE);
//						fp_mediacontroller_nxt_url.setVisibility(View.VISIBLE);
//						pb_try_url_loading.setVisibility(View.VISIBLE);
//						pager.setPagingEnabled(true);
//
//					}
//
//					@Override
//					public void onProblem() {
//						fp_mediacontroller_prev.setEnabled(true);
//						fp_mediacontroller_next.setEnabled(true);
//						fp_mediacontroller_play_pause.setEnabled(true);
//						pager.setCurrentItem(playedSongPos, false);
//						action_add_to_pl.setEnabled(true);
//						action_dislike.setEnabled(true);
//						action_like.setEnabled(true);
//						pager.setPagingEnabled(true);
//						rate_song.setEnabled(true);
//
//					}
//				});

	}

	/**
	 * usused
	 */

//	private void registerThisDownload() {
//		String json = "{\"song_id\":\"" + playedSong.SID + "\",\"url\":\""
//				+ playedSong.url + "\"}";
//		apiCalls.api.downloadFile(json, new NewApiCalls.Callback() {
//			@Override
//			public void onFinished(String response) {
//				try {
//					toast(response);
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		});
//
//	}

	/**
	 * - getting song's url that we wanna download the song from it - initialize
	 * the download service and start it with new download - adding the song to
	 * download list in sqlite db
	 */
	private void download() {
		try {
			URL url = new URL(playedSong.getSongURL());
			URI uri = new URI(url.getProtocol(), url.getUserInfo(),
					url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), url.getRef());
			url = uri.toURL();

			Intent downloadIntent = new Intent(
					"mp3ready.download.services.IDownloadService");
//			boolean inserted = db.addSongToPlayList(playedSong.SID,
//					playedSong.duration, playedSong.listeners,
//					playedSong.songcover, playedSong.likeState,
//					playedSong.rate, url.toExternalForm(), playedSong.SName,
//					playedSong.Artist, playedSong.link.bitrate,
//					Enums.PLAYLIST_DOWNLOAD_LIST);
			// if (inserted) {
			downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
			downloadIntent.putExtra(MyIntents.URL, url.toExternalForm());
			downloadIntent.putExtra(MyIntents.TITLE, playedSong.SName);
			downloadIntent.putExtra(MyIntents.ARTIST, playedSong.Artist);
			mainActivity.startService(downloadIntent);
			// registerThisDownload();
			action_download.setEnabled(false);
			action_download.setImageDrawable(mainActivity.getResources()
					.getDrawable(R.drawable.settings_ic_sync_run));
			adapter.updateSong(pager.getCurrentItem(),
					String.valueOf(((int) rate_song.getRating())),
					String.valueOf(playedSongLikeState), MyIntents.Types.START);

			// }
			// mainActivity.gotoFragment(ListFragment.newInstance(ListFragment.DOWNLOADS_MANAGER),
			// ListFragment.TAG);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * pin the song url
	 */
	private void pinSongUrl() {
		apiCalls.doAction(apiCalls.buildActionPinSongUrlJson(playedSong.SID,
				playedSong.getSongURL()), new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				try {
					ZdParser parser = new ZdParser(response);
					if (parser.code == 200) {
						toast("song url is pinned");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * adding song to pl definded in json
	 * 
	 * @param json
	 *            conaints song id and play list that we want to add the song in
	 *            it
	 */
	private void addSongToPL(String json) {
		apiCalls.doAction(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				try {
					ZdParser parser = new ZdParser(response);
					if (parser.code == 200) {
						toast("song added to playlist");
					} else {
						toast(parser.response);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});


	}

	/**
	 * create new playlust with new name with song defined by user
	 * 
	 * @param pl_name
	 *            the name of new playlist
	 */
	private void createPlayListWithCurrentSong(String pl_name) {
		String json = "{\"list_name\":\"" + pl_name + "\",\"songs_ids\":[\""
				+ playedSong.SID + "\"]}";
		apiCalls.createPlayList(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					toast("playlist created with current song");
				} else {
					toast(parser.response);
				}

			}
		});
	}

	/**
	 * show adding song to playlist dailog
	 * 
	 * @param type
	 *            the type of dailog (select playlist , create new playlist or
	 *            update playlist)
	 */
	private void showPLDailog(int type) {
		SelectCreatePlayListDialog dialog = SelectCreatePlayListDialog
				.newInstance(type, new ICallBack() {

					@Override
					public void onSwitchToCreateMode() {
						
						showPLDailog(SelectCreatePlayListDialog.TYPE_CREATE);
					}

					@Override
					public void onSelectPlayList(PlayList pl) {
						
						addSongToPL(apiCalls.buildActionAddSongToListJson(
								playedSong.SID, pl.id));
					}

					@Override
					public void onCreatePlayList(String pl_name) {
						
						createPlayListWithCurrentSong(pl_name);
					}

					@Override
					public void onUpdatePlayList(String pl_name) {
						

					}
				});
		dialog.show(mainActivity.getSupportFragmentManager(),
				SelectCreatePlayListDialog.TAG);
	}

//	private boolean runPlayerForNextPlayableUrl(Song s) {
//
//		if (s.forceTryNextUrl()) {
//			mainActivity.playSong(s);
//			Log.i(TAG, "changePlayableUrl-" + s.current_valid_url_indx + 1
//					+ ":" + s.url);
//			playedSong = s;
//			return true;
//		} else {
//			return false;
//		}
//
//	}

//	private boolean runPlayerForPrevPlayableUrl(Song s) {
//
//		if (s.forceTryPrevUrl()) {
//			mainActivity.playSong(s);
//			Log.i(TAG, "changePlayableUrl-" + s.current_valid_url_indx + 1
//					+ ":" + s.url);
//			playedSong = s;
//			return true;
//		} else {
//			return false;
//		}
//
//	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
//		case R.id.fp_mediacontroller_next_url:
//			this.output.release();
//			if (!runPlayerForNextPlayableUrl(playedSong)) {
//				playedSong = mainActivity.runPlayerForNextUrl(playedSong);
//				fp_mediacontroller_msg
//						.setText((playedSong.current_url_indx + 1) + " of "
//								+ playedSong.urls.size());
//			}
//			break;
//		case R.id.fp_mediacontroller_prev_url:
//			this.output.release();
//			if (!runPlayerForPrevPlayableUrl(playedSong)) {
//				playedSong = mainActivity.runPlayerForPrevUrl(playedSong);
//				fp_mediacontroller_msg
//						.setText((playedSong.current_url_indx + 1) + " of "
//								+ playedSong.urls.size());
//			}
//			break;
		case R.id.fp_mediacontroller_shuffle:
			// pager.setCurrentItem(Utilities.randInt(0, adapter.getCount()-1));
			String onOff = (String) v.getTag();
			if (onOff.equals("on")) {
				mainActivity.isShuffle = false;
				fp_mediacontroller_shuffle.setImageDrawable(mainActivity
						.getResources().getDrawable(
								R.drawable.player_ic_shuffle));
				fp_mediacontroller_shuffle.setTag("off");
			} else if (onOff.equals("off")) {
				mainActivity.isShuffle = true;
				fp_mediacontroller_shuffle.setImageDrawable(mainActivity
						.getResources().getDrawable(
								R.drawable.player_ic_shuffle_active));
				fp_mediacontroller_shuffle.setTag("on");
			}
			break;

		case R.id.song_action_add_to_pl:
			showPLDailog(SelectCreatePlayListDialog.TYPE_SELECT);
			break;
		case R.id.song_action_dislike:
			setLikeState(playedSong.SID, Enums.DISLIKE_STATE, action_dislike);
			break;
		case R.id.song_action_download:
			download();
			break;
		case R.id.song_action_like:
			setLikeState(playedSong.SID, Enums.LIKE_STATE, action_like);
			break;
		case R.id.song_action_pin:
//			if (playedSong.url != null && !playedSong.url.equals("")) {
//				pinSongUrl();
//			}
			break;

		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		mainActivity.am_i_in_full_player_screen = true;
		super.onConfigurationChanged(newConfig);
		mainActivity.getSupportFragmentManager().beginTransaction()
				.detach(this).attach(this).commitAllowingStateLoss();
	}

	@Override
	protected void setLikeState(String song_id, final int like_State,
			final View v) {
		
		super.setLikeState(song_id, like_State, v);
		apiCalls.doAction(apiCalls.buildActionLikeStateJson(song_id, like_State), new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {

					v.setEnabled(false);
					playedSongLikeState = like_State;
//					if (playedSong.link.download_state == null) {
//						Log.i(TAG,
//								"playedSong.link.download_state null");
//					}
					mainActivity.db.updateSongInPlayList(
							playedSong.file, playedSong.SID,
							(int) rate_song.getRating(),
							playedSongLikeState);
//					adapter.updateSong(pager.getCurrentItem(),
//							String.valueOf(((int) rate_song
//									.getRating())), String
//									.valueOf(playedSongLikeState),
//							playedSong.file.download_state);
					if (like_State == Enums.LIKE_STATE) {
						((ImageButton) v)
								.setImageDrawable(mainActivity
										.getResources()
										.getDrawable(
												R.drawable.love_circle_onboarding_active));
					} else if (like_State == Enums.DISLIKE_STATE) {
						((ImageButton) v)
								.setImageDrawable(mainActivity
										.getResources()
										.getDrawable(
												R.drawable.unlove_circle_onboarding_active));
					}
//					playedSong.likeState = String
//							.valueOf(like_State);
				} else {
					toast("Error");
				}

			}
		});
//		ApiCalls.doAction(
//				ApiCalls.buildActionLikeStateJson(song_id, like_State),
//				new CallbackHandler(mainActivity) {
//
//					@Override
//					public void onFinished(String result) {
//
//						try {
//						} catch (Exception e) {
//							// TODO: handle exception
//							e.printStackTrace();
//						}
//					}
//				});
	}

	private void rateCurrentSong(final float rate) {
		rate_song.setEnabled(false);
		apiCalls.doAction(apiCalls.buildActionRateJson(playedSong.SID, (int) rate), new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					rate_song.setEnabled(true);
					mainActivity.db.updateSongInPlayList(
							playedSong.file, playedSong.SID,
							(int) rate, playedSongLikeState);
//					adapter.updateSong(pager.getCurrentItem(),
//							String.valueOf(((int) rate)),
//							String.valueOf(playedSongLikeState),
//							playedSong.link.download_state);

				}

			}
		});
//		ApiCalls.doAction(
//				ApiCalls.buildActionRateJson(playedSong.SID, (int) rate),
//				new CallbackHandler(mainActivity) {
//
//					@Override
//					public void onFinished(String result) {
//
//						try {
//						} catch (Exception e) {
//							// TODO: handle exception
//							e.printStackTrace();
//						}
//					}
//				});
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rate,
			boolean fromuser) {
		
		if (fromuser) {
			rateCurrentSong(rate);
		}
	}

	interface OnPlayNewSongListener {
		void onPlayNewSong(int pos);
	}

	class myHandler {

		public void triggerHandler(final int pos,
				final OnPlayNewSongListener callback) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					
					if (pos == pager.getCurrentItem()) {
						Log.i(TAG,
								"pos:" + pos + " == currentpos:"
										+ pager.getCurrentItem());
						callback.onPlayNewSong(pos);
					} else {
						Log.i(TAG,
								"pos:" + pos + " != currentpos:"
										+ pager.getCurrentItem());
					}
				}
			}, 5000);
		}

	}
}