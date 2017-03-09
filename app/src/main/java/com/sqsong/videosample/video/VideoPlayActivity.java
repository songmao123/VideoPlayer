package com.sqsong.videosample.video;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sqsong.videosample.MainActivity;
import com.sqsong.videosample.R;
import com.sqsong.videosample.bean.VideoBean;
import com.sqsong.videosample.util.DensityUtil;

import java.util.Formatter;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoPlayActivity extends AppCompatActivity implements View.OnClickListener, VideoContract.VideoView {

    public static final int DEFAULT_SHOW_TIME = 10000;

    @BindView(R.id.root_view)
    View mRootView;

    @BindView(R.id.surfaceview)
    SurfaceView mSurfaceView;

    @BindView(R.id.title_ll)
    LinearLayout mTitleContainer;

    @BindView(R.id.back_iv)
    ImageView mBackBtn;

    @BindView(R.id.title_tv)
    TextView mTitleText;

    @BindView(R.id.control_ll)
    LinearLayout mControlContainer;

    @BindView(R.id.rewind_iv)
    ImageView mRewindBtn;

    @BindView(R.id.play_iv)
    ImageView mPlayBtn;

    @BindView(R.id.forward_iv)
    ImageView mForwardBtn;

    @BindView(R.id.play_time_tv)
    TextView mPlayTimeText;

    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    @BindView(R.id.duration_tv)
    TextView mDurationText;

    private boolean isDragging;
    private VideoBean mVideoInfo;
    private Formatter mFormatter;
    private boolean isPlaying = true;
    private boolean isShowing = true;
    private int mTitleAnimHeight = -1;
    private int mControlAnimHeight = -1;
    private StringBuilder mFormatBuilder;
    private VideoContract.IVideoPresenter mPresenter;

    private Runnable measureHeightRunnable = new Runnable() {
        @Override
        public void run() {
            // 计算出title栏和control栏需要向上向下动画的距离
            mTitleAnimHeight = mTitleContainer.getMeasuredHeight() +
                    DensityUtil.getStatusBarHeight(getApplicationContext());
            mControlAnimHeight = mControlContainer.getMeasuredHeight();
        }
    };

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            long pos = setSeekProgress();
            if (!isDragging && mPresenter.isPlaying()) {
                long delay = 1000 - (pos % 1000);
                Log.i("sqsong", "Delay Time: " + delay + " ms.");
                mSeekBar.postDelayed(progressRunnable, /*delay*/1000);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) return;

            long duration = mPresenter.getDuration();
            long position = (duration * progress) / 1000L;
            mPresenter.seekTo(position);
            mPlayTimeText.setText(stringForTime((int) position));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isDragging = true;
            mSeekBar.removeCallbacks(progressRunnable);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            isDragging = false;
            mSeekBar.post(progressRunnable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor();
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        getIntentParams();
        initSurfaceView();
        initEvent();
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void getIntentParams() {
        Intent intent = getIntent();
        if (intent != null) {
            mVideoInfo = intent.getParcelableExtra(MainActivity.VIDEO_INFO);
            if (mVideoInfo != null) {
                mTitleText.setText(mVideoInfo.getVideoName());
            }
        }
    }

    private void initSurfaceView() {
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPresenter.bindSurfaceHolderAndPlayer(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });
    }

    private void initEvent() {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        mTitleContainer.setPadding(0, DensityUtil.getStatusBarHeight(this), 0, 0);
        mSeekBar.setMax(1000);
        mSeekBar.postDelayed(progressRunnable, 500);

        mRootView.post(measureHeightRunnable);
        mBackBtn.setOnClickListener(this);
        mPlayBtn.setOnClickListener(this);
        mRootView.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        mPresenter = new VideoPresenter(this);
        mSeekBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.setVideoPath(mVideoInfo.getVideoPath());
            }
        }, 300);
        delayHideControlPanel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.root_view:
                toggleShowControlPanel();
                break;
            case R.id.play_iv:
                toggleVideoPlayStatus();
                break;
        }
    }

    private void toggleVideoPlayStatus() {
        if (isPlaying) {
            mPresenter.pause();
            mPlayBtn.setImageResource(R.mipmap.icon_play);
            isPlaying = false;
            mSeekBar.removeCallbacks(progressRunnable);
        } else {
            mPresenter.play();
            mPlayBtn.setImageResource(R.mipmap.icon_pause);
            isPlaying = true;
            mSeekBar.post(progressRunnable);
        }
    }

    private void toggleShowControlPanel() {
        if (isShowing) {
            hideController();
        } else {
            showController();
        }
    }

    private void showController() {
        showStatusBar();
        ObjectAnimator.ofFloat(mTitleContainer, "translationY", -mTitleAnimHeight, 0).setDuration(300).start();
        ObjectAnimator.ofFloat(mControlContainer, "translationY", mControlAnimHeight, 0).setDuration(300).start();
        isShowing = true;
        delayHideControlPanel();
    }

    private void delayHideControlPanel() {
        if (!isShowing) return;
        mTitleContainer.removeCallbacks(hideControlRunnable);
        mTitleContainer.postDelayed(hideControlRunnable, DEFAULT_SHOW_TIME);
    }

    private Runnable hideControlRunnable = new Runnable() {
        @Override
        public void run() {
            hideController();
        }
    };

    private void hideController() {
        hideStatusBar();
        ObjectAnimator.ofFloat(mTitleContainer, "translationY", 0, -mTitleAnimHeight).setDuration(300).start();
        ObjectAnimator.ofFloat(mControlContainer, "translationY", 0, mControlAnimHeight).setDuration(300).start();
        isShowing = false;
        mTitleContainer.removeCallbacks(hideControlRunnable);
    }

    private void showStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Show Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private long setSeekProgress() {
        if (isDragging) {
            return 0;
        }
        long curPos = mPresenter.getCurrentPosition();
        long duration = mPresenter.getDuration();
        if (duration > 0) {
            long pos = 1000 * curPos / duration;
            mSeekBar.setProgress((int) pos);
        }
        mPlayTimeText.setText(stringForTime(curPos));
        mDurationText.setText(stringForTime(duration));
        return curPos;
    }

    private String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours   = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public void setPresenter(VideoContract.IVideoPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onVideoSizeChanged(int width, int height) {
        ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        int newWidth = DensityUtil.getScreenWidth();
        int newHeight = newWidth * height / width;
        params.width = newWidth;
        params.height = newHeight;
        mSurfaceView.getHolder().setFixedSize(newWidth, newHeight);
        mSurfaceView.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.releasePlayer();
    }
}
