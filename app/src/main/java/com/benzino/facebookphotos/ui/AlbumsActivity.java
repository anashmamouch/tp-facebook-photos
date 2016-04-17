package com.benzino.facebookphotos.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.adapters.AlbumGridViewAdapter;
import com.benzino.facebookphotos.model.Album;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15/4/16.
 *
 * @author Anas
 */
public class AlbumsActivity extends AppCompatActivity {
    public static final String TAG = "ANAS == "+AlbumsActivity.class.getSimpleName();

    private List<Album> albums;

    private GridView gridView;
    private AlbumGridViewAdapter gridViewAdapter;

    private TextView details;

    private Toolbar toolbar;

    private String albumId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        initializeScreen();

        getAlbumsFB();
    }

    private void initializeScreen(){
        albums = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar_albums);
        details = (TextView) findViewById(R.id.details);
        gridView = (GridView) findViewById(R.id.gridView);

        String name = getIntent().getStringExtra("NAME");
        String firstName = getIntent().getStringExtra("FIRST NAME");

        setSupportActionBar(toolbar);
        setTitle(firstName + "'s Albums");

        details.setText("Welcome " + name);
    }

    private void getAlbumsFB(){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(album)");

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            JSONArray data =  response.getJSONObject().getJSONArray("data");
                            /*sort data here by name*/

                            for (int i = 0; i < data.length(); i++){

                                String id = data.getJSONObject(i).getString("id");
                                String title = data.getJSONObject(i).getString("name");
                                String url = data.getJSONObject(i).getJSONObject("picture").getJSONObject("data").getString("url");

                                Log.d("ANAS", "url: "+ url);

                                albums.add(new Album(id, title, url));

                                Log.d("ANAS", albums.get(i).getTitle());
                            }

                            gridViewAdapter = new AlbumGridViewAdapter(AlbumsActivity.this, R.layout.album_grid_item, albums);
                            gridView.setAdapter(gridViewAdapter);

                            Log.d("ANAS", data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

}
