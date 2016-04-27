package com.benzino.facebookphotos.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.adapters.AlbumGridViewAdapter;
import com.benzino.facebookphotos.model.Album;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    final String[] afterString = {""};  // will contain the next page cursor

    private String albumId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        initializeScreen();

        loadAlbums(false);
    }

    private void initializeScreen(){
        albums = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar_albums);
        gridView = (GridView) findViewById(R.id.gridView);

        setSupportActionBar(toolbar);
        setTitle("Albums");

    }

    public void loadAlbums(final boolean loadMore){
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture.type(album)");
        parameters.putString("limit", "14");
        parameters.putString("after", afterString[0]);

        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/albums",
                parameters,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {

                        JSONObject jsonObject = response.getJSONObject();
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

                            if(loadMore){
                                gridViewAdapter.notifyDataSetChanged();
                            }else{
                                gridViewAdapter = new AlbumGridViewAdapter(AlbumsActivity.this, R.layout.album_grid_item, albums);
                                gridView.setAdapter(gridViewAdapter);
                            }

                            if(!jsonObject.isNull("paging")){

                                JSONObject cursors = jsonObject.getJSONObject("paging").getJSONObject("cursors");

                                if(!cursors.isNull("after")){
                                    afterString[0] = cursors.getString("after");
                                }
                            }

                            Log.d("ANAS", data.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

    }

    public void onLoadMore(View view){
        loadAlbums(true);
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
