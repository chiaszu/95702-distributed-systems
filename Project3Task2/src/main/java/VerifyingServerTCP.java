import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * This class implements a TCP server that hosts a blockchain.
 * It processes client requests and returns appropriate responses.
 */
public class VerifyingServerTCP {
    private static BlockChain blockchain;

    public static void main(String[] args) {
        // Initialize the blockchain with a genesis block
        blockchain = new BlockChain();
        blockchain.computeHashesPerSecond();

        // Create genesis block with difficulty 2
        Block genesisBlock = new Block(0, blockchain.getTime(), "Genesis", 2);
        blockchain.addBlock(genesisBlock);

        Socket clientSocket = null;
        try {
            int serverPort = 8888;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("Blockchain server running on port " + serverPort);

            // Listen for client connections indefinitely
            while (true) {
                clientSocket = listenSocket.accept();
                System.out.println("\nWe have a visitor");

                // Set up reader for client messages
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Set up writer for server responses
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(clientSocket.getOutputStream())));

                Gson gson = new Gson();

                // Process client requests
                String line;
                while ((line = in.readLine()) != null) {
                    // Parse the JSON request
                    System.out.println("THE JSON REQUEST MESSAGE IS:");
                    System.out.println(line);

                    RequestMessage request = gson.fromJson(line, RequestMessage.class);
                    ResponseMessage response = processRequest(request);

                    boolean isAuthenticated = verifyClientAuthentication(request);

                    if (isAuthenticated) {
                        response = processRequest(request);
                    } else {
                        response = new ResponseMessage(false, request.getOperation());
                        response.setMessage("Error in request: Authentication failed");
                    }

                    // Convert response to JSON and send back to client
                    String responseJson = gson.toJson(response);
                    System.out.println("THE JSON RESPONSE MESSAGE IS:");
                    System.out.println(responseJson);
                    System.out.println("Number of Blocks on Chain == " + blockchain.getChainSize());

                    out.println(responseJson);
                    out.flush();
                }

                // Close the current client connection
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * Verify client authentication by:
     * 1. Does the public key hash to the ID
     * 2. Is the request properly signed?
     */
    private static boolean verifyClientAuthentication(RequestMessage request) {
        try {
            // Get authentication fields
            String clientId = request.getClientId();
            BigInteger e = request.getE();
            BigInteger n = request.getN();
            String signature = request.getSignature();

            if (clientId == null || e == null || n == null || signature == null) {
                System.out.println("Authentication failed: Missing authentication fields");
                return false;
            }

            // 1. Does the public key hash to the ID
            String expectedId = computeClientId(e, n);
            if (!clientId.equals(expectedId)) {
                System.out.println("Authentication failed: Client ID verification failed");
                return false;
            }

            // 2. Is the request properly signed?
            boolean signatureValid = verifySignature(request);
            if (!signatureValid) {
                System.out.println("Authentication failed: Signature verification failed");
                return false;
            }

            System.out.println("Client authentication successful");
            return true;

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Compute client ID from public key (e, n)
     * Takes the last 20 bytes of the SHA-256 hash of e+n
     */
    private static String computeClientId(BigInteger e, BigInteger n) throws Exception {
        // Concatenate e and n as strings
        String combined = e.toString() + n.toString();

        // Compute SHA-256 hash
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(combined.getBytes("UTF-8"));

        // Take last 20 bytes and convert to hex string
        StringBuilder hexString = new StringBuilder();
        for (int i = hashBytes.length - 20; i < hashBytes.length; i++) {
            String hex = Integer.toHexString(0xff & hashBytes[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    /**
     * Verify the request signature, refer to ShortMessageVerify
     */
    private static boolean verifySignature(RequestMessage request) throws Exception {
        // Get data that was signed
        String dataToVerify = request.getDataToSign();

        // Hash the data using SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(dataToVerify.getBytes("UTF-8"));

        // Add zero byte to make hash positive (RSA works only with positive numbers)
        byte[] positiveHash = new byte[hashBytes.length + 1];
        positiveHash[0] = 0; // Most significant byte set to 0
        System.arraycopy(hashBytes, 0, positiveHash, 1, hashBytes.length);

        // Convert to BigInteger
        BigInteger hashBigInt = new BigInteger(positiveHash);

        // Convert signature string to BigInteger
        BigInteger signatureBigInt = new BigInteger(request.getSignature());

        // Decrypt signature using public key (e, n)
        BigInteger decryptedHash = signatureBigInt.modPow(request.getE(), request.getN());

        // Compare decrypted hash with computed hash
        return decryptedHash.equals(hashBigInt);
    }

    /**
     * Process client requests and generate appropriate responses.
     *
     * @param request The client request message
     * @return A response message to send back to the client
     */
    private static ResponseMessage processRequest(RequestMessage request) {
        ResponseMessage response = new ResponseMessage(true, request.getOperation());
        response.setChainSize(blockchain.getChainSize());

        long startTime, endTime;

        try {
            switch (request.getOperation()) {
                case RequestMessage.VIEW_STATUS:
                    StringBuilder status = new StringBuilder();
                    status.append("Current size of chain: ").append(blockchain.getChainSize()).append("\n");
                    status.append("Difficulty of most recent block: ").append(blockchain.getLatestBlock().getDifficulty()).append("\n");
                    status.append("Total difficulty for all blocks: ").append(blockchain.getTotalDifficulty()).append("\n");
                    status.append("Approximate hashes per second on this machine: ").append(blockchain.getHashesPerSecond()).append("\n");
                    status.append("Expected total hashes required for the whole chain: ").append(blockchain.getTotalExpectedHashes()).append("\n");
                    status.append("Nonce for most recent block: ").append(blockchain.getLatestBlock().getNonce()).append("\n");
                    status.append("Chain hash: ").append(blockchain.getChainHash());

                    response.setChainStatus(status.toString());
                    break;

                case RequestMessage.ADD_BLOCK:
                    startTime = System.currentTimeMillis();
                    Block newBlock = new Block(blockchain.getChainSize(), blockchain.getTime(),
                            request.getTransaction(), request.getDifficulty());
                    blockchain.addBlock(newBlock);
                    endTime = System.currentTimeMillis();

                    response.setExecutionTime(endTime - startTime);
                    response.setMessage("Block added successfully");
                    break;

                case RequestMessage.VERIFY_CHAIN:
                    startTime = System.currentTimeMillis();
                    String verifyResult = blockchain.isChainValid();
                    endTime = System.currentTimeMillis();

                    response.setVerifyResult(verifyResult);
                    response.setExecutionTime(endTime - startTime);
                    break;

                case RequestMessage.VIEW_CHAIN:
                    response.setChainData(blockchain.toString());
                    break;

                case RequestMessage.CORRUPT_CHAIN:
                    Block blockToCorrupt = blockchain.getBlock(request.getBlockId());
                    if (blockToCorrupt != null) {
                        blockToCorrupt.setData(request.getNewData());
                        response.setMessage("Block " + request.getBlockId() + " corrupted with data: " + request.getNewData());
                    } else {
                        response.setSuccess(false);
                        response.setMessage("Invalid block ID");
                    }
                    break;

                case RequestMessage.REPAIR_CHAIN:
                    startTime = System.currentTimeMillis();
                    blockchain.repairChain();
                    endTime = System.currentTimeMillis();

                    response.setExecutionTime(endTime - startTime);
                    response.setMessage("Chain repaired successfully");
                    break;

                default:
                    response.setSuccess(false);
                    response.setMessage("Unknown operation: " + request.getOperation());
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error processing request: " + e.getMessage());
        }

        return response;
    }
}