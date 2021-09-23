package com.github.bowen;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static void writeMessage(Socket socket, String message) {
        try {
            socket.getOutputStream().write(message.getBytes(StandardCharsets.UTF_8));
            socket.getOutputStream().write('\n');
            socket.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
