package mp3ready.api;

import android.content.Context;
import android.telecom.Call;
import android.util.Log;
import android.widget.ListView;

import com.zgm.zlib.http.HttpAPI;
import com.zgm.zlib.util.PrefUtil;

import java.util.HashMap;

import mp3ready.enums.Enums;
import mp3ready.ui.MainActivity;
import mp3ready.util.AppCache;

/**
 * Created by Saad on 2/20/2018.
 */

public class NewApiCalls {

    private String TAG = getClass().getSimpleName();

    public HttpAPI api;

    private String baseUrl = "http://172.93.148.138/a/mmusic/admin/api/";

    private String login = "login";
    private String facebook_login = "facebook-login";
    private String new_account = "new-account";
    private String action = "action?auth_key=";
    //	private String URL_RECOMMENDED_BY_GENRE = "recommendedByGenre?auth_key=";
    private String user_recommended = "user-recommended?auth_key=";
    private String top_tags_songs = "top-tags-songs";
    private String search = "search";
    //	private String URL_SEARCH_URLS = "searchUrls";
//    private String URL_GET_SONG_URLS = "GetMP3Urls";
//    private String URL_DOWNLOAD_SONG = "download";
    private String song = "song";
    private String artist_songs = "artist-songs";
    private String user_lists = "user-lists?auth_key=";

    private String list_create = "list-create?auth_key=";
    private String list_delete = "list-delete?auth_key=";
    private String list_update = "list-update?auth_key=";
    private String list_view = "list-view?auth_key=";
//	private String URL_REPORT_URLS = "reportUrl";
//	private String URL_PLAYABLE_URLS = "playableUrl";

    private Context context;
    private static AppCache mem;


    public NewApiCalls(Context context) {
        api = new HttpAPI(context,baseUrl,30,true,true);
        PrefUtil.createSharedPreference(context);
        mem = new AppCache(context);

        this.context = context;
    }

    public void login(String json, Callback2 callback) {
        api.post(login,json,convertCallbackNoCache(callback));
//        apiPOST(login, BASE_URL, json, callback, false);
    }

    public void loginFaceBook(String json, Callback callback) {
        api.post(facebook_login,json,convertCallbackNoCache(callback));
//        apiPOST(facebook_login, BASE_URL, json, callback, false);
    }

    public void signUp(String json, Callback2 callback) {
        api.post(new_account,json,convertCallbackNoCache(callback));
//        apiPOST(new_account, BASE_URL, json, callback, false);
    }

    public String buildActionListenJson(String song_id) {
        return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
                + Enums.ACTION_LISTEN + "\"}";
    }

    public String buildActionPinSongUrlJson(String song_id, String url) {
        return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
                + Enums.ACTION_PIN_SONG_URL + "\",\"url\":\"" + url + "\"}";
    }

    public String buildActionRateJson(String song_id, int rate_value) {
        return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
                + Enums.ACTION_RATE + "\",\"rate_value\":\"" + rate_value
                + "\"}";
    }

    public String buildActionRecommendToFriendJson(String song_id,
                                                          int friend_id) {
        return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
                + Enums.ACTION_RECOMMEND_SONG_TO_FRIEND + "\",\"friend_id\":\""
                + friend_id + "\"}";
    }

    public String buildActionLikeStateJson(String song_id, int like_state) {
        return "{\"song_id\":\"" + song_id + "\",\"action_id\":\""
                + Enums.ACTION_SET_LIKE_STATE + "\",\"like_state\":\""
                + like_state + "\"}";
    }

    public String buildActionAddSongToListJson(String song_id, String list_id) {
        return "{\"action_id\":\"" + 1 + "\",\"song_id\":\""
                + song_id+ "\",\"list_id\":\"" + list_id
                + "\"}";
    }

    public String buildActionRemoveSongFromListJson(String songId, String listId) {
        return "{\"action_id\":\"" + 9 + "\",\"song_id\":\""
                + songId
                + "\",\"list_id\":\"" + listId + "\"}";
    }

