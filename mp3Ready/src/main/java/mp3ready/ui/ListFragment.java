package mp3ready.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mp3ready.adapters.DownloadsAdatper;
import mp3ready.adapters.PlayListsAdatper;
import mp3ready.adapters.SearchResultsDownloadAdatper;
import mp3ready.adapters.SongCellsAdatper;
import mp3ready.adapters.SongItemsAdatper;
import mp3ready.adapters.UserFavGenreAdatper;
import mp3ready.api.NewApiCalls;
import mp3ready.entities.DownloadLink;
import mp3ready.entities.PlayList;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.response.ArtistSongsResponse;
import mp3ready.response.SongsSearchResultsResponse;
import mp3ready.response.TopTagSongsResponse;
import mp3ready.serializer.ZdParser;
import mp3ready.util.ICallBack;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zgm.mp3ready.R;

/**
 * - this fregments responsable of all list of songs in whole application -
 * render RECOMMENDED songs all recommend songs - render RECOMMENDED_BY_GENRE
 * songs all recommend songs by genre - render SONGS songs all search results
 * songs - render SEARCH_RESULTS_DOWNLOADS songs all search results download
 * links - render DOWNLOADS_MANAGER songs all downloads - render ARTIST_SONGS
 * songs all artist's songs - render TOP_GENRE_SONGS songs songs of every top
 * genre - render RECENTLY_PLAYED songs recently played songs - render
 * USER_FAV_GENRES songs all genres to let user choose from them - render
 * PLAY_LISTS songs all user defined play list
 * 
 * @author mhmd
 * 
 */

