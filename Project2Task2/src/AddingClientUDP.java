import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class AddingClientUDP {
    private static DatagramSocket aSocket;
    private static InetAddress aHost;
    private static int serverPort;

    public static void main(String args[]) {
        System.out.println("The UDP Client is running.");

        try {
            // define the server's address
            aHost = InetAddress.getByName("localhost");

            // create UDP socket
            aSocket = new DatagramSocket();

            String input;

            // used to read user input
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            // ask user to enter server side port number
            System.out.print("Enter Server side port number: ");

            // set the port number where the server is listening to
            serverPort = Integer.parseInt(typed.readLine().trim());

            System.out.print("\n");

            // read and loop user input
            while (true) {
                System.out.print("Enter an integer (or type 'halt!' to quit): ");
                input = typed.readLine().trim();

                if (input.equals("halt!")) {
                    System.out.println("UDP Client side quitting.");
                    break;
                }

                try {
                    int number = Integer.parseInt(input);
                    int result = add(number); // Call the "add" method for communication
                    System.out.println("The server returned: " + result);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                }
            }
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }

    // The "add" method will encapsulate all UDP communication (Proxy Design)
    public static int add(int i) throws IOException {
        byte[] sendData = ByteBuffer.allocate(4).putInt(i).array(); // code from ChatGPT
        DatagramPacket request = new DatagramPacket(sendData, sendData.length, aHost, serverPort);
        aSocket.send(request);

        byte[] buffer = new byte[4]; // Buffer for server's response
        DatagramPacket serverResponse = new DatagramPacket(buffer, buffer.length);
        aSocket.receive(serverResponse);

        // Extract int from response
        return ByteBuffer.wrap(serverResponse.getData()).getInt(); // code from ChatGPT
    }
}