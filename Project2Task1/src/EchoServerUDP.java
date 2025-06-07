import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class EchoServerUDP {
    public static void main(String args[]) {
        System.out.println("The UDP Server is running.");

        // DatagramSocket is used for sending and receiving UDP packets
        DatagramSocket aSocket = null;

        // create buffer to store incoming data
        byte[] buffer = new byte[1000];
        try {
            // prompt the user for the port number that the server is supposed to listen on
            System.out.print("Enter Server listening port: ");

            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            int port = Integer.parseInt(userInputReader.readLine());
            System.out.println("Server is listening on port " + port);

            // create socket listening on port 6789
            aSocket = new DatagramSocket(port);

            // packet to receive message from client
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);

            while (true) {
                // wait to receive a packet from client
                aSocket.receive(request);

                // only copy correct number of bytes
                byte[] receivedData = new byte[request.getLength()];
                System.arraycopy(request.getData(), 0, receivedData, 0, request.getLength());

                // set a response packet with the data received from client
                DatagramPacket reply = new DatagramPacket(request.getData(),
                        request.getLength(), request.getAddress(), request.getPort());

                // store the message from client
                String requestString = new String(receivedData);
                // display the received message on the server console
                System.out.println("Echoing: " + requestString);

                // send the response packet back to the client
                aSocket.send(reply);

                // If "halt!" is received, send it back and terminate the server
                if (requestString.equals("halt!")) {
                    System.out.println("UDP Server side quitting.");
                    break;
                }
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
