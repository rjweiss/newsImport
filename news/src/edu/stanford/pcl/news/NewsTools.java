package edu.stanford.pcl.news;

import com.martiansoftware.jsap.*;
import edu.stanford.pcl.news.classifiers.ClassifierClient;
import edu.stanford.pcl.news.classifiers.NewsClassifier;
import edu.stanford.pcl.news.dataHandlers.Importer;
import edu.stanford.pcl.news.dataHandlers.Processor;
import edu.stanford.pcl.news.dataHandlers.Sampler;
import edu.stanford.pcl.news.queriers.LuceneQuerier;
import edu.stanford.pcl.news.scrapers.*;
import edu.stanford.pcl.news.servers.ClassifierServer;

public class NewsTools {
    public static void main(String[] args) throws Exception {
        SimpleJSAP jsap = new SimpleJSAP(
                "LuceneQuerier",
                "Pulls information from Lucene",
                new Parameter[]{
                        new FlaggedOption("actions", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'a', "actions",
                                "Action to perform"),
                        new FlaggedOption("scraper", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'g', "scraper",
                                "Scraper to run"),
                        new FlaggedOption("importPath", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'i', "importPath",
                                "path to import"),
                        new FlaggedOption("queryListFile", JSAP.STRING_PARSER, "/home/ec2-user/queries/conflictQuery.txt", JSAP.NOT_REQUIRED, 'q', "queryListFile",
                                "List of queries to run"),
                        new FlaggedOption("mediaSource", JSAP.STRING_PARSER, "", JSAP.NOT_REQUIRED, 'd', "mediaSource",
                                "Media Source Name"),
                        new FlaggedOption("querySources", JSAP.STRING_PARSER, "all", JSAP.NOT_REQUIRED, 's', "querySources",
                                "Sources to query (source name, all, or aggregate)"),
                        new FlaggedOption("outputFile", JSAP.STRING_PARSER, "", JSAP.NOT_REQUIRED, 'o', "outputFile",
                                "Path and name for output"),
                        new FlaggedOption("startDate", JSAP.STRING_PARSER, "20000101", JSAP.NOT_REQUIRED, 'b', "startDate",
                                "Start date (yyyyMMdd)"),
                        new FlaggedOption("endDate", JSAP.STRING_PARSER, "20070531", JSAP.NOT_REQUIRED, 'f', "endDate",
                                "End date (yyyyMMdd)"),
                        new FlaggedOption("outputFilePath", JSAP.STRING_PARSER, "/home/ec2-user/occurrence/", JSAP.NOT_REQUIRED, 'p', "outFilePath",
                                "Out file path (occurrence only)"),
                        new FlaggedOption("sourceList", JSAP.STRING_PARSER, "/home/ec2-user/sourceList.txt", JSAP.NOT_REQUIRED, 'l', "sourceList",
                                "Source List File Location"),
                        new FlaggedOption("type", JSAP.STRING_PARSER, "occurrenceList", JSAP.NOT_REQUIRED, 't', "type",
                                "Type of data output (queryCounts, dateRangeCounts, occurrenceList)").setList(true).setListSeparator(',')
                }
        );

        JSAPResult JSAPconfig = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);

        if (JSAPconfig.getString("actions").equals("import")) {
            if (JSAPconfig.getString("mediaSource").equals("NewYorkTimes")) {
                Importer.importNews(JSAPconfig.getString("importPath"), "New York Times", "english", "US", "NytParser");
            } else if (JSAPconfig.getString("mediaSource").equals("BaltimoreSun")) {
                Importer.importNews(JSAPconfig.getString("importPath"), "Baltimore Sun", "english", "US", "TribParser");
            } else if (JSAPconfig.getString("mediaSource").equals("ChicagoTribune")) {
                Importer.importNews(JSAPconfig.getString("importPath"), "Chicago Tribune", "english", "US", "TribParser");
            } else if (JSAPconfig.getString("mediaSource").equals("LosAngelesTimes")) {
                Importer.importNews(JSAPconfig.getString("importPath"), "Los Angeles Times", "english", "US", "TribParser");
            }


        } else if (JSAPconfig.getString("actions").equals("scrape")) {
            if (JSAPconfig.getString("scraper").equals("DerSpiegel")) {
                DerSpiegelScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Humanite")) {
                //HumaniteScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Liberation")) {
                LiberationScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("TimesIndia")) {
                TimesIndiaScraper.scrapeNews("2001-07-25", "2012-07-08", 37097);
            } else if (JSAPconfig.getString("scraper").equals("Welt")) {
                WeltScraper.scrapeNews();
            } else if (JSAPconfig.getString("scraper").equals("Zeit")) {
                ZeitScraper.scrapeNews();
            }
        } else if (JSAPconfig.getString("actions").equals("process")) {
            Processor.processNews(JSAPconfig.getString("mediaSource"), JSAPconfig.getString("startDate"), JSAPconfig.getString("endDate"));
        } else if (JSAPconfig.getString("actions").equals("train")) {
            //NewsClassifier.classifyNews();
        } else if (JSAPconfig.getString("actions").equals("query")) {
            LuceneQuerier.queryNews(JSAPconfig);
        } else if (JSAPconfig.getString("actions").equals("sample")) {
            Sampler sampler = new Sampler();
            sampler.sample(1500, 3202924);

        } else if (JSAPconfig.getString("actions").equals("server")) {
            ClassifierServer classifierServer = new ClassifierServer(1500);
        } else if (JSAPconfig.getString("actions").equals("classifierWorker")) {
            //   ClassifierClient classifierClient = new ClassifierClient("localhost", 1500, "");
        }

    }
}
