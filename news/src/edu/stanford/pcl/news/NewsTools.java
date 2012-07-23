package edu.stanford.pcl.news;

import com.martiansoftware.jsap.*;
import edu.stanford.pcl.news.classifiers.StanfordClassifier;
import edu.stanford.pcl.news.dataHandlers.Importer;
import edu.stanford.pcl.news.dataHandlers.Processor;
import edu.stanford.pcl.news.queriers.LuceneQuerier;
import edu.stanford.pcl.news.scrapers.*;

public class NewsTools {
    public static void main(String[] args) throws Exception {
        SimpleJSAP jsap = new SimpleJSAP(
                "LuceneQuerier",
                "Pulls information from Lucene",
                new Parameter[]{
                        new FlaggedOption("action", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'a', "action",
                                "Action to perform"),
                        new FlaggedOption("scraper", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'g', "scraper",
                                "Scraper to run"),
                        new FlaggedOption("queryListFile", JSAP.STRING_PARSER, "", JSAP.NOT_REQUIRED, 'q', "queryListFile",
                                "List of queries to run"),
                        new FlaggedOption("querySources", JSAP.STRING_PARSER, "", JSAP.NOT_REQUIRED, 's', "querySources",
                                "Sources to query (source name, all, or aggregate)"),
                        new FlaggedOption("outputFile", JSAP.STRING_PARSER, "", JSAP.NOT_REQUIRED, 'o', "outputFile",
                                "Path and name for output"),
                        new FlaggedOption("startDate", JSAP.STRING_PARSER, "20020101", JSAP.NOT_REQUIRED, 'b', "startDate",
                                "Start date (yyyyMMdd)"),
                        new FlaggedOption("endDate", JSAP.STRING_PARSER, "20040101", JSAP.NOT_REQUIRED, 'f', "endDate",
                                "End date (yyyyMMdd)"),
                        new FlaggedOption("outputFilePath", JSAP.STRING_PARSER, "/home/ec2-user/occurrence/", JSAP.NOT_REQUIRED, 'p', "outFilePath",
                                "Out file path (occurrence only)"),
                        new FlaggedOption("sourceList", JSAP.STRING_PARSER, "/home/ec2-user/sourceList.txt", JSAP.NOT_REQUIRED, 'l', "sourceList",
                                "Source List File Location"),
                        new FlaggedOption("type", JSAP.STRING_PARSER, "count", JSAP.NOT_REQUIRED, 't', "type",
                                "Type of data output (queryCounts, dateRangeCounts, occurrenceList)").setList(true).setListSeparator(',')
                }
        );

        JSAPResult JSAPconfig = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);


        if (JSAPconfig.getString("actions").equals("import")) {
            Importer.importNews();
        } else if (JSAPconfig.getString("actions").equals("scrape")) {
            if (JSAPconfig.getString("scraper").equals("DerSpiegel")) {
                DerSpiegelScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Humanite")) {
                //HumaniteScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Liberation")) {
                LiberationScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("TimesIndia")) {
                TimesIndiaScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Welt")) {
                WeltScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Zeit")) {
                ZeitScraper.scrapeNews();
            }
        } else if (JSAPconfig.getString("actions").equals("process")) {
            Processor.processNews();
        } else if (JSAPconfig.getString("actions").equals("classify")) {
            StanfordClassifier.classifyNews();
        } else if (JSAPconfig.getString("actions").equals("query")) {
            LuceneQuerier.queryNews(JSAPconfig);
        }


    }
}
