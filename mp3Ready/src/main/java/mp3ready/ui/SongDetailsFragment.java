package mp3ready.ui;

import mp3ready.adapters.SongCellsAdatper;
import mp3ready.api.NewApiCalls;
import mp3ready.entities.Song;
import mp3ready.entities.Tag;
import mp3ready.enums.Enums;
import mp3ready.serializer.ZdParser;
import mp3ready.util.Utilities;
import mp3ready.views.TagCloudLinkView;
import mp3ready.views.TagCloudLinkView.OnTagSelectListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zgm.mp3ready.R;

/**
 * <p>
 * All song's information and similar songs
 * </p>
 * 
 * @author mhmd
 * 
 */

public class SongDetailsFragment extends Zfragment implements OnClickListener,
		OnTagSelectListener, OnItemClickListener {

	public final static String TAG = SongDetailsFragment.class.getName();
	private final int SMALL_tAGS_COUNT = 3;
	private ImageView iv_song_cover;
	private TextView tv_song_title;
	private TextView tv_song_artist;
	private TextView tv_song_duration;
	private TextView tv_song_listeners;
	private TextView tv_song_album;
	private TagCloudLinkView tagView;
	private LinearLayout similar_section;
	private ImageView action_like;
	private ImageView action_dislike;
	private ImageView action_play;
	private Song song;

	public static SongDetailsFragment newInstance(Song song) {
		SongDetailsFragment efrag = new SongDetailsFragment();
		efrag.song = song;
		return efrag;
	}

	/**
	 * <p>
	 * inflatrate the views from xml
	 * </p>
	 */
	private void initView() {
		iv_song_cover = (ImageView) getView().findViewById(R.id.song_cover);
		tv_song_title = (TextView) getView().findViewById(R.id.tv_song_title);
		tv_song_artist = (TextView) getView().findViewById(R.id.tv_song_artist);
		tv_song_duration = (TextView) getView().findViewById(
				R.id.tv_song_duration);
		tv_song_listeners = (TextView) getView().findViewById(
				R.id.tv_song_listeners);
		tv_song_album = (TextView) getView().findViewById(R.id.tv_song_album);
		action_like = (ImageView) getView().findViewById(R.id.action_like);
		action_like.setOnClickListener(this);
		action_dislike = (ImageView) getView()
				.findViewById(R.id.action_dislike);
		action_dislike.setOnClickListener(this);
		if (mainActivity.mem.isAppAuthed()) {
			action_dislike.setVisibility(View.VISIBLE);
			action_like.setVisibility(View.VISIBLE);
		} else {
			action_dislike.setVisibility(View.INVISIBLE);
			action_like.setVisibility(View.INVISIBLE);
		}
		action_play = (ImageView) getView().findViewById(R.id.action_play);
		action_play.setOnClickListener(this);
		tagView = (TagCloudLinkView) getView().findViewById(R.id.song_tags);
		/* LinearLayout.LayoutParams lp = new
		 LinearLayout.LayoutParams(Utilities.getScreenSizePixels(mainActivity)[0],
	 LinearLayout.LayoutParams.WRAP_CONTENT);
		 tagView.setLayoutParams(lp);*/
		//tagView.setMinimumWidth(Utilities.getScreenSizePixels(mainActivity)[0]);
		

		similar_section = (LinearLayout) getView().findViewById(
				R.id.similar_section);
		if (song.songcover != null && !song.songcover.equals("")) {
			mainActivity.mPicasso.load(song.songcover).fit()
					.into(iv_song_cover);
		} else {
			iv_song_cover.setImageDrawable(mainActivity.getResources()
					.getDrawable(R.drawable.default_song_cover));
		}
		tv_song_title.setText(song.SName);
		tv_song_artist.setText(song.Artist);
		tv_song_artist.setTag(song);
		tv_song_artist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Song s = (Song) v.getTag();
				((TextView) v).setTextColor(mainActivity.getResources()
						.getColor(R.color.dark_blue));
				mainActivity.gotoFragment(
						ArtistScreenFragment.newInstance(s.ArtistID),
						ArtistScreenFragment.TAG, true);// is child
			}
		});
		tv_song_duration.setText(song.duration);
		tv_song_listeners.setText(song.listeners);
		tv_song_album.setText(song.album);
	}

	@Override
	public void onPause() {
		
		super.onPause();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.song_details, container, false);
		return view;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	/**
	 * <p>
	 * render all song tags useing {@link TagCloudLinkView}
	 * </p>
	 * <p>
	 * set alphe for each tag by apply this equation
	 * "alpha = 0.6 + tagWeight*0.4/100"
	 * </p>
	 */
	private void renderAllTags() {
		tagView.removeAllViews();
		tagView.invalidate();
		tagView.refreshDrawableState();
		tagView.getTags().clear();
//		if (song.tags != null && !song.tags.equals("")) {
//			String[] tags = song.tags.split(",");
//			for (int i = 0; i < tags.length - 1; i++) {
//				int twoDotsLastIndx = tags[i].lastIndexOf(":");
//				String tag = tags[i].substring(0, twoDotsLastIndx);
//				Float weight = Float.valueOf(tags[i]
//						.substring(twoDotsLastIndx + 1));
//				Double alpha = (0.6 + ((weight * 0.4) / 100));
//				tagView.add(new Tag(i, tag, alpha));
//			}
//			tagView.add(new Tag(tags.length - 1, "Less", 1.0));
//			tagView.drawTags();
//		}
	}

	/**
	 * <p>
	 * render first {@link #SMALL_tAGS_COUNT} song tags using
	 * {@link TagCloudLinkView}
	 * </p>
	 * <p>
	 * set alphe for each tag by apply this equation
	 * "alpha = 0.6 + tagWeight*0.4/100"
	 * </p>
	 */
	private void renderPartOfTags() {
		tagView.removeAllViews();
		tagView.invalidate();
		tagView.refreshDrawableState();
		tagView.getTags().clear();
//		if (song.tags != null && !song.tags.equals("")) {
//			String[] tags = song.tags.split(",");
//			for (int i = 0; i < SMALL_tAGS_COUNT; i++) {
//				int twoDotsLastIndx = tags[i].lastIndexOf(":");
//				String tag = tags[i].substring(0, twoDotsLastIndx);
//				Float weight = Float.valueOf(tags[i]
//						.substring(twoDotsLastIndx + 1));
//				Double alpha = (0.6 + ((weight * 0.4) / 100));
//				tagView.add(new Tag(i, tag, alpha));
//			}
//			tagView.add(new Tag(SMALL_tAGS_COUNT, "More", 1.0));
//			tagView.drawTags();
//		}
	}

	/**
	 * <p>
	 * render the song's information on views and loading the similar songs if
	 * exist
	 * </p>
	 */
	private void loadRestofDetails() {
		tv_song_title.setText(song.SName);
		tv_song_artist.setText(song.Artist);
		tv_song_duration.setText(song.duration);
		tv_song_listeners.setText(song.listeners);
		tv_song_album.setText(song.album);
		/* LinearLayout.LayoutParams lp = new
				 LinearLayout.LayoutParams(Utilities.getScreenSizePixels(mainActivity)[0],
			 LinearLayout.LayoutParams.WRAP_CONTENT);
				 tagView.setLayoutParams(lp);*/
		//tagView.setMinimumWidth(Utilities.getScreenSizePixels(mainActivity)[0]);
//		if (song.tags != null && !song.tags.equals("")) {
//			String[] tags = song.tags.split(",");
//			if (tags.length > SMALL_tAGS_COUNT + 3) {
//				tagView.setOnTagSelectListener(this);
//				//renderPartOfTags();
//				renderAllTags();
//			} else {
//				for (int i = 0; i < tags.length - 1; i++) {
//					int twoDotsLastIndx = tags[i].lastIndexOf(":");
//					String tag = tags[i].substring(0, twoDotsLastIndx);
//					Float weight = Float.valueOf(tags[i]
//							.substring(twoDotsLastIndx + 1));
//					Double alpha = (0.6 + ((weight * 0.4) / 100));
//					tagView.add(new Tag(i, tag, alpha));
//				}
//				tagView.drawTags();
//			}

		}

