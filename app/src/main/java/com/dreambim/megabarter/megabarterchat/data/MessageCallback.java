package com.dreambim.megabarter.megabarterchat.data;

import com.dreambim.megabarter.megabarterchat.pojo.ChatMessage;

import java.util.TreeSet;

/**
 * Created by Operator on 26.03.2017.
 */

public interface MessageCallback {

    void addToStart(ChatMessage message, boolean b);

    void notifyDataSetChanged();

    void clear();

    void addToEnd(TreeSet<ChatMessage> chatMessages);
}
