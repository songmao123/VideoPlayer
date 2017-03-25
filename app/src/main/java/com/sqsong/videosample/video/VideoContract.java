package com.sqsong.videosample.video;

import android.view.SurfaceHolder;

import com.sqsong.videosample.base.BasePresenter;
import com.sqsong.videosample.base.BaseView;

/**
 * Created by 青松 on 2017/3/8.
 */

public interface VideoContract {

    interface VideoView extends BaseView<IVideoPresenter> {
        void onVideoSizeChanged(int width, int height);
        void onVideoPlayComplete();
    }

    interface IVideoPresenter extends BasePresenter {
        void bindSurfaceHolderAndPlayer(SurfaceHolder holder);
        void setVideoPath(String path);
        void play();
        void pause();
        boolean isPlaying();
        long getCurrentPosition();
        long getDuration();
        void seekTo(long time);
        void releasePlayer();
    }

}
