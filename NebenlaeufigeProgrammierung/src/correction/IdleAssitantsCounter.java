package correction;

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
