package com.dreambim.megabarter.megabarterchat.mvp;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.dreambim.megabarter.megabarterchat.pojo.Users;

/**
 * Created by Operator on 26.03.2017.
 */

abstract class AbstractChatPresenter<View extends MvpView> extends MvpPresenter<View> {

    public abstract void sendMessage(Users toUser, String message);

    public abstract void loadMessagesOnStart(Users toUser);

}
