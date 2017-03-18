package com.saravanan.popularmovie_1.data;

public class MovieDetails {

    int movieID;
    private String  releaseDate, rating, overview, title;
    byte[]  imgBlob;
    boolean isFavourite = false;

    public MovieDetails(){}

    public MovieDetails(int movieID,String releaseDate, String rating, String overview, String title){


        this.movieID = movieID;
        this.releaseDate = releaseDate;
        this.rating= rating;
        this.overview = overview;
        this.title = title;

    }

    public MovieDetails(int movieID, String title,byte[] imgBlob, String releaseDate, String rating, String overview){

        this.movieID = movieID;
        this.imgBlob = imgBlob;
        this.releaseDate = releaseDate;
        this.rating= rating;
        this.overview = overview;
        this.title = title;

    }

    public void setMovieId(int movieID){

          this.movieID = movieID;
    }

    public int getMovieID(){

        return this.movieID;
    }

    public byte[] getImgBlob(){
        return this.imgBlob;
    }

    public void setImgBlob(byte[] bite){
        this.imgBlob = bite;
    }

    public String getReleaseDate(){
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate){
        this.releaseDate = releaseDate;
    }

    public String getRating(){
        return this.rating;
    }

    public void setRating(String rating){
         this.rating = rating;
    }

    public String getOverview(){
        return this.overview;
    }

    public void setOverview(String overview){
        this.overview = overview;
    }

    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title = title;
    }



}
