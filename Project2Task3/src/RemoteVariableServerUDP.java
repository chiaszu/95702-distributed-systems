import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.TreeMap;

public class RemoteVariableServerUDP {
    private static final TreeMap<Integer, Integer> userSums = new TreeMap<>(); // store sum by users

    public static void main(String args[]) {
        System.out.println("The UDP Server is running.");

        // DatagramSocket is used for sending and receiving UDP packets
        DatagramSocket aSocket = null;

        // create buffer to store incoming data
        byte[] buffer = new byte[12]; // store id, operation and input
        try {
            // prompt the user for the port number that the server is supposed to listen on
            System.out.print("Enter Server listening port: ");

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            int port = Integer.parseInt(userInputReader.readLine());
            System.out.println("Server is listening on port " + port);

            // create socket listening on port 6789
            aSocket = new DatagramSocket(port);

            while (true) {
                // packet to receive message from client
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                // wait to receive a packet from client
                aSocket.receive(request);

                // received byte data and assign to operation, input number and id
                // ByteBuffer keeps track of where it’s reading from internally, so every time you call getInt(), it moves forward by 4 bytes.
                ByteBuffer receivedInfo = ByteBuffer.wrap(request.getData()); // code from Chatgpt
                int userId = receivedInfo.getInt();
                int operation = receivedInfo.getInt();
                int value = receivedInfo.getInt();

                // Get current sum or initialize it if new user
                int currentSum = userSums.getOrDefault(userId, 0);

                // Process operation
                if (operation == 1) { // Add
                    currentSum += value;
                } else if (operation == 2) { // Subtract
                    currentSum -= value;
                }
                userSums.put(userId, currentSum); // Update sum

                // Print result
                System.out.println("User ID: " + userId +
                        ", Operation: " + operation +
                        ", Value: " + value +
                        ", New sum: " + currentSum);

                // Convert updated sum to byte array
                byte[] responseData = ByteBuffer.allocate(4).putInt(currentSum).array(); // code from Chatgpt

                // set a response packet with the data received from client
                DatagramPacket reply = new DatagramPacket(responseData, responseData.length,
                        request.getAddress(), request.getPort());

                // send the response packet back to the client
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }
}
