import java.net.*;
import java.io.*;
import java.util.Scanner;
import com.google.gson.Gson;

/**
 * This class implements a TCP client that interacts with a blockchain server.
 * It provides a menu-driven interface.
 */
public class ClientTCP {
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            // Connect to the server
            String serverAddress = "localhost";
            int serverPort = 7777;
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
                    sendRequest(request);
                    break;

                case 1: // Add block
                    System.out.println("Enter difficulty > 1");
                    int difficulty = Integer.parseInt(scanner.nextLine());

                    System.out.println("Enter transaction");
                    String transaction = scanner.nextLine();

                    request = RequestMessage.createAddBlockRequest(transaction, difficulty);
                    sendRequest(request);
                    break;

                case 2: // Verify chain
                    System.out.println("Verifying entire chain");
                    request = new RequestMessage(RequestMessage.VERIFY_CHAIN);
                    sendRequest(request);
                    break;

                case 3: // View chain
                    System.out.println("View the Blockchain");
                    request = new RequestMessage(RequestMessage.VIEW_CHAIN);
                    sendRequest(request);
                    break;

                case 4: // Corrupt chain
                    System.out.println("Corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int blockId = Integer.parseInt(scanner.nextLine());

                    System.out.println("Enter new data for block " + blockId);
                    String newData = scanner.nextLine();

                    request = RequestMessage.createCorruptBlockRequest(blockId, newData);
                    sendRequest(request);
                    break;

                case 5: // Repair chain
                    System.out.println("Repairing the entire chain");
                    request = new RequestMessage(RequestMessage.REPAIR_CHAIN);
                    sendRequest(request);
                    break;

                case 6: // Exit
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error processing choice: " + e.getMessage());
        }
    }

    /**
     * Send a request to the server and process the response.
     *
     * @param request The request message to send
     * @throws IOException If an I/O error occurs
     */
    private static void sendRequest(RequestMessage request) throws IOException {
        // Convert request to JSON and send to server
        String jsonRequest = gson.toJson(request);
        out.println(jsonRequest);
        out.flush();

        // Read and process the response
        String jsonResponse = in.readLine();
        ResponseMessage response = gson.fromJson(jsonResponse, ResponseMessage.class);

        displayResponse(response);
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