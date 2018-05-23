package com.deemsysinc.kidsar.utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.deemsysinc.kidsar.R;

public class PlayAudioService extends Service {
    private static final String LOGCAT = "Service";
    static MediaPlayer objPlayer;
    static int music = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        objPlayer = MediaPlayer.create(this, R.raw.appmusic);
        objPlayer.setVolume(100, 100);
        objPlayer.setLooping(true);
        objPlayer.start();
        return START_STICKY;
    }

    public static void onPausePlayer() {
        music = objPlayer.getCurrentPosition();
        objPlayer.pause();
    }

    public static void onResumePlayer() {
        if (objPlayer != null && music != 0) {
            objPlayer.seekTo(music);
            objPlayer.start();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        objPlayer.stop();
    }
    /* @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey("AudioService")) {
                String casevalue = intent.getExtras().getString("AudioService");
                if (casevalue != null) {
                    switch (casevalue) {
                        case Constants.START_JOB:
                            objPlayer.start();
                            break;
                        case Constants.PAUSE_JOB:
                            objPlayer.pause();
                            break;
                        case Constants.RESUME_JOB:
                            objPlayer.seekTo(1000);
                            objPlayer.start();
                            break;
                        case Constants.STOP_JOB:
                            objPlayer.stop();
                            break;
                        default:
                            Log.d("Service_Response", "Media object have not a request");
                            break;
                    }
                }
            }
        }
    }*/

}
