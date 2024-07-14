import java.util.concurrent.Semaphore;

public class AsyncToSyncProblem {

    interface Executor {
        void execute(Callback cb) throws Exception;
    }

    interface Callback {
        void done();
    }

    public static void main(String[] args) throws Exception {
        Executor asyncExecutor = new AsyncExecutor();
        Executor syncExecutor = new SyncExecutor();

        // System.out.println("Executing async task...");
        // asyncExecutor.execute(() -> {
        //     System.out.println("Async task done.");
        // });

        System.out.println("Executing sync task...");
        syncExecutor.execute(() -> {
            System.out.println("Sync task done.");
        });

        System.out.println("Main Thread Exiting...");
    }

    static class AsyncExecutor implements Executor {
        public void execute(Callback callback) throws Exception {
            Thread t = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " is doing something useful");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                callback.done();
            });
            t.start();
        }
    }

    static class SyncExecutor extends AsyncExecutor {
        private final Semaphore waitingForAsyncToComplete = new Semaphore(0);

        @Override
        public void execute(Callback callback) throws Exception {
            Callback newCallback = new Callback() {
                @Override
                public void done() {
                    callback.done();
                    waitingForAsyncToComplete.release();
                }
            };

            super.execute(newCallback);
            waitingForAsyncToComplete.acquire();
        }
    }
}
