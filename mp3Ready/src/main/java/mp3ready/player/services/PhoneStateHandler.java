package mp3ready.player.services;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneStateHandler extends PhoneStateListener {
	private final OutputAccess outputAccess;

	private boolean wasPlaying = false;

	public PhoneStateHandler(OutputAccess outputAccess) {
		this.outputAccess = outputAccess;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		OutputCommand outputCommand = null;
		if (state == TelephonyManager.CALL_STATE_RINGING) {
			outputCommand = new OutputCommand() {
				public void connected(Output output) {
					wasPlaying = output.pause();
				}
			};
		} else if (state == TelephonyManager.CALL_STATE_IDLE) {
			if (wasPlaying) {
				outputCommand = new OutputCommand() {
					public void connected(Output output) {
						output.play();
					}
				};
			}
		}
		if (outputCommand != null) {
			outputAccess.connectPlayer(outputCommand);
		}
		super.onCallStateChanged(state, incomingNumber);
	}
}
