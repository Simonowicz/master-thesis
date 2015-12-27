package pl.edu.eiti.pw.config;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pl.edu.eiti.pw.io.ArnetMinerTransformer;
import pl.edu.eiti.pw.io.InputTransformer;
import pl.edu.eiti.pw.repository.PaperRepository;

import java.net.UnknownHostException;

/**
 * Configures the application creating all the necessary objects for the application
 */
@Configuration
@ComponentScan("pl.edu.eiti.pw")
public class ApplicationConfig {

    @Autowired
    private PaperRepository paperRepository;

    @Bean
    public MongoClient mongoClient(@Value("${mongo.db.host}") String host, @Value("${mongo.db.port}") int port) throws UnknownHostException {
        return new MongoClient(host, port);
    }

    @Bean
    public DB db(MongoClient mongoClient, @Value("${mongo.db.name}") String dbName, @Value("${mongo.db.collection}") String dbCollection) {
        DB db = mongoClient.getDB(dbName);
        if (!db.collectionExists(dbCollection)) {
            db.createCollection(dbCollection, new BasicDBObject("autoIndexId", false));
        }
        return db;
    }

    @Bean
    public DBCollection dbCollection(DB db, @Value("${mongo.db.collection}") String dbCollection) {
        return db.getCollection(dbCollection);
    }

    @Bean
    @Profile("write")
    public InputTransformer inputTransformer(@Value("${arnet.transformer.file}") String filePath, @Value("${arnet.document.limit}") Integer documentLimit) {
        return new ArnetMinerTransformer(filePath, documentLimit, this.paperRepository);
    }
}
