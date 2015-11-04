package com.example.chan1cyrus2.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chan1cyrus2.popularmovies.detailobjects.DetailItem;
import com.example.chan1cyrus2.popularmovies.detailobjects.Header;
import com.example.chan1cyrus2.popularmovies.detailobjects.Review;
import com.example.chan1cyrus2.popularmovies.detailobjects.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by chan1cyrus2 on 11/3/2015.
 */
public class DetailAdapter extends ArrayAdapter<DetailItem>{
    public DetailAdapter(Context context, List<DetailItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DetailItem item = getItem(position);
        ViewHolder holder;

        if(convertView == null){
            switch(item.getViewType()){
                case Movie.VIEWTYPE:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail_movie, parent, false);
                    break;
                case Trailer.VIEWTYPE:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail_trailer, parent, false);
                    break;
                case Review.VIEWTYPE:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail_review, parent, false);
                    break;
                case Header.VIEWTYPE:
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_detail_header, parent, false);
                    break;
            }

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        switch(item.getViewType()){
            case Movie.VIEWTYPE:
                final Movie movie = (Movie)item;
                holder.movie_title.setText(movie.title);
                Picasso.with(getContext())
                        .load(movie.imgURL)
                        .error(R.drawable.error)
                        .into(holder.movie_img);
                holder.movie_date.setText(movie.release_date);
                holder.movie_rating.setText(Double.toString(movie.rating) + "/10");
                holder.movie_plot.setText(movie.plot);
                holder.favorite_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View V) {
                        SaveFavoriteMovieTask task = new SaveFavoriteMovieTask(getContext());
                        task.execute(movie);
                    }
                });
                break;
            case Trailer.VIEWTYPE:
                final Trailer trailer = (Trailer)item;
                holder.trailer_title.setText(trailer.name);
                Picasso.with(getContext())
                        .load(Uri.parse("http://img.youtube.com/vi/").buildUpon()
                                .appendPath(trailer.url)
                                .appendPath("0.jpg").build())
                        .error(R.drawable.error)
                        .into(holder.trailer_img);
                convertView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View V) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://" + trailer.url));
                        getContext().startActivity(intent);
                        }
                });
                break;
            case Review.VIEWTYPE:
                Review review = (Review)item;
                holder.review_author.setText(review.author);
                holder.review_content.setText(review.review);
                break;
            case Header.VIEWTYPE:
                Header header = (Header)item;
                holder.detail_header.setText(header.heading);
                break;
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    public static class ViewHolder{
        //Movie
        @Nullable @Bind (R.id.movie_title) TextView movie_title;
        @Nullable @Bind(R.id.movie_img) ImageView movie_img;
        @Nullable @Bind(R.id.movie_date) TextView movie_date;
        @Nullable @Bind(R.id.movie_rating) TextView movie_rating;
        @Nullable @Bind(R.id.movie_plot) TextView movie_plot;
        @Nullable @Bind(R.id.favorite_button) Button favorite_button;
        //Header
        @Nullable @Bind(R.id.detail_header) TextView detail_header;
        //Trailer
        @Nullable @Bind(R.id.trailer_img) ImageView trailer_img;
        @Nullable @Bind(R.id.trailer_title) TextView trailer_title;
        //Review
        @Nullable @Bind(R.id.review_author) TextView review_author;
        @Nullable @Bind(R.id.review_content) TextView review_content;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
