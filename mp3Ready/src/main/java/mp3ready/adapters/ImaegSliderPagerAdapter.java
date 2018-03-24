package mp3ready.adapters;

import java.util.List;

import mp3ready.ui.PhotoFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;


public class ImaegSliderPagerAdapter extends FragmentStatePagerAdapter {

	private List<String> imgs;

	public ImaegSliderPagerAdapter(List<String> imgs,
			android.support.v4.app.FragmentManager fm) {
		super(fm);

		this.imgs = imgs;

	}

	@Override
	public int getCount() {
		if (this.imgs != null) {
			return imgs.size();
		} else {
			return 0;
		}
	}

	@Override
	public Fragment getItem(int position) {
		if (this.imgs != null) {
			return PhotoFragment.newInstance(this.imgs.get(position));
		} else {
			return null;
		}
	}

}