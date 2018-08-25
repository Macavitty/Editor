package com.example.tania.editor;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentsList = new ArrayList<>();
    private ArrayList<String> titlesList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragmentsList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String tmp = titlesList.get(position);
        return tmp.contains("/") ? tmp.substring(tmp.lastIndexOf("/") + 1, tmp.length()) : tmp;
    }

    public void setPageTitle(int position, String title) { // use it after open
        try {
            titlesList.set(position, title);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void addFrag(Fragment f, String s) {
        fragmentsList.add(f);
        titlesList.add(s);
    }

    public Fragment getFragment(int position) {
        return fragmentsList.get(position);
    }

    public String getTitle(int position) {
        return titlesList.get(position);
    }
}



