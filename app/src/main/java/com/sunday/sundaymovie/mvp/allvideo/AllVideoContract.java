package com.sunday.sundaymovie.mvp.allvideo;

import com.sunday.sundaymovie.base.BasePresenter;
import com.sunday.sundaymovie.base.BaseView;
import com.sunday.sundaymovie.bean.VideoAll;

import java.util.List;

/**
 * Created by agentchen on 2017/8/3.
 */

interface AllVideoContract {
    interface View extends BaseView<AllVideoContract.Presenter> {
        void removeProgressBar();

        void showAllVideo(List<VideoAll.Video> list);

        void showVideo(String url, String title);

        void toast(String text);

        void removeScrollEndListener();

        void showTitle(String title);
    }

    interface Presenter extends BasePresenter {
        void openVideo(VideoAll.Video video);

        void scrollEnd();
    }
}
