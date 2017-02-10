package com.glimpse.lecretsi;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendsActivity extends AppCompatActivity {

    final String LOGGED_USER_ID = ConversationsActivity.loggedInUser.getId();

    private RecyclerView friendsView;
    private LinearLayoutManager mFriendsManager;

    AlertDialog alertDialog;
    private DatabaseReference newFriendListener, newFriendRequestListener;

    public static class FriendsViewHolder extends RecyclerView.ViewHolder{
        RecyclerView friendRequestsView, friendsView;
        TextView friendRequestsText, friendsText;

        public FriendsViewHolder (View v) {
            super(v);
            friendRequestsView = (RecyclerView) itemView.findViewById(R.id.friendRequestsView);
            friendsView = (RecyclerView) itemView.findViewById(R.id.friendsView);
            friendRequestsText = (TextView) itemView.findViewById(R.id.friendRequestsText);
            friendsText = (TextView) itemView.findViewById(R.id.friendsText);
        }
    }

    RecyclerView.Adapter<FriendsViewHolder> friendsViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // This is the adapter for listing user's friends

        friendsView = (RecyclerView) findViewById(R.id.friendsView);
        mFriendsManager = new LinearLayoutManager(this);
        mFriendsManager.setStackFromEnd(true);

        friendsViewAdapter = new RecyclerView.Adapter<FriendsViewHolder>() {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                FriendsViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View view = inflater.inflate(R.layout.friends_view, parent, false);
                viewHolder = new FriendsViewHolder(view);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(FriendsViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 1;
            }
        };

        friendsView.setLayoutManager(mFriendsManager);
        friendsView.setAdapter(friendsViewAdapter);

        FloatingActionButton addFriends = (FloatingActionButton) findViewById(R.id.addFriends);
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ViewGroup nullParent = null;
                LayoutInflater li = LayoutInflater.from(FriendsActivity.this);
                View dialogView = li.inflate(R.layout.friend_request_dialog, nullParent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FriendsActivity.this, R.style.alertDialog);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(dialogView);

                final EditText friendEmail = (EditText) dialogView.findViewById(R.id.friendRequestEmail);

                // set dialog message
                alertDialogBuilder.setPositiveButton("Add friend", null);

                // create alert dialog
                alertDialog = alertDialogBuilder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String friendEmailText = friendEmail.getText().toString();
                                if(!friendEmailText.isEmpty()) {
                                    // Send a friend request to the specified user
                                    final User user = new User(ConversationsActivity.mFirebaseUser);
                                    newFriendRequestListener = FirebaseDatabase.getInstance().getReference();
                                    newFriendRequestListener.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                User details = postSnapshot.getValue(User.class);
                                                if(details.getEmail().equals(friendEmailText)) {
                                                    FirebaseDatabase.getInstance().getReference().child("users")
                                                            .child(details.getId()).child("friend_requests").push().setValue(user);
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(FriendsActivity.this, "Please insert an email address", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                // show it
                alertDialog.show();
            }
        });

    }

    void acceptFriendRequest(User whoseFriendRequest, User receiverFriendRequest) {

    }
}
