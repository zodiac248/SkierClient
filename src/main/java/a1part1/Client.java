package a1part1;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements Callable {
    private int numRequest;
    private LinkedBlockingQueue<SkiRequest> linkedBlockingQueue;
    private SkiersApi apiInstance;
    private ApiClient apiClient;
    private int successRequest;
    private int failedRequest;
    private Vector<Long> latencies;
    public Client(LinkedBlockingQueue linkedBlockingQueue, Vector latencies, int numRequests) {
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.numRequest = numRequests;
        this.apiInstance = new SkiersApi();
        this.failedRequest = 0;
        this.apiClient = new ApiClient();
        this.apiClient.setBasePath("http://18.236.90.242:8080/lab2");
        this.apiInstance.setApiClient(apiClient);
        this.latencies = latencies;

    }

    @Override
    public Object call() throws Exception {
        SkiRequest skiRequest = null;
        int trial = 0;
        int counter = this.numRequest;
        //call API
        while(counter>0){

            try {
                trial++;
                if(trial==1){
                    skiRequest = this.linkedBlockingQueue.take();
                }else if(trial==6){
                    trial=0;
                    counter--;
                    this.failedRequest++;
                    continue;
                }
                LiftRide body = new LiftRide();
                body.setLiftID(skiRequest.getLiftID());
                body.setTime(skiRequest.getTime());
                Integer resortID = skiRequest.getResortID();
                String seasonID = "2022";
                String dayID = "1";
                Integer skierID = skiRequest.getSkierID();
                long startTime = System.currentTimeMillis();
                ApiResponse apiResponse = apiInstance.writeNewLiftRideWithHttpInfo(body, resortID, seasonID, dayID, skierID);
                if(apiResponse.getStatusCode()==200 || apiResponse.getStatusCode()==201){
                    counter--;
                    trial = 0;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (ApiException e) {
                if(e.getCode()!=400||e.getCode()!=404||e.getCode()!=500){
                    trial=0;
                    counter--;
                    this.failedRequest++;
                    continue;
                }
                e.printStackTrace();
            }
        }
        return this.numRequest-this.failedRequest;
    }
}
