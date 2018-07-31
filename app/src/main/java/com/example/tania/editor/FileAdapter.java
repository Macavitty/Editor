package com.example.tania.editor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
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


    public FileAdapter(ArrayList<File> list, Context context, int resources) {
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

    public void changeHighlighting(int position) {
        selected = new boolean[getCount()];
        selected[position] = !selected[position];
        notifyDataSetChanged(); // do not remove this stuff !!!
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        convertView = inflater.inflate(R.layout.row, parent, false);

        Pattern pattern = Pattern.compile(".+\\.(doc|txt|odt|pdf)");

        TextView textView = convertView.findViewById(R.id.item_text);
        View view = convertView.findViewById(R.id.list_background);
        ImageView imageView = convertView.findViewById(R.id.icon);
        File file = list.get(position);
        String path = file.getAbsolutePath();

        textView.setText(file.getName());


        // image for folder
        if (file.isDirectory()) {
            if (file.list().length == 0) imageView.setImageResource(R.drawable.empty_folder);
            else imageView.setImageResource(R.drawable.not_empty_folder);
        }
        // image for file
        //else if (!list.get(position).getAbsolutePath().contains(".") || pattern.matcher(list.get(position).getAbsolutePath()).matches())
        else {
            if (path.endsWith(".txt")) imageView.setImageResource(R.drawable.txt);
            else if (path.endsWith(".doc")) imageView.setImageResource(R.drawable.doc);
            else if (path.endsWith(".pdf")) imageView.setImageResource(R.drawable.pdf);
            else if (path.endsWith(".xls")) imageView.setImageResource(R.drawable.xls);

            else if (path.endsWith(".gif") || path.endsWith(".m4v") || path.endsWith(".mp4") || path.endsWith(".3gp"))
                imageView.setImageResource(R.drawable.video);

            else if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".img") || path.endsWith(".JPG"))
                imageView.setImageResource(R.drawable.image);

            else if (path.endsWith(".mp3") || path.endsWith(".mpc") || path.endsWith(".wav"))
                imageView.setImageResource(R.drawable.audio);

            else imageView.setImageResource(R.drawable.file);
        }

        //ListView listView = (ListView) view.;
        if (!selected[position])
            view.setBackgroundColor(Color.TRANSPARENT);
        else
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorSelectedBackground));
        return convertView;
    }


}
