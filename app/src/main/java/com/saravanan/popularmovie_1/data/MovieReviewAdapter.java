package com.saravanan.popularmovie_1.data;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.saravanan.popularmovie_1.R;

import java.util.ArrayList;

/**
 * Created by DELL on 2/25/2017.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    LayoutInflater inflater;
    Context context;
    ArrayList<String> data = new ArrayList<>();
    static final int TYPE_HEADER =0;
    static final int TYPE_ITEM =1;

    public MovieReviewAdapter(Context context,ArrayList<String> data){

        inflater  = LayoutInflater.from(context);
        this.context = context;
        this.data = data;

    }

    class ReviewHeader extends RecyclerView.ViewHolder {

        public ReviewHeader(View itemView) {
            super(itemView);

        }
    }

    class ReviewItems extends RecyclerView.ViewHolder {

        TextView content;

        public ReviewItems(View itemView) {
            super(itemView);

            content = (TextView) itemView.findViewById(R.id.tvReview);
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == TYPE_HEADER){
            View view = inflater.inflate(R.layout.custom_row_review_header, parent, false);

            ReviewHeader mvHolder = new ReviewHeader(view);

            return mvHolder;

        }else {

            View view = inflater.inflate(R.layout.custom_row_review, parent, false);

            ReviewItems mvHolder = new ReviewItems(view);

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
        if (holder instanceof ReviewHeader){


        }else {
            ReviewItems itemHolder= (ReviewItems) holder;
            //data.get(position);
            itemHolder.content.setText(data.get(position-1));
        }

    }

    @Override
    public int getItemCount() {
        return data.size() +1;
    }
}
