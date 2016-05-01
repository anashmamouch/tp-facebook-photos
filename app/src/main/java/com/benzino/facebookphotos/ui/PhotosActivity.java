package com.benzino.facebookphotos.ui;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.adapters.AlbumGridViewAdapter;
import com.benzino.facebookphotos.adapters.PhotoGridViewAdapter;
import com.benzino.facebookphotos.model.Photo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.backendless.Backendless;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created on 16/4/16.
 *
 * @author Anas
 */
public class PhotosActivity extends AppCompatActivity {

    final static private String APP_KEY = "9ur9oezu0zpjd19";
    final static private String APP_SECRET = "01knf4h7uk5iu3n";

    final static private String APP_ID = "1A759847-411E-E60A-FF08-B375408FC000";
    final static private String APP_ANDROID_KEY = "99F52FD1-E028-A455-FF32-5829C9447900";



    private DropboxAPI<AndroidAuthSession> mDBApi;

    String albumId;

    GridView gridView;
    PhotoGridViewAdapter gridViewAdapter;

    final String[] afterString = {""};  // will contain the next page cursor

    List<Photo> photos;
    private ProgressDialog progressDialog;

    Cloudinary cloudinary;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        initializeData();

        loadData(false);

        Log.e("ANAS", "PHOTO ACTIVITY");
    }



    public void initializeData(){

        /*Initialize Dropbox API*/
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        String appVersion = "v1";

        /*Initialize Backendless*/
        Backendless.initApp(this, APP_ID, APP_ANDROID_KEY, appVersion );

        /*test Backendless*/
//        BackendlessUser user = new BackendlessUser();
//        user.setEmail( "benzinoanas@gmail.com" );
//        user.setPassword( "my_super_password" );
//
//        Backendless.UserService.register( user, new BackendlessCallback<BackendlessUser>()
//        {
//            @Override
//            public void handleResponse( BackendlessUser backendlessUser )
//            {
//                Log.i( "Registration", backendlessUser.getEmail() + " successfully registered" );
//            }
//        } );

        albumId = getIntent().getStringExtra("ALBUM ID");

        gridView = (GridView) findViewById(R.id.gridView_photos);

        photos = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_photos);
        setSupportActionBar(toolbar);

        setTitle(getIntent().getStringExtra("ALBUM NAME"));

        mDBApi.getSession().startOAuth2Authentication(PhotosActivity.this);

    }

    public void onLoadMore(View view){
       loadData(true);

    }

    public void onBackup(View view){

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait...");

        Photo photo;

        final List<String> selectedPhotos = new ArrayList<>();
        boolean onStart = true;

        for (int position = 0; position < photos.size(); position++){
            photo = photos.get(position);

            if (photo.isChecked()){
                Log.e("ANAS", position + ". Selected Photo State: " + photo.isChecked() + "  " + photo.getUrl());
                if (onStart){
                    progressDialog.show();
                    onStart = false;
                }
                selectedPhotos.add(photo.getUrl());
            }
        }

        for(int i = 0; i < selectedPhotos.size(); i++){

            final int finalI = i;
            Picasso.with(getApplicationContext()).load(selectedPhotos.get(i)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    try {
                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(root + "/myFBPhotos");
                        if (!myDir.exists()) {
                            myDir.mkdirs();
                        }
                        final String name = System.currentTimeMillis() + ".jpg";
                        final File image = new File(myDir, name);
                        FileOutputStream out = new FileOutputStream(image);
                        final FileInputStream inputStream = new FileInputStream(image);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    DropboxAPI.Entry response = mDBApi.putFile("/"+name, inputStream,
                                            image.length(), null, null);
                                    Log.e("ANAS UPLOAD", finalI +  " . The uploaded file's rev is: " + response.rev);
                                } catch (DropboxException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();
                    } catch(Exception e){
                        e.printStackTrace();
                        //Log.e("ANASERROR", e.getMessage());
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        progressDialog.dismiss();
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

    public void logout(){
        LoginManager.getInstance().logOut();
        finish();
    }

    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
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

    public class UploadTask extends AsyncTask<Void, Void, Void>{

        public UploadTask(){
//            progressDialog = new ProgressDialog(PhotosActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.setCancelable(false);
//            progressDialog.setMessage("Please wait...");
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Photo photo;

            final List<String> selectedPhotos = new ArrayList<>();
            boolean onStart = true;

            for (int position = 0; position < photos.size(); position++){
                photo = photos.get(position);

                if (photo.isChecked()){
                    Log.e("ANAS", position + ". Selected Photo State: " + photo.isChecked() + "  " + photo.getUrl());
                    if (onStart){
                        //progressDialog.show();
                        onStart = false;
                    }
                    selectedPhotos.add(photo.getUrl());
                }
            }

            for(int i = 0; i < selectedPhotos.size(); i++){

                final int finalI = i;
                Picasso.with(getApplicationContext()).load(selectedPhotos.get(i)).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            String root = Environment.getExternalStorageDirectory().toString();
                            File myDir = new File(root + "/yourDirectory");
                            if (!myDir.exists()) {
                                myDir.mkdirs();
                            }
                            final String name = System.currentTimeMillis() + ".jpg";
                            final File image = new File(myDir, name);
                            FileOutputStream out = new FileOutputStream(image);
                            final FileInputStream inputStream = new FileInputStream(image);

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
                                    try {
                                        DropboxAPI.Entry response = mDBApi.putFile("/"+name, inputStream,
                                                image.length(), null, null);
                                        Log.e("ANAS UPLOAD", finalI +  " . The uploaded file's rev is: " + response.rev);
                                    } catch (DropboxException e) {
                                        e.printStackTrace();
                                    }
//                                }
//                            });

//                        new Thread(new Runnable() {
//                            public void run() {
//                                try {
//                                    DropboxAPI.Entry response = mDBApi.putFile("/"+name, inputStream,
//                                            image.length(), null, null);
//                                    Log.e("ANAS UPLOAD", finalI +  " . The uploaded file's rev is: " + response.rev);
//                                } catch (DropboxException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }).start();


                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                        } catch(Exception e){
                            e.printStackTrace();
                            //Log.e("ANASERROR", e.getMessage());
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            progressDialog.dismiss();
        }
    }
}
