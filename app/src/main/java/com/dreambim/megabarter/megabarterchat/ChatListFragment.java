package com.dreambim.megabarter.megabarterchat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by admin on 29.01.2017.
 */

public class ChatListFragment extends Fragment {


    private static final String TAG = "ChatListFragment";
    public static final String MESSAGES_CHILD = "messages";
    public static final String USERS_CHILD = "users";
    public static final String USER_MESSAGES_CHILD = "users-messages";
    public static final String USER_ID = "user_id";
    public static final String ANONYMOUS = "anonymous";


    private String mUsername;
    private Users toUser;


    private ProgressBar mProgressBar;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private EditText mMessageEditText;

    private ArrayList<ChatMessage> ChatMessageList;
    private ChatMessageListAdapter ChatAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        toUser = (Users) getActivity().getIntent().getSerializableExtra(USER_ID);


    }

    @Override
    public void onResume() {
        super.onResume();
        ChatAdapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        ListView mChatListView = (ListView) view.findViewById(R.id.list);
        ChatMessageList = new ArrayList<>();
        //Log.d(TAG, "message " + ChatMessageList.size());
        ChatAdapter = new ChatMessageListAdapter();
        mChatListView.setAdapter(ChatAdapter);
        mChatListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mChatListView.setStackFromBottom(false);

        loadMessage();

        mMessageEditText = (EditText) view.findViewById(R.id.message_text);
        mMessageEditText.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        Button mSendButton = (Button) view.findViewById(R.id.btnSend);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });


        //loadMessage();
        return view;
    }


    public void loadMessage(){

        ChatMessageList.clear();
            final String uid = mFirebaseUser.getUid();
            Log.d(TAG, "mFU " + mFirebaseUser + " uid " + mFirebaseUser.getUid());
            DatabaseReference refUid = FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(uid);
            refUid.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    final String messageId = dataSnapshot.getKey();
                    DatabaseReference messageRef = FirebaseDatabase.getInstance()
                            .getReference(MESSAGES_CHILD).child(messageId);
                    messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ChatMessage message = dataSnapshot.getValue(ChatMessage.class);

                            if (message.getToUID().contentEquals(toUser.getId())
                                    || message.getFromUID().contentEquals(toUser.getId())) {

                                ChatMessageList.add(message);
                                ChatAdapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

       // ChatAdapter.notifyDataSetChanged();
    }

    private void sendMessage(){

        if (mMessageEditText.length() == 0)
            return;

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);

        String message = mMessageEditText.getText().toString();
        //String timeStamp = ServerValue.TIMESTAMP.toString();
        //FirebaseUser mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mFirebaseUser!= null && toUser != null) {
            final ChatMessage chatMessage = new ChatMessage(message,
                    mFirebaseUser.getUid(),
                    toUser.getId());
            //ChatMessageList.add(chatMessage);
            final String messageKey = FirebaseDatabase.getInstance()
                    .getReference(MESSAGES_CHILD)
                    .push().getKey();
            FirebaseDatabase.getInstance().getReference(MESSAGES_CHILD).child(messageKey)
                    .setValue(chatMessage);
            FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(mFirebaseUser.getUid()).child(messageKey)
                    .setValue(1);
            FirebaseDatabase.getInstance()
                    .getReference(USER_MESSAGES_CHILD).child(toUser.getId()).child(messageKey)
                    .setValue(1);

        }

        mMessageEditText.setText("");
    }


    private class ChatMessageListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return ChatMessageList.size();
        }

        @Override
        public ChatMessage getItem(int item) {
            return ChatMessageList.get(item);
        }

        @Override
        public long getItemId(int itemId) {
            return itemId;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {

            String uid;
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser != null){
                uid = firebaseUser.getUid();

                ChatMessage message = getItem(pos);
                Log.d(TAG, "time " + getCount() + " getItemId " + getItemId(pos));
                if (message.getToUID().contentEquals(uid))
                    view = getActivity().getLayoutInflater().inflate(R.layout.chat_item_fromid, null);
                else
                    view = getActivity().getLayoutInflater().inflate(R.layout.chat_item_toid, null);

                TextView lbl = (TextView) view.findViewById(R.id.lbl1);

                lbl = (TextView) view.findViewById(R.id.lbl2);
                lbl.setText(message.getText());

                lbl = (TextView) view.findViewById(R.id.lbl3);
                //lbl.setText(message.getTimeStamp().toString());
            }



            return view;
        }

    }


}
