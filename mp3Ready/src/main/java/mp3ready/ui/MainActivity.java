package mp3ready.ui;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import mp3ready.Services.PlayerService;
import mp3ready.adapters.DownloadsAdatper;
//import mp3ready.api.ApiCalls;
import mp3ready.api.NewApiCalls;
import mp3ready.db.DataSource;
import mp3ready.entities.DownloadLink;
import mp3ready.entities.PlayList;
import mp3ready.entities.Song;
import mp3ready.entities.User;
import mp3ready.enums.Enums;
import mp3ready.http.OkHttpDownloader;
import mp3ready.lazylist.ImageLoader;
import mp3ready.player.services.BroadcastsHandler;
import mp3ready.player.services.ObservableOutput;
import mp3ready.player.services.Output;
import mp3ready.player.services.OutputCommand;
import mp3ready.player.services.OutputUsingOnClickListener;
import mp3ready.player.services.PhoneStateHandler;
import mp3ready.player.services.PlayerServiceConnector;
//import mp3ready.playlists.PlayListFactory;
import mp3ready.response.TopTagSongsResponse;
import mp3ready.util.AppCache;
import mp3ready.util.DeviceInfo;
import mp3ready.util.FragmentNavigator;
import mp3ready.util.MyIntents;
import mp3ready.util.StorageUtils;
import mp3ready.util.Utilities;
import mp3ready.views.MaterialSection;
import mp3ready.views.MaterialSectionListener;
import mp3ready.views.MaterialSubheader;
//import zgm.http.CallBack;
//import zgm.util.DeviceInfo;
//import zgm.util.OnlineCkeck;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.*;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.andtinder.Utils;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.zgm.mp3ready.R;
import com.zgm.zlib.http.HttpAPI;

/**
 * <p>
 * this the main Activity of application that hold all screens and do the
 * following:
 * </p>
 * <li>initialize the left drawer and add sections into navigation drawer</li>
 * <li>handle open and close left drawer</li> <li>replace the screen to new
 * screen by calling @gotoFragment function</li> <li>get screen dimensions</li>
 * <li>logout from app</li> <li>store authentication key in shared preferences</li>
 * <li>put flag true in shared preferences to save that user is loggedin</li>
 * <li>render small media player controller and</li>
 * 
 * @author mhmd
 * 
 */

public class MainActivity extends AppCompatActivity implements
		TextWatcher, MaterialSectionListener, NavigationView.OnNavigationItemSelectedListener {


	@Bind(R.id.toolbar)	Toolbar toolbar;
	@Bind(R.id.fab)	FloatingActionButton fab;
	@Bind(R.id.fab2) FloatingActionButton fab2;
    @Bind(R.id.drawer_layout) public DrawerLayout drawer;
	@Bind(R.id.player_view) SimpleExoPlayerView playerView;
	@Bind(R.id.nav_view) NavigationView navigationView;

//	@Bind(R.id.exo_content_frame) AspectRatioFrameLayout exoContentFrame;
//	@Bind(R.id.exo_overlay)	FrameLayout exoOverlay;


//	@Bind(R.id.player_controller) LinearLayout playerController;
//	@Bind(R.id.mediacontroller_time_current) TextView playerCurrentTime;
//	@Bind(R.id.mediacontroller_time_total) TextView playerTotalTime;
//	@Bind(R.id.mediacontroller_file_name) TextView playerSongName;
//	@Bind(R.id.mediacontroller_seekbar) SeekBar playerSeekbar;
//	@Bind(R.id.played_song_cover) ImageView playerCover;
//	@Bind(R.id.mediacontroller_prev) ImageButton playerPrevious;
//	@Bind(R.id.mediacontroller_nxt) ImageButton playerNext;
//	@Bind(R.id.mediacontroller_play_pause) ImageButton playerPlay;

	public FragmentNavigator navigator;
	public NewApiCalls apiCalls;

	PlayerService.PlayerBinder playerBinder;
	SimpleExoPlayer simplePlayer;

	private Handler handler;


	public final static String EXTRA_NOTIFICATION_UPDATES = "NOTIFICATION_UPDATES";
	// ListView mDrawerList;
//	LinearLayout sections;
//	RelativeLayout mLDrawerContainer;
	// DrawerAdapter dAdapter;

    public ActionBarDrawerToggle toggle;
    public ActionBar actionBar;

    public DeviceInfo deviceInfo;
	private String[] mNavTitles;
	public Gson gson;
	// private TypedArray mNavIcons;
	// private TypedArray mNavPressedIcons;
	public ImageLoader imageLoader;
	public String currentFrag = "";
	private View v;
	private MyReceiver mReceiver;
	public DownloadsAdatper download_adapter;
	public Picasso mPicasso;
	public DataSource db;
	public boolean am_i_in_home = false;
	public boolean am_i_in_login = false;
	public SeekBar mediacontroller_seekbar;
//	public PlayListFactory playListFactory;
	public LinearLayout song_name_artist_ll;
	public TextView mediacontroller_progresstime;
	public TextView mediacontroller_totaltime;
	public TextView mediacontroller_song_name;
	public boolean isShuffle = false;
	public ImageButton mediacontroller_play_pause;
	public ImageButton mediacontroller_prev;
	public ImageButton mediacontroller_next;
	public TextView mediacontroller_msg;
	public ImageView mediacontroller_song_cover;
	public LinearLayout mediacontroller_ll;
//	public ObservableOutput player;
	private Runnable progressBarRunnable;
	public List<Song> home_page_recommended_songs;
	public List<TopTagSongsResponse> home_page_top_genre_songs;
	public List<TopTagSongsResponse> home_page_choosed_top_genre;
	private List<Fragment> childFragmentStack;
	private List<String> childTitleStack;
	public String mCachedResponse;
	private final static String TAG = MainActivity.class.getName();
	public boolean am_i_in_full_player_screen = false;
	private Set<PhoneStateListener> phoneStateListeners = new HashSet<PhoneStateListener>();
	private Set<BroadcastReceiver> broadcastReceivers = new HashSet<BroadcastReceiver>();
	private List<String> invalidUrls;
	public AppCache mem;
	private float density;
	public Song playedSong = null;
	private List<MaterialSection> sectionList;
	private boolean isCurrentFragmentChild;

	HashMap<String, IObserver> observers = new HashMap<String, IObserver>();
	Menu mainMenu;
	private MaterialSection currentSection;

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.nav_home:
                navigator.gotoMainSection();
				break;
			case R.id.nav_recommended:
			    navigator.gotoSection(PagerFragment.newInstance(PagerFragment.PAGER_RECOMMENDATIONS,0));
				break;
			case R.id.nav_download:
                navigator.gotoSection(PagerFragment.newInstance(PagerFragment.PAGER_PLAY_LISTS,0));
				break;
            case R.id.nav_recently:
                navigator.gotoSection(PagerFragment.newInstance(PagerFragment.PAGER_PLAY_LISTS,1));

                break;
            case R.id.nav_playlist:
                navigator.gotoSection(PagerFragment.newInstance(PagerFragment.PAGER_PLAY_LISTS,2));

                break;
			case R.id.nav_profile:
			    navigator.gotoSection(ListFragment.newInstance(ListFragment.USER_FAV_GENRES,false));
				break;
			case R.id.nav_login:
				navigator.gotoSection(LoginFragment
						.newInstance(new NewApiCalls.Callback() {

							@Override
							public void onFinished(String response) {

								User mUser = gson.fromJson(response,User.class);
								if (mUser != null) {

									mem.setAppAuthed(true);
									Log.e("MainActivity", "authKey:"
											+ mUser.auth_key);
									mem.setAuthKey(mUser.auth_key);

									if (!getSupportActionBar().isShowing())
										getSupportActionBar().show();
//									getCurrentSection().select();
//									MainActivity.this.onBackPressed();

//									for (int i = 0; i < mNavTitles.length; i++) {
//										addSectionVisibilityIfAppAuthed(
//												i, mNavTitles[i]);
//									}
									navigator.gotoMainSection();
									reloadDrawer();

								}

							}
						}));

				break;
			case R.id.nav_logout:
				mem.setAppAuthed(false);
				mem.setAuthKey(null);
				home_page_choosed_top_genre = null;
				home_page_recommended_songs = null;
				home_page_top_genre_songs = null;
				currentSection = null;
				navigator.gotoMainSection();
//				getSectionByTitle(Enums.HOME_SCREEN_POS).setTarget(HomeFragment.newInstance());
//				setSection(getSectionByTitle(Enums.HOME_SCREEN_POS),false);//is not child
//				for (int i = 0; i < mNavTitles.length; i++) {
//					addSectionVisibilityIfAppNotAuthed(i, mNavTitles[i]);
//				}
				reloadDrawer();

				break;
		}
		drawer.closeDrawer(GravityCompat.START);

		return true;
	}


	/**
	 * <p>
	 * interface to add listenres in playlists screen to let each playlist get
	 * the response from server
	 * </p>
	 * 
	 * @author mhmd
	 * 
	 */
	public interface IObserver {
		String getId();

		void onFinished(String response);

		void onTrackAdded(PlayList pl);

		void onDoActionOnSong();
	}


	/**
	 * add listener by implement the onFinished and onTrackAdded
	 * 
	 * @param observer
	 */
	public void addObserver(IObserver observer) {
		observers.put(observer.getId(), observer);
	}

	public void removeObserver(String observeId) {
		observers.remove(observeId);
	}

	/**
	 * calling #onFinished method for all observers to render the playlists
	 * 
	 * @param response
	 *            the response from server and we will cach it in
	 *            {@link #mCachedResponse}
	 */
	public void notifyOnFinished(String response) {
		this.mCachedResponse = response;
		for (IObserver observer : observers.values()) {
			observer.onFinished(response);
		}
	}

	/**
	 * calling #onDoActionOnSong method for all observers to refresh the
	 * recommended songs
	 * 
	 */
	public void notifyOnDoActionOnSong() {

		for (IObserver observer : observers.values()) {
			observer.onDoActionOnSong();
		}
	}

	/**
	 * calling #notifyTrackAdded method for all observers to do something when
	 * some track added to playlist
	 * 
	 * @param listType
	 *            play list type
	 * @param pl
	 *            the play list that we added track to it
	 */
	public void notifyTrackAdded(String listType, PlayList pl) {
		observers.get(listType).onTrackAdded(pl);
	}

	/**
	 * initialize all views and setup all click listeners for all views and
	 * media player controller
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		try {
//			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		mem = new AppCache(getApplicationContext());
		handler = new Handler(getMainLooper());

		Intent intent = new Intent(this, PlayerService.class);
		startService(intent);
		bindService(intent, playerConnection, 0);



		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        actionBar = getSupportActionBar();


        navigator = new FragmentNavigator(this,HomeFragment.newInstance(),R.id.content);




		fab.setVisibility(View.GONE);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

//				SimpleExoPlayer player  =  playerBinder.initializePlayer("/sdcard/boy.mp4");
//				playerView.setPlayer(player);
				Intent downloadIntent = new Intent("mp3ready.download.services.IDownloadService");
				downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ADD);
				downloadIntent.putExtra(MyIntents.URL, "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
				startService(downloadIntent);


			}
		});

		fab2.setVisibility(View.GONE);
		fab2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playerView.showController();
			}
		});


//        getSupportActionBar().hide();
//        getSupportActionBar().show();


        Picasso.Builder builder = new Picasso.Builder(this);
		gson = new GsonBuilder().create();
		builder.downloader(new OkHttpDownloader(this, 100 * 1024 * 1024));
		// builder.downloader(new UrlConnectionDownloader(this));
		mPicasso = builder.build();
		density = this.getResources().getDisplayMetrics().density;
		download_adapter = new DownloadsAdatper(this);
		db = new DataSource(this);
		db.prepareDB();
		db.open();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Log.i("MainActivity", "im after setcontent");
		deviceInfo = DeviceInfo.GetInfo(this);

		apiCalls = new NewApiCalls(this);
//		ApiCalls.setContext(this);
//		getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		imageLoader = new ImageLoader(this);



//		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//		mLDrawerContainer = (RelativeLayout) findViewById(R.id.left_drawer);
		// mRDrawerContainer = (ScrollView)findViewById(R.id.right_drawer);
		// mDrawerList = (ListView) findViewById(R.id.list_drawer );

		// set a custom shadow that overlays the main content when the drawer
		// opens
		drawer.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		navigationView.setNavigationItemSelectedListener(this);


//		toggle = new ActionBarDrawerToggle(this, /* host Activity */
//				drawer, /* DrawerLayout object */
//				R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
//				R.string.drawer_open, /* "open drawer" description for accessibility */
//				R.string.drawer_close /* "close drawer" description for accessibility */
//		) {


