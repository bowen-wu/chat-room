package com.github.bowen;

import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("输入你的昵称！");
        Scanner userInput = new Scanner(System.in);
        String name = userInput.nextLine();

        Socket socket = new Socket("127.0.0.1", 8080);
        Utils.writeMessage(socket, name);

        System.out.println("连接成功！");

        new Thread(() -> readFromServer(socket)).start();
        while (true) {
            System.out.println("输入你要发送的聊天内容");
            System.out.println("id:message,例如，1:hello代表向id为1的用户发送hello消息");
            System.out.println("id=0代表向所有人发送消息，例如，0:hello代表向所有在线用户发送hello消息");

            String line = userInput.nextLine();
            if (line.contains(":")) {
                int colonIndex = line.indexOf(":");
                int id = Integer.parseInt(line.substring(0, colonIndex));
                String message = line.substring(colonIndex + 1);
                Utils.writeMessage(socket, JSON.toJSONString(new Message(id, message)));
            } else {
                System.out.println("输入的格式不对!");
            }

        }
    }

    private static void readFromServer(Socket socket) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
