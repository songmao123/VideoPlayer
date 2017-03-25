package com.sqsong.videosample;

import android.content.Context;

import com.sqsong.videosample.base.BasePresenter;
import com.sqsong.videosample.base.BaseView;
import com.sqsong.videosample.bean.VideoBean;

import java.util.List;

/**
 * Created by 青松 on 2017/3/7.
 */

public interface MainContract {

    interface MainView extends BaseView<Presenter> {

        Context getContext();

        void updateVideoList(List<VideoBean> lists);

        void showEmpty();
    }

    interface Presenter extends BasePresenter {

        void fetchVideos();

        void release();
    }

}
