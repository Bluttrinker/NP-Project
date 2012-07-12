package correction;
/* Dies ist eine unvollst"andige Implementierung der Exam Klasse aus
 * der Aufgabenstellung. Sie bietet die oeffentlichen Methoden correct
 * und finish an, sowie einige privaten Methoden, die dafuer sorgen,
 * dass tatsaechlich Rechenleistung und Speicher waehrend der Methoden
 * correct und finish verbraucht wird. Dies erleichtert es Ihnen die
 * Effizienz Ihre Implementierung auf verschiedenen (Mulit-Core)
 * Architekturen zu testen.
 * 
 * Sie koennen diese Klasse nach belieben erweitern. Sie sollten
 * an den angegebenen Stellen jedoch keine Veraenderung vornehmen.
 */



public class Exam {
    /* 
       Veraendert die Dauer der Methoden correct und finish
     */
    private static final int scale_correct = 1000000; 
    private static final int scale_finish   = 100000; 
    private boolean[] isCorrected; 
    private boolean isFinished;

	/*TODO: constructor?! where do we save the number of exercises? (i guess we should use a parameter but maybe there is a more elegant
    way...) */
    public Exam(){
    	for(boolean b: isCorrected){
    		b = false;
    	}
    	isFinished = false;
    }
    
    /* Rechenleistung verschwenden!
     * (DO NOT CHANGE THIS METHOD)
     */
    private static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);

        return Math.abs(y);
    }

    /* Rechenleistung verschwenden!
     * (DO NOT CHANGE THIS METHOD)
     */
    private static int mod(int x, int y) {
        x = Math.abs(x);
        y = Math.abs(y);

        while (x >= y) 
            x = x - y;
        
        return x;
    }

    /* Rechenleistung verschwenden!
     * (DO NOT CHANGE THIS METHOD)
     */
    private static boolean spend_time(int n, int s) {
        int y = (xorShift(n)) % s;
        int test = 0;

        for (int i = 2; i < y; i++) 
            if (mod(y, i) == 0) 
                test = test + 1;

        return (test > 0);
    }

    /* Rechenleistung verschwenden!
     * (DO NOT CHANGE THIS METHOD)
     */
    public static void do_correction() {
        int i = Thread.currentThread().hashCode();
        spend_time(i * (i + 12345), scale_correct);
    }

    /* Rechenleistung verschwenden!
     * (DO NOT CHANGE THIS METHOD)
     */
    public static void do_finish() {
        int i = Thread.currentThread().hashCode();
        spend_time(i * (i + 12345), scale_finish);
    }

    public void correct(int exercise) {

    	if(isCorrected[exercise-1]) return;
        Exam.do_correction(); // Beansprucht Prozessorleistung und
			      // Speicher. Dieser Aufruf muss
			      // innerhalb der Methode correct
			      // erfolgen. Sie duerfen (und sollten)
			      // jedoch beliebigen Programmcode davor
			      // und danach einfuegen.
        isCorrected[exercise-1] = true;

    }

    public void finish() {


        Exam.do_finish(); // Beansprucht Prozessorleistung und
			      // Speicher. Dieser Aufruf muss
			      // innerhalb der Methode finish
			      // erfolgen. Sie duerfen (und sollten)
			      // jedoch beliebigen Programmcode davor
			      // und danach einfuegen.
        isFinished = true;
        //TODO: what happens with the exams after finishing? are they kept by the professor?
    }
    
    //TODO: i'm confused...what else is to do in finish() and correct()??
}

