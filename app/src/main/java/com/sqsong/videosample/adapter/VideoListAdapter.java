package com.sqsong.videosample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sqsong.videosample.R;
import com.sqsong.videosample.bean.VideoBean;
import com.sqsong.videosample.util.DensityUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 青松 on 2017/3/7.
 */

public class VideoListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_BLANK_ITEM = 0;
    private static final int VIEW_TYPE_NORMAL_ITEM = 1;

    private LayoutInflater mInflater;
    private List<VideoBean> mVideoLists;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClicked(VideoBean videoInfo);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mListener = l;
    }

    public VideoListAdapter(Context context, List<VideoBean> videoLists) {
        this.mVideoLists = videoLists;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mVideoLists.size()) {
            return VIEW_TYPE_BLANK_ITEM;
        } else {
            return VIEW_TYPE_NORMAL_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_BLANK_ITEM) {
            View view = mInflater.inflate(R.layout.item_blank, parent, false);
            return new BlankViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_NORMAL_ITEM && holder instanceof VideoViewHolder) {
            VideoViewHolder videoHolder = (VideoViewHolder) holder;

            final VideoBean bean = mVideoLists.get(position);
            videoHolder.thumb_iv.setImageBitmap(bean.getVideoThumb());
            videoHolder.duration_tv.setText(bean.getDuration());
            videoHolder.name_tv.setText(bean.getVideoName());
            videoHolder.path_tv.setText(bean.getVideoPath());
            videoHolder.name_tv.setSelected(true);
            videoHolder.path_tv.setSelected(true);
            videoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClicked(bean);
                    }
                }
            });
        } else {
            // blank view, do nothing.
        }
    }

    @Override
    public int getItemCount() {
        return mVideoLists.size() + 1;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.thumb_iv)
        ImageView thumb_iv;

        @BindView(R.id.duration_tv)
        TextView duration_tv;

        @BindView(R.id.name_tv)
        TextView name_tv;

        @BindView(R.id.path_tv)
        TextView path_tv;

        View itemView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }

    class BlankViewHolder extends RecyclerView.ViewHolder {

        public BlankViewHolder(View itemView) {
            super(itemView);
        }
    }
}
