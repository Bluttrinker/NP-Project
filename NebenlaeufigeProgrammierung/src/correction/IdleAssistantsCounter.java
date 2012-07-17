package correction;

/**
 * 
 * @author Robin Burghartz
 * 
 * this counter is read by the professor to determine whether all assistants are idle. we need a separate class because we want to
 * have a safe monitor object.
 *
 */
public class IdleAssistantsCounter {

	private int n;
	
	public IdleAssistantsCounter(){
		this.n = 0;
	}
	
	public synchronized void increment(){
	 System.out.println("up " +n+" new "+ (n+1) + this);
            n++;
	}
	
	public synchronized void decrement(){
            System.out.println("down " +n+" new "+ (n-1) + this );
                if(n<=0) throw new IllegalStateException("Counter shall not be smaller then 0");
		n--;
	}

	public synchronized int getN() {
		return n;
	}

	public synchronized void setN(int n) {
                System.out.println("resetting counter" + this);
		this.n = n;
	}
	
	
	
}
