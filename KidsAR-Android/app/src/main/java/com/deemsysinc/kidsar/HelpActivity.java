package com.deemsysinc.kidsar;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.deemsysinc.kidsar.utils.Constants;
import com.deemsysinc.kidsar.utils.MyApplication;
import com.deemsysinc.kidsar.utils.PlayAudioService;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    ImageView close;
    RecyclerView helplist;
    private LinearLayoutManager layoutManager;
    private List<HelpModel> modellist = new ArrayList<>();
    private HelpAdapter adapter;
    private int stopPosition = 0;
    private VideoView itemViewvideoView;
    private RelativeLayout itemViewlinearvideo;
    private ImageView itemViewvideoplay;
    private boolean videopause = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_help);

        modellist = Constants.getModelList();
        close = findViewById(R.id.buttonclose);
        helplist = findViewById(R.id.helplist);
        layoutManager = new LinearLayoutManager(this);
        adapter = new HelpAdapter(this, HelpActivity.this, modellist);
        helplist.setLayoutManager(layoutManager);
        helplist.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
            }
        });
        helplist.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
             /*   int firstvisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (itemViewvideoView != null) {
                    if (firstvisible >= 2) {
                        if (itemViewvideoView.isPlaying()) {
                            VideoPause();
                            Log.d("stopPositionPauseScroll", "" + stopPosition);
                            PlayAudioService.onResumePlayer();
                        }
                    } else {
                        if (!itemViewvideoView.isPlaying() && stopPosition > 0) {
                            VideoResume();
                            Log.d("stopPositionResumeScroll", "" + stopPosition);
                        }
                    }
                }*/
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (videopause) {
            VideoResume();
        } else {
            MyApplication myApp = (MyApplication) this.getApplication();
            if (myApp.wasInBackground) {
                PlayAudioService.onResumePlayer();
            }
            myApp.stopActivityTransitionTimer();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (itemViewvideoView != null) {
            if (itemViewvideoView.isPlaying()) {
                videopause = true;
                VideoPause();
            } else {
                videopause = false;
                ((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
            }
        } else {
            videopause = false;
            ((MyApplication) this.getApplication()).startActivityTransitionTimer(this);
        }*/
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    public void videoItemClick(View itemView) {
        /*if (itemViewvideoView.isPlaying()) {
            VideoPause();
            Log.d("stopPositionPause", "" + stopPosition);
            PlayAudioService.onResumePlayer();
        } else {
            VideoResume();
            Log.d("stopPositionResume", "" + stopPosition);
        }*/
    }

    private void VideoPause() {
        stopPosition = itemViewvideoView.getCurrentPosition();
        itemViewvideoView.pause();
        itemViewvideoplay.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play));
        itemViewlinearvideo.setVisibility(View.VISIBLE);
    }

    private void VideoResume() {
        PlayAudioService.onPausePlayer();
        itemViewvideoView.seekTo(stopPosition);
        itemViewvideoView.start();
        itemViewlinearvideo.setVisibility(View.GONE);
        itemViewvideoplay.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_pause));
    }

    public void videoItemClickInitialize(View itemView) {
//        itemViewvideoView = itemView.findViewById(R.id.videoView);
       /* itemViewlinearvideo = itemView.findViewById(R.id.linearvideo);
        itemViewvideoplay = itemView.findViewById(R.id.videoplay);*/
        /*String path = "android.resource://" + getPackageName() + "/" + R.raw.video;
        itemViewvideoView.setVideoURI(Uri.parse(path));*/
        /*MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(itemViewvideoView);
        itemViewvideoView.setMediaController(mediaController);*/
        /*itemViewvideoView.setOnCompletionListener(this);*/

        Intent videoIntent = new Intent(Intent.ACTION_VIEW);
        videoIntent.setDataAndType(Uri.parse("android.resource://" + getPackageName() + R.raw.video), "video/*");
        startActivity(videoIntent);
    }

    /*@Override
    public void onCompletion(MediaPlayer mp) {
        itemViewlinearvideo.setVisibility(View.VISIBLE);
        stopPosition = 0;
        itemViewvideoplay.setImageDrawable(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play));
    }*/
}
