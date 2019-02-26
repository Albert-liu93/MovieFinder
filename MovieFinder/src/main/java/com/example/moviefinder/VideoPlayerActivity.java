package com.example.moviefinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.moviefinder.Constants.Constants;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class VideoPlayerActivity extends YouTubeBaseActivity {


    YouTubePlayerView mYouTubePlayerView;
    YouTubePlayer.OnInitializedListener mOnInitializedListener;
    String videoURL = "";
    TextView error_mesage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mYouTubePlayerView = findViewById(R.id.youtubeplayerview);
        error_mesage = findViewById(R.id.movie_player_error_message);
        if (getIntent().hasExtra("video_URL")) {
            videoURL = getIntent().getStringExtra("video_URL");
        } else {
            error_mesage.setVisibility(View.VISIBLE);
            mYouTubePlayerView.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(videoURL)) {
            mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    playVideo(youTubePlayer);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            };

            mYouTubePlayerView.initialize(Constants.YOUTUBE_API_KEY, mOnInitializedListener);
        }
    }

    private void playVideo(YouTubePlayer youtubeplayer) {
        youtubeplayer.cueVideo(videoURL);
    }
}
