package sayit.server.db;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * The <c>MongoWrapper</c> class contains the logic for connecting to the <c>MongoDB</c> instance.
 * In particular, this class is responsible for creating a <c>MongoClient</c> which is able to
 * handle serialization and deserialization of POJOs (plain-old-Java-objects).
 */
public final class MongoWrapper {
    private static final String SAYIT_DATABSE = "sayit";


    private static MongoWrapper _mongoInstance;
    private final MongoClient _client;

    private MongoWrapper(String connectionStr) {
        // https://www.mongodb.com/developer/languages/java/java-mapping-pojos/
        ConnectionString connStr = new ConnectionString(connectionStr);
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(connStr)
                .codecRegistry(codecRegistry)
                .build();

        _client = MongoClients.create(clientSettings);
    }

    /**
     * Gets an existing instance of the <c>MongoWrapper</c>, or creates a new one if none exists.
     *
     * @param connectionStr The connection string to use to connect to the MongoDB instance.
     * @return The <c>MongoWrapper</c> instance.
     */
    public static MongoWrapper getOrCreateInstance(String connectionStr) {
        if (_mongoInstance == null) {
            _mongoInstance = new MongoWrapper(connectionStr);
        }

        return _mongoInstance;
    }

    /**
     * Gets the client associated with this instance.
     *
     * @return The client.
     */
    public MongoClient getClient() {
        return this._client;
    }

    /**
     * Gets the <c>sayit</c> database. This might be preferred
     * over the <c>getClient()</c> method.
     *
     * @return The <c>sayit</c> database.
     */
    public MongoDatabase getSayItDatabase() {
        return this._client.getDatabase(SAYIT_DATABSE);
    }
}