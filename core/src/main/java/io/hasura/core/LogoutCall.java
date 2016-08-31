package io.hasura.core;

import android.os.Handler;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by loki on 19/08/16.
 */

public class LogoutCall<T, E extends Exception> {

    private final Converter<T, E> converter;
    /* Underlying okhttp call */
    private okhttp3.Call rawCall;
    public LogoutCall(okhttp3.Call rawCall, Converter<T, E> converter1) {
        this.converter = converter1;
        this.rawCall = rawCall;
    }

    public Request request() {
        return rawCall.request();
    }

    public void enqueue(final Callback<T, E> callback) {
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response rawResponse)
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
                    Hasura.clearSession();
                    callback.onSuccess(response);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }
    public void enqueueOnUIThread(final Callback<T, E> callback) {
        final Handler handler = new Handler();
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, Response rawResponse)
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

            private void callFailure(final E he) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onFailure(he);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });
            }

            private void callSuccess(final T response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Hasura.clearSession();
                            callback.onSuccess(response);
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });
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
