import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class MongoDBDemo {
    public static void main(String[] args) {
        // MongoDB Atlas connection string
        String connectionString = "mongodb+srv://chiaszuk:yFkzx062ILbTEx3f@cluster0.u3zw0.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";

        // Creating the scanner before the MongoDB operations
        Scanner scanner = new Scanner(System.in);

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            System.out.println("Connected to MongoDB Atlas successfully!");

            // Get database and collection
            MongoDatabase database = mongoClient.getDatabase("recipeApp");
            MongoCollection<Document> collection = database.getCollection("userInputs");
            System.out.println("Connected to database 'recipeApp' and collection 'userInputs'");

            // Prompt user for input after connection is established
            System.out.print("Enter a string to store in MongoDB: ");
            String userInput = scanner.nextLine();

            // Create a document to store
            Document document = new Document()
                    .append("text", userInput)
                    .append("timestamp", new Date());

            // Insert the document into the collection
            collection.insertOne(document);
            System.out.println("Successfully stored the string in MongoDB Atlas!");

            // Read all documents from the collection
            System.out.println("\nReading all documents from the database:");
            List<String> allStrings = new ArrayList<>();

            try (MongoCursor<Document> cursor = collection.find().iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String text = doc.getString("text");
                    allStrings.add(text);
                    System.out.println("Document: " + doc.toJson());
                }
            }

            // Print all strings contained in the documents
            System.out.println("\nAll strings stored in the database:");
            for (int i = 0; i < allStrings.size(); i++) {
                System.out.println((i + 1) + ". " + allStrings.get(i));
            }

        } catch (Exception e) {
            System.err.println("Error connecting to MongoDB Atlas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close scanner in finally block to ensure it's always closed
            scanner.close();
        }
    }
}