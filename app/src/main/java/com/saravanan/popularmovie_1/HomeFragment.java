package com.saravanan.popularmovie_1;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import com.saravanan.popularmovie_1.data.MovieContract;
import com.saravanan.popularmovie_1.data.MovieCursorAdapter;
import com.saravanan.popularmovie_1.data.MovieDbHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MOVIE_LOADER=0;

    GridView grid;

    Uri buildImageUri = null, buildVideoUri = null, buildReviewUri = null;

    MovieDbHelper movieDbHelper;

    ArrayList<Uri> imgList;

    ArrayList<HashMap<String,String>> movieList;

    LinkedHashSet<String> favMovieID;

     ArrayList<HashMap<String,String>> favoritesMovieInfo ;
     HashMap<String,String> favMovie ;

    String[] serviceURL;

    MovieCursorAdapter gridCursorAdapter;

    MovieDbHelper db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        favMovieID = new LinkedHashSet<>();

        favoritesMovieInfo = new ArrayList<>();

        favMovie = new HashMap<>();

        db = new MovieDbHelper(getActivity());

        View view = inflater.inflate(R.layout.fragment_home, container, false);

         grid = (GridView) view.findViewById(R.id.gridView1);

        return view;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    MoviesDetailsFragment frag  =new MoviesDetailsFragment();

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuPoplarMovie:

                new MovieTask().execute( serviceURL[0]);

                return true;
            case R.id.menuTopRated:

                new MovieTask().execute( serviceURL[1]);

                return true;

            case R.id.menuFavourites:



                getLoaderManager().initLoader(MOVIE_LOADER, null, this);

                Cursor c =  getContext().getContentResolver().query(MovieContract.MovieFavourites.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

                if (c.moveToFirst()){

                        do {
                                favMovieID.add(c.getString(c.getColumnIndex(MovieContract.MovieFavourites.MOVIE_ID_COLUMN)));
                        }

                        while (c.moveToNext());

                    }

                final List<String> asList = new ArrayList<>(favMovieID);


                gridCursorAdapter = new MovieCursorAdapter(getActivity(), c, 0);

                grid.setAdapter(gridCursorAdapter);

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String[] projections= {
                                MovieContract.MovieFavourites.MOVIE_TITLE_COLUMN,
                                MovieContract.MovieFavourites.MOVIE_POSTER_PATH,
                                MovieContract.MovieFavourites.MOVIE_RELEASE_DATE,
                                MovieContract.MovieFavourites.MOVIE_RATING,
                                MovieContract.MovieFavourites.MOVIE_OVERVIEW
                        };

                        Cursor cur =  getContext().getContentResolver()
                                .query(MovieContract.MovieFavourites.buildMovieWithID(asList.get(i)), projections,
                                       null,
                                null , null);

                        Bitmap selectedPicImg = null;

                        if (cur.moveToFirst()) {
                            do {
                                favMovie.put("moviesID", asList.get(i));
                                favMovie.put("title", cur.getString(cur.getColumnIndex(MovieContract.MovieFavourites.MOVIE_TITLE_COLUMN)));
                                favMovie.put("overview", cur.getString(cur.getColumnIndex(MovieContract.MovieFavourites.MOVIE_OVERVIEW)));
                                favMovie.put("voteAverage", cur.getString(cur.getColumnIndex(MovieContract.MovieFavourites.MOVIE_RATING)));
                                favMovie.put("releaseDate", cur.getString(cur.getColumnIndex(MovieContract.MovieFavourites.MOVIE_RELEASE_DATE)));


                                selectedPicImg = MovieCursorAdapter.convertCursorRowToGridView(cur);
                                favoritesMovieInfo.add(favMovie);

                            } while (cur.moveToNext());
                        }

                        String title="";
                        String overview="";
                        String voteAverage="";
                        String releaseDate="";
                        String movieID="";

                             title = favoritesMovieInfo.get(0).get("title");
                             overview = favoritesMovieInfo.get(0).get("overview");
                             voteAverage = favoritesMovieInfo.get(0).get("voteAverage");
                             releaseDate = favoritesMovieInfo.get(0).get("releaseDate");
                             movieID= favoritesMovieInfo.get(0).get("moviesID");


                        Intent favDetails=new Intent(getActivity(), MovieDetailsActivity.class);
                        favDetails.putExtra("title", title);
                        favDetails.putExtra("overview", overview);
                        favDetails.putExtra("voteAverage", voteAverage);
                        favDetails.putExtra("releaseDate", releaseDate);
                        favDetails.putExtra("Image", selectedPicImg);
                        favDetails.putExtra("isOffline", true);
                        favDetails.putExtra("movieIdKey",movieID);

                        favMovie.clear();
                        favoritesMovieInfo.clear();

                        startActivity(favDetails);

                    }
                });

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    //Json Parsing

    public ArrayList<HashMap<String,String>> getMovieList(String jsonResponse){

        JSONObject popularMovieJsonObj;

        movieList = new ArrayList<HashMap<String, String>>();

        final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w185";

        try {
            popularMovieJsonObj = new JSONObject(jsonResponse);

            JSONArray resultsArr = popularMovieJsonObj.getJSONArray("results");

            for(int i=0; i< resultsArr.length(); i++){

                JSONObject resultsObj = resultsArr.getJSONObject(i);

                HashMap<String, String>  movie  = new HashMap<>();

                String posterPath = resultsObj.getString("poster_path");
                String title = resultsObj.getString("original_title");
                String overview = resultsObj.getString("overview");
                String voteAverage = resultsObj.getString("vote_average");
                String releaseDate = resultsObj.getString("release_date");
                String moviesID = resultsObj.getString("id");

               buildImageUri = Uri.parse(BASE_IMAGE_URL)
                        .buildUpon()
                        .appendPath(IMAGE_SIZE).appendEncodedPath(posterPath).build();

                movie.put("poster",buildImageUri.toString());
                movie.put("title",title);
                movie.put("overview",overview);
                movie.put("voteAverage", voteAverage);
                movie.put("releaseDate", releaseDate);
                movie.put("moviesID", moviesID);

                movieList.add(movie);

            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

        return movieList;

    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {


        super.onActivityCreated(savedInstanceState);

        serviceURL = getResources().getStringArray(R.array.service_url);

        String[] popularMovieUrl = getResources().getStringArray(R.array.service_url);

        new MovieTask().execute(popularMovieUrl[0]);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(),MovieContract.MovieFavourites.CONTENT_URI,
                null,null,null,null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

            gridCursorAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

           gridCursorAdapter.swapCursor(null);

    }


    public class MovieTask extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        final String BASE_VIDEO_URL = "https://api.themoviedb.org/3/movie/";
        final String API_KEY ="api_key";
        final String API_KEY_VALUE = "";

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(String... urls) {

            imgList =new ArrayList<Uri>();

             try{
                 URL url = new URL(urls[0]);

                 urlConnection = (HttpsURLConnection) url.openConnection();
                 urlConnection.setRequestMethod("GET");
                 urlConnection.connect();

                 InputStream stream = urlConnection.getInputStream();
                 StringBuffer buffer = new StringBuffer();

                 if(stream == null){

                     return null;
                 }

                 reader = new BufferedReader(new InputStreamReader(stream));

                 String line;

                 while((line = reader.readLine()) != null){

                     buffer.append(line + '\n');
                 }

                 if(buffer.length() == 0) {

                     return null;
                 }

                 movieJsonStr = buffer.toString();

                 return getMovieList(movieJsonStr);

             }catch (Exception e) {

                 System.out.println("Parsing Exception  " + e);

                 return null;

             }finally {

                 if(urlConnection != null){

                     urlConnection.disconnect();
                 }

                 if(reader != null){

                     try{

                         reader.close();

                     }catch (Exception e){

                     }
                 }

             }
        }

        @Override
        protected void onPostExecute(final ArrayList<HashMap<String,String>> result) {
            super.onPostExecute(result);

            if(result != null){

                grid.invalidate();


                grid.setAdapter(new GridAdapter(getActivity(), result));

                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        String poster  =  result.get(i).get("poster");
                        String title = result.get(i).get("title");
                        String overview = result.get(i).get("overview");
                        String voteAverage = result.get(i).get("voteAverage");
                        String releaseDate = result.get(i).get("releaseDate");
                        String movieID= result.get(i).get("moviesID");

                        buildVideoUri = Uri.parse(BASE_VIDEO_URL)
                                .buildUpon()
                                .appendPath(movieID)
                                .appendPath("videos")
                                .appendQueryParameter(API_KEY,API_KEY_VALUE)
                                .build();


                        buildReviewUri = Uri.parse(BASE_VIDEO_URL)
                                .buildUpon()
                                .appendPath(movieID)
                                .appendPath("reviews")
                                .appendQueryParameter(API_KEY,API_KEY_VALUE)
                                .build();

                        Log.d("video url", ""+buildVideoUri);

                        Intent movieDetails=new Intent(getActivity(), MovieDetailsActivity.class);

                        movieDetails.putExtra("poster", poster);
                        movieDetails.putExtra("title", title);
                        movieDetails.putExtra("overview", overview);
                        movieDetails.putExtra("voteAverage", voteAverage);
                        movieDetails.putExtra("releaseDate", releaseDate);
                        movieDetails.putExtra("movieID", buildVideoUri.toString());
                        movieDetails.putExtra("review",buildReviewUri.toString());
                        movieDetails.putExtra("video","video");
                        movieDetails.putExtra("movieIdKey",movieID);

                        startActivity(movieDetails);

                    }
                });

            }else {

                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();

            }
        }
    }
}
