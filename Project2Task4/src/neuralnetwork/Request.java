package neuralnetwork;

// refer to Appendix - working with gson in IntelliJ.
public class Request {
    String request;
    Double val1, val2, val3, val4; // Supports "setCurrentRange"
    Integer iterations; // Used only for "train"

    // Default constructor
    public Request() {
    }

    public Request(String request, Double val1, Double val2, Double val3, Double val4, Integer iterations) {
        this.request = request;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
        this.iterations = iterations;
    }
}
