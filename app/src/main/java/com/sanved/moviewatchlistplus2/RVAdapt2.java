package com.sanved.moviewatchlistplus2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sanved on 15-08-2017.
 */

public class RVAdapt2 extends RecyclerView.Adapter<RVAdapt2.DataHolder> {

    ArrayList<SearchData> list;
    static ArrayList<SearchData> list2;
    private static Tracker mTracker;
    ImageLoader imageLoader;
    static SQLiteHelper db;
    static int player;
    Typeface font;
    static MovieData md;
    static String imdbglobalid;
    static View v2;

    static Context context;

    RVAdapt2(ArrayList<SearchData> list, Context context, int player) {

        this.list = list;
        list2 = list;
        this.context = context;
        this.player = player;
        AnalyticsApplication application = (AnalyticsApplication) context;
        mTracker = application.getDefaultTracker();

        font = Typeface.createFromAsset(context.getAssets(),"gravity.otf");

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
            v2 = v;
            imageView = (ImageView) v.findViewById(R.id.ivPoster);
            cp = (CircularProgressBar) v.findViewById(R.id.cpGrid);
            percentage = (TextView) v.findViewById(R.id.percentage);
            name = (TextView) v.findViewById(R.id.tvName);
            year = (TextView) v.findViewById(R.id.tvYear);
            cv = (CardView) v.findViewById(R.id.cvList2);
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

                                        imdbglobalid = list2.get(getAdapterPosition()).getImdb();
                                        new MovieInfoTask().execute();
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
    public RVAdapt2.DataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
        RVAdapt2.DataHolder dh = new RVAdapt2.DataHolder(v);
        return dh;
    }

    @Override
    public void onBindViewHolder(RVAdapt2.DataHolder holder1, int position) {
        final RVAdapt2.DataHolder holder = holder1;

        holder.name.setTypeface(font);
        holder.name.setText(list.get(position).getName());
        holder.year.setText("("+list.get(position).getYear()+")");
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error_black_48dp) // resource or drawable
                .showImageOnFail(R.drawable.ic_error_black_48dp)
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

    static class MovieInfoTask extends AsyncTask<String, View, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(v2.getContext());
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            progressDialog.setTitle("Downloading Movie Info");
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                }
            });
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<String> param = new ArrayList<String>();
            URL url;

            try {
                url = new URL("http://www.omdbapi.com/?i=" + imdbglobalid + "&apikey=" + context.getString(R.string.apikey));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(4000);
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (SocketTimeoutException te) {
                Log.e("Nagdi", "Bai");
                progressDialog.setCancelable(true);
            } catch (MalformedURLException malle) {
                Log.e("Mal", "" + malle);
                malle.printStackTrace();
            } catch (IOException ioe) {
                Log.e("IO", "" + ioe);
                ioe.printStackTrace();
                progressDialog.setCancelable(true);
            }

            // Convert response to string using String Builder
            try {
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sBuilder = new StringBuilder();

                String line = null;
                while ((line = bReader.readLine()) != null) {
                    sBuilder.append(line + "\n");
                }

                inputStream.close();
                result = sBuilder.toString();

            } catch (Exception e) {
                Log.e("StringBuilding", "Error converting result " + e.toString());
            }
            return null;
        }

        protected void onPostExecute(Void v) {

            //parse JSON data
            try {
                //Taking JSON from Assets
                JSONObject jo_inside = new JSONObject(result);


                Log.d("Name--", jo_inside.getString("Title"));
                Log.d("Link--", "" + jo_inside.getString("Poster"));
                Log.d("Year--", jo_inside.getString("Year"));
                Log.d("IMDb--", jo_inside.getString("imdbID"));

                //Taking the values and filling them in the HashMap
                String strname = jo_inside.getString("Title");
                String stryear = jo_inside.getString("Year");
                String strimdbid = jo_inside.getString("imdbID");
                String strduration = jo_inside.getString("Runtime");
                String strseason;
                if(jo_inside.getString("Type").contains("series")){
                    strseason = "Seasons - " + jo_inside.getString("totalSeasons");
                }else{
                    strseason = "999";
                }
                String strgenre = jo_inside.getString("Genre");
                String strplot = "Plot - " + jo_inside.getString("Plot");
                String strdirector = "Director - " + jo_inside.getString("Director");
                String strstars = "Starring - " + jo_inside.getString("Actors");
                String link = jo_inside.getString("Poster");

                String strimdb = "999", strtomato = "999";

                JSONArray jarr = jo_inside.getJSONArray("Ratings");
                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject jo2 = jarr.getJSONObject(i);

                    if(jo2.getString("Source").contains("Internet Movie Database")){
                        strimdb = "IMDb Rating - " + jo2.getString("Value");
                    }else if(jo2.getString("Source").contains("Rotten Tomatoes")){
                        strtomato = "Rotten Tomatoes - " + jo2.getString("Value");
                    }

                }

                md = new MovieData(link,strname,stryear,strimdbid,strduration,strimdb,strtomato,strgenre, strplot,strdirector,strstars,strseason);
                db.addMovie(md);

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Usage")
                        .setAction("Added - " + strname)
                        .build());

                this.progressDialog.dismiss();


            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());

                this.progressDialog.dismiss();
            } // catch (JSONException e)
        }

    }
}

