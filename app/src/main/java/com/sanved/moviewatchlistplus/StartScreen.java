package com.sanved.moviewatchlistplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class StartScreen extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton fabAdd;
    TextView placeholder;
    Toolbar toolbar;
    RecyclerView rv;
    RVAdapt adapt;
    LinearLayoutManager llm;
    ArrayList<MovieData> list;
    SQLiteHelper db;

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
}
