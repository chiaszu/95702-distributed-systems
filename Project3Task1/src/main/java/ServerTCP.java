import java.net.*;
import java.io.*;
import java.util.Scanner;
import com.google.gson.Gson;

/**
 * This class implements a TCP server that hosts a blockchain.
 * It processes client requests and returns appropriate responses.
 */
public class ServerTCP {
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
            int serverPort = 7777;
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