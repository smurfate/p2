package mp3ready.ui;

import mp3ready.api.NewApiCalls;
import mp3ready.entities.User;
import mp3ready.enums.Enums;
import mp3ready.serializer.ZdParser;

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
import android.widget.TextView;

import com.google.gson.Gson;
import com.zgm.mp3ready.R;

public class LoginFragment extends Fragment implements OnClickListener {

	public final static String TAG = LoginFragment.class.getName();

	private EditText et_user_name;
	private EditText et_password;
	private Button login_btn;
	private Button fb_login_btn;
	private TextView signup_btn;
	private ImageView iv_arrow_back;
	private NewApiCalls.Callback callback;
	private MainActivity mainActivity;
	private ProgressBar pb_loading;
	private Gson gson;

	public static LoginFragment newInstance(NewApiCalls.Callback callback) {
		LoginFragment efrag = new LoginFragment();
		efrag.callback = callback;
		return efrag;
	}

	private void initView() {
		et_user_name = (EditText) getView().findViewById(R.id.et_user_name);
		et_password = (EditText) getView().findViewById(R.id.et_password);
		login_btn = (Button) getView().findViewById(R.id.login_submit);
		fb_login_btn = (Button) getView().findViewById(R.id.fb_login_submit);
		signup_btn = (TextView) getView().findViewById(R.id.signup_submit);
		iv_arrow_back = (ImageView)getView().findViewById(R.id.iv_arrow_back);
		pb_loading = (ProgressBar)getView().findViewById(R.id.pb_loading);
		iv_arrow_back.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		fb_login_btn.setOnClickListener(this);
		signup_btn.setOnClickListener(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
		mainActivity = (MainActivity)getActivity();
		gson = mainActivity.gson;
	}

	@Override
	public void onResume() {
		
		super.onResume();
		if (mainActivity.getSupportActionBar().isShowing())
			mainActivity.getSupportActionBar().hide();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.login_layout, container, false);
		return view;

	}

	@Override
	public void onStart() {
		
		super.onStart();
		initView();
		mainActivity.am_i_in_login = true;
		mainActivity.am_i_in_home = false;
		
	}

	@Override
	public void onPause() {
		
		super.onPause();

	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
	}

	private void login() {
		User mUser = new User();
		mUser.username = et_user_name.getText().toString();
		mUser.password = et_password.getText().toString();
		mUser.imei = mainActivity.deviceInfo.IMEI;
		mUser.mac_address = mainActivity.deviceInfo.MacAddress;
		mUser.imsi = mainActivity.deviceInfo.IMSI;
		mUser.manufacture = mainActivity.deviceInfo.Manufacture;
		//mainActivity.setSupportProgressBarIndeterminateVisibility(true);
		pb_loading.setVisibility(View.VISIBLE);
		mainActivity.apiCalls.login(gson.toJson(mUser), new NewApiCalls.Callback2() {
			@Override
			public void onFinished(String response) {
				pb_loading.setVisibility(View.GONE);
				login_btn.setEnabled(true);
				ZdParser parser = new ZdParser(response);
				if (parser.code == 200) {
					Log.i(TAG, response);
					callback.onFinished(parser.response);

				} else {
					mainActivity.toast(parser.response);
				}

			}

			@Override
			public void onProblem() {
				pb_loading.setVisibility(View.GONE);

			}
		});
//		ApiCalls.login(gson.toJson(mUser), new CallbackHandler(
//				mainActivity) {
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
		case R.id.login_submit:
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
			login();
			break;
		case R.id.fb_login_submit:

			break;
		case R.id.signup_submit:
			mainActivity.gotoFragment(SignupFragment.newInstance(callback),
					SignupFragment.TAG, true);// is child
			break;
		case R.id.iv_arrow_back:
			mainActivity.navigator.gotoMainSection();
//			mainActivity.onBackPressed();
//
//			mainActivity.getSectionByTitle(Enums.APP_LOGIN_SCREEEN_POS).unSelect();
			break;
		}
		
	}

}