/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package correction;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author bluttrinker
 */
public class Professor {

    private CountDownLatch latch;
    private LinkedList<Assistant> assistants;
    private volatile boolean terminate = false; // used later to finish work
    public static IdleAssistantsCounter waitingAssistants = new IdleAssistantsCounter();
    private final float distributionFrequency = 1.0f; //TODO using a float here will cause problems, change that
    private int distributionCounter;
    private ExamStack[] stacks;
    private Deque<Exam> finalstack = new LinkedBlockingDeque<Exam>();
    private Thread[] threads;
    private int numberAssistants;
    

    public Professor(int assis, int exams) {
        this.numberAssistants=assis;
        this.latch = new CountDownLatch(assis);
        this.assistants = new LinkedList<Assistant>();
        this.stacks = new ExamStack[assis];
        for (int j = 0; j < stacks.length; j++) {
            stacks[j] = new ExamStack(new LinkedList<Exam>());
        }
        this.distributionCounter = 1;
        this.threads = new Thread[assis];
        for (int i = 0; i < assis; i++) {

            this.assistants.add(new Assistant(i + 1, stacks[i], stacks[i - 1 >= 0 ? i - 1 : assis - 1], this));
        }
    }

    /**
     * @author Robin Burghartz, Immo Stanke
     *
     * this method redistributes the exams of the assistants' stacks from time
     * to time in order to keep the stacks even. 'from time to time' means:
     * depending on the distribution frequency the method will distribute or it
     * will return immediately; the higher the value distributionFrequency, the
     * more often the prof will even out stacks, value 1 meaning that the prof
     * will do it in every iteration of his inner while loop, value 0 meaning
     * that the prof will never do it. If the prof decides to distribute, he will 
     * first look at the size of all stacks and then distribute in a way that is 
     * supposed to even them out as good as possible. He also only puts exams on 
     * stacks where he knows that the assistant working on this stack corrects an 
     * exercise that remains to be done in the exam in question.
     */
    private void redistribute() {

        if (!((float) (this.distributionCounter) * this.distributionFrequency == 1.0)) {
            return;
        }
        if (waitingAssistants.getN() < 1) {
            return;
        }

     
        
        
        
        //We first map size to stack, then sort the array of sizes and then can get the
        //corresponding ExamStack using our map. If two ExamStacks have the same size,
        //we don't care wich one we use.
        Map<Integer, Integer> sizeToStack = new HashMap<Integer, Integer>();
        int[] sizeArr = new int[stacks.length];
        //we also calculate the sum of sizes to later get the average size of a stack
        float sizeSum = (float) 0.0;
        for (int i = 0; i < stacks.length; i++) {
            //get current size
            int size = stacks[i].getSize();
            //put size in size array (wich we later sort)
            sizeArr[i] = size;
            //map size to current stack
            sizeToStack.put(size, i);
            //Sum up size.
            sizeSum += (float) size;
//			if(stacks[i].getSize() > biggest_stack) biggest_stack = i;
//			if(stacks[i].getSize() < smallest_stack) smallest_stack = i;
        }
        //get average size
        float sizeAverage = sizeSum / (float) stacks.length;
        //sort the sizes
        Arrays.sort(sizeArr);


        //if(biggest_stack==smallest_stack) return;

        //now, get size of  biggest Stack
        int size = sizeArr[stacks.length - 1];
        //and the biggest Stack itself
        int biggest_stack = sizeToStack.get(size);
       
        //These are the exams that can only be corrected on the biggest stack
        List<Exam> rejectedExams = new LinkedList<Exam>();
        //remember on wich stacks we put something so we can call distribute on those later.
        Set<Integer> refilledStacks = new HashSet<Integer>();

         //get first exam of biggest stack	
        Exam e = stacks[biggest_stack].profPull();
        //loop until we have seen all exams of the biggest stack or reduced it's
        //size below average
        while (e != null && size > sizeAverage) {            

            boolean distributed = false;
            //get unfinished exercises of actual exam
            List<Integer> unfinishedExercises = e.exercisesToDo();
            //put exam on first stack on wich one of the unfinished Exercises can be corrected
            for (int stackSizeRank = 0; stackSizeRank < stacks.length - 1; stackSizeRank++) {
                //get size with this rank
                int currentSize = sizeArr[stackSizeRank];
                //get number of examStack mapped to this size
                int currentStackNr = sizeToStack.get(currentSize);
                if (unfinishedExercises.contains(currentStackNr + 1)) {
                    //if the currentStack can correct one of the unfinished Exercises
                    //push it there
                    stacks[currentStackNr].profPush(e);
                    distributed = true;
                    refilledStacks.add(currentStackNr);
                    break;
                }
            }//end for
            //if we didn't push the exam to another stack, this means only this stack can
            //correct it. Thus we later add it there again.
            if (!distributed) {
                rejectedExams.add(e);
                refilledStacks.add(biggest_stack);
            } else {
                //only if we really took something of our stack we reduce the size
                size--;
            }

            //at last, get next exam of biggest stack.
            e = stacks[biggest_stack].profPull();

        }//end while
        //if we ended the while loop because of the size, there might still be 
        //one last exam we didn't distribute. Just put it back.
        if(e!=null){
            rejectedExams.add(e);
            refilledStacks.add(biggest_stack);
        }
        
        
        
        
        //put other exams back
        for (Exam ex : rejectedExams) {
            stacks[biggest_stack].profPush(ex);
        }

        for (int refilledStack : refilledStacks) {
            stacks[refilledStack].distribute();        
        }
        this.distributionCounter++;
    

    }

