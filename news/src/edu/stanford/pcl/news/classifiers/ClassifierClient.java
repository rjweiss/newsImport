package edu.stanford.pcl.news.classifiers;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClassifierClient {

    ObjectInputStream serverInput;        // to read the socker
    ObjectOutputStream serverOutput;    // towrite on the socket
    Socket socket;

    // Constructor connection receiving a socket number
    public ClassifierClient(String ip, int port) throws IOException {
        // we use "localhost" as host name, the server is on the same machine
        // but you can put the "real" server name or IP address
        try {
            socket = new Socket(ip, port);
        } catch (Exception e) {
            System.out.println("Error connectiong to server:" + e);
            return;
        }
        System.out.println("Connection accepted " +
                socket.getInetAddress() + ":" +
                socket.getPort());

        /* Creating both Data Stream */
        try {
            serverInput = new ObjectInputStream(socket.getInputStream());
            serverOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Exception creating new Input/output Streams: " + e);
            return;
        }

        // Updater updater = new Updater();
        // updater.connect();


        String state = "ready";

        sendMessage(state);

        String serverResponse = "";
        do {
            try {

                serverResponse = (String) serverInput.readObject();
                if (!serverResponse.equals("terminate")) {
                    System.out.println("server>" + serverResponse);
                    sendMessage(classify((serverResponse)));
                }
                // do something
            } catch (ClassNotFoundException classNot) {
                System.err.println("data received in unknown format");
            } catch (EOFException e) {

            }
        } while (!serverResponse.equals("terminate"));

        try {
            serverInput.close();
            serverOutput.close();
        } catch (Exception e) {
        }
    }

    private void sendMessage(String msg) {
        try {
            serverOutput.writeObject(msg);
            serverOutput.flush();
            System.out.println("client>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String classify(String fileName) {
        /* BasicDBObject query = new BasicDBObject();
                query.put("fileName", fileName);

                DBCursor cursor = updater.queryCursor("articles", query);

        */


        return "next";
    }

}

