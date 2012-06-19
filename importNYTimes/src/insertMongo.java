/**
 * Created with IntelliJ IDEA.
 * User: Rebecca
 * Date: 6/18/12
 * Time: 3:29 PM
 * To change this template use File | Settings | File Templates.
 */

import com.mongodb.*;
import java.util.*;

public class insertMongo {

    public static void main(String[] args) throws Exception{

        Mongo myM = new Mongo();
        DB myDB = myM.getDB("test");
        DBCollection myColl = myDB.getCollection("articles");

//        Object o = new Object();
//        insertDocument(o, myColl);

    }

    private static void insertDocument(Object obj, DBCollection coll){
        BasicDBObject doc = new BasicDBObject();

        doc.put("articlePageNumber", obj.toString());
        doc.put("articlePublicationDate", obj.toString());
        doc.put("articleHeadline", obj.toString());
        doc.put("articleText", obj.toString());
        doc.put("articleFile", obj.toString());
        doc.put("articlePath", obj.toString());

        coll.insert(doc);
    }

}
