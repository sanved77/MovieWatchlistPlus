package com.sanved.moviewatchlistplus2;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Collections;

public class StartScreen extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton fabAdd;
    TextView placeholder;
    Toolbar toolbar;
    RecyclerView rv;
    RVAdapt adapt;
    LinearLayoutManager llm;
    ArrayList<MovieData> list;
    SQLiteHelper db;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        initVals();

        list = db.allMovies();
        Context con = this.getApplication();
        adapt = new RVAdapt(list,con, 1);

        rv.setAdapter(adapt);

    }

    public void initVals(){

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(this);

        list = new ArrayList<>();

        db = new SQLiteHelper(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white));
        getSupportActionBar().setTitle("Watchlist");

        rv = (RecyclerView) findViewById(R.id.rv);
        llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(StartScreen.this, MovieScreen.class);
                i.putExtra("imdb", list.get(position).getImdb());
                startActivityForResult(i, 69);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        placeholder = (TextView) findViewById(R.id.tvPlaceHolder);

    }



    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.fabAdd:
                Intent i = new Intent(StartScreen.this, AddMovie.class);
                startActivityForResult(i, 69);
                break;
        }

    }

    public void refreshList(){
        list.clear();
        list = db.allMovies();
        adapt.notifyDataSetChanged();
        adapt = new RVAdapt(list, getApplication(), 1);
        rv.setAdapter(adapt);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 69){
            refreshList();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("App")
                        .setAction("About")
                        .build());
                Intent i = new Intent(StartScreen.this, AboutCopyright.class);
                startActivity(i);
                break;

            case R.id.madd:
                Intent i2 = new Intent(StartScreen.this, AddMovie.class);
                startActivityForResult(i2, 69);
                break;

            case R.id.mshuffle:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Usage")
                        .setAction("Shuffled")
                        .build());
                Collections.shuffle(list);
                adapt.notifyDataSetChanged();
                break;

            case R.id.bug:
                AlertDialog.Builder build2 = new AlertDialog.Builder(StartScreen.this);
                LayoutInflater inflater2 = this.getLayoutInflater();
                final View dialogView2 = inflater2.inflate(R.layout.dialog, null);

                final EditText bug2 = (EditText) dialogView2.findViewById(R.id.etBug);

                build2
                        .setTitle("Report Bugs")
                        .setView(dialogView2)
                        .setMessage("What was the bug/error ?")
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"sanved77@gmail.com"});
                                i.putExtra(Intent.EXTRA_SUBJECT, "GRE Word List: Error Bugs");
                                i.putExtra(Intent.EXTRA_TEXT, "I found a bug in the app. The bug is - " + bug2.getText().toString());
                                try {
                                    startActivity(Intent.createChooser(i, "Send email...."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(StartScreen.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false);

                build2.create().show();
                break;
            case R.id.rate:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Rate")
                        .setAction("App Rated")
                        .build());
                String url = "https://play.google.com/store/apps/details?id=" + getPackageName();
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse(url));
                startActivity(i3);
                break;

            case R.id.mshare:
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share")
                        .setAction("App Shared")
                        .build());
                String shareBody = "Hey, check out this app - GRE Word List Maker. It's helps in creating a word list.\nhttps://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("StartScreen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
