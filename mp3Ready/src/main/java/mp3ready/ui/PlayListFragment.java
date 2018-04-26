package mp3ready.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mp3ready.adapters.SongItemsAdatper;
import mp3ready.api.NewApiCalls;
import mp3ready.entities.PlayList;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.response.ListSongsResponse;
import mp3ready.serializer.ZdParser;
import mp3ready.util.App;
import mp3ready.util.ICallBack;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zgm.mp3ready.R;

/**
 * <p>
 * playlist screen that show image of first song in it
 * </p>
 * <p>
 * list of its songs
 * </p>
 * <p>
 * user can play the song or add it to another playlist
 * </p>
 * 
 * @author mhmd
 * 
 */

public class PlayListFragment extends Zfragment implements OnScrollListener,
		OnItemClickListener {

	public final static String TAG = PlayListFragment.class.getName();
	/**
	 * playlist types
	 */
	public final static int USER_DEFINE = 1;
	public final static int FROM_FRIENDS = 7;
	public final static int LIKES = 11;
	public final static int FIVE_STAR = 12;
	public final static int FOUR_STAR = 13;
	public final static int HISTORY = 10;
	private int type = 1;
	private ImageView iv_pl_cover;
	private TextView tv_pl_name;
	private TextView tv_pl_tracks;
	private GridView songsList;
	private ProgressBar pb_loading;
	private TextView tv_loading;
	private ProgressBar pb_loading_mor;
	private SongItemsAdatper song_adapter;
	private boolean firstTime = true;
	private PlayList mPlayList = null;
//	private String playlist_id;
	private int songs_total_count = 0;
	private int pageNumber = 1;
	private int posInPager = 3;
	private RelativeLayout pl_info_rl;
//	private List<Song> songs;

	public static PlayListFragment newInstance(int type) {
		PlayListFragment efrag = new PlayListFragment();
		efrag.type = type;
		return efrag;
	}

	public static PlayListFragment newInstance(int type, PlayList mPlayList) {
		PlayListFragment efrag = new PlayListFragment();
		efrag.mPlayList = mPlayList;
		efrag.type = type;
		return efrag;
	}

	public static PlayListFragment newInstance(int type, int posInPager) {
		PlayListFragment efrag = new PlayListFragment();
		efrag.posInPager = posInPager;
		efrag.type = type;
		return efrag;
	}

	/**
	 * <p>
	 * initialize the views
	 * </p>
	 */
	private void initView() {
		iv_pl_cover = (ImageView) getView().findViewById(R.id.iv_pl_cover);
		tv_pl_name = (TextView) getView().findViewById(R.id.playlist_name);
		tv_pl_tracks = (TextView) getView().findViewById(
				R.id.playlist_tracks_num);
		pb_loading = (ProgressBar) getView().findViewById(R.id.pb_loading);
		pb_loading.setVisibility(View.GONE);
		tv_loading = (TextView) getView().findViewById(R.id.tv_loading);
		pb_loading_mor = (ProgressBar) getView()
				.findViewById(R.id.pb_load_more);
		songsList = (GridView) getView().findViewById(R.id.list);
		songsList.setOnScrollListener(this);
		songsList.setOnItemClickListener(this);
		pl_info_rl = (RelativeLayout) getView().findViewById(R.id.pl_info_rl);
		// RelativeLayout.LayoutParams lp = new
		// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)
		// (Utilities.getScreenSizePixels(mainActivity)[1]/2.5));
		// lp.height =
		// (int)(Utilities.getScreenSizePixels(mainActivity)[1]/2.5);
		// pl_info_rl.setLayoutParams(lp);

	}

	private void addSongToPL(String json) {
		apiCalls.doAction(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					toast("song added to playlist");
				} else {
					toast(parser.response);
				}

			}
		});
