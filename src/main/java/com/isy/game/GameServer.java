package com.isy.game;

import com.isy.await.Promise;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.isy.await.Await.await;

public class GameServer {
    private Socket client;

    public GameServer(String hostName, int portNumber) {
        try {
            client = new Socket(hostName, portNumber);
            Promise.bindServer(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
