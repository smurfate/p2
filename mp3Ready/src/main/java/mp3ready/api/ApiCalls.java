//package mp3ready.api;
//
//import mp3ready.enums.Enums;
//import mp3ready.util.AppCache;
//import zgm.http.CallBack;
//import zgm.http.HttpConn;
//import zgm.http.HttpConnTask;
//import android.content.Context;
//import android.os.AsyncTask;
//
//public class ApiCalls {
//
//	static String authkey = "";
//	public final static String SERVER_IP = "172.93.148.138";
//	public final static String HOST = SERVER_IP;
//	public final static String HOST_URL = "http://" + HOST + "/";
//
//	public final static String BASE_URL = "http://" + HOST
//			+ "/a/mmusic/admin/api/";
//	private static AppCache mem;
//
//	public static final String URL_LOGIN = "login";
//	public static final String URL_LOGIN_BY_FB = "facebook-login";
//	public static final String URL_SIGNUP = "new-account";
//	public static final String URL_ACTION = "action?auth_key=";
//	//	public static final String URL_RECOMMENDED_BY_GENRE = "recommendedByGenre?auth_key=";
//	public static final String URL_RECOMMENDED = "user-recommended?auth_key=";
//	public static final String URL_TOP_TAGS_SONGS = "top-tags-songs";
//	public static final String URL_SEARCH = "search";
//	//	public static final String URL_SEARCH_URLS = "searchUrls";
//	public static final String URL_GET_SONG_URLS = "GetMP3Urls";
//	public static final String URL_DOWNLOAD_SONG = "download";
//	public static final String URL_SONG_DETIALS = "song";
//	public static final String URL_ARTIST_SONGS = "artist-songs";
//	public static final String URL_PLAY_LISTS = "user-lists?auth_key=";
//	public static final String URL_CREATE_PLAY_LIST = "list-create?auth_key=";
//	public static final String URL_DELETE_PLAY_LIST = "list-delete?auth_key=";
//	public static final String URL_UPDATE_PLAY_LIST = "list-update?auth_key=";
//	public static final String URL_LIST_VIEW = "list-view?auth_key=";
////	public static final String URL_REPORT_URLS = "reportUrl";
////	public static final String URL_PLAYABLE_URLS = "playableUrl";
//
//	private static Context ctx;
//
//	public static void setContext(Context ctx) {
//		ApiCalls.ctx = ctx;
//		mem = new AppCache(ctx);
//	}
//
//	private static void apiPOST(String url, String base_url, String json,
//								CallBack callBack, boolean saveInCach) {
//		url = base_url + url;
//		HttpConnTask task = new HttpConnTask(ctx, SERVER_IP, HttpConn.POST,
//				url, json, callBack);
//		// task.setComputeBandwidth(true);
//		if (saveInCach) {
//			task.setSaveInCache(true);
//			task.FetchRequestFromCacheIfNoConn(true);
//		}
//		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);// to let the
//		// Async Tasks
//		// execute in
//		// parallel
//	}
//
//	public static void getAuthKey(String json, CallbackHandler callback) {
//		apiPOST(URL_LOGIN, BASE_URL, json, callback, false);
//	}
//
//	public static void getAuthKeyByFB(String json, CallbackHandler callback) {
//		apiPOST(URL_LOGIN_BY_FB, BASE_URL, json, callback, false);
//	}
//
//	public static void signUp(String json, CallbackHandler callback) {
//		apiPOST(URL_SIGNUP, BASE_URL, json, callback, false);
//	}
//
//	public static String buildActionListenJson(String song_id) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_LISTEN + "\"}";
//	}
//
//	public static String buildActionPinSongUrlJson(String song_id, String url) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_PIN_SONG_URL + "\",\"url\":\"" + url + "\"}";
//	}
//
//	public static String buildActionRateJson(String song_id, int rate_value) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_RATE + "\",\"rate_value\":\"" + rate_value
//				+ "\"}";
//	}
//
//	public static String buildActionRecommendToFriendJson(String song_id,
//														  int friend_id) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_RECOMMEND_SONG_TO_FRIEND + "\",\"friend_id\":\""
//				+ friend_id + "\"}";
//	}
//
//	public static String buildActionLikeStateJson(String song_id, int like_state) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_SET_LIKE_STATE + "\",\"like_state\":\""
//				+ like_state + "\"}";
//	}
//
//	public static String buildActionAddSongToListJson(String song_id,
//													  String list_id) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_ADD_LIST_ITEM + "\",\"list_id\":\"" + list_id
//				+ "\"}";
//	}
//
//	public static String buildActionRemoveSongFromListJson(String song_id,
//														   String song_action_id) {
//		return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
//				+ Enums.ACTION_REMOVE_LIST_ITEM
//				+ "\",\"action_id_to_be_removed\":\"" + song_action_id + "\"}";
//	}
//
//	public static void search(String json, int page, CallBack callback) {
//		if (mem.getAuthKey() != null) {
//			apiPOST(URL_SEARCH + "?auth_key=" + mem.getAuthKey() + "&page="
//					+ page, BASE_URL, json, callback, false);
//		} else {
//			apiPOST(URL_SEARCH + "?page=" + page, BASE_URL, json, callback,
//					false);
//		}
//	}
//
//	public static void artistSongs(String json, int page,
//								   CallbackHandler callback) {
//		if (mem.getAuthKey() != null) {
//			apiPOST(URL_ARTIST_SONGS + "?auth_key=" + mem.getAuthKey()
//					+ "&page=" + page, BASE_URL, json, callback, true);
//		} else {
//			apiPOST(URL_ARTIST_SONGS + "?page=" + page, BASE_URL, json,
//					callback, true);
//		}
//	}
//
////	public static void searchUrls(String json, CallBack callback) {
////		if (mem.getAuthKey() != null) {
////			apiPOST(URL_SEARCH_URLS + "?auth_key=" + mem.getAuthKey(), BASE_URL,
////					json, callback, false);
////		} else {
////			apiPOST(URL_SEARCH_URLS, BASE_URL, json, callback, false);
////		}
////	}
//
//	public static void doAction(String json, CallbackHandler callback) {
//		apiPOST(URL_ACTION + mem.getAuthKey(), BASE_URL, json, callback, false);
//	}
//
////	public static void getRecommendByGenre(String json, CallbackHandler callback) {
////		apiPOST(URL_RECOMMENDED_BY_GENRE + mem.getAuthKey(), BASE_URL, json,
////				callback, true);
////	}
//
//	public static void getMp3SongsUrls(String json, CallBack callback) {
//		if (mem.getAuthKey() != null) {
//			apiPOST(URL_GET_SONG_URLS + "?auth_key=" + mem.getAuthKey(),
//					BASE_URL, json, callback, false);
//		} else {
//			apiPOST(URL_GET_SONG_URLS, BASE_URL, json, callback, false);
//		}
//	}
//
//	public static void getPlayLists(String json, CallbackHandler callback) {
//		apiPOST(URL_PLAY_LISTS + mem.getAuthKey(), BASE_URL, json, callback,
//				true);
//	}
//
//	public static void getSongDetials(String json, CallbackHandler callback) {
//		if (mem.getAuthKey() != null) {
//			apiPOST(URL_SONG_DETIALS + "?auth_key=" + mem.getAuthKey(),
//					BASE_URL, json, callback, false);
//		} else {
//			apiPOST(URL_SONG_DETIALS, BASE_URL, json, callback, false);
//		}
//	}
//
//	public static void getRecommendedSongs(String json, CallbackHandler callBack) {
//		apiPOST(URL_RECOMMENDED + mem.getAuthKey(), BASE_URL, json, callBack,
//				true);
//	}
//
//	public static void createPlayList(String json, CallbackHandler callBack) {
//		apiPOST(URL_CREATE_PLAY_LIST + mem.getAuthKey(), BASE_URL, json,
//				callBack, false);
//	}
//
//	public static void deletePlayList(String json, CallbackHandler callBack) {
//		apiPOST(URL_DELETE_PLAY_LIST + mem.getAuthKey(), BASE_URL, json,
//				callBack, false);
//	}
//
//	public static void updatePlayList(String json, CallbackHandler callBack) {
//		apiPOST(URL_UPDATE_PLAY_LIST + mem.getAuthKey(), BASE_URL, json,
//				callBack, false);
//	}
//
//	public static void playListView(String json, CallbackHandler callBack) {
//		apiPOST(URL_LIST_VIEW + mem.getAuthKey(), BASE_URL, json, callBack,
//				true);
//	}
//
////	public static void reportURLS(String json, CallbackHandler callBack) {
////		if (mem.getAuthKey() != null) {
////			apiPOST(URL_REPORT_URLS + "?auth_key=" + mem.getAuthKey(), BASE_URL,
////					json, callBack, false);
////		} else {
////			apiPOST(URL_REPORT_URLS, BASE_URL, json, callBack, false);
////		}
////	}
//
//	public static void getTopTagsSongs(String json, CallbackHandler callBack) {
//		apiPOST(URL_TOP_TAGS_SONGS, BASE_URL, json, callBack, true);
//	}
//
//	public static void download(String json, CallbackHandler callBack) {
//		if (mem.getAuthKey() != null) {
//			apiPOST(URL_DOWNLOAD_SONG + "?auth_key=" + mem.getAuthKey(),
//					BASE_URL, json, callBack, false);
//		} else {
//			apiPOST(URL_DOWNLOAD_SONG, BASE_URL, json, callBack, false);
//		}
//	}
//
////	public static void sendPlayableUrls(String json, CallbackHandler callBack) {
////		if (mem.getAuthKey() != null) {
////			apiPOST(URL_PLAYABLE_URLS + "?auth_key=" + mem.getAuthKey(),
////					BASE_URL, json, callBack, false);
////		} else {
////			apiPOST(URL_PLAYABLE_URLS, BASE_URL, json, callBack, false);
////		}
////	}
//}
