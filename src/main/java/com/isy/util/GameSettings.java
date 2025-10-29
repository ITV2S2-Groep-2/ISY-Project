package com.isy.util;

public class GameSettings {
    private static GameSettings instance;
    private final String defaultHostName = "127.0.0.1";
    private final static int defaultPortNumber = 7789;
    private String hostName;
    private int portNumber;
    public GameSettings(){
        this.hostName = defaultHostName;
        this.portNumber = defaultPortNumber;
    }

    public void setGameSettings(String hostName, int portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public static GameSettings get(){
        if(instance == null){
            instance = new GameSettings();
            return instance;
        }
        return instance;
    }

    public String getHostName() {
        return this.hostName;
    }

    public int getPortNumber(){
        return this.portNumber;
    }
}
