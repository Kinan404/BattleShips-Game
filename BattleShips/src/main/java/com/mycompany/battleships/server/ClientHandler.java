/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.battleships.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.BiConsumer;

public class ClientHandler extends Thread {

    private Socket socket;
    private PrintWriter out;
    private String playerRole;

    private BufferedReader in;
    private BiConsumer<String, String> serverCallback;

    public ClientHandler(Socket socket, String playerRole) {
        this.socket = socket;
        this.playerRole = playerRole;

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void setServer(BiConsumer<String, String> serverCallback) {
        this.serverCallback = serverCallback;
    }

    @Override
    public void run() {
        System.out.println(playerRole + " handler started.");

        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (serverCallback != null) {
                    serverCallback.accept(playerRole, message);
                }
            }
        } catch (IOException e) {
            System.out.println(playerRole + " disconnected.");
        }
    }
}
