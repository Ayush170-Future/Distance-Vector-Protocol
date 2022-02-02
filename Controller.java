/*
 * Computer Networks project -- Distance Vector Protocol Implementation in java.
 * Distance Vector uses Bellman ford algorithm for finding the shortest path between nodes in the network.
 * This class initiates the Distance Vector algorithm,
 * i.e. input all the .dat files of the routers and store valid port numbers for them.
 *
 * Based on awesome work by ARUN KUNNUMPURAM THOMAS and Jingying Liu on github.
 */

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class Controller {

    private HashSet<Integer> portsHash = new HashSet<>();

    public static void main(String[] args) {

        HashSet<Integer> portsHash = new HashSet<>();

        if(args.length == 0) {
            System.out.println("Please add the Directory Path");
            System.out.println("Syntax: java Controller C:\\User\\Something");
            return;
        } else if(args.length > 1) {
            System.out.println("Illegal number of parameters: Only one parameter is required");
            return;
        }

        String path = args[0];
        File directory = new File(path);

        if(!directory.isDirectory()) {
            System.out.println("Directory Path is not correct");
            return;
        }

        File[] dataFiles = directory.listFiles();
        int size = dataFiles.length;
        String allRouters = "";
        int[] portsNumber = new int[size];

        System.out.println("Assign the valid ports number for " +size +" Routers");

        /* Taking valid ports number on which different routers will run on */
        Scanner reader = new Scanner(System.in);

        for(int i = 0; i < size; i++) {
            String fileName = dataFiles[i].getName();
            fileName = fileName.substring(0, fileName.indexOf(".dat"));

            System.out.println("Enter the port number for " +fileName +" router");

            boolean status = true;

            while (true) {

                /*Checking the valid port numbers, i.e. 1024-65535*/
                try {
                    int portNumber = Integer.parseInt(reader.nextLine());
                    if(portNumber <= 1024 || portNumber > 65535) {
                        throw new NumberFormatException();
                    } else if(portsHash.contains(portNumber)) {
                        throw new Exception();
                    } else {
                        portsNumber[i] = portNumber;
                        portsHash.add(portNumber);
                        status = false;
                        allRouters += " " + fileName + ":" + portsNumber[i];
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid port number between 1024 and 65535");
                    status = true;
                } catch (Exception e) {
                    System.out.println("Entered port number is already in use for other router");
                    status = true;
                }
            }
        }

        reader.close();

        boolean flag = true;

        for (int i = 0; i < size; i++) {
            /* This statement runs the Router class for every port and sends the required network information */
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "start java Router " + (i + 1) + " \""
                    + dataFiles[i].getParent().replace("\\", "/") + "\" " + size + allRouters);
            try {
                processBuilder.start();
            } catch (IOException e) {
                flag = false;
                e.printStackTrace();
            }
        }
        if(flag)
            System.out.println("Distance Vector Algorithm Started");
    }
}