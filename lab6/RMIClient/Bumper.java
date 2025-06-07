import java.rmi.*;
public interface Bumper extends Remote {
    public boolean bump() throws RemoteException;
    public BigInteger get() throws RemoteException;
}