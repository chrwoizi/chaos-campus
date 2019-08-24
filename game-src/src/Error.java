
import javax.microedition.lcdui.*;

/**
 * Displays an error message on the screen
 * @author Christian Woizischke
 *
 */
public class Error {
	
	private static final int ALERT_TIMEOUT = 10000;
	private static boolean errorSet = false;

	/**
	 * Displays the error message msg
	 * @param msg
	 */
	public Error(String msg) {
		if(!errorSet)
		try {
			if(GameMIDlet.getInstance().display.getCurrent().getClass() != Class.forName("javax.microedition.lcdui.Alert")) {
				Alert alert = new Alert("Error", msg + "\n\nThis message disappears in 20 sec.", null, AlertType.ERROR);
				alert.setTimeout(ALERT_TIMEOUT);
				GameMIDlet.getInstance().display.setCurrent(alert);
			}
		}
		catch(Exception ex) {ex.printStackTrace();}
		
		System.err.println(msg);
		
		errorSet = true;
	}
	
	/**
	 * Displays the exception
	 * @param e
	 */
	public Error(Exception e) {
		if(!errorSet)
		try {
			if(GameMIDlet.getInstance().display.getCurrent().getClass() != Class.forName("javax.microedition.lcdui.Alert")) {
				Alert alert = new Alert("Exception", e + "\nMessage: " + e.getMessage() + "\n\nThis message disappears in 20 sec.", null, AlertType.ERROR);
				alert.setTimeout(ALERT_TIMEOUT);
				GameMIDlet.getInstance().display.setCurrent(alert);
			}
		}
		catch(Exception ex) {ex.printStackTrace();}
		
		e.printStackTrace();
		
		errorSet = true;
	}
	
	/**
	 * Displays the exception with additional description (e.g. where the exception has been catched)
	 * @param e
	 */
	public Error(String description, Exception e) {
		if(!errorSet)
		try {
			if(GameMIDlet.getInstance().display.getCurrent().getClass() != Class.forName("javax.microedition.lcdui.Alert")) {
				Alert alert = new Alert("Exception", description + "\n" + e + "\nMessage: " + e.getMessage() + "\n\nThis message disappears in 20 sec.", null, AlertType.ERROR);
				alert.setTimeout(ALERT_TIMEOUT);
				GameMIDlet.getInstance().display.setCurrent(alert);
			}
		}
		catch(Exception ex) {ex.printStackTrace();}

		System.err.println(description);
		e.printStackTrace();
		
		errorSet = true;
	}
	
	
	/** Tests **/
	public static void test() {
		try {
			System.out.println("Error.testConstructorString(): " + testConstructorString("aB2#"));
			Thread.sleep(ALERT_TIMEOUT + 1000);
			System.out.println("Error.testConstructorString(): " + testConstructorString(""));
			Thread.sleep(ALERT_TIMEOUT + 1000);
			System.out.println("Error.testConstructorException(): " + testConstructorException(new NullPointerException()));
			Thread.sleep(ALERT_TIMEOUT + 1000);
			System.out.println("Error.testConstructorStringException(): " + testConstructorStringException("aB2#", new ArrayIndexOutOfBoundsException()));
			Thread.sleep(ALERT_TIMEOUT + 1000);
			System.out.println("Error.testConstructorStringException(): " + testConstructorStringException("", new ArrayIndexOutOfBoundsException()));
			Thread.sleep(ALERT_TIMEOUT + 1000);
			System.out.println("Error.testConstructorStringException(): " + testConstructorStringException("aB2#", new NullPointerException()));
			Thread.sleep(ALERT_TIMEOUT + 1000);
			System.out.println("Error.testConstructorStringException(): " + testConstructorStringException("", new NullPointerException()));
			Thread.sleep(ALERT_TIMEOUT + 1000);
		} 
		catch(Exception e) {
			
		}
	}
	private static String testConstructorString(String testData) {
		try {
			new Error(testData);
			Thread.sleep(1000);
			Displayable d = GameMIDlet.getInstance().display.getCurrent();
			if(d.getClass() == Class.forName("javax.microedition.lcdui.Alert")) {
				Alert a = (Alert)GameMIDlet.getInstance().display.getCurrent();
				if(a.isShown()) {
					if(a.getTimeout() != ALERT_TIMEOUT) 
						return "testData: \"" + testData + "\": Alert timeout should be " + ALERT_TIMEOUT + " but it is " + a.getTimeout();
					if(0!=a.getString().substring(0, testData.length()).compareTo(testData)) 
						return "testData: \"" + testData + "\": Alert string should be \"" + testData + "\" but it is \"" + a.getString().substring(0, testData.length()) + "\".";
					return "testData: \"" + testData + "\": Success";
				}
				else return "testData: \"" + testData + "\": The Alert is invisible.";
			}
			else return "testData: \"" + testData + "\": The Alert has not been set.";
		}
		catch(Exception e) {
			return "testData: \"" + testData + "\": Exception: " + e.toString();
		}
	}
	private static String testConstructorException(Exception testData) {
		try {
			new Error(testData);
			Thread.sleep(1000);
			Displayable d = GameMIDlet.getInstance().display.getCurrent();
			if(d.getClass() == Class.forName("javax.microedition.lcdui.Alert")) {
				Alert a = (Alert)GameMIDlet.getInstance().display.getCurrent();
				if(a.isShown()) {
					if(a.getTimeout() != ALERT_TIMEOUT) 
						return "testData: \"" + testData + "\": Alert timeout should be " + ALERT_TIMEOUT + " but it is " + a.getTimeout();
					if(0!=a.getString().substring(0, testData.toString().length()).compareTo(testData.toString())) 
						return "testData: \"" + testData + "\": Alert string should be \"" + testData + "\" but it is \"" + a.getString().substring(0, testData.toString().length()) + "\".";
					return "testData: \"" + testData + "\": Success";
				}
				else return "testData: \"" + testData + "\": The Alert is invisible.";
			}
			else return "testData: \"" + testData + "\": The Alert has not been set.";
		}
		catch(Exception e) {
			return "testData: \"" + testData + "\": Exception: " + e.toString();
		}
	}
	private static String testConstructorStringException(String testData1, Exception testData2) {
		try {
			new Error(testData1, testData2);
			Thread.sleep(1000);
			Displayable d = GameMIDlet.getInstance().display.getCurrent();
			if(d.getClass() == Class.forName("javax.microedition.lcdui.Alert")) {
				Alert a = (Alert)GameMIDlet.getInstance().display.getCurrent();
				if(a.isShown()) {
					if(a.getTimeout() != ALERT_TIMEOUT) 
						return "testData: \"" + testData1 + "," + testData2 + "\": Alert timeout should be " + ALERT_TIMEOUT + " but it is " + a.getTimeout();
					if(0!=a.getString().substring(0, (testData1 + "\n" + testData2.toString()).length()).compareTo(testData1 + "\n" + testData2.toString())) 
						return "testData: \"" + testData1 + "," + testData2 + "\": Alert string should be \"" + (testData1 + "\n" + testData2.toString()) + "\" but it is \"" + a.getString().substring(0, (testData1 + "\n" + testData2.toString()).length()) + "\".";
					return "testData: \"" + testData1 + "," + testData2 + "\": Success";
				}
				else return "testData: \"" + testData1 + "," + testData2 + "\": The Alert is invisible.";
			}
			else return "testData: \"" + testData1 + "," + testData2 + "\": The Alert has not been set.";
		}
		catch(Exception e) {
			return "testData: \"" + testData1 + "," + testData2 + "\": Exception: " + e.toString();
		}
	}
}
