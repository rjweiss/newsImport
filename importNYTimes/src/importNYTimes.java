import java.io.*;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;


public class importNYTimes {

    public article NYTimesArticle;

    public void main(String... args) throws ParserConfigurationException, SAXException,
        IOException, XPathExpressionException{
        try {
            NYTimesArticle.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        File[] files = new File("~/Dropbox/stanfordBigData/sampleData/02/").listFiles();
        findFiles(files);
        try {
            NYTimesArticle.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findFiles(File[] files) throws ParserConfigurationException, SAXException,
            IOException, XPathExpressionException{
        for (File file : files) {
            if (file.isDirectory()) {
                //System.out.println("Directory: " + file.getName());
                findFiles(file.listFiles());
            } else {

                parser(file.getAbsolutePath());

            }
        }
    }

        public void parser(String fileName) throws ParserConfigurationException, SAXException,
                IOException, XPathExpressionException{

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            domFactory.setValidating(false);
            domFactory.setFeature("http://xml.org/sax/features/namespaces", false);
            domFactory.setFeature("http://xml.org/sax/features/validation", false);
            domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            domFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            DocumentBuilder builder = domFactory.newDocumentBuilder();

            System.out.println(fileName);
            Document doc = builder.parse(fileName);

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("//head/meta[@name=\"print_page_number\"]");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            NYTimesArticle.setArticlePageNumber(nodes.item(0).getAttributes().getNamedItem("content").getNodeValue().toString());

            expr = xpath.compile("//head/meta[@name=\"publication_month\"]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            String month = nodes.item(0).getAttributes().getNamedItem("content").getNodeValue().toString();

            expr = xpath.compile("//head/meta[@name=\"publication_day_of_month\"]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            String day = nodes.item(0).getAttributes().getNamedItem("content").getNodeValue().toString();

            expr = xpath.compile("//head/meta[@name=\"publication_year\"]");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            String year = nodes.item(0).getAttributes().getNamedItem("content").getNodeValue().toString();

            NYTimesArticle.setArticlePublicationDate(month + "/"+ day + "/"+year);

            expr = xpath.compile("//body/body.head/hedline/hl1");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;
            NYTimesArticle.setArticleHeadline(nodes.item(0).getNodeValue());

            expr = xpath.compile("//body/body.content/block[@class=\"full_text\"]/p/text()");
            result = expr.evaluate(doc, XPathConstants.NODESET);
            nodes = (NodeList) result;

            try{
            NYTimesArticle.setArticleText(nodes.item(0).getNodeValue());
            }
            catch(NullPointerException e)
            {
                NYTimesArticle.setArticleText("");
            }

            NYTimesArticle.setArticleContentType("newspaper");
            NYTimesArticle.setArticleContentSource("New York Times");

            NYTimesArticle.insert();
        }

}