package edu.stanford.pcl.news.scrapers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 7/18/12
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */

public class TimesIndiaScraper {
    public static DateTime convertIntDateToDate(String date) {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String fullDate = year + "-" + month + "-" + day;
        return dateFormat.parseDateTime(fullDate);
    }

    public static void scrapeNews(String startDate, String endDate, Integer startTime) throws IOException, TransformerException, ParserConfigurationException, InterruptedException {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

        DateTime dtStartDate = dateFormat.parseDateTime(startDate);
        DateTime dtEndDate = dateFormat.parseDateTime(endDate);


        for (DateTime date = dtStartDate; date.isBefore(dtEndDate.plusDays(1)); date = date.plusDays(1)) {
            System.out.println(date.toString("d-M-yyyy"));
            getNewsArticleList(date, startTime);
            startTime++;
        }

    }

    public static void getNewsArticleList(DateTime date, Integer starttime) throws IOException, TransformerException, ParserConfigurationException, InterruptedException {
        String URL = "http://timesofindia.indiatimes.com/" + date.toString("yyyy") + "/" + date.toString("M") + "/" + date.toString("d") + "/archivelist/year-" + date.toString("yyyy") + ",month-" + date.toString("M") + ",starttime-" + starttime + ".cms";

        System.out.println(URL);
        Document document = Jsoup.connect(URL).timeout(0).get();

        Elements links = document.select("div[style=font-family:arial ;font-size:12;font-weight:bold; color: #006699] a");
        Integer articleNumber = 0;
        for (Element link : links) {

            try {
                String linkHref = link.attr("href");
                System.out.println(date.toString("yyyy-MM-dd" + articleNumber));
                processFile(linkHref, date, articleNumber);
                Thread.currentThread().sleep(1000);
                articleNumber++;
            } catch (IOException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (TransformerException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (ParserConfigurationException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (InterruptedException e) {
                //e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    public static void processFile(String URL, DateTime date, Integer articleNumber) throws IOException, TransformerException, ParserConfigurationException {
        try {
            // System.out.println("waiting");
            Document document = Jsoup.connect(URL).timeout(0).get();


            String title = document.select("span[class=arttle] h1").text();
            // System.out.println("title: " + title);
            String paragraphText = document.select(".Normal").text();

            //System.out.println("text: " +paragraphText);

            if (!paragraphText.isEmpty()) {
                String result = createXMLDoc(title, date, paragraphText);
                String fileName = "/rawdata/newspapers/timesindia/" + date.toString("yyyy-MM-dd") + "-" + articleNumber.toString() + ".xml";


                Writer out = new OutputStreamWriter(new FileOutputStream(fileName));
                try {
                    out.write(result);
                    //System.out.println("saved");
                } catch (Exception e) {
                    //System.out.println("No text for article: " + title);
                }


                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    public static String createXMLDoc(String title, DateTime date, String paragraphs) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();

        org.w3c.dom.Element article = doc.createElement("article");
        doc.appendChild(article);

        org.w3c.dom.Element publicationDate = doc.createElement("publicationDate");
        article.appendChild(publicationDate);
        publicationDate.appendChild(doc.createTextNode(date.toString("yyyy-MM-dd")));

        org.w3c.dom.Element headline = doc.createElement("headline");
        article.appendChild(headline);
        headline.appendChild(doc.createTextNode(title));

        org.w3c.dom.Element text = doc.createElement("text");
        article.appendChild(text);
        text.appendChild(doc.createTextNode(paragraphs));

        TransformerFactory transfac = TransformerFactory.newInstance();
        Transformer trans = transfac.newTransformer();
        trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");

        String xmlString = null;
        try {
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            xmlString = sw.toString();
        } catch (Exception e) {
            xmlString = null;
        }
        //System.out.println(xmlString);
        return xmlString;
    }
}
