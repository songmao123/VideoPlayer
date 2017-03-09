package com.sqsong.videosample;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.sqsong.videosample.bean.VideoBean;
import com.sqsong.videosample.util.VideoUtils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 青松 on 2017/3/7.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.MainView mMainView;

    public MainPresenter(MainContract.MainView mainView) {
        this.mMainView = mainView;
        mMainView.setPresenter(this);
    }

    @Override
    public void fetchVideos() {
        Observable.just("")
                .map(new Function<String, List<VideoBean>>() {
                    @Override
                    public List<VideoBean> apply(String s) throws Exception {
                        List<VideoBean> videoInfos = new ArrayList<>();
                        StringBuilder formatBuilder = new StringBuilder();
                        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

                        Cursor cursor = mMainView.getContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                new String[]{MediaStore.Video.VideoColumns.DATA}, null, null, null);
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                String path = cursor.getString(0);
                                VideoBean bean = new VideoBean();
                                bean.setVideoPath(path);
                                bean.setVideoName(path.substring(path.lastIndexOf("/") + 1, path.length()));
                                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                                bean.setVideoThumb(bitmap);

                                retriever.setDataSource(path);
                                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                bean.setDuration(VideoUtils.formatTime(formatter, formatBuilder, time));
                                videoInfos.add(bean);
                                Log.i("sqsong", "Path --- > " + path + ", Time: " + time);
                            }
                            cursor.close();
                        }
                        return videoInfos;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<VideoBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onNext(List<VideoBean> value) {
                        mMainView.updateVideoList(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMainView.showEmpty();
                    }

                    @Override
                    public void onComplete() {}
                });

    }

}
