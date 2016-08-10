package io.hasura.auth;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class SocialLoginResponse {
    @SerializedName("hasura_id")
    int hasuraId;

    @SerializedName("hasura_roles")
    String[] hasuraRoles;

    @SerializedName("new_user")
    boolean newUser;

    @SerializedName("auth_token")
    String auth_token;

    @SerializedName("info")
    JsonObject info;

    public int getHasuraId() {
        return hasuraId;
    }

    public String[] getHasuraRoles() {
        return hasuraRoles;
    }

    public String getSessionId() {
        return auth_token;
    }

    public boolean isNewUser() {
      return newUser;
    }

    public JsonObject getInfo() {
        return info;
    }
}
