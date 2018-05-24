package com.xinliangjishipin.pushwms.entity;


import java.io.Serializable;

/**
 * Created by 95 on 2017/3/16.
 */

public class LoginReq implements Serializable{

    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginReq{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
