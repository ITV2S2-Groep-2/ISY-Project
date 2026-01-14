package com.isy.util;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModelFileReader {

    public static JSONObject Read(String filename) {
        try (FileReader file = new FileReader(filename)) {
            JSONTokener t = new JSONTokener(file);
            return new JSONObject(t);
        } catch (IOException e) {
            return null;
        }
    }

}