//		ApiCalls.doAction(json, new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		});

	}

	private void createPlayListWithCurrentSong(String pl_name, Song song) {
		String json = "{\"list_name\":\"" + pl_name + "\",\"songs_ids\":[\""
				+ song.SID + "\"]}";
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
//		ApiCalls.createPlayList(json, new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		});
	}

	private void showPLDailog(int type, final Song song) {
		SelectCreatePlayListDialog dialog = SelectCreatePlayListDialog
				.newInstance(type, new ICallBack() {

					@Override
					public void onSwitchToCreateMode() {
						
						showPLDailog(SelectCreatePlayListDialog.TYPE_CREATE,
								song);
					}

					@Override
					public void onSelectPlayList(PlayList pl) {
						
						addSongToPL(apiCalls.buildActionAddSongToListJson(
								song.SID, pl.id));
					}

					@Override
					public void onCreatePlayList(String pl_name) {
						
						createPlayListWithCurrentSong(pl_name, song);
					}

					@Override
					public void onUpdatePlayList(String pl_name) {
						

					}
				});
		dialog.show(mainActivity.getSupportFragmentManager(),
				SelectCreatePlayListDialog.TAG);
	}

	private void popMenu(View btn, boolean isDeleteVisible) {

		PopupMenu popup = new PopupMenu(mainActivity, btn);

		final Song song = (Song) btn.getTag();
		/** Adding menu items to the popumenu */
		popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
		if (isDeleteVisible) {
			popup.getMenu().findItem(R.id.action_delete_from_pl)
					.setVisible(true);
		} else {
			popup.getMenu().findItem(R.id.action_delete_from_pl)
					.setVisible(false);
		}
		/** Defining menu item click listener for the popup menu */
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(android.view.MenuItem item) {
				
				switch (item.getItemId()) {
				case R.id.action_delete_from_pl:
					removeSongFromPL(song);
					break;
				case R.id.action_add_to_pl:
					showPLDailog(SelectCreatePlayListDialog.TYPE_SELECT, song);
					break;
				}
				return true;
			}
		});

		/** Showing the popup menu */
		popup.show();

	}

	private void removeSongFromPL(final Song song) {
		apiCalls.doAction(apiCalls.buildActionRemoveSongFromListJson(song.SID, mPlayList.id), new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					// toast(parser.response);
					song_adapter.deleteSong(song);
				} else {
					toast(parser.response);
				}

			}
		});
//		ApiCalls.doAction(ApiCalls.buildActionRemoveSongFromListJson(song.SID,
//				song.song_action_id), new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		});
	}

	private void renderPlayListSongs(List<Song> songs) {
		if (song_adapter == null) {
			song_adapter = new SongItemsAdatper(mainActivity, true) {

				@Override
				protected void goToDetails(Song song) {
					
					mainActivity.gotoFragment(
							SongDetailsFragment.newInstance(song),
							SongDetailsFragment.TAG, true);// is child
				}

				@Override
				protected void popUpMenu(View btn) {
					
					if (type == PlayListFragment.USER_DEFINE) {
						popMenu(btn, true);
					} else {
						popMenu(btn, false);
					}

				}
			};
		}

		song_adapter.loadMoreSongs(songs);
		if (songsList.getAdapter() == null) {
			songsList.setAdapter(song_adapter);
		}
		song_adapter.notifyDataSetChanged();
	}

	/**
	 * <p>
	 * load image of playlist's cover according to its type
	 * </p>
	 */
	private void renderPlayListCoverAccordingToType() {
		int size = (int) Math.ceil(Math.sqrt(App.MAX_IMAGE_WIDTH
				* App.MAX_IMAGE_HEIGHT));
		mainActivity.mPicasso.load(PagerFragment.coversMap.get(type))
		// .transform(new BitmapTransform(App.MAX_IMAGE_WIDTH,
		// App.MAX_IMAGE_HEIGHT))
				.resize(size, size).centerInside()
				// .error(mainActivity.getResources().getDrawable(R.drawable.default_song_cover))
				// .fit()
				.into(iv_pl_cover);
	}

	/**
	 * <p>
	 * render the playlist info into views and load the image of playlist
	 * </p>
	 * 
	 * @param pl
	 *            the playlist that we should to render its information
	 */
	private void renderPlayList(final PlayList pl) {

		apiCalls.getListSongs(pl.id, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					List<Song> songs = gson.fromJson(parser.response,
							ListSongsResponse.class).items;

					if (songs != null && songs.size() > 0) {
						renderPlayListSongs(songs);
					} else {
						pb_loading.setVisibility(View.GONE);
						tv_loading.setVisibility(View.VISIBLE);
					}

					try {
						if (type == PlayListFragment.USER_DEFINE && songs != null
								&& songs.size() > 0) {
							if (songs.get(0).songcover != null
									&& !songs.get(0).songcover.equals("")) {
								// toast("render playlist cover");

								mainActivity.mPicasso.load(songs.get(0).songcover)
										// .transform(new BitmapTransform(App.MAX_IMAGE_WIDTH,
										// App.MAX_IMAGE_HEIGHT))
										// .error(R.drawable.default_song_cover)


										.into(iv_pl_cover);
							} else {

								renderPlayListCoverAccordingToType();
							}
						} else {
							renderPlayListCoverAccordingToType();
						}
					} catch (OutOfMemoryError e) {
						// TODO: handle exception
						e.printStackTrace();
					}
		/*
		 * RelativeLayout.LayoutParams lp = new
		 * RelativeLayout.LayoutParams(RelativeLayout
		 * .LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
		 * lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		 * tv_pl_name.setLayoutParams(lp); tv_pl_tracks.setLayoutParams(lp);
		 */
					if (pl.name != null && !pl.name.equals("")) {
						tv_pl_name.setText(pl.name);
					} else if (pl.name != null && !pl.name.equals("")) {
						tv_pl_name.setText(pl.name);
					}
					if (songs.size() > 1)
						tv_pl_tracks.setText(songs.size() + " Tracks");
					else
						tv_pl_tracks.setText(songs.size() + " Track");
					songs_total_count = songs.size();
//					if (pl.id != null) {
//						playlist_id = pl.id;
//					}
//					renderPlayListSongs();
				} else {
					toast(parser.response);
				}

			}
		});



	}

