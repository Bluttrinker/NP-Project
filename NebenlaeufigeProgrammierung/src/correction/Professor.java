/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package correction;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *
 * @author bluttrinker
 */
public class Professor {
	
	private CountDownLatch latch; 					  
	private LinkedList<Assistant> assistants;
	private static volatile boolean terminate = false; // used later to finish work
	public static IdleAssitantsCounter waitingAssistants = new IdleAssitantsCounter();
	private final float distributionFrequency = 0.2f;
	private int distributionCounter;
	private Exam[] exams; 
	private ExamStack[] stacks;
	private Deque<Exam> finalstack = new LinkedBlockingDeque<Exam>();
	private Thread[] threads;
	
	public Professor(int assis, int exams){
		this.latch = new CountDownLatch(assis);
		this.assistants = new LinkedList<Assistant>();
		this.stacks = new ExamStack[assis];
		this.distributionCounter = 1;
		for(int i=0; i<assis; i++){
			// TODO: maybe put this in initialize() 
			this.assistants.add(new Assistant(i+1, stacks[i], stacks[i-1>=0? i-1 : assis-1] ));
		}
		this.exams = new Exam[exams];
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
		//TODO : distribute the exams here, mayyyyybe do this in a separate distributor class
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
	private void divide(){
		
		int i=0;
		
		while(i<exams.length){
		for(int j=0; j<stacks.length;j++){
			if(i>=exams.length) break;
			stacks[j].profPush(exams[i]);
			i++;
		}
		}
	}
	
	
	/**
	 * @author Robin Burghartz
	 * @param args
	 */
	public static void main(String args[]){
		
		//TODO: remove hardcoded values, implement with arguments
		Professor prof = new Professor(4,200);
		prof.divide();
		prof.initialize();
		
		// as long as work is not done yet, do this
		while(!terminate){
			
			// as soon as all assistants are waiting, this loop will be quit and the professor will check if work is really done
			while(waitingAssistants.getN() < prof.getAssistants().size()){
				Exam e = prof.finalstack.removeFirst(); // take the first exam of the final stack
				e.finish();								// FINISH HIM!...ehm...it.
				prof.redistribute();					// from time to time even out stacks
			}
			
			// interrupt all assistants so noone has an exam in their hand 
			for(Thread t: prof.threads){
				t.interrupt();
			}
			
			// using a latch to make sure that every assistant is actually interrupted before proceeding
			prof.latch.await();
			
			// we assume that no exams are left -> we can terminate
			terminate = true;
			for(int i=0; i<prof.stacks.length;i++){
				// TODO: not sure if getLeftStack is right...
				// if we find an exam, we were wrong and work is not done yet
				if(prof.stacks[i].isEmpty()) terminate = false;
			}
			
			//TODO: remove hardcoded value
			// latch has been used, get a new one
			prof.latch = new CountDownLatch(4);
			
			// wake up threads so they can check whether work is done or not
			for(Thread t: prof.threads) t.notify();
			
			// if the assistants have done their work, the professor might still have to finish some exams, we cannot terminate just yet
			if(terminate){
				while(!prof.finalstack.isEmpty()){
					Exam e = prof.finalstack.removeFirst();
					e.finish();
				}
			}
		}

		
		
		
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

	public Exam[] getExams() {
		return exams;
	}

	public void setExams(Exam[] exams) {
		this.exams = exams;
	}

   public static synchronized boolean shouldTerminate(){
       return terminate;
   } 
    
    
}
