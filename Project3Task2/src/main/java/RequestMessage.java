import java.io.Serializable;
import java.math.BigInteger;

/**
 * This class encapsulates requests from the client to the server.
 * It follows a factory method pattern to create different types of requests.
 */
public class RequestMessage implements Serializable {
    private String operation;  // operation to perform
    private int difficulty;    // For add block operations
    private String transaction; // For add block operations
    private int blockId;       // For corrupt block operations
    private String newData;    // For corrupt block operations

    // New authentication fields
    private String clientId;          // Last 20 bytes of hash(e+n)
    private BigInteger e;             // Public key exponent
    private BigInteger n;             // Public key modulus
    private String signature;         // RSA signature

    // Basic constructor for simple operations
    public RequestMessage(String operation) {
        this.operation = operation;
    }

    // for add block operations
    public static RequestMessage createAddBlockRequest(String transaction, int difficulty) {
        RequestMessage req = new RequestMessage(ADD_BLOCK);
        req.transaction = transaction;
        req.difficulty = difficulty;
        return req;
    }

    // for corrupt block operations
    public static RequestMessage createCorruptBlockRequest(int blockId, String newData) {
        RequestMessage req = new RequestMessage(CORRUPT_CHAIN);
        req.blockId = blockId;
        req.newData = newData;
        return req;
    }

    /**
     * creates a string representation of all the fields that will be signed
     * @return String to be hashed and signed
     */
    public String getDataToSign() {
        StringBuilder sb = new StringBuilder();
        sb.append(operation);

        // Include operation-specific fields
        if (operation.equals(ADD_BLOCK)) {
            sb.append(transaction);
            sb.append(difficulty);
        } else if (operation.equals(CORRUPT_CHAIN)) {
            sb.append(blockId);
            sb.append(newData);
        }

        // Include authentication fields (except signature)
        sb.append(clientId);
        sb.append(e != null ? e.toString() : "");
        sb.append(n != null ? n.toString() : "");

        return sb.toString();
    }

    // Getters and setters
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public String getTransaction() { return transaction; }
    public void setTransaction(String transaction) { this.transaction = transaction; }

    public int getBlockId() { return blockId; }
    public void setBlockId(int blockId) { this.blockId = blockId; }

    public String getNewData() { return newData; }
    public void setNewData(String newData) { this.newData = newData; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public BigInteger getE() { return e; }
    public void setE(BigInteger e) { this.e = e; }

    public BigInteger getN() { return n; }
    public void setN(BigInteger n) { this.n = n; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    // Define operation constants
    public static final String VIEW_STATUS = "VIEW_STATUS";
    public static final String ADD_BLOCK = "ADD_BLOCK";
    public static final String VERIFY_CHAIN = "VERIFY_CHAIN";
    public static final String VIEW_CHAIN = "VIEW_CHAIN";
    public static final String CORRUPT_CHAIN = "CORRUPT_CHAIN";
    public static final String REPAIR_CHAIN = "REPAIR_CHAIN";
}