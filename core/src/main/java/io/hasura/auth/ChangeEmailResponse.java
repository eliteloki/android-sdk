package io.hasura.auth;

import com.google.gson.annotations.SerializedName;

public class ChangeEmailResponse {
    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }
}
