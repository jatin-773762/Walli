package com.example.walli.Source_Details;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class BottomAdapter extends FragmentPagerAdapter {


    public BottomAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }

    public void addFrag(Fragment fragment){
        fragmentArrayList.add(fragment);
    }
}
