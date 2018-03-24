package mp3ready.player.services;

import mp3ready.entities.Song;

public interface Output {

	// void change(Song t);

	void play(Song t);

	void toggle();

	/**
	 * @return true if this call had an effect
	 */
	boolean pause();

	void setLooping(boolean looping);

	boolean isMPLooping();

	/**
	 * @return true if this call had an effect
	 */
	boolean play();

	boolean isPLaying();

	void goToMillis(int millis);

	void release();

	int getCurrentMillis();

	int getLengthInMillis();

	Song getCurrSong();

	// void setOnCompletionListener(MediaPlayer.OnCompletionListener listener);
	// void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener
	// listener);
}
