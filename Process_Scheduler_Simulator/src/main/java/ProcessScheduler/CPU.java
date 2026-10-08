package ProcessScheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
/**
 * CPU Thread. Runs a python script from a given PCB.
 *
 */
public class CPU extends Thread {

    /**
     * Instance fields used in constructor for parsing in other objects
     */
    private boolean running = true; // if CPU is actively executing a process
    private ProcessControlBlock PCB = null;

    public CPU(ProcessControlBlock PCB) {
        this.PCB = PCB;
    }
    /**
     * TO DO for Stage 0
     */
    @Override
    public void run() {
        
            // Print that the process is running
            PCB.setState("Running");
            System.out.println(PCB.getPID() +": " + PCB.getState());

            // Build the process to run the Python script
            //note to self: change "python3" to Pythonpat[h before final commit!!!!
            String processPath = PCB.getProcessPath();
            ProcessBuilder p = new ProcessBuilder("python3", processPath);
            p.redirectErrorStream(true);
            Process process = null;
            String scriptOutput = "";
            
            try {
                // Wait for the script to finish execution
                process = p.start();
               process.waitFor();
                // Read the script output
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                scriptOutput = reader.lines().collect(Collectors.joining("\n")).trim();

            } catch (IOException | InterruptedException e) {
                // TODO Auto-generated catch block
                System.out.println(e);
                
            }
            // Update the PCB with the script's output
            PCB.setPCBResult(scriptOutput);

            // Print the output of the process
            System.out.println(PCB.getPCBResult());

            // Mark the CPU as finished
            this.running = false;
    }

    public ProcessControlBlock getPCB() {
        return this.PCB;
    }

    public boolean getRunning() {
        return this.running;
    }

}
