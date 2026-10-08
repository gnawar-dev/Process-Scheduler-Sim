package ProcessScheduler;

import java.util.Queue;

/**
 * Scheduler selects which process runs next based on the chosen algorithm.
 * - FCFS
 * - RR (Round Robin)
 * - Non-preemptive priority
 */
public class Scheduler {

    protected final Queue<ProcessControlBlock> readyQueue;
    private final EventLog log;

    private String schedulerAlgorithm;
    private long timeQuantum;

    public Scheduler(Queue<ProcessControlBlock> readyQueue,
            String schedulerAlgorithm,
            long timeQuantum,
            EventLog log) {
        this.readyQueue = readyQueue;
        this.schedulerAlgorithm = schedulerAlgorithm;
        this.timeQuantum = timeQuantum;
        this.log = log;
    }

    public String getAlgorithm() {
        return schedulerAlgorithm;
    }

    public long getQuantum() {
        return timeQuantum;
    }

    /**
     * Executes exactly ONE scheduling decision per call.
     * 
     * @return stage number for convenience (1=FCFS, 2=RR, 3=PRIORITY), or -1 if
     *         unknown.
     */
    public int runAlgorithm() {
        if (schedulerAlgorithm == null)
            return -1;

        if (schedulerAlgorithm.equalsIgnoreCase("FCFS")) {
            FCFS();
            return 1;
        }

        if (schedulerAlgorithm.equalsIgnoreCase("RR")) {
            RR();
            return 2;
        }

        if (schedulerAlgorithm.equalsIgnoreCase("PRIORITY")
                || schedulerAlgorithm.equalsIgnoreCase("NPPRIORITY")
                || schedulerAlgorithm.equalsIgnoreCase("NPP")
                || schedulerAlgorithm.equalsIgnoreCase("Priority")) {
            nonPreemptivePriority();
            return 3;
        }

        return -1;
    }

    /**
     * Stage 1 — FCFS
     * Runs the first process in the ready queue to completion.
     * Remove the first PCB
     * Run it using CPU
     * Wait for completion
     * Set state to "terminated"
     * Update its execution time
     * Set the remaining burst time to be zero
     * Add PCB to EventLog
     * print the details of each completed PCB. For example,e.g., P01: terminated, Context Switches: 0, Output: Sum is 9, Execution Time: 30ms
     */
    public void FCFS() {
        ProcessControlBlock pcb;
        CPU cpu;
        
        while (!readyQueue.isEmpty()) {
            pcb = readyQueue.poll();
            cpu = new CPU(pcb);

            cpu.run();
        try {
            cpu.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Thread.currentThread().interrupt();
        }

            cpu.getPCB().setState("terminated");
            cpu.getPCB().setCPUBurstTime(0);
            cpu.getPCB().setArrivalTime(timeQuantum);
            log.addPCB(pcb);
            
        }
        System.out.println(log.getPCBLog().toString());

    }

    /**
     * Stage 2 — Round Robin (simulated CPU burst; script executes once at completion)
     * - Remove head of readyQueue
     * - Run for min(remainingBurstTime, timeQuantum) (simulation)
     * - If unfinished: contextSwitches++, state="ready", re-add to END
     * - If finished: run Python once, terminate and log 
    */
    public void RR() {
        
        final ProcessControlBlock pcb;
        CPU cpu;
        
        if (readyQueue.isEmpty()) {
            return;
        }
            pcb = readyQueue.poll();
            
            cpu = new CPU(pcb);
            long runTime = Math.min(cpu.getPCB().getRemainingBurstTime(), timeQuantum);
            //long temp;

            long rbt = cpu.getPCB().getRemainingBurstTime();

            System.out.println("Remaining burst time = " + cpu.getPCB().getRemainingBurstTime());
            cpu.getPCB().setRemainingBurstTime(rbt - runTime);

            cpu.getPCB().addExecutedTime(runTime);
            //Main.advanceTime(runTime);
            System.out.println("new RBT: " + cpu.getPCB().getRemainingBurstTime());

            boolean running = (cpu.getPCB().getRemainingBurstTime() > 0);

        if (running) {
            readyQueue.add(cpu.getPCB());
            //cpu.getPCB().setRemainingBurstTime(pcb.getRemainingBurstTime() - runTime);
            System.out.println("This code is running "+ cpu.getPCB().getRemainingBurstTime());

            cpu.getPCB().addContextSwitch();
            cpu.getPCB().setState("ready");
            System.out.println("rQ post readd: " + readyQueue);
                   
        } else {
            cpu.start();
            try {
                cpu.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cpu.getPCB().setState("terminated");
            log.addPCB(cpu.getPCB());
            System.out.println(log);
        }
        
}
    
    /**
     * Stage 3 — Non-Preemptive Priority Scheduling
     *
     * Picks the highest-priority process from readyQueue (higher number = higher
     * priority).
     * Tie-break: earlier arrivalTime first.
     * Non-preemptive: once selected, runs to completion.
     */
    public void nonPreemptivePriority() {
        ProcessControlBlock chosen = readyQueue.peek();
        ProcessControlBlock pcb;
        CPU cpu;
        long now = Main.getCurrentTime();
        System.out.println("rQ pre anything = " + readyQueue);
        if (readyQueue.isEmpty()) {
            return;
        }
        for (int i = 0; i < readyQueue.size(); i++) {

            pcb = readyQueue.poll();
            System.out.println("polled PCB = " + pcb.getPID());
            try {
                System.out.println("Current chosen: " + chosen.getPID());
            } catch (Exception e) {
            }
            if (pcb.getArrivalTime() <= now) {
                if (pcb.getPriority() > chosen.getPriority()) {
                    chosen = pcb;
                } else if (pcb.getPriority() == chosen.getPriority() && pcb.getArrivalTime() < chosen.getArrivalTime()) {
                    chosen = pcb;
                }
            }
            readyQueue.add(pcb);
            }
        
        if (chosen != null) {
            readyQueue.remove(chosen);
            cpu = new CPU(chosen);
            
            cpu.start();
            try {
                cpu.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Main.advanceTime(chosen.getCPUBurstTime());
            chosen.addExecutedTime(chosen.getCPUBurstTime());
            cpu.getPCB().setState("terminated");
            log.addPCB(cpu.getPCB());
        }
        System.out.println(log.toString());
    }
}
