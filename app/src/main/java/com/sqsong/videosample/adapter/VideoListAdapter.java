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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 青松 on 2017/3/7.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {

    private Context mContext;
    private List<VideoBean> mVideoLists;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClicked(VideoBean videoInfo);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mListener = l;
    }

    public VideoListAdapter(Context context, List<VideoBean> videoLists) {
        this.mContext = context;
        this.mVideoLists = videoLists;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        final VideoBean bean = mVideoLists.get(position);
        holder.thumb_iv.setImageBitmap(bean.getVideoThumb());
        holder.duration_tv.setText(bean.getDuration());
        holder.name_tv.setText(bean.getVideoName());
        holder.path_tv.setText(bean.getVideoPath());
        holder.name_tv.setSelected(true);
        holder.path_tv.setSelected(true);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(bean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideoLists.size();
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
}
