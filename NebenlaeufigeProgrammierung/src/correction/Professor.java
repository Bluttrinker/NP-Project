/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package correction;

/**
 *
 * @author bluttrinker
 */
public class Professor {
   private static boolean terminate;
   public static synchronized boolean shouldTerminate(){
       return terminate;
   } 
    
    
}
