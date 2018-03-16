package com.deemsysinc.neetar11biophase1.expansion;

import android.app.job.JobParameters;
import android.content.Intent;
import android.util.Log;

import com.deemsysinc.neetar11biophase1.downloader.impl.DownloadNotification;


public class DownloaderService extends com.deemsysinc.neetar11biophase1.downloader.impl.DownloaderService {
    public static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq656ZAUz1pM6phy5Cd8O7ZTyZEIIdMdsXIxk3/Tmd4Y+6xneLFJEbYfhT2k8BL2977+XzJM0Zv58zT0OgRpzjGiJAdxuRWlV3wDAm51pXsCkXWYQAYqV6B500dXxP7Bmct/IDzeKc2/Y4yJjw9WvoB+BRY3avDC2OlJoDv6UV6Hqj2y27YVGpxP9RmaUYCSv+3ffnlNeCAjRSKsq5dogRlIneRrQZE4f/OSEx4SKluDM9OsPSmFhE6yduTCLUNSH6cRU2q2LPmNCc6sCxlAsdV3qzNvQ+L0YXGD6CWbHzxT5i0MEf+9EizBL0DmdDYBhgTIKR5pFnebwrjTdK31vrQIDAQAB"; // TODO Add public key
    private static final byte[] SALT = new byte[]{1, 4, -1, -1, 14, 42, -79, -21, 13, 2, -8, -11, 62, 1, -10, -101, -19, 41, -12, 18}; // TODO Replace with random numbers of your choice. (it is just to ensure that the expansion files are encrypted with a unique key from your app)
    private DownloadNotification mnotify;

    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return DownloaderServiceBroadcastReceiver.class.getName();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.i("Job Started", "on start job: " + params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i("Job Started", "on stop job: " + params.getJobId());
        return false;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        mnotify = new DownloadNotification(this, "ARDemoProject");
        mnotify.CancelNotification();
        stopSelf();
        super.onTaskRemoved(rootIntent);
    }
}