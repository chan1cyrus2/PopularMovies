package com.example.chan1cyrus2.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.chan1cyrus2.popularmovies.data.MovieColumns;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chan1cyrus2 on 11/2/2015.
 */
public class MovieCursorAdapter extends CursorAdapter{

    public MovieCursorAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movies, parent, false);

        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(MovieColumns.IMGURL)))
                .into(viewHolder.imageView);
    }

    public static class ViewHolder{
        @Bind(R.id.list_item_movies_image_view) ImageView imageView;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
