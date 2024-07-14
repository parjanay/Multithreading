import java.util.*;
public class BlockingQueue<T>{
    T[] array;
    int MAX_CAPACITY;
    int currentSize;
    int start;
    int end;
    BlockingQueue(int maxCapacity){
        array = (T[]) new Object[maxCapacity];
        this.MAX_CAPACITY = maxCapacity;
    }
    void enqueue(T item) throws InterruptedException{
        synchronized(this){
            while(currentSize == MAX_CAPACITY){
                wait();
            }
            // Can be handled using modulo logic also
            if(end == MAX_CAPACITY){
                end = 0;
            }
            array[end] = item;
            end++;
            currentSize++;
            notifyAll();
        }
        
    }
    T dequeue() throws InterruptedException{
        T item = null;
        synchronized(this){
            while(currentSize==0){
                wait();
            }
            // Can be handled using modulo logic also
            if(start == MAX_CAPACITY){
                start = 0;
            }
            item = array[start];
            array[start] = null;
            start++;
            currentSize--;
            notifyAll();
            
        }
        return item;

    }
public static void test_queueDequeue() {
        BlockingQueue<Integer> q = new BlockingQueue<>(5);
        Set<Thread> allThreads = new HashSet<>();
        
        Thread enqueueThread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 1000; i++) {
                    q.enqueue(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        Thread dequeueThread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    q.dequeue();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread dequeueThread2 = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    q.dequeue();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Start the threads
        enqueueThread1.start();
        dequeueThread1.start();
        dequeueThread2.start();
        
        // Add threads to the set
        allThreads.add(enqueueThread1);
        allThreads.add(dequeueThread1);
        allThreads.add(dequeueThread2);

        // Optionally join threads to wait for their completion
        for (Thread t : allThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue.test_queueDequeue();
    }
}