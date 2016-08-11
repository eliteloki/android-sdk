package io.hasura.db.util;

import com.google.gson.annotations.SerializedName;

public enum RelType {
    @SerializedName("arr_rel")
    ARR_REL,

    @SerializedName("obj_rel")
    OBJ_REL;
}
