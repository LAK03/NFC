package com.example.coco.coconfctag.loginmodule;

/**
 * Created by cocoadmin on 3/17/2017.
 */

public class UserItem {
    private String id="";
    private String userName = "";
    private String password = "";

    public UserItem(String id,String userName, String password) {
        this.id=id;
        this.userName = userName;
        this.password = password;
    }

    public UserItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
