package com.dreambim.megabarter.megabarterchat.pojo;

import java.util.Comparator;

/**
 * Created by Operator on 26.03.2017.
 */

public class ChatMessComp implements Comparator<ChatMessage> {
    @Override
    public int compare(ChatMessage e1, ChatMessage e2) {
        if (e1.getTimeStampLong() > e2.getTimeStampLong()) {
            return 1;
        } else {
            return -1;
        }
    }
}
