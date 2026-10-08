README.md — Structured Reflection Template
--------------------------------------------

The badge below indicates if your submission compiled correctly. It may take some time to update. You can click the badge to see the jobs page and failure traces.

You must ensure your project compiles correctly.

[![Pipeline status](https://gitlab.surrey.ac.uk/csee/com1032/2025-26/com1032mn01341/badges/main/pipeline.svg)](https://gitlab.surrey.ac.uk/csee/com1032/2025-26/com1032mn01341/-/jobs/)

For each stage (0–3), complete the table below.
* 50 to 100 words per stage.
* Bullet points only.

Stage 0 (5%)
--------------
Item:	                    
**Key Implementation:** 
- CPU.java run method   
- runStage0SingleScript in Main.java    
**One Bug Encountered:**	
- runStage0SingleScript returning null causing NullPointerException     
**How You Diagnosed It:**   
- Looked at where in the unit testing stage there was an issue
- Saw that the method was returning 'null'
- Checked the method to see what was returning 'null'  
**What You Changed:**	    
- The last line returned null in all cases of the code running    
- Removed said line and returned the PCB itself instead of 'null'


Stage 1 — FCFS (5%)
----------------------
Item	                    Your Answer (Bullet Points Only)

**Key Implementation:** 
- used a while loop to process PCBs in ready queue until empty
- Returned and removed processes in order of arrival using Poll() method
- Created and executed  CPU thread per PCB and executed it
- Updated PCB state to "terminated" and set burst time to 0 after completion
- Added each completed PCB to event log in order of execution
**One Bug Encountered**	
- No processes were executed and added to log instead of all
**How You Diagnosed It**	
- Unit test expected 4 completed processes, log obtained none
- Reviewed control flow and noticed "if" statement instead of "while" loop
- Reviewed format of readFile() method (some PCBs get no priority)
What You Changed	
- Changed if (!readyQueue.isEmpty()) {...} to while (!readyQueue.isEmpty()) {...}
- Changed readFile() method to give default priority of 1 to PCBs initialised with no priority.


Stage 2 — Round Robin (5%)
-----------------------------
**Item**	                    Your Answer (Bullet Points Only) 

**Key Implementation**	
- Implemented RR as single time slice per call to method
- Removed head of readyQueue per call
- Calculate simulated run time using min(remainingBurstTime, timeQuantum)
- update remaining burst time of PCB by subtracting runTime
- update PCB execution time with addExecutedTime(runTime)
- if process unfinished, increment context switch and reset state to ready. Then append to readyQueue
- If finished, run CPU process to completion and terminate state. Add to EventLog
**One Bug Encountered**     
Executing time not updated
**How You Diagnosed It**
- Looked at my method and analysed logic - no issues there
- Looked at JobQueue as RR() uses logic from said class
**What You Changed**
- In the last line, a built-in method was being used to add the PCB to queue instead of the one that was made. I simply changed it to addToQueue().... and this fixed it.	

Stage 3 — Priority (5%)
-------------------------------
Item	                    Your Answer (Bullet Points Only)
**Key Implementation**	
- Select highest priority PCB from the PCB by looking through each item in readyQueue
- Only considered processes that have arrived before current time
- Implemented priority comparison with arrivalTime tie-break for selection
- Removed selected PCB before execution to prevent reprocessing
- Executed process until complete and advanced system time
- Logged PCB termination once executed
**One Bug Encountered**	
- Process execution order was 1-2-3-4 instead of 1-4-3-2
How You Diagnosed It
- Looked at how time was being advanced
- Realised that only execution time was being advanced instead of system time as a whole
What You Changed	
- Added the line Main.advanceTime(chosen.getCPUBurstTime());
