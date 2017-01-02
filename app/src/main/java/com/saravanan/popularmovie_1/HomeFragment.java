package com.saravanan.popularmovie_1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
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
import javax.net.ssl.HttpsURLConnection;

public class HomeFragment extends Fragment {

    GridView grid;

    Uri buildImageUri;

    ArrayList<Uri> imgList;

    ArrayList<HashMap<String,String>> movieList;

    String[] serviceURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

         grid = (GridView) view.findViewById(R.id.gridView1);

        return view;

    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //Json Parsing

    public ArrayList<HashMap<String,String>> getMovieList(String jsonResponse){

        JSONObject popularMovieJsonObj;

        movieList =new ArrayList<HashMap<String, String>>();

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

               buildImageUri = Uri.parse(BASE_IMAGE_URL)
                        .buildUpon()
                        .appendPath(IMAGE_SIZE).appendEncodedPath(posterPath).build();




                movie.put("poster",buildImageUri.toString());
                movie.put("title",title);
                movie.put("overview",overview);
                movie.put("voteAverage", voteAverage);
                movie.put("releaseDate", releaseDate);

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

        new MovieTask().execute( popularMovieUrl[0]);
    }

    public class MovieTask extends AsyncTask<String, Void, ArrayList<HashMap<String,String>>> {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;



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

                        Intent movieDetails=new Intent(getActivity(), MovieDetailsActivity.class);

                        movieDetails.putExtra("poster", poster);
                        movieDetails.putExtra("title", title);
                        movieDetails.putExtra("overview", overview);
                        movieDetails.putExtra("voteAverage", voteAverage);
                        movieDetails.putExtra("releaseDate", releaseDate);

                        startActivity(movieDetails);

                    }
                });

            }else {

                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();

            }
        }
    }
}
