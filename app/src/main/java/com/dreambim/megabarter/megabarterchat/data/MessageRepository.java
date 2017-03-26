package com.dreambim.megabarter.megabarterchat.data;

import com.dreambim.megabarter.megabarterchat.pojo.Users;

/**
 * Created by Operator on 26.03.2017.
 */

public interface MessageRepository {
    
    void sendMessage(Users toUser, String message, MessageCallback messageCallback);

    void loadMessagesOnStart(Users toUser, MessageCallback messageCallback);

    Users getCurrentUser();
}
