package com.dreambim.megabarter.megabarterchat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;

/**
 * Created by admin on 29.01.2017.
 */

public class ChatListFragment extends Fragment implements MessagesListAdapter.SelectionListener {


    private static final String TAG = "ChatListFragment";
    public static final String MESSAGES_CHILD = "messages";
    public static final String USERS_CHILD = "users";
    public static final String USER_MESSAGES_CHILD = "users-messages";
    public static final String USER_ID = "user_id";

    private Users toUser, currentUser;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private ArrayList<ChatMessage> ChatMessageList;
    private MessageInput input;
    private MessagesList messagesList;
    private MessagesListAdapter<ChatMessage> adapter;

    private int selectionCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);



        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        toUser = (Users) getActivity().getIntent().getSerializableExtra(USER_ID);
        currentUser = new Users(mFirebaseUser.getUid(),mFirebaseUser.getEmail()
                ,mFirebaseUser.getDisplayName());

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(toUser.getName());

        ChatMessageList = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        messagesList = (MessagesList) view.findViewById(R.id.messagesList);
        initMessagesAdapter();

        input = (MessageInput) view.findViewById(R.id.input);
        input.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                sendMessage();
                return true;
            }
        });

        return view;
    }

    private void initMessagesAdapter() {

        adapter = new MessagesListAdapter<>(currentUser.getId(), null);
        adapter.enableSelectionMode(this);

        loadMessages();
        /*
        adapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (totalItemsCount < 50) {
                    //loadMessages();
                }
            }
        });*/

        messagesList.setAdapter(adapter);
    }


    public void loadMessages(){

        ChatMessageList.clear();
        adapter.clear();
            final String uid = mFirebaseUser.getUid();
            Log.d(TAG, "uid " + mFirebaseUser.getUid());

            DatabaseReference refUid = FirebaseDatabase.getInstance()
                    .getReference().child(USER_MESSAGES_CHILD).child(uid).child(toUser.getId());
            refUid.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    final String messageId = dataSnapshot.getKey();

                    DatabaseReference messageRef = FirebaseDatabase.getInstance()
                            .getReference().child(MESSAGES_CHILD).child(messageId);
                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                                        message.setId(messageId);
                            if (message.getFromUID().contentEquals(toUser.getId())){
                                message.setUser(toUser);
                            }else {
                                message.setUser(currentUser);
                            }

                                ChatMessageList.add(message);
                                Log.d(TAG, "message = " + message.getId());

                                adapter.addToStart(message,true);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    //adapter.addToStart(mChatMessage, false);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
       // menu.findItem(R.id.action_delete).setVisible(count > 0);
    }

    private void sendMessage(){

        String message = input.getInputEditText().getText().toString();
        //ChatMessage mChatMessage;

        if(mFirebaseUser!= null && toUser != null) {
            final ChatMessage chatMessage = new ChatMessage(message,
                    mFirebaseUser.getUid(),
                    toUser.getId());
            final String messageKey = FirebaseDatabase.getInstance()
                    .getReference(MESSAGES_CHILD)
                    .push().getKey();
            //mChatMessage = chatMessage;
            FirebaseDatabase.getInstance().getReference(MESSAGES_CHILD).child(messageKey)
                    .setValue(chatMessage);
            FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(mFirebaseUser.getUid())
                    .child(toUser.getId()).child(messageKey).setValue(1);
            FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(toUser.getId())
                    .child(mFirebaseUser.getUid()).child(messageKey).setValue(1);

            adapter.notifyDataSetChanged();
        }
    }

}
