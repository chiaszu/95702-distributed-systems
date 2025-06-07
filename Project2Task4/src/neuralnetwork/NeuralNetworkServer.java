package neuralnetwork;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets; // Import for encoding JSON
import java.util.*;

import com.google.gson.*; // Import for JSON handling


public class NeuralNetworkServer {
    private static Gson gson = new Gson();
    private static NeuralNetwork neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null); // Create the NN
    private static ArrayList<Double[][]> userTrainingSets = new ArrayList<>(Arrays.asList(
            new Double[][]{{0.0, 0.0}, {0.0}},
            new Double[][]{{0.0, 1.0}, {0.0}},
            new Double[][]{{1.0, 0.0}, {0.0}},
            new Double[][]{{1.0, 1.0}, {0.0}}
    ));

    public static void main(String args[]) {
        System.out.println("The Neural Network Server is running.");

        DatagramSocket socket = null;
        try {
            // Prompt for port number
            System.out.print("Enter server listening port: ");
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            int port = Integer.parseInt(userInputReader.readLine());

            socket = new DatagramSocket(port);
            System.out.println("Server is listening on port " + port);

            byte[] buffer = new byte[1500];

            while (true) {
                // raw data received from client
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(requestPacket);

                // Convert client request to JSON string
                String receivedJson = new String(requestPacket.getData(), 0, requestPacket.getLength(), StandardCharsets.UTF_8);

                System.out.println("received JSON from client: " + receivedJson);

                // Convert JSON string to Request Object
                Request request = gson.fromJson(receivedJson, Request.class);

                Response response = processRequest(request);

                // Convert response to JSON String to send to client
                String jsonResponse = gson.toJson(response);

                System.out.println("sending JSON to client: " + jsonResponse);

                // Convert JSON String to raw data
                byte[] responseData = jsonResponse.getBytes(StandardCharsets.UTF_8);

                // send JSON response to client
                DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                        requestPacket.getAddress(), requestPacket.getPort());
                socket.send(responsePacket);
            }

        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (socket != null) socket.close();
        }
    }

    // Process Client Request and Generate Response
    // request refer to example from step 8.
    private static Response processRequest(Request request) {
        switch (request.request) {
            case "getCurrentRange":
                StringBuilder sb = new StringBuilder();
                sb.append("Working with the following truth table\n");
                for (int r = 0; r < 4; r++) {
                    sb.append(userTrainingSets.get(r)[0][0]).append("  ")
                            .append(userTrainingSets.get(r)[0][1]).append("  ")
                            .append(userTrainingSets.get(r)[1][0]).append("\n");
                }
                String res = sb.toString();
                return new Response("getCurrentRange", "OK", 0.1, 0.2, 0.3, 0.4, res);

            case "setCurrentRange":
                // Set a new truth table using the provided four values and reinitialize the neural network
                userTrainingSets = new ArrayList<>(Arrays.asList(
                        new Double[][]{{0.0, 0.0}, {request.val1}},
                        new Double[][]{{0.0, 1.0}, {request.val2}},
                        new Double[][]{{1.0, 0.0}, {request.val3}},
                        new Double[][]{{1.0, 1.0}, {request.val4}}
                ));
                neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);
                return new Response("setCurrentRange", "OK");

            case "train":
                int steps = (request.iterations == null) ? 1 : request.iterations;
                for (int i = 0; i < steps; i++) {
                    singleTrainStep();
                }
                double totalError = neuralNetwork.calculateTotalError(userTrainingSets);
                String msgTrain = "After this step the error is : " + totalError;
                return new Response("train", "OK", msgTrain);

            case "test":
                List<Double> inputs = Arrays.asList(request.val1, request.val2);
                List<Double> output = neuralNetwork.feedForward(inputs);
                double testResult = output.get(0);
                String msgTest = "The range value is approximately " + testResult;
                return new Response("test", "OK", msgTest);

            default:
                return new Response("error", "Invalid request");
        }
    }

    // refer to main method in NeuralNetwork.java
    private static void singleTrainStep() {
        // Create a new Random object for generating random numbers.
        Random rand = new Random();

        int randomIndex = rand.nextInt(4);

        List<Double> trainingInputs = Arrays.asList(userTrainingSets.get(randomIndex)[0]);

        List<Double> trainingOutputs = Arrays.asList(userTrainingSets.get(randomIndex)[1]);

        neuralNetwork.train(trainingInputs, trainingOutputs);
    }
}