    public void search(String json, int page, Callback2 callback) {
        if (mem.getAuthKey() != null) {
            api.post(search + "?auth_key=" + mem.getAuthKey() + "&page=" + page,json,convertCallbackNoCache(callback));
//            apiPOST(search + "?auth_key=" + mem.login() + "&page="
//                    + page, BASE_URL, json, callback, false);
        } else {
            api.post(search + "?page=" + page,json,convertCallbackNoCache(callback));
//            apiPOST(search + "?page=" + page, BASE_URL, json, callback,
//                    false);
        }
    }

    public void artistSongs(String json, int page, Callback callback) {
        if (mem.getAuthKey() != null) {
            api.post(artist_songs + "?auth_key=" + mem.getAuthKey() + "&page=" + page,json,convertCallback(callback));
//            apiPOST(artist_songs + "?auth_key=" + mem.login()
//                    + "&page=" + page, BASE_URL, json, callback, true);
        } else {
            api.post(artist_songs + "?page=" + page,json,convertCallback(callback));
//            apiPOST(artist_songs + "?page=" + page, BASE_URL, json,
//                    callback, true);
        }
    }

//	public  void searchUrls(String json, CallBack callback) {
//		if (mem.login() != null) {
//			apiPOST(URL_SEARCH_URLS + "?auth_key=" + mem.login(), BASE_URL,
//					json, callback, false);
//		} else {
//			apiPOST(URL_SEARCH_URLS, BASE_URL, json, callback, false);
//		}
//	}

    public void doAction(String json, Callback callback) {
        api.post(action + mem.getAuthKey(),json,convertCallbackNoCache(callback));
//        apiPOST(action + mem.login(), BASE_URL, json, callback, false);
    }

//	public  void getRecommendByGenre(String json, CallbackHandler callback) {
//		apiPOST(URL_RECOMMENDED_BY_GENRE + mem.login(), BASE_URL, json,
//				callback, true);
//	}

//    public void getMp3SongsUrls(String json, Callback callback) {
//        if (mem.getAuthKey() != null) {
//            api.post(URL_GET_SONG_URLS + "?auth_key=" + mem.getAuthKey(),json,convertCallbackNoCache(callback));
////            apiPOST(URL_GET_SONG_URLS + "?auth_key=" + mem.login(),
////                    BASE_URL, json, callback, false);
//        } else {
//            api.post(URL_GET_SONG_URLS,json,convertCallbackNoCache(callback));
////            apiPOST(URL_GET_SONG_URLS, BASE_URL, json, callback, false);
//        }
//    }

    public void getPlayLists(String json, Callback callback) {
        api.post(user_lists + mem.getAuthKey(),json,convertCallback(callback));
//        apiPOST(user_lists + mem.login(), BASE_URL, json, callback,
//                true);
    }

    public void getListSongs(String listId, Callback callback)
    {
        String payload = "{ \"list_id\":"+listId+"}";
        api.post(list_view+ mem.getAuthKey(),payload,convertCallback(callback));
    }



    public void getSongDetials(String json, Callback callback) {
        if (mem.getAuthKey() != null) {
            api.post(song + "?auth_key=" + mem.getAuthKey(),json,convertCallbackNoCache(callback));
//            apiPOST(song + "?auth_key=" + mem.login(),
//                    BASE_URL, json, callback, false);
        } else {
            api.post(song,json,convertCallbackNoCache(callback));
//            apiPOST(song, BASE_URL, json, callback, false);
        }
    }

    public void getRecommendedSongs(String json, Callback callBack) {
        api.post(user_recommended + mem.getAuthKey(),json,convertCallback(callBack));
//        apiPOST(user_recommended + mem.login(), BASE_URL, json, callBack,
//                true);
    }

    public void createPlayList(String json, Callback callBack) {
        api.post(list_create + mem.getAuthKey(),json,convertCallbackNoCache(callBack));
//        apiPOST(list_create + mem.login(), BASE_URL, json,
//                callBack, false);
    }

