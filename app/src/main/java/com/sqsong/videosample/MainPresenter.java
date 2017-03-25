package com.sqsong.videosample;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.sqsong.videosample.bean.VideoBean;
import com.sqsong.videosample.util.VideoUtils;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 青松 on 2017/3/7.
 */

public class MainPresenter implements MainContract.Presenter {

    private MainContract.MainView mMainView;
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    public MainPresenter(MainContract.MainView mainView) {
        this.mMainView = mainView;
        mMainView.setPresenter(this);
    }

    @Override
    public void fetchVideos() {
        mDisposable.add(videoListObservable(mMainView.getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<VideoBean>>() {
                    @Override
                    public void onNext(List<VideoBean> value) {
                        if (value != null && !value.isEmpty()) {
                            mMainView.updateVideoList(value);
                        } else {
                            mMainView.showEmpty();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(mMainView.getContext(), "Get SDCard Video Error!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {}
                }));
    }

    static Observable<List<VideoBean>> videoListObservable(final Context context) {
        return Observable.defer(new Callable<ObservableSource<? extends List<VideoBean>>>() {
            @Override
            public ObservableSource<? extends List<VideoBean>> call() throws Exception {
                return Observable.create(new ObservableOnSubscribe<List<VideoBean>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<VideoBean>> e) throws Exception {
                        e.onNext(getVideoInfoList(context));
                        e.onComplete();
                    }
                });
            }
        });
    }

    static List<VideoBean> getVideoInfoList(Context context) {
        List<VideoBean> videoInfos = new ArrayList<>();
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
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

    @Override
    public void release() {
        mDisposable.clear();
        mMainView = null;
    }

}
