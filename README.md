
# Distance Vector Protocol Implementation 

Bellman–Ford Algorithm aka Distance Vector Protocol is an iterative, asynchronous and distributed way of finding routing paths in a network.  


## Concepts Used

**Computer Networks, Socket Programming, Multi-Threading and Graph Theory**


## Files Description 

### Controller.java
Class that accepts all of the initial information from the user, such as valid port numbers and file locations, then launches the Router.java class for each router, i.e. port.

### Router.java
This is the main class that runs on every port and simulates message exchange between routers using UDP Socket programming, as well as displaying the initial and final Distance Vector of a router to the user on the terminal.

### MainThread.java
This is the class that implements Multi-Threading – read and write to the network – for each router with the delay of 5 and 10 seconds respectively.

### datafiles 
Directory that contains all of the .dat files for each router in the network. The .dat files stores the information about the neighbour router and their costs for each router.


## Installation and How-To-Use on a Windows Machine

Install my-project with git-clone 

```bash
  javac *.java
  java Controller Copy&PastePathOfDatafiles
```
Assign six port numbers between 1024 and 65535

Six command prompts will pop up and execute.
    
## Author

- [@Ayush170-Future](https://github.com/Ayush170-Future)


## 🔗 Links
[![twitter](https://img.shields.io/badge/twitter-1DA1F2?style=for-the-badge&logo=twitter&logoColor=white)](https://twitter.com/Ayush_cg)