public class ListFragment extends Zfragment implements OnClickListener,
		OnScrollListener, OnItemClickListener {

	public final static String TAG = ListFragment.class.getName();
	public final static int RECOMMENDED = 0;
	public final static int RECOMMENDED_BY_GENRE = 1;
	public final static int SONGS = 2;
	public final static int SEARCH_RESULTS_DOWNLOADS = 3;
	public final static int DOWNLOADS_MANAGER = 4;
	public final static int ARTIST_SONGS = 5;
	public final static int TOP_GENRE_SONGS = 6;

	public final static int RECENTLY_PLAYED = 8;
	public final static int USER_FAV_GENRES = 9;

	public final static int PLAY_LISTS = 14;

	private GridView list;
	private ProgressBar pb_loading;
	private TextView tv_loading;
	private ProgressBar pb_loading_mor;
	private boolean firstTime = true;
	private List<Song> topGenreSongs;
	private Button btn_goto_tinder;
	// private boolean needLoadMore = true;
	List<PlayList> pls;
	private int pageNumber = 1;
	// private int lastFirstItemPositionInlist=0;
	private int type = 0;
	private SongsSearchResultsResponse searchResults;
	private List<DownloadLink> searchResultslinks;
	private List<Song> data;
	private SongItemsAdatper song_adapter;
	private SearchResultsDownloadAdatper link_adapter;
	private NewApiCalls.Callback handlre;
	private SongCellsAdatper song_cells_adapter;
	private PlayListsAdatper playlist_adapter;
	private UserFavGenreAdatper genre_adapter;
	private String query;
	private boolean withSimilarity = false;
	private boolean withAlbum = true;
	private ArtistSongsResponse mArtistSongsResponse;
	private boolean isHome;

	public static ListFragment newInstance(int type, boolean isHome) {
		ListFragment efrag = new ListFragment();
		efrag.type = type;
		efrag.isHome = isHome;
		return efrag;
	}

	public static ListFragment newInstance(int type, String query) {
		ListFragment efrag = new ListFragment();
		efrag.type = type;
		efrag.query = query;
		return efrag;
	}

	public static ListFragment newInstance(int type, List<Song> songs) {
		ListFragment efrag = new ListFragment();
		efrag.type = type;
		efrag.topGenreSongs = songs;
		return efrag;
	}

	private void initView() {
		list = (GridView) getView().findViewById(R.id.list);
		if (this.type != ListFragment.SEARCH_RESULTS_DOWNLOADS
				&& this.type != ListFragment.DOWNLOADS_MANAGER
				&& this.type != ListFragment.TOP_GENRE_SONGS
				&& this.type != ListFragment.PLAY_LISTS
				&& this.type != ListFragment.RECENTLY_PLAYED
				&& this.type != ListFragment.USER_FAV_GENRES) {
			list.setOnScrollListener(this);
		}
		pb_loading = (ProgressBar) getView().findViewById(R.id.pb_loading);
		pb_loading.setVisibility(View.GONE);
		list.setOnItemClickListener(this);
		tv_loading = (TextView) getView().findViewById(R.id.tv_loading);
		pb_loading_mor = (ProgressBar) getView()
				.findViewById(R.id.pb_load_more);
		btn_goto_tinder = (Button) getView().findViewById(R.id.btn_next_tinder);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		handlre = new callBackHandlre();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		

		View view = inflater.inflate(R.layout.list_layout, container, false);
		return view;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	/**
	 * - initialize the adapter of song cells according to type of list - in
	 * artist's songs we render the album name otherwise render artist name
	 */
	private void loadSongCells() {

		if (song_cells_adapter == null) {

			song_cells_adapter = new SongCellsAdatper(mainActivity,
					withSimilarity, withAlbum) {
				@Override
				protected void artistPage(Song song) {

					mainActivity.gotoFragment(
							ArtistScreenFragment.newInstance(song.ArtistID),
							ArtistScreenFragment.TAG, true);// is child
				}

				@Override
				protected void playSong(Song song) {

					if (getActivity() != null && mainActivity != null) {
						mainActivity.playSong(song);
//						mainActivity.getMp3Urls(song,null);
//						boolean insert = mainActivity.db
//								.addSongToPlayList(
//										song.SID,
//										song.duration,
//										song.listeners,
//										song.songcover,
//										song.likeState,
//										song.rate,
//										song.file,
//										song.SName,
//										song.Artist,
//										(song.link != null
//												&& song.link.bitrate != null ? song.link.bitrate
//												: "0"),
//										Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//						if (insert) {
//							Log.e("ZFragment",
//									"song inserted to recently playlist");
//							toast("song added to Recently Played List");
//						} else {
//							toast("song is already exist in Recently Played List");
//						}
//						mainActivity.playSongIfNoCurrentSongExist(song);
					}
				}

				@Override
				protected void songDetails(Song song) {

					mainActivity.gotoFragment(
							SongDetailsFragment.newInstance(song),
							SongDetailsFragment.TAG, true);// is child
				}

				@Override
				protected void setLikeState(String song_id,
						final int like_State, final ImageView v) {

					apiCalls.doAction(apiCalls.buildActionLikeStateJson(song_id, like_State), new NewApiCalls.Callback() {
						@Override
						public void onFinished(String response) {
							ZdParser parser = new ZdParser(response);
							if (parser.code == 200) {

								if (type == ListFragment.RECOMMENDED) {
									song_cells_adapter = null;
									pageNumber = 1;
									list.setAdapter(null);
									getRecommendSongs();
								} else if (type == ListFragment.RECOMMENDED_BY_GENRE) {
									song_cells_adapter = null;
									pageNumber = 1;
									list.setAdapter(null);
									// TODO: 2/14/2018 s4 fix this
//                                        getRecommendByGenre();
								} else if (type == ListFragment.ARTIST_SONGS) {
									v.setEnabled(false);
									if (like_State == Enums.LIKE_STATE) {
										v.setImageDrawable(mainActivity
												.getResources()
												.getDrawable(
														R.drawable.love_circle_onboarding_active));
									} else if (like_State == Enums.DISLIKE_STATE) {
										v.setImageDrawable(mainActivity
												.getResources()
												.getDrawable(
														R.drawable.unlove_circle_onboarding_active));
									}
								} else if (type == ListFragment.TOP_GENRE_SONGS) {
									mainActivity.notifyOnDoActionOnSong();
								}
							} else {
								toast("Error");
							}

						}
					});

//					ApiCalls.doAction(ApiCalls.buildActionLikeStateJson(
//							song_id, like_State), new CallbackHandler(
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
				}
			};
		}
		if (type == ListFragment.ARTIST_SONGS) {
			song_cells_adapter.loadMoreSongs(mArtistSongsResponse.songs);
		} else if (type == ListFragment.TOP_GENRE_SONGS) {
			song_cells_adapter.loadMoreSongs(this.topGenreSongs);
		} else {
			song_cells_adapter.loadMoreSongs(data);
		}

		if (list.getAdapter() == null)
			list.setAdapter(song_cells_adapter);
		song_cells_adapter.notifyDataSetChanged();
		pb_loading_mor.setVisibility(View.GONE);

	}

	/**
	 * /** adding song to pl definded in json
	 * 
	 * @param json
	 *            conaints song id and play list that we want to add the song to
	 *            it
	 * @param pl
	 *            the playlist that we want to add the song to it
	 */
	private void addSongToPL(String json, final PlayList pl) {
		apiCalls.doAction(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					toast("song added to playlist");
					mainActivity.notifyTrackAdded(
							String.valueOf(ListFragment.PLAY_LISTS), pl);
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

	/**
	 * send request ot server to update user defined playlist
	 * 
	 * @param name
	 *            the new name that user want to update the playlist
	 * @param pl
	 *            the updated playlist
	 */
	private void updatePlayList(final String name, final PlayList pl) {
		String json = "{\"list_id\": \"" + pl.id + "\",\"list_name\":\""
				+ name + "\"}";

		apiCalls.updatePlayList(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					int pos = pls.indexOf(pl);
					pl.name = name;
					playlist_adapter.updatePlayList(pl);
					pls.remove(pos);
					pls.add(pos, pl);
					mainActivity.mCachedResponse = gson.toJson(pls);

				} else {
					toast(parser.response);
				}

			}
		});
//		ApiCalls.updatePlayList(json, new CallbackHandler(mainActivity) {
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

	private void deletePlayList(final PlayList pl) {
		String json = "{\"list_id\": \"" + pl.id + "\"}";
		apiCalls.deletePlayList(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					playlist_adapter.deletePlayList(pl);
					pls.remove(pl);
					mainActivity.mCachedResponse = gson.toJson(pls);
				} else {
					toast(parser.response);
				}

			}
		});
