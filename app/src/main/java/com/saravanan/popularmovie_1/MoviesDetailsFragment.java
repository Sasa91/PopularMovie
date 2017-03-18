package com.saravanan.popularmovie_1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.saravanan.popularmovie_1.data.MovieContract;
import com.saravanan.popularmovie_1.data.MovieDbHelper;
import com.saravanan.popularmovie_1.data.MovieDetails;
import com.saravanan.popularmovie_1.data.MovieReviewAdapter;
import com.saravanan.popularmovie_1.data.MovieTrailerReviewAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MoviesDetailsFragment extends Fragment {


    private String original_title;
    private String poster_path;
    private String overview;
    private String vote_average;
    private String release_date;
    private String movieID;
    private boolean isOffline;
    Context ctx;
    MovieDbHelper dbHelper;
    SQLiteDatabase db;

    String isVideo;

    private String[] movieDtailsArr = new String[2];


    ImageView moviePic;
    TextView movieTitle, releaseDate, votingAvg, plot;
    VideoView movieTrailer;
    RelativeLayout trailerLayout;
    ListView movieTrailerList;
    ListView movieReviewList;
    Button favouritesBtn;

    boolean isFavourite;

    private RecyclerView mTrailerReviewRecyle, mReviewRecycle;
    private RecyclerView.Adapter mTrailerReviewAdapter, mReviewAdapter;

    public static final String FavouriteMovie = "favMovie";
    public static final boolean isFav = false;


    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<String> trailerTitleList;

    ArrayList<String> trailerTitle = new ArrayList<String>();
    ArrayList<String> reviewList = new ArrayList<String>();
    RecyclerView.Adapter adapter;

    String[] trailerUrI;
    String[] values = new String[]{"Android List View",
            "Adapter implementation",
            "Simple List View In Android",
            "Create List View Android",
            "Android Example",
            "List View Source Code",
            "List View Array Adapter",
            "Android Example List View"
    };
    ArrayAdapter<String> trailerAdapter, reviewAdapter;

    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_details, container, false);

        moviePic = (ImageView) view.findViewById(R.id.movieImg);
        movieTitle = (TextView) view.findViewById(R.id.movie_title);
        releaseDate = (TextView) view.findViewById(R.id.tvReleaseDate);
        votingAvg = (TextView) view.findViewById(R.id.vote);
        plot = (TextView) view.findViewById(R.id.tvSynopsis);
        favouritesBtn = (Button) view.findViewById(R.id.btnFavourite);

        dbHelper = new MovieDbHelper(getActivity());

        mTrailerReviewRecyle = (RecyclerView) view.findViewById(R.id.rvTrailer);
        mReviewRecycle = (RecyclerView) view.findViewById(R.id.rvReview);

        original_title = getActivity().getIntent().getStringExtra("title");
        overview = getActivity().getIntent().getStringExtra("overview");
        vote_average = getActivity().getIntent().getStringExtra("voteAverage");
        release_date = getActivity().getIntent().getStringExtra("releaseDate");
        poster_path = getActivity().getIntent().getStringExtra("poster");
        movieID = getActivity().getIntent().getStringExtra("movieIdKey");
        isOffline = getActivity().getIntent().getBooleanExtra("isOffline", false);
        Bitmap bitmap = getActivity().getIntent().getParcelableExtra("Image");

        if (isOffline) {


            moviePic.setImageBitmap(bitmap);
        } else {
            Picasso.with(getContext())
                    .load(poster_path)
                    .into(moviePic);
        }

        movieTitle.setText(original_title);
        releaseDate.setText(release_date);
        votingAvg.setText(vote_average);
        plot.setText(overview);

        //movieIdKey

        String movieTrailerURL = getActivity().getIntent().getStringExtra("movieID");
        String movieReviewURL = getActivity().getIntent().getStringExtra("review");

        isVideo = getActivity().getIntent().getStringExtra("video");

        movieDtailsArr[0] = movieTrailerURL;
        movieDtailsArr[1] = movieReviewURL;

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new MovieDetailsTask().execute(movieDtailsArr[0]);
        new MovieReviewTask().execute(movieDtailsArr[1]);

        favouritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkFavouriteMovie();
                if (isFavourite) {
                    isFavourite = false;

                    String mSelectionClause = "movie_id = ?";
                    String[] mSelectionArgs = new String[]{movieID};

                    long delCount = getContext().getContentResolver().delete(MovieContract.MovieFavourites.CONTENT_URI,
                            mSelectionClause,
                            mSelectionArgs);

                    Toast.makeText(getActivity(), getContext().getText(R.string.toast_unfavourite), Toast.LENGTH_SHORT).show();

                } else {

                    byte[] sample = new byte[0];
                    try {
                        sample = urlToImageBLOB(poster_path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    getContext().getContentResolver().insert(MovieContract.MovieFavourites.CONTENT_URI, dbHelper.addFavouriteMovieDetails(
                            new MovieDetails(Integer.parseInt(movieID)
                                    , original_title
                                    , sample
                                    , release_date
                                    , vote_average
                                    , overview
                            )));

                    Toast.makeText(getActivity(), getContext().getText(R.string.toast_favourite), Toast.LENGTH_LONG).show();

                    isFavourite = true;

                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("MovieFavourite", isFavourite);


    }


    public static byte[] urlToImageBLOB(String url) throws IOException {

        URL imgUrl = new URL(url);
        URLConnection ucon = imgUrl.openConnection();

        InputStream is = ucon.getInputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = is.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();

    }


    private byte[] getByteArrayFromImage(String filePath) throws FileNotFoundException, IOException {

        File file = new File(filePath);
        System.out.println(file.exists() + "!!");

        FileInputStream fis = new FileInputStream(file);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                bos.write(buf, 0, readNum);

                System.out.println("read " + readNum + " bytes,");
            }
        } catch (IOException ex) {
            Log.d("error", "error");
        }
        byte[] bytes = bos.toByteArray();
        return bytes;
    }


    public void checkFavouriteMovie() {
        ctx = getContext();

        List<MovieDetails> movieDetails = dbHelper.getAllFavouriteMovie(ctx);

        int moviesID = 0;

        for (MovieDetails details : movieDetails) {
            moviesID = details.getMovieID();
        }

        if (moviesID == Integer.parseInt(movieID)) {
            isFavourite = true;
        } else {
            isFavourite = false;
        }

    }


    public String getMovieDetails(String jsonStr) {

        return jsonStr;
    }

    public class MovieDetailsTask extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieDetailsStr = null;


        @Override
        protected String doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (stream == null) {

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(stream));

                String line;

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {

                    return null;
                }


                return movieDetailsStr = buffer.toString();


            } catch (Exception e) {

                System.out.println("Parsing Exception  " + e);

                return null;

            } finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();
                }

                if (reader != null) {

                    try {

                        reader.close();

                    } catch (Exception e) {

                    }
                }

            }

        }

        @Override
        protected void onPostExecute(String s) {

            JSONArray resultsArr = null;

            final ArrayList<String> trailerURLList = new ArrayList<String>();
            Uri buildYoutubeUri = null;
            LinearLayout.LayoutParams layoutParams;


            try {

                if (s != null) {
                    JSONObject movieTrailerObj = new JSONObject(s);


                    resultsArr = movieTrailerObj.getJSONArray("results");

                    for (int i = 0; i <= resultsArr.length(); i++) {

                        JSONObject object = resultsArr.getJSONObject(i);

                        String trailer_key = object.getString("key");

                        buildYoutubeUri = Uri.parse("https://www.youtube.com/watch")
                                .buildUpon()
                                .appendQueryParameter("v", trailer_key)
                                .build();

                        trailerURLList.add(buildYoutubeUri.toString());


                        trailerTitle.add("Trailer " + (i + 1));

                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            mTrailerReviewRecyle.setNestedScrollingEnabled(false);

            mTrailerReviewRecyle.setLayoutManager(new LinearLayoutManager(getActivity()));
            mTrailerReviewAdapter = new MovieTrailerReviewAdapter(getActivity(), trailerTitle);

            mTrailerReviewRecyle.setAdapter(mTrailerReviewAdapter);

            mTrailerReviewRecyle.addOnItemTouchListener(
                    new RecyclerItemClickListener(ctx, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            // TODO Handle item click
                            Intent startBrowser = new Intent(Intent.ACTION_VIEW);
                            startBrowser.setData(Uri.parse(trailerURLList.get(position -1).toString()));
                            startActivity(startBrowser);
                        }
                    })
            );

        }
    }

    public class MovieReviewTask extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieReviewStr = null;
        String content = null;
        ArrayList<String> contentsArr;

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (stream == null) {

                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(stream));

                String line;

                while ((line = reader.readLine()) != null) {

                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {

                    return null;
                }


                movieReviewStr = buffer.toString();

                return movieReviewStr;

            } catch (Exception e) {

                System.out.println("Parsing Exception  " + e);

                return null;

            } finally {

                if (urlConnection != null) {

                    urlConnection.disconnect();
                }

                if (reader != null) {

                    try {

                        reader.close();

                    } catch (Exception e) {

                    }
                }

            }
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONArray resultsArr = null;
            try {
                if (s != null) {

                    JSONObject reviewObj = new JSONObject(s);
                    resultsArr = reviewObj.getJSONArray("results");

                    for (int i = 0; i < resultsArr.length(); i++) {

                        JSONObject comment = resultsArr.getJSONObject(i);

                        content = comment.getString("content");

                        reviewList.add(content);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (reviewList.size() > 0) {

                mReviewRecycle.setNestedScrollingEnabled(false);

                mReviewRecycle.setLayoutManager(new LinearLayoutManager(getActivity()));
                mReviewAdapter = new MovieReviewAdapter(getActivity(), reviewList);

                mReviewRecycle.setAdapter(mReviewAdapter);

            }

        }
    }
}
