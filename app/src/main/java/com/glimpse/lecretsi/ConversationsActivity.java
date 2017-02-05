package com.glimpse.lecretsi;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ConversationsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // This is the activity that's gonna contain all the conversations

    ListView mainList;
    private static final String TAG_NICKNAME = "nickname";
    private static final String TAG_LAST_MESSAGE = "lastMessage";
    private static final String TAG_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNewMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mainList = (ListView)findViewById(R.id.conversationsList);

        mainList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(ConversationsActivity.this, ChatActivity.class);
                        /* String conversationID = conversationIDText.getText().toString();
                        intent.putExtra("conversationID", conversationID); */
                        startActivity(intent);
                    }
                }
        );

        ArrayList<HashMap<String, String>> conversationsList = new ArrayList<>();

        String nickname = "Largonji Assistant";
        String last_message = "Hi User! I'm your Largonji Assistant";
        String date = "Feb 3";

        HashMap<String, String> conversation = new HashMap<>();

        conversation.put(TAG_NICKNAME, nickname);
        conversation.put(TAG_LAST_MESSAGE, last_message);
        conversation.put(TAG_DATE, date);

        conversationsList.add(conversation);

        ListAdapter adapter = new SimpleAdapter(
                ConversationsActivity.this, conversationsList,
                R.layout.conversations_list, new String[]{TAG_NICKNAME, TAG_LAST_MESSAGE, TAG_DATE},
                new int[]{R.id.nicknameText, R.id.lastMessage, R.id.dateText}
        );
        mainList.setAdapter(adapter);

    }

    public void onCreateNewMessage(){

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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
