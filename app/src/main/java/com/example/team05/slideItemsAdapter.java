/**
 * This is the class to define the image layouts to be used on the welcome screen
 *
 * NO EDITING NEEDED
 *
 * created by Harry Akitt 16/03/2022
 * **/

package com.example.team05;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class slideItemsAdapter extends PagerAdapter {

    private Context Mcontext;
    private List<slideItems> theSlideItemsModelClassList;

    public slideItemsAdapter(Context Mcontext, List<slideItems> theSlideItemsModelClassList) {
        this.Mcontext = Mcontext;
        this.theSlideItemsModelClassList = theSlideItemsModelClassList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) Mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.welcome_items,null);

        ImageView featured_image = sliderLayout.findViewById(R.id.my_featured_image);

        featured_image.setImageResource(theSlideItemsModelClassList.get(position).getFeatured_image());
        container.addView(sliderLayout);
        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return theSlideItemsModelClassList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

}
