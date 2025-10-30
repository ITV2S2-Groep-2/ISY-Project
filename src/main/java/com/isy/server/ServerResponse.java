package com.isy.server;

public class ServerResponse {
    private static final int invalidationTime = 1000; //1 second after a message is created it will be invalid

    private final String message;
    private final long creationTime;
    private ServerResponse next;
    private boolean isHandled;

    public ServerResponse(String message){
        this.message = message;
        this.creationTime = System.currentTimeMillis();
    }

    public boolean isHandled(){
        return this.isHandled;
    }

    public boolean isStillValid(){
        return !isHandled() && (this.creationTime > System.currentTimeMillis() - invalidationTime);
    }

    public String getMessage(){
        return this.message;
    }

    public ServerResponse getNext(){
        return this.next;
    }

    public void setNext(ServerResponse serverResponse){
        this.next = serverResponse;
    }

    public void setHandled(){
        this.isHandled = true;
    }
}
