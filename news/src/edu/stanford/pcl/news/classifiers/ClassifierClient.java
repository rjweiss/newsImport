package edu.stanford.pcl.news.classifiers;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import edu.stanford.pcl.news.dataHandlers.Article;
import edu.stanford.pcl.news.dataHandlers.Updater;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClassifierClient {

    ObjectInputStream serverInput;
    ObjectOutputStream serverOutput;
    Socket socket;
    private NewsClassifier newsClassifier;

    // Constructor connection receiving a socket number
    public ClassifierClient(String ip, int port, String label, String classifierPath) throws IOException {
        // we use "localhost" as host name, the server is on the same machine
        // but you can put the "real" server name or IP address

        List<String> featureAttributes = new ArrayList<String>();
        featureAttributes.add("annotation.tokens.lemma.pos");
        String labelAttribute = "";
        NewsClassifier newsClassifier = new NewsClassifier(featureAttributes, labelAttribute);
        newsClassifier.loadClassifier(classifierPath);

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

        Updater updater = new Updater();
        updater.connect();

        String state = "ready";
        sendMessage(state);

        String serverResponse = "";
        do {
            try {

                serverResponse = (String) serverInput.readObject();
                if (!serverResponse.equals("terminate")) {
                    System.out.println("server>" + serverResponse);
                    sendMessage(classify(serverResponse, updater));
                }
            } catch (ClassNotFoundException classNot) {
                System.err.println("data received in unknown format");
            } catch (EOFException e) {

            }
        } while (!serverResponse.equals("terminate"));

        try {
            serverInput.close();
            serverOutput.close();
            updater.close();
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

    private String classify(String fileName, Updater updater) {
        BasicDBObject query = new BasicDBObject();
        query.put("fileName", fileName);

        Article article;
        DBCursor cursor = updater.queryCursor("articles", query);

        while (cursor.hasNext()) {
            cursor.next();
            DBObject obj = cursor.curr();
            article = Article.fromMongoObject(obj);
            article = newsClassifier.classify(article);
            updater.updateMongo(article, "articles");
            System.out.println(article.getFileName());
        }

        cursor.close();
        return "next";
    }
}