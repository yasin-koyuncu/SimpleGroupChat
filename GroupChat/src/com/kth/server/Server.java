package com.kth.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	   public static void main(String[] args) {

	        ServerSocket serverSocket = null;
	        ArrayList<ConnectClient> clients = new ArrayList<ConnectClient>();
	        int counter = 1;
	        try {
	            serverSocket = new ServerSocket(50015);
	            PrintWriter out;
	            System.out.println("Server: Waiting for connections..");
	            while (true) {
	                Socket clientSocket = serverSocket.accept();
	                ConnectClient client = new ConnectClient("anonymous" + counter++, clientSocket);

	                out = new PrintWriter(clientSocket.getOutputStream(), true);
	                out.println("Welcome to Peonsson's and roppe546's SimpleGroupChat!");
	                synchronized (clients) {
	                    clients.add(client);
	                }
	                ClientHandler cHandler = new ClientHandler(client, clients);
	                cHandler.start();
	            }
	        }
	        catch (IOException ioe) {
	            System.out.println("Server: Couldn't create server socket.");
	        }
	        finally {
	            try {
	                serverSocket.close();
	            }
	            catch (IOException ioe) {
	                System.out.println("Server: Couldn't close server socket.");
	            }
	        }
	}
}
