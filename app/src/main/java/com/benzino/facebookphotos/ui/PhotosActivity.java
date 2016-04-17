package com.benzino.facebookphotos.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.adapters.AlbumGridViewAdapter;
import com.benzino.facebookphotos.adapters.PhotoGridViewAdapter;
import com.benzino.facebookphotos.model.Photo;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16/4/16.
 *
 * @author Anas
 */
public class PhotosActivity extends AppCompatActivity {

    String albumId;

    GridView gridView;
    PhotoGridViewAdapter gridViewAdapter;

    final String[] afterString = {""};  // will contain the next page cursor

    List<Photo> photos;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        initializeData();

        loadData(false);

        Log.e("ANAS", "PHOTO ACTIVITY");
    }

    public void initializeData(){
        albumId = getIntent().getStringExtra("ALBUM ID");

        gridView = (GridView) findViewById(R.id.gridView_photos);

        photos = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_photos);
        setSupportActionBar(toolbar);

        setTitle(getIntent().getStringExtra("ALBUM NAME"));
    }

    public void onLoadMore(View view){
       loadData(true);
    }

    public void onBackup(View view){

    }

    public void loadData(final boolean loadMore){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "images");
        parameters.putString("limit", "21");
        parameters.putString("after", afterString[0]);

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/" + albumId + "/photos",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        JSONObject jsonObject = response.getJSONObject();

                        /*handle result*/
                        try {
                            JSONArray data =  response.getJSONObject().getJSONArray("data");
                            List<JSONArray> images = new ArrayList<>();

                            for (int i = 0; i < data.length(); i++){
                                images.add(data.getJSONObject(i).getJSONArray("images"));
                                Log.e("ANAS MORE", "IMAGES: "  + images.toString());

                                String source = data.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("source");

                                photos.add(new Photo("0", source));

                                Log.e("ANAS MORE", "url: "  + source);
                            }

                            if(loadMore){
                                gridViewAdapter.notifyDataSetChanged();
                            }else{
                                gridViewAdapter = new PhotoGridViewAdapter(PhotosActivity.this, R.layout.photo_grid_item, photos);
                                gridView.setAdapter(gridViewAdapter);
                            }

                            Log.e("ANAS MORE", data.toString());

                            if(!jsonObject.isNull("paging")){

                                JSONObject cursors = jsonObject.getJSONObject("paging").getJSONObject("cursors");

                                if(!cursors.isNull("after")){
                                    afterString[0] = cursors.getString("after");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }
}
