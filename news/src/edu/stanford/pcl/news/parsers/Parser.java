package edu.stanford.pcl.news.parsers;

import edu.stanford.pcl.news.dataHandlers.Article;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Rebecca
 * Date: 6/19/12
 * Time: 12:44 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Parser {
    public abstract Article parse(File file, String source);
}
