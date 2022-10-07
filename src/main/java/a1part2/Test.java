package a1part2;

public class Test {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ThreadManager threadManager = new ThreadManager(200000);
        int successRequests = threadManager.sendRequest();

    }
}