    public void deletePlayList(String json, Callback callBack) {
        api.post(list_delete + mem.getAuthKey(),json,convertCallbackNoCache(callBack));
//        apiPOST(list_delete + mem.login(), BASE_URL, json,
//                callBack, false);
    }

    public void updatePlayList(String json, Callback callBack) {
        api.post(list_update + mem.getAuthKey(),json,convertCallbackNoCache(callBack));
//        apiPOST(list_update + mem.login(), BASE_URL, json,
//                callBack, false);
    }

    public void playListView(String json, Callback callBack) {
        api.post(list_view + mem.getAuthKey(),json,convertCallback(callBack));
//        apiPOST(list_view + mem.login(), BASE_URL, json, callBack,
//                true);
    }

//	public  void reportURLS(String json, CallbackHandler callBack) {
//		if (mem.login() != null) {
//			apiPOST(URL_REPORT_URLS + "?auth_key=" + mem.login(), BASE_URL,
//					json, callBack, false);
//		} else {
//			apiPOST(URL_REPORT_URLS, BASE_URL, json, callBack, false);
//		}
//	}

    public void getTopTagsSongs(String json, Callback callBack) {
        api.post(top_tags_songs,json,convertCallback(callBack));
//        apiPOST(top_tags_songs, BASE_URL, json, callBack, true);
    }

//    public void download(String json, Callback callBack) {
//        if (mem.getAuthKey() != null) {
//            api.post(URL_DOWNLOAD_SONG + "?auth_key=" + mem.getAuthKey(),json,convertCallbackNoCache(callBack));
////            apiPOST(URL_DOWNLOAD_SONG + "?auth_key=" + mem.login(),
////                    BASE_URL, json, callBack, false);
//        } else {
//            api.post(URL_DOWNLOAD_SONG,json,convertCallbackNoCache(callBack));
////            apiPOST(URL_DOWNLOAD_SONG, BASE_URL, json, callBack, false);
//        }
//    }

