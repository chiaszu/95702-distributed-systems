import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.Arrays;
import java.util.Scanner;
import java.util.List;


/**
 * Author: Chia-Szu, Kuo (chiaszuk)
 * This program analyzes "All's Well That Ends Well" by William Shakespeare using Apache Spark.
 *
 */
public class ShakespeareAnalytics {

    public static void main(String[] args) {
        // Create a Spark configuration object with application name and run in local mode
        SparkConf conf = new SparkConf()
                .setAppName("ShakespeareAnalytics")
                .setMaster("local");

        // Create a Spark context from the configuration
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load text file into an RDD
        JavaRDD<String> lines = sc.textFile("AllsWellThatEndsWell.txt");

        // Task 0: Count number of lines in the file
        long numOfLines = lines.count();

        // Task 1: Count number of words in the file
        // split each line into words using the specified regular expression, refer to LLM
        JavaRDD<String> words = lines.flatMap((String line) ->
                Arrays.asList(line.split("[^a-zA-Z]+"))
        );

        // Filter out empty strings
        Function<String, Boolean> filter = k -> (!k.isEmpty());
        JavaRDD<String> nonEmptyWords = words.filter(word -> !word.isEmpty());

        // Count the number of non-empty words
        long numberOfWords = nonEmptyWords.count();

        // Task 2: display the number of distinct words in the file
        long numberOfDistinctWords = nonEmptyWords.distinct().count();

        // Task 3: find the number of symbols in file
        // split each line into characters using empty string as delimiter
        JavaRDD<String> symbols = lines.flatMap((String line) ->
                Arrays.asList(line.split(""))
        );
        long numberOfSymbols = symbols.count();

        // Task 4: display the number of distinct symbols in the file
        long numberOfDistinctSymbols = symbols.distinct().count();

        // Task 5: display the number of distinct letters in the file
        // filter symbols to keep only letters and count unique letters
        JavaRDD<String> letters = symbols.filter(symbol -> symbol.matches("[a-zA-Z]"));
        long numberOfDistinctLetters = letters.distinct().count();


        // display file info
        System.out.println("[Task 0] Number of lines: " + numOfLines);
        System.out.println("[Task 1] Number of words: " + numberOfWords);
        System.out.println("[Task 2] Number of distinct words: " + numberOfDistinctWords);
        System.out.println("[Task 3] Number of symbols: " + numberOfSymbols);
        System.out.println("[Task 4] Number of distinct symbols: " + numberOfDistinctSymbols);
        System.out.println("[Task 5] Number of distinct letters: " + numberOfDistinctLetters);


        // Task 6: interactive search
        Scanner scanner = new Scanner(System.in);
        System.out.print("[Task 6] Enter a word (case sensitive): ");
        String searchWord = scanner.nextLine();

        // filter lines that contain the search word (case-sensitive)
        List<String> matchingLines = lines.filter(line -> line.contains(searchWord)).collect();

        // Display the results
        System.out.println("Lines containing '" + searchWord + "':");
        for (String line : matchingLines) {
            System.out.println(line);
        }

        // Close scanner
        scanner.close();

        sc.stop();
    }
}