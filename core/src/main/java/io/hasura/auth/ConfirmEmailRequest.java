package io.hasura.auth;

import com.google.gson.annotations.SerializedName;

public class ConfirmEmailRequest {
    @SerializedName("token")
    String token;

    public void setToken(String token) {
        this.token = token;
    }
}