//			toggle = new ActionBarDrawerToggle(this, /* host Activity */
//                    drawer, /* DrawerLayout object */
//				toolbar, /* nav drawer image to replace 'Up' caret */
//				R.string.drawer_open, /* "open drawer" description for accessibility */
//				R.string.drawer_close /* "close drawer" description for accessibility */
//		) {
//			public void onDrawerClosed(View view) {
//				// getSupportActionBar().setTitle(mTitle);
//				invalidateOptionsMenu(); // creates call to
//				// onPrepareOptionsMenu()
//
//			}
//
//			public void onDrawerOpened(View drawerView) {
//				// getSupportActionBar().setTitle(mDrawerTitle);
//				hideKB();
//				invalidateOptionsMenu(); // creates call to
//				// onPrepareOptionsMenu()
//
//				if (drawerView.getId() == R.id.left_drawer) {
//
//					// tv7.setCompoundDrawablesWithIntrinsicBounds(R.drawable.picktaxi_drawer_option,0
//					// , 0, 0 );
//
//				}
//			}
//		};
//		toggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);




//		sections = (LinearLayout) findViewById(R.id.sections);


		// drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED
		// , mRDrawerContainer );

		// set up the drawer's list view with items and click listener
		mNavTitles = this.getResources().getStringArray(R.array.user_nav_array);
		sectionList = new LinkedList<MaterialSection>();
		childFragmentStack = new LinkedList<Fragment>();
		childTitleStack = new LinkedList<String>();

//		for (int i = 0; i < mNavTitles.length; i++) {
//			addSectionAt(i, mNavTitles[i]);
//		}
		// dAdapter = new DrawerAdapter(this);
		// dAdapter.refreshDataSource();

		reloadDrawer();

		// ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mNavTitles) ;

		// mDrawerList.setAdapter(dAdapter);
		// dAdapter.notifyDataSetChanged();
		// mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon

		getSupportActionBar().setTitle("Mp3Ready");
		if (drawer != null) {
			drawer.closeDrawer(GravityCompat.START);
			drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		}

//		getSupportActionBar().show();
		initActionBar();

		// enable ActionBar app icon to behave as action to toggle nav drawer

