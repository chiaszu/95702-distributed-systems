import java.net.*;
import java.io.*;

public class EchoClientUDP {
    public static void main(String args[]) {
        System.out.println("The UDP client is running.");

        // args give message contents and server hostname
        // DatagramSocket is used for sending and receiving UDP packets
        DatagramSocket aSocket = null;
        try {
            // define the server's address
            InetAddress aHost = InetAddress.getByName("localhost");

            // create UDP socket
            aSocket = new DatagramSocket();

            String nextLine;

            // used to read user input
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));

            // ask user to enter server side port number
            System.out.print("Enter server side port number: ");

            // set the port number where the server is listening to
            int serverPort = Integer.parseInt(typed.readLine().trim());

            // read and loop user input
            while ((nextLine = typed.readLine()) != null) {
                // convert user input into byte array
                byte[] m = nextLine.getBytes();

                // create UDP packet
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);

                // send packet to server
                aSocket.send(request);

                // if user enter helt
                if (nextLine.equals("halt!")) {
                    // create buffer and packet to receive response from server
                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

                    // receive response from server
                    aSocket.receive(reply);

                    // only copy correct number of bytes
                    String responseString = new String(reply.getData(), 0, reply.getLength());
                    System.out.println("Reply from server: " + new String(responseString));

                    if (responseString.equals("halt!")) {
                        System.out.println("UDP Client side quitting.");
                        break;
                    }
                } else {
                    // Normal case: Receive response from server
                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);

                    String responseString = new String(reply.getData(), 0, reply.getLength());
                    System.out.println("Reply from server: " + responseString);
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
}