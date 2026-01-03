package com.isy.util;

import com.isy.gui.components.SoundUtils;

public class GameSettings {
    private static GameSettings instance;
    private final String defaultHostName = "127.0.0.1";
    private final static int defaultPortNumber = 7789;
    private String hostName;
    private int portNumber;

    private float backgroundVolume;
    private float effectsVolume;
    private final int defaultBackgroundVolume = 25;
    private final int defaultEffectsVolume = 25;

    private boolean useReversiRules = false;

    public GameSettings(){
        this.hostName = defaultHostName;
        this.portNumber = defaultPortNumber;
        this.backgroundVolume = convertToDB(defaultBackgroundVolume);
        this.effectsVolume = convertToDB(defaultEffectsVolume);
    }

    public void setGameSettings(String hostName, int portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void setBackgroundVolume(int volume){
        this.backgroundVolume = convertToDB(volume);
        SoundUtils.updateBackgroundMusicVolume();
    }

    public void setEffectsVolume(int volume){
        this.effectsVolume = convertToDB(volume);
    }

    public float convertToDB(int value){
        return (float) (Math.log10(value / 100.0) * 20.0);
    }

    public int convertFromDB(float dB) {
        return Math.round((float)(100 * Math.pow(10, dB / 20.0)));
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

    public float getBackgroundVolume(){ return this.backgroundVolume; }

    public float getEffectsVolume(){ return this.effectsVolume; }

    public boolean getUseReversiRules() {
        return useReversiRules;
    }

    public void setUseReversiRules(boolean useReversiRules) {
        this.useReversiRules = useReversiRules;
    }
}