//		if (song.similars != null && song.similars.size() > 0) {
//			View section = LayoutInflater.from(mainActivity).inflate(
//					R.layout.similars_section, null);
//			GridView mGridView = (GridView) section
//					.findViewById(R.id.songs_listview);
//			SongCellsAdatper adapter = new SongCellsAdatper(mainActivity, true,
//					false) {
//
//				@Override
//				protected void artistPage(Song song) {
//
//					mainActivity.gotoFragment(
//							ArtistScreenFragment.newInstance(song.ArtistID),
//							ArtistScreenFragment.TAG, true);// is child
//				}
//
//				@Override
//				protected void playSong(Song song) {
//
//					if (getActivity() != null && mainActivity != null) {
//						// mainActivity.getMp3Urls(song);
////						boolean insert = mainActivity.db
////								.addSongToPlayList(
////										song.SID,
////										song.duration,
////										song.listeners,
////										song.songcover,
////										song.likeState,
////										song.rate,
////										song.url,
////										song.SName,
////										song.Artist,
////										(song.link != null
////												&& song.link.bitrate != null ? song.link.bitrate
////												: "0"),
////										Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
////						if (insert) {
////							Log.e("ZFragment",
////									"song inserted to recently playlist");
////							toast("song added to Recently Played List");
////						} else {
////							toast("song is already exist in Recently Played List");
////						}
//						mainActivity.playSongIfNoCurrentSongExist(song);
//					}
//				}
//
//				@Override
//				protected void songDetails(Song song) {
//
//					mainActivity.gotoFragment(
//							SongDetailsFragment.newInstance(song),
//							SongDetailsFragment.TAG, true);// is child
//				}
//
//				@Override
//				protected void setLikeState(String song_id,
//						final int like_State, final ImageView v) {
//					apiCalls.doAction(apiCalls.buildActionLikeStateJson(song_id, like_State), new NewApiCalls.Callback() {
//						@Override
//						public void onFinished(String response) {
//							toast("set like state");
//							v.setEnabled(false);
//							if (like_State == Enums.LIKE_STATE) {
//								v.setImageDrawable(mainActivity
//										.getResources()
//										.getDrawable(
//												R.drawable.love_circle_onboarding_active));
//							} else if (like_State == Enums.DISLIKE_STATE) {
//								v.setImageDrawable(mainActivity
//										.getResources()
//										.getDrawable(
//												R.drawable.unlove_circle_onboarding_active));
//
//							}
//
//						}
//					});
////					ApiCalls.doAction(ApiCalls.buildActionLikeStateJson(
////							song_id, like_State), new CallbackHandler(
////							mainActivity) {
////
////						@Override
////						public void onFinished(String result) {
////
////						}
////					});
//				}
//			};
//			adapter.loadMoreSongs(song.similars);
//			mGridView.setAdapter(adapter);
//			mGridView.setOnItemClickListener(this);
//			adapter.notifyDataSetChanged();
//			this.similar_section.addView(section);
//		}
//	}

	private void getDetails() {
		String json = "{\"song_id\":\"" + this.song.SID + "\"}";
		apiCalls.getSongDetials(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					song = gson.fromJson(parser.response, Song.class);
					if (song != null) {
						loadRestofDetails();
					}
				} else {
					toast(parser.response);
				}

			}
		});
