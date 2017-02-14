package com.glimpse.lecretsi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    public static User loggedInUser;
    public static Boolean userActive = false;
    public static FirebaseUser mFirebaseUser;
    boolean doubleBackToExitPressedOnce = false;
    private GoogleApiClient mGoogleApiClient;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {

            loggedInUser = new User(mFirebaseUser);

            final DatabaseReference newUserListener = FirebaseDatabase.getInstance().getReference().child("users");
            newUserListener.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean userFound = false;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        if (postSnapshot.getKey().equals(loggedInUser.getId())) {
                            userFound = true;
                        }
                    }
                    if (!userFound) {
                        newUserListener.child(loggedInUser.getId()).setValue(loggedInUser);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            startService(new Intent(this, NewMessageService.class));
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API).build();

        // TODO: Set version and make the user update

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        TextView usernameText = (TextView) header.findViewById(R.id.usernameText);
        usernameText.setText(mFirebaseUser.getDisplayName());

        TextView emailText = (TextView) header.findViewById(R.id.emailText);
        emailText.setText(mFirebaseUser.getEmail());

        CircleImageView userImage = (CircleImageView) header.findViewById(R.id.userImage);
        Glide.with(MainActivity.this)
                .load(mFirebaseUser.getPhotoUrl())
                .into(userImage);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.hide();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fab.isShown()) {
                    Animation fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
                    fab.startAnimation(fab_close);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, FriendsActivity.class);
                            startActivity(intent);
                        }
                    }, 300);
                }
            }
        });

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.fragmentContent, new ConversationsActivity());
        tx.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab.show();
                Animation fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
                fab.startAnimation(fab_open);
            }
        }, 500);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_conversations:
                tx.replace(R.id.fragmentContent, new ConversationsActivity());
                tx.commit();
                setTitle(item.getTitle());
                item.setChecked(true);
                fab.show();
                break;
            case R.id.nav_flashcards:

                item.setChecked(true);
                break;
            case R.id.nav_about:
                tx.replace(R.id.fragmentContent, new AboutActivity());
                tx.commit();
                setTitle(item.getTitle());
                item.setChecked(true);
                fab.hide();
                break;
            case R.id.nav_logout:
                stopService(new Intent(this, NewMessageService.class));
                mFirebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            case R.id.nav_settings:

                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.double_press_close, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        userActive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        userActive = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("MainActivity", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
