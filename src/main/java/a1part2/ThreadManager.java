package a1part2;

import java.util.*;
import java.util.concurrent.*;

public class ThreadManager {
    private DataGenerator dataGenerator;
    private LinkedBlockingQueue linkedBlockingQueue;
    private int totalRequest;
    private int remainRequests;
    private int successRequests;
    private Vector<Long> latencies;

    public ThreadManager(int totalRequest){
        latencies = new Vector<>();
        this.linkedBlockingQueue = new LinkedBlockingQueue();
        this.dataGenerator = new DataGenerator(linkedBlockingQueue);
        this.totalRequest = totalRequest;
        this.remainRequests = totalRequest;
        this.successRequests = 0;
    }



    public int sendRequest(){
        long startTime = System.currentTimeMillis();
        new Thread(dataGenerator).start();
        sendRequestBatch(32);
        while(this.remainRequests>0){
            int batchSize = Math.min(32,(int)Math.ceil(this.remainRequests/1000));
            sendRequestBatch(batchSize);
        }
        long endTime = System.currentTimeMillis();
        long totalTime = 0;
        for (long latency:
             latencies) {
            totalTime+=latency;
        }

        Collections.sort(latencies);
        System.out.println("mean latency is "+((double)totalTime/200000)+"ms");
        System.out.println("throughput is "+(1000*((double)200000/(endTime-startTime))+"requests per second"));
        System.out.println("median latency is "+percentile(latencies,50)+"ms");
        System.out.println("99 percentile latency is "+percentile(latencies,99)+"ms");
        System.out.println("min latency is "+latencies.get(0)+"ms");
        System.out.println("max latency is"+ latencies.get(latencies.size()-1)+"ms");
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
