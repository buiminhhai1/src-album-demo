package com.example.hi.album;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter  extends FragmentStatePagerAdapter{
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

         @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0: AnhFragment anhFragment=new AnhFragment();
                    return anhFragment;
                case 1: AlbumFragment albumFragment=new AlbumFragment();
                    return albumFragment;
            }
            return null;

    }

    @Override
    public int getCount() {
        return 2;
    }
}
