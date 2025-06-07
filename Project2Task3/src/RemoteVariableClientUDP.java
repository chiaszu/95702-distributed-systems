import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class RemoteVariableClientUDP {
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
                System.out.println("\n1. Add a value to your sum.");
                System.out.println("2. Subtract a value from your sum.");
                System.out.println("3. Get your sum.");
                System.out.println("4. Exit client.");
                System.out.print("Enter choice: ");

                int choice = 0;
                try {
                    choice = Integer.parseInt(typed.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter an integer.");
                }

                if (choice == 4) {
                    System.out.println("Client side quitting. The remote variable server is still running.");
                    break;
                }

                int value = 0;
                if (choice == 1 || choice == 2) {
                    System.out.print("Enter value: ");
                    try {
                        value = Integer.parseInt(typed.readLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter an integer.");
                        continue;
                    }
                }

                System.out.print("Enter your ID: ");
                int userId;
                try {
                    userId = Integer.parseInt(typed.readLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid ID. Please enter an integer between 0-999.");
                    continue;
                }

                int result = sendRequest(userId, choice, value);
                System.out.println("The result is: " + result);
            }
        } catch (SocketException e) {
            System.out.println("Socket Exception: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }

    private static int sendRequest(int userId, int choice, int value) throws IOException {
        byte[] sendData = ByteBuffer.allocate(12).putInt(userId).putInt(choice).putInt(value).array(); // code from ChatGPT
        DatagramPacket request = new DatagramPacket(sendData, sendData.length, aHost, serverPort);
        aSocket.send(request);

        byte[] buffer = new byte[4]; // Buffer for response
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        aSocket.receive(reply);

        return ByteBuffer.wrap(reply.getData()).getInt(); // Extract int from response
    }
}