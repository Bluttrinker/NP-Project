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
       arguments[0]= "5";
       arguments[1]= "1000";
        for(int i=0; i<=10; i++){
            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
      
        
        
    }
}
