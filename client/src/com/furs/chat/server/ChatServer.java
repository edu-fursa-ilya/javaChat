package com.furs.chat.server;

import com.furs.chat.network.TCPConnection;
import com.furs.chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer implements TCPConnectionListener{

    public static void main(String[] args) {
        new ChatServer();
    }

    private final List<TCPConnection> tcpConnectionList = new ArrayList<>();

    private ChatServer() {
        System.out.println("[+]Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) {
            while (true) {
                try {
                     new TCPConnection(this, serverSocket.accept());
                } catch (Exception e) {
                    System.out.println("TCPConnection Exception " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection connection) {
        tcpConnectionList.add(connection);
        sendToAllConnections("Client connected: " + connection.toString());
    }

    @Override
    public synchronized void onReceive(TCPConnection connection, String message) {
        sendToAllConnections(message);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection connection) {
        tcpConnectionList.remove(connection);
        sendToAllConnections("Client disconnected: " + connection.toString());
    }

    @Override
    public synchronized void onError(TCPConnection connection, Exception e) {
        System.out.println("TCP Exception: " + e);
    }

    private void sendToAllConnections(String message) {
        System.out.println(message);

        for (TCPConnection connection: tcpConnectionList) {
            connection.sendMessage(message);
        }
    }
}
