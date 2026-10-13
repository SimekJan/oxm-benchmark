package cz.cuni.mff.simekjan.document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import cz.cuni.mff.simekjan.ConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;
import java.util.Map;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Provides connection to chosen Mongo instance from config
 * to all parts of program
 */
@Slf4j
public class MongoDbManager {

    private static final String DEFAULT_MONGO_CONNECTION = "mongodb://mongo:27017";
    private static final String DEFAULT_MONGO_DATABASE_NAME = "oxm_benchmark";

    private static MongoClient client;
    private static MongoDatabase instance;

    /**
     * Provides connection to Mongo Database
     *
     * @return Instance of MongoDB connection
     */
    public static synchronized MongoDatabase getDb() {
        if (instance != null) {
            return instance;
        }

        String connection = DEFAULT_MONGO_CONNECTION;
        String dbName = DEFAULT_MONGO_DATABASE_NAME;

        try {
            Map<String, Object> config = ConfigLoader.getSection(
                ConfigLoader.IMPORTER_SECTION,
                ConfigLoader.MONGO_IMPORTER_SECTION);

            connection = (String) config.get("connection");
            dbName = (String) config.get("db_name");
        } catch (IOException e) {
            log.info("Cannot load MongoDB config, using default connection and DB name.");
        }

        CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(
                PojoCodecProvider.builder()
                    .automatic(true)
                    .build()
            )
        );

        client = MongoClients.create(
            MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connection))
                .codecRegistry(pojoCodecRegistry)
                .build()
        );

        instance = client.getDatabase(dbName);

        return instance;
    }

    public static synchronized void close() {
        if (client != null) {
            client.close();
            client = null;
            instance = null;
        }
    }
}