//		ApiCalls.deletePlayList(json, new CallbackHandler(mainActivity) {
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

	private void showPLDailog(int type, final Song song, final PlayList pl) {
		SelectCreatePlayListDialog dialog = SelectCreatePlayListDialog
				.newInstance(type, (pl != null ? pl.name : ""),
						new ICallBack() {

							@Override
							public void onSwitchToCreateMode() {
								
								showPLDailog(
										SelectCreatePlayListDialog.TYPE_CREATE,
										song, pl);
							}

							@Override
							public void onSelectPlayList(PlayList pl) {
								
								addSongToPL(apiCalls
										.buildActionAddSongToListJson(song.SID,
												pl.id), pl);
							}

							@Override
							public void onCreatePlayList(String pl_name) {
								
								createPlayListWithCurrentSong(pl_name, song);
							}

							@Override
							public void onUpdatePlayList(String pl_name) {
								
								updatePlayList(pl_name, pl);
							}
						});
		dialog.show(mainActivity.getSupportFragmentManager(),
				SelectCreatePlayListDialog.TAG);
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
					PlayList newPL = gson.fromJson(parser.response,
							PlayList.class);
					if (mainActivity.mCachedResponse != null
							&& !mainActivity.mCachedResponse.equals("")) {
						Type TYPE_ArrayList_pl = new TypeToken<ArrayList<PlayList>>() {
						}.getType();
						pls = gson.fromJson(mainActivity.mCachedResponse,
								TYPE_ArrayList_pl);
					} else {
						pls = new ArrayList<PlayList>();
					}
					newPL.type = String
							.valueOf(PlayListFragment.USER_DEFINE);
					pls.add(newPL);
					mainActivity.mCachedResponse = gson.toJson(pls);
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

	/**
	 * - initialize the search resutls songs links and when the user click on it
	 * will play the song in url - user can download the song
	 */
//	private void loadSearchResultslinks() {
//		if (link_adapter == null) {
//			link_adapter = new SearchResultsDownloadAdatper(mainActivity) {
//
//				@Override
//				protected void download(DownloadLink link) {
//
//
//					try {
//						URL url = new URL(link.url);
//						URI uri = new URI(url.getProtocol(), url.getUserInfo(),
//								url.getHost(), url.getPort(), url.getPath(),
//								url.getQuery(), url.getRef());
//						url = uri.toURL();
//
//						Intent downloadIntent = new Intent(
//								"mp3ready.download.services.IDownloadService");
//						boolean inserted = db.addSongToPlayList(link.id, "",
//								"", "", "", "", url.toExternalForm(),
//								link.title, "", link.bitrate,
//								Enums.PLAYLIST_DOWNLOAD_LIST);
//						// if (inserted != -1) {
//						downloadIntent.putExtra(MyIntents.TYPE,
//								MyIntents.Types.ADD);
//						downloadIntent.putExtra(MyIntents.URL,
//								url.toExternalForm());
//						downloadIntent.putExtra(MyIntents.TITLE, link.title);
//						downloadIntent.putExtra(MyIntents.ARTIST, "");
//						mainActivity.startService(downloadIntent);
//						// }
//						// mainActivity.gotoFragment(ListFragment.newInstance(ListFragment.DOWNLOADS_MANAGER),
//						// ListFragment.TAG);
//					} catch (MalformedURLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (URISyntaxException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			};
//		}
//		for (DownloadLink link : searchResultslinks) {
//
//			try {
//				URL url = new URL(link.url);
//				URI uri = new URI(url.getProtocol(), url.getUserInfo(),
//						url.getHost(), url.getPort(), url.getPath(),
//						url.getQuery(), url.getRef());
//				url = uri.toURL();
//				Song song = db.isSongExistIn(url.toExternalForm(), "");
//				if (song != null
//						&& (song.whichPlayList == Enums.PLAYLIST_DOWNLOAD_LIST || song.whichPlayList == Enums.PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST)) {
//					link.download_state = song.link.download_state;
//				} else {
//					link.download_state = -1;
//				}
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		link_adapter.loadMoreUrls(searchResultslinks);
//		list.setAdapter(link_adapter);
//		link_adapter.notifyDataSetChanged();
//	}

	private void deleteSongFromPL(final Song song) {
		switch (type) {
		case ListFragment.RECENTLY_PLAYED:
//			mainActivity.player.connectPlayer(new OutputCommand() {
//
//				@Override
//				public void connected(Output output) {
//
//					if (output != null && output.getCurrSong() != null
//							&& output.getCurrSong().SID.equals(song.SID)) {
//						toast("you can't delete it until finish listening to it");
//					} else {
//						int isDeleted = db.deleteSongFromPL(song.file, song.SID,
//								Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//						if (isDeleted != -1) {
//							song_adapter.deleteSong(song);
//						}
//					}
//				}
//			});
			break;
		case ListFragment.SONGS:
			// nothing to delete it
			break;
		}
	}

	/**
	 * show popup menu on song item to control it
	 * 
	 * @param btn
	 *            the view that we want to show popup near of it
	 * @param isDeleteVisible
	 *            flag to show delete btn or not
	 */
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
					deleteSongFromPL(song);
					break;
				case R.id.action_add_to_pl:
					showPLDailog(SelectCreatePlayListDialog.TYPE_SELECT, song,
							null);
					break;
				}
				return true;
			}
		});

		/** Showing the popup menu */
		popup.show();

	}

	/**
	 * show popup menu on playlist item to control it
	 * 
	 * @param btn
	 *            the view that we want to show popup near of it
	 */
	private void popMenuPL(View btn) {

		PopupMenu popup = new PopupMenu(mainActivity, btn);

		final PlayList pl = (PlayList) btn.getTag();
		/** Adding menu items to the popumenu */
		popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
		popup.getMenu().findItem(R.id.action_delete_from_pl).setVisible(true);
		popup.getMenu().findItem(R.id.action_add_to_pl).setTitle("Update");
		/** Defining menu item click listener for the popup menu */
		popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(android.view.MenuItem item) {
				
				switch (item.getItemId()) {
				case R.id.action_delete_from_pl:
					deletePlayList(pl);
					break;
				case R.id.action_add_to_pl:
					showPLDailog(SelectCreatePlayListDialog.TYPE_UPDATE, null,
							pl);
					break;
				}
				return true;
			}
		});

		/** Showing the popup menu */
		popup.show();

	}

	/**
	 * initailize the song items adapter
	 */
	private void loadSongsItems() {
		if (song_adapter == null) {
			song_adapter = new SongItemsAdatper(mainActivity, false) {

				@Override
				protected void goToDetails(Song song) {
					
					mainActivity.gotoFragment(
							SongDetailsFragment.newInstance(song),
							SongDetailsFragment.TAG, true);// is child
				}

				@Override
				protected void popUpMenu(View btn) {
					
					popMenu(btn, false);
				}
			};
		}
		song_adapter.loadMoreSongs(searchResults.items);
		list.setAdapter(song_adapter);
		song_adapter.notifyDataSetChanged();
	}

	/**
	 * - send request to search user's query - render search results songs
	 */
	private void search() {
		String json = "{\"song\":\"" + query + "\",\"artist\":\"\"}";
		apiCalls.search(json, pageNumber, new NewApiCalls.Callback2() {
			@Override
			public void onFinished(String response) {
				pb_loading.setVisibility(View.GONE);
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {

					searchResults = gson.fromJson(parser.response,
							SongsSearchResultsResponse.class);
					if (searchResults != null && searchResults.items != null
							&& searchResults.items.size() > 0) {
						loadSongsItems();
						((AutoCompleteTextView) mainActivity
								.getSupportActionBar()
								.getCustomView()
								.findViewById(
										R.id.search_autoCompleteTextView))
								.setText("");
					} else {
						tv_loading.setVisibility(View.VISIBLE);
						tv_loading.setText("No Songs found");
					}
				} else {
					toast(parser.response);
					tv_loading.setVisibility(View.VISIBLE);
					tv_loading.setText("No Songs found");
				}

			}

			@Override
			public void onProblem() {
				tv_loading.setVisibility(View.VISIBLE);
				tv_loading.setText("No Songs found");
				pb_loading.setVisibility(View.GONE);
				mainActivity.toast(" Couldn't connect to server!");
			}
		});
//		ApiCalls.search(json, pageNumber, new CallBack() {
//
//			@Override
//			public void onUnknownHost() {
//
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("No Songs found");
//				pb_loading.setVisibility(View.GONE);
//				mainActivity.toast(" Couldn't connect to server!");
//			}
//
//			@Override
//			public void onTimeOut() {
//
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("No Songs found");
//				pb_loading.setVisibility(View.GONE);
//				mainActivity.toast(" Connection Timed out, try again later");
//			}
//
//			@Override
//			public void onNoInet() {
//
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("No Songs found");
//				pb_loading.setVisibility(View.GONE);
//				mainActivity.toast("No Internet Connetion");
//			}
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//					pb_loading.setVisibility(View.GONE);
//					ZdParser parser = new ZdParser(result);
//					if (parser.code == 200) {
//
//						searchResults = gson.fromJson(parser.response,
//								SongsSearchResultsResponse.class);
//						if (searchResults != null && searchResults.items != null
//								&& searchResults.items.size() > 0) {
//							loadSongsItems();
//							((AutoCompleteTextView) mainActivity
//									.getSupportActionBar()
//									.getCustomView()
//									.findViewById(
//											R.id.search_autoCompleteTextView))
//									.setText("");
//						} else {
//							tv_loading.setVisibility(View.VISIBLE);
//							tv_loading.setText("No Songs found");
//						}
//					} else {
//						toast(parser.response);
//						tv_loading.setVisibility(View.VISIBLE);
//						tv_loading.setText("No Songs found");
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					pb_loading.setVisibility(View.GONE);
//					tv_loading.setVisibility(View.VISIBLE);
//					tv_loading.setText("No Songs found");
//				}
//			}
//		});
	}

	/**
	 * - send request to search user's query - render search resutls songs'
	 * links
	 */
