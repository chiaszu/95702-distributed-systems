package neuralnetwork;

// refer to Appendix - working with gson in IntelliJ.
public class Response {
    String response;
    String status;
    Double val1, val2, val3, val4; // Supports "getCurrentRange"
    String result;

    public Response() {
    } // Default constructor

    public Response(String response, String status, Double val1, Double val2, Double val3, Double val4, String result) {
        this.response = response;
        this.status = status;
        this.val1 = val1;
        this.val2 = val2;
        this.val3 = val3;
        this.val4 = val4;
        this.result = result;
    }

    public Response(String response, String status, Double val1) {
        this.response = response;
        this.status = status;
        this.val1 = val1;
    }

    public Response(String response, String status, String result) {
        this.response = response;
        this.status = status;
        this.result = result;
    }

    public Response(String response, String status) {
        this.response = response;
        this.status = status;
    }
}
