package com.furs.chat.client;


import network.TCPConnection;
import network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private static final String IP_ADDR = "192.168.1.68";
    private static final int PORT = 8189;
    private static final int WIDTH = 600;
    public static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ClientWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private final JTextArea log = new JTextArea();
    private final JTextField fieldNick = new JTextField("Ilya： ");
    private final JTextField fieldInput = new JTextField("");

    private TCPConnection connection;

    private ClientWindow() throws IOException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        log.setEditable(false);
        log.setLineWrap(true);
        fieldInput.addActionListener(this);

        add(log, BorderLayout.CENTER);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNick, BorderLayout.NORTH);
        fieldNick.setFont(new Font("Tahoma", Font.BOLD, 14));

        setVisible(true);

        connection = new TCPConnection(this, IP_ADDR, PORT);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = fieldInput.getText();
        if(message.equals("")) return;
        fieldInput.setText(null);
        connection.sendMessage(fieldNick.getText() + ": " + message);
    }

    @Override
    public void onConnectionReady(TCPConnection connection) {
        printMessage("Connection ready");
    }

    @Override
    public void onReceive(TCPConnection connection, String message) {
        printMessage(message);
    }

    @Override
    public void onDisconnect(TCPConnection connection) {
        printMessage("Connection close");
    }

    @Override
    public void onError(TCPConnection connection, Exception e) {
        printMessage("Connection exception: " + e);
    }

    private synchronized void printMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            log.append(message + " \n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }
}
