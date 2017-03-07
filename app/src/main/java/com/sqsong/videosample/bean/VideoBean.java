package com.sqsong.videosample.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 青松 on 2017/3/7.
 */

public class VideoBean implements Parcelable {

    private String videoName;
    private Bitmap videoThumb;
    private String videoPath;
    private String duration;

    public VideoBean() {}

    protected VideoBean(Parcel in) {
        videoName = in.readString();
        videoThumb = in.readParcelable(Bitmap.class.getClassLoader());
        videoPath = in.readString();
        duration = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(videoName);
        dest.writeParcelable(videoThumb, flags);
        dest.writeString(videoPath);
        dest.writeString(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel in) {
            return new VideoBean(in);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public Bitmap getVideoThumb() {
        return videoThumb;
    }

    public void setVideoThumb(Bitmap videoThumb) {
        this.videoThumb = videoThumb;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
