/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package correction;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
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
	
	public Professor(int assis, int exams){
		this.latch = new CountDownLatch(assis);
		this.assistants = new LinkedList<Assistant>();
		this.stacks = new ExamStack[assis];
		for(int j=0; j<stacks.length;j++){
			stacks[j] = new ExamStack(new LinkedList<Exam>());
		}
		this.distributionCounter = 1;
		this.threads = new Thread[assis];
		for(int i=0; i<assis; i++){
			
			this.assistants.add(new Assistant(i+1, stacks[i], stacks[i-1>=0? i-1 : assis-1], this ));
		}
	}
    
	/**
	 * @author Robin Burghartz
	 * 
	 * this method redistributes the exams of the assistants' stacks from time to time in order to keep the stacks even.
	 * 'from time to time' means: depending on the distribution frequency the method will distribute or it will return immediately;
	 * the higher the value distributionFrequency, the more often the prof will even out stacks, value 1 meaning that the prof will do it
	 * in every iteration of his inner while loop, value 0 meaning that the prof will never do it.
	 * furthermore, after deciding to distribute, the method will look at the stacks and decide if it really makes sense, and - if it
	 * does - determine a clever way to do it, i.e. which stack to take exams from etc. pp. yadayada
	 */
	private void redistribute(){
	
            if(! ((float) (this.distributionCounter) * this.distributionFrequency == 1.0)) return;
		if(waitingAssistants.getN() < 1) return;
		//System.out.println("redistributing");
		// looking for the biggest stack
		int biggest_stack = 0;
		int smallest_stack = Integer.MAX_VALUE;
		for(int i=0; i< stacks.length; i++){
			if(stacks[i].getSize() > biggest_stack) biggest_stack = i;
			if(stacks[i].getSize() < smallest_stack) smallest_stack = i;
		}
		if(biggest_stack==smallest_stack) return;
			
		Exam e = stacks[biggest_stack].profPull();
                List<Exam> rejectedExams = new LinkedList<Exam>();
                
        int size = stacks[biggest_stack].getSize();        //TODO : just for debugging
		while(e!=null && size > 0){
			
			if(e.exercisesToDo().contains(smallest_stack + 1)){
				stacks[smallest_stack].profPush(e);
			}else{
                            rejectedExams.add(e);
                        }
			
			e=stacks[biggest_stack].profPull();
			size--;

		}
                //put other exams back
                for(Exam ex: rejectedExams){
                    stacks[biggest_stack].profPush(ex);
                }
                
        stacks[smallest_stack].distribute();      //TODO: not supposed to be this way, change it  
		this.distributionCounter = 1;
		return;
		
	}
	
	
	/**
	 * @author Robin Burghartz
	 * iterates through the assistants, creates threads for them and starts them
	 * 
	 */
	private void initialize(){
		int i=0;
		for(Assistant a: assistants){
			this.threads[i] = new Thread(a);
			threads[i].start();
			i++;
		}
	}
	
	
	/**
	 * @author Robin Burghartz
	 * divides the exams into equal exam stacks (at the beginning)
	 */
	private void divide(int exams, int exerc){
		
		
		int j = 0;
		for(int i=0; i<exams; i++){
			stacks[j].profPush(new Exam(exerc));
			j = (j+1)%stacks.length;
		}
	}
	
	
	/**
	 * @author Robin Burghartz
	 * @param args
	 */
	public static void main(String args[]){
		
		int assis, exams;
		long runtime = System.currentTimeMillis();
		
		if(args.length ==0){

			assis = 5;

			exams = 1000;
			//System.out.println("Using default values: 4 exercises, 200 exams");
			//System.out.println(""); //TODO
		}
		else if(args.length == 2){
			assis = Integer.parseInt(args[0]);
			exams = Integer.parseInt(args[1]);
		}
		else{
			throw new IllegalArgumentException("illegal no of arguments");
		}
		
		Professor prof = new Professor(assis,exams);
		prof.divide(exams,assis);
		prof.initialize();
		
		// as long as work is not done yet, do this
		while(!prof.shouldTerminate()){
			
			// as soon as all assistants are waiting, this loop will be quit and the professor will check if work is really done
			System.out.println("Starting to finish and redistribute...");
			while(waitingAssistants.getN() < prof.getAssistants().size()){
				if(!prof.finalstack.isEmpty()){
					Exam e = prof.finalstack.removeFirst(); // take the first exam of the final stack
					e.finish();	 							// FINISH HIM!...ehm...it.
				}
				prof.redistribute();						// from time to time even out stacks
<<<<<<< HEAD
				if(System.currentTimeMillis() - runtime >10000) System.out.println("Error in inner while loop. Thread 0 is: "+prof.threads[0].getState()+" Last method in vocation: "+
				prof.threads[0].getStackTrace());
=======
				if(System.currentTimeMillis() - runtime >10000){
                                    System.out.println("the error is in the inner while loop");
                                    System.out.println("wartende"+waitingAssistants.getN());
                                    System.out.println("gesamt" +prof.getAssistants().size());
                                }
>>>>>>> cf6b5ac2a885f06d94d46b2fd1702042167fffb0
			}
			
			// interrupt all assistants so noone has an exam in their hand 
			System.out.println("Interrupting assistants...");
			for(int i=0; i<prof.threads.length; i++){
                                Thread t = prof.threads[i];
                                prof.stacks[i-1>=0? i-1 : assis-1].assiLock(); //TODO: what is this?
				t.interrupt();
                                prof.stacks[i-1>=0? i-1 : assis-1].returnAssiLock();
			}
			
			System.out.println("Waiting for assistants to be interrupted... (Creating Latch)");
			// using a latch to make sure that every assistant is actually interrupted before proceeding
			try {
				prof.latch.await();
                                Professor.waitingAssistants.setN(0);
			} catch (InterruptedException e1) {
				throw new IllegalStateException("bla");
			}
			
			// we assume that no exams are left -> we can terminate
			prof.done(true);
			System.out.println("Checking stacks for missed exams...");
			for(int i=0; i<prof.stacks.length;i++){
				// if we find an exam, we were wrong and work is not done yet
				if(!prof.stacks[i].isEmpty()) prof.done(false);
			}
			
			
			// latch has been used, get a new one
			prof.latch = new CountDownLatch(assis);
			
			System.out.println("Work done: "+prof.shouldTerminate()+". Waking up assistants...");
			// wake up threads so they can check whether work is done or not
			for(Assistant a: prof.assistants){
				synchronized(a){
					a.notifyAll();
				}
				
			}
			
			System.out.println("Finishing left exams...");
			// if the assistants have done their work, the professor might still have to finish some exams, we cannot terminate just yet
			if(prof.shouldTerminate()){
				while(!prof.finalstack.isEmpty()){
					Exam e = prof.finalstack.removeFirst();
					e.finish();
				}
			}
                        //reset static variables so that multible runs don't create problems
                        waitingAssistants = new IdleAssistantsCounter();
			
			
		}
		
		System.out.println("Work is done. Waiting for assistants to terminate...");
		// when the professor wants to terminate, wait for other threads to terminate first
		try {
			prof.latch.await();
		} catch (InterruptedException e) {
			throw new IllegalStateException();
		}
		prof.latch = new CountDownLatch(assis);
		System.out.println(System.currentTimeMillis() - runtime);
		
		
		
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


   public synchronized boolean shouldTerminate(){
       return terminate;
   } 
   
   private synchronized void done(boolean b){
	   terminate = b;
   }
   
   public synchronized void countdownLatch(){
	   latch.countDown();
   }
    
   public void pushFinalStack(Exam e){
	   finalstack.addLast(e);
   }
    
}
