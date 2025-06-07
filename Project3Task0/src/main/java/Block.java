// Chia-Szu, Kuo (chiaszuk)

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

/**
 * This class represents a simple Block.
 */
public class Block {
    private int index; // position of the block on the chain
    private Timestamp timestamp; // holds the time of the block's creation
    private String data; // holding the block's single transaction details.
    private String previousHash; // the SHA256 hash of a block's parent
    private BigInteger nonce; // nounce + block data -> produce hash
    private int difficulty;

    /**
     * Block constructor.
     *
     * @param index - This is the position within the chain. Genesis is at 0.
     * @param timestamp - This is the time this block was added.
     * @param data - This is the transaction to be included on the blockchain.
     * @param difficulty - This is the number of leftmost nibbles that need to be 0.
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = "";
        this.nonce = BigInteger.ZERO;
        this.difficulty = difficulty;
    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash,
     * nonce, and difficulty.
     *
     * @return A SHA256 hash of the block's data.
     */
    public String calculateHash() {
        String dataToHash = index + timestamp.toString() + data + previousHash + nonce.toString() + difficulty;
        MessageDigest digest;
        byte[] bytes = null;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            bytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error calculating hash: " + ex.getMessage());
        }

        // Convert byte array to hexadecimal string
        StringBuilder buffer = new StringBuilder();
        for (byte b : bytes) {
            buffer.append(String.format("%02X", b));
        }

        return buffer.toString();
    }

    /**
     * The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
     *
     * @return A hash that meets the difficulty requirement.
     */
    public String proofOfWork() {
        String target = new String(new char[difficulty]).replace('\0', '0');
        String hash = calculateHash();

        while (!hash.substring(0, difficulty).equals(target)) {
            // If the hash does not have the appropriate number of leading hex zeroes,
            // it increments the nonce by 1 and tries again
            nonce = nonce.add(BigInteger.ONE);
            hash = calculateHash();
        }

        return hash;
    }

    public String getData() {
        return data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getIndex() {
        return index;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


    public void setData(String data) {
        this.data = data;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Override Java's toString method
     */
    @Override
    public String toString() {
        return "{\"index\" : " + index + ","
                + "\"time stamp \" : \"" + timestamp + "\","
                + "\"Tx \": \"" + data + "\","
                + "\"PrevHash\" : \"" + previousHash + "\","
                + "\"nonce\" : " + nonce + ","
                + "\"difficulty\": " + difficulty + "}";
    }
}