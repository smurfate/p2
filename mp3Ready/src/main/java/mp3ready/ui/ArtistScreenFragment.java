package mp3ready.ui;

import mp3ready.api.NewApiCalls;
import mp3ready.entities.Tag;
import mp3ready.enums.Enums;
import mp3ready.response.ArtistSongsResponse;
import mp3ready.serializer.ZdParser;
import mp3ready.util.Utilities;
import mp3ready.views.TagCloudLinkView;
import mp3ready.views.TagCloudLinkView.OnTagSelectListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zgm.mp3ready.R;

/**
 * <p>
 * render artist's information
 * </p>
 * 
 * @author mhmd
 * 
 */
public class ArtistScreenFragment extends Zfragment implements OnClickListener,
		OnTagSelectListener {

	public final static String TAG = ArtistScreenFragment.class.getName();
	private final int SMALL_tAGS_COUNT = 5;
	private ImageView iv_artist_photo;
	private TextView tv_artist_name;
	private TextView tv_artist_bio;
	private TagCloudLinkView tagView;
	private LinearLayout top_songs_section;
	private ArtistSongsResponse data;
	private String artist_id;
	private RelativeLayout artist_photo_section;

	public static ArtistScreenFragment newInstance(String artist_id) {
		ArtistScreenFragment efrag = new ArtistScreenFragment();
		efrag.artist_id = artist_id;
		return efrag;
	}

	/**
	 * initialize the views and inflaterate them from xml
	 */
	private void initView() {
		iv_artist_photo = (ImageView) getView().findViewById(R.id.artist_photo);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				(int) (Utilities.getScreenSizePixels(mainActivity)[1] / 2.5));
		lp.height = (int) (Utilities.getScreenSizePixels(mainActivity)[1] / 2.5);
		iv_artist_photo.setLayoutParams(lp);
		tv_artist_name = (TextView) getView().findViewById(R.id.tv_artist_name);
		tv_artist_bio = (TextView) getView().findViewById(R.id.artist_bio);
		tagView = (TagCloudLinkView) getView().findViewById(R.id.artist_tags);
		// LinearLayout.LayoutParams lp = new
		// LinearLayout.LayoutParams(Utilities.getScreenSizePixels(mainActivity)[0],
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// tagView.setLayoutParams(lp);
		//tagView.setMinimumWidth(Utilities.getScreenSizePixels(mainActivity)[0]);

		top_songs_section = (LinearLayout) getView().findViewById(
				R.id.artist_top_songs);

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
		
		View view = inflater.inflate(R.layout.artist_screen, container, false);
		return view;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		super.onCreateOptionsMenu(menu, inflater);

	}

	/**
	 * <p>
	 * render all artist tags useing {@link TagCloudLinkView}
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
		if (data.artist.tags != null && !data.artist.tags.equals("")) {
			String[] tags = data.artist.tags.split(",");
			for (int i = 0; i < tags.length - 1; i++) {
				int twoDotsLastIndx = tags[i].lastIndexOf(":");
				String tag = tags[i].substring(0, twoDotsLastIndx);
				Float weight = Float.valueOf(tags[i]
						.substring(twoDotsLastIndx + 1));
				Double alpha = (0.6 + ((weight * 0.4) / 100));
				tagView.add(new Tag(i, tag, alpha));
			}
			tagView.add(new Tag(tags.length - 1, "Less", 1.0));
			tagView.drawTags();
		}
	}

	/**
	 * <p>
	 * render first {@link #SMALL_tAGS_COUNT} artist tags useing
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
		if (data.artist.tags != null && !data.artist.tags.equals("")) {
			String[] tags = data.artist.tags.split(",");
			for (int i = 0; i < SMALL_tAGS_COUNT; i++) {
				int twoDotsLastIndx = tags[i].lastIndexOf(":");
				String tag = tags[i].substring(0, twoDotsLastIndx);
				Float weight = Float.valueOf(tags[i]
						.substring(twoDotsLastIndx + 1));
				Double alpha = (0.6 + ((weight * 0.4) / 100));
				tagView.add(new Tag(i, tag, alpha));
			}
			tagView.add(new Tag(SMALL_tAGS_COUNT, "More", 1.0));
			tagView.drawTags();
		}
	}

	/**
	 * read artist's info and render it in views render artist's tags calling
	 * {@link #renderSection(java.util.List, String, ViewGroup, boolean, boolean, boolean, OnClickListener)}
	 * to render grid of songs of artist
	 */
	private void loadDetails() {
		if (data.artist.image != null && !data.artist.image.equals("")) {
			mainActivity.mPicasso.load(data.artist.image).fit()
					.into(iv_artist_photo);
		}
		tv_artist_name.setText(data.artist.name);
		tv_artist_bio.setText(data.artist.about);

		if (data.artist.tags != null && !data.artist.tags.equals("")) {
			String[] tags = data.artist.tags.split(",");
			if (tags.length > SMALL_tAGS_COUNT + 3) {
				tagView.setOnTagSelectListener(this);
				//renderPartOfTags();
				renderAllTags();
			} else {
				for (int i = 0; i < tags.length - 1; i++) {
					int twoDotsLastIndx = tags[i].lastIndexOf(":");
					String tag = tags[i].substring(0, twoDotsLastIndx);
					Float weight = Float.valueOf(tags[i]
							.substring(twoDotsLastIndx + 1));
					Double alpha = (0.6 + ((weight * 0.4) / 100));
					tagView.add(new Tag(i, tag, alpha));
				}
				tagView.drawTags();
			}

		}
		ViewGroup section = (ViewGroup) LayoutInflater.from(mainActivity)
				.inflate(R.layout.genre_section, null);
		renderSection(data.songs, "TOP SONGS", section, false, true, true,
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						
						mainActivity.gotoFragment(ListFragment.newInstance(
								ListFragment.ARTIST_SONGS,
								data.artist.artist_id), ListFragment.TAG, true);// is
																				// child
					}
				});
		this.top_songs_section.addView(section);
	}

	/**
	 * send request to getting artist info and his songs
	 */
	private void getArtistInfo() {
		String json = "{\"artist_id\":\"" + artist_id + "\"}";
		mainActivity.apiCalls.artistSongs(json, 1, new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				try {
					ZdParser parser = new ZdParser(response);
					if (parser.code == 200) {
						data = gson.fromJson(parser.response,
								ArtistSongsResponse.class);
						if (data != null) {
							loadDetails();
						}
					} else {
						toast(parser.response);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}


			}
		});
	}

	@Override
	public void onStart() {
		
		super.onStart();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		
		super.onViewCreated(view, savedInstanceState);
		initView();
		// mainActivity.dAdapter.setSelectedItemPos(-1);
		mainActivity.currentFrag = ArtistScreenFragment.TAG;
		if (tagView.getTags().size() <= 0) {
			getArtistInfo();
		}
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
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

		}
	}

	/**
	 * we render tag to reperesent "Load More Tags" , so when we click on it we
	 * expand/collapse artist's tags
	 */
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
				try {
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
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}
		});

	}

}