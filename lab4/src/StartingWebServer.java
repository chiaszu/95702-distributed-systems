// Lab 4 - HTTP Server Lab
// We will be making the necessary modifications to this code so that it is able
// to respond to two requests from a browser.
// One request will be for http://localhost:7777/index.html and another request
// will be for http://localhost:7777/test.html.
// This server will also work if the user enters a request for a file that is not available.


import java.net.*;
import java.io.*;

// StartingWebServer is a web server that needs to be modified
// for the HTTP Server Lab.

public class StartingWebServer {

    public static void main(String args[]) {
        Socket clientSocket = null;
        try {
            int serverPort = 7777; // the port that this server will listen on

            // Create a new server socket on the port
            ServerSocket listenSocket = new ServerSocket(serverPort);

            // Handle mulitple visits
            while (true) {

                // block and wait for a visit
                clientSocket = listenSocket.accept();

                // display on the server console
                System.out.println("We have a visit");

                // Set up "inFromSocket" to read from the client socket
                BufferedReader inFromSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Set up "out" to write to the client socket
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

                // read a line of HTTP from the TCP socket
                String data = inFromSocket.readLine();

                // Do we have a GET request
                if (data != null && data.startsWith("GET")) {

                    // extract file name from the request
                    String[] tokens = data.split(" ");
                    String requestedFile = tokens[1].substring(1);

                    // Now requestedFile contains the file name
                    System.out.println("The file is " + requestedFile);
                    BufferedReader fileIn = null;
                    try {
                        // open the file
                        File file = new File(requestedFile);
                        fileIn = new BufferedReader(new FileReader(file));

                        System.out.println("We need to send back response headers now.");

                        System.out.println("We need to send back file data now.");

                        // The next 7 lines are just for startup. Remove
                        // these lines 7 when the lab is complete.
                        System.out.println("This program is not yet complete, so send back an HTTP 404 error.");
                        System.out.println("No such file");
                        out.println("HTTP/1.1 404 Not Found");
                        out.println("Content-Type: text/html");
                        out.println("Connection: close");
                        out.println();
                        out.println("<html><body><h1>File Not Found</h1></body></html>");

                    } catch (FileNotFoundException e) {

                        System.out.println("No such file");
                        out.println("HTTP/1.1 404 Not Found");
                        out.println("Content-Type: text/html");
                        out.println("Connection: close");
                        out.println();
                        out.println("<html><body><h1>File Not Found</h1></body></html>");
                    } finally {
                        if (fileIn != null) {
                            fileIn.close();
                        }
                    }
                }
                System.out.println("Closing ");
                out.flush();
                out.close();
                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException ");
            }
        }
    }
}