//	private void loadMoreSongs(int page) {
//		String json = "{\"list_id\" : " + playlist_id + ",\"page\":\"" + page
//				+ "\"}";
//		apiCalls.playListView(json, new NewApiCalls.Callback() {
//			@Override
//			public void onFinished(String response) {
//				ZdParser parser = new ZdParser(response);
//				if (parser.code == 200) {
//					mPlayList = gson.fromJson(parser.response,
//							PlayList.class);
//					renderPlayListSongs(mPlayList);
//				} else {
//					toast(parser.response);
//				}
//
//			}
//		});
////		ApiCalls.playListView(json, new CallbackHandler(mainActivity) {
////
////			@Override
////			public void onFinished(String result) {
////
////				try {
////				} catch (Exception e) {
////					// TODO: handle exception
////					e.printStackTrace();
////				}
////			}
////		});
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		// RelativeLayout.LayoutParams lp = new
		// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,(int)
		// (Utilities.getScreenSizePixels(mainActivity)[1]/2.5));
		// lp.height =
		// (int)(Utilities.getScreenSizePixels(mainActivity)[1]/2.5);
		// pl_info_rl.setLayoutParams(lp);
//		if (type == PlayListFragment.USER_DEFINE) {
//			mainActivity.getSupportFragmentManager().beginTransaction()
//			// .replace(R.id.content,
//			// PlayListFragment.newInstance(type,mPlayList),ListFragment.TAG)
//					.detach(this).attach(this).commit();
//		}

	}

	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
	}

	/**
	 * <p>
	 * parse the response from server and getting the playlist
	 * </p>
	 * 
	 * @param response
	 *            json response from server
	 */
	private void preparePlayList(String response) {
		Type TYPE_ArrayList_pl = new TypeToken<ArrayList<PlayList>>() {}.getType();
		List<PlayList> pls = gson.fromJson(response, TYPE_ArrayList_pl);
		for (PlayList pl : pls) {
			if (pl.type!=null && pl.type.equals(String.valueOf(this.type))) {
				renderPlayList(pl);
				break;
			}
		}
	}

	@Override
	public void onDetach() {
		
		super.onDetach();
		mainActivity.removeObserver(String.valueOf(this.type));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater
				.inflate(R.layout.playlist_layout, container, false);

		return view;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		initView();
		/**
		 * <p>
		 * if playlists's type one of them then we render the playlist directly
		 * from cach or from server
		 * </p>
		 *
		 *
		 *
		 * redundant parts
		 */
//		if (this.type == PlayListFragment.FROM_FRIENDS
//				|| this.type == PlayListFragment.LIKES
//				|| this.type == PlayListFragment.FIVE_STAR
//				|| this.type == PlayListFragment.FOUR_STAR
//				|| this.type == PlayListFragment.HISTORY) {
//
//			renderPlayListCoverAccordingToType();
//			if (song_adapter == null) {
//				pb_loading.setVisibility(View.VISIBLE);
//				if (mainActivity.mCachedResponse != null
//						&& !mainActivity.mCachedResponse.equals("")) {
//					pb_loading.setVisibility(View.GONE);
//					preparePlayList(mainActivity.mCachedResponse);
//				} else {
//					mainActivity.addObserver(new MainActivity.IObserver() {
//
//						@Override
//						public void onDoActionOnSong() {
//						}
//
//						@Override
//						public void onTrackAdded(PlayList pl) {
//
//
//						}
//
//						@Override
//						public void onFinished(String response) {
//
//							preparePlayList(response);
//						}
//
//						@Override
//						public String getId() {
//
//							return String.valueOf(type);
//						}
//
//					});
//				}
//			} else {
//				songsList.setAdapter(song_adapter);
//				song_adapter.notifyDataSetChanged();
//			}

			/**
			 * if the type of playlist is user defined then we render the
			 * playlist from server
			 */
//		} else
			if (this.type == PlayListFragment.USER_DEFINE) {
			if (mPlayList != null) {
//				if (song_adapter == null) {
					renderPlayList(mPlayList);

//					playlist_id = mPlayList.id;
//					String json = "{\"list_id\" : " + playlist_id + "}";
//					pb_loading.setVisibility(View.VISIBLE);
//					apiCalls.playListView(json, new NewApiCalls.Callback() {
//						@Override
//						public void onFinished(String response) {
//							ZdParser parser = new ZdParser(response);
//							pb_loading.setVisibility(View.GONE);
//							if (parser.code == 200) {
////								mPlayList = gson.fromJson(parser.response,
////										PlayList.class);
//								mPlayList.id = playlist_id;
//								renderPlayList(mPlayList);
//								if (songs != null
//										&& songs.size() > 0) {
//									renderPlayListSongs();
//								} else {
//									pb_loading.setVisibility(View.GONE);
//									tv_loading.setVisibility(View.VISIBLE);
//								}
//							} else {
//								toast(parser.response);
//							}
//
//						}
//					});
//					ApiCalls.playListView(json, new CallbackHandler(
//							mainActivity) {
//
//						@Override
//						public void onFinished(String result) {
//
//							try {
//							} catch (Exception e) {
//								// TODO: handle exception
//								e.printStackTrace();
//							}
//						}
//					});
//				} else {
//					renderPlayList(mPlayList);
//					songsList.setAdapter(song_adapter);
//					song_adapter.notifyDataSetChanged();
//				}
			} else {
				toast("play list null");
			}
		}
	}

	@Override
	public void onStart() {
		
		super.onStart();

	}

	@Override
	public void onPause() {
		
		super.onPause();

	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		}
		return super.onOptionsItemSelected(item);

	}

	/**
	 * when user scrolling to end of list of songs then we should load next page
	 * if exist
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (song_adapter != null && song_adapter.getCount() < songs_total_count
				&& firstTime == false) {
			final int lastItem = firstVisibleItem + visibleItemCount;
			if (totalItemCount != 0 && lastItem == totalItemCount) {
				pb_loading_mor.setVisibility(View.VISIBLE);
				pageNumber++;
//				loadMoreSongs(pageNumber);
			} else {
				pb_loading_mor.setVisibility(View.GONE);
			}
		} else {
			firstTime = false;
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
			long arg3) {
		
		Song selectedSong = (Song) adapter.getItemAtPosition(pos);
//		if (selectedSong != null) {
//			boolean insert = mainActivity.db
//					.addSongToPlayList(
//							selectedSong.SID,
//							selectedSong.duration,
//							selectedSong.listeners,
//							selectedSong.songcover,
//							selectedSong.likeState,
//							selectedSong.rate,
//							selectedSong.url,
//							selectedSong.SName,
//							selectedSong.Artist,
//							(selectedSong.link != null
//									&& selectedSong.link.bitrate != null ? selectedSong.link.bitrate
//									: "0"), Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//			if (insert) {
//				Log.e("ZFragment", "song inserted to recently playlist");
//				toast("song added to Recently Played List");
//			} else {
//				toast("song is already exist in Recently Played List");
//			}

//			mainActivity.playSongIfNoCurrentSongExist(selectedSong);

		List<Song> uri = new ArrayList<>();
		for(int i=0;i<adapter.getCount();i++)
		{
			Song s = (Song) adapter.getItemAtPosition(i);
			uri.add(s);
		}

//		mainActivity.playSongIfNoCurrentSongExist(uri);
		mainActivity.playerBinder.initializePlayerSong(uri);


			// mainActivity.getMp3Urls(selectedSong, null);
//		}
	}

}