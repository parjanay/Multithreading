public class BarberShopProblem{
    int waitingCustomers = 0;
    int MAX_CAPACITY = 3;
    Semaphore waitForCustomerToEnter= new Semaphore(0);
    Semaphore waitForBarberToGetReady= new Semaphore(0);
    Semaphore waitForCustomerToLeave= new Semaphore(0);
    Semaphore waitForBarberToCutHair= new Semaphore(0);
    int haircutsGiven = 0;
    ReentrantLock lock = new ReentrantLock();
    public void customerWalksIn() throws InterruptedException{
        lock.lock();
        if(waitingCustomers >=MAX_CAPACITY){
            lock.unlock();
            return;
        }
        waitingCustomers++;
        lock.unlock();
        waitForCustomerToEnter.release();
        waitForBarberToGetReady.acquire();
        waitForBarberToCutHair.acquire();
        waitForCustomerToLeave.release();

        // this block can be moved up before barber to Cut hair
        // simulates one thread waiting at the outside
        // Increased Throughput a bit
        lock.lock();
        waitingCustomers--;
        lock.unlock();
    }   

    public void barber() throws InterruptedException{
        while(true){
            waitForCustomerToEnter.acquire();
            waitForBarberToGetReady.release();
            haircutsGiven++;
            System.out.println("Barber cutting hair..."+ haircutsGiven);
            Thread.sleep(50);
            waitForBarberToCutHair.release();
            waitForCustomerToLeave.acquire();
        }
    }

    public static void runTest() throws InterruptedException{
        Set<Thread> set = new HashSet<Thread>();
        BarberShopProblem barberShopProblem = new BarberShopProblem();
        Thread barberThread = new Thread(()->{
            try{
                barberShopProblem.barber();
            }catch(InterruptedException e){

            }
        });
        barberThread.setName("Barber_1");
        barberThread.start();

        for(int i= 0;i<10;i++){
            Thread t = new Thread(()->{
                try{
                    barberShopProblem.customerWalksIn();
                }catch(InterruptedException e){

                }
            });
            t.setName("Customer_"+(i+1));
            set.add(t);
        }
        for(Thread t: set){
            t.start();
        }
        for(Thread t: set){
            t.join();
        }
    }
}