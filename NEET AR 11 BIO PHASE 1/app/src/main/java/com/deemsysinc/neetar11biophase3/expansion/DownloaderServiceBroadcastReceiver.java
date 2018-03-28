package com.deemsysinc.neetar11biophase3.expansion;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.deemsysinc.neetar11biophase3.downloader.DownloaderClientMarshaller;


public class DownloaderServiceBroadcastReceiver extends android.content.BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            DownloaderClientMarshaller.startDownloadServiceIfRequired(context, intent, DownloaderService.class);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}