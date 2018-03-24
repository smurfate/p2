package mp3ready.ui;

import java.util.HashMap;

import mp3ready.adapters.PagerAdapter;
import mp3ready.api.NewApiCalls;
import mp3ready.serializer.ZdParser;
import mp3ready.ui.ListFragment.callBackHandlre;
import mp3ready.util.App;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;
import com.zgm.mp3ready.R;

/**
 * <p>
 * pager for show user the following:
 * </p>
 * <li>pager PAGER_SEARCH_RESULTS</li> <li>pager of playlist as PAGER_PLAY_LISTS
 * </li> <li>pager of recommended songs as PAGER_RECOMMENDATIONS</li>
 * 
 * @author mhmd
 * 
 */
public class PagerFragment extends Zfragment implements OnPageChangeListener {

	public final static String TAG = PagerFragment.class.getName();
	public static final int PAGER_SEARCH_RESULTS = 0;
	public static final int PAGER_PLAY_LISTS = 1;
	public static final int PAGER_RECOMMENDATIONS = 2;

	private PagerAdapter adapter;
	private static ViewPager pager;
	private PagerSlidingTabStrip tabs;
	private int CurrentItem = 0;
	private String query;
	private callBackHandlre callback;
	private int type = 0;
	public static HashMap<Integer, Integer> coversMap = new HashMap<Integer, Integer>();

	public static PagerFragment newInstance(int type, int CurrentItem,
			String query) {
		PagerFragment efrag = new PagerFragment();

		efrag.CurrentItem = CurrentItem;
		efrag.query = query;
		efrag.type = type;
		return efrag;
	}

	public static PagerFragment newInstance(int type, int CurrentItem) {
		PagerFragment efrag = new PagerFragment();

		efrag.CurrentItem = CurrentItem;

		efrag.type = type;
		return efrag;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (type == PAGER_PLAY_LISTS) {
			try {
				int size = (int) Math.ceil(Math.sqrt(App.MAX_IMAGE_WIDTH
						* App.MAX_IMAGE_HEIGHT));

//				coversMap.put(PlayListFragment.FROM_FRIENDS, R.drawable.playlist_friends);
				coversMap.put(PlayListFragment.LIKES, R.drawable.playlist_fav);
//				coversMap.put(PlayListFragment.FIVE_STAR, R.drawable.playlist_5stars);
//				coversMap.put(PlayListFragment.FOUR_STAR, R.drawable.playlist_4stars);
				coversMap.put(PlayListFragment.USER_DEFINE,	R.drawable.grid_default_cover_playlist);
//				coversMap.put(PlayListFragment.HISTORY,	R.drawable.playlist_history);
			} catch (OutOfMemoryError e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.pager, container, false);
		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);

		pager = (ViewPager) getView().findViewById(R.id.pager);
		pager.addOnPageChangeListener(this);
		tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.indicator);

		// int margin =
		// (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20*2,
		// getResources().getDisplayMetrics());
		// pager.setPageMargin(-margin);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		if (type == PagerFragment.PAGER_PLAY_LISTS) {
			mainActivity.getSupportFragmentManager().beginTransaction()
					.detach(this).attach(this).commit();
		}
	}

	@Override
	public void onStart() {

		super.onStart();
		Log.i(TAG, "onStart");

		if (type == PagerFragment.PAGER_SEARCH_RESULTS) {
			String[] titles = { "SONGS", "LINKS" };
			tabs.setShouldExpand(true);
			adapter = new PagerAdapter(this.type, titles, query,
					getChildFragmentManager());
		} else if (type == PagerFragment.PAGER_PLAY_LISTS) {
			if (mainActivity.mem.isAppAuthed()) {
				String[] titles = { "DOWNLOADS", "RECENTLY PLAYED",
						"PLAYLISTS" };
				adapter = new PagerAdapter(this.type, titles,
						getChildFragmentManager());
				mainActivity.mCachedResponse = "";
				tabs.setShouldExpand(false);
//				getPlayLists();
				if (CurrentItem == 0) {
					// mainActivity.dAdapter.setSelectedItemPos(Enums.PLAYLIST_DOWNLOADS_SCREEN_POS);
				} else if (CurrentItem == 1) {
					// mainActivity.dAdapter.setSelectedItemPos(Enums.PLAYLIST_RECENTLY_SCREEN_POS);
				} else if (CurrentItem == 2) {
					// mainActivity.dAdapter.setSelectedItemPos(Enums.PLAYLIST_PLS_SCREEN_POS);
				}
			} else {
				String[] titles = { "DOWNLOADS", "RECENTLY PLAYED" };
				adapter = new PagerAdapter(this.type, titles,
						getChildFragmentManager());
				tabs.setShouldExpand(true);
			}
		} else if (type == PagerFragment.PAGER_RECOMMENDATIONS) {
			String[] titles = { "Recommended" };
			tabs.setShouldExpand(true);
			adapter = new PagerAdapter(this.type, titles,
					getChildFragmentManager());
		}
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		pager.setCurrentItem(CurrentItem);
		adapter.notifyDataSetChanged();

	}

	/**
	 * <p>
	 * getting playlists and then notifiy all playlists observers to render
	 * thier items
	 * </p>
	 */
//	private void getPlayLists() {
//		apiCalls.getPlayLists("", new NewApiCalls.Callback() {
//			@Override
//			public void onFinished(String response) {
//				ZdParser parser = new ZdParser(response);
//				if (parser.code == 200) {
//					Log.i(TAG, parser.response);
//					mainActivity.notifyOnFinished(parser.response);
//				} else {
//					toast(parser.response);
//				}
//
//			}
//		});
////		ApiCalls.getPlayLists("", new CallbackHandler(mainActivity) {
////
////			@Override
////			public void onFinished(String result) {
////
////				try {
////
////				} catch (Exception e) {
////					// TODO: handle exception
////					e.printStackTrace();
////					toast("Error");
////				}
////			}
////		});
//	}

	@Override
	public void onPageSelected(int pos) {

		this.CurrentItem = pos;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {


	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {


	}

}