//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		getSupportActionBar().setHomeButtonEnabled(true);

		if (!StorageUtils.isSDCardPresent()) {

			return;
		}

		if (!StorageUtils.isSdCardWrittenable()) {

			return;
		}

		try {
			StorageUtils.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		Intent downloadIntent = new Intent(
				"mp3ready.download.services.IDownloadService");
		downloadIntent.putExtra(MyIntents.TYPE, MyIntents.Types.START);
		startService(downloadIntent);
//		playListFactory = new PlayListFactory(this);
//		playListFactory.setCurrnetPLayList(Enums.PLAYLIST_RECENTLY_PLAYED_LIST);
//		player = new PlayerServiceConnector(this);
		mediacontroller_ll = (LinearLayout) findViewById(R.id.player_controller);
		mediacontroller_play_pause = (ImageButton) findViewById(R.id.mediacontroller_play_pause);
		mediacontroller_prev = (ImageButton) findViewById(R.id.mediacontroller_prev);
		mediacontroller_next = (ImageButton) findViewById(R.id.mediacontroller_nxt);
		invalidUrls = new ArrayList<String>();
		song_name_artist_ll = (LinearLayout) findViewById(R.id.song_name_artist_ll);
//		mediacontroller_next.setOnClickListener(new OutputUsingOnClickListener(
//				player) {
//
//			@Override
//			public void onClick(View v, Output output) {
//
//				if (player == null) {
//					return;
//				}
//
////				Song nxt = playListFactory.getNext(isShuffle,
////						output.getCurrSong());
////				if (nxt != null) {
////
////					getMp3Urls(nxt, null);
////				}
//			}
//		});

//		mediacontroller_prev.setOnClickListener(new OutputUsingOnClickListener(player) {
//
//			@Override
//			public void onClick(View v, Output output) {
//
//				if (player == null) {
//					return;
//				}
//				Song prev = playListFactory.getPrevious(isShuffle,
//						output.getCurrSong());
//				if (prev != null) {
//					getMp3Urls(prev, null);
//				}
//			}
//		});
//		mediacontroller_play_pause
//				.setOnClickListener(new OutputUsingOnClickListener(player) {
//
//					@Override
//					public void onClick(View v, Output output) {
//
//						output.toggle();
//					}
//				});
		mediacontroller_progresstime = (TextView) findViewById(R.id.mediacontroller_time_current);
		mediacontroller_seekbar = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
		mediacontroller_song_name = (TextView) findViewById(R.id.mediacontroller_file_name);
		mediacontroller_totaltime = (TextView) findViewById(R.id.mediacontroller_time_total);
		mediacontroller_msg = (TextView) findViewById(R.id.mediacontroller_msg);

		mediacontroller_song_cover = (ImageView) findViewById(R.id.played_song_cover);
		mediacontroller_song_cover.setEnabled(false);
		song_name_artist_ll.setEnabled(false);
//		song_name_artist_ll.setOnClickListener(new OutputUsingOnClickListener(
//				player) {
//
//			@Override
//			public void onClick(View v, Output output) {
//
//				if (output.getCurrSong() != null //&& output.getCurrSong().isSong
//						 //!output.getCurrSong().isTinderSong &&
//						 ) {
//					v.setEnabled(false);
//					getSupportActionBar().hide();
//					am_i_in_full_player_screen = true;
//					gotoFragment_topbottom(
//							FullPlayerFragment.newInstance(output),
//							FullPlayerFragment.TAG);
//				}
//			}
//		});
//		mediacontroller_song_cover
//				.setOnClickListener(new OutputUsingOnClickListener(player) {
//
//					@Override
//					public void onClick(View v, Output output) {
//
//						if (output.getCurrSong() != null) {
//
//							v.setEnabled(false);
//							getSupportActionBar().hide();
//							am_i_in_full_player_screen = true;
//							gotoFragment_topbottom(
//									FullPlayerFragment.newInstance(output),
//									FullPlayerFragment.TAG);
//						}
//					}
//				});
//		setupObservers();
		SetupPhoneHandlers();
		//resetLastTrack();
		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("mp3ready.ui.ListFragment");
		registerReceiver(mReceiver, filter);



		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				hideKB();
				if (!navigator.isBack)
				{
					drawer.openDrawer(GravityCompat.START);
				}
				else
				{
					navigator.goBack();
				}
			}
		});


        startApp();


	}

	public void reloadDrawer()
	{
		if (!mem.isAppAuthed()) {
			navigationView.getMenu().findItem(R.id.nav_download).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_playlist).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_recently).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_recommended).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
		}else
		{
			navigationView.getMenu().findItem(R.id.nav_download).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
			navigationView.getMenu().findItem(R.id.nav_playlist).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_profile).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_recently).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_recommended).setVisible(true);
			navigationView.getMenu().findItem(R.id.nav_home).setVisible(true);
		}

	}

	public void playURL(String url)
	{
		SimpleExoPlayer player  =  playerBinder.initializePlayer(url);
		playerView.setPlayer(player);

	}

	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("mp3ready.Services.PlayerService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * add sub header in left drawer
	 * 
	 * @param title
	 *            the title of sub header
	 */
	public void addSubheader(CharSequence title) {
		MaterialSubheader subheader = new MaterialSubheader(this);
		subheader.setTitle(title);
		subheader.setTitleFont(Utilities.getTypefaceRegular(this));

//		sections.addView(subheader.getView());
	}

	/**
	 * Get a setted section knowing his position
	 * 
	 * N.B. this search only into section list.
	 * 
	 * @param pos
	 *            is the pos of the section
	 * @return the section with pos or null if the section is not founded
	 */
	public MaterialSection getSectionByTitle(int pos) {

		String title = mNavTitles[pos];
		for (MaterialSection section : sectionList) {
			if (section.getTitle().equals(title)) {
				return section;
			}
		}

		return null;
	}

	/**
	 * adding visibility to all sections according to user logged in or not
	 * 
	 * @param pos
	 *            the position of screen
	 * @param name
	 *            the name of section
	 */
//	private void addSectionVisibilityIfAppNotAuthed(int pos, String name) {
//		if (pos == Enums.HOME_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == 1) {
//			sections.getChildAt(pos).setVisibility(View.GONE);
//		} else if (pos == Enums.RECOMMENDED_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.GONE);
//		} else if (pos == Enums.RECOMMENDED_BY_GENRE_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.GONE);
//		} else if (pos == 4) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_DOWNLOADS_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_RECENTLY_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_PLS_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.GONE);
//		} else if (pos == Enums.PLAYLIST_FROM_FRIENDS_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.GONE);
//		} else if (pos == Enums.PLAYLIST_LIKES_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.GONE);
//		} else if (pos == 10) {
//			sections.getChildAt(pos).setVisibility(View.GONE);
//		} else if (pos == Enums.REDEFINE_PROF_SCREEEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.GONE);
//		} else if (pos == 12) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.APP_LOGIN_SCREEEN_POS) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.APP_LOGOUT_SCREEEN_POS) {
//			sections.getChildAt(pos).setVisibility(View.GONE);
//		}
//	}

	/**
	 * adding visibility to all sections according to user logged in or not
	 * 
	 * @param pos
	 *            the position of screen
	 * @param name
	 *            the name of section
	 */
//	private void addSectionVisibilityIfAppAuthed(int pos, String name) {
//		if (pos == Enums.HOME_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == 1) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.RECOMMENDED_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.RECOMMENDED_BY_GENRE_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == 4) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_DOWNLOADS_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_RECENTLY_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_PLS_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_FROM_FRIENDS_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == Enums.PLAYLIST_LIKES_SCREEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == 10) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.REDEFINE_PROF_SCREEEN_POS) {
//			getSectionByTitle(pos).getView().setVisibility(View.VISIBLE);
//		} else if (pos == 12) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		} else if (pos == Enums.APP_LOGIN_SCREEEN_POS) {
//			sections.getChildAt(pos).setVisibility(View.GONE);
//		} else if (pos == Enums.APP_LOGOUT_SCREEEN_POS) {
//			sections.getChildAt(pos).setVisibility(View.VISIBLE);
//		}
//
//	}

	/**
	 * create new section in drawer
	 * 
	 * @param title
	 *            the title of section
	 * @param icon
	 *            the icon of the section as drawable
	 * @param target
	 *            the fragment of section
	 * @return new section
	 */
	private MaterialSection newSection(String title, Drawable icon,
			Fragment target) {
		MaterialSection section = new MaterialSection(this,
				MaterialSection.ICON_24DP, MaterialSection.TARGET_FRAGMENT);
		section.setOnClickListener(this);
		section.setIcon(icon);
		section.setTitle(title);
		section.setTarget(target);

		return section;
	}
//	 public void addDivisor() {
//	        View view = new View(this);
//	        view.setBackgroundColor(Color.parseColor("#8f8f8f"));
//	        // height 1 px
//	        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
//	        params.setMargins(0,(int) (8 * density), 0 , (int) (8 * density));
//
//	        sections.addView(view, params);
//	    }
	 /**
		 * create new section in drawer
		 * 
		 * @param title
		 *            the title of section
		 * @param icon
		 *            the icon of the section as drawable
		 * @param target
		 *            click listenr
		 * @return new section
		 */
	  public MaterialSection newSection(String title, int icon,MaterialSectionListener target) {
	        return  newSection(title,getResources().getDrawable(icon),target);
	    }
	  /**
		 * create new section in drawer
		 * 
		 * @param title
		 *            the title of section
		 * @param icon
		 *            the icon of the section as drawable
		 * @param target
		 *            click listenr
		 * @return new section
		 */
	  public MaterialSection newSection(String title, Drawable icon, MaterialSectionListener target) {
	        MaterialSection section = new MaterialSection(this,MaterialSection.ICON_24DP,MaterialSection.TARGET_LISTENER);
	        section.setOnClickListener(this);
	        section.setIcon(icon);
	        section.setTitle(title);
	        section.setTarget(target);

	        return section;
	    }
	/**
	 * create new section in drawer
	 * 
	 * @param title
	 *            the title of section
	 * @param icon
	 *            the icon of the section as drawable
	 */
	private MaterialSection newSection(String title, int icon, Fragment target) {
		return newSection(title, getResources().getDrawable(icon), target);
	}

//	/**
//	 * create section in given position and given name
//	 *
//	 * @param pos
//	 *            the desired position of new section
//	 * @param name
//	 *            the name of new section
//	 * @return added section
//	 */
//	private MaterialSection addSectionAt(int pos, String name) {
//		MaterialSection section = null;
//		if (pos == Enums.HOME_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_drawer_home,
//					HomeFragment.newInstance());
//		} else if (pos == 1) {
//			addSubheader(name);
//		} else if (pos == Enums.RECOMMENDED_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_like,
//					PagerFragment.newInstance(
//							PagerFragment.PAGER_RECOMMENDATIONS, 0));
//		} else if (pos == Enums.RECOMMENDED_BY_GENRE_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_like,
//					PagerFragment.newInstance(
//							PagerFragment.PAGER_RECOMMENDATIONS, 1));
//		} else if (pos == 4) {
//			addSubheader(name);
//		} else if (pos == Enums.PLAYLIST_DOWNLOADS_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_drawerdownloads,
//					PagerFragment
//							.newInstance(PagerFragment.PAGER_PLAY_LISTS, 0));
//		} else if (pos == Enums.PLAYLIST_RECENTLY_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_drawer_recentlyplayed,
//					PagerFragment
//							.newInstance(PagerFragment.PAGER_PLAY_LISTS, 1));
//		} else if (pos == Enums.PLAYLIST_PLS_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_drawer_playlist,
//					PagerFragment
//							.newInstance(PagerFragment.PAGER_PLAY_LISTS, 2));
//		} else if (pos == Enums.PLAYLIST_FROM_FRIENDS_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_drawer_friends,
//					PagerFragment
//							.newInstance(PagerFragment.PAGER_PLAY_LISTS, 3));
//		} else if (pos == Enums.PLAYLIST_LIKES_SCREEN_POS) {
//			section = newSection(name, R.drawable.ic_drawer_likes,
//					PagerFragment
//							.newInstance(PagerFragment.PAGER_PLAY_LISTS, 4));
//		} else if (pos == 10) {
//			addSubheader(name);
//		} else if (pos == Enums.REDEFINE_PROF_SCREEEN_POS) {
//			section = newSection(name, R.drawable.ic_drawer_redefine,
//					ListFragment.newInstance(ListFragment.USER_FAV_GENRES,
//							false));
//		}else if (pos == 12){
//			addDivisor();
//		}
//		else if (pos == Enums.APP_LOGIN_SCREEEN_POS) {
//			section = newSection(name, R.drawable.ic_login,this);
//		}else if (pos == Enums.APP_LOGOUT_SCREEEN_POS){
//			section = newSection(name, R.drawable.ic_login, this);
//		}
//
//		if (section != null)
//			addSection(section);
//		return section;
//	}

//	/**
//	 * add section to drawer view and sections list
//	 *
//	 * @param section
//	 *            the new section
//	 */
//	private void addSection(MaterialSection section) {
//		// section.setPosition(sectionList.size());
//		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//				ViewGroup.LayoutParams.MATCH_PARENT, (int) (48 * density));
//		section.setTypeface(Utilities.getTypefaceMedium(this));
//		sectionList.add(section);
//
//		// ViewParent parent = section.getView().getParent();
//		sections.addView(section.getView(), params);
//
//	}

	public void hideTitle() {
		try {
			((View) findViewById(android.R.id.title).getParent())
					.setVisibility(View.GONE);
		} catch (Exception e) {
		}
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
	}

	/**
	 * <p>
	 * read from db how many pending downloads and set it in drawer
	 * </p>
	 */
	private void setPendingDownlaodsCountInDrawer() {
		int count = db.getPendingDownloadsCount();

		getSectionByTitle(Enums.PLAYLIST_DOWNLOADS_SCREEN_POS)
				.setNotifications(count);
	}

	/**
	 * <p>
	 * read from recommended songs size and set it in drawer
	 * </p>
	 */
	private void setRecommendedSongsCountInDrawer() {
		if (home_page_recommended_songs != null
				&& home_page_recommended_songs.size() > 0) {
			int count = home_page_recommended_songs.size();
			getSectionByTitle(Enums.RECOMMENDED_SCREEN_POS).setNotifications(
					count);
		}

	}

	/**
	 * - adding observer to listener and do action when the player change the
	 * song , stopped, started or has invalid url - setup the seekBar and update
	 * it
	 */
//	private void setupObservers() {
//		player.addObserver(new ObservableOutput.PlayerObserver() {
//
//			@Override
//			public String getId() {
//
//				return "PlayViewComponentsUpdater1";
//			}
//
//			@Override
//			public void trackChanged(Song track, int lengthInMillis) {
//
//				Log.i("MainActivity", "trackChanged");
//				mediacontroller_next.setEnabled(true);
//				mediacontroller_prev.setEnabled(true);
//				mediacontroller_seekbar.setMax(lengthInMillis);
//				mediacontroller_seekbar.setSecondaryProgress(0);
//				mediacontroller_totaltime.setText(Utilities
//						.ConvertToMinutes(lengthInMillis));
//				mediacontroller_song_name.setText(track.SName);
//				mediacontroller_song_cover.setEnabled(true);
//				song_name_artist_ll.setEnabled(true);
//				mem.setLastTrackPlayed(gson.toJson(track));
//				mediacontroller_song_cover.setImageDrawable(getResources()
//						.getDrawable(R.drawable.song_cover_tst));
//				if (track.songcover != null && !track.songcover.equals("")) {
//					mPicasso.load(track.songcover).fit()
//							.into(mediacontroller_song_cover);
//				} else {
//					mediacontroller_song_cover.setImageDrawable(getResources()
//							.getDrawable(R.drawable.song_cover_tst));
//				}
//				// mediacontroller_msg.setText("url:"+(track.current_url_indx+1)+" of "+track.urls.size());
//				if (track.Artist != null && !track.Artist.equals("")) {
//					try {
//						mediacontroller_msg.setText(track.Artist + " - "
//								+ track.link.bitrate);
//					} catch (Exception e) {
//						// TODO: handle exception
//						mediacontroller_msg.setText(track.Artist);
//					}
//
//				} else {
//					mediacontroller_msg.setText("");
//				}
//				mediacontroller_play_pause.setEnabled(true);
////				if (track.SID != null) {
////					sendPlayableUrl(track);
////					sendReportingAboutInvalidURLs(track);
////				}
//				if (track.urls != null && track.urls.size() > 0) {
//					Log.e("MainActivity", "set Link As Valid");
//					track.setLinkAsValid();
//				}
//			}
//
//			@Override
//			public void stopped() {
//
//				Log.i("MainActivity", "stopped");
//
//				mediacontroller_play_pause.setImageDrawable(getResources()
//						.getDrawable(R.drawable.player_ic_play));
//			}
//
//			@Override
//			public void started() {
//				Log.i("MainActivity", "started");
//
//
//				mediacontroller_play_pause.setImageDrawable(getResources()
//						.getDrawable(R.drawable.player_ic_pause));
//			}
//
//			@Override
//			public void TrackHasInvalidUrl(Song track) {
//
//				if (!invalidUrls.contains(track.url)) {
//					invalidUrls.add(track.url);
//				}
//				mediacontroller_play_pause.setEnabled(false);
//				mediacontroller_seekbar.setSecondaryProgress(0);
//				Log.i("MainActivity", "TrackHasInvalidUrl:" + track.url);
//				if (track.urls != null) {
//					mediacontroller_msg.setText((track.current_url_indx + 1)
//							+ " of " + track.urls.size());
//					runPlayerForNextUrl(track);
//				}
//
//			}
//
//			@Override
//			public void onMediaPlayerBufferingonUpdate(int duration, int pos) {
//
//				Log.e("MainActivity", duration + ":" + pos);
//				mediacontroller_seekbar.setSecondaryProgress((duration / 100)
//						* pos);
//			}
//
//			@Override
//			public void onMediaPlayerCompletion(Output output) {
//
//				if (output.getCurrSong() != null && mem.isAppAuthed()) {
//					sendListenAction(output.getCurrSong());
//				}
//				if (!output.isMPLooping()) {
//
//					mediacontroller_seekbar.setSecondaryProgress(0);
//					if (output.getCurrentMillis() > 5000) {
//						Log.e("MainActivity",
//								"onCompletion:" + output.getCurrentMillis());
//						Song nxt = playListFactory.getNext(isShuffle,
//								output.getCurrSong());
//						if (nxt != null) {
//							getMp3Urls(nxt, null);
//						}
//					}
//				}
//			}
//		});
//
//		progressBarRunnable = new Runnable() {
//
//			@Override
//			public void run() {
//
//				player.connectPlayer(new OutputCommand() {
//
//					@Override
//					public void connected(Output output) {
//
//
//						mediacontroller_seekbar.setProgress(output
//								.getCurrentMillis());
//
//						mediacontroller_progresstime.setText(Utilities
//								.ConvertToMinutes(output.getCurrentMillis()));
//						mediacontroller_seekbar.postDelayed(
//								progressBarRunnable, 100);
//					}
//				});
//			}
//		};
//		mediacontroller_seekbar.postDelayed(progressBarRunnable, 0);
//		mediacontroller_seekbar
//				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//					@Override
//					public void onStopTrackingTouch(final SeekBar seekBar) {
//
//						player.connectPlayer(new OutputCommand() {
//							public void connected(Output output) {
//								output.goToMillis(seekBar.getProgress());
//								mediacontroller_seekbar.postDelayed(
//										progressBarRunnable, 100);
//							}
//						});
//					}
//
//					@Override
//					public void onStartTrackingTouch(SeekBar seekBar) {
//
//						mediacontroller_seekbar
//								.removeCallbacks(progressBarRunnable);
//					}
//
//					@Override
//					public void onProgressChanged(SeekBar seekBar, int arg1,
//							boolean arg2) {
//
//
//					}
//				});
//
//		player.connectPlayer(new OutputCommand() {
//
//			@Override
//			public void connected(final Output output) {
//
//				if (output.getCurrSong() == null) {
//					mediacontroller_ll.setVisibility(View.GONE);
//				}
//			}
//		});
//	}

	/**
	 * report song's urls as invalid urls to server
	 * 
	 * @param s
	 *            the song that has invalid urls
	 */
//	private void sendReportingAboutInvalidURLs(Song s) {
//		if (invalidUrls != null && invalidUrls.size() > 0) {
//			String json = "{\"song_id\":\"" + s.SID + "\",\"urls\":["
//					+ TextUtils.join(",", invalidUrls) + "]}";
//			ApiCalls.reportURLS(json, new CallbackHandler(this) {
//
//				@Override
//				public void onFinished(String result) {
//					
//					try {
//						ZdParser parser = new ZdParser(result);
//						if (parser.code == 200) {
//							invalidUrls.clear();
//						}
//					} catch (Exception e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//				}
//			});
//		}
//	}

	/**
	 * <p>
	 * send to server that this song is listened from user
	 * </p>
	 * 
	 * @param s
	 *            the song that listened from user
	 */
	public void sendListenAction(Song s) {

		apiCalls.doAction(apiCalls.buildActionListenJson(s.SID), new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				Log.e("MainActivity", "listen:" + response);
			}

		});
