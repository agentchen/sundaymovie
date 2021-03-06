package com.sunday.sundaymovie.mvp.moviedetail;

import com.sunday.sundaymovie.bean.AllPhoto;
import com.sunday.sundaymovie.bean.Movie;
import com.sunday.sundaymovie.model.AllPhotoModel;
import com.sunday.sundaymovie.model.MovieDetailModel;
import com.sunday.sundaymovie.model.StarModel;
import com.sunday.sundaymovie.mvp.person.PersonActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by agentchen on 2017/7/24.
 */

class MovieDetailPresenter implements MovieDetailContract.Presenter {
    private final int mMovieId;
    private final MovieDetailContract.View mView;
    private final MovieDetailModel mDetailModel;
    private final StarModel mStarModel;
    private Movie mMovie;
    private ArrayList<String> mPhotos;
    private CompositeDisposable mDisposables;
    private boolean mNeedMorePhotoUrl = true;

    MovieDetailPresenter(MovieDetailContract.View view, int movieId) {
        mView = view;
        mMovieId = movieId;
        mDetailModel = new MovieDetailModel();
        mStarModel = new StarModel();
        mDisposables = new CompositeDisposable();
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadMovieDetail();
    }

    private void loadMovieDetail() {
        mDetailModel.getMovieDetail(mMovieId).subscribe(new Observer<Movie>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mDisposables.add(d);
            }

            @Override
            public void onNext(@NonNull Movie movie) {
                if (movie != null) {
                    mView.hideProgressBar();
                    mMovie = movie;
                    modelToView();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                e.printStackTrace();
                mView.toast("有点问题");
                mView.finish();
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void openPhoto(int position) {
        mView.showPhoto(mPhotos, position);
        if (mNeedMorePhotoUrl) {
            new AllPhotoModel().getAllPhoto(mMovieId)
                    .map(new Function<AllPhoto, ArrayList<String>>() {
                        @Override
                        public ArrayList<String> apply(@NonNull AllPhoto allPhoto) throws Exception {
                            mNeedMorePhotoUrl = false;
                            ArrayList<String> list = new ArrayList<>(mPhotos.size() + allPhoto.getImages().size());
                            list.addAll(mPhotos);
                            for (AllPhoto.Image image : allPhoto.getImages()) {
                                list.add(image.getImage());
                            }
                            mPhotos = list;
                            return list;
                        }
                    })
                    .subscribe(new Observer<ArrayList<String>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            mDisposables.add(d);
                        }

                        @Override
                        public void onNext(@NonNull ArrayList<String> strings) {
                            mView.updatePhotos(strings);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

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
                , MovieDetailModel.getMovieReleaseText(mMovie.getBasic().getReleaseDate()
                        , mMovie.getBasic().getReleaseArea())
                , mMovie.getBasic().getMins(), mMovie.getBoxOffice().getTotalBoxDes());
        List<String> types = mMovie.getBasic().getType();
        if (types.size() > 0) {
            String type = MovieDetailModel.getMovieType(types);
            mView.showType(type);
        } else {
            mView.hideType();
        }
        mView.showMovieStory(mMovie.getBasic().getStory());
//        如果此电影没有视频，则去除视频相关view
        if (mMovie.getBasic().getVideo().getCount() == 0) {
            mView.hideVideoInfo();
        } else {
            mView.showVideoInfo(mMovie.getBasic().getVideo().getTitle(), mMovie.getBasic().getVideo().getImg());
        }

        List<Movie.BasicBean.StageImgBean.ListBean> listBean = mMovie.getBasic().getStageImg().getList();
        mPhotos = new ArrayList<>(4);
        for (int i = 0; i < listBean.size() && i < 4; i++) {
            mPhotos.add(listBean.get(i).getImgUrl());
        }
        mView.showPhotos(mPhotos);

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
        boolean isStar = mStarModel.isStar(mMovieId);
        mView.setFollowed(isStar);
    }

    @Override
    public void openAllPhoto() {
        String title = mMovie.getBasic().getName();
        if (mNeedMorePhotoUrl) {
            mView.showAllPhoto(mMovieId, title);
        } else {
            mView.showAllPhoto(mPhotos, title);
        }
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
    public void openActor(Movie.BasicBean.ActorsBean actorsBean) {
        PersonActivity.startMe(mView.getContext(), actorsBean.getActorId());
    }

    @Override
    public void star() {
        mStarModel.starMovie(mMovieId, mMovie.getBasic().getName(), mMovie.getBasic().getImg());
    }

    @Override
    public void unStar() {
        mStarModel.unstarMovie(mMovieId);
    }

    @Override
    public void unsubscribe() {
        mDisposables.clear();
    }
}
