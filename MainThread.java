
/*
 * Computer Networks project -- Distance Vector Protocol Implementation in java.
 * Distance Vector uses Bellman ford algorithm for finding the shortest path between nodes in the network.
 * This class is for implementing multi-threading -- Read & Write to the Network
 */

public class MainThread extends Thread{

    private String vector;
    private String type;
    private int port;

    public MainThread(String type) {
        this.type = type;
    }

    public MainThread(String type, String vector, int port){
        this.type = type;
        this.vector = vector;
        this.port = port;
    }

    public void run() {
        if(type.equalsIgnoreCase("r")) {
            Router.readData();
        } else if(type.equalsIgnoreCase("w")) {
            while(true) {
                try {
                    Router.read();
                    Router.output();
                    Router.broadcast();

                    Thread.sleep(5000);

                    Router.distanceAlgo_BellmanFord();

                    Thread.sleep(10000);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

        } else if(type.equalsIgnoreCase("u")) {
            Router.updateNetworkVectors(vector.split(":"), port);
        }
    }

}