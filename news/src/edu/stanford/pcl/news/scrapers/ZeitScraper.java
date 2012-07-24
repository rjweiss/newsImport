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
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */

public class ZeitScraper {
    public static DateTime convertIntDateToDate(String date) {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6, 8);
        String fullDate = year + "-" + month + "-" + day;
        return dateFormat.parseDateTime(fullDate);
    }

    public static void scrapeNews() throws IOException, TransformerException, ParserConfigurationException {
        int year = 2000;
        int issue = 1;
        for (year = 2000; year <= 2011; year++) {
            for (issue = 1; issue <= 55; issue++) {
                if (year != 2000 || !(year == 2001 && issue <= 15)) {
                    String twoDigitIssue = Integer.toString(issue);
                    if (twoDigitIssue.length() < 2) {
                        twoDigitIssue = "0" + twoDigitIssue;
                    }

                    getNewsArticleList(Integer.toString(year), twoDigitIssue);
                    System.out.println(year + "-" + twoDigitIssue);
                }
            }
        }

    }

    public static void getNewsArticleList(String year, String issue) throws IOException, TransformerException, ParserConfigurationException {
        try {
            String URL = "http://www.zeit.de/" + year + "/" + issue + "/index";
            System.out.println(URL);
            Document document = Jsoup.connect(URL).timeout(12000).get();

            Elements links = document.select("#main a");
            Integer articleNumber = 0;
            String lastURL = null;
            for (Element link : links) {

                String linkHref = link.attr("href");
                if (!linkHref.equals(lastURL)) {

                    //System.out.println("link: " + linkHref);
                    processFile(linkHref, year, issue, articleNumber);
                    lastURL = linkHref;
                    articleNumber++;
                }
            }
        } catch (Exception e) {
        }

    }

    public static void processFile(String URL, String year, String issue, Integer articleNumber) throws IOException, TransformerException, ParserConfigurationException {
        Document document = Jsoup.connect(URL).timeout(4000).get();

        String title = document.select(".articleheader").text();

        //System.out.println("title: " +title);
        Elements paragraphs = document.select("#main p[class!=excerpt]");
        String paragraphText = paragraphs.text();
        //for (Element paragraph : paragraphs) {
        //    paragraphText += paragraph.text();
        //}

        String result = createXMLDoc(title, year, issue, paragraphText);
        String fileName = "/rawdata/newspapers/zeit/" + year + "-" + issue + "-" + articleNumber.toString() + ".xml";
        //System.out.println("text: " +paragraphText);
        Writer out = new OutputStreamWriter(new FileOutputStream(fileName));
        try {
            out.write(result);
        } catch (Exception e) {
            System.out.println("No text for article: " + title);
        }

        out.close();
    }

    public static String createXMLDoc(String title, String year, String issue, String paragraphs) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();

        org.w3c.dom.Element article = doc.createElement("article");
        doc.appendChild(article);

        org.w3c.dom.Element publicationDate = doc.createElement("publicationDate");
        article.appendChild(publicationDate);
        publicationDate.appendChild(doc.createTextNode(year + "-" + issue));

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
