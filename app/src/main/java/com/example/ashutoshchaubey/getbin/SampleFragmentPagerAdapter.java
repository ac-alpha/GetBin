package com.example.ashutoshchaubey.getbin;

/**
 * Created by ashutoshchaubey on 09/03/18.
 */
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ashutoshchaubey on 05/03/18.
 */

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {

        super(fm);
        this.context=context;

    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new OneFragment();
        }else if(position==1) {
            return new TwoFragment();
        }else{
            return new ThreeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }


}
