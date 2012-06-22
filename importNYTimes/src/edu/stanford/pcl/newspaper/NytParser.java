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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Rebecca
 * Date: 6/18/12
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */

public class NytParser extends Parser {

    private static Map<String, String> attributeMap = new HashMap<String, String>();
    static {
        attributeMap.put("articlePageNumber", "//head/meta[@name=\"print_page_number\"]");
        attributeMap.put("_articlePublicationMonth", "//head/meta[@name=\"publication_month\"]");
        attributeMap.put("_articlePublicationDayOfMonth", "//head/meta[@name=\"publication_day_of_month\"]");
        attributeMap.put("_articlePublicationYear", "//head/meta[@name=\"publication_year\"]");
//        attributeMap.put("articleHeadline", articleHeadline);
//        attributeMap.put("articleText", "//body/body.content/block[@class=\"full_text\"]/p/text()");
//        attributeMap.put("articleFileName", articleFileName);
//        attributeMap.put("articleContentType", articleContentType);
//        attributeMap.put("articleContentSource", articleContentSource);
        // TODO:  Fill out other attributes.
    }

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

            Map<String, String> attributes = new HashMap<String, String>();

            // Attributes
            for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
                expr = xpath.compile(entry.getValue());
                result = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
                attributes.put(entry.getKey(), result.item(0).getAttributes().getNamedItem("content").getNodeValue());
            }

            // Page Number
            try {
                article.setPageNumber(attributes.get("articlePageNumber"));
            }
            catch (Exception e) {
                article.setPageNumber("");
            }

            // Publication Date
            // TODO: Use a real date object.
            try {
                article.setPublicationDate(attributes.get("_articlePublicationYear") + "/" + attributes.get("_articlePublicationMonth") + "/" + attributes.get("_articlePublicationDayOfMonth") );
            }
            catch (Exception e) {
                article.setPublicationDate("");
            }

            // Headline
            expr = xpath.compile("//body/body.head/hedline/hl1");
            result = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
            try {
                article.setHeadline(result.item(0).getTextContent());
            }
            catch (Exception e) {
                article.setHeadline("");
            }

            // Text
            StringBuilder sb = new StringBuilder();
            expr = xpath.compile("//body/body.content/block[@class=\"full_text\"]/p/text()");
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
            char[] year =null;
            article.getPublicationDate().getChars(0,3,year,0);
            int yearFour = Integer.parseInt(new String(year));

            char[] month =null;
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
