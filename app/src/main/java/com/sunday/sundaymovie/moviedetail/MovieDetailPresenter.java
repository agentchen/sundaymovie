package com.sunday.sundaymovie.moviedetail;

import com.sunday.sundaymovie.bean.AllPhoto;
import com.sunday.sundaymovie.bean.Movie;
import com.sunday.sundaymovie.model.AllPhotoModel;
import com.sunday.sundaymovie.model.MovieDetailModel;
import com.sunday.sundaymovie.model.StarModel;
import com.sunday.sundaymovie.net.callback.ImageAllCallBack;
import com.sunday.sundaymovie.net.callback.MovieCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agentchen on 2017/7/24.
 */

class MovieDetailPresenter implements MovieDetailContract.Presenter {
    private final int mMovieId;
    private final MovieDetailContract.View mView;
    private final MovieDetailModel mDetailModel;
    private final StarModel mStarModel;
    private Movie mMovie;
    private ArrayList<String> mImgsList;
    private boolean updateImages = true;

    MovieDetailPresenter(MovieDetailContract.View view, int movieId) {
        mView = view;
        mMovieId = movieId;
        mDetailModel = new MovieDetailModel();
        mStarModel = new StarModel(view.getContext());
        view.setPresenter(this);
    }

    @Override
    public void start() {
        loadMovieDetail();
    }

    @Override
    public void loadMovieDetail() {
        mDetailModel.getMovieDetail(mMovieId, new MovieCallBack() {
            @Override
            public void onResponse(Movie response) {
                mView.removeProgressBar();
                if (response == null) {
                    onError();
                } else {
                    mMovie = response;
                    modelToView();
                }
            }

            @Override
            public void onError() {
                mView.toast("有点问题");
                mView.finish();
            }
        });
    }

    @Override
    public void openPhoto(int position) {
        mView.showPhoto(mImgsList, position);
        if (updateImages) {
            updateImages = false;
            new AllPhotoModel().getAllPhoto(mMovieId, new ImageAllCallBack() {
                @Override
                public void onResponse(AllPhoto response) {
                    if (response != null) {
                        ArrayList<String> list = new ArrayList<>(mImgsList.size() + response.getImages().size());
                        list.addAll(mImgsList);
                        for (AllPhoto.Image image : response.getImages()) {
                            list.add(image.getImage());
                        }
                        mView.updatePhotos(list);
                        mImgsList = list;
                    }
                }

                @Override
                public void onError() {
                }
            });
        }
    }

    private void modelToView() {
        List<Movie.BasicBean.StageImgBean.ListBean> imgs = mMovie.getBasic().getStageImg().getList();
        if (imgs.size() != 0) {
            mView.showTopImage(imgs.get(0).getImgUrl());
        }
        mView.showMainImage(mMovie.getBasic().getImg());
        mView.showBasicInfo(mMovie.getBasic().getName(), mMovie.getBasic().getNameEn()
                , mMovie.getBasic().isIs3D(), mMovie.getBasic().getOverallRating()
                , mMovie.getBasic().getDirector().getName()
                , mDetailModel.getMovieReleaseText(mMovie.getBasic().getReleaseDate()
                        , mMovie.getBasic().getReleaseArea())
                , mMovie.getBasic().getMins(), mMovie.getBoxOffice().getTotalBoxDes());
        List<String> types = mMovie.getBasic().getType();
        if (types.size() > 0) {
            String type = mDetailModel.getMovieType(types);
            mView.showType(type);
        } else {
            mView.removeType();
        }
        mView.showMovieStory(mMovie.getBasic().getStory());
//        如果此电影没有视频，则去除视频相关view
        if (mMovie.getBasic().getVideo().getCount() == 0) {
            mView.removeVideoInfo();
        } else {
            mView.showVideoInfo(mMovie.getBasic().getVideo().getTitle(), mMovie.getBasic().getVideo().getImg());
        }

        List<Movie.BasicBean.StageImgBean.ListBean> listBean = mMovie.getBasic().getStageImg().getList();
        mImgsList = new ArrayList<>(4);
        for (int i = 0; i < listBean.size() && i < 4; i++) {
            mImgsList.add(listBean.get(i).getImgUrl());
        }
        mView.showPhotos(mImgsList);

        List<Movie.BasicBean.ActorsBean> actors = mMovie.getBasic().getActors();
//        把导演插入到演员list
        Movie.BasicBean.ActorsBean ab = new Movie.BasicBean.ActorsBean();
        Movie.BasicBean.DirectorBean directorBean = mMovie.getBasic().getDirector();
        ab.setActorId(directorBean.getDirectorId());
        ab.setImg(directorBean.getImg());
        ab.setName(directorBean.getName());
        ab.setRoleName("导演");
        actors.add(0, ab);
        mView.showActor(actors);
        mView.setFollowed(mStarModel.isExist(mMovieId));
    }

    @Override
    public void openAllPhoto() {
        mView.showAllPhoto(mMovieId, mMovie.getBasic().getName());
    }

    @Override
    public void openVideo() {
        Movie.BasicBean.VideoBean bean = mMovie.getBasic().getVideo();
        mView.showVideo(bean.getHightUrl(), bean.getTitle());
    }

    @Override
    public void openAllVideo() {
        mView.showAllVideo(mMovieId, mMovie.getBasic().getName());
    }

    @Override
    public void star() {
        mStarModel.insertMovie(mMovieId, mMovie.getBasic().getName(), mMovie.getBasic().getImg());
    }

    @Override
    public void unStar() {
        mStarModel.deleteMovie(mMovieId);
    }

    @Override
    public void onDestroy() {
        mStarModel.close();
    }
}
