package com.isy.server;

import com.isy.server.await.Promise;

import java.io.IOException;
import java.net.Socket;

import static com.isy.server.ServerUtils.await;

public class Server {
    private static Server instance;
    private Socket client;

    private Server(String hostName, int portNumber) {
        try {
            client = new Socket(hostName, portNumber);
            Promise.bindServer(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public void shutdown()
    {
        await(new Promise().setCommand("logout"));
        Promise.bindServer(null);

        try {
            client.close();
        } catch (IOException e) {
        }
    }
}
