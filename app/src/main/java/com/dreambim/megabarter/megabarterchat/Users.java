package com.dreambim.megabarter.megabarterchat;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;

/**
 * Created by admin on 02.02.2017.
 */

public class Users implements Serializable, IUser{

    private String id;
    private String email;
    private String name;
    private String avatar;

    public Users() {
    }

    public Users(String id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatar = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }
}