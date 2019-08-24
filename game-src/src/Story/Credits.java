/**
 * 
 */
package Story;

import IODevice;
import StreamOperations;
import Error;
import GameMIDlet;
import SoundManager;

import javax.microedition.lcdui.*;


/**
 * Class for showing the Credits
 * 
 * @author Sascha Lity
 *
 */
public class Credits {

	private static final byte TEXT_MOVEMENT_DELAY = 1;
	private static final short CREDITS_FPS = 10;
	
	private boolean finished = false;
	private boolean started = false;
	 		
	private short screenCenter;
	private short textBaseline;
	private short delayTime = TEXT_MOVEMENT_DELAY;				
	private byte activeTextLine = 0;
	private byte pixelLine = 0;
	private short lineHeight = (short)Font.getDefaultFont().getHeight();
	private short lineWidth = (short)(IODevice.getInstance().getWidth()-6);
	
	private String[] creditsTextLines;
	
	/**
	 * Constructor of credits, loads the text for the credits
	 * 
	 */
	public Credits() 
	{
		try
		{
			// load whole text of the scene
			creditsTextLines = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/credits/credits.txt",
					Font.getDefaultFont(), lineWidth);
			// set image and text position
			short screenHeight = (short)IODevice.getInstance().getHeight();
			short textHeight = (short)(3*lineHeight);
			screenCenter = (short)(screenHeight/4);
			textBaseline = (short)(screenCenter);
			
			if (textHeight > screenHeight) 
				textHeight = (short)(screenHeight);
			screenCenter -= textHeight/2;
			textBaseline += textHeight/2;
			
			
		}
		catch( Exception e ) {
			new Error("Credits constructor: ", e);
		}
	}
	
	/**
	 * Updates the shown text and renders it. 
	 * 
	 * @param g 
	 */
	public void render(Graphics g) 
	{
		// only render cutscene if it's not finished yet
		if( !finished )
		{
			// do init if not started yet
			if( !started ) {
				SoundManager.getInstance().setActiveSound((byte)1);
				GameMIDlet.getInstance().setFps(CREDITS_FPS);
				SoundManager.getInstance().playSound();
				started = true;
			}
			
			// fill background with black color
			g.setColor(0,0,0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
			
			byte tempLine = activeTextLine;
			short left = (short)((IODevice.getInstance().getWidth())/2);
			g.setClip(0, textBaseline-lineHeight*3, IODevice.getInstance().getWidth(), lineHeight*9);

			// draw text
			g.setColor(255, 255, 255);
			// highest text line will fade out if not within image
			if( pixelLine >= lineHeight ){
				int color = 255 - 128/(lineHeight) * (pixelLine-lineHeight);
				g.setColor(color, color, color);
			}
			tempLine = activeTextLine;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine, 
					Graphics.HCENTER|Graphics.BASELINE);

			g.setColor(255, 255, 255);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 2*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 3*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 4*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 5*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 6*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 7*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 8*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 9*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);
			tempLine++;
			g.drawString(getTextLine(tempLine), left, textBaseline - pixelLine + 10*lineHeight, 
					Graphics.HCENTER|Graphics.BASELINE);

			g.setClip(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());

			// update text movement every <TEXT_MOVEMENT_DELAY> calls
			delayTime--;
			if( delayTime <= 0 ) {
				delayTime = TEXT_MOVEMENT_DELAY;
				// place text one pixel higher
				pixelLine+=1;
			}

			// when text is moved 25 pixels higher change the text in our lines
			if( pixelLine >= 3*lineHeight ) {
				pixelLine -= lineHeight;
				activeTextLine++;
			}
		}		
	}
	
	
	
	public void breakCredits() {
		finished = true;
		GameMIDlet.getInstance().setFps(GameMIDlet.MAX_FRAMES_PER_SECOND);
		SoundManager.getInstance().releaseActiveSound();
	}
	
	/**
	 * The Method gives the State of the Credits back
	 * @return 
	 */
	public boolean isfinished(){
		return finished;
	}
	
	/**
	 * increases the text line 
	 */
	private String getTextLine(byte textLine) {
		if(textLine >= this.creditsTextLines.length){
			breakCredits();
			return "";
		}
		return creditsTextLines[textLine];
	}
}

