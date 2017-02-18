package com.dreambim.megabarter.megabarterchat;

/**
 * Created by admin on 10.02.2017.
 */

import android.support.v4.app.Fragment;


public class ChatListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ChatListFragment();

    }
}
