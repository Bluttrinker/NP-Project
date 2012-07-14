package correction;

import java.util.LinkedList;
import java.util.List;

/*
 * Dies ist eine unvollst"andige Implementierung der Exam Klasse aus der
 * Aufgabenstellung. Sie bietet die oeffentlichen Methoden correct und finish
 * an, sowie einige privaten Methoden, die dafuer sorgen, dass tatsaechlich
 * Rechenleistung und Speicher waehrend der Methoden correct und finish
 * verbraucht wird. Dies erleichtert es Ihnen die Effizienz Ihre Implementierung
 * auf verschiedenen (Mulit-Core) Architekturen zu testen.
 *
 * Sie koennen diese Klasse nach belieben erweitern. Sie sollten an den
 * angegebenen Stellen jedoch keine Veraenderung vornehmen.
 */

public class Exam {
    /*
     * Veraendert die Dauer der Methoden correct und finish
     */

    private static final int scale_correct = 1000000;
    private static final int scale_finish = 100000;
    //boolean array containing a flag for each exam.
    //true means it is finished, false (initial value) means it's unfinished.
    private boolean[] correctedExersise;

    /**
     * Create a new exam
     *
     * @param numberExercises the number of exercises this exam has.
     */
    


    public Exam(int numberExercises) {

        this.correctedExersise = new boolean[numberExercises];

    }

    
    /*
     * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
     */
    private static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);

        return Math.abs(y);
    }

    /*
     * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
     */
    private static int mod(int x, int y) {
        x = Math.abs(x);
        y = Math.abs(y);

        while (x >= y) {
            x = x - y;
        }

        return x;
    }

    /*
     * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
     */
    private static boolean spend_time(int n, int s) {
        int y = (xorShift(n)) % s;
        int test = 0;

        for (int i = 2; i < y; i++) {
            if (mod(y, i) == 0) {
                test = test + 1;
            }
        }

        return (test > 0);
    }

    /*
     * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
     */
    public static void do_correction() {
        int i = Thread.currentThread().hashCode();
        spend_time(i * (i + 12345), scale_correct);
    }

    /*
     * Rechenleistung verschwenden! (DO NOT CHANGE THIS METHOD)
     */
    public static void do_finish() {
        int i = Thread.currentThread().hashCode();
        spend_time(i * (i + 12345), scale_finish);
    }

    /**
     * Correct an exercise of this exam. This method takes some time wasting
     * ressources.
     *
     * @param exercise The number of the exercise we want to correct. Numbering
     * starts at 1
     */
    public void correct(int exercise) {


        //check if is a valid exercise
        if (exercise < 1 || exercise > correctedExersise.length) {
            throw new IllegalArgumentException("Not a valid exercise number!");
        }
        //check if we already corrected this
        if (correctedExersise[exercise - 1]) {
            return;
        }

        Exam.do_correction(); // Beansprucht Prozessorleistung und
        // Speicher. Dieser Aufruf muss
        // innerhalb der Methode correct
        // erfolgen. Sie duerfen (und sollten)
        // jedoch beliebigen Programmcode davor
        // und danach einfuegen.

        //exercise was corrected, remember this
        correctedExersise[exercise - 1] = true;

    }

    public void finish() {


        Exam.do_finish(); // Beansprucht Prozessorleistung und
			      // Speicher. Dieser Aufruf muss
			      // innerhalb der Methode finish
			      // erfolgen. Sie duerfen (und sollten)
			      // jedoch beliebigen Programmcode davor
			      // und danach einfuegen.
    }
    



    /**
     * @return true if all exercises of this exam have already been corrected.
     */
    public boolean isCorrected() {
        for (int i = 0; i < correctedExersise.length; i++) {
            if (!correctedExersise[i]) {
                return false;
            }
        }
        return true;
    }
    
    /** This method gives you the numbers of all exercises that have not yet been corrected.
     *   
     * @return A list of integers wich are the not yet corrected exersises. Empty list if all are corrected.
     */
    public List<Integer> exercisesToDo(){
        List<Integer> uncorrected = new LinkedList<Integer>();
        for(int i=0; i<correctedExersise.length; i++){
            if(!correctedExersise[i]){
                uncorrected.add(i+1);
            }
        }
        return uncorrected;
}
}
