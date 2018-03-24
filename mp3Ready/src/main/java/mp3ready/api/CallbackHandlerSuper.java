//package mp3ready.api;
//
//import mp3ready.ui.MainActivity;
//import zgm.http.CallBack;
//
//public abstract class CallbackHandlerSuper implements CallBack {
//
//	// CallBack cb ;
//	MainActivity mainActivity;
//
//	public CallbackHandlerSuper(MainActivity mainActivity) {
//		// this.cb = cb ;
//		this.mainActivity = mainActivity;
//	}
//
//	@Override
//	public abstract void onFinished(String result);
//
//	@Override
//	public void onNoInet() {
//		try {
//			mainActivity.setSupportProgressBarIndeterminateVisibility(false);
//			mainActivity.toast("No Internet Connetion");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void onUnknownHost() {
//		try {
//			mainActivity.setSupportProgressBarIndeterminateVisibility(false);
//			mainActivity.toast(" Couldn't connect to server!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void onTimeOut() {
//		try {
//			mainActivity.setSupportProgressBarIndeterminateVisibility(false);
//			mainActivity.toast(" Connection Timeout");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
