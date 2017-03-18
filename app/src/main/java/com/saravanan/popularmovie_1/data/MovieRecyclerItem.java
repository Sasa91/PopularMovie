package com.saravanan.popularmovie_1.data;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.saravanan.popularmovie_1.R;

/**
 * Created by DELL on 2/25/2017.
 */

public class MovieRecyclerItem extends RecyclerView.ViewHolder {

    TextView itemContent;
    ImageView imgPlay;

    public MovieRecyclerItem(View itemView) {
        super(itemView);

        itemContent = (TextView) itemView.findViewById(R.id.tvInfo);
     //   imgPlay  = (ImageView) itemView.findViewById(R.id.imgPlayTrailer);


    }
}
