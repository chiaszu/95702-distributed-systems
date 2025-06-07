package ds.project1task1;

import java.io.*;

import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import jakarta.xml.bind.DatatypeConverter;

@WebServlet(name = "ComputeHashes", urlPatterns = "/computeHashes")
public class ComputeHashes extends HttpServlet {
    // receive post request and send back response
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String input = request.getParameter("inputText");
        String hashAlgorithm = request.getParameter("hashAlgorithm");

        String hashRes = computeHash(input, hashAlgorithm);

        response.setContentType("text/plain");
        response.getWriter().println("Hash (" + hashAlgorithm + "): " + input + hashRes);
    }

    // function to compute hash function
    private String computeHash(String input, String hashAlgorithm) {
        try {
            // the hashAlgorithm is either MD5 or SHA-256
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);

            // computes the raw binary hash from input
            byte[] hashBytes = digest.digest(input.getBytes());

            // Convert to hexadecimal
            String hex = DatatypeConverter.printHexBinary(hashBytes);

            // Convert to Base64
            String base64 = DatatypeConverter.printBase64Binary(hashBytes);
            return "\nHex: " + hex + "\nBase64: " + base64;
        } catch (NoSuchAlgorithmException e) {
            return (e.getMessage());
        }
    }
}
