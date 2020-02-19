package com.sanved.moviewatchlistplus2;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
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
 * Created by Sanved on 07-08-2017.
 */

public class MovieScreen extends AppCompatActivity implements View.OnClickListener {

    TextView placeholder, name, year, duration, seasons, genre, plot, director, stars, imdb, tomato;
    ImageButton back;
    Button done, delete;
    private ImageView poster;
    CircularProgressBar cp;
    TextView percentage;
    ImageLoader imageLoader;
    SQLiteHelper db;
    private Tracker mTracker;

    private String imdbDB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.moviescreen);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if (extras != null) {
                imdbDB = extras.getString("imdb");
            }

        } else {
            imdbDB = (String) savedInstanceState.getSerializable("imdb");
        }

        initVals();


        if (imdbDB == null) {
            placeholder.setVisibility(View.VISIBLE);
            done.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            poster.setVisibility(View.GONE);
        }

        MovieData md = db.getMovieById(imdbDB);

        name.setText(md.getName());
        year.setText(md.getYear());
        duration.setText(md.getDuration());

        if(!(md.getSeasons().equals("999")))
            seasons.setText(md.getSeasons());

        genre.setText(md.getGenre());
        plot.setText(md.getPlot());
        director.setText(md.getDirector());
        stars.setText(md.getStars());

        if(!(md.getRateimdb().equals("999")))
            imdb.setText(md.getRateimdb());

        if(!(md.getRatetoma().equals("999")))
            tomato.setText(md.getRatetoma());


        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error_black_48dp) // resource or drawable
                .showImageOnFail(R.drawable.ic_error_black_48dp)
                .showImageOnLoading(R.drawable.white)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true)
                .build();

        imageLoader.displayImage(md.getLink() , poster, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                cp.setProgress(0);
                percentage.setText("0%");
                percentage.setVisibility(View.VISIBLE);
                cp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                cp.setVisibility(View.GONE);
                percentage.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                cp.setVisibility(View.GONE);
                percentage.setVisibility(View.GONE);
                poster.setVisibility(View.VISIBLE);
                //notifyDataSetChanged();
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                cp.setProgress(Math.round(100.0f * current / total));
                percentage.setText(Math.round(100.0f * current / total) + "%");
            }
        });

    }

    public void initVals() {

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        placeholder = (TextView) findViewById(R.id.tvPlaceHolder);
        name = (TextView) findViewById(R.id.tvName);
        year = (TextView) findViewById(R.id.tvYear);
        duration = (TextView) findViewById(R.id.tvDuration);
        seasons = (TextView) findViewById(R.id.tvSeasons);
        genre = (TextView) findViewById(R.id.tvGenre);
        plot = (TextView) findViewById(R.id.tvPlot);
        director = (TextView) findViewById(R.id.tvDirector);
        stars = (TextView) findViewById(R.id.tvStars);
        imdb = (TextView) findViewById(R.id.tvIMDB);
        tomato = (TextView) findViewById(R.id.tvTomato);

        name.setText("");
        year.setText("");
        duration.setText("");
        seasons.setText("");
        genre.setText("");
        plot.setText("");
        director.setText("");
        stars.setText("");
        imdb.setText("");
        tomato.setText("");

        done = (Button) findViewById(R.id.ibDone);
        back = (ImageButton) findViewById(R.id.ibBack);
        delete = (Button) findViewById(R.id.ibDelete);

        done.setOnClickListener(this);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);

        poster = (ImageView) findViewById(R.id.ivPoster);

        cp = (CircularProgressBar) findViewById(R.id.cpGrid);
        percentage = (TextView) findViewById(R.id.percentage);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .imageDownloader(new BaseImageDownloader(this, 10 * 1000, 20 * 1000))
                .build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        db = new SQLiteHelper(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ibDone:
                removeFromList(0);
                break;
            case R.id.ibBack:
                finish();
                break;
            case R.id.ibDelete:
                removeFromList(1);
                break;
        }

    }

    public void removeFromList(final int option){
        final String[] titles = {"Done","Delete"};
        String[] messages = {"Have you seen this movie/tvshow and want to remove it from the list?","Do you want to remove this movie/show from list ?"};
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build
                .setTitle(titles[option])
                .setMessage(messages[option])
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Usage")
                                .setAction("Removed - " + titles[option] + " " + name.getText().toString())
                                .build());
                        db.deleteMovie(imdbDB);
                        finish();
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

    class MovieInfoTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MovieScreen.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            placeholder.setVisibility(View.GONE);
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
                url = new URL("http://www.omdbapi.com/?i=" + imdbDB + "&apikey=" + getString(R.string.apikey));
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
            MovieData sd;
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
                String strduration = jo_inside.getString("Runtime");
                String strseason;
                if(jo_inside.getString("Type").contains("series")){
                    strseason = jo_inside.getString("totalSeasons");
                }else{
                    strseason = "";
                }
                String strgenre = jo_inside.getString("Genre");
                String strplot = "Plot - " + jo_inside.getString("Plot");
                String strdirector = "Director - " + jo_inside.getString("Director");
                String strstars = "Starring - " + jo_inside.getString("Actors");
                String link = jo_inside.getString("Poster");

                String strimdb = "", strtomato = "";

                JSONArray jarr = jo_inside.getJSONArray("Ratings");
                for (int i = 0; i < jarr.length(); i++) {

                    JSONObject jo2 = jarr.getJSONObject(i);

                    if(jo2.getString("Source").contains("Internet Movie Database")){
                        strimdb = "IMDb Rating - " + jo2.getString("Value");
                    }else if(jo2.getString("Source").contains("Rotten Tomatoes")){
                        strtomato = "Rotten Tomatoes - " + jo2.getString("Value");
                    }

                }

                name.setText(strname);
                year.setText(stryear);
                duration.setText(strduration);
                seasons.setText(strseason);
                genre.setText(strgenre);
                plot.setText(strplot);
                director.setText(strdirector);
                stars.setText(strstars);
                imdb.setText(strimdb);
                tomato.setText(strtomato);

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.ic_error_black_48dp) // resource or drawable
                        .showImageOnFail(R.drawable.ic_error_black_48dp)
                        .showImageOnLoading(R.drawable.white)
                        .cacheInMemory(true)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .cacheOnDisk(true)
                        .build();

                imageLoader.displayImage(link , poster, options, new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        cp.setProgress(0);
                        percentage.setText("0%");
                        percentage.setVisibility(View.VISIBLE);
                        cp.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        cp.setVisibility(View.GONE);
                        percentage.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        cp.setVisibility(View.GONE);
                        percentage.setVisibility(View.GONE);
                        poster.setVisibility(View.VISIBLE);
                        //notifyDataSetChanged();
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        cp.setProgress(Math.round(100.0f * current / total));
                        percentage.setText(Math.round(100.0f * current / total) + "%");
                    }
                });

                this.progressDialog.dismiss();


            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
                this.progressDialog.dismiss();
                placeholder.setText("No Movies / TV Shows Found");
                placeholder.setVisibility(View.VISIBLE);
            } // catch (JSONException e)
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("MovieScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}

