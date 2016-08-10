package io.hasura.core;

import okhttp3.Request;

import java.io.IOException;

public class Call<T, E extends Exception> {

    private final Converter<T, E> converter;
    /* Underlying okhttp call */
    private okhttp3.Call rawCall;

    public Call(okhttp3.Call rawCall, Converter<T, E> converter) {
        this.converter = converter;
        this.rawCall = rawCall;
    }

    public Request request() {
        return rawCall.request();
    }

    public void enqueue(final Callback<T, E> callback) {
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse)
                    throws IOException {
                T response;
                try {
                    response = converter.fromResponse(rawResponse);
                } catch (Exception e) {
                    callFailure(converter.castException(e));
                    return;
                }
                callSuccess(response);
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                try {
                    callFailure(converter.fromIOException(e));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            private void callFailure(E he) {
                try {
                    callback.onFailure(he);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            private void callSuccess(T response) {
                try {
                    callback.onSuccess(response);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    public boolean isExecuted() {
        return rawCall.isExecuted();
    }

    public T execute() throws E {
        try {
            return converter.fromResponse(rawCall.execute());
        } catch (IOException e) {
            throw converter.fromIOException(e);
        }
    }

    public void cancel() {
        rawCall.cancel();
    }

    public boolean isCancelled() {
        return rawCall.isCanceled();
    }
}
