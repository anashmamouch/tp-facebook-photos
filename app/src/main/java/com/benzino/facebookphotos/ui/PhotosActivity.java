package com.benzino.facebookphotos.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.benzino.facebookphotos.R;

/**
 * Created on 16/4/16.
 *
 * @author Anas
 */
public class PhotosActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
    }
}
