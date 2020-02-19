package com.sanved.moviewatchlistplus2;

/**
 * Created by Sanved on 06-08-2017.
 */

public class MovieData {

    String link, name, year, imdb, duration, rateimdb, ratetoma, genre, plot, director, stars, seasons;

    public MovieData(String link, String name, String year, String imdb, String duration, String rateimdb, String ratetoma, String genre, String plot, String director, String stars, String seasons){
        this.link = link;
        this.name = name;
        this.year = year;
        this.imdb = imdb;
        this.duration = duration;
        this.rateimdb = rateimdb;
        this.ratetoma = ratetoma;
        this.genre = genre;
        this.plot = plot;
        this.director = director;
        this.stars = stars;
        this.seasons = seasons;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getImdb() {
        return imdb;
    }

    public String getDuration() {
        return duration;
    }

    public String getRateimdb() {
        return rateimdb;
    }

    public String getRatetoma() {
        return ratetoma;
    }

    public String getDirector() {
        return director;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlot() {
        return plot;
    }

    public String getStars() {
        return stars;
    }

    public String getSeasons() {
        return seasons;
    }
}
