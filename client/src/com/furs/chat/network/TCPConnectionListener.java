package com.furs.chat.network;


public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection connection);

    void onReceive(TCPConnection connection, String message);

    void onDisconnect(TCPConnection connection);

    void onError(TCPConnection connection, Exception e);
}
