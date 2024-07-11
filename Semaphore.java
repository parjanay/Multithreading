public class Semaphore{
    int activePasses = 0;
    int MAX_CAPACITY;
    Semaphore(int capacity){
        this.MAX_CAPACITY = capacity;
    }
    public synchronized void acquire() throws InterruptedException{
        if(activePasses == MAX_CAPACITY){
            wait();
        }
        activePasses++;
        notify();
    }

    public synchronized void release() throws InterruptedException{
        if(activePasses == 0){
            wait();
        }
        activePasses--;
        notify();
    }

    public void test_interleavingThreads(){
        Thread thread1 = new Thread(()->{
            for(int i = 0; i < 1000;i++){
                try{
                    acquire();
                    System.out.println("Ping: "+i);
                }catch(InterruptedException e){
                    System.out.println("Exception: "+ e);
                }
            }
            
        });

        Thread thread2 = new Thread(()->{
            
            for(int i = 0; i < 1000; i++ ){
                try{
                    release();
                    System.out.println("Pong: "+ i);
                }catch(InterruptedException e){
                    
                }
            }
        });
        
        thread1.start();
        thread2.start();
        try{
            thread1.join();
            thread2.join();
        }catch(InterruptedException e){
            System.out.println("Exception: "+e);
        }
        
    }

    public static void main(String[] args){
        Semaphore s = new Semaphore(1);
        s.test_interleavingThreads();

    }
}