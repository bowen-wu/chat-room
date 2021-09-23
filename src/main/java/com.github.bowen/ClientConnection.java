package com.github.bowen;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientConnection extends Thread {
    private final Socket socket;
    private final Server server;
    private String clientName;
    private final int clientId;

    public String getClientName() {
        return clientName;
    }

    public int getClientId() {
        return clientId;
    }

    public ClientConnection(int clientId, Server server, Socket socket) {
        this.clientId = clientId;
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (clientName == null) {
                    clientName = line;
                    server.registerClient(this);
                } else {
                    Message message = JSON.parseObject(line, Message.class);
                    server.sendMessage(this, message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            server.clientOffline(this);
        }
    }

    public void sendMessage(String message) {
        Utils.writeMessage(socket, message);
    }
}
