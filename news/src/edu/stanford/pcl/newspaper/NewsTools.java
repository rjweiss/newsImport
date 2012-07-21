package edu.stanford.pcl.newspaper;

import com.martiansoftware.jsap.*;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 7/20/12
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewsTools {
    public static void main(String[] args) throws IOException, org.apache.lucene.queryParser.ParseException, JSAPException, java.text.ParseException {
        SimpleJSAP jsap = new SimpleJSAP(
                "QueryLucene",
                "Pulls information from Lucene",
                new Parameter[]{
                        new FlaggedOption("queryListFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'q', "queryListFile",
                                "List of queries to run"),
                        new FlaggedOption("querySources", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 's', "querySources",
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
                        new FlaggedOption("type", JSAP.STRING_PARSER, "count", JSAP.REQUIRED, 't', "type",
                                "Type of data output (queryCounts, dateRangeCounts, occurrenceList)").setList(true).setListSeparator(',')
                }
        );

        JSAPResult JSAPconfig = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);


        if()




    }
}
