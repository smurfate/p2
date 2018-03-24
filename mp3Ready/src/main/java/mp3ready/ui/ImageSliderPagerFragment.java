package mp3ready.ui;

import java.util.List;

import mp3ready.adapters.ImaegSliderPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.viewpagerindicator.PageIndicator;
import com.zgm.mp3ready.R;

/**
 * <p>
 * pager for show user any image he clicks on it in fullscreen
 * </p>
 * 
 * @author mhmd
 * 
 */
public class ImageSliderPagerFragment extends Zfragment implements
		OnClickListener {

	public final static String TAG = ImageSliderPagerFragment.class.getName();

	private ImaegSliderPagerAdapter adapter;
	private static ViewPager pager;
	private PageIndicator mIndicator;
	List<String> imgs;
	private int CurrentItem = 0;
	private String title;

	public static ImageSliderPagerFragment newInstance(String title,
			int CurrentItem, List<String> imgs) {
		ImageSliderPagerFragment efrag = new ImageSliderPagerFragment();

		efrag.CurrentItem = CurrentItem;
		efrag.imgs = imgs;
		efrag.title = title;
		return efrag;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.image_slider_pager, container,
				false);
		return view;

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);

		pager = (ViewPager) getView().findViewById(R.id.pager);
		mIndicator = (PageIndicator) getView().findViewById(R.id.indicator);

	}

	@Override
	public void onStart() {
		
		super.onStart();
		// mainActivity.getSupportActionBar().setTitle(title);

		if (adapter == null) {
			adapter = new ImaegSliderPagerAdapter(imgs,
					mainActivity.getSupportFragmentManager());
			pager.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			mIndicator.setViewPager(pager);
			mIndicator.setCurrentItem(CurrentItem);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		

	}

}