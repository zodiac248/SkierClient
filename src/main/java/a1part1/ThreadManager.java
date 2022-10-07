package a1part1;

import java.util.*;
import java.util.concurrent.*;

public class ThreadManager {
    private DataGenerator dataGenerator;
    private LinkedBlockingQueue linkedBlockingQueue;
    private int totalRequest;
    private int remainRequests;
    private int successRequests;
    private Vector<Long> latencies;
    private int maxRequestsPerThread;
    private int numThread;
    public ThreadManager(int totalRequest, int maxRequestsPerThread, int numThread){
        latencies = new Vector<>();
        this.linkedBlockingQueue = new LinkedBlockingQueue();
        this.dataGenerator = new DataGenerator(linkedBlockingQueue);
        this.totalRequest = totalRequest;
        this.remainRequests = totalRequest;
        this.successRequests = 0;
        this.maxRequestsPerThread=maxRequestsPerThread;
        this.numThread=numThread;
    }



    public int sendRequest(){
        new Thread(dataGenerator).start();
        sendRequestBatch(this.numThread,this.totalRequest);
        while(this.remainRequests>0){
            int batchSize = Math.min(this.numThread*this.maxRequestsPerThread,this.remainRequests);
            sendRequestBatch(Math.min(this.remainRequests,this.numThread), batchSize);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = 0;
        for (long latency:
                latencies) {
            totalTime+=latency;
        }

        dataGenerator.setLoop(false);
        return this.successRequests;
    }

    public void sendRequestBatch(int threadNum, int batchSize){
        //this number keeps track of the number of requests send(not neccessarily successful)
        ArrayList<Future<Integer>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            //create thread
            Client client = null;
            int ThreadRequestsNum = Math.min(batchSize/threadNum,this.remainRequests);
            client = new Client(linkedBlockingQueue,latencies,ThreadRequestsNum);
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
    public static long percentile(Vector<Long> latencies, double percentile) {
        int index = (int) Math.ceil(percentile / 100.0 * latencies.size());
        return latencies.get(index-1);
    }
}
