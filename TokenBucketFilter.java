public class TokenBucketFilter{
    private int availableTokens = 0;
    private int MAX_TOKEN;
    long lastRequestTime = System.currentTimeMillis();

    TokenBucketFilter(int maxToken) { 
        this.MAX_TOKEN = maxToken;
    }
    private int getTokensFromTime(){
        long requestedTime = System.currentTimeMillis();
        int tokens = (requestedTime - lastRequestTime) / 1000;
        return tokens;
    }

    public synchronized void getToken() throws InterruptedException{
        availableTokens += max(MAX_TOKEN, getTokensFromTime());
        if(availableTokens == 0){
            // add a delay of 1 second
            // Why wait() not used here? As no need to signal this part
            // Also, chronological order should be maintained, hence if Request1 comes earlier than Request2
            // No need to move to request2, stay stuck in request1
            Thread.sleep(1000);
        }
        if(availableTokens > 0 && availableTokens <= MAX_TOKEN){
            availableToken--;
            lastRequestTime = System.currentTimeMillis();
            System.out.println("Carrying out the work of " + Thread.currentThread().getName());
            Thread.sleep(500)
            System.out.println("Work of " + Thread.currentThread().getName() + " is complete.");
        }

    } 

    public void test_createTenThreadsRequestingTokensQuickly(){
        Set<Thread> allThreads = new HashSet<>();
        for(int i = 0; i< 10; i++){
            Thread thread = new Thread(this::getToken);
            
            thread.setName("Thread_"+(i+1));
            allThreads.add(thread);
        }

        for(Thread t: allThreads){
            t.start();
            System.out.println("Before Request "+ System.currentTime.MILLISECONDS + "Thread: "+ t);
            Thread.sleep(2000);
            System.out.println("After Request "+ System.currentTime.MILLISECONDS + "Thread: "+t);
        }

        for(Thread t: allThreads){
            t.join();
        }
        
    }

    public static void main(String[] args){
        System.out.println("################## Test-1 #######################");
        TokenBucketFilter tbf = new TokenBucketFilter(5);
        tbf.test_createTenThreadsRequestingTokensQuickly();
    }


}