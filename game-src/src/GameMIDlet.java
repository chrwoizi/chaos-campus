import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;


/**
 * The main Application. Entry point for the operating system.
 * Do not create a game by yourself !!
 * It is a MIDlet and runs in its own thread. 
 * Owns the MyGameCanvas and the GameManager.
 * @author Christian Woizischke
 *
 */
public class GameMIDlet extends MIDlet implements Runnable {

	public static final short MAX_FRAMES_PER_SECOND = 16;
	private short frameLength = 1000/MAX_FRAMES_PER_SECOND;
	
	private static GameMIDlet instance = null; // the instance
	
	public Display display; // the display. there is only one. public for Error
	private Thread thread; // run the game in its own thread
	
	private boolean running = false; // this can be set to false to end the game by calling shutdownApp
	
	private String gameStatusString = "";
	public String getGameStatusString() { return gameStatusString; }

	public GameMIDlet() {
		// The operating system has created a game. Set the instance to this.
		instance = this;

		// create the inout manager
		IODevice.getInstance();
		
		// create the game manager
		GameManager.getInstance();
	}
	
	/**
	 * Returns the game (which was created by the os).
	 * @return The game
	 */
	public static GameMIDlet getInstance() {
		// do not create a game here (as you would think of singletons). The operating system already does.
		return instance;
	}
	
	public void setFps(short fps) {
		frameLength = (short)(1000/fps);
	}
	
	protected void startApp() throws MIDletStateChangeException {
		display = Display.getDisplay(this);
		display.setCurrent(IODevice.getInstance());
		
		System.out.print("total memory: ");
		System.out.println(Runtime.getRuntime().totalMemory());
		System.out.print("free memory: ");
		System.out.println(Runtime.getRuntime().freeMemory());
		
		try {
            // Start the game in its own thread
            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            new Error("Could not create a new thread.");
        }
	}

	protected void pauseApp() {
		GameManager.getInstance().pauseGame();
	}
	
	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		notifyDestroyed();
	}
	
	/**
	 * Kills the application the hard way. Use in case of errors.
	 */
	public void killApp() {
		try {
			destroyApp(true);
		} 
		catch(MIDletStateChangeException e) {
			new Error("destroyApp threw an exception.");
		}
	}
	
	/**
	 * Exits the main loop.
	 */
	public void shutdownApp() {
		running = false;
	}
	
	/**
	 * The main loop. Has no effect. Do not use.
	 */
	public void run()
    {
		try {
			// time for uniform FPS
			long tickCount, sleep;
			long fpsTimer = System.currentTimeMillis();
			byte fpsCounter = 0;
			
			// run the game only once
			if(!running) {
				// begin main loop
		        running = true;
		    	while(running) {
		    		// get current time
	    			tickCount = System.currentTimeMillis();
	    			
		    		// update the game
		    		GameManager.getInstance().update();
		    		
		    		// repaint the scene
		    		IODevice.getInstance().repaint();
		    		
		    		// make uniform FPS
		    		try {
		    			sleep = frameLength-(System.currentTimeMillis()-tickCount);
		    			if(sleep > 0)
		    				Thread.sleep(sleep);
		    		}
		    		catch(Exception e) {}
		    		
		    		// update fps and free memory
		    		if( GameManager.getInstance().isStatusVisible()) {
		    			fpsCounter++;
		    			if( tickCount - fpsTimer > 1000 ) {
		    				
		    				System.gc();
		    				long usedmem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		    				gameStatusString = (int)fpsCounter + " fps"
		    				+ " - Mem: " + usedmem
							+ " / " + Runtime.getRuntime().totalMemory()
							+ " (" + (usedmem*100 / Runtime.getRuntime().totalMemory()) + "%)";
		    				
		    				fpsCounter = 0;
		    				fpsTimer += 1000;
		    			}
		    		}
		    	}
		    	
		    	// destroy app
		    	try {
					destroyApp(false);
				} 
				catch(MIDletStateChangeException e) {}
			}
		}
		catch(Exception e) {
			new Error("Uncatched Exception", e);
		}
    }
	
}
