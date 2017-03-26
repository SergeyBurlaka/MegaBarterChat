package com.dreambim.megabarter.megabarterchat.mvp;

import com.arellomobile.mvp.MvpView;
import com.dreambim.megabarter.megabarterchat.pojo.ChatMessage;

import java.util.TreeSet;

/**
 * Created by Operator on 26.03.2017.
 */


public interface ChatView extends MvpView {

    void addToStart(ChatMessage message, boolean b);

    void notifyDataSetChanged();

    void clear();

    void addToEnd(TreeSet<ChatMessage> chatMessages);

    void showProgress();

    void hideProgress();
}
