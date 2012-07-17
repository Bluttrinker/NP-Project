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
<<<<<<< HEAD
       arguments[0]= "3";
       arguments[1]= "70";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=400; i++){
=======
       arguments[0]= "5";
       arguments[1]= "100";
        for(int i=0; i<=200; i++){
>>>>>>> cf6b5ac2a885f06d94d46b2fd1702042167fffb0
            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
      
        
        
    }
}
