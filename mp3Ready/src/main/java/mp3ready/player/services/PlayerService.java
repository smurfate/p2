package mp3ready.player.services;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import mp3ready.entities.Song;
import mp3ready.ui.MainActivity;
//import zgm.util.OnlineCkeck;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * Access for native Android Media player.<br />
 * 
 * take a look at:
 * http://developer.android.com/reference/android/media/MediaPlayer
 * .html#StateDiagram <br />
 * <br />
 * {@link #getMp()} garantees media player is at least in idle state<br />
 * {@link #initialized} garantees media player is at least in initialized state<br />
 */
public class PlayerService extends Service implements Output, OnErrorListener,
		OnBufferingUpdateListener, OnCompletionListener {
	private final static int NOTIFICATION_ID = 22;

	public enum Notify {
		STARTED, STOPPED, CHANGED, URL_INVALID, BUFFER_UPDATE, SONG_COMPLETION
	}

	public final static String PARAM_OBJECT_TRACK = "track";
	public final static String PARAM_OBJECT_TRACK_MEDIA_POS = "track_media_pos";
	public final static String PARAM_INTEGER_MILLIS = "millis";
	private Runnable connectivityCheckingRunnable;
	private Handler connectivityCheckingHandlre;
	private boolean shouldForceChangeSongUrl = true;

	public class PlayerServiceBinder extends Binder {
		public PlayerService getPlayerService() {
			return PlayerService.this;
		}

		public void register(Messenger messenger) {
			clients.add(messenger);
		}

		public void unregister(Messenger messenger) {
			clients.remove(messenger);
		}
	}

	Binder playerServiceBinder = new PlayerServiceBinder();

	private final Set<Messenger> clients = new HashSet<Messenger>();

	@Override
	public IBinder onBind(Intent intent) {
		return playerServiceBinder;
	}

	private MediaPlayer mp = null; // use getMp to access plz
	private boolean isPlaying = false;
	private Song currTrack = null;

	private boolean initialized = false; // indicates the player is at least in
											// Initialized mode

	@Override
	public void onCreate() {
		Log.i("PlayerService", "PlayerService.onCreate() called");
		super.onCreate();
		connectivityCheckingHandlre = new Handler();
		connectivityCheckingRunnable = new Runnable() {

			@Override
			public void run() {
				
				Log.e("PlayerService", "check connectivity Run");
				if (!getMp().isPlaying() && shouldForceChangeSongUrl) {
					Log.e("PlayerService", "force notifyTrackHasInvalidUrl");
					notifyTrackHasInvalidUrl(currTrack);
				}
				connectivityCheckingHandlre
						.removeCallbacks(connectivityCheckingRunnable);
			}
		};

	}

	@Override
	public void onDestroy() {
		Log.i("PlayerService", "PlayerService.onDestroy() called");
		super.onDestroy();
		release();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Log.i("PlayerService", "PlayerService.onUnbind() called");
		return super.onUnbind(intent);
	}

	/*
	 * public void change(final Song t){ if(t != null && t.url != null &&
	 * !t.url.equals("")) { prepare(t,new OnPreparedListener() {
	 * 
	 * @Override public void onPrepared(MediaPlayer mp) { // TODO Auto-generated
	 * method stub Log.e("PlayerService", "onPrepared"); boolean wasPLaying =
	 * isPlaying; initialized = true; isPlaying = false;
	 * 
	 * if(wasPLaying){ playInternal(); } notifyTrackChanged(t,
	 * getMp().getDuration()); } }); } }
	 */

	public void play(final Song t) {
		if (t != null && t.file != null && !t.file.equals("")) {

			prepare(t, new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					
					Log.e("PlayerService", "onPrepared");
					//boolean wasPlaying = isPlaying;
					initialized = true;
					isPlaying = false;
					boolean started = playInternal();
					if (started) {
						notifyStarted();
					}
					notifyTrackChanged(t, getMp().getDuration());
				}
			});

		}
	}

	public void toggle() {
		if (initialized) {
			if (isPlaying) {
				pause();
			} else {
				play();
			}
		}
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean pause() {
		boolean stopped = pauseInternal();
		if (stopped) {
			notifyStopped();
		}
		return stopped;
	}

	public boolean pauseInternal() {
		if (initialized && isPlaying) {
			getMp().pause();
			isPlaying = false;
			return true;
		}
		return false;
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean play() {
		boolean started = playInternal();
		if (started) {
			notifyStarted();
		}
		return started;
	}

	/**
	 * @return true if this call had an effect
	 */
	public boolean playInternal() {
		if (!isPlaying && initialized) {
			getMp().start();
			isPlaying = true;
			return true;
		}
		return false;
	}

	public void goToMillis(int millis) {
		if (initialized) {
			getMp().seekTo(Math.max(Math.min(millis, getMp().getDuration()), 0));
		}
	}

	public int getCurrentMillis() {
		return initialized ? getMp().getCurrentPosition() : 0;
	}

	public Song getCurrSong() {
		return currTrack;
	}

	/*
	 * public void setOnCompletionListener(MediaPlayer.OnCompletionListener
	 * listener) { getMp().setOnCompletionListener(listener); }
	 */

	/**
	 * @return MediaPlayer at least in idle state
	 */
	private MediaPlayer getMp() {
		if (mp == null) {
			mp = new MediaPlayer();
			mp.reset();
		}
		return mp;
	}

	/**
	 * releases the mp and kill the reference because the old instance is not
	 * usable anymore
	 */
	public void release() {
		initialized = false;
		pause();
		// setOnCompletionListener(null);
		// setOnBufferingUpdateListener(null);
		getMp().release();
		mp = null;
	}

	private void prepare(Song t, MediaPlayer.OnPreparedListener l) {
		if (t != null) {
			try {
				currTrack = t;
				getMp().reset();
				getMp().setOnPreparedListener(l);
				getMp().setOnErrorListener(this);
				getMp().setOnBufferingUpdateListener(this);
				getMp().setOnCompletionListener(this);
				getMp().setAudioStreamType(AudioManager.STREAM_MUSIC);
				getMp().setDataSource(t.file);
				shouldForceChangeSongUrl = true;
				connectivityCheckingHandlre.postDelayed(
						connectivityCheckingRunnable, 6000);
				getMp().prepareAsync();

			} catch (IOException e) {
				Log.v("PlayerService", e.getMessage());
			}
		}
	}

	// ---------------------------------- Observable

	private void notifyTrackHasInvalidUrl(Song track) {
		Bundle params = new Bundle();
		params.putSerializable(PARAM_OBJECT_TRACK, track);
		notifyClients(Notify.URL_INVALID, params);
		if (isPlaying) {
			notifyStopped();
		}
	}

	private void notifyOnMediaPlayerBufferingonUpdate(MediaPlayer mp, int pos) {
		Bundle params = new Bundle();
		params.putInt(PARAM_INTEGER_MILLIS, mp.getDuration());
		params.putInt(PARAM_OBJECT_TRACK_MEDIA_POS, pos);
		notifyClients(Notify.BUFFER_UPDATE, params);
	}

	private void notifyOnMediaPlayerCompletion() {
		notifyClients(Notify.SONG_COMPLETION, null);
	}

	private void notifyTrackChanged(Song track, int lengthInMillis) {
		if (isPlaying) {
			startForeground(NOTIFICATION_ID, getNotification());
		}
		Bundle params = new Bundle();
		params.putSerializable(PARAM_OBJECT_TRACK, track);
		params.putInt(PARAM_INTEGER_MILLIS, lengthInMillis);
		notifyClients(Notify.CHANGED, params);
	}

	private void notifyStarted() {
		startForeground(NOTIFICATION_ID, getNotification());
		notifyClients(Notify.STARTED, null);
	}

	private void notifyStopped() {
		stopForeground(true);
		notifyClients(Notify.STOPPED, null);
	}

	private Notification getNotification() {
		Notification note = new Notification(com.zgm.mp3ready.R.drawable.logo,
				getText(com.zgm.mp3ready.R.string.app_name),
				System.currentTimeMillis());
		Intent i = new Intent(this, MainActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);


//		note.setLatestEventInfo(this,
//				getText(com.zgm.mp3ready.R.string.app_name),
//				getCurrSong() == null ? "" : getCurrSong().SName, pi);
		note.flags |= Notification.FLAG_NO_CLEAR;
		return note;
	}

	private void notifyClients(Notify notification, Bundle params) {
		final Set<Messenger> clientsToRemove = new HashSet<Messenger>();
		for (Messenger client : clients) {
			try {
				Message msg = Message.obtain(null, notification.ordinal());
				msg.setData(params);
				client.send(msg);
			} catch (RemoteException e) {
				// If we get here, the client is dead, and we should remove it
				// from the list
				Log.d("PLayerService", "Client does not respond, remove it");
				clientsToRemove.add(client);
			}
		}
		for (Messenger client : clientsToRemove) {
			clients.remove(client);
		}
	}

	/*
	 * @Override public void
	 * setOnBufferingUpdateListener(OnBufferingUpdateListener listener) { //
	 * TODO Auto-generated method stub
	 * getMp().setOnBufferingUpdateListener(listener); }
	 */

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		
		shouldForceChangeSongUrl = false;
//		if (OnlineCkeck.isOnline(getApplicationContext())) {
//			notifyTrackHasInvalidUrl(currTrack);
//			if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN) {
//				Log.e("PlayerService", "MEDIA_ERROR_UNKNOWN");
//			} else if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED) {
//				Log.e("PlayerService", "MEDIA_ERRO_SERVER_DIED");
//			} else if (extra == MediaPlayer.MEDIA_ERROR_IO) {
//				Log.e("PlayerService", "MEDIA_ERROR_IO");
//			} else if (extra == MediaPlayer.MEDIA_ERROR_MALFORMED) {
//				Log.e("PlayerService", "MEDIA_ERROR_MALFORMED");
//			} else if (extra == MediaPlayer.MEDIA_ERROR_UNSUPPORTED) {
//				Log.e("PlayerService", "MEDIA_ERROR_UNSUPPORTED");
//			} else if (extra == MediaPlayer.MEDIA_ERROR_TIMED_OUT) {
//				Log.e("PlayerService", "MEDIA_ERROR_TIMED_OUT");
//			}
//		}
		return true;
	}

	@Override
	public int getLengthInMillis() {
		
		return (initialized ? getMp().getDuration() : 0);
	}

	@Override
	public boolean isPLaying() {
		
		return getMp().isPlaying();
	}

	@Override
	public void setLooping(boolean looping) {
		
		getMp().setLooping(looping);
	}

	@Override
	public boolean isMPLooping() {
		
		return getMp().isLooping();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int pos) {
		
		notifyOnMediaPlayerBufferingonUpdate(mp, pos);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		
		notifyOnMediaPlayerCompletion();
	}

}
