package mp3ready.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mp3ready.api.NewApiCalls;
import mp3ready.entities.Song;
import mp3ready.enums.Enums;
import mp3ready.response.RecommendedResponse;
import mp3ready.response.TopTagSongsResponse;
import mp3ready.serializer.ZdParser;
import mp3ready.util.Utilities;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.gson.reflect.TypeToken;
import com.zgm.mp3ready.R;

/**
 * render sections of songs as following: - first section is recommended songs
 * that will updated each time the user like/dislike/rate any song - the rest of
 * sections are top genre songs
 * 
 * @author mhmd
 * 
 */
public class HomeFragmentx extends Zfragment implements OnClickListener {

	public static final String TAG = HomeFragmentx.class.getName();
	private LinearLayout toptag_sections_container;
	private LinearLayout recommended_sections_container;
	private ProgressBar pb_recommend_loading;
	private ProgressBar pb_topgener_loading;
	private ScrollView scroll_view;
	private RelativeLayout recommend_container_parent;
	private RelativeLayout section_container_parent;

	public static HomeFragmentx newInstance() {
		HomeFragmentx f = new HomeFragmentx();

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.frag_home, container, false);

		return rootView;
	}

	@Override
	public void onStart() {
		
		super.onStart();
		// mainActivity.dAdapter.setSelectedItemPos(Enums.HOME_SCREEN_POS);
		mainActivity.currentFrag = HomeFragmentx.TAG;
		mainActivity.am_i_in_home = true;

	}

	/**
	 * initailize the views of this fragment
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		toptag_sections_container = (LinearLayout) getView().findViewById(
				R.id.sections_container);
		recommended_sections_container = (LinearLayout) getView().findViewById(
				R.id.recommend_container);
		pb_recommend_loading = (ProgressBar) getView().findViewById(
				R.id.pb_recommend_loading);
		pb_topgener_loading = (ProgressBar) getView().findViewById(
				R.id.pb_topgenre_loading);
		scroll_view = (ScrollView) getView().findViewById(R.id.scroll_view);
		scroll_view.setMinimumHeight(Utilities
				.getScreenSizePixels(mainActivity)[1]);
		recommend_container_parent = (RelativeLayout) getView().findViewById(
				R.id.recommend_container_parent);
		section_container_parent = (RelativeLayout) getView().findViewById(
				R.id.sections_container_parent);
		if (mainActivity.mem.isAppAuthed()) {
			recommend_container_parent.setMinimumHeight(150);
			section_container_parent.setMinimumHeight(500);
		} else {
			recommend_container_parent.setMinimumHeight(0);
			section_container_parent.setMinimumHeight(Utilities
					.getScreenSizePixels(mainActivity)[1]);
		}
		prepareHomeScreen();
	}

	@Override
	public void onResume() {
		
		super.onResume();

	}

	public boolean isTablet() {
		return getResources().getBoolean(R.bool.isTablet);
	}

	private void loadRecommendedSongs() {
		recommended_sections_container.removeAllViews();
		recommended_sections_container.invalidate();
		recommended_sections_container.refreshDrawableState();
		ViewGroup recommended_section = (ViewGroup) LayoutInflater.from(
				mainActivity).inflate(R.layout.genre_section, null);
		renderRecommendedSection(mainActivity.home_page_recommended_songs,
				"RECOMMENDED", recommended_section, false,
				new OnNORecommendedSongsListener() {

					@Override
					public void onNORecommendedSongs() {
						
						recommend_container_parent.setVisibility(View.GONE);
						recommend_container_parent.setMinimumHeight(0);
					}
				});
		recommended_sections_container.addView(recommended_section);
	}

	private void getRecommendedSongs() {
		recommended_sections_container.removeAllViews();
		recommended_sections_container.invalidate();
		recommended_sections_container.refreshDrawableState();
		String json = "{\"page\":\"1\"}";
		apiCalls.getRecommendedSongs(json, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				pb_recommend_loading.setVisibility(View.GONE);

				if (getView() != null && getActivity() != null) {
					ZdParser parser = new ZdParser(response);
					if (parser.code == 200) {
						mainActivity.home_page_recommended_songs = array2List(gson
								.fromJson(parser.response,
										Song[].class));
						if (mainActivity.home_page_recommended_songs != null
								&& mainActivity.home_page_recommended_songs != null
								&& mainActivity.home_page_recommended_songs.size() > 0) {



							loadRecommendedSongs();

						} else {
							recommend_container_parent
									.setVisibility(View.GONE);
							recommend_container_parent.setMinimumHeight(0);
						}
					} else {
						toast(parser.response);
					}
				}
			}
		});
//		ApiCalls.getRecommendedSongs(json, new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//
//
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//
//					pb_recommend_loading.setVisibility(View.GONE);
//
//				}
//			}
//		});
	}

	private void loadTopGenreSongs() {
		// List<ViewGroup> toptag_sections= new LinkedList<ViewGroup>();
		for (final TopTagSongsResponse tts : (mainActivity.home_page_choosed_top_genre != null
				&& mainActivity.home_page_choosed_top_genre.size() > 0 ? mainActivity.home_page_choosed_top_genre
				: mainActivity.home_page_top_genre_songs)) {

			if (tts.songs != null && tts.songs.size() > 0) {
				Log.i("HomeFragment", "render:" + tts.tag_name);
				ViewGroup toptag_section = (ViewGroup) LayoutInflater.from(
						mainActivity).inflate(R.layout.genre_section, null);
				List<Song> parts = null;
				if (tts.songs.size() > Enums.SONGS_IN_SECTION) {
					parts = tts.songs.subList(0, Enums.SONGS_IN_SECTION);
				} else {
					parts = tts.songs;
				}

				renderSection(parts, tts.tag_name.toUpperCase(),
						toptag_section, false, true, false,
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								
								mainActivity.gotoFragment(ListFragment
										.newInstance(
												ListFragment.TOP_GENRE_SONGS,
												tts.songs), ListFragment.TAG,
										true);// is child
							}
						});
				// toptag_sections.add(toptag_section);
				toptag_sections_container.addView(toptag_section);
			}
		}
		// return toptag_sections;
	}

	private void getTopTagsSongs() {
		toptag_sections_container.removeAllViews();
		toptag_sections_container.invalidate();
		toptag_sections_container.refreshDrawableState();
		apiCalls.getTopTagsSongs("", new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				pb_topgener_loading.setVisibility(View.GONE);
				if (getView() != null && getActivity() != null) {

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
						} else {

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
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//
//					pb_topgener_loading.setVisibility(View.GONE);
//				}
//			}
//		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);

	}

	/**
	 * - render the sections of top genre and section of recommend songs from
	 * cach otherwise from server - if user defined which genre he want , then
	 * we should to render just them
	 */
	private void prepareHomeScreen() {
		if (toptag_sections_container.getChildCount() <= 0) {
			if ((mainActivity.home_page_choosed_top_genre != null && mainActivity.home_page_choosed_top_genre
					.size() > 0)
					|| (mainActivity.home_page_top_genre_songs != null)) {
				pb_topgener_loading.setVisibility(View.GONE);
				loadTopGenreSongs();
			} else {
				pb_topgener_loading.setVisibility(View.VISIBLE);
				getTopTagsSongs();
			}
		}
		if (mainActivity.mem.isAppAuthed()) {
			if (recommended_sections_container.getChildCount() <= 0) {
				if (mainActivity.home_page_recommended_songs != null) {
					pb_recommend_loading.setVisibility(View.GONE);
					loadRecommendedSongs();
				} else {
					pb_recommend_loading.setVisibility(View.VISIBLE);
					getRecommendedSongs();
				}
			}
		} else {
			pb_recommend_loading.setVisibility(View.GONE);
			recommend_container_parent.setMinimumHeight(0);
		}

		return;

	}

	@Override
	public void onClick(View arg0) {

	}

	/**
	 * send like/dislike to server on given song and disable/enable given view
	 */
	@Override
	protected void setLikeState(String song_id, final int like_State,
			final View v) {

		apiCalls.doAction(apiCalls.buildActionLikeStateJson(song_id, like_State), new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					getRecommendedSongs();
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

}