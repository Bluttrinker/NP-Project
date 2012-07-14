/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package correction;

/**
 *
 * @author bluttrinker
 */
public class Assistant implements Runnable {

    private int assignedExercise;
    private ExamStack left, right;
    private Professor prof;


    public Assistant(int assignedExercise, ExamStack l, ExamStack r, Professor p) {
        this.assignedExercise = assignedExercise;
        this.left = l;
        this.right = r;
        this.prof = p;
    }
    
    
    
    @Override
    public void run() {
        while(!Professor.shouldTerminate()){
            
        	while(!Thread.interrupted()){
            	Exam e;
            	try{
                e = right.assiPop();
            	}
            	catch(InterruptedException ex){
            		break;
            	}
                e.correct(assignedExercise);
                if(e.isCorrected()){
                	prof.pushFinalStack(e);
                }
                else {
                	left.assiPush(e);
                }
                
            }
        	
        	synchronized(this){
            prof.countdownLatch();
            
            try{
            	wait(); 
            	}
            
            catch(InterruptedException ex){
            	throw new IllegalStateException("bla");
            }
        
            
        }
    }
        prof.countdownLatch();
    
}
}
