package com.example.chan1cyrus2.popularmovies.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;


@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {
    public MovieDatabase(){}
    public static final int VERSION = 3;

    @Table(MovieColumns.class) public static final String MOVIES = "movies";
}
