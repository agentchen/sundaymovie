package com.sunday.sundaymovie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.GridView;

import com.sunday.sundaymovie.R;
import com.sunday.sundaymovie.adapter.ImgAllGridViewAdapter;
import com.sunday.sundaymovie.api.Api;
import com.sunday.sundaymovie.model.ImageAll;
import com.sunday.sundaymovie.net.OkManager;
import com.sunday.sundaymovie.net.callback.ImageAllCallBack;

import java.util.ArrayList;
import java.util.List;

public class ImageAllActivity extends BaseActivity {
    private GridView mGridView;
    private int mMovieId;
    private String mTitle;
    private ImageAll mImageAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(mTitle);
        OkManager.getInstance().asyncGet(Api.getImageAllUrl(mMovieId), new ImageAllCallBack() {
            @Override
            public void onResponse(ImageAll response) {
                mImageAll = response;
                modelToView();
            }

            @Override
            public void onError(Exception e) {
                finish();
            }
        });
    }

    @Override
    protected void initParams(Bundle bundle) {
        mMovieId = bundle.getInt("movieId");
        mTitle = bundle.getString("title");
    }

    @Override
    protected void initView(Context context) {
        setContentView(R.layout.activity_image_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.image_all_toolbar);
        setSupportActionBar(toolbar);
        mGridView = (GridView) findViewById(R.id.image_all_grid_view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return true;
    }

    private void modelToView() {
        mGridView.setAdapter(new ImgAllGridViewAdapter(getImgUrls(), this));
    }

    private List<String> getImgUrls() {
        List<String> list = new ArrayList<>(mImageAll.getImages().size());
        for (ImageAll.Image image : mImageAll.getImages()) {
            list.add(image.getImage());
        }
        return list;
    }

    public static void startMe(Context context, int movieId, String movieName) {
        Intent intent = new Intent(context, ImageAllActivity.class);
        intent.putExtra("movieId", movieId);
        intent.putExtra("title", movieName);
        context.startActivity(intent);
    }
}
