package com.isy.gui.lang;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class LangFile {
    private final HashMap<String, String> lang;
    private final String name;
    private final String path;

    public LangFile(String langName, String path){
        this.lang = new HashMap<>();
        this.name = langName;
        this.path = path;

        this.parseFile();
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    private String readFromInputStreamSafe(InputStream inputStream){
        try {
            return readFromInputStream(inputStream);
        } catch (IOException e){
            return "";
        }
    }

    private void parseFile(){
        String file = readFromInputStreamSafe(LangFile.class.getResourceAsStream(this.path));
        String[] lines = file.split("\\n");

        for (String line : lines) {
            String[] splits = line.split("=");
            this.lang.put(splits[0], splits[1]);
        }
    }

    public String translate(String key, @Nullable Object... args){
        if (!this.lang.containsKey(key)) return key;

        return String.format(this.lang.get(key), args);
    }
}
