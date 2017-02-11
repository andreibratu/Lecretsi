package com.glimpse.lecretsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    static final User LOGGED_USER = ConversationsActivity.loggedInUser;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        // This is the adapter for inflating friend requests and friends in a single RecyclerView

        RecyclerView friendsViewList = (RecyclerView) findViewById(R.id.friendsViewList);
        LinearLayoutManager mFriendsListManager = new LinearLayoutManager(this);
        mFriendsListManager.setStackFromEnd(true);

        RecyclerView.Adapter<FriendsViewHolder> friendsViewAdapter = new RecyclerView.Adapter<FriendsViewHolder>() {
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

                // Create conversation with the assistant

                holder.assistantItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                User assistant = new User("largonjiAssistant", "Largonji Assistant", "largonji@assistant.com", "http://i.imgur.com/NglEj0p.png");
                                Conversation largonjiConversation = new Conversation(assistant, null, null);
                                DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference();
                                conversationReference.child("users").child(LOGGED_USER.getId()).child("conversations").child(largonjiConversation.getUser().getId()).setValue(largonjiConversation);
                                startActivity(new Intent(FriendsActivity.this, ChatActivity.class).
                                        putExtra("userId", largonjiConversation.getUser().getId()));
                                finish();
                            }
                        }, 500);
                    }
                });

                // Database queries for retrieving items

                DatabaseReference mUserFriendRequests = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(LOGGED_USER.getId()).child("friend_requests");
                DatabaseReference mUserFriends = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(LOGGED_USER.getId()).child("friends");

                // This is the adapter for displaying user's friend requests

                final LinearLayoutManager mFriendRequestsManager = new LinearLayoutManager(getApplicationContext());
                mFriendRequestsManager.setStackFromEnd(true);

                final FirebaseRecyclerAdapter<User, FriendsListHolder> mFriendRequestsAdapter, mFriendsAdapter;

                mFriendRequestsAdapter = new FirebaseRecyclerAdapter<User, FriendsListHolder>(
                        User.class, R.layout.friends_item, FriendsListHolder.class, mUserFriendRequests) {

                    @Override
                    protected void populateViewHolder(final FriendsListHolder viewHolder, final User user, int position) {
                        viewHolder.friendUsername.setText(user.getName());
                        viewHolder.friendEmail.setText(user.getEmail());
                        Glide.with(getApplicationContext())
                                .load(user.getPhotoURL())
                                .into(viewHolder.friendPicture);

                        // Build a dialog for accepting or denying a friend request

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(FriendsActivity.this, "I clicked " + viewHolder.itemView, Toast.LENGTH_SHORT).show();

                                final ViewGroup nullParent = null;
                                LayoutInflater li = LayoutInflater.from(FriendsActivity.this);
                                final View dialogView = li.inflate(R.layout.friend_request_dialog, nullParent);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FriendsActivity.this, R.style.alertDialog);

                                alertDialogBuilder.setView(dialogView);

                                final TextView friendRequestNameText = (TextView) dialogView.findViewById(R.id.friendRequestNameText);
                                final TextView friendRequestEmailText = (TextView) dialogView.findViewById(R.id.friendRequestEmailText);
                                final ImageView friendRequestPicture = (ImageView) dialogView.findViewById(R.id.friendRequestPicture);

                                alertDialogBuilder.setPositiveButton("Accept", null);
                                alertDialogBuilder.setNegativeButton("Deny", null);

                                alertDialog = alertDialogBuilder.create();


                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialog) {
                                        friendRequestNameText.setText(user.getName());
                                        friendRequestEmailText.setText(user.getEmail());
                                        Glide.with(getApplicationContext())
                                                .load(user.getPhotoURL())
                                                .asBitmap()
                                                .into(friendRequestPicture);

                                        Button accept = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        Button deny = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                        final DatabaseReference friendRequest = FirebaseDatabase.getInstance().getReference().child("users");

                                        // TODO: Andrei, pune-ti toasturile :P

                                        accept.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                friendRequest.child(LOGGED_USER.getId()).child("friends").child(user.getId()).setValue(user);
                                                friendRequest.child(user.getId()).child("friends").child(LOGGED_USER.getId()).setValue(LOGGED_USER);
                                                friendRequest.child(LOGGED_USER.getId()).child("friend_requests").child(user.getId()).removeValue();
                                                alertDialog.dismiss();
                                            }
                                        });

                                        deny.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                friendRequest.child(LOGGED_USER.getId()).child("friend_requests").child(LOGGED_USER.getId()).removeValue();
                                                alertDialog.dismiss();
                                            }
                                        });
                                    }
                                });
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.show();
                                        if(alertDialog.getWindow() != null){
                                            alertDialog.getWindow().setLayout(850, 975);
                                        }
                                    }
                                }, 500);
                            }
                        });
                    }

                    @Override
                    public int getItemCount() {
                        return super.getItemCount();
                    }
                };

                mFriendRequestsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendRequestsCount = mFriendRequestsAdapter.getItemCount();
                        int lastVisiblePosition =
                                mFriendRequestsManager.findLastCompletelyVisibleItemPosition();
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendRequestsCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            holder.friendRequestsView.scrollToPosition(positionStart);
                        }
                        holder.friendRequestsText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);
                        if (mFriendRequestsAdapter.getItemCount() == 0) {
                            holder.friendRequestsText.setVisibility(View.GONE);
                        }
                    }
                });

                if (mFriendRequestsAdapter.getItemCount() == 0) {
                    holder.friendRequestsText.setVisibility(View.GONE);
                } else {
                    holder.friendRequestsText.setVisibility(View.VISIBLE);
                }

                holder.friendRequestsView.setLayoutManager(mFriendRequestsManager);
                holder.friendRequestsView.setAdapter(mFriendRequestsAdapter);

                // This is the adapter for displaying user's friends

                final LinearLayoutManager mFriendsManager = new LinearLayoutManager(getApplicationContext());
                mFriendsManager.setStackFromEnd(true);

                mFriendsAdapter = new FirebaseRecyclerAdapter<User, FriendsListHolder>(
                        User.class, R.layout.friends_item, FriendsListHolder.class, mUserFriends) {

                    @Override
                    protected void populateViewHolder(FriendsListHolder viewHolder, final User user, int position) {
                        viewHolder.friendUsername.setText(user.getName());
                        viewHolder.friendEmail.setText(user.getEmail());
                        Glide.with(getApplicationContext())
                                .load(user.getPhotoURL())
                                .into(viewHolder.friendPicture);

                        // Start a conversation when the user clicks a friend

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        final Conversation conversation = new Conversation(user, null, null);
                                        final DatabaseReference conversationReference = FirebaseDatabase.getInstance().getReference()
                                                .child("users").child(LOGGED_USER.getId()).child("conversations");
                                        conversationReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                boolean userFound = false;
                                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                                    if(postSnapshot.getKey().equals(user.getId())){
                                                        userFound = true;
                                                    }
                                                }
                                                if (!userFound) {
                                                    conversationReference.child(user.getId()).setValue(conversation);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                        startActivity(new Intent(FriendsActivity.this, ChatActivity.class).
                                                putExtra("userId", user.getId()));
                                        finish();
                                    }
                                }, 500);
                            }
                        });
                    }
                };

                mFriendsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        int friendlyMessageCount = mFriendsAdapter.getItemCount();
                        int lastVisiblePosition =
                                mFriendsManager.findLastCompletelyVisibleItemPosition();
                        if (lastVisiblePosition == -1 ||
                                (positionStart >= (friendlyMessageCount - 1) &&
                                        lastVisiblePosition == (positionStart - 1))) {
                            holder.friendsView.scrollToPosition(positionStart);
                        }
                        holder.friendsText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);
                        if (mFriendsAdapter.getItemCount() == 0) {
                            holder.friendsText.setVisibility(View.GONE);
                        }
                    }
                });

                if (mFriendRequestsAdapter.getItemCount() == 0) {
                    holder.friendsText.setVisibility(View.GONE);
                } else {
                    holder.friendsText.setVisibility(View.VISIBLE);
                }

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
                View dialogView = li.inflate(R.layout.friend_add_dialog, nullParent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder
                        (FriendsActivity.this, R.style.alertDialog);

                alertDialogBuilder.setView(dialogView);

                final EditText friendEmail = (EditText)
                        dialogView.findViewById(R.id.friendRequestEmail);

                alertDialogBuilder.setPositiveButton(R.string.add_friend, null);

                alertDialog = alertDialogBuilder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final String friendEmailText = friendEmail.getText().toString();

                                if (friendEmailText.isEmpty()) {
                                    Toast.makeText(FriendsActivity.this, R.string.no_email_inserted, Toast.LENGTH_LONG).show();
                                    return;
                                }
                                /* Disabled for debug
                                    TODO: Enable this exception
                                if (friendEmailText.equals(LOGGED_USER.getEmail())) {
                                    Toast.makeText(FriendsActivity.this, "You can't send a friend request to yourself ^_^", Toast.LENGTH_LONG).show();
                                    return;
                                }*/

                                final DatabaseReference friendRequest = FirebaseDatabase.getInstance().getReference().child("users");
                                friendRequest.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        boolean userFound = false;
                                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                                            final User user = postSnapshot.getValue(User.class);
                                            if(user.getEmail().equals(friendEmailText)){
                                                userFound = true;
                                                DatabaseReference requestNotSent = FirebaseDatabase.getInstance().getReference()
                                                        .child("users").child(user.getId()).child("friend_requests").child(LOGGED_USER.getId());
                                                requestNotSent.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot snapshot) {
                                                        if (snapshot.getValue() == null) {
                                                            DatabaseReference friendNotAdded = FirebaseDatabase.getInstance().getReference()
                                                                    .child("users").child(LOGGED_USER.getId()).child("friends").child(user.getId());
                                                            friendNotAdded.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot snapshot) {
                                                                    if (snapshot.getValue() == null) {
                                                                        friendRequest.child(user.getId()).child("friend_requests").child(LOGGED_USER.getId()).setValue(LOGGED_USER);
                                                                        Toast.makeText(getApplicationContext(), R.string.friend_request_sent, Toast.LENGTH_LONG).show();
                                                                        alertDialog.dismiss();
                                                                    } else {
                                                                        Toast.makeText(FriendsActivity.this, R.string.friend_already_added, Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(FriendsActivity.this, R.string.friend_req_already_sent, Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
                                        }
                                        if(!userFound){
                                            Toast.makeText(getApplicationContext(), R.string.user_not_found, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });

    }

    public static class FriendsListHolder extends RecyclerView.ViewHolder {

        TextView friendUsername;
        TextView friendEmail;
        CircleImageView friendPicture;

        public FriendsListHolder(View itemView) {
            super(itemView);
            friendUsername = (TextView) itemView.findViewById(R.id.friendUsername);
            friendEmail = (TextView) itemView.findViewById(R.id.friendEmail);
            friendPicture = (CircleImageView) itemView.findViewById(R.id.friendPicture);
        }
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        LinearLayout assistantItem;
        RecyclerView friendRequestsView, friendsView;
        TextView friendRequestsText, friendsText;

        FriendsViewHolder(View v) {
            super(v);
            assistantItem = (LinearLayout) itemView.findViewById(R.id.assistantItem);
            friendRequestsView = (RecyclerView) itemView.findViewById(R.id.friendRequestsView);
            friendsView = (RecyclerView) itemView.findViewById(R.id.friendsView);
            friendRequestsText = (TextView) itemView.findViewById(R.id.friendRequestsText);
            friendsText = (TextView) itemView.findViewById(R.id.friendsText);
        }
    }

    /* TODO acceptFriendRequest
    void acceptFriendRequest(User whoseFriendRequest, User receiverFriendRequest) {

    }*/
}
