// BumperServant.java
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.math.BigInteger;

public class BumperServant extends UnicastRemoteObject implements Bumper {
    private BigInteger value;

    public BumperServant() throws RemoteException {
        // Initialize the BigInteger to 0
        value = BigInteger.ZERO;

        public boolean bump() throws RemoteException {
            // Add 1 to the BigInteger
            value = value.add(BigInteger.ONE);
            // Return true on completion
            return true;
        }

        public BigInteger get() throws RemoteException {
            // Return the current value of the BigInteger
            return value;
        }
    }
}