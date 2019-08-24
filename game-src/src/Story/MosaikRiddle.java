package Story;


import GameManager;
import Error;
import IODevice;

import javax.microedition.lcdui.*;

import Gui.Manuals;



/**
 * Class for playing the MosaikRiddle
 * @author Sascha Lity
 *
 */
public class MosaikRiddle extends Riddle {
	
	private short currentPosition;
	private int[][] mosaikBlock;
	final int[] blockPosition = {0,3,1,6,8,7,4,2,5}; // 3x3 field in a 1-Dim Array;
	private int[] blockPositionUsed;
	private Image activeImage = null;
	private short endDelay = 0;
	private int halbeBildschirmBreite = IODevice.getInstance().getWidth()/2;
	private int halbeBildschirmHöhe = IODevice.getInstance().getHeight()/2;
	private int imageBreite; 
	private int imageHöhe;
	
	Manuals introductionManual = null;
	
	/**
	 * Constructor of MosaikRiddle
	 *
	 */
	public MosaikRiddle(){
		//Load Instruction
		introductionManual = new Manuals(Manuals.MOSAIKRIDDLE);
		hasManual = true;
		
		//Load Mosaikimage
		try{
			activeImage = Image.createImage("/riddle/Mosaik.png");
		}
		catch (Exception e){
			new Error("mosaik img", e);
		}
		
		blockPositionUsed = new int[9];
		
		currentPosition = 0;
		
		mosaikBlock = new int[9][5];	//9 Rows for 9 blocks
		for(int i = 0; i < blockPosition.length; i++){
			blockPositionUsed[i] = blockPosition[i];
		}
		
		//5 crevices for x,y,activeBlockflag, ImageRegion x, ImageRegion y
		//Set x,y
		mosaikBlock[0][0] = halbeBildschirmBreite - (169/2);
		mosaikBlock[0][1] = halbeBildschirmHöhe - (169/2);
		mosaikBlock[3][0] = halbeBildschirmBreite - (169/2) + 57;
		mosaikBlock[3][1] = halbeBildschirmHöhe - (169/2);
		mosaikBlock[1][0] = halbeBildschirmBreite - (169/2) + 57 + 57;//Block 2 ist der leere Block 
		mosaikBlock[1][1] = halbeBildschirmHöhe - (169/2);//Block 2 ist der leere Block
		mosaikBlock[6][0] = halbeBildschirmBreite - (169/2);
		mosaikBlock[6][1] = halbeBildschirmHöhe - (169/2) + 57;
		mosaikBlock[8][0] = halbeBildschirmBreite - (169/2) + 57;
		mosaikBlock[8][1] = halbeBildschirmHöhe - (169/2) + 57;
		mosaikBlock[7][0] = halbeBildschirmBreite - (169/2) + 57 + 57;
		mosaikBlock[7][1] = halbeBildschirmHöhe - (169/2) + 57;
		mosaikBlock[4][0] = halbeBildschirmBreite - (169/2);
		mosaikBlock[4][1] = halbeBildschirmHöhe - (169/2) + 57 + 57;
		mosaikBlock[2][0] = halbeBildschirmBreite - (169/2) + 57;
		mosaikBlock[2][1] = halbeBildschirmHöhe - (169/2) + 57 + 57;
		mosaikBlock[5][0] = halbeBildschirmBreite - (169/2) + 57 + 57;
		mosaikBlock[5][1] = halbeBildschirmHöhe - (169/2) + 57 + 57;
		//Set active Block (activeBlockflag)
		mosaikBlock[0][2] = 1;
		for(int i = 1; i < mosaikBlock.length; i++){
			mosaikBlock[i][2] = 0;
		}
		
		imageBreite = activeImage.getWidth();
		imageHöhe = activeImage.getHeight();
		//Set the regions of the image --> ImageRegion x, ImageRegion y
		mosaikBlock[0][3] = imageBreite - 165;
		mosaikBlock[0][4] = imageHöhe - 165;
		mosaikBlock[1][3] = imageBreite - 165 + 55;
		mosaikBlock[1][4] = imageHöhe - 165;
		mosaikBlock[2][3] = imageBreite - 165 + 55 + 55;//Block 2 is the empty block 
		mosaikBlock[2][4] = imageHöhe - 165;			//Block 2 is the empty block
		mosaikBlock[3][3] = imageBreite - 165;
		mosaikBlock[3][4] = imageHöhe - 165 + 55;
		mosaikBlock[4][3] = imageBreite - 165 + 55;
		mosaikBlock[4][4] = imageHöhe - 165 + 55;
		mosaikBlock[5][3] = imageBreite - 165 + 55 + 55;
		mosaikBlock[5][4] = imageHöhe - 165 + 55;
		mosaikBlock[6][3] = imageBreite - 165;
		mosaikBlock[6][4] = imageHöhe - 165 + 55 + 55;
		mosaikBlock[7][3] = imageBreite - 165 + 55;
		mosaikBlock[7][4] = imageHöhe - 165 + 55 + 55;
		mosaikBlock[8][3] = imageBreite - 165 + 55 + 55;
		mosaikBlock[8][4] = imageHöhe - 165 + 55 + 55;
	}

