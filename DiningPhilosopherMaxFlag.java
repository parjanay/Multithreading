public class DiningPhilosopherMaxFlag{
    /* 
    Why did we not choose simple semaphore passes and choose array? 
    Because   
    */
    private Semaphore[] forks = new Semaphore[4]; 
    private
    void eat(){
        System.out.println( Thread.currentThread().getName() + " has started eating.");
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " has finished eating");
    }
    void contemplate(){

    }
    void eat(int id){

    }
}