package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
Student name: Mostafa Ibrahim Abdellatif, Student ID: 20205006, Group: NCS1
* */
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
                ", isAllocated=" + isAllocated +
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
        this.policy = policy;//Setting the policy of the memory allocator
        this.memory = new ArrayList<Partition>();
        //just copying the original list of partitions (in order not to change the list itself as i am passing it multiple times)
        for (int i = 0; i < memory.size(); i++) {
            this.memory.add(new Partition(memory.get(i).getPartitionName(),memory.get(i).getPartitionSize(),"",memory.get(i).getIsOccupied()));
        }
        this.processes=new ArrayList<Process>();
        //just copying the original list of processes (in order not to change the list itself as i am passing it multiple times)
        for (int i = 0; i < processes.size(); i++) {
            this.processes.add(new Process(
                    processes.get(i).getProcessName(),
                    processes.get(i).getProcessSize(),
                    processes.get(i).getIsAllocated()));
        }
        //the largest partition number entered (the number in the partition name), new partitions will increment to this count
        this.partitionCount=partitionCount;
    }
    public void runFirstFit(){
        //making sure not to invoke by a memory allocator already set to another policy
        if(policy!="First-Fit"){
            return;
        }
        Integer count=0;
        Scanner scanner= new Scanner(System.in);
        while(true){
            for (int i = 0; i < processes.size(); i++) {
                for (int j = 0; j < memory.size(); j++) {
                    //The first one that could fit the process (<=), and not occupied
                    if(!memory.get(j).getIsOccupied() && processes.get(i).getProcessSize()<=memory.get(j).getPartitionSize()){
                        processes.get(i).setIsAllocated(true);//set the process status to be allocated
                        //now we check if it was larger, we create new partition out of the remaining size of the partition
                        if (memory.get(j).getPartitionSize()>processes.get(i).getProcessSize()){

                            Integer remainingSize=memory.get(j).getPartitionSize()-processes.get(i).getProcessSize();

                            //allocating the original partition for the size of the process
                            memory.get(j).setPartitionSize(processes.get(i).getProcessSize());

                            //we'll create a new partition, so we increment the latest largest partition number
                            partitionCount++;
                            //Creating the new process of size=remaining size, setting it as not occupied
                            Partition newPartition = new Partition("Partition "+partitionCount,remainingSize,"",false);
                            memory.add(memory.indexOf(memory.get(j))+1,newPartition);

                        }
                        memory.get(j).setIsOccupied(true);//set the partition to be allocated
                        memory.get(j).setProcessInPartition(processes.get(i).getProcessName());//set process to be in the partition
                        break;//break out of memory looping, because we've already found the first fit
                    }
                }

            }
            String notAllocated="\n";
            //Getting the processes that we didn't find a fit for (for print)
            for (int i = 0; i < processes.size(); i++) {
                if(!processes.get(i).getIsAllocated()){
                    notAllocated+=processes.get(i).getProcessName()+" can not be allocated\n";
                }
            }
            //Printing partitions and processes allocated to them (or external fragments)
            for (int i = 0; i < memory.size(); i++) {
                if (memory.get(i).getProcessInPartition() != "") {
                    System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => "+memory.get(i).getProcessInPartition());
                    continue;
                }
                System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => External fragment");
            }
            System.out.println(notAllocated);

            //This count just to return back to first fit allocation after choosing to compact
            // (for the not allocated, we allocate them after compaction if a partition was suitable)
            if(count>0){break;}
            count++;
            System.out.println("\nDo you want to compact?\n1. yes\n2. no\nSelect 1 or 2:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){//yes
                notAllocated="\n";
                int freeMemorySize=0;
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getProcessInPartition() == "") {
                        freeMemorySize+=memory.get(i).getPartitionSize();//Getting total free memory
                    }
                }

                List<Partition> memoryOccupied=new ArrayList<Partition>();
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getIsOccupied()==true) {
                        memoryOccupied.add(memory.get(i));//Getting just the ones that were occupied,
                        // as we will create a one large partition out of the free memory
                    }
                }
                memory=memoryOccupied;

                List<Process> leftProcesses=new ArrayList<Process>();
                for (int i = 0; i < processes.size(); i++) {
                    if(processes.get(i).getIsAllocated()==false){
                        leftProcesses.add(processes.get(i));//Getting not allocated processes
                    }
                }
                processes=leftProcesses;

                //Creating the new partition of size == free memory available
                partitionCount++;
                Partition freeMemoryPartition = new Partition("Partition "+partitionCount,freeMemorySize,"",false);
                memory.add(freeMemoryPartition);
            } else if (choice.equals(new String("2"))) {//no
                break;
            }else{
                System.out.println("Invalid choice");
                break;
            }
        }


    }
    private Partition getMaxDiff(Integer processSize){//For the worst fit
        Integer MIN=0;
        for (int i = 0; i < memory.size(); i++) {MIN+=memory.get(i).getPartitionSize();}
        //just init value for comparison and to check with if we didn't find the worst fit(not allocated)
        Partition maxDiffPartition=new Partition("None",-MIN,"None",true);
        for (int i = 0; i < memory.size(); i++) {
            if(!memory.get(i).getIsOccupied()){
                Integer currentDiff=memory.get(i).getPartitionSize()-processSize;
                //check if the difference between the current partition size and the process size are actually worse/larger, if so we set it as the worst fit
                if(currentDiff>=0 && currentDiff>maxDiffPartition.getPartitionSize()-processSize){
                    maxDiffPartition=memory.get(i);
                }
            }
        }

        return maxDiffPartition;
    }
    public void runWorstFit(){
        //making sure not to invoke by a memory allocator already set to another policy
        if(policy!="Worst-Fit"){
            return;
        }
        String notAllocated="\n";
        Integer count=0;
        Scanner scanner= new Scanner(System.in);
        while(true){
            for (int i = 0; i < processes.size(); i++) {
                Partition worstFitPartition=getMaxDiff(processes.get(i).getProcessSize());
                //If we didn't find the worst fit, can't be allocated
                if(worstFitPartition.getPartitionName()=="None"){
                    notAllocated+=processes.get(i).getProcessName()+" can not be allocated\n";
                }else{
                    processes.get(i).setIsAllocated(true);//set the process status to be allocated

                    //now we check if it was larger, we create new partition out of the remaining size of the partition
                    if(worstFitPartition.getPartitionSize()>processes.get(i).getProcessSize()){
                        Integer remainingSize=worstFitPartition.getPartitionSize()-processes.get(i).getProcessSize();

                        //allocating the original partition for the size of the process
                        worstFitPartition.setPartitionSize(processes.get(i).getProcessSize());
                        memory.get(memory.indexOf(worstFitPartition)).setPartitionSize(processes.get(i).getProcessSize());

                        //we'll create a new partition, so we increment the latest largest partition number
                        partitionCount++;
                        //Creating the new process of size=remaining size, setting it as not occupied
                        Partition newPartition = new Partition("Partition "+partitionCount,remainingSize,"",false);
                        memory.add(memory.indexOf(worstFitPartition)+1,newPartition);
                    }
                    //set the partition to be allocated
                    worstFitPartition.setIsOccupied(true);
                    memory.get(memory.indexOf(worstFitPartition)).setIsOccupied(true);

                    //set process to be in the partition
                    worstFitPartition.setProcessInPartition(processes.get(i).getProcessName());
                    memory.get(memory.indexOf(worstFitPartition)).setProcessInPartition(processes.get(i).getProcessName());
                }
            }
            //Printing partitions and processes allocated to them (or external fragments)
            for (int i = 0; i < memory.size(); i++) {
                if (memory.get(i).getProcessInPartition() != "") {
                    System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => "+memory.get(i).getProcessInPartition());
                    continue;
                }
                System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => External fragment");
            }
            System.out.println(notAllocated);

            //This count just to return back to first fit allocation after choosing to compact
            // (for the not allocated, we allocate them after compaction if a partition was suitable)
            if(count>0){break;}
            count++;
            System.out.println("\nDo you want to compact?\n1. yes\n2. no\nSelect 1 or 2:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){//yes
                notAllocated="\n";
                int freeMemorySize=0;
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getProcessInPartition() == "") {
                        freeMemorySize+=memory.get(i).getPartitionSize();//Getting total free memory
                    }
                }

                List<Partition> memoryOccupied=new ArrayList<Partition>();
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getIsOccupied()==true) {
                        memoryOccupied.add(memory.get(i));//Getting just the ones that were occupied,
                        // as we will create a one large partition out of the free memory
                    }
                }
                memory=memoryOccupied;
                List<Process> leftProcesses=new ArrayList<Process>();
                for (int i = 0; i < processes.size(); i++) {
                    if(processes.get(i).getIsAllocated()==false){
                        leftProcesses.add(processes.get(i));//Getting not allocated processes
                    }
                }
                processes=leftProcesses;

                //Creating the new partition of size == free memory available
                partitionCount++;
                Partition freeMemoryPartition = new Partition("Partition "+partitionCount,freeMemorySize,"",false);
                memory.add(freeMemoryPartition);
            } else if (choice.equals(new String("2"))) {//no
                break;
            }else{
                System.out.println("Invalid choice");
                break;
            }

        }//

    }
    private Partition getMinDiff(Integer processSize){
        Integer MAX=0;
        for (int i = 0; i < memory.size(); i++) {MAX+=memory.get(i).getPartitionSize();}
        //just init value for comparison and to check with if we didn't find the best fit(not allocated)
        Partition minDiffPartition = new Partition("None",MAX,"",true);
        for (int i = 0; i < memory.size(); i++) {
            if(!memory.get(i).getIsOccupied()){
                Integer currentDiff=memory.get(i).getPartitionSize()-processSize;
                //check if the difference between the current partition size and the process size are actually better/less, if so we set it as the best fit
                if(currentDiff>=0 && currentDiff<minDiffPartition.getPartitionSize()-processSize){
                    minDiffPartition=memory.get(i);
                }
            }
        }
        return minDiffPartition;
    }
    public void runBestFit(){
        //making sure not to invoke by a memory allocator already set to another policy
        if(policy!="Best-Fit"){
            return;
        }
        String notAllocated="\n";
        Integer count=0;
        Scanner scanner= new Scanner(System.in);
        while(true){
            for (int i = 0; i < processes.size(); i++) {
                Partition bestFitPartition=getMinDiff(processes.get(i).getProcessSize());
                //If we didn't find the best fit, can't be allocated
                if(bestFitPartition.getPartitionName()=="None"){
                    notAllocated+=processes.get(i).getProcessName()+" can not be allocated\n";
                }else{
                    processes.get(i).setIsAllocated(true);//set the process status to be allocated
                    //now we check if it was larger, we create new partition out of the remaining size of the partition
                    if(bestFitPartition.getPartitionSize()>processes.get(i).getProcessSize()){
                        Integer remainingSize=bestFitPartition.getPartitionSize()-processes.get(i).getProcessSize();

                        //allocating the original partition for the size of the process
                        bestFitPartition.setPartitionSize(processes.get(i).getProcessSize());
                        memory.get(memory.indexOf(bestFitPartition)).setPartitionSize(processes.get(i).getProcessSize());

                        //we'll create a new partition, so we increment the latest largest partition number
                        partitionCount++;
                        //Creating the new process of size=remaining size, setting it as not occupied
                        Partition newPartition = new Partition("Partition "+partitionCount,remainingSize,"",false);
                        memory.add(memory.indexOf(bestFitPartition)+1,newPartition);
                    }
                    //set the partition to be allocated
                    bestFitPartition.setIsOccupied(true);
                    memory.get(memory.indexOf(bestFitPartition)).setIsOccupied(true);

                    //set process to be in the partition
                    bestFitPartition.setProcessInPartition(processes.get(i).getProcessName());
                    memory.get(memory.indexOf(bestFitPartition)).setProcessInPartition(processes.get(i).getProcessName());
                }
            }
            //Printing partitions and processes allocated to them (or external fragments)
            for (int i = 0; i < memory.size(); i++) {
                if (memory.get(i).getProcessInPartition() != "") {
                    System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => "+memory.get(i).getProcessInPartition());
                    continue;
                }
                System.out.println(memory.get(i).getPartitionName()+" ("+memory.get(i).getPartitionSize()+" KB) => External fragment");
            }
            System.out.println(notAllocated);
            //This count just to return back to first fit allocation after choosing to compact
            // (for the not allocated, we allocate them after compaction if a partition was suitable)
            if(count>0){break;}
            count++;
            System.out.println("\nDo you want to compact?\n1. yes\n2. no\nSelect 1 or 2:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){//yes
                notAllocated="\n";
                int freeMemorySize=0;
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getProcessInPartition() == "") {
                        freeMemorySize+=memory.get(i).getPartitionSize();//Getting total free memory
                    }
                }

                List<Partition> memoryOccupied=new ArrayList<Partition>();
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getIsOccupied()==true) {
                        memoryOccupied.add(memory.get(i));//Getting just the ones that were occupied,
                        // as we will create a one large partition out of the free memory
                    }
                }
                memory=memoryOccupied;
                List<Process> leftProcesses=new ArrayList<Process>();
                for (int i = 0; i < processes.size(); i++) {
                    if(processes.get(i).getIsAllocated()==false){
                        leftProcesses.add(processes.get(i));//Getting not allocated processes
                    }
                }
                processes=leftProcesses;
                //Creating the new partition of size == free memory available
                partitionCount++;
                Partition freeMemoryPartition = new Partition("Partition "+partitionCount,freeMemorySize,"",false);
                memory.add(freeMemoryPartition);
            } else if (choice.equals(new String("2"))){//no
                break;
            }else{
                System.out.println("Invalid choice");
                break;
            }
        }

    }
}
public class Main {
    public static void main(String[] args) {
        Scanner scanner= new Scanner(System.in).useDelimiter("\n");
        System.out.print("Enter the number of partitions: ");
        int numberOfPartitions = scanner.nextInt();

        List<Partition> memory = new ArrayList<Partition>(numberOfPartitions);
        String partitionName="";
        Integer partitionSize=0;

        Integer partitionCount=0;
        String partitionCountString="";
        System.out.print("\n------------------- Input of the info of the "+numberOfPartitions+" partitions -------------------");
        for(int i=0;i<numberOfPartitions;i++){
            System.out.println("\n---------- #"+(i+1)+" ----------");
            System.out.println("Partition name:");
            partitionName=scanner.next();
            System.out.println("Partition size:");
            partitionSize=scanner.nextInt();

            memory.add(new Partition(partitionName, partitionSize,"",false));
            partitionCountString=partitionName;
            partitionCountString=partitionCountString.replaceAll("[^0-9]", "");
            partitionCount=Integer.parseInt(partitionCountString);
        }

        System.out.println("\n=========================================================================");
        System.out.print("Enter the number of processes: ");
        int numberOfProcesses = scanner.nextInt();

        List<Process> processes = new ArrayList<Process>(numberOfProcesses);
        String processName="";
        Integer processSize=0;
        System.out.print("\n------------------- Input of the info of the "+numberOfProcesses+" processes -------------------");

        for(int i=0;i<numberOfProcesses;i++){
            System.out.println("\n---------- #"+(i+1)+" ----------");
            System.out.println("Process name:");
            processName=scanner.next();
            System.out.println("Process size:");
            processSize=scanner.nextInt();

            processes.add(new Process(processName, processSize, false));
        }
        //Policies
        while(true){
            System.out.println("\n=========================================================================");
            System.out.println("\nSelect the policy you want to apply:\n1. First fit\n2. Best fit\n3. Worst fit\n q To quit program\nSelect policy/choice:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){
                //First fit
                MemAllocator memAllocator= new MemAllocator("First-Fit",memory,processes,partitionCount);
                memAllocator.runFirstFit();

            } else if (choice.equals(new String("2"))) {
                //Best fit
                MemAllocator memAllocator= new MemAllocator("Best-Fit",memory,processes,partitionCount);
                memAllocator.runBestFit();
            } else if (choice.equals(new String("3"))) {
                //Worst fit
                MemAllocator memAllocator= new MemAllocator("Worst-Fit",memory,processes,partitionCount);
                memAllocator.runWorstFit();
            } else if (choice.equals(new String("q"))) {//quit
                break;
            } else{
                System.out.println("Invalid choice, choose from menu.");
            }
        }



    }
}