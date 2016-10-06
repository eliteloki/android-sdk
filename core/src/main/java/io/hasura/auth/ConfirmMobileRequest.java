package io.hasura.auth;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ConfirmMobileRequest {
    @SerializedName("mobile")
    String mobile;

    @SerializedName("otp")
    int otp;
    @SerializedName("info")
    JsonObject info;

    public void setInfo(JsonObject info) {
        this.info = info;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setOTP(int otp) {
        this.otp = otp;
    }
}
