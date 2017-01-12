package io.hasura.core;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by loki on 18/08/16.
 */

public class HasuraTokenInterceptor implements Interceptor {
    String userRole = "";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response;
        Request newRequest = null;
        if(Hasura.getRequestType() && !Hasura.getUserToken().equals("")) {
            newRequest = request.newBuilder()
                    .addHeader("Authorization", "Bearer " + Hasura.getUserToken())
                    .build();
        }else {
            newRequest = request.newBuilder()
                    .build();
        }
        response = chain.proceed(newRequest);
        return response;
    }
}
