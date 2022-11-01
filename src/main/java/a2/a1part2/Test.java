package a2.a1part2;

public class Test {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ThreadManager threadManager = new ThreadManager(200000,1000,256);
        int successRequests = threadManager.sendRequest();
        //print message is in threadManager class

    }
}
