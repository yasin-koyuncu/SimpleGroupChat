package com.kth.server;

import java.net.Socket;

public class ConnectClient extends Thread {

	  private String nickname;
	    private Socket clientSocket;

	    public ConnectClient(String nickname, Socket clientSocket) {
	        this.nickname = nickname;
	        this.clientSocket = clientSocket;
	    }

	    public String getNickname() {
	        return nickname;
	    }

	    public void setNickname(String nickname) {
	        this.nickname = nickname;
	    }

	    public Socket getClientSocket() {
	        return clientSocket;
	}
}
