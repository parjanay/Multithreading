
public class ReadWriteLock{
    int activeReaders = 0;
    boolean activeWriter = false;
    
    public synchronized void acquireReadLock() throws InterruptedException{
        while(activeWriter){
            wait();
        }
        activeReaders++;
    }

    public synchronized void releaseReadLock() throws InterruptedException{
        activeReaders--;
        notifyAll();
    }

    public synchronized void acquireWriteLock() throws InterruptedException{
        while(activeWriter || activeReaders > 0){
            wait();
        }
        activeWriter = true;
    }

    public synchronized void releaseWriteLock() throws InterruptedException{
        activeWriter = false;
        notifyAll();
    }

    private void writerLogic(){
        while(true){
            try{
                acquireWriteLock();
                System.out.println( Thread.currentThread().getName()+" acquired write lock at "+ System.currentTimeMillis() );
                System.out.println("Started Writing...");
                Thread.sleep(1000);
                System.out.println("Finished Writing...");
                System.out.println( Thread.currentThread().getName()+" releasing write lock at "+ System.currentTimeMillis() );
                releaseWriteLock();
            }catch(Exception e){
                
            }
            
        }
    }

    private void readerLogic(){
        while(true){
            try{
                acquireReadLock();
                System.out.println( Thread.currentThread().getName()+" acquired read lock at "+ System.currentTimeMillis() );
                System.out.println("Started Reading...");
                Thread.sleep(500);
                System.out.println("Finished Reading...");
                System.out.println( Thread.currentThread().getName()+" releasing read lock at "+ System.currentTimeMillis() );
                releaseReadLock();
            }catch(Exception e){
                
            }
            
        }
    }

    public void test_ReadWriteLock2Reader2Writer(){
        Thread writer1 = new Thread(()->{
            writerLogic();
        });
        Thread writer2 = new Thread(() -> {
            writerLogic();
        });

        Thread reader1 = new Thread(()->{
            readerLogic();
        });

        Thread reader2 = new Thread(()->{
            readerLogic();
        });
        writer1.start();
        writer2.start();
        reader1.start();
        reader2.start();
        try{
            writer1.join();
            writer2.join();
            reader1.join();
            reader2.join();
        }catch(Exception e){
            
        }
    }
    public static void main(String[] args){
        ReadWriteLock rwl = new ReadWriteLock();
        rwl.test_ReadWriteLock2Reader2Writer();
    }
}