	/**
	 * Renders the scene. 
	 * Called by GameManager
	 * @see Story.Riddle#render(javax.microedition.lcdui.Graphics)
	 * @param g The graphics context to draw on.
	 */
	public void render(Graphics g){
		//for manual
		if (introductionManual != null) {
				introductionManual.render(g);
				return;
		}
		//Is Mosiak solved?
		if(mosaikRiddleDissolved()){
			if(endDelay == 30){
				setFinished(true);
				StoryManager.getInstance().onRiddleSolved((byte)1);
				activeImage = null;
			}
			else{
				//For the Background
				g.setColor(0, 0, 0);
				g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
				g.setColor(0, 0, 255);
				g.fillArc(5, 5, IODevice.getInstance().getWidth() - 10, IODevice.getInstance().getHeight() - 10, 0, 360);
				//Draw the solution of the riddle
				g.drawImage(activeImage, IODevice.getInstance().getWidth()/2 -165/2, IODevice.getInstance().getHeight()/2 -165/2, Graphics.TOP | Graphics.LEFT);
				endDelay++;
			}	
		}
		else{
			//For the Background
			g.setColor(0, 0, 0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
			g.setColor(0, 0, 255);
			g.fillArc(5, 5, IODevice.getInstance().getWidth() - 10, IODevice.getInstance().getHeight() - 10, 0, 360);
			//For the space in the image and the empty Block
			g.setColor(0, 0, 0);
			g.fillRect(IODevice.getInstance().getWidth()/2 -169/2, IODevice.getInstance().getHeight()/2 -169/2, 169, 169);		
			//Draw the Blocks
			g.fillRect(mosaikBlock[2][0], mosaikBlock[2][1], 55, 55);
			for(int i = 0; i < mosaikBlock.length; i++){
				if(i == 2){
					//Draw activeBlock
					if(mosaikBlock[i][2] == 1){
						g.setColor(255, 255, 255);
						g.drawRect(mosaikBlock[i][0] + 10, mosaikBlock[i][1] + 10, 35, 35);
					}
					continue;
				}
				else{
					try{
						//Draw the different regions of the image
						g.drawRegion(activeImage, mosaikBlock[i][3], mosaikBlock[i][4], 55, 55, 0, mosaikBlock[i][0], 
								mosaikBlock[i][1], Graphics.TOP | Graphics.LEFT);
					}
					catch(Exception e){
						new Error("mosaik render drawRegion", e);
					}
					//Draw activeBlock
					if(mosaikBlock[i][2] == 1){
						g.setColor(255, 255, 255);
						g.drawRect(mosaikBlock[i][0] + 10, mosaikBlock[i][1] + 10, 35, 35);
					}
				}	
			}	
		}
	}
		
	/**
	 * Manage what happen when you press a button
	 * @see Story.Riddle#onKeyDown(int)
	 * @param keyCode Value for which button was pressed
	 */
	public void onKeyDown(int keyCode){
		// for manual
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
			}
			return;
		}
		switch (keyCode)
		{
		  case GameManager.DOWN:
		  {
			  if((currentPosition == 6) || (currentPosition == 7) || (currentPosition == 8)){
				  break;
			  }
			  else{
				 activeBlock((short)currentPosition, (short)(currentPosition + 3));
				 currentPosition = (short)(currentPosition + 3);
				 break;
			  } 
		  }
		  case GameManager.UP:
		  {
			  if((currentPosition == 0) || (currentPosition == 1) || (currentPosition == 2)){
				  break;
			  }
			  else{
				 activeBlock((short)currentPosition, (short)(currentPosition - 3));
				 currentPosition = (short)(currentPosition - 3);
				 break;
			  }
		  }
		  case GameManager.RIGHT:
		  {
			  if((currentPosition == 2) || (currentPosition == 5) || (currentPosition == 8)){
				  break;
			  }
			  else{
				 activeBlock((short)currentPosition, (short)(currentPosition + 1));
				 currentPosition = (short)(currentPosition + 1);
				 break;
			  }
		  }
		  case GameManager.LEFT:
		  {
			  if((currentPosition == 0) || (currentPosition == 3) || (currentPosition == 6)){
				  break;
			  }
			  else{
				activeBlock((short)currentPosition, (short)(currentPosition - 1));
				currentPosition = (short)(currentPosition - 1);
				break;
			  }
		  }
		  case GameManager.ATTACK:
		  {
			  if(blockPositionUsed[currentPosition] == 2){
				  break;
			  }
			  else{
				  activeBlock(currentPosition);
				  break;
			  }
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
	 * Change the value of blocks between active (new Block) or not active (old Block)
	 * @param positionAlt Position of the old block
	 * @param positionNeu Position of the new block
	 */
	private void activeBlock(short positionAlt, short positionNeu){
		mosaikBlock[blockPositionUsed[positionAlt]][2] = 0;
		mosaikBlock[blockPositionUsed[positionNeu]][2] = 1;
	}
	
	/**
	 * Set the value of blocks for changing positions
	 * @param block Block which change its position
	 */
	private void activeBlock(short block){
		short position = -1;
		mosaikBlock[blockPositionUsed[block]][2] = 2;
		for(int i = 0; i < blockPositionUsed.length; i++){
			if(blockPositionUsed[i] == 2){
				position = (short)i;
			}
		}
		if((currentPosition == position + 1) || (currentPosition == position - 1) 
				|| (currentPosition == position + 3) || (currentPosition == position - 3)){
			changeBlock((short)currentPosition, (short)(position));
			currentPosition = position;
		}
	}
	
	/**
	 * Stop the riddle, whithout solving
	 * @see Story.Riddle#stop()
	 */
	public void stop(){
		setFinished(true);
	}
	
	/**
	 * Change the blocks 
	 * @param blockAlt Position of the old Block	
	 * @param blockNeu Position of the new Block
	 */
	private void changeBlock(short blockAlt, short blockNeu){
		int x = mosaikBlock[blockPositionUsed[blockAlt]][0];
		int y = mosaikBlock[blockPositionUsed[blockAlt]][1];
		int block = blockPositionUsed[blockAlt];
		
		mosaikBlock[blockPositionUsed[blockAlt]][0] = mosaikBlock[blockPositionUsed[blockNeu]][0];
		mosaikBlock[blockPositionUsed[blockAlt]][1] = mosaikBlock[blockPositionUsed[blockNeu]][1];
		blockPositionUsed[blockAlt] = blockPositionUsed[blockNeu];
		
		mosaikBlock[blockPositionUsed[blockNeu]][0] = x;
		mosaikBlock[blockPositionUsed[blockNeu]][1] = y;
		blockPositionUsed[blockNeu] = block;	
		mosaikBlock[blockPositionUsed[blockNeu]][2] = 1;	
	}
	
	/**
	 * Check the Mosaik if it is solved
	 * @return Solved or not?
	 */
	private boolean mosaikRiddleDissolved(){
		int finishCount = 0;
		int blockNummer = 0;
		for(int i = 0; i < blockPositionUsed.length; i++){
			if(blockPositionUsed[i] == blockNummer){
				blockNummer += 1;
				finishCount += 1;
			}
			else{
				break;
			}
		}
		if(finishCount == 9){
			return true;
		}
		return false;
	}
	
	/**
	 * Starts the riddle again from the beginning
	 * @see Story.Riddle#reset()
	 */
	public void reset(){
		for(int i = 0; i < blockPosition.length; i++){
			blockPositionUsed[i] = blockPosition[i];
		}
		
		currentPosition = 0;
		//5 crevices for x,y,activeBlockflag, ImageRegion x, ImageRegion y
		//Set x,y
		mosaikBlock[0][0] = halbeBildschirmBreite - (169/2);
		mosaikBlock[0][1] = halbeBildschirmHöhe - (169/2);
		mosaikBlock[3][0] = halbeBildschirmBreite - (169/2) + 57;
		mosaikBlock[3][1] = halbeBildschirmHöhe - (169/2);
		mosaikBlock[1][0] = halbeBildschirmBreite - (169/2) + 57 + 57;//Block 2 ist der leere Block 
		mosaikBlock[1][1] = halbeBildschirmHöhe - (169/2);//Block 2 ist der leere Block
		mosaikBlock[6][0] = halbeBildschirmBreite - (169/2);
		mosaikBlock[6][1] = halbeBildschirmHöhe - (169/2) + 57;
		mosaikBlock[8][0] = halbeBildschirmBreite - (169/2) + 57;
		mosaikBlock[8][1] = halbeBildschirmHöhe - (169/2) + 57;
		mosaikBlock[7][0] = halbeBildschirmBreite - (169/2) + 57 + 57;
		mosaikBlock[7][1] = halbeBildschirmHöhe - (169/2) + 57;
		mosaikBlock[4][0] = halbeBildschirmBreite - (169/2);
		mosaikBlock[4][1] = halbeBildschirmHöhe - (169/2) + 57 + 57;
		mosaikBlock[2][0] = halbeBildschirmBreite - (169/2) + 57;
		mosaikBlock[2][1] = halbeBildschirmHöhe - (169/2) + 57 + 57;
		mosaikBlock[5][0] = halbeBildschirmBreite - (169/2) + 57 + 57;
		mosaikBlock[5][1] = halbeBildschirmHöhe - (169/2) + 57 + 57;
		//Set active Block (activeBlockflag)
		mosaikBlock[0][2] = 1;
		for(int i = 1; i < mosaikBlock.length; i++){
			mosaikBlock[i][2] = 0;
		}
		
		//Set the regions of the image --> ImageRegion x, ImageRegion y
		mosaikBlock[0][3] = imageBreite - 165;
		mosaikBlock[0][4] = imageHöhe - 165;
		mosaikBlock[1][3] = imageBreite - 165 + 55;
		mosaikBlock[1][4] = imageHöhe - 165;
		mosaikBlock[2][3] = imageBreite - 165 + 55 + 55;//Block 2 is the empty block 
		mosaikBlock[2][4] = imageHöhe - 165;			//Block 2 is the empty block
		mosaikBlock[3][3] = imageBreite - 165;
		mosaikBlock[3][4] = imageHöhe - 165 + 55;
		mosaikBlock[4][3] = imageBreite - 165 + 55;
		mosaikBlock[4][4] = imageHöhe - 165 + 55;
		mosaikBlock[5][3] = imageBreite - 165 + 55 + 55;
		mosaikBlock[5][4] = imageHöhe - 165 + 55;
		mosaikBlock[6][3] = imageBreite - 165;
		mosaikBlock[6][4] = imageHöhe - 165 + 55 + 55;
		mosaikBlock[7][3] = imageBreite - 165 + 55;
		mosaikBlock[7][4] = imageHöhe - 165 + 55 + 55;
		mosaikBlock[8][3] = imageBreite - 165 + 55 + 55;
		mosaikBlock[8][4] = imageHöhe - 165 + 55 + 55;
	}
}
