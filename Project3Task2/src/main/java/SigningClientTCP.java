import com.google.gson.Gson;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Random;
import java.util.Scanner;

/**
 * This class implements a TCP client that interacts with a blockchain server.
 * It provides a menu-driven interface.
 */
public class SigningClientTCP {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Gson gson = new Gson();

    // RSA key components
    private static BigInteger n, e, d;
    private static String clientId;

    public static void main(String[] args) {
        // Generate RSA keys
        try {
            generateRSAKeys();

            // Display keys to user
            System.out.println("RSA Keys:");
            System.out.println("Public Key (e): " + e);
            System.out.println("Public Key (n): " + n);
            System.out.println("Private Key (d): " + d);
            System.out.println("Client ID: " + clientId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Connect to the server
            String serverAddress = "localhost";
            int serverPort = 8888;
            socket = new Socket(serverAddress, serverPort);

            // Set up reader and writer
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));

            // User interface
            Scanner scanner = new Scanner(System.in);
            int choice = 0;

            while (choice != 6) {
                displayMenu();

                try {
                    choice = Integer.parseInt(scanner.nextLine());
                    processChoice(choice, scanner);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                }
            }

            scanner.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        }
    }

    // Generate RSA keys (from RSAExample.java)
    private static void generateRSAKeys() throws Exception {
        Random rnd = new Random();
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);
        n = p.multiply(q);
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
        e = new BigInteger("65537");
        d = e.modInverse(phi);

        // Generate client ID from public key
        clientId = computeClientId(e, n);
    }

    /**
     * Compute client ID from public key (e, n)
     * Takes the last 20 bytes of the SHA-256 hash of e+n
     */
    private static String computeClientId(BigInteger e, BigInteger n) throws Exception {
        // Combine e+n
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
     * Sign data using the private key (d, n)
     * refer to ShortMessageSign.java
     */
    private static String signData(String data) throws Exception {
        // Compute SHA-256 hash of data
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = md.digest(data.getBytes("UTF-8"));

        // Add zero byte to make hash positive (RSA works only with positive numbers), refer to LLM
        byte[] positiveHash = new byte[hashBytes.length + 1];
        positiveHash[0] = 0;  // Most significant byte set to 0
        System.arraycopy(hashBytes, 0, positiveHash, 1, hashBytes.length);

        // Convert to BigInteger
        BigInteger hashBigInt = new BigInteger(positiveHash);

        // Sign by encrypting with private key (d, n)
        BigInteger signature = hashBigInt.modPow(d, n);

        return signature.toString();
    }

    // Sign and send requests
    private static void sendSignedRequest(RequestMessage request) throws Exception {
        // Add authentication fields to request (except signature)
        request.setClientId(clientId);
        request.setE(e);
        request.setN(n);

        // Generate data to sign and create signature
        String dataToSign = request.getDataToSign();
        String signature = signData(dataToSign);
        request.setSignature(signature);

        // Convert request to JSON and send to server
        String jsonRequest = gson.toJson(request);
        System.out.println("Sending signed request to server...");
        out.println(jsonRequest);
        out.flush();

        // Read and process the response
        String jsonResponse = in.readLine();
        ResponseMessage response = gson.fromJson(jsonResponse, ResponseMessage.class);

        displayResponse(response);
    }

    /**
     * Display the menu options to the user.
     */
    private static void displayMenu() {
        System.out.println("0. View basic blockchain status.");
        System.out.println("1. Add a transaction to the blockchain.");
        System.out.println("2. Verify the blockchain.");
        System.out.println("3. View the blockchain.");
        System.out.println("4. Corrupt the chain.");
        System.out.println("5. Hide the corruption by recomputing hashes.");
        System.out.println("6. Exit");
    }

    /**
     * Process user's menu choice.
     *
     * @param choice The user's selection
     * @param scanner Scanner for reading user input
     */
    private static void processChoice(int choice, Scanner scanner) {
        RequestMessage request = null;

        try {
            switch (choice) {
                case 0: // View status
                    request = new RequestMessage(RequestMessage.VIEW_STATUS);
                    sendSignedRequest(request);
                    break;

                case 1: // Add block
                    System.out.println("Enter difficulty > 1");
                    int difficulty = Integer.parseInt(scanner.nextLine());

                    System.out.println("Enter transaction");
                    String transaction = scanner.nextLine();

                    request = RequestMessage.createAddBlockRequest(transaction, difficulty);
                    sendSignedRequest(request);
                    break;

                case 2: // Verify chain
                    System.out.println("Verifying entire chain");
                    request = new RequestMessage(RequestMessage.VERIFY_CHAIN);
                    sendSignedRequest(request);
                    break;

                case 3: // View chain
                    System.out.println("View the Blockchain");
                    request = new RequestMessage(RequestMessage.VIEW_CHAIN);
                    sendSignedRequest(request);
                    break;

                case 4: // Corrupt chain
                    System.out.println("Corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int blockId = Integer.parseInt(scanner.nextLine());

                    System.out.println("Enter new data for block " + blockId);
                    String newData = scanner.nextLine();

                    request = RequestMessage.createCorruptBlockRequest(blockId, newData);
                    sendSignedRequest(request);
                    break;

                case 5: // Repair chain
                    System.out.println("Repairing the entire chain");
                    request = new RequestMessage(RequestMessage.REPAIR_CHAIN);
                    sendSignedRequest(request);
                    break;

                case 6: // Exit
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error processing choice: " + e.getMessage());
        }
    }


    /**
     * Display the server's response to the user.
     *
     * @param response The response message from the server
     */
    private static void displayResponse(ResponseMessage response) {
        if (!response.isSuccess()) {
            System.out.println("Error: " + response.getMessage());
            return;
        }

        switch (response.getOperation()) {
            case RequestMessage.VIEW_STATUS:
                System.out.println(response.getChainStatus());
                break;

            case RequestMessage.ADD_BLOCK:
                System.out.println("Total execution time to add this block was " +
                        response.getExecutionTime() + " milliseconds");
                break;

            case RequestMessage.VERIFY_CHAIN:
                System.out.println("Chain verification: " + response.getVerifyResult());
                System.out.println("Total execution time required to verify the chain was " +
                        response.getExecutionTime() + " milliseconds");
                break;

            case RequestMessage.VIEW_CHAIN:
                System.out.println(response.getChainData());
                break;

            case RequestMessage.CORRUPT_CHAIN:
                System.out.println(response.getMessage());
                break;

            case RequestMessage.REPAIR_CHAIN:
                System.out.println("Total execution time required to repair the chain was " +
                        response.getExecutionTime() + " milliseconds");
                break;
        }
    }
}