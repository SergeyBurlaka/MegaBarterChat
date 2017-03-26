package com.dreambim.megabarter.megabarterchat.data;

import com.dreambim.megabarter.megabarterchat.pojo.ChatMessComp;
import com.dreambim.megabarter.megabarterchat.pojo.ChatMessage;
import com.dreambim.megabarter.megabarterchat.pojo.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.TreeSet;

/**
 * Created by Operator on 26.03.2017.
 */

public class FirebaseMessageRepositoryImplement implements MessageRepository {

    public static final String MESSAGES_CHILD = "messages";
    public static final String USERS_CHILD = "users";
    public static final String USER_MESSAGES_CHILD = "users-messages";
    private Users currentUser;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private boolean isFirst = true;
    private MessageCallback mMessageCallback;
    final private TreeSet<ChatMessage> chatMessageTreeSet = new TreeSet<>(new ChatMessComp());
    private Users toUser;

    public FirebaseMessageRepositoryImplement() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        currentUser = new Users(mFirebaseUser.getUid()
                , mFirebaseUser.getEmail()
                , mFirebaseUser.getDisplayName());
    }

    @Override
    public void sendMessage(Users toUser, String message, MessageCallback messageCallback) {
        if (mFirebaseUser != null && toUser != null) {
            final ChatMessage chatMessage = new ChatMessage(message,
                    mFirebaseUser.getUid(),
                    toUser.getId());

            final String messageKey = FirebaseDatabase.getInstance()
                    .getReference(MESSAGES_CHILD)
                    .push().getKey();

            FirebaseDatabase.getInstance().getReference(MESSAGES_CHILD).child(messageKey)
                    .setValue(chatMessage);

            FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(mFirebaseUser.getUid())
                    .child(toUser.getId()).child(messageKey).setValue(1);

            FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(toUser.getId())
                    .child(mFirebaseUser.getUid()).child(messageKey).setValue(1);

            messageCallback.notifyDataSetChanged();
        }
    }

    @Override
    public void loadMessagesOnStart(final Users toUser, final MessageCallback messageCallback) {
        mMessageCallback = messageCallback;
        this.toUser = toUser;

        final String uid = mFirebaseUser.getUid();
        chatMessageTreeSet.clear();
        messageCallback.clear();

        DatabaseReference refUid = FirebaseDatabase.getInstance()
                .getReference(USER_MESSAGES_CHILD).child(uid).child(toUser.getId());

        refUid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Map<String, Object> messageIdList = (Map<String, Object>) dataSnapshot.getValue();

                if (messageIdList == null) {
                    return;
                }
                final int totalLastMessages = messageIdList.size();

                for (final String messageId : messageIdList.keySet()) {
                    DatabaseReference messageRef = FirebaseDatabase.getInstance()
                            .getReference(MESSAGES_CHILD).child(messageId);

                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                            message.setId(messageId);
                            if (message.getFromUID().contentEquals(toUser.getId())) {
                                message.setUser(toUser);
                            } else {
                                message.setUser(currentUser);
                            }

                            chatMessageTreeSet.add(message);

                            if (totalLastMessages == chatMessageTreeSet.size()) {
                                mMessageCallback.addToEnd(chatMessageTreeSet);
                                initMessagePostListener();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //on Cancelled
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on Cancelled
            }
        });
    }

    @Override
    public Users getCurrentUser() {
        return currentUser;
    }

    private void initMessagePostListener() {
        final String uid = mFirebaseUser.getUid();

        DatabaseReference refUid = FirebaseDatabase.getInstance()
                .getReference(USER_MESSAGES_CHILD).child(uid).child(toUser.getId());

        refUid.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChild) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }

                final String messageId = dataSnapshot.getKey();
                DatabaseReference messageRef = FirebaseDatabase.getInstance()
                        .getReference(MESSAGES_CHILD).child(messageId);

                messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                        if (message == null) {
                            return;
                        }
                        message.setId(messageId);
                        if (message.getFromUID().contentEquals(toUser.getId())) {
                            message.setUser(toUser);
                        } else {
                            message.setUser(currentUser);
                        }

                        mMessageCallback.addToStart(message, true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //on cancell
                    }

                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //on Child Changed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //on Child Removed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //on Child Moved
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // on Cancelled
            }

        });
    }
}
