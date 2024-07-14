import java.util.*;
public class UnisexBathroom{
    static string WOMEN = "women";
    static string MEN = "men";
    static string NONE = "none";
    string inUseBy = NONE;
    int empsInBathroom = 0;
    Semaphore maxEmps = new Semaphore(3);
    void useBathroom(String name) throws InterruptedException{
        System.out.println("\n"+name+"is using the bathroom. Current employees in bathroom = "+ empsInBathroom);
        Thread.sleep(3000);
        System.out.println("\n"+name+"is done using the bathroom. ");

    }
    void maleUseBathroom(String name) throws InterruptedException{
        synchronized(this){
            while(inUseBy.equals(WOMEN)){
                wait();
            }
            maxEmps.acquire();
            empsInBathroom++;
            inUseBy = MEN;
        }
        useBathroom();
        maxEmps.release();
        synchronized(this){
            empsInBathroom--;
            if(empsInBathroom == 0){
                inUseBy = NONE;
            }
            this.notifyAll();
        }
        
    }

    void femaleUseBathroom(String name) throws InterruptedException{
        synchronized(this){
            while(inUseBy.equals(MEN)){
                this.wait();
            }
            maxEmps.acquire();
            empsInBathroom++;
            inUseBy = WOMEN;
        }
        useBathroom(name);
        maxEmps.release();
        synchronized(this){
            empsInBathroom--;
            if(empsInBathroom == 0){
                inUseBy = NONE;
            }
            this.notifyAll();
        }
    }

    public static void runTest() throws InterruptedException{
        final UnisexBathroom unisexBathroom = new UnisexBathroom();
        Thread f1 = new Thread(()->{
            try{
                unisexBathroom.femaleUseBathroom("Lisa");
            }catch(InterruptedException e){

            }
        });

        Thread m1 = new Thread(()->{
            try{
                unisexBathroom.maleUseBathroom("John");
            }catch(InterruptedException e){

            }
        });

        Thread m2 = new Thread(()->{
            try{
                unisexBathroom.maleUseBathroom("Bob");
            }catch(InterruptedException e){

            }
        });

        Thread m3 = new Thread(()->{
            try{
                unisexBathroom.maleUseBathroom("Anil");
            }catch(InterruptedException e){

            }
        });

        Thread m4 = new Thread(()->{
            try{
                unisexBathroom.maleUseBathroom("Wentao");
            }catch(InterruptedException e){

            }
        });

        f1.start();
        m1.start();
        m2.start();
        m3.start();
        m4.start();

        f1.join();
        m1.join();
        m2.join();
        m3.join();
        m4.join();
    }
}