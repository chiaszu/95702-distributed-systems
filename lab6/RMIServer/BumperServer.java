// BumperServer.java
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class BumperServer {
    public static void main(String[] args) {
        try {
            // Create an instance of the remote object
            BumperServant bumperServant = new BumperServant();

            // Start the RMI registry on the default port (1099)
            LocateRegistry.createRegistry(1099);

            // Bind the remote object's stub in the registry with name "bumper"
            Naming.rebind("bumper", bumperServant);

            System.out.println("BumperServer ready");
        } catch (Exception e) {
            System.err.println("BumperServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}