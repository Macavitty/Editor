package com.example.tania.editor;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class RootMatEgoFragment extends Fragment {

    private TabLayout layout;
    private ViewPager pager;
    private ViewPagerAdapter adapter;
    private int currentTab;
    private LeafMatEgoFragment leafFragment;
    private Map<Integer, Stack<String>> bufferUndoMap = new HashMap<>();
    private Map<Integer, Stack<String>> bufferRedoMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.root_fragment, container, false);
        pager = view.findViewById(R.id.viewpager);
        layout = view.findViewById(R.id.sliding_tabs);
        adapter = new ViewPagerAdapter(getFragmentManager());
        pager.setAdapter(adapter);

        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                pager.setCurrentItem(currentTab);
                String tmp = adapter.getTitle(currentTab);
                MainActivity.fileName = tmp.contains("/") ? tmp : "";
                Log.d("name *", "filename: " + MainActivity.fileName + " tab: " + currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }

    public void addTab(String title, ViewPagerAdapter adapter) {
        leafFragment = new LeafMatEgoFragment();
        leafFragment.setUntaughtText("");
        // leafFragment.setArguments(bundle);
        adapter.addFrag(leafFragment, title);
        adapter.notifyDataSetChanged();
        layout.setupWithViewPager(pager);
        currentTab = adapter.getCount() - 1;
        pager.setCurrentItem(adapter.getCount() - 1);
    }

    public void deleteTab(int position) {
        Log.e("*** remove", position + "");
        ViewPagerAdapter newAdapter = new ViewPagerAdapter(getFragmentManager());
        for (int i = 0; i < adapter.getCount(); i++) {
            if (i != position) {
                newAdapter.addFrag(adapter.getFragment(i), adapter.getTitle(i));
                newAdapter.notifyDataSetChanged();
            }

        }
        this.adapter = newAdapter;
        pager.setAdapter(newAdapter);
        layout.setupWithViewPager(pager);
        currentTab = position < newAdapter.getCount() - 1 ? position : newAdapter.getCount();
        pager.setCurrentItem(currentTab);
    }

    public ViewPagerAdapter getAdapter() {
        return adapter;
    }

    public int getCurrentTab() {
        return currentTab;
    }
}
