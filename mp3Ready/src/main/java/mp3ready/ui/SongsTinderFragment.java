package mp3ready.ui;

import java.util.List;

import mp3ready.adapters.SimpleCardStackAdapter;
import mp3ready.api.NewApiCalls;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.player.services.ObservableOutput;
import mp3ready.player.services.Output;
import mp3ready.player.services.OutputCommand;
import mp3ready.serializer.ZdParser;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations.Orientation;
import com.andtinder.view.CardContainer;
import com.andtinder.view.CardContainer.OnLastCardDismissedListener;
import com.zgm.mp3ready.R;

/**
 * <p>
 * show user tinder cards of songs and let them listen , like and dislike the
 * song card to redefine his profile
 * </p>
 * 
 * @author mhmd
 * 
 */

public class SongsTinderFragment extends Zfragment implements OnClickListener,
		OnLastCardDismissedListener {

	public final static String TAG = SongsTinderFragment.class.getName();

	private CardContainer cards_container;
	private ImageButton ib_like_song;
	private ImageButton ib_dislike_song;
	private SimpleCardStackAdapter adapter;
	private Song playedSong;
	private List<Song> songs;

	public static SongsTinderFragment newInstance(List<Song> songs) {
		SongsTinderFragment efrag = new SongsTinderFragment();
		efrag.songs = songs;
		return efrag;
	}

	/**
	 * <p>
	 * initialize the views and buttons by inflatrate them
	 * </p>
	 */
	private void initView() {
		cards_container = (CardContainer) getView().findViewById(
				R.id.card_container);
		ib_like_song = (ImageButton) getView().findViewById(
				R.id.song_action_like);
		ib_dislike_song = (ImageButton) getView().findViewById(
				R.id.song_action_dislike);
		ib_like_song.setOnClickListener(this);
		ib_dislike_song.setOnClickListener(this);
		cards_container.setOnLastCardDismissedListener(this);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.song_tinder_layout, container,
				false);
		return view;

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		/*
		 * mainActivity.getSupportFragmentManager().beginTransaction()
		 * .detach(this).attach(this).commitAllowingStateLoss();
		 */
	}

	/**
	 * <p>
	 * create songs cards from list of top genre songs to show user the cards
	 * and redefine his profile
	 * </p>
	 * <p>
	 * implementing the listeners to do actions when user like/dislike or
	 * play/pause the song
	 * </p>
	 */
	private void loadTopGenreSongs() {
		for (Song song : songs) {
			if (song == null) {
				continue;
			}
			CardModel model = new CardModel(song);
			// Log.i(TAG, model.getCardId()+":"+model.getTitle());
			model.setOnCardDismissedListener(new CardModel.OnCardDismissedListener() {

				@Override
				public void onTendToLike(View mTopCard) {
					
					// Log.i(TAG,"I tend to like the card");
					mTopCard.findViewById(R.id.song_card_love).setVisibility(
							View.VISIBLE);
					mTopCard.findViewById(R.id.song_card_unlove).setVisibility(
							View.INVISIBLE);
					mTopCard.findViewById(R.id.song_card_play_pause)
							.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onTendToDislike(View mTopCard) {
					
					// Log.i(TAG,"I tend to dislike the card");
					mTopCard.findViewById(R.id.song_card_love).setVisibility(
							View.INVISIBLE);
					mTopCard.findViewById(R.id.song_card_unlove).setVisibility(
							View.VISIBLE);
					mTopCard.findViewById(R.id.song_card_play_pause)
							.setVisibility(View.INVISIBLE);
				}

				@Override
				public void onLike(View mTopCard, CardModel card) {
					
					Log.i(TAG,
							"I like the song:"
									+ ((Song) card.getDefindObject()).SName);
					mTopCard.findViewById(R.id.song_card_love).setVisibility(
							View.VISIBLE);
					mTopCard.findViewById(R.id.song_card_unlove).setVisibility(
							View.INVISIBLE);
					mTopCard.findViewById(R.id.song_card_play_pause)
							.setVisibility(View.INVISIBLE);
					setLikeState(((Song) card.getDefindObject()).SID,
							Enums.LIKE_STATE, ib_like_song);

					songs.remove((Song) card.getDefindObject());
				}

				@Override
				public void onDislike(View mTopCard, CardModel card) {
					
					Log.i(TAG,
							"I dislike the song:"
									+ ((Song) card.getDefindObject()).SName);
					mTopCard.findViewById(R.id.song_card_love).setVisibility(
							View.INVISIBLE);
					mTopCard.findViewById(R.id.song_card_unlove).setVisibility(
							View.VISIBLE);
					mTopCard.findViewById(R.id.song_card_play_pause)
							.setVisibility(View.INVISIBLE);
					setLikeState(((Song) card.getDefindObject()).SID,
							Enums.DISLIKE_STATE, ib_dislike_song);

					songs.remove((Song) card.getDefindObject());
				}

				@Override
				public void onNoTend(View mTopCard) {
					
					// Log.i(TAG,"no tend to the card");
					mTopCard.findViewById(R.id.song_card_love).setVisibility(
							View.INVISIBLE);
					mTopCard.findViewById(R.id.song_card_unlove).setVisibility(
							View.INVISIBLE);
					mTopCard.findViewById(R.id.song_card_play_pause)
							.setVisibility(View.VISIBLE);
				}

				@Override
				public void onCustomEvent(int eventType, View mTopCard) {
					
					switch (eventType) {
					case Enums.CUSTOME_EVENT_PLAY:
						mTopCard.findViewById(R.id.song_card_play_pause)
								.setEnabled(true);
						((ImageView) mTopCard
								.findViewById(R.id.song_card_play_pause))
								.setImageDrawable(mainActivity
										.getResources()
										.getDrawable(
												R.drawable.pause_circle_light_tinder));

						break;
					case Enums.CUSTOME_EVENT_PAUSE:
						mTopCard.findViewById(R.id.song_card_play_pause)
								.setEnabled(true);
						((ImageView) mTopCard
								.findViewById(R.id.song_card_play_pause))
								.setImageDrawable(mainActivity
										.getResources()
										.getDrawable(
												R.drawable.play_circle_light_onboarding_tinder));
						break;
					}
				}

			});
			adapter.add(model);
		}
		cards_container.setOrientation(Orientation.Disordered);
		if (cards_container.getAdapter() == null)
			cards_container.setAdapter(adapter);
	}

	/**
	 * <p>
	 * createing the tinder and load it on screen
	 * </p>
	 */
	private void initTinder() {
		adapter = new SimpleCardStackAdapter(mainActivity) {

			@Override
			protected void playSong(CardModel card) {
				
				Song song = (Song) card.getDefindObject();
				if (playedSong != null && playedSong.SID.equals(song.SID)) {
//					mainActivity.player.connectPlayer(new OutputCommand() {
//
//						@Override
//						public void connected(Output output) {
//
//							output.toggle();
//						}
//					});

				} else {
//					song.isTinderSong = true;
//					playedSong = song;
//					boolean insert = mainActivity.db
//							.addSongToPlayList(
//									song.SID,
//									song.duration,
//									song.listeners,
//									song.songcover,
//									song.likeState,
//									song.rate,
//									song.url,
//									song.SName,
//									song.Artist,
//									(song.link != null
//											&& song.link.bitrate != null ? song.link.bitrate
//											: "0"),
//									Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//					if (insert) {
//						Log.e("ZFragment",
//								"song inserted to recently playlist");
//						toast("song added to Recently Played List");
//					} else {
//						toast("song is already exist in Recently Played List");
//					}
					mainActivity.playURL(song.getSongURL());
				}
			}
		};
		if (mainActivity.home_page_choosed_top_genre != null
				&& mainActivity.home_page_choosed_top_genre.size() > 0) {
			loadTopGenreSongs();
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
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		initView();
		if (adapter == null) {

			initTinder();
		} else {
			if (cards_container.getAdapter() == null)
				cards_container.setAdapter(adapter);
		}
//		mainActivity.player.addObserver(new ObservableOutput.PlayerObserver() {
//
//			@Override
//			public String getId() {
//
//				return "PlayViewComponentsUpdater3";
//			}
//
//			@Override
//			public void trackChanged(Song track, int lengthInMillis) {
//
//				if (((Song) cards_container.getCurrCard().getDefindObject()).SID
//						.equals(track.SID)) {
//					cards_container
//							.triggerCustomeEvent(Enums.CUSTOME_EVENT_PAUSE);
//				}
//			}
//
//			@Override
//			public void stopped() {
//
//				if (((Song) cards_container.getCurrCard().getDefindObject()).SID
//						.equals(playedSong.SID)) {
//					cards_container
//							.triggerCustomeEvent(Enums.CUSTOME_EVENT_PAUSE);
//				}
//			}
//
//			@Override
//			public void started() {
//
//				if (((Song) cards_container.getCurrCard().getDefindObject()).SID
//						.equals(playedSong.SID)) {
//					cards_container
//							.triggerCustomeEvent(Enums.CUSTOME_EVENT_PLAY);
//				}
//			}
//
//			@Override
//			public void TrackHasInvalidUrl(Song track) {
//
//
//			}
//
//			@Override
//			public void onMediaPlayerBufferingonUpdate(int duration, int pos) {
//
//
//			}
//
//			@Override
//			public void onMediaPlayerCompletion(Output output) {
//
//
//			}
//		});
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();

//		mainActivity.player.removeObserver("PlayViewComponentsUpdater3");
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.song_action_like:
			if (cards_container != null
					&& cards_container.getAdapter().getCount() >= 1
					&& !cards_container.isAnimating()) {
				cards_container.like();
			}
			break;
		case R.id.song_action_dislike:
			if (cards_container != null
					&& cards_container.getAdapter().getCount() >= 1
					&& !cards_container.isAnimating()) {
				cards_container.dislike();
			}
			break;

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

					v.setEnabled(true);

					// if (like_State == Enums.LIKE_STATE){
					// ((ImageButton)v).setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.love_circle_onboarding_active));
					// }else if (like_State == Enums.DISLIKE_STATE){
					// ((ImageButton)v).setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.unlove_circle_onboarding_active));
					// }
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
	public void onLastCardDismissed() {
		
		mainActivity.onBackPressed();
	}
}