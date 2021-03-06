package edu.stanford.pcl.news.dataHandlers;

import au.com.bytecode.opencsv.CSVWriter;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import javax.sound.midi.SysexMessage;
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

    public static void sample(Integer sampleSize, Integer totalArticles) throws IOException {

        Set<Integer> sample = new HashSet<Integer>();
        Random random = new Random();
        int draw = 0;
        while (sample.size() < sampleSize + 200) {
            sample.add(random.nextInt(totalArticles));
            System.out.println(draw);
            draw++;
        }

        Article article;

        Updater updater = new Updater();
        updater.connect();

        Iterator sampleIterator = sample.iterator();
        Integer i = 0;
        while (sampleIterator.hasNext() && i <= sampleSize) {

            try {
                ArrayList<String> resultSet = new ArrayList<String>();
                BasicDBObject query = new BasicDBObject();
                Integer nextArticle = (Integer) sampleIterator.next();
                query.put("articleNumber", nextArticle);
                DBObject obj = updater.getOne("articles", query);


                article = Article.fromMongoObject(obj);
                resultSet.add(article.getPublicationDate().toString("yyyyMMdd"));
                resultSet.add(article.getMediaSource());
                resultSet.add(article.getFileName());
                resultSet.add(article.getHeadline());
                resultSet.add(article.getText());
                System.out.println(i + " " + article.getFileName());
                results.put(Integer.toString(i), resultSet);
                i++;
            } catch (Exception e) {
                //  e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }


        updater.close();

        saveFile("/home/ec2-user/sample.txt");
    }

}
