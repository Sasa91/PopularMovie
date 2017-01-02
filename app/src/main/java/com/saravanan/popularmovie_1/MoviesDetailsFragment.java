package com.saravanan.popularmovie_1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MoviesDetailsFragment extends Fragment{


    private String original_title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;

    ImageView moviePic;
    TextView movieTitle, releaseDate, votingAvg, plot;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details, container, false);


         moviePic = (ImageView) view.findViewById(R.id.movieImg);
        movieTitle = (TextView) view.findViewById(R.id.movie_title);
        releaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
        votingAvg = (TextView) view.findViewById(R.id.vote);
        plot = (TextView) view.findViewById(R.id.tvSynopsis);

        Picasso.with(getContext())
                .load(getActivity().getIntent().getStringExtra("poster"))
                .into(moviePic);
        movieTitle.setText(getActivity().getIntent().getStringExtra("title"));
        releaseDate.setText(getActivity().getIntent().getStringExtra("releaseDate"));
        votingAvg.setText(getActivity().getIntent().getStringExtra("voteAverage"));
        plot.setText(getActivity().getIntent().getStringExtra("overview"));

        return view;

    }


}
