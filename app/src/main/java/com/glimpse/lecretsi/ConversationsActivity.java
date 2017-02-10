package com.glimpse.lecretsi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class ConversationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    // This is the activity that's gonna contain all the conversations

    public static User loggedInUser;

    private GoogleApiClient mGoogleApiClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    public static FirebaseUser mFirebaseUser;

    public TextView usernameText, emailText;
    public CircleImageView userImage;

    public static class ConversationsHolder extends RecyclerView.ViewHolder {

        TextView conversationUsername;
        TextView lastMessage;
        TextView lastMessageDate;
        CircleImageView conversationPicture;

        public ConversationsHolder(View itemView) {
            super(itemView);
            conversationUsername = (TextView) itemView.findViewById(R.id.conversationUsername);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessage);
            lastMessageDate = (TextView) itemView.findViewById(R.id.lastMessageDate);
            conversationPicture = (CircleImageView) itemView.findViewById(R.id.conversationPicture);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        } else {

            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

            loggedInUser = new User(mFirebaseUser);

            DatabaseReference newUserListener = FirebaseDatabase.getInstance().getReference();
            newUserListener.child("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean userFound = false;
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                if(postSnapshot.getKey().equals(loggedInUser.getId())){
                                    userFound = true;
                                }
                            }
                            if (!userFound) {
                                mDatabase.child("users").child(loggedInUser.getId()).setValue(loggedInUser);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API).build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConversationsActivity.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        usernameText = (TextView) header.findViewById(R.id.usernameText);
        usernameText.setText(mFirebaseUser.getDisplayName());

        emailText = (TextView) header.findViewById(R.id.emailText);
        emailText.setText(mFirebaseUser.getEmail());

        userImage = (CircleImageView) header.findViewById(R.id.userImage);
        Glide.with(ConversationsActivity.this)
                .load(mFirebaseUser.getPhotoUrl())
                .into(userImage);

        final RecyclerView conversationsView = (RecyclerView) findViewById(R.id.conversationsView);
        LinearLayoutManager mFConversationsManager = new LinearLayoutManager(this);
        mFConversationsManager.setStackFromEnd(true);

        DatabaseReference mConversations = FirebaseDatabase.getInstance().getReference()
                .child("users").child(loggedInUser.getId()).child("conversations");

        // This is the adapter for displaying user's friend requests

        final LinearLayoutManager mConversationsManager = new LinearLayoutManager(getApplicationContext());
        mConversationsManager.setStackFromEnd(false);

        final FirebaseRecyclerAdapter<Conversation, ConversationsHolder> mConversationsAdapter;

        mConversationsAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationsHolder>(
                Conversation.class, R.layout.conversations_item, ConversationsHolder.class, mConversations) {

            @Override
            protected void populateViewHolder(ConversationsHolder viewHolder, Conversation conversation, int position) {
                viewHolder.conversationUsername.setText(conversation.getUser().getName());
                if(conversation.getLastMessage() == null) {
                    viewHolder.lastMessage.setVisibility(View.GONE);
                } else {
                    viewHolder.lastMessage.setVisibility(View.VISIBLE);
                    viewHolder.lastMessage.setText(conversation.getLastMessage());
                }
                viewHolder.lastMessageDate.setText(conversation.getLastMessageDate());
                Glide.with(ConversationsActivity.this)
                        .load(conversation.getUser().getPhotoURL())
                        .into(viewHolder.conversationPicture);
            }
        };

        mConversationsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mConversationsAdapter.getItemCount();
                int lastVisiblePosition =
                        mConversationsManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    conversationsView.scrollToPosition(positionStart);
                }
            }
        });

        conversationsView.setLayoutManager(mConversationsManager);
        conversationsView.setAdapter(mConversationsAdapter);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_conversations) {

        } else if (id == R.id.nav_flashcards) {

        } else if (id == R.id.nav_archives) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_logout) {
            mFirebaseAuth.signOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("ConversationsActivity", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
