package com.sanved.moviewatchlistplus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Sanved on 06-08-2017.
 */

public class AddMovie extends AppCompatActivity {

    TextView placeholder;
    Toolbar toolbar;
    RecyclerView rv;
    RVAdapt adapt;
    LinearLayoutManager llm;
    ArrayList<MovieData> list;
    EditText search;
    ImageButton searchB;

    String term = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_movie);

        initVals();


        Context con = getApplication();
        adapt = new RVAdapt(list,con,2);

        rv.setAdapter(adapt);
    }

    public void initVals(){

        list = new ArrayList<>();

        search = (EditText) findViewById(R.id.etSearchTermQues);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH) {
                    searchStuff();
                }
                return false;
            }
        });

        search.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (search.getRight() - search.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        search.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        searchB = (ImageButton) findViewById(R.id.bSearchQues);
        searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchStuff();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setTitle("Add Movie/TV show");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_36dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rv = (RecyclerView) findViewById(R.id.rv);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        placeholder = (TextView) findViewById(R.id.tvPlaceHolder);

    }

    /*
    public void fillData() {

        MovieData sd = new MovieData("https://images-na.ssl-images-amazon.com/images/M/MV5BOTQ5NDI3MTI4MF5BMl5BanBnXkFtZTgwNDQ4ODE5MDE@._V1_SX300.jpg", "Harry Potter", "2006");
        MovieData sd2 = new MovieData("https://images-na.ssl-images-amazon.com/images/M/MV5BOTQ5NDI3MTI4MF5BMl5BanBnXkFtZTgwNDQ4ODE5MDE@._V1_SX300.jpg", "Harry Potter", "2006");
        MovieData sd3 = new MovieData("https://images-na.ssl-images-amazon.com/images/M/MV5BOTQ5NDI3MTI4MF5BMl5BanBnXkFtZTgwNDQ4ODE5MDE@._V1_SX300.jpg", "Harry Potter", "2006");

        list.add(sd);
        list.add(sd2);
        list.add(sd3);

    }*/


    public void searchStuff(){

        term = search.getText().toString();

        if(term.isEmpty() || term == null){
            Toast.makeText(this, "Please enter something in the search box", Toast.LENGTH_SHORT).show();
        }else {
            list.clear();
            adapt.notifyDataSetChanged();
            new MovieSearchTask().execute();
        }
    }


    class MovieSearchTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(AddMovie.this);
        InputStream inputStream = null;
        String result = "";

        protected void onPreExecute() {
            placeholder.setVisibility(View.GONE);
            progressDialog.setTitle("Searching Movies");
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

            try{
                url = new URL("http://www.omdbapi.com/?s=" + term + "&apikey=" + getString(R.string.apikey));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setUseCaches(false);
                urlConnection.setConnectTimeout(4000);
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            }catch (SocketTimeoutException te){
                Log.e("Nagdi","Bai");
                progressDialog.setCancelable(true);
            }catch (MalformedURLException malle){
                Log.e("Mal", ""+malle);
                malle.printStackTrace();
            }catch (IOException ioe){
                Log.e("IO", ""+ioe);
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
                JSONObject jobj = new JSONObject(result);
                //Taking a JSON Array from the JSONObject created above
                JSONArray jarr = jobj.getJSONArray("Search");

                //Loop to iterate all the values off the Array
                for (int i = 0; i < jarr.length(); i++) {
                    //Put the pointer on the object of the array
                    JSONObject jo_inside = jarr.getJSONObject(i);

                    Log.d("Name--", jo_inside.getString("Title"));
                    Log.d("Link--", ""+jo_inside.getString("Poster"));
                    Log.d("Year--", jo_inside.getString("Year"));
                    Log.d("IMDb--", jo_inside.getString("imdbID"));

                    //Taking the values and filling them in the HashMap
                    String name = jo_inside.getString("Title");
                    String link = jo_inside.getString("Poster");
                    String year = jo_inside.getString("Year");
                    String imdb = jo_inside.getString("imdbID");
                    sd = new MovieData(link, name, year,imdb);

                    list.add(sd);
                    //Adding the HashMap to the ArrayList
                }
                this.progressDialog.dismiss();
                Context con = AddMovie.this.getApplication();
                adapt = new RVAdapt(list,con, 2);

                rv.setAdapter(adapt);

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
                this.progressDialog.dismiss();
                placeholder.setText("No Movies / TV Shows Found");
                placeholder.setVisibility(View.VISIBLE);
            } // catch (JSONException e)
        }

    }

}

