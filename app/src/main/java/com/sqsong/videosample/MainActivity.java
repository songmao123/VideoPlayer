package com.sqsong.videosample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sqsong.videosample.adapter.VideoListAdapter;
import com.sqsong.videosample.bean.VideoBean;
import com.sqsong.videosample.video.VideoPlayActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainContract.MainView, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, VideoListAdapter.OnItemClickListener {

    private static final int PERMISSION_CODE_READ_EXTERNAL_STORAGE = 100;
    private static final int TPYE_TIP_PERMISSION_DENY = 1;
    private static final int TPYE_TIP_NO_VIDEO_FILE = 2;
    public static final String VIDEO_INFO = "video_info";

    @BindView(R.id.toolbar)
    Toolbar mToolBar;

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.tips_ll)
    LinearLayout mTipsLl;

    @BindView(R.id.tips_tv)
    TextView mTipsTv;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout mSwipeLayout;

    private VideoListAdapter mVideoAdapter;
    private MainContract.Presenter mPresenter;
    private List<VideoBean> mVideoLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        initEvent();
    }

    private void initEvent() {
        setSupportActionBar(mToolBar);

        mTipsLl.setOnClickListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);
        mSwipeLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mVideoAdapter = new VideoListAdapter(this, mVideoLists);
        mVideoAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mVideoAdapter);

        mPresenter = new MainPresenter(this);

        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { // 获取SD卡读权限
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_CODE_READ_EXTERNAL_STORAGE);
        } else {
            getVideoList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getVideoList();
            } else {
                showTips(TPYE_TIP_PERMISSION_DENY);
            }
        }
    }

    private void getVideoList() {
        mSwipeLayout.setRefreshing(true);
        mPresenter.fetchVideos();
    }

    @Override
    public void onRefresh() {
        mPresenter.fetchVideos();
    }

    private void showTips(int type) {
        mRecyclerView.setVisibility(View.GONE);
        mTipsLl.setVisibility(View.VISIBLE);
        mSwipeLayout.setEnabled(false);
        if (type == TPYE_TIP_PERMISSION_DENY) {
            mTipsTv.setText("Please allow application access SDCard!");
        } else {
            mTipsTv.setText("There is no video on the phone!");
        }
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void updateVideoList(List<VideoBean> lists) {
        mSwipeLayout.setRefreshing(false);
        if (lists == null || lists.size() < 1) {
            showEmpty();
            return;
        }
        mVideoLists.clear();
        mVideoLists.addAll(lists);
        mVideoAdapter.notifyDataSetChanged();

        mTipsLl.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showEmpty() {
        showTips(TPYE_TIP_NO_VIDEO_FILE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tips_ll:
                mSwipeLayout.setEnabled(true);
                checkPermission();
                break;
        }
    }

    @Override
    public void onItemClicked(VideoBean videoInfo) {
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra(VIDEO_INFO, videoInfo);
        startActivity(intent);
    }
}
