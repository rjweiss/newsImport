package edu.stanford.pcl.news;

import edu.stanford.pcl.news.classifiers.ClassifierClient;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 7/31/12
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerTester {

    public static void main(String[] arg) throws IOException {
        // start server on port 1500
        //ClassifierServer classifierServer = new ClassifierServer(1500);

        ClassifierClient classifierClient = new ClassifierClient("localhost", 1500);

    }
}
