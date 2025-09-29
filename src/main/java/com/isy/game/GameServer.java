package com.isy.game;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GameServer implements Runnable {

    private String hostName = "127.0.0.1";
    private int portNumber = 7789;

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private boolean done;

    boolean placed = false;

    private volatile boolean running = true;

    private List<Consumer<String>> listeners = new ArrayList<>();

    private List<Integer> gohitthese;

    public GameServer(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    @Override
    public void run()
    {
        System.out.print("starting game");

        try {
            client = new Socket(hostName, portNumber);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            String inputMessage; // we maken een input message aan

            // Start een aparte thread om server-berichten te lezen
            new Thread(() -> {
                try {
                    String line;
                    while (running && (line = in.readLine()) != null) {
                        System.out.println("[SERVER] " + line);
                        notifyListeners(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            //TODO handle
        }

        // Thread om console-input te lezen
        new Thread(() -> {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            String line;
            try {
                while (running && (line = consoleReader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    sendCommand(line);  // stuurt naar server
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    private void notifyListeners(String message) {
        for (Consumer<String> l : listeners) {
            l.accept(message);
        }
    }

    public void sendCommand(String cmd){
        if(out != null){
            out.println(cmd);
        }
    }

    public String readServerLine(){
        try{
            if(in != null){
                return in.readLine();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deze methode verbreekt de verbinding met de server
     */
    public void shutdown()
    {
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            //we negeren een exception omdat we toch de verbinding verbreken
        }
    }

    /**
     * Deze methode logt in met de opgegeven gebruikersnaam parameter
     * @param name
     */
    public void login(String name)
    {
        System.out.println("Logging in as " + name);
        out.println("login " + name);
    }

    public static void main(String[] args) {
    }

}
