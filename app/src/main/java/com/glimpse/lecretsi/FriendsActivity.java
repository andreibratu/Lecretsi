package com.glimpse.lecretsi;

import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    static final String LOGGED_USER_ID = ConversationsActivity.loggedInUser.getId();

    private RecyclerView friendsViewList;
    private LinearLayoutManager mFriendsListManager, mFriendsManager, mFriendRequestsManager;

    AlertDialog alertDialog;
    private DatabaseReference newFriendRequestListener;

    private RecyclerView.Adapter<FriendsViewHolder> friendsViewAdapter;

    public static class FriendsListHolder extends RecyclerView.ViewHolder {

        TextView friendUsername;
        TextView friendEmail;
        CircleImageView friendPicture;

        // like the one above
        public FriendsListHolder(View itemView) {
            super(itemView);
            friendUsername = (TextView) itemView.findViewById(R.id.friendUsername);
            friendEmail = (TextView) itemView.findViewById(R.id.friendEmail);
            friendPicture = (CircleImageView) itemView.findViewById(R.id.friendPicture);
        }
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // This is the adapter for inflating friend requests and friends in a single RecyclerView

        friendsViewList = (RecyclerView) findViewById(R.id.friendsViewList);
        mFriendsListManager = new LinearLayoutManager(this);
        mFriendsListManager.setStackFromEnd(true);

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
            public void onBindViewHolder(final FriendsViewHolder holder, int position) {
                // Database queries for retrieving items

                DatabaseReference mUserFriendRequests = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(LOGGED_USER_ID).child("friend_requests");
                DatabaseReference mUserFriends = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(LOGGED_USER_ID).child("friends");

                // This is the adapter for displaying user's friend requests

                final LinearLayoutManager mFriendRequestsManager = new LinearLayoutManager(getApplicationContext());
                mFriendRequestsManager.setStackFromEnd(true);

                final FirebaseRecyclerAdapter<User, FriendsListHolder> mFriendRequestsAdapter, mFriendsAdapter;

                mFriendRequestsAdapter = new FirebaseRecyclerAdapter<User, FriendsListHolder>(
                        User.class,
                        R.layout.friends_item,
                        FriendsListHolder.class,
                        mUserFriendRequests){

                    @Override
                    protected void populateViewHolder(FriendsListHolder viewHolder, User user, int position) {
                        viewHolder.friendUsername.setText(user.getName());
                        viewHolder.friendEmail.setText(user.getEmail());
                        Glide.with(getApplicationContext())
                                .load(user.getPhotoURL())
                                .into(viewHolder.friendPicture);
                    }

                };

                mFriendRequestsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mFriendRequestsAdapter.getItemCount();
                        int lastVisiblePosition =
                                mFriendRequestsManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            holder.friendRequestsView.scrollToPosition(positionStart);
                        }
                    }
                });

                holder.friendRequestsView.setLayoutManager(mFriendRequestsManager);
                holder.friendRequestsView.setAdapter(mFriendRequestsAdapter);

                // This is the adapter for displaying user's friends

                final LinearLayoutManager mFriendsManager = new LinearLayoutManager(getApplicationContext());
                mFriendsManager.setStackFromEnd(true);

                mFriendsAdapter = new FirebaseRecyclerAdapter<User, FriendsListHolder>(
                        User.class,
                        R.layout.friends_item,
                        FriendsListHolder.class,
                        mUserFriends){

                    @Override
                    protected void populateViewHolder(FriendsListHolder viewHolder, User user, int position) {
                        viewHolder.friendUsername.setText(user.getName());
                        viewHolder.friendEmail.setText(user.getEmail());
                        Glide.with(getApplicationContext())
                                .load(user.getPhotoURL())
                                .into(viewHolder.friendPicture);
                    }

                };

                mFriendsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mFriendsAdapter.getItemCount();
                        int lastVisiblePosition =
                                mFriendsManager.findLastCompletelyVisibleItemPosition();
                        // If the recycler view is initially being loaded or the
                        // user is at the bottom of the list, scroll to the bottom
                        // of the list to show the newly added message.
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            holder.friendsView.scrollToPosition(positionStart);
                        }
                    }
                });

                holder.friendsView.setLayoutManager(mFriendsManager);
                holder.friendsView.setAdapter(mFriendsAdapter);
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        };

        friendsViewList.setLayoutManager(mFriendsListManager);
        friendsViewList.setAdapter(friendsViewAdapter);



        FloatingActionButton addFriends = (FloatingActionButton) findViewById(R.id.addFriends);
        addFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ViewGroup nullParent = null;
                LayoutInflater li = LayoutInflater.from(FriendsActivity.this);
                View dialogView = li.inflate(R.layout.friend_request_dialog, nullParent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                        (FriendsActivity.this, R.style.alertDialog);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(dialogView);

                final EditText friendEmail = (EditText)
                        dialogView.findViewById(R.id.friendRequestEmail);

                // set dialog message
                alertDialogBuilder.setPositiveButton(R.string.add_friend, null);

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
                                    newFriendRequestListener =
                                            FirebaseDatabase.getInstance().getReference();


                                    newFriendRequestListener.child("users").
                                            addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    boolean userFound = false;

                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                        User details = postSnapshot.getValue(User.class);
                                                        if(details.getEmail().equals(friendEmailText)) {
                                                            userFound = true;
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("users")
                                                                    .child(details.getId())
                                                                    .child("friend_requests")
                                                                    .push().setValue(user);
                                                        }
                                                    }

                                                    if(!userFound) {
                                                        Toast.makeText(getApplicationContext(),
                                                                R.string.user_not_found,
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    alertDialog.dismiss();
                                } else {
                                    Toast.makeText(FriendsActivity.this, R.string.no_email_inserted
                                            , Toast.LENGTH_SHORT).show();
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

    //TODO acceptFriendRequest
    void acceptFriendRequest(User whoseFriendRequest, User receiverFriendRequest) {

    }
}
