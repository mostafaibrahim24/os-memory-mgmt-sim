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
        this.policy = policy;
        this.memory = new ArrayList<Partition>();
        for (int i = 0; i < memory.size(); i++) {
            this.memory.add(new Partition(memory.get(i).getPartitionName(),memory.get(i).getPartitionSize(),"",memory.get(i).getIsOccupied()));
        }
        this.processes=new ArrayList<Process>();
        for (int i = 0; i < processes.size(); i++) {
            this.processes.add(new Process(
                    processes.get(i).getProcessName(),
                    processes.get(i).getProcessSize(),
                    processes.get(i).getIsAllocated()));
        }
        this.partitionCount=partitionCount;
    }
    public void runFirstFit(){
        if(policy!="First-Fit"){
            return;
        }
        Integer count=0;
        Scanner scanner= new Scanner(System.in);
        while(true){
            for (int i = 0; i < processes.size(); i++) {
                for (int j = 0; j < memory.size(); j++) {
                    if(!memory.get(j).getIsOccupied() && processes.get(i).getProcessSize()<=memory.get(j).getPartitionSize()){
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
            if(count>0){break;}
            count++;
            System.out.print("\nDo you want to compact?\n1. yes\n2. no\nSelect 1 or 2:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){
                notAllocated="\n";
                int freeMemorySize=0;
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getProcessInPartition() == "") {
                        freeMemorySize+=memory.get(i).getPartitionSize();
                    }
                }

                List<Partition> memoryOccupied=new ArrayList<Partition>();
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getIsOccupied()==true) {
                        memoryOccupied.add(memory.get(i));
                    }
                }
                memory=memoryOccupied;
                List<Process> leftProcesses=new ArrayList<Process>();
                for (int i = 0; i < processes.size(); i++) {
                    if(processes.get(i).getIsAllocated()==false){
                        leftProcesses.add(processes.get(i));
                    }
                }
                processes=leftProcesses;
                partitionCount++;
                Partition freeMemoryPartition = new Partition("Partition"+partitionCount,freeMemorySize,"",false);
                memory.add(freeMemoryPartition);
            } else if (choice.equals(new String("2"))) {
                break;
            }else{
                System.out.println("Invalid choice");
                break;
            }
        }


    }
    private Partition getMaxDiff(Integer processSize){
        Integer MIN=0;
        for (int i = 0; i < memory.size(); i++) {MIN+=memory.get(i).getPartitionSize();}
        Partition maxDiffPartition=new Partition("None",-MIN,"None",true);
        for (int i = 0; i < memory.size(); i++) {
            if(!memory.get(i).getIsOccupied()){
                Integer currentDiff=memory.get(i).getPartitionSize()-processSize;
                if(currentDiff>=0 && currentDiff>maxDiffPartition.getPartitionSize()-processSize){
                    maxDiffPartition=memory.get(i);
                }
            }
        }

        return maxDiffPartition;
    }
    public void runWorstFit(){
        if(policy!="Worst-Fit"){
            return;
        }
        String notAllocated="\n";
        Integer count=0;
        Scanner scanner= new Scanner(System.in);
        while(true){
            for (int i = 0; i < processes.size(); i++) {
                Partition worstFitPartition=getMaxDiff(processes.get(i).getProcessSize());
                if(worstFitPartition.getPartitionName()=="None"){
                    notAllocated+=processes.get(i).getProcessName()+" can not be allocated\n";
                }else{
                    processes.get(i).setIsAllocated(true);
                    if(worstFitPartition.getPartitionSize()>processes.get(i).getProcessSize()){
                        Integer remainingSize=worstFitPartition.getPartitionSize()-processes.get(i).getProcessSize();

                        worstFitPartition.setPartitionSize(processes.get(i).getProcessSize());
                        memory.get(memory.indexOf(worstFitPartition)).setPartitionSize(processes.get(i).getProcessSize());

                        partitionCount++;
                        Partition newPartition = new Partition("Partition"+partitionCount,remainingSize,"",false);
                        memory.add(memory.indexOf(worstFitPartition)+1,newPartition);
                    }
                    worstFitPartition.setIsOccupied(true);
                    memory.get(memory.indexOf(worstFitPartition)).setIsOccupied(true);

                    worstFitPartition.setProcessInPartition(processes.get(i).getProcessName());
                    memory.get(memory.indexOf(worstFitPartition)).setProcessInPartition(processes.get(i).getProcessName());
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
            if(count>0){break;}
            count++;
            System.out.print("\nDo you want to compact?\n1. yes\n2. no\nSelect 1 or 2:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){
                notAllocated="\n";
                int freeMemorySize=0;
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getProcessInPartition() == "") {
                        freeMemorySize+=memory.get(i).getPartitionSize();
                    }
                }

                List<Partition> memoryOccupied=new ArrayList<Partition>();
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getIsOccupied()==true) {
                        memoryOccupied.add(memory.get(i));
                    }
                }
                memory=memoryOccupied;
                List<Process> leftProcesses=new ArrayList<Process>();
                for (int i = 0; i < processes.size(); i++) {
                    if(processes.get(i).getIsAllocated()==false){
                        leftProcesses.add(processes.get(i));
                    }
                }
                processes=leftProcesses;
                partitionCount++;
                Partition freeMemoryPartition = new Partition("Partition"+partitionCount,freeMemorySize,"",false);
                memory.add(freeMemoryPartition);
            } else if (choice.equals(new String("2"))) {
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
        Partition minDiffPartition = new Partition("None",MAX,"",true);
        for (int i = 0; i < memory.size(); i++) {
            if(!memory.get(i).getIsOccupied()){
                Integer currentDiff=memory.get(i).getPartitionSize()-processSize;
                if(currentDiff>=0 && currentDiff<minDiffPartition.getPartitionSize()-processSize){
                    minDiffPartition=memory.get(i);
                }
            }
        }
        return minDiffPartition;
    }
    public void runBestFit(){
        if(policy!="Best-Fit"){
            return;
        }
        String notAllocated="\n";
        Integer count=0;
        Scanner scanner= new Scanner(System.in);
        while(true){
            for (int i = 0; i < processes.size(); i++) {
                Partition bestFitPartition=getMinDiff(processes.get(i).getProcessSize());
                if(bestFitPartition.getPartitionName()=="None"){
                    notAllocated+=processes.get(i).getProcessName()+" can not be allocated\n";
                }else{
                    processes.get(i).setIsAllocated(true);
                    if(bestFitPartition.getPartitionSize()>processes.get(i).getProcessSize()){
                        Integer remainingSize=bestFitPartition.getPartitionSize()-processes.get(i).getProcessSize();

                        bestFitPartition.setPartitionSize(processes.get(i).getProcessSize());
                        memory.get(memory.indexOf(bestFitPartition)).setPartitionSize(processes.get(i).getProcessSize());

                        partitionCount++;
                        Partition newPartition = new Partition("Partition"+partitionCount,remainingSize,"",false);
                        memory.add(memory.indexOf(bestFitPartition)+1,newPartition);
                    }
                    bestFitPartition.setIsOccupied(true);
                    memory.get(memory.indexOf(bestFitPartition)).setIsOccupied(true);

                    bestFitPartition.setProcessInPartition(processes.get(i).getProcessName());
                    memory.get(memory.indexOf(bestFitPartition)).setProcessInPartition(processes.get(i).getProcessName());
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
            if(count>0){break;}
            count++;
            System.out.print("\nDo you want to compact?\n1. yes\n2. no\nSelect 1 or 2:");
            String choice=scanner.next();
            if(choice.equals(new String("1"))){
                notAllocated="\n";
                int freeMemorySize=0;
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getProcessInPartition() == "") {
                        freeMemorySize+=memory.get(i).getPartitionSize();
                    }
                }

                List<Partition> memoryOccupied=new ArrayList<Partition>();
                for (int i = 0; i < memory.size(); i++) {
                    if (memory.get(i).getIsOccupied()==true) {
                        memoryOccupied.add(memory.get(i));
                    }
                }
                memory=memoryOccupied;
                List<Process> leftProcesses=new ArrayList<Process>();
                for (int i = 0; i < processes.size(); i++) {
                    if(processes.get(i).getIsAllocated()==false){
                        leftProcesses.add(processes.get(i));
                    }
                }
                processes=leftProcesses;
                partitionCount++;
                Partition freeMemoryPartition = new Partition("Partition"+partitionCount,freeMemorySize,"",false);
                memory.add(freeMemoryPartition);
            } else if (choice.equals(new String("2"))){
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
        //Policies
        while(true){
            System.out.println("\n=========================================================================");
            System.out.print("\nSelect the policy you want to apply:\n1. First fit\n2. Best fit\n3. Worst fit\n q To quit program\nSelect policy/choice:");
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
            } else if (choice.equals(new String("q"))) {
                break;
            } else{
                System.out.println("Invalid choice, choose from menu.");
            }
        }



    }
}