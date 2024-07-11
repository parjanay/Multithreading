import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MultithreadedTokenBucketFilter {
    private int availableTokens = 0;
    private final Condition newTokenAdded;
    private final int MAX_TOKEN;

    private final ReentrantLock addTokenLock = new ReentrantLock();
    private final ReentrantLock consumeTokenLock = new ReentrantLock();
    
    void daemonThread(){
        while (true) {
            try {
                addTokenLock.lock();
                if(availableTokens < MAX_TOKEN){
                    this.availableTokens = this.availableTokens + 1 ;
                    System.out.println("New token added by " + Thread.currentThread().getName() + " at: " + System.currentTimeMillis());
                    newTokenAdded.signalAll();
                }else{
                    this.availableTokens = MAX_TOKEN;
                    System.out.println("Already Full Token capacity");
                }
            } finally {
                addTokenLock.unlock();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    MultithreadedTokenBucketFilter(int maxToken) {
        this.MAX_TOKEN = maxToken;
        newTokenAdded = addTokenLock.newCondition();
        
        Thread tokenProducer = new Thread(() -> {
            daemonThread();
        });
        // Identify a daemon thread use case
        tokenProducer.setDaemon(true);
        tokenProducer.start();
    }

    public void getToken() {
        try {
            consumeTokenLock.lock();
            while (availableTokens == 0) {
                System.out.println("No Token Available for " + Thread.currentThread().getName());
                try {
                    newTokenAdded.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            availableTokens--;
            System.out.println("Using the token for Thread: " + Thread.currentThread().getName() + " Starting its work at: " + System.currentTimeMillis());
            System.out.println("Execution completed for Thread: " + Thread.currentThread().getName()+" at: "+System.currentTimeMillis());
            System.out.println("------------------------------------");
        } finally {
            consumeTokenLock.unlock();
        }
        
    }

    public void test_10ThreadsTryingToGetToken() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                try {
                    getToken();
                } catch (Exception e) {
                    System.out.println("Caught an exception for Thread " + Thread.currentThread().getName());
                }
            });
            t.setName("Thread_" + (i + 1));
            allThreads.add(t);
        }
        for (Thread t : allThreads) {
            t.start();
            //Thread.sleep(500);
        }
        for (Thread t : allThreads) {
            t.join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MultithreadedTokenBucketFilter mtbf = new MultithreadedTokenBucketFilter(5);
        Thread.sleep(10000);
        mtbf.test_10ThreadsTryingToGetToken();
    }
}
