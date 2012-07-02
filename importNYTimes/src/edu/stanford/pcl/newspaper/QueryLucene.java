package edu.stanford.pcl.newspaper;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.martiansoftware.jsap.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class QueryLucene {
    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
    LinkedHashMap<String, ArrayList> results = new LinkedHashMap<String, ArrayList>();

    public void saveFile(String outputFile) throws IOException {
        Iterator<Entry<String, ArrayList>> iterator = results.entrySet().iterator();
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), '\t');

        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            ArrayList<String> row = (ArrayList) entry.getValue();
            String[] fullRow = row.toArray(new String[row.size()]);
            writer.writeNext(fullRow);
        }
        writer.close();
    }

    public String executeCountQuery(String source, String terms, Integer startDate, Integer endDate) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
        IndexReader reader = IndexReader.open(index);



        Query sourceQuery = new TermQuery(new Term("mediaSource", source));
        QueryParser queryParser = new QueryParser(Version.LUCENE_36, "text", analyzer);
        Query textQuery = queryParser.parse(terms);
        Query dateRangeQuery = NumericRangeQuery.newIntRange("publicationDate", startDate, endDate, true, true);

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(sourceQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(textQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(dateRangeQuery, BooleanClause.Occur.MUST);

        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(booleanQuery, 1);
        String hitCount = String.valueOf(topDocs.totalHits);
        searcher.close();
        reader.close();
        analyzer.close();
        return hitCount;
    }

    public static DateTime convertIntDateToDate(String date){
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        String year = date.substring(0,4);
        String month = date.substring(4,6);
        String day = date.substring(6,8);
        String fullDate = year + "-" + month + "-" +day;
        DateTime formattedDate = dateFormat.parseDateTime(fullDate);
        return formattedDate;
    }

    public static ArrayList<String> createDateRangeHeader(Integer startDate, Integer endDate) {
        DateTime dtStartDate = convertIntDateToDate(startDate.toString());
        DateTime dtEndDate = convertIntDateToDate(endDate.toString());

        ArrayList<String> resultHeader = new ArrayList<String>();
        resultHeader.add(null);
        for (DateTime date = dtStartDate; date.isBefore(dtEndDate); date = date.plusDays(1)) {
            resultHeader.add(date.toString("yyyy-MM-dd"));
        }
        return resultHeader;
    }

    public static void main(String[] args) throws IOException, ParseException, JSAPException, java.text.ParseException {
        SimpleJSAP jsap = new SimpleJSAP(
                "QueryLucene",
                "Pulls information from Lucene",
                new Parameter[]{
                        new FlaggedOption("queryListFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'q', "queryListFile",
                                "List of queries to run"),
                        new FlaggedOption("querySources", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 's', "querySources",
                                "Sources to query (source name, all, or aggregate)"),
                        new FlaggedOption("outputFile", JSAP.STRING_PARSER, "", JSAP.REQUIRED, 'o', "outputFile",
                                "Path and name for output"),
                        new FlaggedOption("startDate", JSAP.STRING_PARSER, "20000101", JSAP.NOT_REQUIRED, 'b', "startDate",
                                "Start date"),
                        new FlaggedOption("endDate", JSAP.STRING_PARSER, "20070531", JSAP.NOT_REQUIRED, 'f', "endDate",
                                "End date"),
                        new FlaggedOption("type", JSAP.STRING_PARSER, "count", JSAP.REQUIRED, 't', "type",
                                "Type of data output (queryCounts, dateRangeCounts, occurrenceList)").setList(true).setListSeparator(',')
                }
        );

        JSAPResult JSAPconfig = jsap.parse(args);
        if (jsap.messagePrinted()) System.exit(1);
        String type = JSAPconfig.getString("source");
        Integer startDate = Integer.parseInt(JSAPconfig.getString("startDate"));
        Integer endDate = Integer.parseInt(JSAPconfig.getString("endDate"));

        QueryLucene ql = new QueryLucene();
        CSVReader CSVReader = new CSVReader(new FileReader(JSAPconfig.getString("queryListFile")), '\t');
        List<String[]> queries = CSVReader.readAll();

        if ("queryCounts".equals(JSAPconfig.getString("type"))) {
            generateQueryCounts(startDate, endDate, JSAPconfig.getString("querySources"), ql, queries);
        } else if ("dateRangeCounts".equals(JSAPconfig.getString("type"))) {
            generateDateRangeCounts(startDate, endDate, JSAPconfig.getString("querySources"), ql, queries);
        } else if ("occurrenceList".equals(JSAPconfig.getString("type"))) {
            //know this is wrong, but whatevs
            for (String[] row : queries) {
                generateOccurenceList(startDate, endDate, JSAPconfig.getString("querySources"), row[0], ql);
            }
        } else {
            System.exit(1);
        }
        ql.saveFile(JSAPconfig.getString("outputFile"));
    }

    public static void generateOccurenceList(Integer startDate, Integer endDate, String querySources, String terms, QueryLucene ql) throws IOException, ParseException {


        if (querySources.equals("all")) {
            System.out.println("Not supported");
            System.exit(1);
        } else if (querySources.equals("aggregate")) {
            executeOccurenceQuery(ql, "\"New York Times\" OR \"Los Angeles Times\" OR \"Baltimore Sun\" OR \"Chicago Tribune\"", terms, startDate, endDate);
        } else {
            executeOccurenceQuery(ql, querySources, terms, startDate, endDate);
        }
    }

    public static void executeOccurenceQuery(QueryLucene ql, String source, String terms, Integer startDate, Integer endDate) throws IOException, ParseException {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
        Directory index = new SimpleFSDirectory(new File(LUCENE_INDEX_DIRECTORY));
        IndexReader reader = IndexReader.open(index);



        Query sourceQuery = new TermQuery(new Term("mediaSource", source));
        QueryParser queryParser = new QueryParser(Version.LUCENE_36, "text", analyzer);
        Query textQuery = queryParser.parse(terms);
        Query dateRangeQuery = NumericRangeQuery.newIntRange("publicationDate", startDate, endDate, true, true);

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(sourceQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(textQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(dateRangeQuery, BooleanClause.Occur.MUST);

        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs topDocs = searcher.search(booleanQuery, 1);
        Sort sort = new Sort(new SortField("publicationDate", SortField.INT));
        System.out.println(topDocs.totalHits);
        TopDocs hits = searcher.search(booleanQuery, topDocs.totalHits, sort);

        int i = 0;
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            ArrayList<String> resultRow = new ArrayList<String>();
            Document doc = searcher.doc(scoreDoc.doc);
            resultRow.add(String.valueOf(i));
            resultRow.add(doc.get("publicationDate"));
            resultRow.add(doc.get("mediaSource"));
            resultRow.add(doc.get("filename"));
            resultRow.add(doc.get("headline"));

            ql.results.put(String.valueOf(i), resultRow);
            i++;
        }
        searcher.close();
        reader.close();
        analyzer.close();
    }

    public static String cleanLabel(String label) {
        label = label.replace(" ", "");
        label = label.replace("+", "");
        return label;
    }

    public static void generateQueryCounts(Integer startDate, Integer endDate, String querySources, QueryLucene ql, List<String[]> queries) throws IOException, ParseException {
        Boolean isHeader = true;
        String source;
        for (String[] row : queries) {
            if (isHeader) {
                ArrayList<String> resultHeader = new ArrayList<String>();
                resultHeader.add(null);
                for (String column : row) {
                    if ("all".equals(querySources)) {
                        source = "NYT";
                        resultHeader.add(cleanLabel(column + "." + source));
                        source = "LAT";
                        resultHeader.add(cleanLabel(column + "." + source));
                        source = "BS";
                        resultHeader.add(cleanLabel(column + "." + source));
                        source = "CT";
                        resultHeader.add(cleanLabel(column + "." + source));
                        resultHeader.add(cleanLabel(column + ".total"));
                    } else if ("aggregate".equals(querySources)) {
                        source = "all";
                        resultHeader.add(cleanLabel(column + "." + source));
                    } else {
                        resultHeader.add(cleanLabel(column + "." + querySources));
                    }

                }
                ql.results.put("0", resultHeader);
                isHeader = false;
            } else {
                ArrayList<String> resultRow = new ArrayList<String>();

                String rowName = cleanLabel(row[0]);
                resultRow.add(rowName);

                //System.out.println(querySources);
                for (String column : row) {

                    if ("all".equals(querySources)) {
                        source = "New York Times";
                        String NYTResult = ql.executeCountQuery(source, column, startDate, endDate);
                        resultRow.add(NYTResult);
                        source = "Los Angeles Times";
                        String LATResult = ql.executeCountQuery(source, column, startDate, endDate);
                        resultRow.add(LATResult);
                        source = "Baltimore Sun";
                        String BSResult = ql.executeCountQuery(source, column, startDate, endDate);
                        resultRow.add(BSResult);
                        source = "Chicago Tribune";
                        String CTResult = ql.executeCountQuery(source, column, startDate, endDate);
                        resultRow.add(CTResult);
                        String total = Integer.toString(Integer.parseInt(NYTResult) + Integer.parseInt(LATResult) + Integer.parseInt(BSResult) + Integer.parseInt(CTResult));
                        resultRow.add(total);
                    } else if ("aggregate".equals(querySources)) {
                        source = "\"New York Times\" or \"Los Angeles Times\" or \"Baltimore Sun\" or \"Chicago Tribune\"";
                        resultRow.add(ql.executeCountQuery(source, column, startDate, endDate));
                    } else {
                        resultRow.add(ql.executeCountQuery(querySources, column, startDate, endDate));
                    }
                }
                ql.results.put(rowName, resultRow);
            }
        }
    }

    public static void generateDateRangeCounts(Integer startDate, Integer endDate, String querySources, QueryLucene ql, List<String[]> queries) throws IOException, ParseException {

        ql.results.put("", createDateRangeHeader(startDate, endDate));
        for (String[] row : queries) {
            if (querySources.equals("all")) {
                issueDateRangeQueries(startDate, endDate, "New York Times", row[0], ql);
                issueDateRangeQueries(startDate, endDate, "Los Angeles Times", row[0], ql);
                issueDateRangeQueries(startDate, endDate, "Baltimore Sun", row[0], ql);
                issueDateRangeQueries(startDate, endDate, "Chicago Tribune", row[0], ql);

            } else if (querySources.equals("aggregate")) {
                issueDateRangeQueries(startDate, endDate, "\"New York Times\" or \"Los Angeles Times\" or \"Baltimore Sun\" or \"Chicago Tribune\"", row[0], ql);
            } else {
                issueDateRangeQueries(startDate, endDate, querySources, row[0], ql);
            }
        }
    }

    private static void issueDateRangeQueries(Integer startDate, Integer endDate, String source, String queryText, QueryLucene ql) throws IOException, ParseException {
        DateTime dtStartDate = convertIntDateToDate(startDate.toString());
        DateTime dtEndDate = convertIntDateToDate(endDate.toString());

        ArrayList<String> resultRow = new ArrayList<String>();
        String rowName;
        endDate = startDate;

        rowName = cleanLabel(source + "." + queryText);
        resultRow.add(rowName);
        for (DateTime date = dtStartDate; date.isBefore(dtEndDate); date = date.plusDays(1)) {

            Integer queryDate = Integer.parseInt(date.toString("yyyyMMdd"));
            System.out.println(queryDate);
            resultRow.add(ql.executeCountQuery(source, queryText, queryDate, queryDate));

        }
        ql.results.put(rowName, resultRow);
    }
}
