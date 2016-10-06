package io.hasura.auth;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RegisterRequest implements Serializable{
    @SerializedName("username")
    String username;

    @SerializedName("password")
    String password;

    @SerializedName("email")
    String email;

    @SerializedName("mobile")
    String mobile;

    @SerializedName("info")
    JsonObject info;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setInfo(JsonObject info) {
        this.info = info;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public JsonObject getInfo() {
        return info;
    }
}
