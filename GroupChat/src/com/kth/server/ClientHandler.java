package com.kth.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 
 * @author yasin93
 *
 */
public class ClientHandler extends Thread {
	private ConnectClient client = null;
    private ArrayList<ConnectClient> connectedClients = null;

    public ClientHandler(ConnectClient client, ArrayList<ConnectClient> connectedClients) {
        this.client = client;
        this.connectedClients = connectedClients;
    }

    public void run() {
        try {

            String message = "";
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
            while (true) {
                message = in.readLine();
                if(message == null) {
                    broadcast(client.getNickname() + " has left the chat.");
                    for (int i = 0; i < connectedClients.size(); i++) {
                        if (connectedClients.get(i).getNickname().equals(client.getNickname()))
                            synchronized (connectedClients) {
                                connectedClients.remove(i);
                            }
                    }
                }
                if (!checkForCommands(message)) {
                    broadcast(client.getNickname() + ": " + message);
                }
            }
        } catch (NullPointerException e) {
            return;
        }
        catch (IOException ioe) {
            return;
        }
    }

    private boolean checkForCommands(String message) {
        try {
            PrintWriter out = new PrintWriter(client.getClientSocket().getOutputStream(), true);
            switch (message) {
                case "/quit":
                    int clientToRemove = connectedClients.indexOf(client);
                    client.getClientSocket().close();
                    synchronized (connectedClients) {
                        connectedClients.remove(clientToRemove);
                    }
                    broadcast(client.getNickname() + " has left chat.");
                    return true;
                case "/who":
                    String whoMsg = "";
                    for (int i = 0; i < connectedClients.size(); i++) {
                        whoMsg += connectedClients.get(i).getNickname() + "\n";
                    }
                    out.println(whoMsg);
                    return true;
                case "/help":
                    out.println("You can use the following commands:\n/who - list all clients online\n/nick <NICKNAME> - change nickname\n/quit - quit the chat");
                    return true;
                default:
                    if (message.startsWith("/nick")) {
                        String[] myStrings = message.split(" ");
                        for (int i = 0; i < connectedClients.size(); i++) {
                            if (myStrings[1].equals(connectedClients.get(i).getNickname())) {
                                out.println("Nickname already in use please choose another one!");
                                return true;
                            }
                        }
                        client.setNickname(myStrings[1]);
                        return true;
                    }
                    else if (message.startsWith("/")) {
                        out.println("Unknown command");
                        return true;
                    }
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return false;
    }

    private void broadcast(String message) {

        PrintWriter out;
        for (int i = 0; i < connectedClients.size(); i++) {

            // Do not send clients own message to itself
            if (connectedClients.get(i).equals(client))
                continue;

            try {
                out = new PrintWriter(connectedClients.get(i).getClientSocket().getOutputStream(), true);
                out.println(message);
                System.out.println("Sending: " + message + " from client: " + client.getClientSocket().getInetAddress() + ":" + client.getClientSocket().getPort() + " to client " + connectedClients.get(i).getClientSocket().getInetAddress() + ":" + connectedClients.get(i).getClientSocket().getPort());
            }
            catch (IOException e) {
                System.out.println("ClientHandler: Couldn't send to all clients registered.");

                for (int j = 0; j < connectedClients.size(); j++) {
                    if (connectedClients.get(j).getClientSocket().isClosed()) {
                        synchronized (connectedClients) {
                            connectedClients.remove(j);
                        }
                        i--;
                    }
                }
                System.out.println(connectedClients.toString());
            }
        }
}
}
