package mp3ready.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mp3ready.api.NewApiCalls;
import mp3ready.response.TopTagSongsResponse;
import mp3ready.serializer.ZdParser;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.google.gson.reflect.TypeToken;
import com.zgm.mp3ready.R;

public class HomeFragment extends Zfragment implements OnClickListener,
		OnPageChangeListener {

	public static final String TAG = HomeFragment.class.getName();
	private PagerSlidingTabStrip home_tabs;
	private ViewPager vpHomePager;
	private ProgressBar pb_loading;
	private int currentItem = 0;
	private SectionPagerAdapter sectionsAdapter;

	public static HomeFragment newInstance() {
		HomeFragment f = new HomeFragment();

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.home_layout, container, false);

		return rootView;
	}

	@Override
	public void onStart() {
		
		super.onStart();
		// mainActivity.dAdapter.setSelectedItemPos(Enums.HOME_SCREEN_POS);
		mainActivity.currentFrag = HomeFragment.TAG;
		mainActivity.am_i_in_home = true;

	}

	/**
	 * initailize the views of this fragment
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		home_tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
		vpHomePager = (ViewPager) getView().findViewById(R.id.home_pager);
		vpHomePager.setOnPageChangeListener(this);
		pb_loading = (ProgressBar) getView().findViewById(R.id.pb_loading);
		vpHomePager.setClipToPadding(false);
		vpHomePager.setPadding(20, 20, 20, 20);
		vpHomePager.setPageMargin(10);
		vpHomePager.setCurrentItem(this.currentItem);
		prepareHomeScreen();
	}

	@Override
	public void onResume() {
		
		super.onResume();

	}

	public boolean isTablet() {
		return getResources().getBoolean(R.bool.isTablet);
	}

	private void loadTopGenreSongs() {
		sectionsAdapter = new SectionPagerAdapter(
				mainActivity.home_page_choosed_top_genre != null
						&& mainActivity.home_page_choosed_top_genre.size() > 0 ? mainActivity.home_page_choosed_top_genre
						: mainActivity.home_page_top_genre_songs,
				mainActivity.mem.isAppAuthed(), getChildFragmentManager());
		vpHomePager.setAdapter(sectionsAdapter);
		home_tabs.setViewPager(vpHomePager);

	}

	private void getTopTagsSongs() {

		apiCalls.getTopTagsSongs("", new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				pb_loading.setVisibility(View.GONE);
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
//				}
//			}
//		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		mainActivity.getSupportFragmentManager().beginTransaction()
				.detach(this).attach(this).commit();

	}

	/**
	 * - render the sections of top genre and section of recommend songs from
	 * cach otherwise from server - if user defined which genre he want , then
	 * we should to render just them
	 */
	private void prepareHomeScreen() {

		pb_loading.setVisibility(View.VISIBLE);
		if ((mainActivity.home_page_choosed_top_genre != null && mainActivity.home_page_choosed_top_genre
				.size() > 0)
				|| (mainActivity.home_page_top_genre_songs != null)) {
			pb_loading.setVisibility(View.GONE);
			loadTopGenreSongs();
		} else {
			getTopTagsSongs();
		}

		return;

	}

	@Override
	public void onPause() {
		
		super.onPause();
		this.currentItem = vpHomePager.getCurrentItem();
	}

	@Override
	public void onClick(View arg0) {

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		

	}

	@Override
	public void onPageSelected(int pos) {
		this.currentItem = pos;
		/*
		 * if (mainActivity.mem.isAppAuthed()){ if (this.currentItem == 0){
		 * tv_section_title.setText("RECOMMENDED"); }else {
		 * tv_section_title.setText
		 * (mainActivity.home_page_choosed_top_genre!=null
		 * &&mainActivity.home_page_choosed_top_genre
		 * .size()>0?mainActivity.home_page_choosed_top_genre
		 * .get(pos-1).tag_name
		 * .toUpperCase():mainActivity.home_page_top_genre_songs
		 * .get(pos-1).tag_name.toUpperCase()); } }else {
		 * tv_section_title.setText
		 * (mainActivity.home_page_choosed_top_genre!=null
		 * &&mainActivity.home_page_choosed_top_genre
		 * .size()>0?mainActivity.home_page_choosed_top_genre
		 * .get(pos).tag_name.toUpperCase
		 * ():mainActivity.home_page_top_genre_songs
		 * .get(pos).tag_name.toUpperCase()); }
		 */
	}

	public class SectionPagerAdapter extends FragmentStatePagerAdapter {

		public List<TopTagSongsResponse> top_genre_songs;
		public boolean isThereRecommendedSongs = false;

		public SectionPagerAdapter(List<TopTagSongsResponse> top_genre_songs,
				boolean isThereRecommendedSongs,
				android.support.v4.app.FragmentManager fm) {
			super(fm);
			this.top_genre_songs = top_genre_songs;
			this.isThereRecommendedSongs = isThereRecommendedSongs;

		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			
			// super.restoreState(arg0, arg1);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			
			if (isThereRecommendedSongs) {
				if (position == 0) {
					return "RECOMMENDED";
				} else {
					return this.top_genre_songs.get(position - 1).tag_name;
				}
			} else {
				return this.top_genre_songs.get(position).tag_name;
			}
		}

		@Override
		public int getCount() {
			if (isThereRecommendedSongs) {
				return top_genre_songs.size() + 1;
			} else {
				return top_genre_songs.size();
			}

		}

		@Override
		public Fragment getItem(int position) {
			if (isThereRecommendedSongs) {
				if (position == 0) {
					return ListFragment.newInstance(ListFragment.RECOMMENDED,
							true);
				} else {
					return ListFragment.newInstance(
							ListFragment.TOP_GENRE_SONGS,
							this.top_genre_songs.get(position - 1).songs);
				}
			} else {
				return ListFragment.newInstance(ListFragment.TOP_GENRE_SONGS,
						this.top_genre_songs.get(position).songs);
			}
		}

	}
}