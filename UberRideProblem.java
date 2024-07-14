import java.util.*;
public class UberRideProblem{
    int currentCongress = 0;
    int currentBJP = 0;
    Semaphore congressWaiting = new Semaphore(0);
    Semaphore BJPWaiting = new Semaphore(0);
    CyclicBarrier barrier = new CyclicBarrier(4);
    ReentrantLock lock = new ReentrantLock();
    void drive(){
        System.out.println("Uber Ride going. Leader: "+Thread.currentThread().getName());
        System.out.flush();
    }
    void seatCongressPerson() throws InterruptedException, BrokenBarrierException{
        boolean rideLeader = false;
        lock.lock();
        currentCongress++;
        if(currentCongress == 4){
            congressWaiting.release(3);
            currentCongress -=4;
            rideLeader = true;
            
        }
        else if(currentCongress == 2 && currentBJP >=2){
            congressWaiting.release(1);
            BJPWaiting.release(2);
            currentCongress-=2;
            currentBJP-=2
            rideLeader=true;
        }else{
            congressWaiting.acquire(); // discuss shouldn't they 
            lock.release(); // be swapped here
        }
        seated();
        barrier.await();
        if(rideLeader == true){
            drive();
            lock.unlock();
        }
        void seated(){
            System.out.println(Thread.currentName().getName()+" seated.");
            System.out.flush();
        }
    }

    void seatBJPPerson() throws InterruptedException, BrokenBarrierException{
        boolean rideLeader = false;
        lock.lock();
        currentBJP++;
        if(currentBJP == 4){
            BJPWaiting.release(3);
            currentBJP-=4;
            rideLeader = true;
        }
        if(currentBJP == 2 && currentCongress >= 2){
            BJPWaiting.release(1);
            congressWaiting.release(2);
            currentBJP-=2;
            currentCongress-=2;
            rideLeader = true;

        }else{
            BJPWaiting.acquire();
            lock.unlock();

        }
        seated();
        barrier.await();
        if(rideLeader == true){
            drive;
            lock.unlock();
        }
    }

    public static void main(String[] args){
        UberRideProblem uberRideProblem = new UberRideProblem();
        Set<Thread> allThreads = new HashSet<Thread>();
        for(int i=0;i<10;i++){
            Thread thread = new Thread(()->{
                try{
                    uberRideProblem.seatCongressPerson();
                }catch(Exception e){
                    System.out.println("We have a problem");
                }
                
            });
            thread.setName("Congress_"+(i+1));
            allThreads.add(thread);
            Thread.sleep(50);
        }

        for(int i = 0;i<20;i++){
            Thread thread = new Thread(()->{
                try{
                    UberRideProblem.seatBJPPerson();
                }catch(Exception e){
                    System.out.println("We have a problem");
                }
            });
            thread.setName("BJP_"+(i+1));
            allThreads.add(thread);
            Thread.sleep(50);
        }

        for(Thread t: allThreads){
            t.start();
        }

        for(Thread t: allThreads){
            t.join();
        }
    }
}