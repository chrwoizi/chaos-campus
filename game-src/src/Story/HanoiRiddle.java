/**
 * 
 */
package Story;

import javax.microedition.lcdui.Graphics;
import Error;
import javax.microedition.lcdui.Image;

import Gui.Manuals;

import GameManager;
import IODevice;

/**
 * Class for playing the Hanoi Riddle
 * 
 * @author Sascha Lity
 */
public class HanoiRiddle extends Riddle {
	
	private Image activeImage = null;
	private Image stoneImage = null;
	private int[][] staebe;
	private int[][] stoneImageRegions;
	private int height = IODevice.getInstance().getHeight();
	private int width = IODevice.getInstance().getWidth();
	private short endDelay = 0;
	private short activeBlock;
	private short activeBlockTest = 0;
	private short currentPosition;
	private boolean flag = false;
	private short highscore = 0;
	
	Manuals introductionManual = null;
	
	/**
	 * Constructor of HanoiRiddle
	 */
	public HanoiRiddle(){
		//Load Instruction
		introductionManual = new Manuals(Manuals.HANOIRIDDLE);
		hasManual = true;
		
		//Load HanoiImage
		try{
			activeImage = Image.createImage("/riddle/hanoi_bg.png");
			stoneImage = Image.createImage("/riddle/hanoi_fg.png");
		}
		catch (Exception e){
			new Error("hanoi img", e);
		}
		
		staebe = new int[3][4];
		//at the beginning the 4 Stones are on the first bar
		staebe[0][0] = 4; //first bar
		staebe[0][1] = 3;
		staebe[0][2] = 2;
		staebe[0][3] = 1;
		staebe[1][0] = 0;	//second bar
		staebe[1][1] = 0;
		staebe[1][2] = 0;
		staebe[1][3] = 0;
		staebe[2][0] = 0;
		staebe[2][1] = 0;  //third bar
		staebe[2][2] = 0;
		staebe[2][3] = 0;
		
		activeBlock = 1;
		currentPosition = 0;
		
		stoneImageRegions = new int[5][2];
		//The Region x value and y value for render method
		stoneImageRegions[1][0] = 25;	 //Block 1
		stoneImageRegions[1][1] = 138;
		stoneImageRegions[2][0] = 18;	//Block 2
		stoneImageRegions[2][1] = 145;
		stoneImageRegions[3][0] = 12;	//BLock 3
		stoneImageRegions[3][1] = 152;
		stoneImageRegions[4][0] = 6;	//Block 4
		stoneImageRegions[4][1] = 159;
	
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
			}
			return;
		}
		switch (keyCode)
		{
		  case GameManager.RIGHT:
		  {
			  if(flag){
				 if(currentPosition == 2){
					 break;
				 }
				 else{
					 currentPosition += 1;					
					 break;
				 }
			 }
			 else{
				 if(currentPosition == 2){
					for(int i = 0; i < staebe[2].length; i++){
						if(staebe[2][i] != 0){
							activeBlock = (short)staebe[2][i];
						}
						else{
							activeBlockTest +=1;
						}
					 }
					if(activeBlockTest == 4){
						activeBlock = 0;
					}
					activeBlockTest = 0;
				 }
				else{
					currentPosition += 1;
					for(int i = 0; i < staebe[currentPosition].length; i++){
						if(staebe[currentPosition][i] != 0){
							activeBlock = (short)staebe[currentPosition][i];
						}
						else{
								activeBlockTest +=1;
						}
					}
					if(activeBlockTest == 4){
						activeBlock = 0;
					}
				}		
				 activeBlockTest = 0;
				}
			  break;
		  }
		  case GameManager.LEFT:
		  {
			  if(flag){
					 if(currentPosition == 0){
						 break;
					 }
					 else{
						 currentPosition -= 1;					
						 break;
					 }
				 }
				 else{
					 if(currentPosition == 0){
						for(int i = 0; i < staebe[0].length; i++){
							if(staebe[0][i] != 0){
								activeBlock = (short)staebe[0][i];
							}
							else{
								activeBlockTest +=1;
							}
						 }
						if(activeBlockTest == 4){
							activeBlock = 0;
						}	
						activeBlockTest = 0;
					 }
					else{
						currentPosition -= 1;
						for(int i = 0; i < staebe[currentPosition].length; i++){
							if(staebe[currentPosition][i] != 0){
								activeBlock = (short)staebe[currentPosition][i];
							}
							else{
								activeBlockTest +=1;
							}
						 }
						if(activeBlockTest == 4){
							activeBlock = 0;
						}
						}		
					 activeBlockTest = 0;
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
		  case GameManager.ATTACK:
		  {
			 if((activeBlock == 0) || ((activeBlock == 0) && (flag = true))){
				 flag = false;
			 }
			 else{
				 if(!flag){
						for(int i = 0; i < staebe[currentPosition].length; i++){
							if(staebe[currentPosition][i] == activeBlock){
								staebe[currentPosition][i] = 0;
							}
						}
						flag = true;
						break; 
				 }
				 else{
					 if(staebe[currentPosition][0] == 0){
						 staebe[currentPosition][0] = activeBlock;
						 flag = false;
						 highscore += 1;
					 }
					 else{
						for(int i = 1; i < staebe[currentPosition].length; i++){
							if((staebe[currentPosition][i] == 0) && (activeBlockTest == 0)){
								activeBlockTest = (short)i;
							}
						}
						if(checkOrder(activeBlockTest, currentPosition)){
							staebe[currentPosition][activeBlockTest] = activeBlock;
							highscore += 1;
							flag = false;
						}
						activeBlockTest = 0;
					 }
					 break;
				 } 
			 }
			 
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
		//Is Hanoi solved?
		if(hanoiRiddleDissolved()){
			if(endDelay == 30){
				setFinished(true);
				StoryManager.getInstance().onRiddleSolved((byte)3);
				activeImage = null;
			}
			else{
				//For the Background
				g.setColor(0, 0, 0);
				g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
				g.drawImage(activeImage, width/2 - activeImage.getWidth()/2, height/2 - activeImage.getHeight()/2, Graphics.TOP | Graphics.LEFT);
				//blocks
				for(int j = 0; j < staebe[2].length; j++){
					g.drawRegion(stoneImage, stoneImageRegions[staebe[2][j]][0], 
							stoneImageRegions[staebe[2][j]][1], staebe[2][j]*18, 7, 0, 
							width/2 - activeImage.getWidth()/2 + 146 - (staebe[2][j] * 6) + 1, 
							height/2 + activeImage.getHeight()/2 - 16 - j*7, 
							Graphics.TOP|Graphics.LEFT);	
				}
				//Draw highscore
				g.setColor(255, 255, 255);
				g.fillRect(width/2 - 10, height/2 -10, 20, 20);
				g.setColor(255,0,0);
				g.drawString("" + highscore, width/2 - 5, height/2 + 5, 
						Graphics.BASELINE|Graphics.LEFT);
				endDelay++;
			}	
		}
		else{
			//For the Background 
			g.setColor(0, 0, 0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), 
					IODevice.getInstance().getHeight());
			g.drawImage(activeImage, width/2 - activeImage.getWidth()/2, 
					height/2 - activeImage.getHeight()/2, 
					Graphics.TOP | Graphics.LEFT);
			//Blocks
			if(flag){
				//Draw highscore
				g.setColor(255, 255, 255);
				g.fillRect(width/2 - 10, height/2 -10, 20, 20);
				g.setColor(255,0,0);
				if(highscore < 10){
					g.drawString("" + highscore, width/2 - 2, 
							height/2 + 5, Graphics.BASELINE|Graphics.LEFT);
				}
				else{
					g.drawString("" + highscore, width/2 - 5, height/2 + 5, 
							Graphics.BASELINE|Graphics.LEFT);
				}
				//activeBlock
				switch(currentPosition){
				case 0:
					g.drawRegion(stoneImage, stoneImageRegions[activeBlock][0], 
							stoneImageRegions[activeBlock][1], activeBlock*18, 7, 0, 
							width/2 - activeImage.getWidth()/2 + 30 - (activeBlock * 6) + 1, 
							height/2 + activeImage.getHeight()/2 - 57, 
							Graphics.TOP|Graphics.LEFT);
					break;
				case 1:
					g.drawRegion(stoneImage, stoneImageRegions[activeBlock][0], 
							stoneImageRegions[activeBlock][1], activeBlock*18, 7, 0, 
							width/2 - activeImage.getWidth()/2 + 87 - (activeBlock * 6) + 2, 
							height/2 + activeImage.getHeight()/2 - 57, 
							Graphics.TOP|Graphics.LEFT);
					break;
				case 2:
					g.drawRegion(stoneImage, stoneImageRegions[activeBlock][0], 
							stoneImageRegions[activeBlock][1], activeBlock*18, 7, 0, 
							width/2 - activeImage.getWidth()/2 + 146 - (activeBlock * 6) + 1,
							height/2 + activeImage.getHeight()/2 - 57, 
							Graphics.TOP|Graphics.LEFT);
					break;
				}
				//other blocks
				for(int i = 0; i < staebe.length; i++){
					for(int j = 0; j < staebe[i].length; j++){
						try{
							if(i == 0){
								g.drawRegion(stoneImage, stoneImageRegions[staebe[i][j]][0], 
										stoneImageRegions[staebe[i][j]][1], staebe[i][j]*18, 7, 0, 
										width/2 - activeImage.getWidth()/2 + 30 - (staebe[i][j] * 6) + 1, 
										height/2 + activeImage.getHeight()/2 - 16 - j*7, 
										Graphics.TOP|Graphics.LEFT);
							}
							else if(i == 1){
								g.drawRegion(stoneImage, stoneImageRegions[staebe[i][j]][0], 
										stoneImageRegions[staebe[i][j]][1], staebe[i][j]*18, 7, 0, 
										width/2 - activeImage.getWidth()/2 + 87 - (staebe[i][j] * 6) + 2,
										height/2 + activeImage.getHeight()/2 - 16 - j*7, 
										Graphics.TOP|Graphics.LEFT);
							}
							else{
								g.drawRegion(stoneImage, stoneImageRegions[staebe[i][j]][0], 
										stoneImageRegions[staebe[i][j]][1], staebe[i][j]*18, 7, 0, 
										width/2 - activeImage.getWidth()/2 + 146 - (staebe[i][j] * 6) + 1, 
										height/2 + activeImage.getHeight()/2 - 16 - j*7, 
										Graphics.TOP|Graphics.LEFT);
							}	
						}
						catch(Exception e){
							new Error("stone img", e);
						}
					}
				}
			}
			else{
				//blocks
				for(int i = 0; i < staebe.length; i++){
					for(int j = 0; j < staebe[i].length; j++){
						try{
							if(i == 0){
								g.drawRegion(stoneImage, stoneImageRegions[staebe[i][j]][0], 
										stoneImageRegions[staebe[i][j]][1], staebe[i][j]*18, 7, 0, 
										width/2 - activeImage.getWidth()/2 + 30 - (staebe[i][j] * 6) + 1, 
										height/2 + activeImage.getHeight()/2 - 16 - j*7, 
										Graphics.TOP|Graphics.LEFT);
							}
							else if(i == 1){
								g.drawRegion(stoneImage, stoneImageRegions[staebe[i][j]][0], 
										stoneImageRegions[staebe[i][j]][1], staebe[i][j]*18, 7, 0, 
										width/2 - activeImage.getWidth()/2 + 87 - (staebe[i][j] * 6) + 2, 
										height/2 + activeImage.getHeight()/2 - 16 - j*7, 
										Graphics.TOP|Graphics.LEFT);
							}
							else{
								g.drawRegion(stoneImage, stoneImageRegions[staebe[i][j]][0], 
										stoneImageRegions[staebe[i][j]][1], staebe[i][j]*18, 7, 0, 
										width/2 - activeImage.getWidth()/2 + 146 - (staebe[i][j] * 6) + 1, 
										height/2 + activeImage.getHeight()/2 - 16 - j*7, 
										Graphics.TOP|Graphics.LEFT);
							}	
						}
						catch(Exception e){
							new Error("stone img", e);
						}
					}
				}
				//draw the choice point
				for(int i = 0; i < staebe[currentPosition].length; i++){
					if((staebe[currentPosition][i] == activeBlock) && (activeBlock != 0)){
						g.setColor(0,0,0);
						switch(currentPosition){
						case 0:
							g.fillRect(width/2 - activeImage.getWidth()/2 + 28, 
									height/2 + activeImage.getHeight()/2 - 15 - i*7, 6, 5);
							break;
						case 1:
							g.fillRect(width/2 - activeImage.getWidth()/2 + 86, 
									height/2 + activeImage.getHeight()/2 - 15 - i*7, 6, 5);
							break;
						case 2:
							g.fillRect(width/2 - activeImage.getWidth()/2 + 144, 
									height/2 + activeImage.getHeight()/2 - 15 - i*7, 6, 5);
							break;
						}
					}
				}
				//Draw highscore
				g.setColor(255, 255, 255);
				g.fillRect(width/2 - 10, height/2 -10, 20, 20);
				g.setColor(255,0,0);
				if(highscore < 10){
					g.drawString("" + highscore, width/2 - 2, 
							height/2 + 5, Graphics.BASELINE|Graphics.LEFT);
				}
				else{
					g.drawString("" + highscore, width/2 - 5, height/2 + 5, 
							Graphics.BASELINE|Graphics.LEFT);
				}
				
			}
			
		}
	}

	/**
	 * Starts the riddle again from the beginning
	 * @see Story.Riddle#reset()
	 */
	public void reset() {
		//at the beginning the 4 Stones are on the first bar
		staebe[0][0] = 4; 	//first bar
		staebe[0][1] = 3;
		staebe[0][2] = 2;
		staebe[0][3] = 1;
		staebe[1][0] = 0;	//second bar
		staebe[1][1] = 0;
		staebe[1][2] = 0;
		staebe[1][3] = 0;
		staebe[2][0] = 0;
		staebe[2][1] = 0;  	//third bar
		staebe[2][2] = 0;
		staebe[2][3] = 0;
		
		activeBlock = 1;
		currentPosition = 0;
		flag = false;
		highscore = 0;
	}

	/**
	 * Stop the riddle, whithout solving
	 * @see Story.Riddle#stop()
	 */
	public void stop() {
		setFinished(true);
	}
	
	/**
	 * Checks the order of the stones
	 * @param position Position of the Stone on the bar
	 * @param stab Bar which is active
	 * @return Is order position -1 < position
	 */
	private boolean checkOrder(short position, short stab){
		return (activeBlock < staebe[stab][position - 1]);
	}
	
	/**
	 * Check the HanoiRiddle if it is solved
	 * @return Solved or not?
	 */
	private boolean hanoiRiddleDissolved(){
		if(staebe[2][0] == 4){
			if(staebe[2][1] == 3){
				if(staebe[2][2] == 2){
					if(staebe[2][3] == 1){
						return true;
					}
				}
			}
		}
		return false;
	}

}
