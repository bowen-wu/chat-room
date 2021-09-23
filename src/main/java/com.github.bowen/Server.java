package com.github.bowen;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Server {
    private final AtomicInteger COUNTER = new AtomicInteger(0);
    private final ServerSocket server;
    private final Map<Integer, ClientConnection> clients = new ConcurrentHashMap<Integer, ClientConnection>();

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port);
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = server.accept();
            new ClientConnection(COUNTER.incrementAndGet(), this, socket).start();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server(8080).start();
    }

    public void registerClient(ClientConnection clientConnection) {
        clients.put(clientConnection.getClientId(), clientConnection);
        this.clientOnline(clientConnection);
    }

    public String getAllClientsInfo() {
        return clients.entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue().getClientName()).collect(Collectors.joining(","));
    }

    public void sendMessage(ClientConnection clientConnection, Message message) {
        if (message.getId() == 0) {
            clients.values().forEach(client -> dispatchMessage(client, clientConnection.getClientName(), "所有人", message.getMessage()));
        } else {
            ClientConnection targetClient = clients.get(message.getId());
            if (targetClient == null) {
                System.err.println("用户" + message.getId() + "不存在");
            } else {
                dispatchMessage(targetClient, clientConnection.getClientName(), "你", message.getMessage());
            }
        }
    }

    public void clientOnline(ClientConnection clientWhoHasJustLoggedIn) {
        clients.values().forEach(client -> dispatchMessage(client, "系统", "所有人", clientWhoHasJustLoggedIn.getClientName() + "上线了！" + getAllClientsInfo()));
    }

    public void clientOffline(ClientConnection clientConnection) {
        clients.remove(clientConnection.getClientId());
        clients.values().forEach(client -> dispatchMessage(client, "系统", "所有人", clientConnection.getClientName() + "下线了！" + getAllClientsInfo()));
    }

    public void dispatchMessage(ClientConnection client, String source, String target, String message) {
        client.sendMessage(source + "对" + target + "说：" + message);
    }
}
