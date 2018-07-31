package com.example.tania.editor;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class RootMatEgoFragment extends Fragment {

    private TabLayout layout;
    private ViewPager pager;
    private MainPagerAdapter adapter;
    private int currentTab;
    private LeafMatEgoFragment leafMatEgoFragment;
    private Map<Integer, Stack<String>> bufferUndoMap = new HashMap<>();
    private Map<Integer, Stack<String>> bufferRedoMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.root_fragment, container, false);
        pager = view.findViewById(R.id.viewpager);

        // это очень важный костыль, обеспечивающий корректную работу undo/redo
        pager.setOffscreenPageLimit(200);

        layout = view.findViewById(R.id.sliding_tabs);
        adapter = new MainPagerAdapter(getFragmentManager(), getActivity(), pager, layout);
        pager.setAdapter(adapter);
        layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //super.onTabSelected(tab);
                pager.setCurrentItem(tab.getPosition());
                currentTab = tab.getPosition();
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

    public void addTab(String title) {
        Bundle bundle = new Bundle();
        bundle.putString("data", title);
        currentTab = adapter.getCount(); // DO NOT REMOVE: it should be here !!!!
        Log.e("add: ", currentTab+"");

        leafMatEgoFragment = new LeafMatEgoFragment();
        leafMatEgoFragment.setArguments(bundle);
        bufferRedoMap.put(currentTab, new Stack<String>());
        bufferUndoMap.put(currentTab, new Stack<String>());
        adapter.addFrag(leafMatEgoFragment, title);
        adapter.notifyDataSetChanged();
        layout.setupWithViewPager(pager);
        pager.setCurrentItem(adapter.getCount()-1);
        leafMatEgoFragment.setTextWatcher();
        }

    public void deleteTab(int position, Map<Integer, Stack<String>> u, Map<Integer, Stack<String>> r) {
        bufferRedoMap = new HashMap<>();
        bufferUndoMap = new HashMap<>();

        for (Map.Entry me : u.entrySet()) {
            int p = (Integer)me.getKey();
            Stack<String> s = (Stack<String>)me.getValue();
            if (p < position){
                bufferUndoMap.put(p, s);
                System.out.println(p + " = " + s);
            }
            else if (p > position){
                bufferUndoMap.put(p-1, s);
                System.out.println(p + " = " + s);
            }
        }
        System.out.println(bufferUndoMap.size() + ": undo");
        for (Map.Entry me : r.entrySet()) {
            int p = (Integer)me.getKey();
            Stack<String> s = (Stack<String>)me.getValue();
            if (p < position){
                bufferRedoMap.put(p, s);
            }
            else if (p > position){
                bufferRedoMap.put(p-1, s);
            }
        }
        adapter.removeFrag(position);
        leafMatEgoFragment.setTextWatcher();
    }

    public MainPagerAdapter getAdapter() {
        return adapter;
    }

    public int getCurrentTab() {
        return currentTab;
    }

    public Map<Integer, Stack<String>> getBufferUndoMap(){
        Log.e("getBufferUndoMap", "*");
        return bufferUndoMap;
    }

    public Map<Integer, Stack<String>> getBufferRedoMap(){
        Log.e("getBufferRedoMap", "*");
        return bufferRedoMap;
    }
    public void setBufferUndoMap(Map<Integer, Stack<String>> u){
        bufferUndoMap = u;
    }

    public void setBufferRedoMap(Map<Integer, Stack<String>> r){
        bufferRedoMap = r;
    }
    public void setLimitToPager(){
        pager.setOffscreenPageLimit(200);
    }
}
