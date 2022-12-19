package org.example;

import java.util.*;

class Process{
    private String processName;
    private Integer processSize;

    private Boolean isAllocated;

    public Process(String processName, Integer processSize, Boolean isAllocated) {
        this.processName = processName;
        this.processSize = processSize;
        this.isAllocated = isAllocated;
    }

    public String getProcessName() {
        return processName;
    }

    public Integer getProcessSize() {
        return processSize;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public void setProcessSize(Integer processSize) {
        this.processSize = processSize;
    }

    public Boolean getIsAllocated() {
        return isAllocated;
    }

    public void setIsAllocated(Boolean allocated) {
        isAllocated = allocated;
    }

    @Override
    public String toString() {
        return "Process{" +
                "processName='" + processName + '\'' +
                ", processSize=" + processSize +
                '}';
    }
}
class Partition{
    private String partitionName;
    private Integer partitionSize;

    private String processInPartition;
    private Boolean isOccupied;

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean occupied) {
        isOccupied = occupied;
    }

    public Partition(String partitionName, Integer partitionSize, String processInPartition, Boolean isOccupied) {
        this.partitionName = partitionName;
        this.partitionSize = partitionSize;
        this.processInPartition = processInPartition;
        this.isOccupied = isOccupied;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    public Integer getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(Integer partitionSize) {
        this.partitionSize = partitionSize;
    }

    public String getProcessInPartition() {
        return processInPartition;
    }

    public void setProcessInPartition(String processInPartition) {
        this.processInPartition = processInPartition;
    }

    public Boolean getOccupied() {
        return isOccupied;
    }

    public void setOccupied(Boolean occupied) {
        isOccupied = occupied;
    }

    @Override
    public String toString() {
        return "Partition{" +
                "partitionName='" + partitionName + '\'' +
                ", partitionSize=" + partitionSize +
                ", processInPartition='" + processInPartition + '\'' +
                ", isOccupied=" + isOccupied +
                '}';
    }
}
class MemAllocator{
    String policy;
    List<Partition> memory;
    List<Process> processes;

    Integer partitionCount;

