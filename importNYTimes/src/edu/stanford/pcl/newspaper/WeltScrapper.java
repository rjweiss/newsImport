package edu.stanford.pcl.newspaper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;

//http://www.welt.de/nachrichtenarchiv/print-nachrichten-vom-1-1-2000.html?tabPane=ZEITUNG
public class WeltScrapper {
    public static DateTime convertIntDateToDate(String date){
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
        String year = date.substring(0,4);
        String month = date.substring(4,6);
        String day = date.substring(6,8);
        String fullDate = year + "-" + month + "-" +day;
        return dateFormat.parseDateTime(fullDate);
    }

    public static void main(String[] args) throws IOException, TransformerException, ParserConfigurationException {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

        DateTime dtStartDate = dateFormat.parseDateTime("2004-09-10");
        DateTime dtEndDate = dateFormat.parseDateTime("2012-07-08");

        for (DateTime date = dtStartDate; date.isBefore(dtEndDate.plusDays(1)); date = date.plusDays(1)) {
            System.out.println(date.toString("d-M-yyyy"));
            getNewsArticleList(date);
        }

    }

    public static void getNewsArticleList(DateTime date) throws IOException, TransformerException, ParserConfigurationException {
        String URL = "http://www.welt.de/nachrichtenarchiv/print-nachrichten-vom-" + date.toString("d-M-yyyy") + ".html?tabPane=ZEITUNG";


        Document document = Jsoup.connect(URL).timeout(12000).get();

        Elements links =  document.select("a[name=_ch_R_printArchive_]");
        Integer articleNumber=0;
        String lastURL =null;
        for (Element link : links) {

                String linkHref = link.attr("href");
                if (!linkHref.equals(lastURL))
                {

                    processFile(linkHref,date,articleNumber);
                    lastURL = linkHref;
                    articleNumber++;
                }
        }


    }

    public static void processFile(String URL, DateTime date, Integer articleNumber) throws IOException, TransformerException, ParserConfigurationException {
        Document document = Jsoup.connect(URL).timeout(12000).get();


        String title = document.select("h1").text();
        //System.out.println(title);
        Elements paragraphs = document.select("p[class=prefix_2 text artContent]");
        String paragraphText = null;
        for (Element paragraph : paragraphs) {
            paragraphText += paragraph.text();
        }

        String result = createXMLDoc(title,date,paragraphText);
        String fileName = "/Users/seanwestwood/Desktop/diewelt/" + date.toString("yyyy-MM-dd") + "-" + articleNumber.toString() + ".xml";

        Writer out = new OutputStreamWriter(new FileOutputStream(fileName));
        try {
            out.write(result);
        }
        catch (Exception e)
        {
            System.out.println("No text for article: " + title);
        }


            out.close();

    }

    public static String createXMLDoc(String title, DateTime date, String paragraphs) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();

        org.w3c.dom.Element article = doc.createElement("article");
        doc.appendChild(article);

        org.w3c.dom.Element publicationDate= doc.createElement("publicationDate");
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

        String xmlString =null;
        try{
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        DOMSource source = new DOMSource(doc);
        trans.transform(source, result);
        xmlString = sw.toString();
        }
        catch (Exception e)
        {
            xmlString = null;
        }
        //System.out.println(xmlString);
        return xmlString;
    }

}
