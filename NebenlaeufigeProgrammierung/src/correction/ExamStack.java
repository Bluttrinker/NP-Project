/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package correction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author bluttrinker
 */
public class ExamStack {

    private ArrayList<Exam> assiStack;
    private final Lock assiLock = new ReentrantLock();
    private final Condition assiStackNotEmpty;
    private ArrayList<Exam> profStack;
    private final Lock profLock = new ReentrantLock();
    
    private final int BUFFER_SIZE = 5;

    /**
     * Creates a new ExamStack filled with the list of exams given in the
     * constructor
     *
     * @param initialExams The Exams initially contained by this ExamStack.
     */
    public ExamStack(ArrayList<Exam> initialExams) {
        super();
        this.assiStack = initialExams;
        //I want my diamond operator back...
        this.profStack = new ArrayList<Exam>();
        assiStackNotEmpty = assiLock.newCondition();
    }

    /**
     * Puts an exam on the ExamStack. This method is to be used by the
     * Assistants
     *
     * @param exam The exam to be put on the stack.
     */
    public void assiPush(Exam exam) {
        //Lock Assi Stack
        assiLock.lock();
        //push exam
        try {
            assiStack.add(exam);
            assiStackNotEmpty.signalAll();
        } finally {
            assiLock.unlock();
        }

    }

    /**Takes an exam from the step and returns it
     * this method is to be called from the Assistants
     * 
     * @return The first element on the ExamStack
     * @throws InterruptedException If the assistant has to wait for an exam to arrive on the Stack, but was interrupted in between
     */
    public Exam assiPop() throws InterruptedException {
        Exam exam;
        //Lock Assi Stack
        assiLock.lock();
        
        while (assiStack.isEmpty()) {
            //Maybe there is still something on the profstack
            assiLock.unlock();
            distribute();
            assiLock.lock();
            //Is it still empty?
            if (assiStack.isEmpty()) //Wait for the stack to be not empty
            {                
            	Professor.waitingAssistants.increment();
                assiStackNotEmpty.await();
                Professor.waitingAssistants.decrement();
            }
        }
        //ok, the stack is not empty and we have the lock, so take an element!
        exam = assiStack.remove(0);
        
          assiLock.unlock();  
        
        //we removed the exam, we can now give back the lock
        if(exam!=null)
            return exam;
        //this case should not happen.
        throw new IllegalStateException("Somehow we didn't get an exam even though we where not interrupted");
    }

    public void profPush(Exam exam) {
        //Get Lock for profStack
        profLock.lock();
        try{
            profStack.add(exam);
        }finally{
            //give back the lock no matter what
            profLock.unlock();
        }
    
    }

    public Exam profPull() {
        Exam exam = null;
        profLock.lock();
        try{
            if(profStack.isEmpty()){
                profLock.unlock();
                distribute();
                profLock.lock();
            }
                
            if(!profStack.isEmpty())
                //remove last element so we don't have to shift 
                //for prof the order doesn't matter since he iterates anyways.
                exam = profStack.remove(profStack.size()-1);
        }finally{
            profLock.unlock();
        }
        //Give this back, if it is null the prof knows he saw (almost) the entire stack
        return exam;
    }

    private void distribute() {
        //We need both locks!
        assiLock.lock();
        profLock.lock();
        try{
            //if both are empty, skip the rest.
            if(!assiStack.isEmpty() || !profStack.isEmpty()){
                if(assiStack.isEmpty()){
                    //ok, so assi Stack is empty, prof Stack not.
                    //put some elements on the assiStack.
                    fillAssiStack();
                }else{
                    //then prof stack must be empty, check this!
                    if(!profStack.isEmpty())
                        throw new IllegalArgumentException("One of the two stacks has to be empty!");
                    //ok, so prof stack is empty, but assi stack is not.
                    //put all elements on the prof stack
                    profStack = assiStack;
                    assiStack.clear();
                    //now put some back on the assiStack
                    fillAssiStack();           
                    
                }
            
            }            
            
        }finally{
            //give locks back no matter what
            assiLock.unlock();
            profLock.unlock();
        }
        
    
        
    }
    private void fillAssiStack(){    
        int movedElements =0;
                    Iterator iter = profStack.iterator();
                    while(movedElements<BUFFER_SIZE && iter.hasNext()){
                        Exam e = (Exam)iter.next();
                        assiStack.add(e);                        
                    }
                    profStack.removeAll(assiStack);
    }
    
    public void assiLock(){        
        assiLock.lock();
    }
    
    public void returnAssiLock(){
        assiLock.unlock();
    }
    
    public boolean isEmpty(){
    	assiLock.lock();
    	profLock.lock();
    	boolean b;
    	try{
    		b = profStack.isEmpty() && assiStack.isEmpty();
    	}
    	finally{
    		assiLock.unlock();
    		profLock.unlock();
    	}
    	return b;
    }
    
    public int getSize(){
    	assiLock.lock();
    	profLock.lock();
    	int n;
    	try{
    		n = assiStack.size() + profStack.size();
    	}
    	finally{
    		assiLock.unlock();
    		profLock.unlock();
    	}
    	return n;
    }
}
