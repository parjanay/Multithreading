import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeDeferredCallback {

    PriorityQueue<Callback> queue = new PriorityQueue<>((o1, o2) -> Long.compare(o1.expectedTime, o2.expectedTime));

    ReentrantLock lock = new ReentrantLock();
    Condition newCallbackReceived = lock.newCondition();

    static class Callback {
        public long expectedTime;
        public String message;

        Callback(long executeAfter, String message) {
            this.expectedTime = System.currentTimeMillis() + (executeAfter * 1000);
            this.message = message;
        }
    }

    private Long getSleepTime() {
        long currentTime = System.currentTimeMillis(); // Corrected "System.currenTimeMillis()" to "System.currentTimeMillis()"
        long expectedTime = queue.peek().expectedTime;
        return expectedTime - currentTime;
    }

    public void start() {
        while (true) {
            lock.lock();
            System.out.println("-----"+Thread.currentThread().getName() +"--------");
            try {
                while (queue.size() == 0) {
                    System.out.println("Nothing in queue");
                    try {
                        newCallbackReceived.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                while (queue.size() != 0) {
                    // logic to recalculate the sleep time
                    Long sleepTime = getSleepTime();
                    System.out.println("Sleep time: "+ sleepTime);
                    if (sleepTime <= 0) {
                        break;
                    }
                    // why choose await with time and not thread.sleep?
                    try {
                        newCallbackReceived.await(sleepTime, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    
                }
                System.out.println("HELLO HELLO");
                Callback cb = queue.poll();
                if (cb != null) {
                    System.out.println("Executed At: " + System.currentTimeMillis() / 1000 + " required at: " + cb.expectedTime + " message: " + cb.message);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void registerCallback(Callback callback) {
        lock.lock();
        System.out.println("-----"+Thread.currentThread().getName() +"--------");
        try {
            queue.add(callback);
            newCallbackReceived.signal();
        } finally {
            lock.unlock();
        }
    }

    public void test_additionOfCallbacksOnceAndChronologicalExecution() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        // Logic to start the daemon thread initially
        Thread service = new Thread(this::start);
        //service.setDaemon(true); // Make it a daemon thread
        service.start();
        
        Thread.sleep(10000);
        
        
        for (int i = 0; i < 10; i++) {
            final int threadIndex = i; // Use a final variable for the lambda expression
            Thread th = new Thread(() -> {
                long time = (long) (Math.random() * 10);
                String message = "Callback from Thread " + threadIndex + " time at: " + time; // Added missing semicolon
                Callback cb = new Callback(time, message);
                registerCallback(cb);
                System.out.println("Registered the callback: "+ cb+" time: "+time+" message: "+message);
            });
            th.setName("Thread" + i);
            allThreads.add(th);
        }
        for (Thread t : allThreads) {
            t.start();
        }
        for (Thread t : allThreads) {
            t.join();
        }
        service.join();
    }

    public void test_additionOfCallbackInBetween() throws InterruptedException {
        // Logic to start the daemon thread initially
        Thread service = new Thread(this::start);
        //service.setDaemon(true); // Make it a daemon thread
        service.start();

        Thread th1 = new Thread(() -> {
            long time = 8;
            String message = "Callback from Thread " + Thread.currentThread().getName() + " time at: " + time; // Added missing semicolon
            Callback cb = new Callback(time, message);
            registerCallback(cb);
        });
        th1.setName("Thread-1");
        th1.start();

        System.out.println("Sleeping for 2 seconds");
        Thread.sleep(2000);

        Thread th2 = new Thread(() -> {
            long time = 1;
            String message = "Callback from Thread " + Thread.currentThread().getName() + " time at: " + time; // Added missing semicolon
            Callback cb = new Callback(time, message);
            registerCallback(cb);
        });
        th2.setName("Thread-2");
        th2.start();

        th1.join();
        th2.join(); // Corrected repeated th1.join() to th2.join()
    }

    public static void main(String[] args) throws InterruptedException { // Added "throws InterruptedException" for the main method
        ThreadSafeDeferredCallback tsdc = new ThreadSafeDeferredCallback(); // Corrected instantiation from DeferredCallback to ThreadSafeDeferredCallback
        // System.out.println("###### TEST 1 ######");
        // tsdc.test_additionOfCallbacksOnceAndChronologicalExecution();
        System.out.println("###### TEST 2 ######");
        tsdc.test_additionOfCallbackInBetween();
    }
}
