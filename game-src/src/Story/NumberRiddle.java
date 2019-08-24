/**
 * 
 */
package Story;

import javax.microedition.lcdui.*;

import Gui.Manuals;
import Error;

import GameManager;
import IODevice;

/**
 * Class for playing the NumberRiddle
 * @author Sascha Lity
 *
 */
public class NumberRiddle extends Riddle {

	private Image activeImage;
	private int[][] numberBlock;
	private short activeBlock;
	private short activePosition;
	private boolean solved = false;
	private int endDelay = 0;

	private int height = IODevice.getInstance().getHeight();
	private int width = IODevice.getInstance().getWidth();
	
	Manuals introductionManual = null;
	
	/**
	  * Constructor of NumberRiddle
	 */
	public NumberRiddle(){
		//Load Instruction
		introductionManual = new Manuals(Manuals.NUMBERRIDDLE);
		hasManual = true;
		//Load NumberRiddleImage
		try{
			activeImage = Image.createImage("/riddle/Steintafel.png");
		}
		catch (Exception e){
			new Error("numberriddle img", e);
		}
		
		numberBlock = new int[6][3];
		//Block 1
		numberBlock[0][0] = 0;
		numberBlock[0][1] = 0;
		numberBlock[0][2] = 1;
		//Block 2
		numberBlock[1][0] = 0;
		numberBlock[1][1] = 0;	
		numberBlock[1][2] = 2;
		//Block 3
		numberBlock[2][0] = 0;
		numberBlock[2][1] = 0;
		numberBlock[2][2] = 6;
		//Block 4
		numberBlock[3][0] = 0;
		numberBlock[3][1] = 2;
		numberBlock[3][2] = 4;
		//Block 5
		numberBlock[4][0] = 0;
		numberBlock[4][1] = 0;
		numberBlock[4][2] = 0;
		//Block 6
		numberBlock[5][0] = 0;
		numberBlock[5][1] = 0;
		numberBlock[5][2] = 0;
		
		activeBlock = 4;
		activePosition = 0;
	}
	
	/**
	 * Manage what happen when you press a button
	 * @see Story.Riddle#onKeyDown(int)
	 * @param keyCode Value for which button was pressed
	 */
	public void onKeyDown(int keyCode) {
		 //for manual
		if (introductionManual != null) {
			switch(keyCode) {
			case GameManager.UP:
			case GameManager.DOWN:
				introductionManual.onKeyDown(keyCode);
				break;
			case GameManager.OK:
				introductionManual = null;	
				IODevice.getInstance().setLeftCommand(IODevice.COMMANDTYPE_BACK);
				IODevice.getInstance().setRightCommand(IODevice.COMMANDTYPE_RESET);
				break;
			}
			return;
		}
		// dont accept keys when riddle is already solved
		if( solved )
			return;
		
		switch (keyCode)
		{
		  case GameManager.DOWN:
		  {
			  if(numberBlock[activeBlock][activePosition] == 0){
				  numberBlock[activeBlock][activePosition] = 9;
			  }
			  else{
				  numberBlock[activeBlock][activePosition] -= 1;
			  }
			  break;
		  }
		  case GameManager.UP:
		  {
			  if(numberBlock[activeBlock][activePosition] == 9){
				  numberBlock[activeBlock][activePosition] = 0;
			  }
			  else{
				  numberBlock[activeBlock][activePosition] += 1;
			  }
			  break;
		  }
		  case GameManager.RIGHT:
		  {
			  activePosition++;
			  if(activePosition > 2){
				  activePosition = 0;
				  activeBlock++;
				  if( activeBlock > 5 )
					  activeBlock = 4;
			  }
			  break;
		  }
		  case GameManager.LEFT:
		  {
			  activePosition--;
			  if(activePosition < 0){
				  activePosition = 2;
				  activeBlock--;
				  if( activeBlock < 4 )
					  activeBlock = 5;
			  }
			  break;
		  }
		  case GameManager.BACK:
		  {
			  stop();
			  break;
		  }
		  case GameManager.OK:
		  case GameManager.SPECIAL:
		  {
			  reset();
			  break;
		  }
	   }
	}

