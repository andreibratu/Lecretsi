package com.glimpse.lecretsi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    final String LOGGED_USER_ID = ConversationsActivity.loggedInUser.getId();

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView friendUsername;
        TextView friendEmail;
        CircleImageView friendPicture;

        public MessageViewHolder(View v) {
            super(v);
            friendUsername = (TextView) itemView.findViewById(R.id.friendUsername);
            friendEmail = (TextView) itemView.findViewById(R.id.friendEmail);
            friendPicture = (CircleImageView) itemView.findViewById(R.id.friendPicture);
        }
    }

    private RecyclerView friendsView, friendRequestsView;
    private LinearLayoutManager mFriendsManager, mFriendRequestsManager;

    private DatabaseReference newFriendListener, newFriendRequestListener;
    private FirebaseRecyclerAdapter<User, MessageViewHolder>
            mFriendsAdapter, mFriendRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        newFriendListener = FirebaseDatabase.getInstance().getReference()
                .child("friendships")
                .child(LOGGED_USER_ID);

        newFriendListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // added friend as a pair {userID-true}

                DatabaseReference getFriendObject = FirebaseDatabase.getInstance().getReference()
                        .child("users")
                        .child(dataSnapshot.getKey())
                        .child(dataSnapshot.getKey());

                getFriendObject.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(LOGGED_USER_ID).child("friends")
                                .child(dataSnapshot.getValue(User.class).getId())
                                .setValue(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("FriendsActivity", "Error binding friend with user"
                                + databaseError.getMessage());
                    }
                });

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FriendsActivity", "Error adding friendships: " + databaseError.getMessage());
            }
        });


        newFriendRequestListener = FirebaseDatabase.getInstance().getReference()
                .child("friend_requests").child(LOGGED_USER_ID);

        newFriendRequestListener.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //added friend request as userID-true

                DatabaseReference getFriendReqObject = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(dataSnapshot.getKey()).child(dataSnapshot.getKey());

                getFriendReqObject.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        FirebaseDatabase.getInstance().getReference().child("users")
                                .child(LOGGED_USER_ID).child("friend_requests")
                                .child(dataSnapshot.getValue(User.class).getId())
                                .setValue(dataSnapshot.getValue(User.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("FriendsActivity", "Error bind friendReq user: "
                                + databaseError.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FriendsActivity", "Error adding friend request: " + databaseError.getMessage());
            }
        });

        DatabaseReference mUserFriends = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER_ID).child("friends");
        DatabaseReference mUserFriendRequests = FirebaseDatabase.getInstance().getReference()
                .child("users").child(LOGGED_USER_ID).child("friend_requests");

        // This is the adapter for listing user's friends

        friendsView = (RecyclerView) findViewById(R.id.friendsView);
        mFriendsManager = new LinearLayoutManager(this);
        mFriendsManager.setStackFromEnd(true);
        friendsView.setLayoutManager(mFriendsManager);

        mFriendsAdapter = new FirebaseRecyclerAdapter<User, MessageViewHolder>(
                User.class,
                R.layout.friends_item,
                MessageViewHolder.class,
                mUserFriends) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, User user, int position) {
                viewHolder.friendUsername.setText(user.getName());
                viewHolder.friendEmail.setText(user.getId());
                Glide.with(FriendsActivity.this)
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
                    friendsView.scrollToPosition(positionStart);
                }
            }
        });

        // This is the adapter for listing user's friend requests

        friendRequestsView = (RecyclerView) findViewById(R.id.friendRequestsView);
        mFriendRequestsManager = new LinearLayoutManager(this);
        mFriendRequestsManager.setStackFromEnd(true);
        friendRequestsView.setLayoutManager(mFriendRequestsManager);

        mFriendRequestAdapter = new FirebaseRecyclerAdapter<User, MessageViewHolder>(
                User.class,
                R.layout.friends_item,
                MessageViewHolder.class,
                mUserFriendRequests) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, User user, int position) {
                viewHolder.friendUsername.setText(user.getName());
                viewHolder.friendEmail.setText(user.getId());
                Glide.with(FriendsActivity.this)
                        .load(user.getPhotoURL())
                        .into(viewHolder.friendPicture);
            }
        };

        mFriendRequestAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFriendRequestAdapter.getItemCount();
                int lastVisiblePosition =
                        mFriendRequestsManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    friendRequestsView.scrollToPosition(positionStart);
                }
            }
        });

    }

    void acceptFriendRequest(User whoseFriendRequest, User receiverFriendRequest) {
        FirebaseDatabase.getInstance().getReference().child("users")
                .child(whoseFriendRequest.getId()).child("friends")
                .child(receiverFriendRequest.getId()).setValue(receiverFriendRequest);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(receiverFriendRequest.getId()).child("friends")
                .child(whoseFriendRequest.getId()).setValue(whoseFriendRequest);

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(receiverFriendRequest.getId()).child("friend_requests")
                .child(whoseFriendRequest.getId()).removeValue();
    }
}
