package com.smithyproductions.supertracks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewAnimator;

public class MainActivity extends AppCompatActivity {

    private ScaleGestureDetector mScaleDetector;
    private ImageView mPlayImageView;
    private ObjectAnimator upscaleAnimatorX;
    private ObjectAnimator upscaleAnimatorY;
    private ObjectAnimator downscaleAnimatorX;
    private ObjectAnimator downscaleAnimatorY;
    private ProgressBar mProgressBar;
    private View mBannerAd;
    private View mBankAd;
    private View mFullscreenAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = findViewById(R.id.root_view);

        mBannerAd = findViewById(R.id.bannerAd);
        mBankAd = findViewById(R.id.bankAd);
        mFullscreenAd = findViewById(R.id.fullscreenAd);

        mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPlayImageView = (ImageView) findViewById(R.id.playImageView);

        upscaleAnimatorX = ObjectAnimator.ofFloat(mPlayImageView, "scaleX", 1f).setDuration(600);
        upscaleAnimatorY = ObjectAnimator.ofFloat(mPlayImageView, "scaleY", 1f).setDuration(600);
        downscaleAnimatorX = ObjectAnimator.ofFloat(mPlayImageView, "scaleX", 0f).setDuration(200);
        downscaleAnimatorY = ObjectAnimator.ofFloat(mPlayImageView, "scaleY", 0f).setDuration(200);
        upscaleAnimatorX.setInterpolator(new BounceInterpolator());
        upscaleAnimatorY.setInterpolator(new BounceInterpolator());
        downscaleAnimatorX.setInterpolator(new AccelerateDecelerateInterpolator());
        downscaleAnimatorY.setInterpolator(new AccelerateDecelerateInterpolator());

        downscaleAnimatorY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPlayImageView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);

                MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.sweet_dreams);
                mediaPlayer.start();

                mBannerAd.setVisibility(View.VISIBLE);
                final Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBankAd.setVisibility(View.VISIBLE);
                    }
                }, 1000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFullscreenAd.setVisibility(View.VISIBLE);
                    }
                }, 2000);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        throw new RuntimeException("You only had one job...");
                    }
                }, 3000);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() >= 5) {
                    mScaleDetector.onTouchEvent(event);
                }

                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            onTouchFinish();
                            break;
                        case MotionEvent.ACTION_DOWN:
                            onTouchBegin();
                            break;
                    }
                }
                return true;
            }
        });

    }

    private void onTouchBegin() {
        upscaleAnimatorX.cancel();
        upscaleAnimatorY.cancel();
    }

    private void onTouchFinish() {
        if(mPlayImageView.getScaleX() < 0.6f){
            //scale down
            downscaleAnimatorX.start();
            downscaleAnimatorY.start();

        }else{
            //scale back up
            upscaleAnimatorX.start();
            upscaleAnimatorY.start();
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = mPlayImageView.getScaleX() * detector.getScaleFactor();
            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 1.0f));

            if(!downscaleAnimatorY.isStarted() && mPlayImageView.getVisibility() == View.VISIBLE) {
                mPlayImageView.setScaleX(scaleFactor);
                mPlayImageView.setScaleY(scaleFactor);
            }

            return true;
        }
    }

}
