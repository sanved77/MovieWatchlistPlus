package com.sanved.moviewatchlistplus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Sanved on 06-08-2017.
 */

public class RVAdapt extends RecyclerView.Adapter<RVAdapt.DataHolder> {

    ArrayList<MovieData> list;
    static ArrayList<MovieData> list2;
    private static Tracker mTracker;
    ImageLoader imageLoader;
    static SQLiteHelper db;
    static int player;

    Context context;

    RVAdapt(ArrayList<MovieData> list, Context context, int player) {
        this.list = list;
        list2 = list;
        this.context = context;
        this.player = player;
        AnalyticsApplication application = (AnalyticsApplication) context;
        mTracker = application.getDefaultTracker();

        db = new SQLiteHelper(context);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .imageDownloader(new BaseImageDownloader(context, 10 * 1000, 20 * 1000))
                .build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
    }

    public static class DataHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        CircularProgressBar cp;
        TextView percentage, name, year;
        CardView cv;

        DataHolder(final View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.ivPoster);
            cp = (CircularProgressBar) v.findViewById(R.id.cpGrid);
            percentage = (TextView) v.findViewById(R.id.percentage);
            name = (TextView) v.findViewById(R.id.tvName);
            year = (TextView) v.findViewById(R.id.tvYear);
            cv = (CardView) v.findViewById(R.id.cvList);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(player == 1){

                        Intent i = new Intent(v.getContext(), MovieScreen.class);
                        i.putExtra("imdb", list2.get(getAdapterPosition()).getImdb());
                        v.getContext().startActivity(i);

                    }else if(player == 2) {
                        AlertDialog.Builder build = new AlertDialog.Builder(v.getContext());
                        build
                                .setTitle("Add Movie")
                                .setMessage("Do you want to add this movie to the watchlist ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        MovieData md = new MovieData(
                                                list2.get(getAdapterPosition()).getLink(),
                                                list2.get(getAdapterPosition()).getName(),
                                                list2.get(getAdapterPosition()).getYear(),
                                                list2.get(getAdapterPosition()).getImdb()
                                        );
                                        db.addMovie(md);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false);

                        build.create().show();
                    }
                }
            });
        }

    }

    @Override
    public DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        DataHolder dh = new DataHolder(v);
        return dh;
    }

    @Override
    public void onBindViewHolder(DataHolder holder1, int position) {
        final DataHolder holder = holder1;

        holder.name.setText(list.get(position).getName());
        holder.year.setText(list.get(position).getYear());
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error_black_48dp) // resource or drawable
                .showImageOnFail(R.drawable.ic_error_black_48dp)
                .showImageOnLoading(R.drawable.white)
                .cacheInMemory(false)
                .cacheOnDisk(true)
                .build();

        imageLoader.displayImage(list.get(position).getLink(), holder.imageView, options , new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.cp.setProgress(0);
                holder.percentage.setText("0%");
                holder.percentage.setVisibility(View.VISIBLE);
                holder.cp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                holder.cp.setVisibility(View.GONE);
                holder.percentage.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.cp.setVisibility(View.GONE);
                holder.percentage.setVisibility(View.GONE);
                //notifyDataSetChanged();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                holder.cp.setProgress(Math.round(100.0f * current / total));
                holder.percentage.setText( Math.round(100.0f * current / total) + "%");
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

