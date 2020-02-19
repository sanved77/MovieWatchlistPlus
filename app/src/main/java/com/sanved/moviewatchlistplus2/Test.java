package com.sanved.moviewatchlistplus2;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Sanved on 06-08-2017.
 */

public class Test extends AppCompatActivity {

    ImageView imageView;
    CircularProgressBar cp;
    TextView percentage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list_item);

        imageView = (ImageView) findViewById(R.id.ivPoster);
        cp = (CircularProgressBar) findViewById(R.id.cpGrid);
        percentage = (TextView) findViewById(R.id.percentage);

        ImageLoader imageLoader;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .imageDownloader(new BaseImageDownloader(this, 10 * 1000, 20 * 1000))
                .build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_error_black_48dp) // resource or drawable
                .showImageOnFail(R.drawable.ic_error_black_48dp)
                .showImageOnLoading(R.drawable.white)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true)
                .build();

        imageLoader.displayImage("https://images-na.ssl-images-amazon.com/images/M/MV5BMTcxODgwMDkxNV5BMl5BanBnXkFtZTYwMDk2MDg3._V1_SX300.jpg", imageView, options, new SimpleImageLoadingListener() {

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
}
