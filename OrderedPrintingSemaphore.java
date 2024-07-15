import java.util.concurrent.Semaphore;

public class OrderedPrintingSemaphore {
    public static void main(String[] args) {
        Semaphore sem1 = new Semaphore(0);
        Semaphore sem2 = new Semaphore(0);

        Thread t1 = new Thread(() -> {
            while(true){
                System.out.println("First thread executing.");
                sem1.release(); 
            }
            
        });

        Thread t2 = new Thread(() -> {
            while(true){
                try {
                    sem1.acquire();  // Wait for t1 to finish
                    System.out.println("Second thread executing.");
                    sem2.release();  // Signal t3 to proceed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
        });

        Thread t3 = new Thread(() -> {
            while(true){
                try {
                    sem2.acquire();  // Wait for t2 to finish
                    System.out.println("Third thread executing.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
        });

        t3.start();  // Start t3 first (it will wait)
        t2.start();  // Start t2 second (it will wait)
        t1.start();  // Start t1 last (it will execute immediately)
    }
}
