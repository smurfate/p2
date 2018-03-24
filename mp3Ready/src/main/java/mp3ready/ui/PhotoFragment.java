package mp3ready.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zgm.mp3ready.R;

/**
 * <p>
 * photo screen that render the image in single tab in
 * {@link ImageSliderPagerFragment}
 * </p>
 * 
 * @author mhmd
 * 
 */

public class PhotoFragment extends Zfragment {

	public final static String TAG = PhotoFragment.class.getName();

	private ImageView imgView;
	private String image;

	public static PhotoFragment newInstance(String img) {
		PhotoFragment efrag = new PhotoFragment();
		efrag.image = img;
		return efrag;
	}

	private void initView() {

		imgView = (ImageView) getView().findViewById(R.id.imgview);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.img_fullscreen_layout, container,
				false);
		return view;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	@Override
	public void onStart() {
		
		super.onStart();
		initView();
		if (image != null && mainActivity != null) {
			mainActivity.mPicasso.load(image).into(imgView);

		}
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

}