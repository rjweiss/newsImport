package edu.stanford.pcl.news.parsers;

import edu.stanford.pcl.news.dataHandlers.Article;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Rebecca                                                                    `
 * Date: 6/18/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class generalParser extends Parser {

    public Article parse(File file, String source, String language, String country) {
        Article article = new Article();
        article.setMediaType("printnews");
        article.setMediaSource(source);
        article.setFileName(file.getAbsolutePath());
        article.setLanguage(language);
        article.setCountry(country);

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        domFactory.setValidating(false);

        try {
            domFactory.setFeature("http://xml.org/sax/features/namespaces", false);
            domFactory.setFeature("http://xml.org/sax/features/validation", false);
            domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document document = builder.parse(file);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr;
            NodeList result;


            article.setPageNumber("");


            // Publication Date
            expr = xpath.compile("//article/publicationDate");
            result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            try {
//                String yearFour = result.item(0).getTextContent().substring(0,3);
//                String monthTwo = result.item(0).getTextContent().substring(4,5);
//                String dayTwo = result.item(0).getTextContent().substring(6,7);
                DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd");

                article.setPublicationDate(dateFormat.parseDateTime(result.item(0).getTextContent()));

            } catch (Exception e) {
                article.setPublicationDate(null);

            }


            // Headline
            expr = xpath.compile("//article/headline");
            result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            try {
                article.setHeadline(result.item(0).getTextContent());
            } catch (Exception e) {
                article.setHeadline("");
            }

            // Text
            StringBuilder sb = new StringBuilder();
            expr = xpath.compile("//article/text");
            result = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
            try {
                article.setText(result.item(0).getTextContent());
            } catch (Exception e) {
                article.setText("");
            }

            // set status
            article.setStatus("0");

            // set complete flag
            try {
                int yearFour = Integer.parseInt(article.getPublicationDate().toString("yyyy"));
                int monthTwo = Integer.parseInt(article.getPublicationDate().toString("MM"));

                if (yearFour < 2007 || (yearFour == 2007 && monthTwo < 6)) {
                    article.setOverLap("1");
                } else {
                    article.setOverLap("0");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }
        // TODO:  Exception handling.
        catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        return article;
    }

}
