package com.saravanan.popularmovie_1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.saravanan.popularmovie_1.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by DELL on 2/19/2017.
 */

public class MovieTrailerReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LayoutInflater inflater;
    Context context;
    ArrayList<String> data = new ArrayList<>();
    static final int TYPE_HEADER =0;
    static final int TYPE_ITEM =1;

    public MovieTrailerReviewAdapter(Context context,ArrayList<String> data){

        inflater  = LayoutInflater.from(context);
        this.context = context;
        this.data = data;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_HEADER){
            View view = inflater.inflate(R.layout.custom_row_header, parent, false);

            MoviesViewHeaderHolder mvHolder = new MoviesViewHeaderHolder(view);

            return mvHolder;

        }else {

            View view = inflater.inflate(R.layout.custom_row, parent, false);

            MoviesViewItemHolder mvHolder = new MoviesViewItemHolder(view);

            return mvHolder;
        }

    }

    @Override
    public int getItemViewType(int position) {

        if(position  == 0){

            return TYPE_HEADER;
        }else{

            return TYPE_ITEM;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MoviesViewHeaderHolder){


        }else {
            MoviesViewItemHolder itemHolder= (MoviesViewItemHolder) holder;
            itemHolder.content.setText(data.get(position-1));


        }



    }

    @Override
    public int getItemCount() {
        return data.size() +1;
    }



    class MoviesViewItemHolder extends RecyclerView.ViewHolder {

        TextView content;

        public MoviesViewItemHolder(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.tvInfo);
        }
    }

    class MoviesViewHeaderHolder extends RecyclerView.ViewHolder {

        public MoviesViewHeaderHolder(View itemView) {
            super(itemView);
        }
    }



}
