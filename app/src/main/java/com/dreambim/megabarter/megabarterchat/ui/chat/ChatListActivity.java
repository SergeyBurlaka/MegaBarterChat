package com.dreambim.megabarter.megabarterchat.ui.chat;

/**
 * Created by admin on 10.02.2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.dreambim.megabarter.megabarterchat.ui.SingleFragmentActivity;
import com.dreambim.megabarter.megabarterchat.ui.chat.ChatListFragment;


public class ChatListActivity extends SingleFragmentActivity {



    @Override
    protected Fragment createFragment() {
        return new ChatListFragment();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
