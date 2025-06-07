// Working with deadlock.
public class DeadLockLabPart1 {

    final Object resource1 = new Object();
    final Object resource2 = new Object();

    final int n = 100;

    Thread t1 = new Thread( new Runnable() {
        public void run() {
            // Only one thread can execute a synchronized method or block at a time for the same object
            // It establishes a happens-before relationship, which ensures that changes made by one thread are visible to other threads
            synchronized (resource1) {
                for (int x = 1; x <= n; x++) {
                }
                synchronized (resource2) {
                    System.out.println();
                    for (int i = 1; i <= 10; i++) {
                        System.out.print(" t1: " + i);
                    }

                }
            }
        }
    });

    Thread t2 = new Thread( new Runnable() {
        public void run() {
            synchronized (resource2) {
                for (int x = 1; x <= n; x++) {
                }
                synchronized (resource1) {
                    System.out.println();
                    for (int i = 1; i <= 10; i++) {
                        System.out.print(" t2: " + i);

                    }
                }
            }
        }
    });

    public void foo() {
        // Start up two threads that may become deadlocked.
        t1.start();
        t2.start();
    }
    public static void main(String[] args) {
        System.out.println("About to startup and run two threads.");
        new DeadLockLabPart1().foo();
        System.out.println("Startup complete but threads may still be running.");
    }
}