//		ApiCalls.getSongDetials(json, new CallbackHandler(mainActivity) {
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

	@Override
	public void onStart() {
		
		super.onStart();
		initView();
		mainActivity.currentFrag = SongDetailsFragment.TAG;
		// mainActivity.dAdapter.setSelectedItemPos(-1);
		if (tagView.getTags().size() <= 0) {
//			if (song.tags != null || song.similars != null) {
//				loadRestofDetails();
//			} else {
				getDetails();
//			}
		}
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);

		/*
		 * mainActivity.getSupportFragmentManager() .beginTransaction()
		 * .replace(R.id.content, SongDetailsFragment.newInstance(song),
		 * SongDetailsFragment.TAG) .commitAllowingStateLoss();
		 */
		mainActivity.getSupportFragmentManager().beginTransaction()
				.detach(this).attach(this).commit();

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.action_like:
			setLikeState(this.song.SID, Enums.LIKE_STATE, action_like);
			break;
		case R.id.action_dislike:
			setLikeState(this.song.SID, Enums.DISLIKE_STATE, action_dislike);
			break;
		case R.id.action_play:
			if (getActivity() != null && mainActivity != null) {
				// mainActivity.getMp3Urls(song);
//				boolean insert = mainActivity.db
//						.addSongToPlayList(
//								song.SID,
//								song.duration,
//								song.listeners,
//								song.songcover,
//								song.likeState,
//								song.rate,
//								song.url,
//								song.SName,
//								song.Artist,
//								(song.link != null && song.link.bitrate != null ? song.link.bitrate
//										: "0"),
//								Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//				if (insert) {
//					Log.e("ZFragment", "song inserted to recently playlist");
//					toast("song added to Recently Played List");
//				} else {
//					toast("song is already exist in Recently Played List");
//				}
				mainActivity.playSongIfNoCurrentSongExist(song);
			}
			break;
		}
	}

	@Override
	public void onTagSelected(Tag tag, int position) {
		
		if (tag.getText().equals("More")) {
			renderAllTags();
		} else if (tag.getText().equals("Less")) {
			renderPartOfTags();
		}
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
					if (like_State == Enums.LIKE_STATE) {
						((ImageView) v)
								.setImageDrawable(mainActivity
										.getResources()
										.getDrawable(
												R.drawable.love_circle_onboarding_active));
					} else if (like_State == Enums.DISLIKE_STATE) {
						((ImageView) v)
								.setImageDrawable(mainActivity
										.getResources()
										.getDrawable(
												R.drawable.unlove_circle_onboarding_active));
					}
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

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
			long arg3) {
		
		Song selectedSong = (Song) adapter.getItemAtPosition(pos);
		if (selectedSong != null) {
			mainActivity.gotoFragment(
					SongDetailsFragment.newInstance(selectedSong),
					SongDetailsFragment.TAG, true);// is child
		}
	}

}