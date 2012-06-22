package edu.stanford.pcl.newspaper;

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
 * User: Rebecca
 * Date: 6/18/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class TribParser extends Parser {



    // TODO:  Handle missing fields robustly.
    public Article parse(File file, String source) {
        Article article = new Article();
        article.setMediaType("newspaper");
        article.setMediaSource(source);
        article.setFileName(file.getAbsolutePath());
	
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

            // Page Number
            expr = xpath.compile("//docdt/startpg");
            result = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
            try {
                article.setPageNumber(result.item(0).getTextContent());
	    }
            catch (Exception e) {
                article.setPageNumber("");
            }

            // Publication Date
            expr = xpath.compile("//pcdt/pcdtn");
            result = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
            try {
                article.setPublicationDate(result.item(0).getTextContent());
	    }
            catch (Exception e) {
                article.setPublicationDate("");
            }


            // Headline
            expr = xpath.compile("//docdt/doctitle");
            result = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
            try {
                article.setHeadline(result.item(0).getTextContent());
	    }
            catch (Exception e) {
                article.setHeadline("");
            }

            // Text
            StringBuilder sb = new StringBuilder();
            expr = xpath.compile("//txtdt/text/paragraph/text()");
            result = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < result.getLength(); i++) {
                sb.append(result.item(i).getTextContent()).append(" ");
            }
            try {
                article.setText(sb.toString());
            }
            catch (Exception e) {
                article.setText("");
            }

            // set status
            article.setStatus("0");

            // set complete flag
            char[] year = new char[4];
            article.getPublicationDate().getChars(0,3,year,0);

            System.out.println(year.toString());

            int yearFour = Integer.parseInt(new String(year));

            char[] month =new char[2];
            article.getPublicationDate().getChars(4,5,year,0);
            int monthTwo = Integer.parseInt(new String(month));

            if (yearFour <2007 || (yearFour == 2007 && monthTwo <6)){
                article.setOverLap("1");
            }
            else{
                article.setOverLap("0");
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
