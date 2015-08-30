package com.dnj.exchange.net;

public interface HttpCallbackListener {
    void onfinish(String response);
    void onError();
}
