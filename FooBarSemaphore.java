import java.util.concurrent.Semaphore;
public class FooBar{
    Semaphore waitingForFooThread = new Semaphore(0);
    Semaphore waitingForBarThread = new Semaphore(1);
    void printFoo() throws InterruptedException{
        waitingForBarThread.acquire();
        System.out.println("Foo");
        waitingForFooThread.release();
    }
    void printBar() throws InterruptedException{
        waitingForFooThread.acquire();
        System.out.println("Bar");
        waitingForBarThread.release();
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