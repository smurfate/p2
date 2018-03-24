package mp3ready.player.services;

import mp3ready.entities.Song;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class PlayerServiceConnector extends ObservableOutput {

	final ContextWrapper contextWrapper;
	final Object waitOnConnectionLock = new Object();

	public PlayerServiceConnector(ContextWrapper contextWrapper) {
		this.contextWrapper = contextWrapper;
	}

	final Messenger mMessenger = new Messenger(new Handler() {
		@Override
		public void handleMessage(Message msg) {
			PlayerService.Notify notification = PlayerService.Notify.values()[msg.what];
			switch (notification) {
			case CHANGED:
				int duration = msg.getData().getInt(
						PlayerService.PARAM_INTEGER_MILLIS);
				Song track = (Song) msg.getData().get(
						PlayerService.PARAM_OBJECT_TRACK);
				notifyTrackChanged(track, duration);
				break;
			case STARTED:
				notifyStarted();
				break;
			case STOPPED:
				notifyStopped();
				break;
			case URL_INVALID:
				Song song = (Song) msg.getData().get(
						PlayerService.PARAM_OBJECT_TRACK);
				notifyTrackHasInvalidUrl(song);
				break;
			case BUFFER_UPDATE:
				int trackDuration = msg.getData().getInt(
						PlayerService.PARAM_INTEGER_MILLIS);
				int trackMediaPos = msg.getData().getInt(
						PlayerService.PARAM_OBJECT_TRACK_MEDIA_POS);
				notifyOnMediaPlayerBufferingonUpdate(trackDuration,
						trackMediaPos);
				break;
			case SONG_COMPLETION:
				connectPlayer(new OutputCommand() {

					@Override
					public void connected(Output output) {
						
						notifyOnMediaPlayerCompletion(output);
					}
				});
				break;
			default:
				super.handleMessage(msg);
			}
		}
	});

	PlayerService.PlayerServiceBinder playerServiceBinder = null;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			synchronized (waitOnConnectionLock) {
				Log.i("PLayerServiceConnector",
						"PlayerServiceConnector.onServiceConnected() called");
				playerServiceBinder = (PlayerService.PlayerServiceBinder) service;
				playerServiceBinder.register(mMessenger);
				waitOnConnectionLock.notifyAll();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			synchronized (waitOnConnectionLock) {
				Log.i("PLayerServiceConnector",
						"PlayerServiceConnector.onServiceDisconnected() called");
				if (playerServiceBinder != null) {
					playerServiceBinder.unregister(mMessenger);
					playerServiceBinder = null;
				}
			}
		}
	};

	public void releasePlayer() {
		synchronized (waitOnConnectionLock) {
			if (playerServiceBinder != null) {
				Log.i("PLayerServiceConnector",
						"PlayerServiceConnector.doUnbindService() called");
				playerServiceBinder.unregister(mMessenger);
				contextWrapper.unbindService(mConnection);
				playerServiceBinder = null;
			}
		}
	}

	public void connectPlayer(final OutputCommand outputCommand) {
		new AsyncTask<Void, Void, Output>() {
			@Override
			protected Output doInBackground(Void... params) {
				Thread.currentThread().setName(
						Thread.currentThread().getName() + ":connectPlayer");
				synchronized (waitOnConnectionLock) {
					if (playerServiceBinder == null) {
						try {
							contextWrapper.bindService(new Intent(
									contextWrapper, PlayerService.class),
									mConnection, Context.BIND_AUTO_CREATE);
							waitOnConnectionLock.wait();
						} catch (InterruptedException e) {
							Log.e("PLayerServiceConnector",
									"wait on output was interupted", e);
						}
					}
					return playerServiceBinder.getPlayerService();
				}
			}

			@Override
			protected void onPostExecute(Output output) {
				if (output != null) {
					outputCommand.connected(output);
				} else {
					Log.e("PLayerServiceConnector", "omitting Player call: "
							+ outputCommand);
				}
			}

		}.execute();
	}

	public void connectPlayerExtra(final OutputCommand outputCommand) {
		new AsyncTask<Void, Void, Output>() {
			@Override
			protected Output doInBackground(Void... params) {
				Thread.currentThread().setName(
						Thread.currentThread().getName() + ":connectPlayer");
				synchronized (waitOnConnectionLock) {
					if (playerServiceBinder == null) {
						try {
							contextWrapper.bindService(new Intent(
									contextWrapper, PlayerService.class),
									mConnection, Context.BIND_AUTO_CREATE);
							waitOnConnectionLock.wait();
						} catch (InterruptedException e) {
							Log.e("PLayerServiceConnector",
									"wait on output was interupted", e);
						}
					}
					Output output = playerServiceBinder.getPlayerService();
					if (output != null) {
						outputCommand.connected(output);
					} else {
						Log.e("PLayerServiceConnector",
								"omitting Player call: " + outputCommand);
					}
					return output;
				}
			}

			@Override
			protected void onPostExecute(Output output) {

			}

		}.execute();
	}
}
