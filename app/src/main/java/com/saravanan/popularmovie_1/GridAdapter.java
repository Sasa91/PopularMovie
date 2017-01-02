package com.saravanan.popularmovie_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;


public class GridAdapter extends BaseAdapter {

    Context context;

    ArrayList<HashMap<String, String>> moviesData;

    GridAdapter(Context context, ArrayList<HashMap<String, String>> moviesData){
        this.context = context;
        this.moviesData = moviesData;
    }


    @Override
    public int getCount() {
        return moviesData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row= view;

        Holder holder;


        if(view == null){

            row = inflater.inflate(R.layout.grid_exam, null);
            holder= new Holder();
           holder.imgView= (ImageView) row.findViewById(R.id.imgGrid);
            row.setTag(holder);


        }else{

            holder= (Holder)row.getTag();

        }

        Picasso.with(this.context)
                .load(moviesData.get(i).get("poster"))
                .into(holder.imgView);

        return row;
    }

    class Holder{

        ImageView imgView;

    }
}
