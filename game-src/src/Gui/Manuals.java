package Gui;

import GameManager;
import Error;
import IODevice;
import StreamOperations;

import javax.microedition.lcdui.*;


public class  Manuals {
	
	public static final byte INTRODUCTION = 1, 
	                        MOSAIKRIDDLE = 2,
	                        DESTILLIERRIDDLE = 3,
	                        HANOIRIDDLE = 4,
							NUMBERRIDDLE = 5,
							MASTERMINDRIDDLE = 6;
	
	private int textlineHeight = Font.getDefaultFont().getHeight();
	private static final byte BORDER_LEFT = 5;
	
	private boolean finished = false;
	private short visibleTextLines = (short)(IODevice.getInstance().getHeight()/textlineHeight);
	private short textPositionY = 0;
	private String [] manualtext;
	
    // manualtext [] erhält im Konstruktor alle Textzeilen 
	// aus introduction.txt.
  
	public Manuals(byte type) {

		try{
			//loads the manaul text 
			switch(type){
				case INTRODUCTION:
					manualtext = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/introduction.txt", Font.getDefaultFont(), 
					IODevice.getInstance().getWidth() - BORDER_LEFT);
					break;
				case MOSAIKRIDDLE:
					manualtext = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/mosaikInstruction.txt", Font.getDefaultFont(), 
					IODevice.getInstance().getWidth() - BORDER_LEFT);
					break;
				case DESTILLIERRIDDLE:
					manualtext = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/DestilliererInstruction.txt", Font.getDefaultFont(), 
					IODevice.getInstance().getWidth() - BORDER_LEFT);
					break;
				case HANOIRIDDLE:
					manualtext = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/HanoiInstruction.txt", Font.getDefaultFont(), 
					IODevice.getInstance().getWidth() - BORDER_LEFT);
					break;
				case NUMBERRIDDLE:
					manualtext = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/NumberInstruction.txt", Font.getDefaultFont(), 
					IODevice.getInstance().getWidth() - BORDER_LEFT);
					break;
				case MASTERMINDRIDDLE:
					manualtext = StreamOperations.readFileLinesWrappedIntoStringArray(
					"/manuals/MastermindInstruction.txt", Font.getDefaultFont(), 
					IODevice.getInstance().getWidth() - BORDER_LEFT);
					break;
			}
			
		}
	
		catch( Exception e ) {
			new Error(e);
		}
	}

	
	public boolean isFinished() { return finished; }
	
	public void onKeyDown(int key) {

		switch(key) {
		
		case GameManager.ATTACK:
		case GameManager.OK:
	  		finished = true;
	  		break;
	  		
		// if you press DOWN the text scrolls down
		case GameManager.DOWN: 
			if (textPositionY > -(manualtext.length-visibleTextLines)*textlineHeight) { 
				textPositionY -= textlineHeight;
			}
			break;

	  	// if you press UP the text scrolls up
		case GameManager.UP:
	  		if (textPositionY < 0) { 
		  		textPositionY += textlineHeight;
	  		}
	  		break;
		
		case GameManager.BACK:
			GuiManager.getInstance().setActiveMenu(GuiManager.MAIN_MENU);
			break;
		}
	}
	
	
	public void render(Graphics g) {  
		
		if( !finished )  {
			
			// fills background with black color
			g.setColor(0,0,0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
				
			// draws text
			g.setColor(255,255,255);
			for (int i=0, hoehe=textPositionY; i < manualtext.length; i++,hoehe=hoehe+textlineHeight){
				g.drawString(manualtext [i], BORDER_LEFT,hoehe, Graphics.LEFT|Graphics.TOP);
			}
			    
		}
	}
	
}