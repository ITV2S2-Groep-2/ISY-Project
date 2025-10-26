package com.isy.await;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class Promise{
    private Pattern accept;
    private String command = null;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Socket socket = null;

    public Promise(){
        this((Pattern) null);
    }

    public Promise(String pattern){
        this(pattern, true);
    }

    public Promise(String pattern, boolean checkCase){
        this(Pattern.compile(pattern, checkCase ? CASE_INSENSITIVE : 0));
    }

    public Promise(Pattern accept){
        this.accept = accept;
    }

    public Promise setCommand(String command) {
        this.command = command;
        return this;
    }

    public static void bindServer(Socket socket){
        Promise.socket = socket;
        if (socket != null) {
            try {
                Promise.out = new PrintWriter(socket.getOutputStream(), true);
                Promise.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (Exception e) {
                throw new RuntimeException("can not get input or output stream from socket", e);
            }
        } else {
            Promise.in = null;
            Promise.out = null;
        }
    }

    public String getData() throws IOException {
        if (socket == null) return "";


        if (command != null)
            out.println(this.command);

        String line;
        Matcher matcher = null;

        if (accept == null)
            return null;

        while ((line = in.readLine()) != null) {
            matcher = accept.matcher(line);

            if (matcher.matches()){
                break;
            }
        }

        if (line == null)
            return "err";


        return matcher.group();
    }
}