//	private void searchForUrls() {
//		String json = "{\"song\":\"" + query + "\"}";
//		ApiCalls.searchUrls(json, new CallBack() {
//
//			@Override
//			public void onUnknownHost() {
//				
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("No links found");
//				pb_loading.setVisibility(View.GONE);
//				mainActivity.toast(" Couldn't connect to server!");
//			}
//
//			@Override
//			public void onTimeOut() {
//				
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("No links found");
//				pb_loading.setVisibility(View.GONE);
//				mainActivity.toast(" Connection Timed out, try again later");
//			}
//
//			@Override
//			public void onNoInet() {
//				
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("No links found");
//				pb_loading.setVisibility(View.GONE);
//				mainActivity.toast("No Internet Connetion");
//			}
//
//			@Override
//			public void onFinished(String result) {
//				
//				try {
//					pb_loading.setVisibility(View.GONE);
//					ZdParser parser = new ZdParser(result);
//					if (parser.code == 200) {
//						Type TYPE_ArrayList_link = new TypeToken<ArrayList<DownloadLink>>() {
//						}.getType();
//						searchResultslinks = gson.fromJson(parser.response,
//								TYPE_ArrayList_link);
//						if (searchResultslinks != null
//								&& searchResultslinks.size() > 0) {
//							loadSearchResultslinks();
//						} else {
//							tv_loading.setVisibility(View.VISIBLE);
//							tv_loading.setText("No links found");
//						}
//					} else {
//						toast(parser.response);
//						tv_loading.setVisibility(View.VISIBLE);
//						tv_loading.setText("No links found");
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					pb_loading.setVisibility(View.GONE);
//					tv_loading.setVisibility(View.VISIBLE);
//					tv_loading.setText("No links found");
//				}
//			}
//		});
//	}

	/**
	 * getting artist's songs and render them
	 */
	private void getArtistSongs() {
		tv_loading.setVisibility(View.GONE);
		String json = "{\"artist_id\":\"" + query + "\"}";
		apiCalls.artistSongs(json, pageNumber, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				pb_loading.setVisibility(View.GONE);
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					mArtistSongsResponse = gson.fromJson(
							parser.response,
							ArtistSongsResponse.class);
					if (mArtistSongsResponse != null
							&& mArtistSongsResponse.songs != null
							&& mArtistSongsResponse.songs.size() > 0) {
						loadSongCells();
					}
				} else {
					toast(parser.response);
				}

			}
		});
