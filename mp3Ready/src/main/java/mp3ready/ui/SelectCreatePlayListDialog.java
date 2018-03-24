package mp3ready.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import mp3ready.adapters.PlayListsAdatper;
import mp3ready.api.NewApiCalls;
import mp3ready.entities.PlayList;
import mp3ready.enums.Enums;
import mp3ready.serializer.ZdParser;
import mp3ready.util.ICallBack;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.zgm.mp3ready.R;

/**
 * - implment the dialog of select , create and update playlist according to
 * {@link #type} - render the playlists in ListView - render editText to let
 * user create/update playlist
 * 
 * @author mhmd
 * 
 */

public class SelectCreatePlayListDialog extends DialogFragment implements
		OnClickListener, OnItemClickListener {

	public final static String TAG = SelectCreatePlayListDialog.class.getName();

	public final static int TYPE_SELECT = 0;
	public final static int TYPE_CREATE = 1;
	public final static int TYPE_UPDATE = 2;
	public int type = 0;// the type of dailog TYPE_SELECT, TYPE_CREATE or
						// TYPE_UPDATE
	private MainActivity mainActivity;
	private Gson gson;
	private ProgressBar pb_loading;
	private TextView tv_header_title;
	private ListView list_pls;
	private EditText et_new_pl_name;
	private Button btn_submit;
	private ICallBack callback;
	private String oldPlaylistName;
	private PlayListsAdatper playlist_adapter;

	public static SelectCreatePlayListDialog newInstance(int type,
			ICallBack callback) {
		SelectCreatePlayListDialog efrag = new SelectCreatePlayListDialog();
		efrag.callback = callback;
		efrag.type = type;
		return efrag;
	}

	public static SelectCreatePlayListDialog newInstance(int type,
			String oldName, ICallBack callback) {
		SelectCreatePlayListDialog efrag = new SelectCreatePlayListDialog();
		efrag.callback = callback;
		efrag.type = type;
		efrag.oldPlaylistName = oldName;
		return efrag;
	}

	/**
	 * <p>
	 * parse the json response from server and render the playlists in adapter
	 * </p>
	 * 
	 * @param response
	 *            the json response from server
	 */
	private void renderPlaylists(String response) {
		pb_loading.setVisibility(View.GONE);
		;
		Type TYPE_ArrayList_pls = new TypeToken<ArrayList<PlayList>>() {
		}.getType();
		List<PlayList> pls = gson.fromJson(response, TYPE_ArrayList_pls);
		playlist_adapter = new PlayListsAdatper(mainActivity,
				Enums.PLAYLIST_USER_DEFINE_TYPE, false) {

			@Override
			protected void popUpMenu(View btn) {
				

			}
		};
		playlist_adapter.loadMorePLS(pls);
		if (playlist_adapter.getCount() <= 0) {
			dismiss();
			callback.onSwitchToCreateMode();
		}
		list_pls.setAdapter(playlist_adapter);
		playlist_adapter.notifyDataSetChanged();
	}

	/**
	 * send request to getting the playlists from server
	 */
	private void getPlayLists() {

		mainActivity.apiCalls.getPlayLists("", new NewApiCalls.Callback() {
			@Override
			public void onFinished(String response) {
				pb_loading.setVisibility(View.GONE);
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					renderPlaylists(parser.response);
				} else {

				}

			}
		});
//		ApiCalls.getPlayLists("", new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onFinished(String result) {
//
//				try {
//
//				} catch (Exception e) {
//
//				}
//			}
//		});
	}

	/**
	 * <p>
	 * initialize the dialog and specify the view according to {@link #type}
	 * </p>
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View view = getActivity().getLayoutInflater().inflate(
				R.layout.select_create_playlists_layout, null);
		pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
		tv_header_title = (TextView) view.findViewById(R.id.tv_header_title);
		list_pls = (ListView) view.findViewById(R.id.list_sc_pl);
		list_pls.setOnItemClickListener(this);
		et_new_pl_name = (EditText) view.findViewById(R.id.new_pl_name);
		btn_submit = (Button) view.findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		builder.setView(view);
		if (type == SelectCreatePlayListDialog.TYPE_CREATE) {
			tv_header_title.setText("Choose a playlist name:");
			list_pls.setVisibility(View.GONE);
			et_new_pl_name.setVisibility(View.VISIBLE);
			btn_submit.setText("Ok");
		} else if (type == SelectCreatePlayListDialog.TYPE_UPDATE) {
			tv_header_title.setText("Update PlayList Name:");
			list_pls.setVisibility(View.GONE);
			et_new_pl_name.setVisibility(View.VISIBLE);
			et_new_pl_name.setText(oldPlaylistName);
			btn_submit.setText("Ok");
		} else if (type == SelectCreatePlayListDialog.TYPE_SELECT) {
			tv_header_title.setText("Select a playlist:");
			list_pls.setVisibility(View.VISIBLE);
			et_new_pl_name.setVisibility(View.GONE);
			btn_submit.setText("New playlist");
			if (playlist_adapter == null) {
				pb_loading.setVisibility(View.VISIBLE);
				if (mainActivity.mCachedResponse != null
						&& !mainActivity.mCachedResponse.equals("")) {
					renderPlaylists(mainActivity.mCachedResponse);
				} else {
					getPlayLists();
				}
			} else {
				list_pls.setAdapter(playlist_adapter);
				playlist_adapter.notifyDataSetChanged();
			}
		}
		return builder.create();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		mainActivity = (MainActivity) getActivity();
		gson = new GsonBuilder().create();

	}

	@Override
	public void onStart() {
		
		super.onStart();

	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();

	}

	@Override
	public void onClick(View view) {
		
		view.setEnabled(false);
		switch (view.getId()) {
		case R.id.btn_submit:
			if (type == SelectCreatePlayListDialog.TYPE_CREATE) {
				String pl_name = et_new_pl_name.getText().toString();
				if (pl_name.length() != 0) {
					this.dismiss();
					callback.onCreatePlayList(pl_name);
				} else {
					this.dismiss();
				}

			} else if (type == SelectCreatePlayListDialog.TYPE_UPDATE) {
				String pl_name = et_new_pl_name.getText().toString();
				if (pl_name.length() != 0) {
					this.dismiss();
					callback.onUpdatePlayList(pl_name);
				} else {
					this.dismiss();
				}

			} else if (type == SelectCreatePlayListDialog.TYPE_SELECT) {
				this.dismiss();
				callback.onSwitchToCreateMode();
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
			long arg3) {
		
		PlayList pl = (PlayList) adapter.getItemAtPosition(pos);
		if (pl != null) {
			this.dismiss();
			callback.onSelectPlayList(pl);
		}
	}

}