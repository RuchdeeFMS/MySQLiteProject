package com.example.teach.mysqliteproject;

/**
 * Created by teach on 7/11/2560.
 */

public class Movie {
    private int _movie_id;
    private String _movie_name;
    private String _release_year;

    public Movie(int _movie_id, String _movie_name, String _release_year) {
        this._movie_id = _movie_id;
        this._movie_name = _movie_name;
        this._release_year = _release_year;
    }

    public Movie(String _movie_name, String _release_year) {
        this._movie_name = _movie_name;
        this._release_year = _release_year;
    }

    public int get_movie_id() {
        return _movie_id;
    }

    public void set_movie_id(int _movie_id) {
        this._movie_id = _movie_id;
    }

    public String get_movie_name() {
        return _movie_name;
    }

    public void set_movie_name(String _movie_name) {
        this._movie_name = _movie_name;
    }

    public String get_release_year() {
        return _release_year;
    }

    public void set_release_year(String _release_year) {
        this._release_year = _release_year;
    }
}
