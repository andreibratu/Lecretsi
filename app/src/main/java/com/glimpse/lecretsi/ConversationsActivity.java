package com.glimpse.lecretsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsActivity extends Fragment {

    // This is the activity that's gonna contain all the conversations

    public RecyclerView conversationsView;
    public LinearLayout emptyConversationsBackground;
    public DatabaseReference mConversations;
    public LinearLayoutManager mConversationsManager;
    public FirebaseRecyclerAdapter<Conversation, ConversationsHolder> mConversationsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_conversations, container, false);

        conversationsView = (RecyclerView) view.findViewById(R.id.conversationsView);
        emptyConversationsBackground = (LinearLayout) view.findViewById(R.id.emptyConversationsBackground);

        mConversations = FirebaseDatabase.getInstance().getReference()
                .child("users").child(MainActivity.loggedInUser.getId()).child("conversations");

        // This is the adapter for displaying user's friend requests

        mConversationsManager = new LinearLayoutManager(getActivity());

        emptyConversationsBackground.setVisibility(View.GONE);
        mConversationsAdapter = new FirebaseRecyclerAdapter<Conversation, ConversationsHolder>(
                Conversation.class, R.layout.conversations_item, ConversationsHolder.class, mConversations.orderByValue()) {

            @Override
            protected void populateViewHolder(final ConversationsHolder viewHolder, final Conversation conversation, int position) {
                // TODO: Test if last message is responsive, when someone else sends one
                viewHolder.conversationUsername.setText(conversation.getUser().getName());

                mConversations.child(conversation.getUser().getId()).child("lastMessage")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    viewHolder.lastMessage.setVisibility(View.GONE);
                                } else {
                                    viewHolder.lastMessage.setVisibility(View.VISIBLE);
                                    viewHolder.lastMessage.setText(dataSnapshot.getValue().toString());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                mConversations.child(conversation.getUser().getId()).child("lastMessageDate")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Long timestamp = Long.parseLong(dataSnapshot.getValue().toString());
                                    DateFormat dateFormat = new SimpleDateFormat("d EEE", Locale.FRANCE);
                                    viewHolder.lastMessageDate.setText(dateFormat.format(new Date(timestamp)));
                                    viewHolder.lastMessageDate.setVisibility(View.VISIBLE);
                                } else {
                                    viewHolder.lastMessageDate.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                Glide.with(ConversationsActivity.this)
                        .load(conversation.getUser().getPhotoURL())
                        .into(viewHolder.conversationPicture);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent;
                                if (conversation.getUser().getId().equals("largonjiAssistant")) {
                                    intent = new Intent(getActivity(), AssistantActivity.class);
                                } else {
                                    intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("friendUserID", conversation.getUser().getId());
                                    intent.putExtra("friendUsername", conversation.getUser().getName());
                                }
                                startActivity(intent);
                            }
                        }, 500);
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        final ViewGroup nullParent = null;
                        final LayoutInflater li = LayoutInflater.from(getActivity());
                        View dialogView = li.inflate(R.layout.long_click_dialog, nullParent);

                        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity(), R.style.alertDialog);

                        alertDialogBuilder.setView(dialogView);

                        final AlertDialog alertDialog = alertDialogBuilder.create();

                        final LinearLayout archive = (LinearLayout) dialogView.findViewById(R.id.archive);
                        final LinearLayout delete = (LinearLayout) dialogView.findViewById(R.id.delete);
                        final LinearLayout block = (LinearLayout) dialogView.findViewById(R.id.block);

                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(DialogInterface dialog) {
                                archive.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                                delete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(getActivity());
                                        deleteDialogBuilder.setTitle(R.string.dialog_delete_title).setMessage(R.string.dialog_delete_message)
                                                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        mConversations.child(conversation.getUser().getId()).removeValue();
                                                        dialog.dismiss();
                                                    }
                                                })
                                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        deleteDialogBuilder.show();
                                        alertDialog.dismiss();
                                    }
                                });
                                block.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                });
                            }

                        });
                        alertDialog.show();
                        return false;
                    }
                });
            }

            @Override
            public int getItemCount() {
                return super.getItemCount();
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
                emptyConversationsBackground.setVisibility(View.GONE);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                if (mConversationsAdapter.getItemCount() == 0) {
                    emptyConversationsBackground.setVisibility(View.VISIBLE);
                }
            }
        });

        if (mConversationsAdapter.getItemCount() == 0) {
            emptyConversationsBackground.setVisibility(View.VISIBLE);
        } else {
            emptyConversationsBackground.setVisibility(View.GONE);
        }

        conversationsView.setLayoutManager(mConversationsManager);
        conversationsView.setAdapter(mConversationsAdapter);
        return view;
    }

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

}
