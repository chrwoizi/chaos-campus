
public class Mutex {
	private short m;
	
	public Mutex() {
		m = 1;
	}
	
	public synchronized void enter() {
		m--;
		if(m<0) {
			try {
				wait();
			}
			catch(Exception e) {
				
			}
		}
	}
	
	public synchronized void leave() {
		m++;
		if(m>=0) {
			try {
				notifyAll();
			}
			catch(Exception e) {
				
			}
		}
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("Mutex.testAll(): " + testAll());
	}
	private static String testAll() {
		try {
			Mutex m = new Mutex();
			
			if(m.m != 1) return "Mutex should be initialized with 1 but it is " + m.m;
			
			m.enter();
			if(m.m != 0) return "Mutex should be 0 after enter but it is " + m.m;
			
			m.leave();
			if(m.m != 1) return "Mutex should be 1 after leave but it is " + m.m;
			
			return "Success";
		}
		catch(Exception e) {
			return "Exception: " + e.toString();
		}
	}
}
