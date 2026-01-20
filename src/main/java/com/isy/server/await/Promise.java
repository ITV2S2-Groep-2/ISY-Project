package com.isy.server.await;

import com.isy.server.Server;
import com.isy.server.ServerResponse;
import com.isy.server.ServerUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class Promise{
    private String accept;
    private String command = null;
    private Supplier<Boolean> secondaryBreakStatement = () -> false;
    private static PrintWriter out;
    private static Socket socket = null;

    public Promise(){
        this(null);
    }

    public Promise(String accept){
        this.accept = accept;
    }

    public Promise(String accept, Supplier<Boolean> secondaryBreakStatement){
        this.accept = accept;
        this.secondaryBreakStatement = secondaryBreakStatement;
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
            } catch (Exception e) {
                throw new RuntimeException("can not get input or output stream from socket", e);
            }
        } else {
            Promise.out = null;
        }
    }

    public String getData(){
        if (socket == null) return "err socket closed";

        if (command != null)
            out.println(this.command);

        ServerResponse current = Server.getInstance().getCurrent();

        if (accept == null)
            return null;

        try {
            while (current.getMessage() != null) {
                if (this.secondaryBreakStatement.get()) return null;

                if (current.getMessage().matches(accept) && current.isStillValid()){
                    break;
                }

                System.out.println(current.getMessage());

                while (current.getNext() == null){
                    if (this.secondaryBreakStatement.get()) return null;
                    Thread.sleep(ServerUtils.waitTime);
                }

                current = current.getNext();
            }
        }catch (InterruptedException e){
            return "err connection closed";
        }

        if (current.getMessage() == null){
            return "err connection closed";
        }

        current.setHandled();
        return current.getMessage();
    }
}
