package tasks;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**Implementation to handle scenarios when the pending beverage requests goes beyond MAX_QUEUE_SIZE*/
class RejectedTaskHandler implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        System.out.printf("The beverage request %s has been rejected by coffee machine", r.toString());
    }
}