//		ApiCalls.artistSongs(json, pageNumber,
//				new CallbackHandler(mainActivity) {
//
//					@Override
//					public void onFinished(String result) {
//
//						try {
//						} catch (Exception e) {
//							// TODO: handle exception
//							e.printStackTrace();
//							pb_loading.setVisibility(View.GONE);
//						}
//					}
//				});
	}

	/**
	 * getting recommend songs and render them in {@link #handlre}
	 */
	private void getRecommendSongs() {
		tv_loading.setVisibility(View.GONE);
		String json = "{\"page\":\"" + pageNumber + "\"}";
		apiCalls.getRecommendedSongs(json, handlre);
	}

	/**
	 * getting recommend songs by genre and render them in {@link #handlre}
	 */
//	private void getRecommendByGenre() {
//		tv_loading.setVisibility(View.GONE);
//		String json = "{\"page\":\"" + pageNumber + "\"}";
//		ApiCalls.getRecommendByGenre(json, handlre);
//	}

	/**
	 * render top genre songs
	 */
	private void loadTopGenreSongs() {
		genre_adapter = new UserFavGenreAdatper(mainActivity,
				mainActivity.home_page_top_genre_songs);
		list.setAdapter(genre_adapter);
		genre_adapter.notifyDataSetChanged();
	}

	/**
	 * getting top genre songs
	 */
	private void getTopTagsSongs() {
		apiCalls.getTopTagsSongs("", new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				if (getView() != null && getActivity() != null) {
					pb_loading.setVisibility(View.GONE);
					ZdParser parser = new ZdParser(response);
					if (parser.code == 200) {
						Type TYPE_ArrayList_TopTags = new TypeToken<ArrayList<TopTagSongsResponse>>() {
						}.getType();
						mainActivity.home_page_top_genre_songs = gson
								.fromJson(parser.response,
										TYPE_ArrayList_TopTags);

						if (mainActivity.home_page_top_genre_songs != null
								&& mainActivity.home_page_top_genre_songs
								.size() > 0) {
							loadTopGenreSongs();
						}

					} else {
						toast(parser.response);
					}
				}

			}
		});
