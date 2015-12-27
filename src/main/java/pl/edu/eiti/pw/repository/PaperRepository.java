package pl.edu.eiti.pw.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.eiti.pw.model.Paper;
import pl.edu.eiti.pw.mongo.MongoConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Component used to store and obtain objects from mongo database
 */
@Component
public class PaperRepository {

    private static final Logger logger = LoggerFactory.getLogger(PaperRepository.class);

    @Autowired
    private DBCollection dbCollection;

    @Autowired
    private MongoConverter<Paper> mongoConverter;

    public void save(Paper paper) {
        logger.debug("Saving paper with id: {} into database...", paper.getIndex());
        dbCollection.save(mongoConverter.convertToDbObject(paper));
    }

    public List<Paper> findPapersByAuthor(String authorName) {
        List<Paper> publications = new ArrayList<>();
        BasicDBObject query = new BasicDBObject();
        query.put("authors", Pattern.compile(authorName));

        DBCursor cursor = dbCollection.find(query);
        while (cursor.hasNext()) {
            DBObject dbObject = cursor.next();
            logger.info(String.valueOf(dbObject));
            publications.add(mongoConverter.convertToDomainObject(dbObject, Paper.class));
        }

        return publications;
    }

    public void setMongoConverter(MongoConverter<Paper> mongoConverter) {
        this.mongoConverter = mongoConverter;
    }

    public void setDbCollection(DBCollection dbCollection) {
        this.dbCollection = dbCollection;
    }
}