	/**
	 * Renders the scene. 
	 * Called by GameManager
	 * @see Story.Riddle#render(javax.microedition.lcdui.Graphics)
	 * @param g The graphics context to draw on.
	 */
	public void render(Graphics g) {
		//for manual
		if (introductionManual != null) {
				introductionManual.render(g);
				return;
		}
		// background color
		g.setColor(150, 130, 120);
		g.fillRect(0, 0, width, height);
		
		// stone background
		g.drawImage(activeImage, width/2 - activeImage.getWidth()/2 , 
				height/2 - activeImage.getHeight()/2, 
				Graphics.TOP | Graphics.LEFT);
		
		// number fields
		Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
		int numberWidth = font.charWidth('0')+2;
		int numberHeight = font.getHeight();
		g.setFont(font);
		
		for(int i = 0; i < numberBlock.length; i++)
		{
			for(int j = 0; j < numberBlock[i].length; j++) 
			{
				// calculate number position
				int x = width/2 - activeImage.getWidth()/4 - (numberWidth*3+4)/2;
				if( i % 2 == 1 ) 
					x += activeImage.getWidth()/2;
				int y = height/2 - numberHeight/2;
				if( i < 2) 
					y -= activeImage.getHeight()/3;
				else if( i > 3 ) 
					y += activeImage.getHeight()/3;
					
				// draw number background
				if(i == activeBlock && activePosition == j && !solved)
					g.setColor(0,0,0);
				else
					g.setColor(150, 130, 120);
				g.fillRect(x - 1 + j * (numberWidth+2), y, numberWidth, numberHeight);
				
				// draw number
				if(i == activeBlock && activePosition == j && !solved)
					g.setColor(255, 255, 255);
				else
					g.setColor(0, 0, 0);
				g.drawChar(getChar(numberBlock[i][j]), 
						x + j * (numberWidth+2), y, 
						Graphics.TOP | Graphics.LEFT);
			}

		}

		//Is Number solved?
		if(numberRiddleDissolved()){
			solved = true;
			if(endDelay == 30){
				setFinished(true);
				StoryManager.getInstance().onRiddleSolved((byte)4);
				activeImage = null;
			}
			endDelay++;
		}
	}

	/**
	 * Starts the riddle again from the beginning
	 * @see Story.Riddle#reset()
	 */
	public void reset() {
		//Block 1
		numberBlock[0][0] = 0;
		numberBlock[0][1] = 0;
		numberBlock[0][2] = 1;
		//Block 2
		numberBlock[1][0] = 0;
		numberBlock[1][1] = 0;	
		numberBlock[1][2] = 2;
		//Block 3
		numberBlock[2][0] = 0;
		numberBlock[2][1] = 0;
		numberBlock[2][2] = 6;
		//Block 4
		numberBlock[3][0] = 0;
		numberBlock[3][1] = 2;
		numberBlock[3][2] = 4;
		//Block 5
		numberBlock[4][0] = 0;
		numberBlock[4][1] = 0;
		numberBlock[4][2] = 0;
		//Block 6
		numberBlock[5][0] = 0;
		numberBlock[5][1] = 0;
		numberBlock[5][2] = 0;
		
		activeBlock = 4;
		activePosition = 0;
		solved = false;
	}

	/**
	 * Stop the riddle, whithout solving
	 * @see Story.Riddle#stop()
	 */
	public void stop() {
		setFinished(true);

	}
	
	/**
	 * Check the NumberRiddle if it is solved
	 * @return Solved or not?
	 */
	private boolean numberRiddleDissolved(){
		if(numberBlock[4][0] == 1){
			if(numberBlock[4][1] == 2){
				if(numberBlock[4][2] == 0){
					if(numberBlock[5][0] == 7){
						if(numberBlock[5][1] == 2){
							if(numberBlock[5][2] == 0){  
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Cast a int to a char
	 * @param number Value which have to cast
	 * @return the value as char
	 */
	private char getChar(int number){
		char c = 'a'; 
		switch(number){
			case 0:
			{
				c = '0';
				break;
			}
			case 1:
			{
				c = '1';
				break;
			}
			case 2:
			{
				c = '2';
				break;
			}
			case 3:
			{
				c = '3';
				break;
			}
			case 4:
			{
				c = '4';
				break;
			}
			case 5:
			{
				c = '5';
				break;
			}
			case 6:
			{
				c = '6';
				break;
			}	
			case 7:
			{
				c = '7';
				break;
			}
			case 8:
			{
				c = '8';
				break;
			}
			case 9:
			{	
				c = '9';
				break;
			}
		}
		return c;
	}
	
}


