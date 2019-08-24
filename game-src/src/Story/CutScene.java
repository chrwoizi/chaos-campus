package Story;


import IODevice;
import StreamOperations;
import Error;
import GameMIDlet;
import SoundManager;

import javax.microedition.lcdui.*;

import java.io.IOException;

/**
 * The class CutScene manage the cutscenes in the game 
 * 
 * @author Mbelale Sandrine, Martin Wahnschaffe
 *
 */

public class CutScene {

	private static final byte TEXT_MOVEMENT_DELAY = 1;
	private static final short CUTSCENE_FPS = 10;
	
	private boolean finished = false;

	private byte id;
	private byte activeImageNumber;
	
	private Image activeImage;  		
	private short imageCenterY;
	private short textBaseline;
	private short delayTime = TEXT_MOVEMENT_DELAY;				
	private byte activeTextLine = 0;
	private byte pixelLine = 0;
	private boolean drawWithinImage = true;
	private short lineHeight = (short)Font.getDefaultFont().getHeight();
	private short lineWidth = (short)(IODevice.getInstance().getWidth()-6);
	
	// cutsceneTextLines erhält im Konstruktor alle Textzeilen 
	// die währrend der cutscene abgespielt werden sollen
	// Dieser soll aus einer Datei geladen werden. 
	// Der Text wird Zeilenweise ausgegeben
	// Das '+' am Anfang einer Zeile gibt den Wechsel der Bilder an
	private String[] cutsceneTextLines;
	
	/**
	 * Constructor of a cutscene, loads the first Image and the text for the cutscene
	 * 
	 * @param id Identity for loading the Image and the Text
	 */
	public CutScene(byte id) 
	{
		try
		{
			if( id == 0 )
				SoundManager.getInstance().setActiveSound((byte)1);
			
			GameMIDlet.getInstance().setFps(CUTSCENE_FPS);
			this.id = id;
			// load first image of the scene 
			activeImageNumber = 0;
			String imageName = "scene" + id + '_' + String.valueOf(activeImageNumber);
			activeImage = Image.createImage("/cutscenes/" + imageName + ".png");
			// load whole text of the scene
			cutsceneTextLines = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/cutscenes/scene" + id + ".txt",
					Font.getDefaultFont(), lineWidth);
			// set image and text position
			short screenHeight = (short)IODevice.getInstance().getHeight();
			short textHeight = (short)(3*lineHeight);
			imageCenterY = (short)(screenHeight/2);
			textBaseline = (short)(imageCenterY + activeImage.getHeight()/2);
			
			if (textHeight > screenHeight - activeImage.getHeight()) 
				textHeight = (short)(screenHeight - activeImage.getHeight());
			imageCenterY -= textHeight/2;
			textBaseline += textHeight/2;
			
			drawWithinImage = true;
			
			SoundManager.getInstance().playSound();
		}
		catch( Exception e ) {
			new Error("Cutscene constructor: ", e);
		}
	}
	
	/**
	 * Updates the shown image and text and renders both. 
	 * 
	 * @param g 
	 */
	public void render(Graphics g) {
		
		// only render cutscene if it's not finished yet
		if( !finished )
		{
			// fill background with black color
			g.setColor(0,0,0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
			// draw image
			g.drawImage(activeImage, 
					IODevice.getInstance().getWidth()/2, imageCenterY,
					Graphics.VCENTER | Graphics.HCENTER);
			
			byte tempLine = activeTextLine;
			//short left = (short)((IODevice.getInstance().getWidth()-lineWidth)/2);
			short left = (short)(IODevice.getInstance().getWidth()/2);
			g.setClip(0, textBaseline-lineHeight*3, lineWidth, lineHeight*3);

			if( drawWithinImage )
			{
				// draw shadow
				g.setColor(0, 0, 0);
				g.drawString(getTextLine(tempLine), left+1, textBaseline-pixelLine+1, 
						Graphics.HCENTER|Graphics.BASELINE);
				g.drawString(getTextLine(tempLine), left+2, textBaseline-pixelLine+2, 
						Graphics.HCENTER|Graphics.BASELINE);
				tempLine++;
				g.drawString(getTextLine(tempLine), left+1, textBaseline-pixelLine+lineHeight+1, 
						Graphics.HCENTER|Graphics.BASELINE);
				g.drawString(getTextLine(tempLine), left+2, textBaseline-pixelLine+lineHeight+2, 
						Graphics.HCENTER|Graphics.BASELINE);
				tempLine++;
				g.drawString(getTextLine(tempLine), left+1, textBaseline-pixelLine+2*lineHeight+1, 
						Graphics.HCENTER|Graphics.BASELINE);
				g.drawString(getTextLine(tempLine), left+2, textBaseline-pixelLine+2*lineHeight+2, 
						Graphics.HCENTER|Graphics.BASELINE);
			}			
			// draw text
			g.setColor(255, 255, 255);
			// highest text line will fade out if not within image
			if( pixelLine >= lineHeight ){//&& !drawWithinImage) {
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

			g.setClip(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());

			// update text movement every <TEXT_MOVEMENT_DELAY> calls
			delayTime--;
			if( delayTime <= 0 ) {
				delayTime = TEXT_MOVEMENT_DELAY;
				// place text one pixel higher
				pixelLine+=1;
			}

			// when text is moved 25 pixels higher change the text in our lines
			if( pixelLine >= 2*lineHeight ) {
				pixelLine -= lineHeight;
				activeTextLine++;
			}
		}		
	}
	
	/**
	 * changeImage loads the new Image
	 */
	private void changeImage(byte imageNumber)
	{
		activeImageNumber = imageNumber;
		String imageName = "scene" + id + '_' + String.valueOf(activeImageNumber);
		try {
			activeImage = Image.createImage("/cutscenes/" + imageName + ".png");
		} catch (IOException e) {
			new Error(e);
		}
	}
	
	
	public void breakCutScene() {
		finished = true;
		GameMIDlet.getInstance().setFps(GameMIDlet.MAX_FRAMES_PER_SECOND);
		SoundManager.getInstance().releaseActiveSound();
	}
	
	/**
	 * The Method gives the State of the Cutscene back
	 * @return 
	 */
	public boolean isfinished(){
		return finished;
	}
	
	/**
	 * increases the text line and does loading manages image loading, etc.
	 */
	private String getTextLine(byte textLine) {
		if(textLine >= this.cutsceneTextLines.length){
			breakCutScene();
			return "";
		}
		else if(this.cutsceneTextLines[textLine].length() > 0 ) {
			if( this.cutsceneTextLines[textLine].charAt(0) == '+') {
				cutsceneTextLines[textLine] = cutsceneTextLines[textLine].substring(1);
				changeImage((byte)(activeImageNumber+1));
			}
			if( this.cutsceneTextLines[textLine].charAt(0) == '#') {
				byte imageNumber = Byte.parseByte(cutsceneTextLines[textLine].substring(1,3));
				cutsceneTextLines[textLine] = cutsceneTextLines[textLine].substring(4);
				changeImage(imageNumber);
			}
		}
		return cutsceneTextLines[textLine];
	}
}