    private com.zgm.zlib.http.Callback convertCallback(final Callback callback)
    {
        final MainActivity activity = (MainActivity)context;
        activity.setSupportProgressBarIndeterminateVisibility(true);
//        final String name = activity.navigator.getCurrentFragment().getClass().getSimpleName();
        com.zgm.zlib.http.Callback cb = new com.zgm.zlib.http.Callback() {
            @Override
            public void onCached(String response) {
                try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
            }

            @Override
            public void onFinished(String response, HashMap<String, String> headers) {
                activity.log(response);
                try
                {
                    if(activity.navigator.getCurrentFragment().getView() != null
                            && activity.navigator.getCurrentFragment().isAdded())
                    {
                        activity.setSupportProgressBarIndeterminateVisibility(false);
                        try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
                    }
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onProblem(HttpAPI.ErrorMessage errorMessage, String cachedResponse) {
                activity.setSupportProgressBarIndeterminateVisibility(false);
                activity.snack(errorMessage);
            }
        };
        return cb;
    }

    private com.zgm.zlib.http.Callback convertCallback(final Callback3 callback)
    {
        final MainActivity activity = (MainActivity)context;
        activity.setSupportProgressBarIndeterminateVisibility(true);
        final String name = activity.navigator.getCurrentFragment().getClass().getSimpleName();
        com.zgm.zlib.http.Callback cb = new com.zgm.zlib.http.Callback() {
            @Override
            public void onCached(String response) {
                try{callback.onCached(response);}catch (Exception e){activity.log(e.getMessage());}
            }

            @Override
            public void onFinished(String response, HashMap<String, String> headers) {
                activity.log(response);

                try
                {
                    if(activity.navigator.getCurrentFragment().getView() != null
                            && activity.navigator.getCurrentFragment().isAdded()
                            && activity.navigator.getCurrentFragment().getClass().getSimpleName().equals(name))
                    {
                        activity.setSupportProgressBarIndeterminateVisibility(false);
                        try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
                    }
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void onProblem(HttpAPI.ErrorMessage errorMessage, String cachedResponse) {
                activity.setSupportProgressBarIndeterminateVisibility(false);
                activity.snack(errorMessage);
            }
        };
        return cb;
    }

    private com.zgm.zlib.http.Callback convertCallback(final Callback2 callback)
    {
        final MainActivity activity = (MainActivity)context;
        activity.setSupportProgressBarIndeterminateVisibility(true);
        final String name = activity.navigator.getCurrentFragment().getClass().getSimpleName();
        activity.log("FN : "+name);
        com.zgm.zlib.http.Callback cb = new com.zgm.zlib.http.Callback() {
            @Override
            public void onCached(String response) {
                try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
            }

            @Override
            public void onFinished(String response, HashMap<String, String> headers) {
                activity.log(response);

                activity.log("FN : "+activity.navigator.getCurrentFragment().getClass().getSimpleName());
                if(activity.navigator.getCurrentFragment().getView() != null
                        && activity.navigator.getCurrentFragment().isAdded()
                        && activity.navigator.getCurrentFragment().getClass().getSimpleName().equals(name))
                {
                    activity.setSupportProgressBarIndeterminateVisibility(false);
                    try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
                }
            }

            @Override
            public void onProblem(HttpAPI.ErrorMessage errorMessage, String cachedResponse) {
                activity.setSupportProgressBarIndeterminateVisibility(false);
                activity.snack(errorMessage);
                try{callback.onProblem();}catch (Exception e){activity.log(e.getMessage());}

            }
        };
        return cb;
    }

    private com.zgm.zlib.http.CallbackNoCache convertCallbackNoCache(final Callback callback)
    {
        final MainActivity activity = (MainActivity)context;
        activity.setSupportProgressBarIndeterminateVisibility(true);
//        final String name = activity.navigator.getCurrentFragment().getClass().getSimpleName();
//        activity.log("FN : "+name);
        com.zgm.zlib.http.CallbackNoCache cb = new com.zgm.zlib.http.CallbackNoCache() {
            @Override
            public void onFinished(String response, HashMap<String, String> headers) {
                activity.log(response);

                activity.log("FN : "+activity.navigator.getCurrentFragment().getClass().getSimpleName());
                if(activity.navigator.getCurrentFragment().getView() != null
                        && activity.navigator.getCurrentFragment().isAdded())
                {
                    activity.setSupportProgressBarIndeterminateVisibility(false);
                    try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
                }
            }

            @Override
            public void onProblem(HttpAPI.ErrorMessage errorMessage) {
                activity.setSupportProgressBarIndeterminateVisibility(false);
                activity.snack(errorMessage);
            }
        };
        return cb;
    }

    private com.zgm.zlib.http.CallbackNoCache convertCallbackNoCache(final Callback2 callback)
    {
        final MainActivity activity = (MainActivity)context;
//        activity.setSupportProgressBarIndeterminateVisibility(true);
        final String name = activity.navigator.getCurrentFragment().getClass().getSimpleName();
        Log.d(TAG, "convertCallbackNoCache: " + name);
        com.zgm.zlib.http.CallbackNoCache cb = new com.zgm.zlib.http.CallbackNoCache() {
            @Override
            public void onFinished(String response, HashMap<String, String> headers) {
                activity.log(response);
                if(activity.navigator.getCurrentFragment().getView() != null
                        && activity.navigator.getCurrentFragment().isAdded()
                        && activity.navigator.getCurrentFragment().getClass().getSimpleName().equals(name))
                {
                    activity.setSupportProgressBarIndeterminateVisibility(false);
                    try{callback.onFinished(response);}catch (Exception e){activity.log(e.getMessage());}
                }
            }

            @Override
            public void onProblem(HttpAPI.ErrorMessage errorMessage) {
                activity.setSupportProgressBarIndeterminateVisibility(false);
                activity.snack(errorMessage);
                try{callback.onProblem();}catch (Exception e){activity.log(e.getMessage());}
            }
        };
        return cb;
    }

    public interface Callback {
        void onFinished(String response);
    }

    public interface Callback2 {
        void onFinished(String response);
        void onProblem();
    }

    public interface Callback3 {
        void onCached(String response);
        void onFinished(String response);
    }


}
