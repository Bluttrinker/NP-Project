/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Project;

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
	public static volatile boolean terminate = false; // used later to finish work
	public static int waitingAssistants = 0;
	private final float distributionFrequency = 0.2f;
	private int distributionCounter;
	private Exam[] exams;
	private Deque<Exam> finalstack = new LinkedBlockingDeque<Exam>();
	Thread[] threads;
	
	public Professor(int assis, int exams){
		this.latch = new CountDownLatch(assis);
		this.assistants = new LinkedList<Assistant>();
		this.distributionCounter = 1;
		for(int i=0; i<assis; i++){
			//TODO: need to give assistants a reference to their exam stacks, however I need two more parameters in the assistant's constr.
			this.assistants.add(new Assistant(i));
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
	 * does - determine a clever way to do it, i.e. which stack to take exams from etc.
	 */
	private void redistribute(){
		if(! ((float) (this.distributionCounter) * this.distributionFrequency == 1.0)) return;
		//TODO : distribute the exams here
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
		for(Assistant a: assistants){
			if(i>=exams.length) break;
			a.getLeftStack().profPush(exams[i]);
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
			while(waitingAssistants < prof.getAssistants().size()){
				Exam e = prof.finalstack.removeFirst(); // take the first exam of the final stack
				e.finish();								// FINISH HIM!...ehm...it.
				prof.redistribute();					// from time to time evn out stacks
			}
			
			for(Thread t: prof.threads){
				t.interrupt();
			}
			
			prof.latch.await();
			terminate = true;
			for(Assistant a: prof.assistants){
				if(!a.getLeftStack.isEmpty) terminate = false;
			}
			
			//TODO: remove hardcoded value
			prof.latch = new CountDownLatch(4);
			
			// wake up threads so they can check whether work is done or not
			for(Thread t: prof.threads) t.notify();
		}

		
		
		
	}

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
    
    
}
