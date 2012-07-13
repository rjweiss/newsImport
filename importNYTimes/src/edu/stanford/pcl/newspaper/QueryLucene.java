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

// Notes:
// 1) Dates are treated as integers because of Lucene's limited type system
// 2) Aggregate queries are not tested and should be used with extreme caution

public class QueryLucene {
    private static final String LUCENE_INDEX_DIRECTORY = "/rawdata/luceneindex";
    LinkedHashMap<String, ArrayList> results = new LinkedHashMap<String, ArrayList>();
    private static ArrayList<String> mediaSourceList = new ArrayList<String>();


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

    public static DateTime convertIntDateToDate(String date) {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String fullDate = year + "-" + month + "-" + day;
        return dateFormat.parseDateTime(fullDate);
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

    public static String cleanLabel(String label) {
        label = label.replace(" ", "-");
        label = label.replace("+", "");
        label = label.replace("\"", "");
        return label;
    }

    public static String sourceList() {
        String sourceList = null;
        for (String source : mediaSourceList) {
            sourceList += "\"" + source + "\" OR";
        }
        sourceList = sourceList.substring(0, sourceList.length() - 3);
        return sourceList;
    }

    public static void loadSourceList(String file) throws IOException {
        CSVReader CSVReader = new CSVReader(new FileReader(file), '\t');

        List<String[]> sources = CSVReader.readAll();
        for (String[] sourceRow : sources) {
            for (String source : sourceRow) {
                System.out.println(source);
                mediaSourceList.add(source);
            }
        }
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

        TotalHitCountCollector collector = new TotalHitCountCollector();
        searcher.search(booleanQuery, collector);

        System.out.println("start: " + startDate);
        System.out.println("end: " + endDate);
        System.out.println("total: " + collector.getTotalHits());

        String hitCount = String.valueOf(collector.getTotalHits());
        searcher.close();
        reader.close();
        analyzer.close();
        return hitCount;
    }

    public static void generateQueryCounts(Integer startDate, Integer endDate, String querySources, QueryLucene ql, List<String[]> queries, String outFile) throws IOException, ParseException {
        Boolean isHeader = true;

        for (String[] row : queries) {
            if (isHeader) {
                ArrayList<String> resultHeader = new ArrayList<String>();
                //resultHeader.add(null);
                for (String column : row) {
                    if ("all".equals(querySources)) {
                        for (String source : mediaSourceList) {
                            resultHeader.add(cleanLabel(column + "." + source));
                        }
                        resultHeader.add(cleanLabel(column + ".total"));
                    } else if ("aggregate".equals(querySources)) {
                        resultHeader.add(cleanLabel(column + ".all"));
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
                        Integer total = 0;
                        for (String source : mediaSourceList) {
                            System.out.println(column);
                            String result = ql.executeCountQuery(source, column, startDate, endDate);

                            resultRow.add(result);
                            total += Integer.parseInt(result);
                        }
                        resultRow.add(Integer.toString(total));
                    } else if ("aggregate".equals(querySources)) {
                        resultRow.add(ql.executeCountQuery(sourceList(), column, startDate, endDate));
                    } else {
                        resultRow.add(ql.executeCountQuery(querySources, column, startDate, endDate));
                    }
                }
                ql.results.put(rowName, resultRow);
            }
        }
        ql.saveFile(outFile);
    }

    public static void generateDateRangeCounts(Integer startDate, Integer endDate, String querySources, QueryLucene ql, List<String[]> queries, String outFile) throws IOException, ParseException {

        ql.results.put("", createDateRangeHeader(startDate, endDate));
        for (String[] row : queries) {
            if (querySources.equals("all")) {
                for (String source : mediaSourceList) {
                    issueDateRangeQueries(startDate, endDate, source, row[0], ql);
                }

            } else if (querySources.equals("aggregate")) {
                issueDateRangeQueries(startDate, endDate, sourceList(), row[0], ql);
            } else {
                issueDateRangeQueries(startDate, endDate, querySources, row[0], ql);
            }
        }
        ql.saveFile(outFile);
    }

    private static void issueDateRangeQueries(Integer startDate, Integer endDate, String source, String queryText, QueryLucene ql) throws IOException, ParseException {
        DateTime dtStartDate = convertIntDateToDate(startDate.toString());
        DateTime dtEndDate = convertIntDateToDate(endDate.toString());

        ArrayList<String> resultRow = new ArrayList<String>();
        String rowName;
        endDate = startDate;

        rowName = cleanLabel(source + "." + queryText);
        resultRow.add(rowName);
        for (DateTime date = dtStartDate; date.isBefore(dtEndDate.plusDays(1)); date = date.plusDays(1)) {

            Integer queryDate = Integer.parseInt(date.toString("yyyyMMdd"));
            System.out.println(queryDate);
            resultRow.add(ql.executeCountQuery(source, queryText, queryDate, queryDate));
        }
        ql.results.put(rowName, resultRow);
    }

    public static void generateOccurenceList(Integer startDate, Integer endDate, String querySources, String terms, QueryLucene ql, String outFilePath) throws IOException, ParseException {
        if (querySources.equals("all")) {
            for (String source : mediaSourceList) {
                executeOccurenceQuery(ql, source, terms, startDate, endDate, outFilePath);
            }
        } else if (querySources.equals("aggregate")) {
            executeOccurenceQuery(ql, sourceList(), terms, startDate, endDate, outFilePath);
        } else {
            executeOccurenceQuery(ql, querySources, terms, startDate, endDate, outFilePath);
        }
    }

    public static void executeOccurenceQuery(QueryLucene ql, String source, String terms, Integer startDate, Integer endDate, String outFilePath) throws IOException, ParseException {
        ql.results.clear();
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

        IndexSearcher indexSearcherCount = new IndexSearcher(reader);
        IndexSearcher indexSearcherEntries = new IndexSearcher(reader);
        TotalHitCountCollector collector = new TotalHitCountCollector();
        indexSearcherCount.search(booleanQuery, collector);

        System.out.println("start: " + startDate);
        System.out.println("end: " + endDate);
        System.out.println("total: " + collector.getTotalHits());

        Sort sort = new Sort(new SortField("publicationDate", SortField.INT));

        ArrayList<String> headerRow = new ArrayList<String>();
        headerRow.add("id");
        headerRow.add("publicationDate");
        headerRow.add("mediaSource");
        headerRow.add("fileName");
        headerRow.add("headline");
        headerRow.add("pageNumber");
        ql.results.put("header", headerRow);

        if (collector.getTotalHits() > 0) {
            TopDocs topDocs = indexSearcherEntries.search(booleanQuery, collector.getTotalHits(), sort);

            int i = 0;
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                ArrayList<String> resultRow = new ArrayList<String>();
                Document doc = indexSearcherEntries.doc(scoreDoc.doc);
                resultRow.add(String.valueOf(i));
                resultRow.add(doc.get("publicationDate"));
                resultRow.add(doc.get("mediaSource"));
                resultRow.add(doc.get("fileName"));
                resultRow.add(doc.get("headline"));
                resultRow.add(doc.get("pageNumber"));
                ql.results.put(String.valueOf(i), resultRow);
                i++;
            }
        } else {
            ArrayList<String> resultRow = new ArrayList<String>();
            resultRow.add("0");
            resultRow.add("0");
            resultRow.add("0");
            resultRow.add("0");
            resultRow.add("0");
            resultRow.add("0");
            ql.results.put("0", resultRow);
        }
        indexSearcherCount.close();
        indexSearcherEntries.close();
        reader.close();
        analyzer.close();

        if(terms.length()>80) {
            String cname = terms.substring(0,terms.lastIndexOf("(")-5);
            terms = cname + "-conflict";
        }

        String file = outFilePath + cleanLabel(terms) + "-" + cleanLabel(source) + ".txt";
        System.out.println(file);
        ql.saveFile(file);
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
        loadSourceList(JSAPconfig.getString("sourceList"));
        String type = JSAPconfig.getString("source");
        Integer startDate = Integer.parseInt(JSAPconfig.getString("startDate"));
        Integer endDate = Integer.parseInt(JSAPconfig.getString("endDate"));

        QueryLucene ql = new QueryLucene();
        CSVReader CSVReader = new CSVReader(new FileReader(JSAPconfig.getString("queryListFile")), '\t');
        List<String[]> queries = CSVReader.readAll();

        if ("queryCounts".equals(JSAPconfig.getString("type"))) {
            generateQueryCounts(startDate, endDate, JSAPconfig.getString("querySources"), ql, queries, JSAPconfig.getString("outputFile"));
        } else if ("dateRangeCounts".equals(JSAPconfig.getString("type"))) {
            generateDateRangeCounts(startDate, endDate, JSAPconfig.getString("querySources"), ql, queries, JSAPconfig.getString("outputFile"));
        } else if ("occurrenceList".equals(JSAPconfig.getString("type"))) {
            //know this is wrong, but whatevs
            for (String[] rows : queries) {
                for (String column : rows) {
                    generateOccurenceList(startDate, endDate, JSAPconfig.getString("querySources"), column, ql, JSAPconfig.getString("outputFilePath"));
                }
            }
        } else {
            System.exit(1);
        }
    }
}
