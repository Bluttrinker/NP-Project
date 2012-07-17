/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import correction.Professor;

/**
 *
 * @author bluttrinker
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       String[] arguments = new String[2];
       arguments[0]= "3";
       arguments[1]= "70";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=400; i++){
            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
      
        
        
    }
}
