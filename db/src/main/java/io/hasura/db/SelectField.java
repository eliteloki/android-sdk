package io.hasura.db;

import com.google.gson.JsonElement;

public interface SelectField<R> {

    JsonElement toQCol();

}
