/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.tv.TvInputInfo;
import android.media.tv.TvView;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;
    private static final int NUM_ROWS = 6;
    private static final int NUM_COLS = 15;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;
    private VideoView mVideoView;
    private PopupWindow mPopWindow;
    private TvView mTvPlayer;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if (item instanceof Movie) {
                Log.d(TAG, "select Item: " + item.toString() + "row is " );
                mBackgroundUri = ((Movie) item).getBackgroundImageUrl();
                startBackgroundTimer();
                if (row.getId() == 0){
                    initPopWindow(itemViewHolder.view);
                    TvPlayerController.tune(2408);
                }else {
                    playVideoTask(row.getId());
                }
            }
        }
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Toast.makeText(getActivity(),"完成播放",Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideoTask(final long index){
//        if(mVideoView.isPlaying()){
//            mVideoView.stopPlayback();
//        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                String videoUri;
                if (index == 1){
                    TvPlayerController.tune(2409);
//                    videoUri = "https://v-cdn.zjol.com.cn/280443.mp4";
                }else if(index == 2){
                    TvPlayerController.tune(2410);
//                    videoUri = "https://v-cdn.zjol.com.cn/276985.mp4";
                }else if(index == 3){
                    TvPlayerController.tune(2411);
//                    videoUri = "https://www.w3schools.com/html/movie.mp4";
                }else if(index == 4){
                    TvPlayerController.tune(2412);
//                    videoUri = "https://v-cdn.zjol.com.cn/276982.mp4";
                }else{
                    TvPlayerController.tune(2413);
//                    videoUri = "https://v-cdn.zjol.com.cn/276984.mp4";
                }
//                Uri uri = Uri.parse(videoUri);
//                mVideoView.setMediaController(new MediaController(getActivity()));
//                mVideoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
//                mVideoView.setVideoURI(uri);
//                mVideoView.start();
            }
        });
    }


//    private Context mContext = MainActivity.this;
    private void initPopWindow(View v) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.test_popup_window, null, false);
//        mVideoView = view.findViewById(R.id.videoView);
        mTvPlayer = view.findViewById(R.id.tv_player);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        mPopWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);

        mPopWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        //这些为了点击非PopupWindow区域，PopupWindow会消失的，如果没有下面的
        //代码的话，你会发现，当你把PopupWindow显示出来了，无论你按多少次后退键
        //PopupWindow并不会关闭，而且退不出程序，加上下述代码可以解决这个问题
        mPopWindow.setTouchable(false);
        mPopWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        mPopWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

        TvSupportHolder.configPlayer(mTvPlayer);

        //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
        mPopWindow.showAsDropDown(v, 0, 0);
    }


    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }


    private void loadRows() {
        List<Movie> list = MovieList.setupMovies();

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        int i;
        for (i = 0; i < NUM_ROWS; i++) {
            if (i != 0) {
                Collections.shuffle(list);
            }
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < NUM_COLS; j++) {
                listRowAdapter.add(list.get(j % 5));
            }
            HeaderItem header = new HeaderItem(i, MovieList.MOVIE_CATEGORY[i]);
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        HeaderItem gridHeader = new HeaderItem(i, "PREFERENCES");

        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.account));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));
        String channelid = MtkTvConfig.getInstance().getConfigString(MtkTvConfigType.CFG_NAV_AIR_CRNT_CH);
        Log.d(TAG, "getConfigValue current lang is : " + channelid);

        setAdapter(rowsAdapter);
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getContext(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle(getString(R.string.browse_title)); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(false);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getContext(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getContext(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                initPopWindow(view);
            }
        });

        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setBrowseTransitionListener(new BrowseTransitionListener(){
            @Override
            public void onHeadersTransitionStart(boolean withHeaders) {
            }

            @Override
            public void onHeadersTransitionStop(boolean withHeaders) {
                Log.d(TAG, " onHeadersTransitionStop: " + withHeaders);
                if (false == withHeaders){
                    mTvPlayer.reset();
                    TvSupportHolder.releaseTvPlayer();
                    mPopWindow.dismiss();
                }
            }
        });
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;

        Log.d(TAG, "updateBackground: " + width + " x " + height + uri);
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "setDrawable: " + resource.toString());
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "onItemClicked Movie: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME)
                        .toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                Log.d(TAG, "onItemClicked String: " + item.toString());
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
