package com.dreambim.megabarter.megabarterchat.mvp;

import com.arellomobile.mvp.InjectViewState;
import com.dreambim.megabarter.megabarterchat.data.FirebaseMessageRepositoryImplement;
import com.dreambim.megabarter.megabarterchat.data.MessageCallback;
import com.dreambim.megabarter.megabarterchat.pojo.ChatMessage;
import com.dreambim.megabarter.megabarterchat.pojo.Users;

import java.util.TreeSet;

/**
 * Created by Operator on 26.03.2017.
 */

@InjectViewState
public class ChatPresenter extends AbstractChatPresenter<ChatView> implements MessageCallback {
    
    private final FirebaseMessageRepositoryImplement mCountryRepository;

    public ChatPresenter() {
        mCountryRepository = new FirebaseMessageRepositoryImplement();
    }

    @Override
    public void addToStart(ChatMessage message, boolean b) {
        getViewState().addToStart(message, b);
    }

    @Override
    public void notifyDataSetChanged() {
        getViewState().notifyDataSetChanged();
    }

    @Override
    public void clear() {
        getViewState().clear();
    }

    @Override
    public void addToEnd(TreeSet<ChatMessage> chatMessages) {

        getViewState().addToEnd(chatMessages);
    }

    @Override
    public void sendMessage(Users toUser, String message) {
        mCountryRepository.sendMessage(
                toUser,
                message
                , this
        );
    }

    @Override
    public void loadMessagesOnStart(Users toUser) {
        mCountryRepository.loadMessagesOnStart(toUser, this);
    }

    public Users getCurrentUser() {
        return mCountryRepository.getCurrentUser();
    }
}
