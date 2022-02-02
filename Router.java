/*
* Computer Networks project -- Distance Vector Protocol Implementation in java.
* Distance Vector uses Bellman ford algorithm for finding the shortest path between nodes in the network.
* This is the main class that runs for every router in the network i.e. ports in this case.
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Router {

    public static double[][] routersDistanceVectors;
    public static int[] routersPortNumbers;
    public static String[] routersNames;
    public static int routerID;
    public static double[] routerMyVector;   // storing costs
    public static String[] routerMyHopList;  // storing the next hop
    public static File routerMyFile;
    public static DatagramSocket routerSocket;
    public static String[] routerNeighbours;
    public static int routerDisplayCount = 1;

    public static void main(String[] args) {

        if(args.length < 3) {
            System.out.println("args not enough");
            return;
        }
        int totalPorts = Integer.parseInt(args[2]);
        int currentRouterId = Integer.parseInt(args[0]);
        String filePath = args[1];
        setParametersForRouter(totalPorts, args, currentRouterId, filePath);

        MainThread readThread = new MainThread("r");
        readThread.start();

        MainThread writeThread = new MainThread("w");
        writeThread.start();

        while (true)
            ;
    }

    /* Method for initialising network information before beginning peer-to-peer message exchange. */
    private static void setParametersForRouter(int totalPorts, String[] args, int currentRouterId, String parent) {

        routersDistanceVectors = new double[totalPorts][totalPorts];
        routersPortNumbers = new int[totalPorts];
        routersNames = new String[totalPorts];

        for(int i = 0; i < totalPorts; i++) {
            Arrays.fill(routersDistanceVectors, Double.MAX_VALUE);
            routersDistanceVectors[i][i] = 0.0;
            String temp[] = args[i+3].split(":");
            routersNames[i] = temp[0];
            routersPortNumbers[i] = Integer.parseInt(temp[1]);
        }

        routerID = currentRouterId;
        routerMyVector = new double[totalPorts];
        routerMyHopList = new String[totalPorts];
        Arrays.fill(routerMyHopList, Double.MAX_VALUE);
        routerMyVector[routerID - 1] = 0.0;
        routerMyFile = new File(parent +"/" +routersNames[routerID - 1] +".dat");

        try{
            /* For the UDP connection */
            routerSocket = new DatagramSocket(routersPortNumbers[routerID - 1]);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        System.out.println("Router " +routersNames[routerID - 1] +" is Working! ");
    }

    public static void readData() {
        boolean status = true;
        while(status) {
            try {
                String type = "u";
                /* byte is the data format for UDP */
                byte[] data = new byte[1024];
                int size = data.length;
                /* Packet for UDP connection */
                DatagramPacket packet = new DatagramPacket(data, size);
                routerSocket.receive(packet);
                int length = packet.getLength();
                String vector = new String(packet.getData(), 0, length);
                MainThread updateThread = new MainThread(type, vector, packet.getPort());
                updateThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* This method reads the data about neighbors from the .dat file of this.router */
    public static void read() {
        try {
            Arrays.fill(routersDistanceVectors[routerID - 1], Double.MAX_VALUE);
            routersDistanceVectors[routerID - 1][routerID - 1] = 0.0;

            /* Reading the content of this router file -- information about the neighbours */
            BufferedReader reader = new BufferedReader(new FileReader(routerMyFile));

            int length = Integer.parseInt(reader.readLine());
            routerNeighbours = new String[length];

            for(int i = 0; i < length; i++) {
                String lineContent[] = reader.readLine().split(" ");
                routerNeighbours[i] = lineContent[0];
                int index = indexFinder(lineContent[1]);
                if (routerDisplayCount == 1) {
                    routerMyHopList[index] = lineContent[0];
                    routerMyVector[index] = Double.parseDouble(lineContent[1]);
                }
                routersDistanceVectors[routerID - 1][index] = Double.parseDouble(lineContent[1]);
            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Method to find index of particular router using its name */
    public static int indexFinder(String routerName) {
        int index = -1;
        for(int i = 0; i < routersNames.length; i++) {
            if(routerName.equalsIgnoreCase(routersNames[i])) {
                index = i;
                break;
            }
        }
        return index;
    }

    /* Method for updating the end term user for initial and final distance vectors of the router */
    public static void output() {

        System.out.println("> output number " + routerDisplayCount++);
        System.out.println();
        String source = routersNames[routerID - 1];
        for(int i = 0; i < routerMyVector.length; i++) {
            if(i != routerID - 1) {
                String destination = routersNames[i];
                if(routerMyVector[i] == Double.MAX_VALUE) {
                    System.out.println("Shortest path " +source +"-" +destination +": No router found");
                } else {
                    System.out.println("Shortest path " +source +"-" +destination +":" +" The next hop is " +routerMyHopList[i] +" and the Cast is " +routerMyVector[i]);
                }
            }
        }
    }

    /* Method for broadcasting Distance vector of this.router to all the neighbors */
    /* For this I am using UDP connections between the ports i.e. sending DatagramPackets */
    public static void broadcast() {
        try {
            for(int i = 0; i < routerNeighbours.length; i++) {
                String data = "";
                for(int j = 0; j < routerMyVector.length; j++) {
                    if(routerNeighbours[i].equalsIgnoreCase(routerMyHopList[j])) {
                        data += Double.MAX_VALUE +":";
                    } else {
                        data += routerMyVector[j] +":";
                    }
                }
                DatagramPacket packet = new DatagramPacket(data.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8).length);
                packet.setAddress(InetAddress.getByName("localhost"));
                packet.setPort(routersPortNumbers[indexFinder(routerNeighbours[i])]);
                routerSocket.send(packet);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /* Method to implement Bellman ford algorithm, this method updates the this.router Distance vector according to
    * information by its neighbors */
    public static void distanceAlgo_BellmanFord() {

        for(int i = 0; i < routerNeighbours.length; i++) {
            int index = indexFinder(routersNames[i]);
            for(int j = 0; j < routerMyVector.length; j++) {
                if(i == 0) {
                    routerMyVector[j] = routersDistanceVectors[routerID - 1][index] + routersDistanceVectors[index][j];
                    routerMyHopList[j] = routerNeighbours[i];
                } else {
                    if(routerMyVector[j] > routersDistanceVectors[routerID - 1][index] + routersDistanceVectors[index][j]) {
                        routerMyHopList[j] = routerNeighbours[i];
                        routerMyVector[j] = routersDistanceVectors[routerID - 1][index] + routersDistanceVectors[index][j];
                    }
                }
            }
        }
    }


    public synchronized static void updateNetworkVectors(String[] vector, int port) {
        int index = 0;
        int ports_Length = routersPortNumbers.length;
        while(index < ports_Length)
        {
            if (routersPortNumbers[index] == port)
            {
                break;
            }
            index++;
        }
        if (index == ports_Length)
        {
            return;
        }
        for (int i = 0; i < vector.length; i++)
        {
            routersDistanceVectors[index][i] = Double.parseDouble(vector[i]);
        }
    }
}