//		ApiCalls.doAction(ApiCalls.buildActionListenJson(s.SID),
//				new CallbackHandler(this) {
//
//					@Override
//					public void onFinished(String result) {
//
//						Log.e("MainActivity", "listen:" + result);
//					}
//				});
	}

	/**
	 * <p>
	 * send to server the playable song's url
	 * 
	 * @param s
	 *            the song that has playable url
	 */
//	public void sendPlayableUrl(Song s) {
//		String json = "{\"urls\":[\"" + s.url + "\"]}";
//		ApiCalls.sendPlayableUrls(json, new CallbackHandler(this) {
//
//			@Override
//			public void onFinished(String result) {
//				
//				Log.e("MainActivity", "playable:" + result);
//			}
//		});
//	}

	/**
	 * <p>
	 * getting from sharedPreference the last played song in player and set it
	 * with initialize the media player
	 * </p>
	 */
//	private void resetLastTrack() {
//		String lastTrack = mem.getLastTrackPlayed();
//		if (!lastTrack.equals("")) {
//			final Song restoredTrack = gson.fromJson(lastTrack, Song.class);
//			if (restoredTrack != null) {
//				mediacontroller_song_name.setText(restoredTrack.SName);
//				if (restoredTrack.songcover != null
//						&& !restoredTrack.songcover.equals("")) {
//					mPicasso.load(restoredTrack.songcover).fit()
//							.into(mediacontroller_song_cover);
//				} else {
//					mediacontroller_song_cover.setImageDrawable(getResources()
//							.getDrawable(R.drawable.song_cover_tst));
//				}
//				// mediacontroller_msg.setText("url:"+(track.current_url_indx+1)+" of "+track.urls.size());
//				if (restoredTrack.Artist != null
//						&& !restoredTrack.Artist.equals("")) {
//					try {
//						mediacontroller_msg.setText(restoredTrack.Artist
//								+ " - " + restoredTrack.link.bitrate);
//					} catch (Exception e) {
//						// TODO: handle exception
//						mediacontroller_msg.setText(restoredTrack.Artist);
//					}
//
//				} else {
//					mediacontroller_msg.setText("");
//				}
//				mediacontroller_seekbar.setSecondaryProgress(0);
//				if (restoredTrack.url.startsWith("http")) {
//					player.connectPlayer(new OutputCommand() {
//
//						@Override
//						public void connected(Output output) {
//
//
//							// output.play(restoredTrack);
//							// output.goToMillis(mem.getExitPlayTime());
//						}
//					});
//				} else {
//					File songPath = new File(restoredTrack.url);
//					if (songPath.isFile()) {
//						player.connectPlayer(new OutputCommand() {
//
//							@Override
//							public void connected(Output output) {
//
//
//								// output.play(restoredTrack);
//								// output.goToMillis(mem.getExitPlayTime());
//							}
//						});
//					} else {
//						// do nothing
//					}
//				}
//			} else {
//				mediacontroller_ll.setVisibility(View.GONE);
//			}
//		} else {
//			mediacontroller_ll.setVisibility(View.GONE);
//		}
//	}

	/**
	 * - initialize the action bar and inflaterate the view on it - setup the
	 * listeners of login/logout button and search bar
	 */
	private void initActionBar() {
		getSupportActionBar().setDisplayOptions(
				ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_USE_LOGO
						| ActionBar.DISPLAY_SHOW_HOME
						| ActionBar.DISPLAY_HOME_AS_UP);
		v = LayoutInflater.from(this).inflate(R.layout.header, null);

		AutoCompleteTextView search_autocomplete = (AutoCompleteTextView) v.findViewById(R.id.search_autoCompleteTextView);
		
		search_autocomplete.addTextChangedListener(this);
		search_autocomplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							search();
							return true;
						}
						return false;
					}
				});

		// ImageButton
		// filter_daialog=(ImageButton)v.findViewById(R.id.filter_imageButton);
		ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT);

		getSupportActionBar().setCustomView(v, lp);

	}

	public void showTitle() {
		try {
			((View) findViewById(android.R.id.title).getParent())
					.setVisibility(View.VISIBLE);
		} catch (Exception e) {
		}
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private boolean processNotificationIntent(Intent intent) {
		if (intent != null && intent.hasExtra(EXTRA_NOTIFICATION_UPDATES)) {
			int notification_type = intent.getIntExtra(
					EXTRA_NOTIFICATION_UPDATES, -1);
			Log.e(TAG, "onNewIntent notification_type " + notification_type);

			return true;
		}
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		
		super.onNewIntent(intent);
		Log.e(TAG, "onNewIntent");
		processNotificationIntent(intent);
	}

	/**
	 * <p>
	 * called after login to start the app and goto home screen
	 * </p>
	 * 
	 */
	public void startApp() {
//		if (!getSupportActionBar().isShowing())
//			getSupportActionBar().show();
//		try {
//			while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//				getSupportFragmentManager().popBackStackImmediate();
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		Log.i("MainActivity", "im in start app");

//		if (!processNotificationIntent(getIntent())) {
//			Log.i("MainActivity", "go to main menu");
//
//			if (drawer != null)
//				drawer
//						.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//			MaterialSection first = getSectionByTitle(Enums.HOME_SCREEN_POS);
////			setSection(first,false);// is not child
//			/*
//			 * if (mem.isAppAuthed()){ selectItem(Enums.HOME_SCREEN_POS); }else
//			 * { selectItemForVisitor(Enums.HOME_SCREEN_POS); }
//			 */
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		/*
		 * if (keyCode == KeyEvent.KEYCODE_MENU) { hideKB(); if
		 * (drawer.isDrawerOpen( mLDrawerContainer)) {
		 * drawer.closeDrawer(mLDrawerContainer); } else {
		 * drawer.openDrawer(mLDrawerContainer); } return true; } else {
		 * return super.onKeyDown(keyCode, event); }
		 */
		return super.onKeyDown(keyCode, event);
	}

	public Point getScreenDim() {
		WindowManager wm = (WindowManager) getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// demo();
		Log.e(TAG, MainActivity.class.getCanonicalName() + "1");
		Log.e(TAG, MainActivity.class.getName() + "2");
		Log.e(TAG, MainActivity.class.getSimpleName() + "3");

	}

	// The click listener for ListView in the navigation drawer (unused)
	/*
	 * private class DrawerItemClickListener implements
	 * ListView.OnItemClickListener {
	 * 
	 * @Override public void onItemClick(AdapterView<?> parent, View view, int
	 * position, long id) {
	 * 
	 * if (mem.isAppAuthed()){ selectItem(position); }else {
	 * selectItemForVisitor(position); }
	 * 
	 * } }
	 */
	/**
	 * <p>
	 * goto Fullplayer fragement with topbottom animation
	 * </p>
	 * <p>
	 * set this fragment child
	 * </p>
	 * 
	 * @param frag
	 *            the full player fragment
	 * @param tag
	 *            tag of fragment
	 */
	public void gotoFragment_topbottom(Fragment frag,
			String tag) {
		navigator.gotoSubSection(frag);
//		if (MainActivity.this != null) {
//
//			if (drawer != null)
//				drawer.closeDrawer(mLDrawerContainer);
//
//			isCurrentFragmentChild = true;
//			childFragmentStack.add(frag);
//			childTitleStack.add(tag);
//			hideKB();
//
//			getSupportFragmentManager()
//					.beginTransaction()
//					.setCustomAnimations(R.anim.slide_in_from_bottom,
//							R.anim.slide_out_to_top,
//							R.anim.slide_in_from_top,
//							R.anim.slide_out_to_bottom)
//					// .setCustomAnimations(R.animator.slide_out_right ,
//					// R.animator.slide_in_left)
//					// .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
//					.addToBackStack(tag).replace(R.id.content, frag, tag)
//					.commitAllowingStateLoss();
//
//		}
	}

	/**
	 * <p>
	 * goto fragment with slide animation
	 * <p>
	 * 
	 * @param frag
	 *            the fragment that we want to goto it
	 * @param tag
	 *            the tag of fragment
	 * @param ischild
	 *            flag to know if the fragment is child or not
	 */
	public void gotoFragment(Fragment frag, String tag,
			boolean ischild) {
		if(ischild)
		{
			navigator.gotoSubSection(frag);
		}else
		{
			navigator.gotoSection(frag);
		}
//		if (MainActivity.this != null) {
//
//			if (drawer != null)
//				drawer.closeDrawer(mLDrawerContainer);
//			isCurrentFragmentChild = ischild;
//			if (isCurrentFragmentChild) {
////				 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////				toggle.setDrawerIndicatorEnabled(false);
//
//				toggle.setDrawerIndicatorEnabled(false);
//				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//				childFragmentStack.add(frag);
//				childTitleStack.add(tag);
//			} else {
////				 getSupportActionBar().setDisplayHomeAsUpEnabled(false);
////				toggle.setDrawerIndicatorEnabled(true);
//
//				getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//				toggle.setDrawerIndicatorEnabled(true);
//
//			}
//			hideKB();
//			getSupportFragmentManager()
//					.beginTransaction()
//					// .setCustomAnimations(R.animator.slide_in_from_bottom ,
//					// R.animator.slide_out_to_top,R.animator.slide_in_from_top,R.animator.slide_out_to_bottom)
//					.setCustomAnimations(R.anim.slide_out_right,
//							R.anim.slide_in_left)
//					// .setCustomAnimations(R.animator.fade_in,R.animator.fade_out)
//					.addToBackStack(null).replace(R.id.content, frag, tag)
//					.commitAllowingStateLoss();
//
//		}
	}

	@Override
	protected void onStart() {

		super.onStart();
//		if (mem.isAppAuthed()) {
//			for (int i = 0; i < mNavTitles.length; i++) {
//				addSectionVisibilityIfAppAuthed(i, mNavTitles[i]);
//			}
//		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (toggle != null)
			toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles

		if (toggle != null)
			toggle.onConfigurationChanged(newConfig);
		/*
		 * if (mLDrawerContainer != null) {
		 * 
		 * DrawerLayout.LayoutParams lp =
		 * (DrawerLayout.LayoutParams)mLDrawerContainer.getLayoutParams();
		 * lp.width = (int)getResources().getDimension(R.dimen.drawer_width);
		 * mLDrawerContainer.setLayoutParams(lp); }
		 */
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		Log.e("MainActivity", "onDestroy");
		if (db != null)
			db.close();
		if (this.mPicasso != null) {
			this.mPicasso.shutdown();
		}

		unbindService(playerConnection);
		// player.connectPlayer(new OutputCommand() {
		//
		// @Override
		// public void connected(Output output) {
		// 
		// if (mem != null) {
		// Log.e(TAG, "save the current millis:"+output.getCurrentMillis());
		// mem.setExitPlayTime(output.getCurrentMillis());
		// }
		// }
		// });
		try {
			unregisterReceiver(mReceiver);
			TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
			for (PhoneStateListener phoneStateListener : phoneStateListeners) {
				mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
			}

			for (BroadcastReceiver broadcastReceiver : broadcastReceivers) {
				unregisterReceiver(broadcastReceiver);
			}
//			player.releasePlayer();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void SetupPhoneHandlers() {
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//		PhoneStateListener phoneStateListener = new PhoneStateHandler(player);
//		phoneStateListeners.add(phoneStateListener);
//		mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

//		BroadcastReceiver broadcastsHandler = new BroadcastsHandler(player);
//		broadcastReceivers.add(broadcastsHandler);
//		registerReceiver(broadcastsHandler, new IntentFilter(
//				Intent.ACTION_HEADSET_PLUG));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//
//            case android.R.id.home: {
//
//                Log.e(TAG, "android.R.id.home");
//                if (drawer.isDrawerOpen(mLDrawerContainer)) {
//                    drawer.closeDrawer(mLDrawerContainer);
//                } else {
//                    setPendingDownlaodsCountInDrawer();
//                    if (mem.isAppAuthed()) {
//                        setRecommendedSongsCountInDrawer();
//                    }
//                    if (!toggle.isDrawerIndicatorEnabled()) {
//                        toggle.setDrawerIndicatorEnabled(true);
//                        MainActivity.this.onBackPressed();
//                    } else {
//                        drawer.openDrawer(mLDrawerContainer);
//                    }
//                }
//                break;
//            }
//        }
		return super.onOptionsItemSelected(item);
	}
	public MaterialSection getCurrentSection()
	{
		return this.currentSection;
	}
	/**
	 * <p>
	 * if we are in home page then we should exit from application , otherwise
	 * return back to home screen
	 * </p>
	 * <p>
	 * if fragment is one of childs then we reutrn back to prevous child or
	 * previous section
	 * </p>
	 */
	@Override
	public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();

        }

//		if (drawer.isDrawerOpen(mLDrawerContainer)) {
//			drawer.closeDrawer(mLDrawerContainer);
//			return;
//		}
//		if (am_i_in_home) {
//			this.moveTaskToBack(true);
//		} else {
//			if (!isCurrentFragmentChild) {
//				MaterialSection section = sectionList.get(0);
//				if (currentSection == section)
//					super.onBackPressed();
//				else {
//					am_i_in_home = true;
//					setSection(section,false);//is not child
//				}
//			} else {
//				FragmentManager fm = getSupportFragmentManager();
//				childFragmentStack.remove(childFragmentStack.size() - 1);
//				childTitleStack.remove(childTitleStack.size() - 1);
//				if (childFragmentStack.size() == 0) {
//					isCurrentFragmentChild = false;
//					if (!toggle.isDrawerIndicatorEnabled()) {
//						toggle.setDrawerIndicatorEnabled(true);
//					}
//				} else {
//					if (toggle.isDrawerIndicatorEnabled()) {
//						toggle.setDrawerIndicatorEnabled(false);
//					}
//				}
//				// toast("back to second childs:"+childFragmentStack.size());
//				fm.popBackStack();
//				if (getCurrentSection()!=null)
//					getCurrentSection().select();
//			}
//		}
	}

	/**
	 * <p>
	 * clear childs list after every section setted
	 * </p>
	 */
	private void afterSectionFragmentSetted() {

		for (int i = childFragmentStack.size() - 1; i >= 0; i--) { // if a
																	// section
																	// is
																	// clicked
																	// when user
																	// is into a
																	// child,
																	// remove
																	// all
																	// childs
																	// from
																	// stack
			childFragmentStack.remove(i);
			childTitleStack.remove(i);
		}
		isCurrentFragmentChild = false;
	}

	public void hideKB() {
		// getWindow().setSoftInputMode(
		// WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		try {

			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputManager != null && getCurrentFocus() != null
					&& getCurrentFocus().getWindowToken() != null)
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void toast(String txt) {
		
		// super.toast(txt);
		niftyNotification(txt, Effects.flip);
	}

	/**
	 * <p>
	 * render custom toast with animation
	 * 
	 * @param msg
	 *            : message that you want to show
	 * @param effect
	 *            : animation type
	 */
	private void niftyNotification(String msg, Effects effect) {
		com.gitonway.lee.niftynotification.lib.Configuration cfg = new com.gitonway.lee.niftynotification.lib.Configuration.Builder()
				.setAnimDuration(700).setDispalyDuration(2000)
				.setBackgroundColor("#0A0A2A").setTextColor("#FFFFFFFF")
				.setIconBackgroundColor("#FFFFFFFF").setTextPadding(5) // dp
				.setViewHeight(60) // dp
				.setTextLines(2) // You had better use setViewHeight and
									// setTextLines together
				.setTextGravity(Gravity.CENTER) // only text def
												// Gravity.CENTER,contain icon
												// Gravity.CENTER_VERTICAL
				.build();
		if (MainActivity.this != null) {
			NiftyNotificationView.build(this, msg, effect, R.id.mLyout, cfg)
			// .setIcon(R.drawable.lion) //remove this line ,only text
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							// add your code
						}
					}).show();
		}

	}

	/**
	 * <p>
	 * read the search query and goto search results fragment to search for it
	 * </p>
	 */
	private void search() {
		String query = ((AutoCompleteTextView) v
				.findViewById(R.id.search_autoCompleteTextView)).getText()
				.toString().trim();
		hideKB();
		if (query.length() >= 3) {
			gotoFragment(PagerFragment.newInstance(
					PagerFragment.PAGER_SEARCH_RESULTS, 0, query),
					PagerFragment.TAG, true);// is child
		} else {
			toast("you should type 3 chars at least");
		}
	}

	/**
	 * <p>
	 * if there's no current played song then we play the given song
	 * 
	 * @param s
	 *            the song that we wanna to play it
	 */
	public void playSongIfNoCurrentSongExist(final Song s) {
		SimpleExoPlayer player  =  playerBinder.initializePlayer(s.getSongURL());
		playerView.setPlayer(player);

//		if (invalidUrls != null && invalidUrls.size() > 0) {
//			invalidUrls.clear();
//		}
//		player.connectPlayer(new OutputCommand() {
//
//			@Override
//			public void connected(Output output) {
//
//				if (output.getCurrSong() == null) {
//					getMp3Urls(s, null);
//				}
//			}
//		});
	}

	/**
	 * <p>
	 * check the song that w wanna to play it, if it's downloaded before then we
	 * playlist offline, otherwise from net
	 * 
	 * @param s
	 *            the song to be play
	 * @return true if downloaded , false otherwise
	 */
