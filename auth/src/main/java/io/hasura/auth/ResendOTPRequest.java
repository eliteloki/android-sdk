package io.hasura.auth;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ResendOTPRequest {
    @SerializedName("mobile")
    String mobile;

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
