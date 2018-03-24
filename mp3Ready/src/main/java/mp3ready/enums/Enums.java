package mp3ready.enums;

public class Enums {

	public final static int HOME_SCREEN_POS = 0;
	public final static int RECOMMENDED_SCREEN_POS = 2;
	public final static int RECOMMENDED_BY_GENRE_SCREEN_POS = 3;
	public final static int PLAYLIST_DOWNLOADS_SCREEN_POS = 5;
	public final static int PLAYLIST_RECENTLY_SCREEN_POS = 6;
	public final static int PLAYLIST_PLS_SCREEN_POS = 7;
	public final static int PLAYLIST_FROM_FRIENDS_SCREEN_POS = 8;
	public final static int PLAYLIST_LIKES_SCREEN_POS = 9;
	public final static int REDEFINE_PROF_SCREEEN_POS = 11;
	public final static int APP_LOGIN_SCREEEN_POS = 13;
	public final static int APP_LOGOUT_SCREEEN_POS = 14;

	public final static int ITEMS_IN_PAGE = 30;// for pagination
	public final static int SONGS_IN_SECTION = 10; // in home screen we render
													// 10 songs in each section

	public final static int LIKE_STATE = 1;
	public final static int DISLIKE_STATE = -1;

	public final static int PLAYLIST_DOWNLOAD_LIST = 1;
	public final static int PLAYLIST_RECENTLY_PLAYED_LIST = 2;
	public final static int PLAYLIST_DOWNLOAD_RECENTLY_PLAYED_LIST = 3;

	public final static String PLAYLIST_DOWNLOADED_NAME = "Downloaded";
	public final static String PLAYLIST_RECENTLY_PLAYED_NAME = "Recently Played";

	public final static String PLAYLIST_USER_DEFINE_TYPE = "1";
	public final static int DOWNLOAD_STATE_PAUSED = 0;
	public final static int DOWNLOAD_STATE_ERROR = 1;
	public final static int DOWNLOAD_STATE_COMPLETE = 2;

	public final static int ACTION_ADD_LIST_ITEM = 1; // extra_int_info =
														// list_id // should be
														// smae to the
														// ACTION_PIN
	public final static int ACTION_LISTEN = 2; // extra_int_info = order
	public final static int ACTION_RATE = 3;// extra_int_info = rate_value
	public final static int ACTION_SET_LIKE_STATE = 4; // extra_int_info = (1
														// ==> LIKE , -1 ==>
														// DISLIKE
	public final static int ACTION_RECOMMEND_SONG_TO_FRIEND = 8; // extra_int_info
																	// =
																	// friend_id//
																	// ACTION_PUSH_PROFILE
	public final static int ACTION_REMOVE_LIST_ITEM = 9; // should be same to
															// the ACTION_UNPIN
	public final static int ACTION_RECOMMEND_LIST_TO_FRIEND = 10; // ACTION_PUSH_DRAWER_TOFRIEND
																	// =
																	// 10;//'6';
	public final static int ACTION_REPORT = 13;
	public final static int ACTION_SEARCH = 14;
	public final static int ACTION_PIN_SONG_URL = 15;
	public final static int ACTION_DOWNLAOD = 16;
	public final static int ACTION_CHANGE_RECOMMENED_SONG_ORDER = 17;

	public final static int CUSTOME_EVENT_PLAY = 1;
	public final static int CUSTOME_EVENT_PAUSE = 0;

}