    /**
     * @author Robin Burghartz iterates through the assistants, creates threads
     * for them and starts them
     *
     */
    private void initialize() {
        int i = 0;
        for (Assistant a : assistants) {
            this.threads[i] = new Thread(a);
            threads[i].start();
            i++;
        }
    }

    /**
     * @author Robin Burghartz divides the exams into equal exam stacks (at the
     * beginning)
     */
    private void divide(int exams, int exerc) {


        int j = 0;
        for (int i = 0; i < exams; i++) {
            stacks[j].profPush(new Exam(exerc));
            j = (j + 1) % stacks.length;
        }
    }

    /**
     * @author Robin Burghartz, Immo Stanke
     * @param args
     */
    public static void main(String args[]) {

        int assis, exams;
        long runtime = System.currentTimeMillis();

        if (args.length == 0) {

            assis = 5;

            exams = 1000;
            //System.out.println("Using default values: 4 exercises, 200 exams");
            //System.out.println(""); //TODO
        } else if (args.length == 2) {
            assis = Integer.parseInt(args[0]);
            exams = Integer.parseInt(args[1]);
        } else {
            throw new IllegalArgumentException("Illegal number of arguments");
        }
        
        //create new professor
        Professor prof = new Professor(assis, exams);
        //divide the exams on the stacks
        prof.divide(exams, assis);
        //initialization phase
        prof.initialize();
        //do the actual work of
        prof.run();
        
    }

    
    private void run(){
        // as long as work is not done yet, do this
        while (!shouldTerminate()) {

            // as soon as all assistants are waiting, this loop will be quit and the professor will check if work is really done
        
            while (waitingAssistants.getN() < getAssistants().size()) {
                if (!finalstack.isEmpty()) {
                    Exam e = finalstack.removeFirst(); // take the first exam of the final stack
                    e.finish();         		// FINISH HIM!...ehm...it.
                                           
                }
                redistribute();				// from time to time even out stacks

            }

            // interrupt all assistants so noone has an exam in their hand 
            System.out.println("Interrupting assistants...");
            for (int i = 0; i < threads.length; i++) {
                Thread t = threads[i];
                //this makes sure that we don't interrupt an assistant while he pushes or pops an exam,
                //except when he is waiting because of an empty stack.
                stacks[i - 1 >= 0 ? i - 1 : numberAssistants - 1].assiLock();                 
                t.interrupt();
                stacks[i - 1 >= 0 ? i - 1 : numberAssistants - 1].returnAssiLock();
            }

            System.out.println("Waiting for assistants to be interrupted... (Creating Latch)");
            // using a latch to make sure that every assistant is actually interrupted before proceeding
            try {
                latch.await();
                Professor.waitingAssistants.setN(0);
            } catch (InterruptedException e1) {
                throw new IllegalStateException("Professor was interrupted while waiting for his latch.");
            }

            // we assume that no exams are left -> we can terminate
            done(true);
            System.out.println("Checking stacks for missed exams...");
            for (int i = 0; i < stacks.length; i++) {
                // if we find an exam, we were wrong and work is not done yet
                if (!stacks[i].isEmpty()) {
                    done(false);
                }
            }


            // latch has been used, get a new one
            latch = new CountDownLatch(numberAssistants);

            System.out.println("Work done: " + shouldTerminate() + ". Waking up assistants...");
            // wake up threads so they can check whether work is done or not
            for (Assistant a : assistants) {
                synchronized (a) {
                    a.notifyAll();
                }

            }

            System.out.println("Finishing left exams...");
            // if the assistants have done their work, the professor might still have to finish some exams, we cannot terminate just yet
            if (shouldTerminate()) {
                while (!finalstack.isEmpty()) {
                    Exam e = finalstack.removeFirst();
                    e.finish();                 
                }
            }

        }

        System.out.println("Work is done. Waiting for assistants to terminate...");
        // when the professor wants to terminate, wait for other threads to terminate first
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
        latch = new CountDownLatch(numberAssistants);
        

        //reset static variables so that multible runs don't create problems
        waitingAssistants.setN(0);
      
   
    }
    
    // getters and setters
    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }

    public LinkedList<Assistant> getAssistants() {
        return assistants;
    }

    public void setAssistants(LinkedList<Assistant> assistants) {
        this.assistants = assistants;
    }

    public synchronized boolean shouldTerminate() {
        return terminate;
    }

    private synchronized void done(boolean b) {
        terminate = b;
    }

    public synchronized void countdownLatch() {
        latch.countDown();
    }

    public void pushFinalStack(Exam e) {
        finalstack.addLast(e);
    }
}
