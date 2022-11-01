package a2;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class DataGenerator implements Runnable{
    private LinkedBlockingQueue<SkiRequest> linkedBlockingQueue;
    private boolean loop;

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public DataGenerator(LinkedBlockingQueue linkedBlockingQueue) {
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.loop = true;
    }

    @Override
    public void run() {

        while(loop){
            if(linkedBlockingQueue.size()<100){
                Random random = new Random();
                int skierID = random.nextInt(9999) +1;
                int resortID = random.nextInt(9)+1;
                int liftID = random.nextInt(39)+1;
                String seasonID = "2022";
                String dayID = "1";
                int time = random.nextInt(359)+1;
                try {
                    linkedBlockingQueue.put(new SkiRequest(liftID,time,resortID,seasonID,dayID,skierID));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