//	public boolean checkIfDownloadedAndPlayIt(Song s) {
//		if (s.Artist != null && !s.Artist.equals("")) {
//			String fileName = s.Artist + " - " + s.SName + ".mp3";
//			File file = new File(StorageUtils.FILE_ROOT, fileName);
//			if (file.isFile()) {
//				s.url = file.getPath();
//				playSong(s);
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			try {
//				URL url = new URL(s.url);
//				String fileName = new File(url.getFile()).getName();
//				File file = new File(StorageUtils.FILE_ROOT, fileName);
//				if (file.isFile()) {
//					s.url = file.getPath();
//					playSong(s);
//					return true;
//				} else {
//					return false;
//				}
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//		}
//		return false;
//	}

	/**
	 * <p>
	 * get song urls from server and play the song if there's no current played
	 * song in player
	 * </p>
	 * <p>
	 * check if song downloaded before or not
	 * {@link #checkIfDownloadedAndPlayIt(Song)}
	 * 
	 * @param s
	 *            the song that we wanna to play it
	 * @param callback
	 *            callback to implement functionalaity after song's urls come
	 */
//	public void getMp3Urls(final Song s, final NewApiCalls.Callback2 callback) {
//		if (checkIfDownloadedAndPlayIt(s)) {
//			return;
//		}
//		toast("get songs urls");
//		// Log.e("zfragment",gson.toJson(s));
//
//		mediacontroller_play_pause.setEnabled(false);
//		mediacontroller_next.setEnabled(false);
//		mediacontroller_prev.setEnabled(false);
//		mediacontroller_song_cover.setEnabled(false);
//		song_name_artist_ll.setEnabled(false);
//		mediacontroller_song_name.setText(s.SName);
//		mediacontroller_song_cover.setEnabled(true);
//		song_name_artist_ll.setEnabled(true);
//		mediacontroller_song_cover.setImageDrawable(getResources().getDrawable(
//				R.drawable.default_song_cover));
//		if (s.songcover != null && !s.songcover.equals("")) {
//			mPicasso.load(s.songcover)
//					.error(getResources().getDrawable(
//							R.drawable.default_song_cover)).fit()
//					.into(mediacontroller_song_cover);
//		} else {
//			mediacontroller_song_cover.setImageDrawable(getResources()
//					.getDrawable(R.drawable.default_song_cover));
//		}
//		// mediacontroller_msg.setText("url:"+(track.current_url_indx+1)+" of "+track.urls.size());
//		if (s.Artist != null && !s.Artist.equals("")) {
//			try {
//				mediacontroller_msg.setText(s.Artist + " - " + s.link.bitrate);
//			} catch (Exception e) {
//
//				mediacontroller_msg.setText(s.Artist);
//			}
//		}
//		player.connectPlayer(new OutputCommand() {
//
//			@Override
//			public void connected(Output output) {
//
//				if (output.getCurrSong() != null) {
//					mediacontroller_seekbar.setProgress(0);
//					mediacontroller_seekbar.setSecondaryProgress(0);
//					output.release();
//				}
//				s.rate = s.RateValue;
//				s.likeState = "";
//				s.url = s.getSongURL();
//
//				if (apiCalls.api.isOnline()) {
//					mediacontroller_next.setEnabled(true);
//					mediacontroller_prev.setEnabled(true);
//					playSong(s);
//
//					if (callback != null)
//						callback.onFinished("");
//				} else {
//
//					toast("no internet connection");
//				}
////									s.urls = res.urls;
//
////				String json = "{\"song_id\":\"" + s.SID + "\"}";
////				ApiCalls.getMp3SongsUrls(json, new CallBack() {
////
////					@Override
////					public void onUnknownHost() {
////
////						toast(" Couldn't connect to server!");
////						mediacontroller_next.setEnabled(true);
////						mediacontroller_prev.setEnabled(true);
////						mediacontroller_play_pause.setEnabled(true);
////						mediacontroller_song_cover.setEnabled(true);
////						song_name_artist_ll.setEnabled(true);
////						if (callback != null)
////							callback.onUnknownHost();
////					}
////
////					@Override
////					public void onTimeOut() {
////
////						toast(" Connection Timeout");
////						mediacontroller_next.setEnabled(true);
////						mediacontroller_prev.setEnabled(true);
////						mediacontroller_play_pause.setEnabled(true);
////						mediacontroller_song_cover.setEnabled(true);
////						song_name_artist_ll.setEnabled(true);
////						if (callback != null)
////							callback.onTimeOut();
////					}
////
////					@Override
////					public void onNoInet() {
////						toast("No Internet Connetion");
////
////						mediacontroller_next.setEnabled(true);
////						mediacontroller_prev.setEnabled(true);
////						mediacontroller_play_pause.setEnabled(true);
////						mediacontroller_song_cover.setEnabled(true);
////						song_name_artist_ll.setEnabled(true);
////						if (callback != null)
////							callback.onNoInet();
////					}
////
////					@Override
////					public void onFinished(String result) {
////
////						try {
////							ZdParser parser = new ZdParser(result);
////							if (parser.code == 200) {
////								Song res = gson.fromJson(parser.response,
////										Song.class);
////								if (res != null) {
////									s.rate = res.rate;
////									s.likeState = res.likeState;
////									s.url = res.url;
////									s.urls = res.urls;
////									if (OnlineCkeck.isOnline(MainActivity.this)) {
////										mediacontroller_next.setEnabled(true);
////										mediacontroller_prev.setEnabled(true);
////										if (s.canTryNextUrl()) {
////											toast("play song");
////											playSong(s);
////										} else {
////											toast("no more links");
////										}
////										if (callback != null)
////											callback.onFinished(result);
////									} else {
////
////										toast("no internet connection");
////									}
////								}
////							} else {
////								toast(parser.response);
////							}
////						} catch (Exception e) {
////							// TODO: handle exception
////							e.printStackTrace();
////							toast("Error can't get song urls");
////						}
////					}
////				});
//			}
//		});
//	}

	/**
	 * <p>
	 * connect to player to see of there's current played song or not
	 * </p>
	 * <p>
	 * if there then we shouldn't play given song , play it otherwise
	 * </p>
	 * 
	 * @param s
	 *            the song that we wanna to play it
	 */
	public void playSong(final Song s) {
		SimpleExoPlayer player  =  playerBinder.initializePlayer(s.getSongURL());
		playerView.setPlayer(player);

//		if (player == null) {
//			return;
//		}
//		if (invalidUrls != null && invalidUrls.size() > 0) {
//			invalidUrls.clear();
//		}
//		player.connectPlayer(new OutputCommand() {
//
//			@Override
//			public void connected(Output output) {
//
//
//				if (output.getCurrSong() == null) {
//					if (mediacontroller_ll.getVisibility() == View.GONE) {
//						playedSong = s;
//						mediacontroller_ll.setVisibility(View.VISIBLE);
//					}
//				}
//
//				Log.i("MainAcitvity", "play:" + s.file);
//				if (s.file != null) {
//					mediacontroller_msg.setText((s.current_url_indx + 1)
//							+ " of " + s.urls.size());
//				}
//				output.play(s);
//			}
//		});
	}

//	public void playURL(DownloadLink link, boolean isSong) {
//		if (player == null) {
//			return;
//		}
//		if (invalidUrls != null && invalidUrls.size() > 0) {
//			invalidUrls.clear();
//		}
//
//		final Song s = new Song();
//
//		s.SID = link.id;
//		s.current_url_indx = -1;
//		s.link = link;
//		s.isSong = isSong;
//		if (link.downloaded_path != null) {
//			File songFile = new File(link.downloaded_path);
//			if (songFile.isFile()) {
//				s.url = link.downloaded_path;
//			} else {
//				s.url = link.url;
//			}
//		} else {
//			s.url = link.url;
//		}
//		s.SName = link.title;
//		if (link.artist != null && !link.artist.equals("")) {
//			s.Artist = link.artist;
//		}
//		player.connectPlayer(new OutputCommand() {
//
//			@Override
//			public void connected(Output output) {
//
//
//				Log.i("MainAcitvity", "play:" + s.url);
//				if (output.getCurrSong() == null) {
//					if (mediacontroller_ll.getVisibility() == View.GONE) {
//						playedSong = s;
//						mediacontroller_ll.setVisibility(View.VISIBLE);
//					}
//				}
//				Log.i("MainAcitvity", "play:" + s.url);
//				output.play(s);
//			}
//		});
//	}

	/*
	 * public void changesSong(final Song s) { if (player == null) {return;}
	 * player.connectPlayer(new OutputCommand() {
	 * 
	 * @Override public void connected(Output output) { // TODO Auto-generated
	 * method stub Log.i("MainAcitvity", "change:"+s.url); if
	 * (output.getCurrSong()==null){ if (mediacontroller_ll.getVisibility() ==
	 * View.GONE) { playedSong = s;
	 * mediacontroller_ll.setVisibility(View.VISIBLE); } } if
	 * (s.urls!=null&&s.urls.size()>0){
	 * mediacontroller_msg.setText((s.current_url_indx+1)+" of "+s.urls.size());
	 * } output.change(s); } }); }
	 */

//	public Song runPlayerForNextUrl(Song s) {
//		// Log.e("zfragment",gson.toJson(s));
//		if (s.canTryNextUrl()) {
//			playSong(s);
//		} else {
//			toast("no more links");
//		}
//		return s;
//	}

//	public Song runPlayerForPrevUrl(Song s) {
//		// Log.e("zfragment",gson.toJson(s));
//		if (s.canTryPrevUrl()) {
//			playSong(s);
//		} else {
//			toast("no more links");
//		}
//		return s;
//	}

	@Override
	public void afterTextChanged(Editable arg0) {
		

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context ctx, Intent intent) {
			
			handleIntent(intent);
		}

		private void handleIntent(Intent intent) {

			if (intent != null
					&& intent.getAction().equals("mp3ready.ui.ListFragment")) {
				int type = intent.getIntExtra(MyIntents.TYPE, -1);
				String url;
				String title;

				switch (type) {
				case MyIntents.Types.ADD:

					url = intent.getStringExtra(MyIntents.URL);
					title = intent.getStringExtra(MyIntents.TITLE);
					// String artist = intent.getStringExtra(MyIntents.ARTIST);
					String progress = intent
							.getStringExtra(MyIntents.PROCESS_PROGRESS);
					boolean isPaused = intent.getBooleanExtra(
							MyIntents.IS_PAUSED, false);
					String error = intent.getStringExtra(MyIntents.ERROR_CODE);
					Log.i(TAG, "add task:" + title + ":" + url + ":" + progress
							+ ":" + isPaused);
					if (!TextUtils.isEmpty(url)) {
						download_adapter.addItem(url, title, progress,
								isPaused, error);
						// gotoFragment(ListFragment.newInstance(ListFragment.DOWNLOADS_MANAGER),
						// ListFragment.TAG);
					}
					break;
				case MyIntents.Types.COMPLETE:

					url = intent.getStringExtra(MyIntents.URL);
					// String path = intent.getStringExtra(MyIntents.PATH);
					download_adapter.notifyDataSetChanged();

					break;
				case MyIntents.Types.PROCESS:

					url = intent.getStringExtra(MyIntents.URL);
					// title= intent.getStringExtra(MyIntents.TITLE);
					// Log.i(TAG, "process"+title+":"+url);
					String porgress = intent
							.getStringExtra(MyIntents.PROCESS_PROGRESS);
					if (Integer.valueOf(porgress) >= 0) {
						download_adapter
								.updateItem(url, porgress, porgress, "");
					}

					break;
				case MyIntents.Types.ERROR:
					url = intent.getStringExtra(MyIntents.URL);
					// download_adapter.removeItem(url);
					download_adapter.updateItem(url, "0", "0", "Error");
					break;
				case MyIntents.Types.RETRY:
					url = intent.getStringExtra(MyIntents.URL);
					download_adapter.updateItem(url, "0", "0", "");
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * select current section and unselect the rest
	 * 
	 * @param section
	 *            the section that we click on it
	 */
	private void syncSectionsState(MaterialSection section) {
		

		// search in first list
		int position = sectionList.indexOf(section);
		if (position != -1) {
			for (int i = 0; i < sectionList.size(); i++)
				if (i != position)
					sectionList.get(i).unSelect();
		}
	}

	/**
	 * <p>
	 * go to new section by clicking on drawer section item
	 * </p>
	 * <p>
	 * remove all childs fragments
	 * </p>
	 * 
	 * @param section
	 *            the section that we wanna to go to it
	 */
	public void setSection(MaterialSection section,boolean isChild) {
		section.select();
//		drawer.closeDrawer(mLDrawerContainer);
		if (mediacontroller_ll.getVisibility() == View.GONE) {
			if (playedSong != null) {
				mediacontroller_ll.setVisibility(View.VISIBLE);
			}
		}
		if (section == currentSection) {
			return;
		}
		gotoFragment(section.getTargetFragment(), null, isChild);
		if (!isChild){
			afterSectionFragmentSetted();
			currentSection = section;
		}
		syncSectionsState(section);

	}

	@Override
	public void onClick(MaterialSection section) {
		
//		if (section.getTitle().equals("Login")){
//			section.setTarget(LoginFragment
//					.newInstance(new NewApiCalls.Callback() {
//
//						@Override
//						public void onFinished(String response) {
//
//							User mUser = gson.fromJson(response,
//									User.class);
//							if (mUser != null) {
//
//								mem.setAppAuthed(true);
//								Log.e("MainActivity", "authKey:"
//										+ mUser.auth_key);
//								mem.setAuthKey(mUser.auth_key);
//
//								if (!getSupportActionBar().isShowing())
//									getSupportActionBar().show();
//								getCurrentSection().select();
//								MainActivity.this.onBackPressed();
//
//								for (int i = 0; i < mNavTitles.length; i++) {
//									addSectionVisibilityIfAppAuthed(
//											i, mNavTitles[i]);
//								}
//							}
//
//						}
//					}));
//			setSection(section, false);//is not child
//
//		}else if (section.getTitle().equals("Logout")) {
//			mem.setAppAuthed(false);
//			mem.setAuthKey(null);
//			home_page_choosed_top_genre = null;
//			home_page_recommended_songs = null;
//			home_page_top_genre_songs = null;
//			currentSection = null;
//			getSectionByTitle(Enums.HOME_SCREEN_POS).setTarget(HomeFragment.newInstance());
//			setSection(getSectionByTitle(Enums.HOME_SCREEN_POS),false);//is not child
//			for (int i = 0; i < mNavTitles.length; i++) {
//				addSectionVisibilityIfAppNotAuthed(i, mNavTitles[i]);
//			}
//		}else {
//			setSection(section,false); // is not child
//		}
		
	}


	public void log(String log)
	{
		Log.d("Debug",getClass().getSimpleName()+": "+log);
	}
	public void snack(String message)
	{
		Snackbar.make(findViewById(R.id.content), message, Snackbar.LENGTH_LONG)
				.setAction("Action", null).show();
	}

	public void snack(HttpAPI.ErrorMessage message)
	{
		Snackbar.make(findViewById(R.id.content), "Check your internet connection", Snackbar.LENGTH_LONG)
				.setAction("Action", null).show();
	}


	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection playerConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service) {
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			playerBinder = (PlayerService.PlayerBinder) service;

			playerView.setPlayer(playerBinder.getPlayer());
//			binder
//			mService = binder.getService();
//			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			playerBinder = null;
//			mBound = false;
		}
	};


}