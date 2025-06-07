package ds.project1task2;

import java.util.Map;
import java.util.TreeMap;

public class ClickerModel {
    private final Map<String, Integer> voteCounts;

    // Constructor initializes an empty vote counter
    public ClickerModel() {
        this.voteCounts = new TreeMap<>();
    }

    // Method to add a vote
    public synchronized void addVote(String answer) {
        if (voteCounts.containsKey(answer)) {
            voteCounts.put(answer, voteCounts.get(answer) + 1);
        } else {
            voteCounts.put(answer, 1);
        }
    }

    // Method to get the results (returns TreeMap directly)
    public synchronized Map<String, Integer> getResults() {
        return voteCounts; // No need to convert, already sorted
    }

    // Method to clear the results
    public synchronized void clearResults() {
        voteCounts.clear();
    }
}
