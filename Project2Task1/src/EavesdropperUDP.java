import java.net.*;
import java.io.*;

public class EavesdropperUDP {
    public static void main(String args[]) {
        System.out.println("The Eavesdropper is running.");

        DatagramSocket eavesdropperSocket = null;

        try {
            // Prompt user to enter two ports: one for listening, one for masquerading
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("Enter Eavesdropper listening port: ");
            int listenPort = Integer.parseInt(userInputReader.readLine());

            System.out.print("Enter correct port: ");
            int serverPort = Integer.parseInt(userInputReader.readLine());

            eavesdropperSocket = new DatagramSocket(listenPort);
            System.out.println("Eavesdropper listening on port: " + listenPort);

            byte[] buffer = new byte[1000];

            while (true) {
                // Receive packet from client
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                eavesdropperSocket.receive(request);

                // convert and display original message
                String clientMessage = new String(request.getData(), 0, request.getLength());
                System.out.println("Received from client: " + clientMessage);

                // Modify message if it contains the word "like"
                String modifiedMessage = modifyMessage(clientMessage);

                // Forward modified message to correct server
                byte[] forwardMessage = modifiedMessage.getBytes();
                InetAddress serverAddress = request.getAddress();
                DatagramPacket forwardPacket = new DatagramPacket(forwardMessage, forwardMessage.length, serverAddress, serverPort);
                eavesdropperSocket.send(forwardPacket);

                // Receive response from the correct server
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                eavesdropperSocket.receive(response);

                // Forward the correct server response to the client
                DatagramPacket responseToClient = new DatagramPacket(
                        response.getData(), response.getLength(),
                        request.getAddress(), request.getPort()
                );
                eavesdropperSocket.send(responseToClient);

                // Display forwarded response
                System.out.println("Forwarding response to client: " + new String(response.getData(), 0, response.getLength()));

                // Eavesdropper don't exit even if message was "halt!"
            }

        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (eavesdropperSocket != null) eavesdropperSocket.close();
        }
    }

    // Modify "like" to "dislike"
    private static String modifyMessage(String message) {
        if (message.equals("halt!")) {
            return message; // Do not modify "halt!"
        }
        return message.replaceFirst("\\blike\\b", "dislike"); // Replace "like" when it is exactly "like"
    }
}