package a1part1;

public class Test {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ThreadManager threadManager = new ThreadManager(200000,1000,32);
        int successRequests = threadManager.sendRequest();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime-startTime;
        System.out.println("number of threads: "+32);
        System.out.println("all requests has been sent, "+successRequests+" success");
        System.out.println(200000-successRequests+" requests failed");
        System.out.println("total time: "+totalTime+"ms");
        System.out.println("throughput: "+((double)200000/totalTime)*1000+" requests per second");
    }
}
