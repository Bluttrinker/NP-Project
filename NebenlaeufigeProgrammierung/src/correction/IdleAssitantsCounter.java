package correction;

/**
 * 
 * @author Robin Burghartz
 * 
 * this counter is read by the professor to determine whether all assistants are idle. we need a separate class because we want to
 * have a safe monitor object.
 *
 */
public class IdleAssitantsCounter {

	private int n;
	
	public IdleAssitantsCounter(){
		this.n = 0;
	}
	
	public synchronized void increment(){
		n++;
	}
	
	public synchronized void decrement(){
		n--;
	}

	public synchronized int getN() {
		return n;
	}

	public synchronized void setN(int n) {
		this.n = n;
	}
	
	
	
}
