package com.sunday.sundaymovie.mvp.photo;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.sunday.sundaymovie.R;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by agentchen on 2017/2/28.
 * Email agentchen97@gmail.com
 */

class PhotoViewPagerAdapter extends PagerAdapter {
    private List<String> mList;
    private Context mContext;

    PhotoViewPagerAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final ImageView view = (ImageView) LayoutInflater
                .from(mContext).inflate(R.layout.photo_view_pager_item, container, false);
        container.addView(view);
        ((Animatable) view.getDrawable()).start();
        Glide.with(mContext).load(mList.get(position)).into(new SimpleTarget<GlideDrawable>() {
            @Override
            public void onResourceReady(GlideDrawable resource
                    , GlideAnimation<? super GlideDrawable> glideAnimation) {
                view.clearAnimation();
                view.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setImageDrawable(resource);
                new PhotoViewAttacher(view);
            }
        });
        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    void replaceData(List<String> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
