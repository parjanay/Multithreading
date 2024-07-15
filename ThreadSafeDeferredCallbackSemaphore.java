import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ThreadSafeDeferredCallback {

    PriorityQueue<Callback> queue = new PriorityQueue<>((o1, o2) -> Long.compare(o1.expectedTime, o2.expectedTime));

    Semaphore semaphore = new Semaphore(1);
    Semaphore newCallbackReceived = new Semaphore(0);

    static class Callback {
        public long expectedTime;
        public String message;

        Callback(long executeAfter, String message) {
            this.expectedTime = System.currentTimeMillis() + (executeAfter * 1000);
            this.message = message;
        }
    }

    private Long getSleepTime() {
        long currentTime = System.currentTimeMillis();
        long expectedTime = queue.peek().expectedTime;
        return expectedTime - currentTime;
    }

    public void start() {
        while (true) {
            try {
                semaphore.acquire();
                System.out.println("-----" + Thread.currentThread().getName() + "--------");
                while (queue.size() == 0) {
                    System.out.println("Nothing in queue");
                    semaphore.release();
                    newCallbackReceived.acquire();
                    semaphore.acquire();
                }
                while (queue.size() != 0) {
                    // logic to recalculate the sleep time
                    Long sleepTime = getSleepTime();
                    System.out.println("Sleep time: " + sleepTime);
                    if (sleepTime <= 0) {
                        break;
                    }
                    semaphore.release();
                    newCallbackReceived.tryAcquire(sleepTime, TimeUnit.MILLISECONDS);
                    semaphore.acquire();
                }
                System.out.println("HELLO HELLO");
                Callback cb = queue.poll();
                if (cb != null) {
                    System.out.println("Executed At: " + System.currentTimeMillis() / 1000 + " required at: " + cb.expectedTime + " message: " + cb.message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                semaphore.release();
            }
        }
    }

    public void registerCallback(Callback callback) {
        try {
            semaphore.acquire();
            System.out.println("-----" + Thread.currentThread().getName() + "--------");
            queue.add(callback);
            newCallbackReceived.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release();
        }
    }

    public void test_additionOfCallbacksOnceAndChronologicalExecution() throws InterruptedException {
        Set<Thread> allThreads = new HashSet<>();
        // Logic to start the daemon thread initially
        Thread service = new Thread(this::start);
        service.start();
        
        Thread.sleep(10000);
        
        for (int i = 0; i < 10; i++) {
            final int threadIndex = i;
            Thread th = new Thread(() -> {
                long time = (long) (Math.random() * 10);
                String message = "Callback from Thread " + threadIndex + " time at: " + time;
                Callback cb = new Callback(time, message);
                registerCallback(cb);
                System.out.println("Registered the callback: " + cb + " time: " + time + " message: " + message);
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
        service.start();

        Thread th1 = new Thread(() -> {
            long time = 8;
            String message = "Callback from Thread " + Thread.currentThread().getName() + " time at: " + time;
            Callback cb = new Callback(time, message);
            registerCallback(cb);
        });
        th1.setName("Thread-1");
        th1.start();

        System.out.println("Sleeping for 2 seconds");
        Thread.sleep(2000);

        Thread th2 = new Thread(() -> {
            long time = 1;
            String message = "Callback from Thread " + Thread.currentThread().getName() + " time at: " + time;
            Callback cb = new Callback(time, message);
            registerCallback(cb);
        });
        th2.setName("Thread-2");
        th2.start();

        th1.join();
        th2.join();
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeDeferredCallback tsdc = new ThreadSafeDeferredCallback();
        // System.out.println("###### TEST 1 ######");
        // tsdc.test_additionOfCallbacksOnceAndChronologicalExecution();
        System.out.println("###### TEST 2 ######");
        tsdc.test_additionOfCallbackInBetween();
    }
}
