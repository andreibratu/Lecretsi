package com.glimpse.lecretsi;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;



public class FriendsAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }



    private static class FriendRequestsHolder extends RecyclerView.ViewHolder {

        TextView friendUsername;
        TextView friendEmail;
        CircleImageView friendPicture;

        public FriendRequestsHolder(View itemView) {
            super(itemView);
            friendUsername = (TextView) itemView.findViewById(R.id.friendUsername);
            friendEmail = (TextView) itemView.findViewById(R.id.friendEmail);
            friendPicture = (CircleImageView) itemView.findViewById(R.id.friendPicture);
            // prepare your ViewHolder
        }
    }

    private static class FriendsHolder extends RecyclerView.ViewHolder {

        TextView friendUsername;
        TextView friendEmail;
        CircleImageView friendPicture;

        // like the one above
        public FriendsHolder(View itemView) {
            super(itemView);
            friendUsername = (TextView) itemView.findViewById(R.id.friendUsername);
            friendEmail = (TextView) itemView.findViewById(R.id.friendEmail);
            friendPicture = (CircleImageView) itemView.findViewById(R.id.friendPicture);
            // prepare your ViewHolder
        }
    }

    final String LOGGED_USER_ID = ConversationsActivity.loggedInUser.getId();

    DatabaseReference mUserFriends = FirebaseDatabase.getInstance().getReference()
            .child("users").child(LOGGED_USER_ID).child("friends");
    DatabaseReference mUserFriendRequests = FirebaseDatabase.getInstance().getReference()
            .child("users").child(LOGGED_USER_ID).child("friend_requests");

    private FirebaseRecyclerAdapter<User, FriendRequestsHolder> mFriendRequests, mFriendsAdapter;

    Context context;


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        mFriendsAdapter = new FirebaseRecyclerAdapter<User, FriendRequestsHolder>(
                User.class,
                R.layout.friends_item,
                FriendsAdapter.FriendRequestsHolder.class,
                mUserFriendRequests) {

            @Override
            protected void populateViewHolder(FriendRequestsHolder viewHolder, User user, int position) {
                viewHolder.friendUsername.setText(user.getName());
                viewHolder.friendEmail.setText(user.getEmail());
                Glide.with(context)
                        .load(user.getPhotoURL())
                        .into(viewHolder.friendPicture);
            }
        };


        return null;
    }

}
