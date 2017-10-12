package network;


import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private Thread thread;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener listener;

    public TCPConnection(TCPConnectionListener listener, String ipAddr, int port) throws IOException {
        this(listener, new Socket(ipAddr, port));
    }


    public TCPConnection(TCPConnectionListener listener, Socket socket) throws IOException {
        this.socket = socket;
        this.listener = listener;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        thread = new Thread(() -> {
            try {
                listener.onConnectionReady(TCPConnection.this);
                while (!thread.isInterrupted()) {
                    listener.onReceive(TCPConnection.this, in.readLine());
                }
            } catch (IOException e) {
                listener.onError(TCPConnection.this, e);
            } finally {
                listener.onDisconnect(TCPConnection.this);
            }
        });
        thread.start();
    }

    public synchronized void sendMessage(String message) {
        try {
            out.write(message + "\r\n");
            out.flush();
        } catch (IOException e) {
            listener.onError(TCPConnection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.onError(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ":" + socket.getPort();
    }
}
