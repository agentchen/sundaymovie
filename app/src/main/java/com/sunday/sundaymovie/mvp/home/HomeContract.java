package com.sunday.sundaymovie.mvp.home;

import android.content.Context;

import com.sunday.sundaymovie.base.BasePresenter;
import com.sunday.sundaymovie.base.BaseView;

import java.util.List;

/**
 * Created by agentchen on 2017/7/23.
 */

interface HomeContract {
    interface View<T> extends BaseView<Presenter> {
        void setRefreshing(boolean refreshing);

        void snackBar(String text);

        void showNetError();

        void removeNetError();

        void showMovies(List<T> list);

        void showMovieDetail(int id);

        void smoothScrollToTop();

        Context getContext();
    }

    interface Presenter extends BasePresenter {

        void refresh();

        void star(int id, String name, String imageURL);

        void unStar(int id);

        void openMovieDetail(int id);
    }
}
