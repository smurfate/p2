package mp3ready.player.services;

import android.view.View;

public abstract class OutputUsingOnClickListener implements
		View.OnClickListener {
	final OutputAccess outputAccess;

	public OutputUsingOnClickListener(OutputAccess outputAccess) {
		this.outputAccess = outputAccess;
	}

	public void onClick(final View v) {
		outputAccess.connectPlayer(new OutputCommand() {
			public void connected(Output output) {
				onClick(v, output);
			}
		});
	}

	public abstract void onClick(View v, Output output);
}
