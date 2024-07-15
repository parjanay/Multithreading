import java.util.Concurrent.ReentrantLock;
import java.util.Concurrent.Condition;
public class FooBarLock{
    int count = 0;
    ReentrantLock lock = new ReentrantLock();
    Condition waitingForFooThread = lock.newCondition();
    Condition waitingForBarThread = lock.newCondition();
    public void printFoo(){
        lock.lock();
        while(count%2 == 1){
            waitingForFooThread.await();
        }
        count++;
        System.out.println("Foo");
        waitingForBarThread.signal();
        lock.unlock();
        

    }
    public void printBar(){
        lock.lock();
        while(count%2 == 0){
            waitingForBarThread.await();
        }
        count++;
        System.out.println("Bar");
        waitingForFooThread.signal();
        lock.unlock();

    }
    static void test_alternatePrinting(){
        FooBar fb = new FooBar();
        Thread fooThread = new Thread(()->{
            for(int i=0;i<10;i++){
                try{
                    fb.printFoo();
                }catch(Exception e){
                    
                }
            }
        });
        Thread barThread = new Thread(()->{
            for(int i=0;i<10;i++){
                try{
                    fb.printBar();
                }catch(Exception e){
                    
                }
            }
        });
        fooThread.start();
        barThread.start();
        try{
            fooThread.join();
            barThread.join();
        }catch(Exception e){
            
        }
    }
    public static void main(String[] args){
        FooBar.test_alternatePrinting();
    }
}