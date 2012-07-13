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

    public Assistant(int assignedExercise) {
        this.assignedExercise = assignedExercise;
    }
    
    
    
    @Override
    public void run() {
        while(!Professor.shouldTerminate()){
            while(!Thread.interrupted()){
                
            }
            
            
        }
       //terminate
    }
    
}
