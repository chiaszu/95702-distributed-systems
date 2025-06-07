import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class AddingServerUDP {
    private static int sum = 0;

    public static void main(String args[]) {
        System.out.println("The UDP Server is running.");

        // DatagramSocket is used for sending and receiving UDP packets
        DatagramSocket aSocket = null;

        // create buffer to store incoming data
        byte[] buffer = new byte[4]; // to store int
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

                // received byte data and convert to integer
                int receivedNumber = ByteBuffer.wrap(request.getData()).getInt(); // code from Chatgpt

                // Update sum
                sum += receivedNumber;
                System.out.println("Adding: " + receivedNumber + " to sum.\nReturning sum of " + sum + " to client");

                byte[] receivedData = new byte[request.getLength()];
                System.arraycopy(request.getData(), 0, receivedData, 0, request.getLength());

                // Convert updated sum to byte array
                byte[] responseData = ByteBuffer.allocate(4).putInt(sum).array(); // code from Chatgpt

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
