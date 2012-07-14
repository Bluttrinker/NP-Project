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


    public Assistant(int assignedExercise, ExamStack l, ExamStack r) {
        this.assignedExercise = assignedExercise;
        this.left = l;
        this.right = r;
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
                	//TODO: put it on the prof's stack here
                }
                else {
                	left.assiPush(e);
                }
            }
            
            
        }
       //terminate
    }
    
}
