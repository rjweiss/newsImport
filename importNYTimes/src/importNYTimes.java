import java.io.*;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;

/**
 * Created with IntelliJ IDEA.
 * User: seanwestwood
 * Date: 6/18/12
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class importNYTimes {
    public static void main(String... args) {
        File[] files = new File("C:/").listFiles();
        findFiles(files);
    }

    public static void findFiles(File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                //System.out.println("Directory: " + file.getName());
                findFiles(file.listFiles());
            } else {
                parse(file.getName());
            }
        }
    }
    public static void parse(String fileName){

        XPathReader reader = new XPathReader(fileName);
        String expression = "//head/meta[@name=\"print_page_number\"]";
        String articlePageNumber = reader.read(expression,XPathConstants.STRING).toString();

        expression = "//head/meta[@name=\"publication_month\"]";
        String month = reader.read(expression,XPathConstants.STRING).toString();
        expression = "//head/meta[@name=\"publication_day_of_month\"]";
        String day = reader.read(expression,XPathConstants.STRING).toString();
        expression = "//head/meta[@name=\"publication_year\"]";
        String year = reader.read(expression,XPathConstants.STRING).toString();

        String articlePublicationdate = month + "/"+ day + "/"+year;

        expression = "//body/body.head/hedline/hl1";
        String articleHeadline = reader.read(expression,XPathConstants.STRING).toString();

        expression = "//body/body.content/block[@class=\"full_text\"]/p/text()";
        String articleText = reader.read(expression,XPathConstants.STRING).toString();
        System.out.println(articleText);
    }





}
