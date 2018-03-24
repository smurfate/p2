package mp3ready.adapters;

import mp3ready.ui.ListFragment;
import mp3ready.ui.PagerFragment;
import mp3ready.ui.PlayListFragment;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * 
 * 
 * @author mhmd
 * 
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

	private String[] pagestitles;
	private String query;
	private int type = 0;

	public PagerAdapter(int type, String[] pagestitles, String query,
			android.support.v4.app.FragmentManager fm) {
		super(fm);

		this.pagestitles = pagestitles;

		this.query = query;
		this.type = type;
	}

	public PagerAdapter(int type, String[] pagestitles,
			android.support.v4.app.FragmentManager fm) {
		super(fm);

		this.pagestitles = pagestitles;
		this.type = type;

	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		
		// super.restoreState(arg0, arg1);
	}

	@Override
	public CharSequence getPageTitle(int position) {

		return pagestitles[position];
	}

	@Override
	public int getCount() {
		return pagestitles.length;
	}

	@Override
	public Fragment getItem(int position) {
		if (type == PagerFragment.PAGER_SEARCH_RESULTS) {
			if (position == 0) {
				return ListFragment.newInstance(ListFragment.SONGS, query);
			} else if (position == 1) {
				return ListFragment.newInstance(ListFragment.SEARCH_RESULTS_DOWNLOADS, query);
			}
		} else if (type == PagerFragment.PAGER_RECOMMENDATIONS) {
			if (position == 0) {
				return ListFragment.newInstance(ListFragment.RECOMMENDED, false);// false
																		// means
																		// is
																		// not
																		// home
			}
			else if (position == 1) {
				return ListFragment.newInstance(
						ListFragment.RECOMMENDED_BY_GENRE, false);// false means
																	// is not
																	// home
			}

		} else if (type == PagerFragment.PAGER_PLAY_LISTS) {
			if (position == 0) {
				return ListFragment.newInstance(ListFragment.DOWNLOADS_MANAGER,
						false);// false means is not home
			} else if (position == 1) {
				return ListFragment.newInstance(ListFragment.RECENTLY_PLAYED,
						false);// false means is not home
			} else if (position == 2) {
				return ListFragment.newInstance(ListFragment.PLAY_LISTS, false);// false
																				// means
																				// is
																				// not
																				// home
			}
			else if (position == 3) {
				return PlayListFragment.newInstance(
						PlayListFragment.FROM_FRIENDS, position);
			} else if (position == 4) {
				return PlayListFragment.newInstance(PlayListFragment.LIKES,
						position);
			} else if (position == 5) {
				return PlayListFragment.newInstance(PlayListFragment.FIVE_STAR,
						position);
			} else if (position == 6) {
				return PlayListFragment.newInstance(PlayListFragment.FOUR_STAR,
						position);
			} else if (position == 7) {
				return PlayListFragment.newInstance(PlayListFragment.HISTORY,
						position);
			}
		}
		return null;
	}

}