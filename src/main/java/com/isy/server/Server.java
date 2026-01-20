package com.isy.server;

import com.isy.server.await.Promise;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static com.isy.server.ServerUtils.await;

public class Server {
    private static Server instance;
    private final Socket client;
    private boolean isRunning;
    private ServerResponse last;
    private ServerResponse current;

    private Server(String hostName, int portNumber) {
        this.isRunning = true;
        this.current = new ServerResponse("");
        this.last = this.current;
        this.current.setHandled();

        try {
            client = new Socket(hostName, portNumber);
            Promise.bindServer(client);

            new Thread(this::handleServerMessage).start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleServerMessage() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String line;

            while (isRunning && (line = input.readLine()) != null){
                last.setNext(new ServerResponse(line));
                last = last.getNext();

                //sets current to be the last valid message, makes it so the jvm removes any unused messages
                while (!current.isStillValid() && current.getNext() != null){
                    current = current.getNext();
                }
            }
        } catch (IOException e) {}
    }

    public static Server resetServer(String host, int port){
        if (instance != null)
            instance.shutdown();

        instance = new Server(host, port);

        return instance;
    }

    public static Server getInstance(){
        return instance;
    }

    /**
     * Deze methode verbreekt de verbinding met de server
     */
    public void shutdown() {
        addEmptyMessage();
        this.isRunning = false;

        await(new Promise().setCommand("logout"));
        Promise.bindServer(null);

        try {
            client.close();
        } catch (IOException e) {
        }
    }

    public void addEmptyMessage(){
        last.setNext(new ServerResponse(null));
    }

    public void addFakeMessage(String msg){
        last.setNext(new ServerResponse(msg));
    }

    public ServerResponse getCurrent(){
        return this.current;
    }
}
