package mp3ready.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.RemoteInput;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.zgm.mp3ready.R;

import java.util.List;

import mp3ready.ui.MainActivity;

public class PlayerService extends Service {

    private final IBinder playerBinder = new PlayerBinder();
    private String TAG = getClass().getSimpleName();


    private SimpleExoPlayer player;


    private DataSource.Factory dataSourceFactory;
    private TrackSelector trackSelector;
    private BandwidthMeter bandwidthMeter;
    private TrackSelection.Factory trackSelectionFactory;
    private ExtractorsFactory extractorsFactory;

    private MediaSource mediaSource;


    public PlayerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //called only once when the service started the first time
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "playerService"), (TransferListener<? super DataSource>) bandwidthMeter);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        extractorsFactory = new DefaultExtractorsFactory();

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //called every time the startService is called
        return super.onStartCommand(intent, flags, startId);
    }

    private SimpleExoPlayer initializePlayer(String uri) {

        mediaSource = buildMediaSource(uri); //new ExtractorMediaSource(Uri.parse(uri), dataSourceFactory, extractorsFactory, null, null);

        player.prepare(mediaSource);



        return player;

    }

    private MediaSource buildMediaSource(String uri) {
        return new ExtractorMediaSource(Uri.parse(uri), dataSourceFactory, extractorsFactory, null, null);
    }

    private SimpleExoPlayer initializePlayer(List<String> uri)
    {
        MediaSource[] mediaSources = new MediaSource[uri.size()];
        for (int i = 0; i < uri.size(); i++) {
            mediaSources[i] = buildMediaSource(uri.get(i));
        }
        mediaSource = mediaSources.length == 1 ? mediaSources[0]
                : new ConcatenatingMediaSource(mediaSources);
        player.prepare(mediaSource);
        return player;
    }


    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            trackSelector = null;
        }
    }

//    private void notification()
//    {
//        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
//        final NotificationManager mNotifyManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        final String KEY_TEXT_REPLY = "key_text_reply";
//        String replyLabel = "reply_label";
//        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
//                .setLabel(replyLabel)
//                .build();
//
//        mBuilder.setSmallIcon(R.drawable.album_icon);
//        mBuilder.setContentTitle("My notification");
//        mBuilder.setContentText("Hello World!");
//
//        new Thread(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        int incr;
//                        // Do the "lengthy" operation 20 times
//                        for (incr = 0; incr <= 100; incr+=5) {
//                            // Sets the progress indicator to a max value, the
//                            // current completion percentage, and "determinate"
//                            // state
//                            mBuilder.setProgress(100, incr, false);
//                            // Displays the progress bar for the first time.
//                            mNotifyManager.notify(0, mBuilder.build());
//                            // Sleeps the thread, simulating an operation
//                            // that takes time
//                            try {
//                                // Sleep for 5 seconds
//                                Thread.sleep(5*1000);
//                            } catch (InterruptedException e) {
//                                Log.d("TAG", "sleep failure");
//                            }
//                        }
//                        // When the loop is finished, updates the notification
//                        mBuilder.setContentText("Download complete")
//                                // Removes the progress bar
//                                .setProgress(0,0,false);
//                        mNotifyManager.notify(12, mBuilder.build());
//                    }
//                }
//// Starts the thread by calling the run() method in its Runnable
//        ).start();
//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//
//
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        // mId allows you to update the notification later on.
//        mNotificationManager.notify(12, mBuilder.build());
//    }


    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class PlayerBinder extends Binder {
        public SimpleExoPlayer initializePlayer(String uri)
        {
            Log.d(TAG, "initializePlayer: uri:"+uri);
            return PlayerService.this.initializePlayer(uri);
        }

        public SimpleExoPlayer initializePlayer(List<String> uri)
        {
            Log.d(TAG, "initializePlayer: uri:"+uri);
            return PlayerService.this.initializePlayer(uri);
        }


        public SimpleExoPlayer getPlayer()
        {
            return PlayerService.this.player;
        }


        public PlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return playerBinder;
    }



}
