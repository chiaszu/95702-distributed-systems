import java.io.Serializable;

/**
 * This class encapsulates responses from the server to the client.
 */
public class ResponseMessage implements Serializable {
    private boolean success;      // Whether the operation was successful
    private String operation;     // operation that was performed
    private String message;       // message to display
    private String chainStatus;
    private String chainData;
    private String verifyResult;
    private long executionTime;
    private int chainSize;

    /**
     * Constructor for creating a response message.
     *
     * @param success Whether the operation was successful
     * @param operation The operation that was performed
     */
    public ResponseMessage(boolean success, String operation) {
        this.success = success;
        this.operation = operation;
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getChainStatus() { return chainStatus; }
    public void setChainStatus(String chainStatus) { this.chainStatus = chainStatus; }

    public String getChainData() { return chainData; }
    public void setChainData(String chainData) { this.chainData = chainData; }

    public String getVerifyResult() { return verifyResult; }
    public void setVerifyResult(String verifyResult) { this.verifyResult = verifyResult; }

    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public int getChainSize() { return chainSize; }
    public void setChainSize(int chainSize) { this.chainSize = chainSize; }
}