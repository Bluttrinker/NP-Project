/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testing;

import correction.Professor;
import java.io.IOException;

/**
 *
 * @author bluttrinker
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
       String[] arguments = new String[2];
       //System.in.read();
       
       arguments[0]= "42";
       arguments[1]= "10000";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=100; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
        
        arguments[0]= "5";
       arguments[1]= "100";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=1000; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
       
       
       arguments[0]= "100";
       arguments[1]= "100";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=1000; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
      
        arguments[0]= "5";
       arguments[1]= "1000";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=50; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
        
        arguments[0]= "4";
       arguments[1]= "1000";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=50; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
        
        arguments[0]= "10";
       arguments[1]= "50";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=1000; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
        
        arguments[0]= "7";
       arguments[1]= "135";
       System.out.println("Test run with "+arguments[0]+" exercises and "+arguments[1]+ " exams.");
        for(int i=0; i<=1000; i++){

            System.out.println("run nr"+i);
              Professor.main(arguments);
              
        }
        
        
    }
}