//		ApiCalls.getTopTagsSongs("", new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//					pb_loading.setVisibility(View.GONE);
//
//				}
//			}
//		});
	}

	/**
	 * render user defined playlists if and only of each playlist's type is "1"
	 * 
	 * @param response
	 *            the response from server that containts all playlists
	 */
	private void renderUserPlayList(String response) {
		Type TYPE_ArrayList_pl = new TypeToken<ArrayList<PlayList>>() {}.getType();
		pls = gson.fromJson(response, TYPE_ArrayList_pl);
		boolean found = false;
		for (PlayList pl : pls) {
			if (pl.type != null && pl.type.equals("1")) {
				found = true;
				break;
			}
		}
		pb_loading.setVisibility(View.GONE);
		if (found) {
			playlist_adapter = new PlayListsAdatper(mainActivity,
					Enums.PLAYLIST_USER_DEFINE_TYPE, true) {

				@Override
				protected void popUpMenu(View btn) {

					popMenuPL(btn);
				}
			};
			playlist_adapter.loadMorePLS(pls);
			list.setAdapter(playlist_adapter);
			playlist_adapter.notifyDataSetChanged();
		} else {
			pb_loading.setVisibility(View.GONE);
			tv_loading.setVisibility(View.VISIBLE);
			tv_loading.setText("no playlists found");
		}

	}

	@Override
	public void onDetach() {
		
		super.onDetach();
		mainActivity.removeObserver(String.valueOf(this.type));
	}

	@Override
	public void onStart() {
		
		super.onStart();
		if (type == ListFragment.TOP_GENRE_SONGS) {
			mainActivity.am_i_in_home = true;
		} else if (type == ListFragment.RECOMMENDED) {
			mainActivity.am_i_in_home = isHome;
		}

	}

	private void getPlayLists() {
		if (playlist_adapter == null) {
			pb_loading.setVisibility(View.VISIBLE);
			apiCalls.getPlayLists("", new NewApiCalls.Callback() {
				@Override
				public void onFinished(String response) {
					ZdParser parser = new ZdParser(response);
					if (parser.code == 200) {
						Log.i(TAG, parser.response);
						pb_loading.setVisibility(View.GONE);
						renderUserPlayList(parser.response);


					} else {
						toast(parser.response);
					}

				}
			});

		} else {
			list.setAdapter(playlist_adapter);
			playlist_adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		initView();

		switch (this.type) {
//			case ListFragment.USER_FAV_GENRES:
//				list.setNumColumns(3);
//				// mainActivity.dAdapter.setSelectedItemPos(Enums.REDEFINE_PROF_SCREEEN_POS);
//				btn_goto_tinder.setOnClickListener(this);
//				if (mainActivity.home_page_choosed_top_genre == null) {
//					mainActivity.home_page_choosed_top_genre = new ArrayList<TopTagSongsResponse>();
//				}
//				if (mainActivity.home_page_choosed_top_genre.size() > 0) {
//					btn_goto_tinder.setVisibility(View.VISIBLE);
//				} else {
//					btn_goto_tinder.setVisibility(View.GONE);
//				}
//				if (genre_adapter == null) {
//					if (mainActivity.home_page_top_genre_songs != null) {
//						loadTopGenreSongs();
//					} else {
//						pb_loading.setVisibility(View.VISIBLE);
//						getTopTagsSongs();
//					}
//				} else {
//					list.setAdapter(genre_adapter);
//					genre_adapter.notifyDataSetChanged();
//				}
//				break;
//


			case ListFragment.RECENTLY_PLAYED:
				list.setNumColumns(1);
				// mainActivity.dAdapter.setSelectedItemPos(Enums.PLAYLIST_RECENTLY_SCREEN_POS);
				if (song_adapter == null) {
					pb_loading.setVisibility(View.VISIBLE);
					List<Song> songs = db
							.getPlayList(Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
					if (songs != null && songs.size() > 0) {
						song_adapter = new SongItemsAdatper(mainActivity, true) {

							@Override
							protected void goToDetails(Song song) {

								mainActivity.gotoFragment(
										SongDetailsFragment.newInstance(song),
										SongDetailsFragment.TAG, true);// is child
							}

							@Override
							protected void popUpMenu(View btn) {

								popMenu(btn, true);
							}
						};
						song_adapter.loadMoreSongs(songs);
						list.setAdapter(song_adapter);
						song_adapter.notifyDataSetChanged();
						pb_loading.setVisibility(View.GONE);
					} else {
						pb_loading.setVisibility(View.GONE);
						tv_loading.setVisibility(View.VISIBLE);
					}
				} else {
					list.setAdapter(song_adapter);
					song_adapter.notifyDataSetChanged();
				}
				break;


			case ListFragment.PLAY_LISTS:
				list.setNumColumns(1);
				getPlayLists();
				// mainActivity.dAdapter.setSelectedItemPos(Enums.PLAYLIST_PLS_SCREEN_POS);
				break;



			case ListFragment.ARTIST_SONGS:
				// mainActivity.dAdapter.setSelectedItemPos(-1);
				if (song_cells_adapter == null) {

					pb_loading.setVisibility(View.VISIBLE);
					getArtistSongs();
				} else {
					list.setAdapter(song_cells_adapter);
					song_cells_adapter.notifyDataSetChanged();
				}
				break;
			case ListFragment.DOWNLOADS_MANAGER:
				list.setNumColumns(1);
				// mainActivity.dAdapter.setSelectedItemPos(Enums.PLAYLIST_DOWNLOADS_SCREEN_POS);
				if (mainActivity.download_adapter != null) {
					if (mainActivity.download_adapter.getCount() > 0) {
						tv_loading.setVisibility(View.GONE);
						list.setAdapter(mainActivity.download_adapter);
						mainActivity.download_adapter.notifyDataSetChanged();
					} else {
						tv_loading.setVisibility(View.VISIBLE);
						tv_loading.setText("no downloads");
					}
				}
				break;
			case ListFragment.SEARCH_RESULTS_DOWNLOADS:
				list.setNumColumns(1);
				// mainActivity.dAdapter.setSelectedItemPos(-1);
				if (link_adapter == null) {
					pb_loading.setVisibility(View.VISIBLE);
					//// TODO: 2/14/2018 s4 fix this
//				searchForUrls();
				} else {
					list.setAdapter(link_adapter);
					link_adapter.notifyDataSetChanged();
				}
				break;
			case ListFragment.SONGS:
				list.setNumColumns(1);
				// mainActivity.dAdapter.setSelectedItemPos(-1);
				if (song_adapter == null) {
					pb_loading.setVisibility(View.VISIBLE);
					search();
				} else {
					list.setAdapter(song_adapter);
					song_adapter.notifyDataSetChanged();
				}
				break;
			case ListFragment.RECOMMENDED:
				withSimilarity = true;
				withAlbum = false;
				mainActivity.am_i_in_home = isHome;
				// mainActivity.dAdapter.setSelectedItemPos(Enums.RECOMMENDED_SCREEN_POS);
				if (song_cells_adapter == null) {
					pb_loading.setVisibility(View.VISIBLE);
					getRecommendSongs();
				} else {
					list.setAdapter(song_cells_adapter);
					song_cells_adapter.notifyDataSetChanged();
				}
				mainActivity.addObserver(new MainActivity.IObserver() {

					@Override
					public void onTrackAdded(PlayList pl) {


					}

					@Override
					public void onFinished(String response) {


					}

					@Override
					public void onDoActionOnSong() {

						song_cells_adapter = null;
						list.setAdapter(song_cells_adapter);
						pb_loading.setVisibility(View.VISIBLE);
						getRecommendSongs();
					}

					@Override
					public String getId() {

						return String.valueOf(type);
					}

				});
				break;
			case ListFragment.RECOMMENDED_BY_GENRE:
				withSimilarity = true;
				withAlbum = false;
				// mainActivity.dAdapter.setSelectedItemPos(Enums.RECOMMENDED_BY_GENRE_SCREEN_POS);
				if (song_cells_adapter == null) {
					pb_loading.setVisibility(View.VISIBLE);
					// // TODO: 2/14/2018 s4 fix this
//				getRecommendByGenre();
				} else {
					list.setAdapter(song_cells_adapter);
					song_cells_adapter.notifyDataSetChanged();
				}
				break;
			case ListFragment.TOP_GENRE_SONGS:
				withSimilarity = false;
				withAlbum = false;

				// mainActivity.dAdapter.setSelectedItemPos(-1);
				if (song_cells_adapter == null) {
					loadSongCells();
				} else {
					list.setAdapter(song_cells_adapter);
					song_cells_adapter.notifyDataSetChanged();
				}
				break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		// Log.i(TAG, "onConfigurationChanged:"+type) ;
		/*
		 * if (type == ListFragment.RECOMMENDED || type ==
		 * ListFragment.RECOMMENDED_BY_GENRE){
		 * 
		 * mainActivity.getSupportFragmentManager() .beginTransaction()
		 * .replace(R.id.content,
		 * PagerFragment.newInstance(PagerFragment.PAGER_RECOMMENDATIONS
		 * ,(type==ListFragment.RECOMMENDED?0:1)),ListFragment.TAG)
		 * .commitAllowingStateLoss();
		 * 
		 * mainActivity.getSupportFragmentManager()
		 * .beginTransaction().detach(this
		 * ).attach(this).commitAllowingStateLoss();
		 * 
		 * }else if (type == ListFragment.TOP_GENRE_SONGS) {
		 * mainActivity.getSupportFragmentManager() .beginTransaction()
		 * .replace(R.id.content, ListFragment.newInstance(type,
		 * this.topGenreSongs),ListFragment.TAG) .commitAllowingStateLoss();
		 * mainActivity.getSupportFragmentManager()
		 * .beginTransaction().detach(this).attach(this).commit(); }
		 */
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		// remove observer when recommended and playlists screen destroyed
		if (type == ListFragment.RECOMMENDED || type == ListFragment.PLAY_LISTS) {
			mainActivity.removeObserver(String.valueOf(type));
		}
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.btn_next_tinder:
			int songsCountInEacGenre = Enums.ITEMS_IN_PAGE
					/ mainActivity.home_page_choosed_top_genre.size();
			List<Song> songs = new ArrayList<Song>();
			for (TopTagSongsResponse tts : mainActivity.home_page_choosed_top_genre) {
				if (tts.songs != null
						&& tts.songs.size() > songsCountInEacGenre) {
					songs.addAll(tts.songs.subList(0, songsCountInEacGenre));
				}
			}
			Collections.shuffle(songs);
			mainActivity.gotoFragment(SongsTinderFragment.newInstance(songs),
					SongsTinderFragment.TAG, true);// is child
			break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		// pb_loading_page.setVisibility(View.GONE);

		// Log.i("firstVisibleItem", firstVisibleItem+"");
		// Log.i("visibleItemCount", visibleItemCount+"");
		// Log.i("totalItemCount", totalItemCount+"");

		if (((song_cells_adapter != null && song_cells_adapter.getCount() < (data != null ? data.size()
				: mArtistSongsResponse.total_count)) || song_adapter != null
				&& song_adapter.getCount() < searchResults.count)
				&& firstTime == false) {
			final int lastItem = firstVisibleItem + visibleItemCount;
			if (totalItemCount != 0 && lastItem == totalItemCount) {

				// load more items
				Log.i("load more items", totalItemCount + "");
				pb_loading_mor.setVisibility(View.VISIBLE);

				Log.i("page number", pageNumber + "");
				if (type == ListFragment.RECOMMENDED) {
					pageNumber = (song_cells_adapter.getCount() / Enums.ITEMS_IN_PAGE) + 1;
					getRecommendSongs();
				} else if (type == ListFragment.RECOMMENDED_BY_GENRE) {
					pageNumber = (song_cells_adapter.getCount() / Enums.ITEMS_IN_PAGE) + 1;
					// TODO: 2/14/2018 s4 fix this
//					getRecommendByGenre();
				} else if (type == ListFragment.ARTIST_SONGS) {
					pageNumber = (song_cells_adapter.getCount() / Enums.ITEMS_IN_PAGE) + 1;
					getArtistSongs();
				} else if (type == ListFragment.SONGS) {
					pageNumber++;
					search();
				}
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
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {

		if (type == ListFragment.RECOMMENDED
				|| type == ListFragment.RECOMMENDED_BY_GENRE
				|| type == ListFragment.ARTIST_SONGS
				|| type == ListFragment.TOP_GENRE_SONGS) {
			Song selectedSong = (Song) adapter.getItemAtPosition(pos);
			if (selectedSong != null) {
				mainActivity.gotoFragment(
						SongDetailsFragment.newInstance(selectedSong),
						SongDetailsFragment.TAG, true);// is child
			}
		} else if (type == ListFragment.PLAY_LISTS) {
			PlayList selectedPL = (PlayList) adapter.getItemAtPosition(pos);
			if (selectedPL != null) {
				mainActivity.gotoFragment(PlayListFragment.newInstance(
						PlayListFragment.USER_DEFINE, selectedPL),
						PlayListFragment.TAG, true);// is child
			} else {
				toast("no songs in this playlist");
			}
		} else if (type == ListFragment.SEARCH_RESULTS_DOWNLOADS) {
			DownloadLink selectedLink = (DownloadLink) adapter
					.getItemAtPosition(pos);
			if (selectedLink != null && selectedLink.url != null
					&& !selectedLink.url.equals("")) {
				mainActivity.playURL(selectedLink.url);
			} else {
				toast("no url found");
			}
		} else if (type == ListFragment.SONGS) {
//			Song selectedSong = (Song) adapter.getItemAtPosition(pos);
//			if (selectedSong != null) {
//				mainActivity.getMp3Urls(selectedSong, null);
//			}
		} else if (type == ListFragment.DOWNLOADS_MANAGER) {
			HashMap<Integer, String> selectedLink = (HashMap<Integer, String>) mainActivity.download_adapter.getItem(pos);
			if (selectedLink != null
					&& selectedLink.get(DownloadsAdatper.KEY_URL) != null
					&& !selectedLink.get(DownloadsAdatper.KEY_URL).equals("")) {

				Song downloadedSong = db.isSongExistIn(selectedLink.get(DownloadsAdatper.KEY_URL), "");
				if (downloadedSong != null
						&& (downloadedSong.whichPlayList == Enums.PLAYLIST_DOWNLOAD_LIST || downloadedSong.whichPlayList == Enums.PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST)
//						&& downloadedSong.link.download_state != null
//						&& downloadedSong.link.download_state == MyIntents.Types.COMPLETE
						) {
//					if (downloadedSong.Artist != null
//							&& !downloadedSong.Artist.equals("")) {
//						if (downloadedSong.link.downloaded_path != null) {
//							File songFile = new File(
//									downloadedSong.link.downloaded_path);
//							if (songFile.isFile()) {
//								downloadedSong.url = downloadedSong.link.downloaded_path;
//							} else {
//								downloadedSong.url = downloadedSong.link.url;
//							}
//						} else {
//							downloadedSong.url = downloadedSong.link.url;
//						}
//						mainActivity.playSong(downloadedSong);
//					} else {
//						mainActivity.playURL(downloadedSong.link, false);
//					}

				} else {
					toast("can't play");
				}

			} else {
				toast("no song found");
			}
		} else if (type == ListFragment.RECENTLY_PLAYED) {
			Song selectedSong = (Song) adapter.getItemAtPosition(pos);
//			if (selectedSong != null) {
//				mainActivity.getMp3Urls(selectedSong, null);
//				// mainActivity.gotoFragment(SongDetailsFragment.newInstance(selectedSong),
//				// SongDetailsFragment.TAG);
//			}
		} else if (type == ListFragment.USER_FAV_GENRES) {
			TopTagSongsResponse selectedTopGenre = (TopTagSongsResponse) adapter
					.getItemAtPosition(pos);
			if (selectedTopGenre != null) {
				if (mainActivity.home_page_choosed_top_genre == null) {
					mainActivity.home_page_choosed_top_genre = new ArrayList<TopTagSongsResponse>();
				}
				if (genre_adapter.setSelectedItem(pos)) {
					mainActivity.home_page_choosed_top_genre
							.add(selectedTopGenre);
				} else {
					mainActivity.home_page_choosed_top_genre
							.remove(selectedTopGenre);
				}
				if (mainActivity.home_page_choosed_top_genre.size() > 0) {
					btn_goto_tinder.setVisibility(View.VISIBLE);
				} else {
					btn_goto_tinder.setVisibility(View.GONE);
				}
			}
		}
	}

	public class callBackHandlre implements NewApiCalls.Callback {

		@Override
		public void onFinished(String response) {
			pb_loading.setVisibility(View.GONE);
			ZdParser parser = new ZdParser(response);
			if (parser.code == 200) {
				data = array2List(gson.fromJson(parser.response,
						Song[].class));
				if (data != null && data.size() > 0) {
					if (type == ListFragment.RECOMMENDED) {
						mainActivity.home_page_recommended_songs = data;
					}
					loadSongCells();
				} else {
					tv_loading.setVisibility(View.VISIBLE);
					tv_loading.setText("No Songs found");
				}
			} else {
				toast(parser.response);
			}

		}
	}

//	public class callBackHanfdlre  {
//
//		public callBackHandlre(MainActivity mainActivity) {
//			super(mainActivity);
//			// TODO Auto-generated constructor stub
//		}
//
//		@Override
//		public void onFinished(String result) {
//
//			try {
//			} catch (Exception e) {
//				// TODO: handle exception
//				tv_loading.setVisibility(View.VISIBLE);
//				tv_loading.setText("Error");
//				e.printStackTrace();
//			}
//		}
//	}
}