package com.aaburov.tedrssviewer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Created by Giorgio on 16.05.2015.
 */
public class VideoViewActivity extends Activity {

    private VideoView videoView;
    private int position = 0;
    private MediaController mMediaController;
    private String PositionInUrl ;
    private static final String TAG = "VideoViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        if (null !=url){
            setContentView(R.layout.activity_videoview);
            videoView = (VideoView) findViewById(R.id.videoView);

            mMediaController = new MediaController(this);
            mMediaController.setAnchorView(videoView);
            mMediaController.setMediaPlayer(videoView);
            Uri video = Uri.parse(url);
            PositionInUrl="pos:"+url;
            videoView.setMediaController(mMediaController);
            videoView.setVideoURI(video);
        }

    }

    protected void onResume(){
        super.onResume();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        position = preferences.getInt(PositionInUrl, 0);
        //Log.d(TAG, "Loadpos=" + position);
        videoView.seekTo(position);
        videoView.start();
    }

    protected void onPause(){
        super.onPause();
        position = videoView.getCurrentPosition();
        //Log.d(TAG, "SAVEpos=" + videoView.getCurrentPosition());
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PositionInUrl,position);
        editor.commit();
        videoView.pause();
    }

}
