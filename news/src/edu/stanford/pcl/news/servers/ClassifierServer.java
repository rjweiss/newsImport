package edu.stanford.pcl.news.servers;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.pcl.news.dataHandlers.Article;
import edu.stanford.pcl.news.dataHandlers.Updater;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ClassifierServer {
    private static ArrayList<String> articleList = new ArrayList<String>();


    // server constructor
    public ClassifierServer(int port) throws IOException {
        loadArticles();
        /* create socket server and wait for connection requests */
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("ClassifierServer waiting for client on port " + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();  // accept connection
                System.out.println("New client asked for a connection");
                TcpThread t = new TcpThread(socket);    // make a thread of it
                System.out.println("Starting a thread for a new ClassifierClient");
                t.start();
            }
        } catch (IOException e) {
            System.out.println("Exception on new ServerSocket: " + e);
        }
    }

    //	you must "run" server to have the server run as a console application
    public static void newServer(int port) throws IOException {
        // start server on port 1500
        new ClassifierServer(port);
    }

    /**
     * One instance of this thread will run for each client
     */
    class TcpThread extends Thread {
        // the socket where to listen/talk
        Socket socket;
        ObjectInputStream serverInput;
        ObjectOutputStream serverOutput;

        TcpThread(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            /* Creating both Data Stream */
            System.out.println("Thread trying to create Object Input/Output Streams");
            try {
                // create output first
                serverOutput = new ObjectOutputStream(socket.getOutputStream());
                serverOutput.flush();
                serverInput = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                System.out.println("Exception creating new Input/output Streams: " + e);
                return;
            }
            System.out.println("Thread waiting for a String from the ClassifierClient");

            String clientMessage = "";
            int i = 0;
            do {
                try {
                    clientMessage = (String) serverInput.readObject();
                    System.out.println("client>" + clientMessage);
                    sendMessage(getNext());
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                i++;
                System.out.println(i);
            } while (getLength() > 0);
            sendMessage("terminate");

            try {
                serverOutput.close();
                serverInput.close();
            } catch (Exception e) {
            }
        }

        void sendMessage(String msg) {
            try {
                serverOutput.writeObject(msg);
                serverOutput.flush();
                System.out.println("server>" + msg);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public synchronized void loadArticles() throws IOException {
        BasicDBObject query = new BasicDBObject();

        Updater updater = new Updater();
        updater.connect();
        DBCursor cursor = updater.queryCursor("articles", query);
        Article article;
        System.out.println("loading list of articles to classify");
        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);
            articleList.add(article.getFileName());
        }
        updater.close();
    }

    public synchronized String getNext() {
        int current = articleList.size() - 1;
        String item = articleList.get(current);
        articleList.remove(current);
        return item;
    }

    public synchronized Integer getLength() {
        int size = articleList.size();
        return size;
    }
}

