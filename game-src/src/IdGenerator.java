
/**
 * Generates unique identifier for dynamically added objects. 
 * The first id is the last id of all level-file objects +1.
 * @author Christian Woizischke
 *
 */
public class IdGenerator {
	private static short current = 100;
	
	public static void init(short _first) {
		current = _first;
	}
	
	public static short createId() {
		short result = current;
		current++;
		return result;
	}
	
	
	/** Tests **/
	public static void test() {
		System.out.println("IdGenerator.testConstructor(): " + test((short)1));
		System.out.println("IdGenerator.testConstructor(): " + test((short)0));
		System.out.println("IdGenerator.testConstructor(): " + test((short)-1));
	}
	private static String test(short testData) {
		try {
			IdGenerator.init(testData);
			
			short s = IdGenerator.createId();
			if(s != testData) return "Test data " + testData + ": createId should return " + (testData) + " but it returned " + s;
			
			s = IdGenerator.createId();
			if(s != testData+1) return "Test data " + testData + ": createId should return " + (testData+1) + " but it returned " + s;
			
			return "Test data " + testData + ": Success";
		}
		catch(Exception e) {
			return "Test data " + testData + ": Exception: " + e.toString();
		}
	}
}
