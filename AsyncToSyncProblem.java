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

        System.out.println("Executing async task...");
        asyncExecutor.execute(() -> {
            System.out.println("Async task done.");
        });

        System.out.println("Executing sync task...");
        syncExecutor.execute(() -> {
            System.out.println("Sync task done.");
        });

        System.out.println("Main Thread Exiting...");
    }

    static class AsyncExecutor implements Executor {
        public void execute(Callback callback) throws Exception{
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
        @Override
        public void execute(Callback callback) throws Exception {
            Object signal = new Object();
            final boolean[] isDone = new boolean[1];

            Callback newCallback = new Callback() {
                @Override
                public void done() {
                    callback.done();
                    synchronized (signal) {
                        isDone[0] = true;
                        signal.notify();
                    }
                }
            };

            super.execute(newCallback);

            synchronized (signal) {
                while (!isDone[0]) {
                    signal.wait();
                }
            }
        }
    }
}
