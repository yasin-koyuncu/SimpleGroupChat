package com.kth.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Client {
//hjbhv
	public static void main(String[] args) {

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            InetAddress server = InetAddress.getLocalHost();
            socket = new Socket(server, 50015);

            ClientMessageHandler receiver = new ClientMessageHandler(socket);
            receiver.start();

            out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scan = new Scanner(System.in);

            String message = "";
            while (true) {
                try {
                    message = scan.nextLine();
                } catch(NoSuchElementException e) {
                    return;
                }
                out.println(message);
                if (message.equals("/quit")) {
                    break;
                }
            }
            scan.close();
        }
        catch (UnknownHostException uhe) {
            System.out.println("UnknownHostException.");
        }
        catch (IOException ioe) {
            System.out.println("Socket error.");
        }
        finally {
            try {
                socket.close();
            } catch (NullPointerException e) {
                System.out.println("Server probably not alive.");
            }
            catch (IOException ioe) {
                System.out.println("Couldn't close socket.");
            }
        }
}
}
