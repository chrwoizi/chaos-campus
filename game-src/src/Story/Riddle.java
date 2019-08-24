package Story;
/**
 * 
 */
import javax.microedition.lcdui.*;

/**
 * Abstract class for the riddles in the Game
 * @author Sascha Lity
 *
 */
public abstract class Riddle {

	private boolean finished = false;
	public boolean hasManual = false;
	
	/**
	 * Set the Variable finished for ending the Riddle
	 */
	public void setFinished(boolean finished){
		this.finished = finished;
	}
	
	/**
	 * Return the value of the attribute finished
	 * @return value of finished
	 */
	public boolean isfinished(){
		return finished;
	}
	
	//Implements in the lower classes
	public abstract void render(Graphics g);
	public abstract void onKeyDown(int keyCode);
	public abstract void stop();
	public abstract void reset();
	
}
