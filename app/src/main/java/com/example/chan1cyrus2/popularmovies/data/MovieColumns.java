package com.example.chan1cyrus2.popularmovies.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

//This Table only stores Movies that is marked as favorite by users
public interface MovieColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement public static final String _ID = "_id";
    @DataType(DataType.Type.TEXT) @NotNull @Unique String MOVIE_ID = "movie_ID";
    @DataType(DataType.Type.TEXT) @NotNull String TITLE = "title";
    @DataType(DataType.Type.TEXT) String IMGURL = "imgURL";
    @DataType(DataType.Type.TEXT) @NotNull String PLOT = "plot";
    @DataType(DataType.Type.REAL) @NotNull String RATING = "rating";
    @DataType(DataType.Type.TEXT) @NotNull String RELEASE_DATE = "release_date";
}
