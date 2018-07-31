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


public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragmentsList = new ArrayList<>();
    private ArrayList<String> titlesList = new ArrayList<>();
    private ViewPager pager;
    private TabLayout layout;

    public MainPagerAdapter(FragmentManager manager, Context context, ViewPager pager, TabLayout layout) {
        super(manager);
        this.pager = pager;
        this.layout = layout;
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
        return titlesList.get(position);
    }

    public void setPageTitle(int position, String title) { // use it after open
        titlesList.set(position, title);
    }

    public void addFrag(Fragment f, String s) {
        fragmentsList.add(f);
        titlesList.add(s);
    }

    public void removeFrag(int position) {
        removeTab(position);
        Fragment f = fragmentsList.get(position);
        fragmentsList.remove(f);
        titlesList.remove(position);
        destroyFragmentView(pager, position, f);
        pager.setOffscreenPageLimit(200);
        notifyDataSetChanged();
    }

    public void removeTab(int position) {
        if (layout.getChildCount() > 0) {
            layout.removeTabAt(position);
        }
    }

    public void destroyFragmentView(ViewGroup container, int position, Object object) {
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
    }


}
