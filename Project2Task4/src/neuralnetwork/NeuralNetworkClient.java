package neuralnetwork;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets; // Import for encoding JSON

import com.google.gson.*; // Import for JSON handling

public class NeuralNetworkClient {
    private static DatagramSocket socket;
    private static InetAddress serverAddress;
    private static int serverPort;
    private static Gson gson = new Gson();

    public static void main(String args[]) {
        System.out.println("The Neural Network Client is running.");

        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost");

            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            // Ask user to enter the server's port number
            System.out.print("Enter server side port number: ");
            serverPort = Integer.parseInt(typed.readLine());

            while (true) {
                System.out.println("\nUsing a neural network to learn a truth table.\n");
                System.out.println("Main Menu");
                System.out.println("0. Get current range.");
                System.out.println("1. Set truth table values.");
                System.out.println("2. Perform a single training step.");
                System.out.println("3. Perform multiple training steps.");
                System.out.println("4. Test with inputs.");
                System.out.println("5. Exit client.");
                System.out.print("Enter choice: ");

                int choice = Integer.parseInt(typed.readLine());

                if (choice == 5) {
                    System.out.println("Client side quitting.");
                    break;
                }

                //JsonObject requestObj = new JsonObject();
                Request req = null;
                switch (choice) {
                    case 0:
                        // addProperty is used to manually create JSON Object
                        // requestObj.addProperty("request", "getCurrentRange");
                        req = new Request("getCurrentRange", null, null, null, null, null);
                        break;
                    case 1:
                        System.out.println("Enter the four results of a 4 by 2 truth table (e.g., 0 1 1 0):");
                        String[] params = typed.readLine().split("\\s+");
                        req = new Request("setCurrentRange",
                                Double.parseDouble(params[0]),
                                Double.parseDouble(params[1]),
                                Double.parseDouble(params[2]),
                                Double.parseDouble(params[3]),
                                null);
                        break;
                    case 2:
                        // Single training step: iterations = 1
                        req = new Request("train", null, null, null, null, 1);
                        break;
                    case 3:
                        // Multiple training steps: ask for number of iterations
                        System.out.println("Enter the number of training sets.");
                        int n = Integer.parseInt(typed.readLine());
                        req = new Request("train", null, null, null, null, n);
                        break;
                    case 4:
                        // test with a pair of inputs
                        System.out.println("Enter a pair of doubles from a row of the truth table:");
                        String[] pair = typed.readLine().split("\\s+");
                        req = new Request("test",
                                Double.parseDouble(pair[0]),
                                Double.parseDouble(pair[1]),
                                null,
                                null,
                                null);
                        break;
                    default:
                        System.out.println("Invalid choice. Try again.");
                        continue;
                }

                // Send request to server
                String jsonRequest = gson.toJson(req);
                byte[] requestData = jsonRequest.getBytes(StandardCharsets.UTF_8);
                DatagramPacket requestPacket = new DatagramPacket(requestData, requestData.length, serverAddress, serverPort);
                socket.send(requestPacket);

                // Receive response from server
                byte[] buffer = new byte[1500];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                socket.receive(reply);

                String jsonResponse = new String(reply.getData(), 0, reply.getLength(), StandardCharsets.UTF_8);
                Response res = gson.fromJson(jsonResponse, Response.class);
                if (res.result != null) {
                    System.out.println("The result is: " + res.result);
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }
}