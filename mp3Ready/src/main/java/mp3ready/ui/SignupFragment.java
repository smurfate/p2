package mp3ready.ui;

import mp3ready.api.NewApiCalls;
import mp3ready.entities.User;
import mp3ready.enums.Enums;
import mp3ready.serializer.ZdParser;
import mp3ready.views.MaterialSection;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.zgm.mp3ready.R;


public class SignupFragment extends Fragment implements OnClickListener {

	public final static String TAG = SignupFragment.class.getName();

	private EditText et_fname;
	private EditText et_lname;
	private EditText et_user_name;
	private EditText et_email;
	private EditText et_password;
	private EditText et_confirm_password;
	private Button signup_ok;
	private Button fb_login_submit;
	private ImageView iv_arrow_back;
	private NewApiCalls.Callback callback;
	private MainActivity mainActivity;
	private ProgressBar pb_loading;
	private Gson gson;

	public static SignupFragment newInstance(NewApiCalls.Callback callback) {
		SignupFragment efrag = new SignupFragment();
		efrag.callback = callback;
		return efrag;
	}

	private void initView() {
		et_fname = (EditText) getView().findViewById(R.id.et_fname);
		et_lname = (EditText) getView().findViewById(R.id.et_lname);
		et_user_name = (EditText) getView().findViewById(R.id.et_user_name);
		et_email = (EditText) getView().findViewById(R.id.et_email);
		et_password = (EditText) getView().findViewById(R.id.et_password);
		et_confirm_password = (EditText) getView().findViewById(
				R.id.et_password_confirm);
		pb_loading = (ProgressBar)getView().findViewById(R.id.pb_loading);
		iv_arrow_back = (ImageView)getView().findViewById(R.id.iv_arrow_back);
		signup_ok = (Button) getView().findViewById(R.id.signup_ok);
		fb_login_submit= (Button) getView().findViewById(R.id.fb_login_submit);
		signup_ok.setOnClickListener(this);
		iv_arrow_back.setOnClickListener(this);
		fb_login_submit.setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
		mainActivity = (MainActivity)getActivity();
		gson = mainActivity.gson;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.signup_layout, container, false);
		return view;

	}
	@Override
	public void onResume() {
		
		super.onResume();
	}
	@Override
	public void onStart() {
		
		super.onStart();
		initView();
		mainActivity.am_i_in_login = true;
	}

	@Override
	public void onPause() {
		
		super.onPause();

	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
	}

	private void signup() {
		User mUser = new User();
		mUser.fname = et_fname.getText().toString();
		mUser.lname = et_lname.getText().toString();
		mUser.username = et_user_name.getText().toString();
		mUser.email = et_email.getText().toString();
		mUser.password = et_password.getText().toString();
		mUser.verifyPassword = et_confirm_password.getText().toString();

		mUser.imei = mainActivity.deviceInfo.IMEI;
		mUser.mac_address = mainActivity.deviceInfo.MacAddress;
		mUser.imsi = mainActivity.deviceInfo.IMSI;
		mUser.manufacture = mainActivity.deviceInfo.Manufacture;
		//mainActivity.setSupportProgressBarIndeterminateVisibility(true);
		pb_loading.setVisibility(View.VISIBLE);
		mainActivity.apiCalls.signUp(gson.toJson(mUser), new NewApiCalls.Callback2() {
			@Override
			public void onFinished(String response) {
				pb_loading.setVisibility(View.GONE);
				signup_ok.setEnabled(true);
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					Log.i(TAG, response);
					callback.onFinished(parser.response);
					if (!mainActivity.getSupportActionBar().isShowing()){
						mainActivity.getSupportActionBar().show();
					}
					if (!mainActivity.getSupportActionBar().isShowing())
						mainActivity.getSupportActionBar().hide();
					MaterialSection section = mainActivity.getSectionByTitle(Enums.HOME_SCREEN_POS);
					mainActivity.navigator.gotoSection(section.getTargetFragment());
//					mainActivity.setSection(section, false);//is not child
						/*mainActivity.gotoFragment(ListFragment.newInstance(
								ListFragment.USER_FAV_GENRES, false),
								ListFragment.TAG, false);*/
				} else {
					mainActivity.toast(parser.response);
				}

			}

			@Override
			public void onProblem() {
				pb_loading.setVisibility(View.GONE);

			}
		});
//		ApiCalls.signUp(gson.toJson(mUser), new CallbackHandler(mainActivity) {
//
//			@Override
//			public void onNoInet() {
//
//				super.onNoInet();
//				pb_loading.setVisibility(View.GONE);
//			}
//			@Override
//			public void onTimeOut() {
//
//				super.onTimeOut();
//				pb_loading.setVisibility(View.GONE);
//			}
//			@Override
//			public void onUnknownHost() {
//
//				super.onUnknownHost();
//				pb_loading.setVisibility(View.GONE);
//			}
//			@Override
//			public void onFinished(String result) {
//
//				try {
//					//mainActivity
//						//	.setSupportProgressBarIndeterminateVisibility(false);
//				} catch (Exception e) {
//					// TODO: handle exception
//					e.printStackTrace();
//				}
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.fb_login_submit:
			break;
		case R.id.iv_arrow_back:
			mainActivity.onBackPressed();
			break;
		case R.id.signup_ok:
			v.setEnabled(false);
			if (et_user_name.getText().toString().length() == 0) {
				mainActivity.toast("you must enter username");
				v.setEnabled(true);
				break;
			}
			if (et_password.getText().toString().length() == 0) {
				mainActivity.toast("you must enter password");
				v.setEnabled(true);
				break;
			}
			if (et_confirm_password.getText().toString().length() == 0) {
				mainActivity.toast("you must enter confirm password");
				v.setEnabled(true);
				break;
			}
			if (!et_confirm_password.getText().toString()
					.equals(et_password.getText().toString())) {
				mainActivity.toast("password and confirm password are not same");
				v.setEnabled(true);
				break;
			}
			signup();
			break;
		}
	}

}