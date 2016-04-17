package com.benzino.facebookphotos.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.benzino.facebookphotos.R;
import com.benzino.facebookphotos.model.Album;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        //updateWithToken(AccessToken.getCurrentAccessToken());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.app_name);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("user_friends", "user_photos"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker profileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile;

                if(Profile.getCurrentProfile() == null) {
                    profileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("ANAS", profile2.getFirstName());
                            Intent intent = new Intent(MainActivity.this, AlbumsActivity.class);
                            intent.putExtra("NAME", profile2.getName());
                            intent.putExtra("FIRST NAME", profile2.getFirstName());
                            startActivity(intent);
                            profileTracker.stopTracking();
                        }
                    };
                    profileTracker.startTracking();
                }
                else {
                    profile = Profile.getCurrentProfile();
                    Log.v("ANAS", profile.getFirstName());
                }

                Log.d("ANAS", "Login Succeded");
            }

            @Override
            public void onCancel() {
                Log.d("ANAS", "Login Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ANAS", "Login Error" + error.getMessage());
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void updateWithToken(AccessToken currentAccessToken){
        if (currentAccessToken != null){
            startActivity(new Intent(MainActivity.this, AlbumsActivity.class));
            finish();
        }else {

        }
    }
}