    public MemAllocator(String policy, List<Partition> memory, List<Process> processes, Integer partitionCount) {
        this.policy = policy;
        this.memory = memory;
        this.processes = processes;
        this.partitionCount=partitionCount;
    }
    public void runFirstFit(){
        if(policy!="First-Fit"){
            return;
        }
        for (int i = 0; i < processes.size(); i++) {
            for (int j = 0; j < memory.size(); j++) {
                if(!memory.get(i).getIsOccupied() && processes.get(i).getProcessSize()<=memory.get(j).getPartitionSize()){
                    processes.get(i).setIsAllocated(true);
                    if (memory.get(j).getPartitionSize()>processes.get(i).getProcessSize()){
                        Integer remainingSize=memory.get(j).getPartitionSize()-processes.get(i).getProcessSize();

                        memory.get(j).setPartitionSize(processes.get(i).getProcessSize());

                        partitionCount++;
                        Partition newPartition = new Partition("Partition"+partitionCount,remainingSize,"",false);
                        memory.add(memory.indexOf(memory.get(j))+1,newPartition);

                    }
                    memory.get(j).setIsOccupied(true);
                    memory.get(j).setProcessInPartition(processes.get(i).getProcessName());
                    break;
                }
            }

        }
        String notAllocated="\n";
        for (int i = 0; i < processes.size(); i++) {
            if(!processes.get(i).getIsAllocated()){
                notAllocated+=processes.get(i).getProcessName()+" can not be allocated\n";
            }
        }
        for (int i = 0; i < memory.size(); i++) {
            if (memory.get(i).getProcessInPartition() != "") {
                System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => "+memory.get(i).getProcessInPartition());
                continue;
            }
            System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => External fragment");
        }
        System.out.println(notAllocated);

    }
    public void runWorstFit(){
        if(policy!="Worst-Fit"){
            return;
        }

    }
    public void runBestFit(){
        if(policy!="Best-Fit"){
            return;
        }

    }
}
public class Main {
    public static void main(String[] args) {

//        memory.add(0,new Partition("Partition0",3));
//        memory.add(1,new Partition("Partition1",3));
//        for (int i = 0; i < memory.size(); i++) {
//            System.out.println(i+" : "+memory.get(i).getPartitionName());
//        }
//        memory.add(1,new Partition("Partition2",3));
//        System.out.println("-------------");
//        for (int i = 0; i < memory.size(); i++) {
//            System.out.println(i+" : "+memory.get(i).getPartitionName());
//        }
        //In java memory.add(index, elem)
        // this element will be in that index, and what was in that index will be pushed to the right
        Scanner scanner= new Scanner(System.in);
        System.out.print("Enter the number of partitions: ");
        int numberOfPartitions = scanner.nextInt();

        List<Partition> memory = new ArrayList<Partition>(numberOfPartitions);
        String partitionName="";
        Integer partitionSize=0;

        Integer partitionCount=0;
        System.out.print("\n------------------- Input of the info of the "+numberOfPartitions+" partitions -------------------");
        for(int i=0;i<numberOfPartitions;i++){
            System.out.println("\nPartition name and its size: ");
            partitionName=scanner.next();
            partitionSize=scanner.nextInt();
            if(i==numberOfPartitions-1){
                partitionCount=Integer.parseInt(String.valueOf(partitionName.charAt(partitionName.length()-1)));
            }
            memory.add(new Partition(partitionName, partitionSize,"",false));
        }

        System.out.println("\n=========================================================================");
        System.out.print("Enter the number of processes: ");
        int numberOfProcesses = scanner.nextInt();

        List<Process> processes = new ArrayList<Process>(numberOfProcesses);
        String processName="";
        Integer processSize=0;
        System.out.print("\n------------------- Input of the info of the "+numberOfProcesses+" processes -------------------");

        for(int i=0;i<numberOfProcesses;i++){
            System.out.println("\nProcess name and its size: ");
            processName=scanner.next();
            processSize=scanner.nextInt();

            processes.add(new Process(processName, processSize, false));
        }

        List<Partition> memoryCpFirstFit = new ArrayList<Partition>();
        List<Partition> memoryCpWorstFit = new ArrayList<Partition>();
        List<Partition> memoryCpBestFit = new ArrayList<Partition>();
        for (int i = 0; i < memory.size(); i++) {
            memoryCpFirstFit.add(new Partition(memory.get(i).getPartitionName(),memory.get(i).getPartitionSize(),"",memory.get(i).getIsOccupied()));
            memoryCpWorstFit.add(new Partition(memory.get(i).getPartitionName(),memory.get(i).getPartitionSize(),"",memory.get(i).getIsOccupied()));
            memoryCpBestFit.add(new Partition(memory.get(i).getPartitionName(),memory.get(i).getPartitionSize(),"",memory.get(i).getIsOccupied()));
        }

        System.out.println("\n=========================================================================");
        System.out.print("\nSelect the policy you want to apply:\n1. First fit\n2. Worst fit\n3. Best fit\nSelect policy:");
        Integer choice=scanner.nextInt();
        System.out.println(choice);
        if(choice==1){
            //Run first fit
            System.out.println("FF");
            MemAllocator memAllocator= new MemAllocator("First-Fit",memoryCpFirstFit,processes,partitionCount);
            memAllocator.runFirstFit();
        } else if (choice==2) {
            //Run worst fit
//            MemAllocator memAllocator= new MemAllocator("Worst-Fit",memoryCpWorstFit,processes,partitionCount);
//            memAllocator.runWorstFit();
        } else if (choice==3) {
            //Run best fit
//            MemAllocator memAllocator= new MemAllocator("Best-Fit",memoryCpBestFit,processes,partitionCount);
//            memAllocator.runBestFit();
        }else{
            //invalid choice
        }


    }
}