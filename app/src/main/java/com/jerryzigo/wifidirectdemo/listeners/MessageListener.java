package com.jerryzigo.wifidirectdemo.listeners;

/**
 * Created by jerry on 2015-04-30.
 */
public interface MessageListener {

    void onMessageReceived(String message);
    void onMessageSent();

}
