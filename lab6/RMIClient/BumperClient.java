public class BumperClient{
    public static void main(String[] args) {
        try {
            // Look up the remote object named "bumper"
            Bumper bumper = (Bumper) Naming.lookup("rmi://localhost/bumper");

            // Initialize client-side counters
            BigInteger ctr = BigInteger.ZERO;
            BigInteger n = new BigInteger("10000");

            // Start timer
            long start = System.currentTimeMillis();

            // Loop until ctr equals n
            while (ctr.compareTo(n) < 0) {
                // Increment the client-side counter
                ctr = ctr.add(BigInteger.ONE);

                // Call the remote bump method (ignore return value)
                bumper.bump();
            }

            // End timer
            long stop = System.currentTimeMillis();

            // Get the server-side value
            BigInteger serverValue = bumper.get();

            // Calculate elapsed time in seconds
            double elapsedSeconds = (stop - start) / 1000.0;

            // Display results
            System.out.println("Server's BigInteger value: " + serverValue);
            System.out.println("Time to complete 10,000 calls: " + elapsedSeconds + " seconds");

        } catch (Exception e) {
            System.err.println("BumperClient exception: " + e.toString());
            e.printStackTrace();
        }
    }
}