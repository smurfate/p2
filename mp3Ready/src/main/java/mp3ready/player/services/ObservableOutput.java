package mp3ready.player.services;

import java.util.HashMap;

import mp3ready.entities.Song;

public abstract class ObservableOutput implements OutputAccess {
	// ---------------------------------- Observable

	HashMap<String, PlayerObserver> observers = new HashMap<String, PlayerObserver>();

	/**
	 * calling #OnMediaPlayerBufferingonUpdate method for all observers to
	 * update secondary progress bar in full player screen
	 * 
	 */
	protected void notifyOnMediaPlayerBufferingonUpdate(int duration, int pos) {

		for (PlayerObserver observer : observers.values()) {
			observer.onMediaPlayerBufferingonUpdate(duration, pos);
		}
	}

	/**
	 * calling #onMediaPlayerCompletion method for all observers to play next
	 * song if isLooping didn't set
	 * 
	 */
	protected void notifyOnMediaPlayerCompletion(Output output) {

		for (PlayerObserver observer : observers.values()) {
			observer.onMediaPlayerCompletion(output);
		}
	}

	protected void notifyTrackHasInvalidUrl(Song track) {
		for (PlayerObserver observer : observers.values()) {
			observer.TrackHasInvalidUrl(track);
		}
	}

	protected void notifyTrackChanged(Song track, int lengthInMillis) {
		for (PlayerObserver observer : observers.values()) {
			observer.trackChanged(track, lengthInMillis);
		}
	}

	protected void notifyStarted() {
		for (PlayerObserver observer : observers.values()) {
			observer.started();
		}
	}

	protected void notifyStopped() {
		for (PlayerObserver observer : observers.values()) {
			observer.stopped();
		}
	}

	public void addObserver(PlayerObserver observer) {
		observers.put(observer.getId(), observer);
	}

	public void removeObserver(String observeId) {
		observers.remove(observeId);
	}

	public interface PlayerObserver extends Observer {
		void TrackHasInvalidUrl(Song track);

		void trackChanged(Song track, int lengthInMillis);

		void started();

		void stopped();

		void onMediaPlayerBufferingonUpdate(int duration, int pos);

		void onMediaPlayerCompletion(Output output);
	}
}
