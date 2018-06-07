package com.example.tania.editor;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import java.util.regex.Pattern;

public class FileAdapter extends ArrayAdapter {
    private ArrayList<File> list;
    private boolean[] selected;
    private LayoutInflater inflater;


    public FileAdapter(ArrayList<File> list, Context context, int resources){
        super(context, resources);
        this.selected = new boolean[list.size()];
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    public void changeHighlighting(int position){
        selected=new boolean[getCount()];
        selected[position] = !selected[position];
        notifyDataSetChanged(); // do not remove this stuff !!!
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.row, parent, false);

        Pattern pattern = Pattern.compile(".+\\.(doc|txt|odt|pdf)");

        TextView textView = convertView.findViewById(R.id.item_text);
        View view = convertView.findViewById(R.id.list_background);
        ImageView imageView = convertView.findViewById(R.id.icon);

        textView.setText(list.get(position).getName());

        if (list.get(position).isDirectory()) imageView.setImageResource(R.drawable.folder);
        else if (!list.get(position).getAbsolutePath().contains(".") || pattern.matcher(list.get(position).getAbsolutePath()).matches())
            imageView.setImageResource(R.drawable.book);


        //ListView listView = (ListView) view.;
        if (!selected[position])
            view.setBackgroundColor(Color.TRANSPARENT);
        else
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSelectedBackground));
        return convertView;
    }

}
