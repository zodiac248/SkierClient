package a1part1;

import java.util.ArrayList;
import java.util.concurrent.*;

public class ThreadManager {
    private DataGenerator dataGenerator;
    private LinkedBlockingQueue linkedBlockingQueue;
    private int totalRequest;
    private int remainRequests;
    private int successRequests;
    public ThreadManager(int totalRequest){
        this.linkedBlockingQueue = new LinkedBlockingQueue();
        this.dataGenerator = new DataGenerator(linkedBlockingQueue);
        this.totalRequest = totalRequest;
        this.remainRequests = totalRequest;
        this.successRequests = 0;
    }



    public int sendRequest(){
        new Thread(dataGenerator).start();
        sendRequestBatch(32);
        while(this.remainRequests>0){
            int batchSize = Math.min(32,(int)Math.ceil(this.remainRequests/1000));
            sendRequestBatch(batchSize);
        }
        dataGenerator.setLoop(false);

        return this.successRequests;
    }

    public void sendRequestBatch(int batchSize){
        //this number keeps track of the number of requests send(not neccessarily successful)
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);
        for (int i = 0; i < batchSize; i++) {
            //create thread
            Client client = null;
            int ThreadRequestsNum = Math.min(1000,this.remainRequests);
            client = new Client(linkedBlockingQueue,ThreadRequestsNum);
            this.remainRequests -= ThreadRequestsNum;
            futures.add(executorService.submit(client));
        }
        for (Future<Integer> future:futures
        ) {
            try {
                this.successRequests += future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }

}
