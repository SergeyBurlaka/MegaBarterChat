package com.dreambim.megabarter.megabarterchat.ui.chat;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.dreambim.megabarter.megabarterchat.R;
import com.dreambim.megabarter.megabarterchat.mvp.ChatPresenter;
import com.dreambim.megabarter.megabarterchat.mvp.ChatView;
import com.dreambim.megabarter.megabarterchat.pojo.ChatMessage;
import com.dreambim.megabarter.megabarterchat.pojo.Users;
import com.dreambim.megabarter.megabarterchat.ui.ProgressDialog;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by admin on 29.01.2017.
 */

public class ChatListFragment extends MvpAppCompatFragment
        implements ChatView, MessagesListAdapter.SelectionListener {

    @InjectPresenter
    ChatPresenter chatPresenter;
    private MessageInput mMessageInput;
    private MessagesList messagesList;
    private MessagesListAdapter<ChatMessage> adapter;
    private Users toUser;
    public static final String USER_ID = "user_id";
    AppCompatActivity myAppCompatActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        toUser = (Users) getActivity().getIntent().getSerializableExtra(USER_ID);

        if (getActivity() instanceof AppCompatActivity) {
            myAppCompatActivity = (AppCompatActivity) getActivity();
        }

        if (myAppCompatActivity != null) {
            myAppCompatActivity.getSupportActionBar().setTitle(toUser.getName());
            myAppCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            myAppCompatActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
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

        adapter = new MessagesListAdapter<>(chatPresenter.getCurrentUser().getId(), null);
        adapter.enableSelectionMode(this);
        messagesList.setAdapter(adapter);

        if (savedInstanceState == null) {
            initMessagesRepository();
        }

        mMessageInput = (MessageInput) view.findViewById(R.id.input);

        mMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                chatPresenter.sendMessage(toUser, mMessageInput.getInputEditText().getText().toString());
                return true;
            }
        });
        return view;
    }

    private void initMessagesRepository() {
        showProgress();
        chatPresenter.loadMessagesOnStart(toUser);
    }

    @Override
    public void onSelectionChanged(int count) {
    }

    @Override
    public void addToStart(ChatMessage message, boolean b) {
        adapter.addToStart(message, b);
    }

    @Override
    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void addToEnd(TreeSet<ChatMessage> chatMessages) {
        List<ChatMessage> chatMessagesList = new ArrayList<ChatMessage>(chatMessages);
        hideProgress();
        adapter.addToEnd(chatMessagesList, true);
    }

    @Override
    public void showProgress() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(ProgressDialog.class.getName());
        if (fragment == null) {
            fragment = ProgressDialog.newInstance();
        }
        if (fragment instanceof DialogFragment) {
            DialogFragment dialog = (DialogFragment) fragment;
            if (dialog.isAdded()) {
                dialog.dismiss();
                //TODO Fix it later
                /* Fix issue with findFragmentById. After show dialog in second time, its tag sets to null... */
                ProgressDialog.newInstance().show(getActivity().getSupportFragmentManager(), ProgressDialog.class.getName());
            } else {
                dialog.show(getFragmentManager(), ProgressDialog.class.getName());
            }
        }
    }

    @Override
    public void hideProgress() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(ProgressDialog.class.getName());
        if (fragment != null && fragment instanceof DialogFragment) {
            DialogFragment dialog = (DialogFragment) fragment;
            dialog.dismiss();
        }
    }
}

