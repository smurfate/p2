package mp3ready.player.services;

public interface OutputAccess {
	public void connectPlayer(final OutputCommand outputCommand);

	public void connectPlayerExtra(final OutputCommand outputCommand);

	public void releasePlayer();
}
