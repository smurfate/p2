package mp3ready.player.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcastsHandler extends BroadcastReceiver {
	private boolean headsetConnected = false;
	private OutputAccess outputAccess;

	public BroadcastsHandler(OutputAccess outputAccess) {
		this.outputAccess = outputAccess;
	}

	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("state")) {
			OutputCommand outputCommand = null;

			if (headsetConnected && intent.getIntExtra("state", 0) == 0) {
				headsetConnected = false;
				Toast.makeText(context, "Headphones UnPlugged",
						Toast.LENGTH_LONG).show();
				outputCommand = new OutputCommand() {
					public void connected(Output output) {
						output.pause();
					}
				};
			} else if (!headsetConnected && intent.getIntExtra("state", 0) == 1) {
				headsetConnected = true;
				outputCommand = new OutputCommand() {
					public void connected(Output output) {
						output.play();
					}
				};
			}
			if (outputCommand != null) {
				outputAccess.connectPlayer(outputCommand);
			}
		}
	}
}
