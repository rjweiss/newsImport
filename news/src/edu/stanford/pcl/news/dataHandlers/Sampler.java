package edu.stanford.pcl.news.dataHandlers;

import au.com.bytecode.opencsv.CSVWriter;
import com.martiansoftware.jsap.JSAPResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.joda.time.DateTime;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class Sampler {
    private static LinkedHashMap<String, ArrayList> results = new LinkedHashMap<String, ArrayList>();

    private static void saveFile(String outputFile) throws IOException {
        Iterator<Map.Entry<String, ArrayList>> iterator = results.entrySet().iterator();
        CSVWriter writer = new CSVWriter(new FileWriter(outputFile), '\t');

        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            ArrayList<String> row = (ArrayList) entry.getValue();
            String[] fullRow = row.toArray(new String[row.size()]);
            writer.writeNext(fullRow);
        }
        writer.close();
    }

    public static void sample(JSAPResult JSAPconfig) throws IOException {

        Integer i;

        List<Integer> years = new ArrayList<Integer>();
        List<Integer> weeks = new ArrayList<Integer>();
        List<Integer> days = new ArrayList<Integer>();
        Random random = new Random();

        System.out.println("here");

        while (years.size() <= 28) {
            int y = 2000 + random.nextInt(7);
            years.add(y);
            System.out.println(y);
        }


        random = new Random();
        while (weeks.size() <= 28) {
            weeks.add(random.nextInt(52) + 1);
        }

        random = new Random();
        while (days.size() <= 28) {
            days.add(random.nextInt(7) + 1);
        }

        Article article;

        Updater updater = new Updater();
        updater.connect();

        Iterator yearsIterator = years.iterator();
        Iterator weeksIterator = weeks.iterator();
        Iterator daysIterator = days.iterator();
        BasicDBObject query = new BasicDBObject();

        for (i = 0; i <= 28; i++) {
            ArrayList<String> resultSet = new ArrayList<String>();


            Integer year = (Integer) yearsIterator.next();
            Integer week = (Integer) weeksIterator.next();
            Integer day = (Integer) daysIterator.next();

            DateTime date = new DateTime(year, 1, 1, 0, 0, 0, 0).plusWeeks(week);
            date = date.plusDays(day);
            System.out.println(date.toString());
            String sources[] = new String[]{"New York Times", "Chicago Tribune", "Los Angeles Times", "Baltimore Sun"};
            Integer weights[] = new Integer[]{10, 10, 10, 10};

            int j;
            for (j = 0; j < 4; j++) {
                System.out.println(sources[j]);
                query.clear();
                query.put("publicationDate", date.toDate());
                query.put("mediaSource", sources[j]);

                DBCursor cursor = updater.queryCursor("articles", query);
                Integer count = 0;
                int size = cursor.size();


                Set<Integer> articles = new HashSet<Integer>();

                random = new Random();
                while (articles.size() <= weights[j]) {
                    articles.add(random.nextInt(size) + 1);
                }
                int recorded = 0;
                Iterator articlesIterator = articles.iterator();
                while (recorded < weights[j]) {
                    Integer nextArticleIndex = (Integer) articlesIterator.next();
                    cursor.limit(-1).skip(nextArticleIndex).next();
                    DBObject obj = cursor.curr();
                    article = Article.fromMongoObject(obj);
                    resultSet.add(article.getPublicationDate().toString("yyyyMMdd"));
                    resultSet.add(article.getMediaSource());
                    resultSet.add(article.getFileName());
                    resultSet.add(article.getHeadline());
                    resultSet.add(article.getText());
                    results.put(Integer.toString(i + j), resultSet);
                }
            }
        }
        updater.close();
        saveFile("/home/ec2-user/sample.txt");
    }

}
