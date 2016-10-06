package io.hasura.auth;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ConfirmEmailRequest {
    @SerializedName("token")
    String token;
    @SerializedName("info")
    JsonObject info;

    public void setInfo(JsonObject info) {
        this.info = info;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
