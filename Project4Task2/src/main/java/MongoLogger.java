import com.mongodb.client.*;
import org.bson.Document;
import java.util.Date;
import java.util.Arrays;
import java.util.ArrayList;
import com.mongodb.MongoCommandException;

/**
 * Utility class to handle MongoDB logging
 * @author Chiaszu Kuo
 */
public class MongoLogger {
    private final MongoCollection<Document> collection;

    public MongoLogger(String connectionString) {
        MongoCollection<Document> tempCollection;
        try {
            MongoClient mongoClient = MongoClients.create(connectionString);
            MongoDatabase database = mongoClient.getDatabase("recipeApp");
            
            // Try to get the collection first
            try {
                tempCollection = database.getCollection("logs");
                // Test the connection with a simple operation
                tempCollection.countDocuments();
                System.out.println("Successfully connected to MongoDB!");
            } catch (Exception e) {
                // If collection doesn't exist, create it
                try {
                    database.createCollection("logs");
                    System.out.println("Created 'logs' collection");
                } catch (MongoCommandException mce) {
                    // If collection already exists with different case or other issue
                    System.out.println("Collection might already exist: " + mce.getMessage());
                }
                tempCollection = database.getCollection("logs");
            }
            this.collection = tempCollection;
            
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Log a recipe search request
     * @param searchType Type of search (name, ingredient, category)
     * @param searchTerm The search term used
     * @param resultCount Number of results found
     * @param deviceInfo Information about the device making the request
     * @param responseTime Time taken to process the request
     */
    public void logSearch(String searchType, String searchTerm, int resultCount, 
                         String deviceInfo, long responseTime) {
        Document log = new Document()
                .append("timestamp", new Date())
                .append("searchType", searchType)
                .append("searchTerm", searchTerm)
                .append("resultCount", resultCount)
                .append("deviceInfo", deviceInfo)
                .append("responseTime", responseTime)
                .append("endpoint", "/recipe/search");

        collection.insertOne(log);
    }

    /**
     * Get all logs for the dashboard
     * @return FindIterable<Document> containing all logs
     */
    public FindIterable<Document> getAllLogs() {
        return collection.find().sort(new Document("timestamp", -1));
    }

    /**
     * Get analytics: most searched terms
     * @param searchType Type of search to analyze
     * @return AggregateIterable<Document> containing search term frequencies
     */
    public AggregateIterable<Document> getMostSearchedTerms(String searchType) { // refer to LLM
        return collection.aggregate(Arrays.asList(
            new Document("$match", new Document("searchType", searchType)),
            new Document("$group", new Document()
                .append("_id", "$searchTerm")
                .append("count", new Document("$sum", 1))),
            new Document("$sort", new Document("count", -1)),
            new Document("$limit", 10)
        ));
    }

    /**
     * Get analytics: average response time
     * @return double representing average response time
     */
    public double getAverageResponseTime() {
        Document result = collection.aggregate(Arrays.asList(
            new Document("$group", new Document()
                .append("_id", null)
                .append("avgTime", new Document("$avg", "$responseTime")))
        )).first();
        
        return result != null ? result.getDouble("avgTime") : 0.0;
    }
} 