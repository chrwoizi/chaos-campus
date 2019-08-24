/**
 * 
 */
package Story;

import GameManager;
import IODevice;
import Error;

import javax.microedition.lcdui.*;

import Gui.Manuals;

/**
 * Class for playing the Destillierer riddle
 * @author Sascha Lity
 *
 */
public class DestillierRiddle extends Riddle {

	private short currentPosition = 0;
	private short endDelay= 0;
	private short activeBlock = 0;
	private byte flag = 0;
	private byte changeBlock = 0;
	private byte changePosition;
	
	private Image activeImage = null;
	private int halbeBildschirmBreite = IODevice.getInstance().getWidth()/2;
	private int halbeBildschirmHöhe = IODevice.getInstance().getHeight()/2;
	private int imageBreite;
	private int imageHöhe;
	private int[][] imagePart;
	private int[][] partPosition;
	
	Manuals introductionManual = null;
	
	/**
	 * Constructor of DestillierRiddle
	 */
	public DestillierRiddle(){
		//Load Instruction
		introductionManual = new Manuals(Manuals.DESTILLIERRIDDLE);
		hasManual = true;
		
		//Load DestillierImage
		try{
			activeImage = Image.createImage("/riddle/Destillierer.png");
		}
		catch (Exception e){
			new Error("destiller img", e);
		}
		
		partPosition = new int[9][3];
		//Set x,y
		partPosition[0][0] = halbeBildschirmBreite - (165/2);
		partPosition[0][1] = halbeBildschirmHöhe - (165/2);
		partPosition[1][0] = halbeBildschirmBreite - (165/2) + 55;
		partPosition[1][1] = halbeBildschirmHöhe - (165/2);
		partPosition[2][0] = halbeBildschirmBreite - (165/2) + 55 + 55;
		partPosition[2][1] = halbeBildschirmHöhe - (165/2);
		partPosition[3][0] = halbeBildschirmBreite - (165/2);
		partPosition[3][1] = halbeBildschirmHöhe - (165/2) + 55;
		partPosition[4][0] = halbeBildschirmBreite - (165/2) + 55;
		partPosition[4][1] = halbeBildschirmHöhe - (165/2) + 55;
		partPosition[5][0] = halbeBildschirmBreite - (165/2) + 55 + 55;
		partPosition[5][1] = halbeBildschirmHöhe - (165/2) + 55;
		partPosition[6][0] = halbeBildschirmBreite - (165/2);
		partPosition[6][1] = halbeBildschirmHöhe - (165/2) + 55 + 55;
		partPosition[7][0] = halbeBildschirmBreite - (165/2) + 55;
		partPosition[7][1] = halbeBildschirmHöhe - (165/2) + 55 + 55;
		partPosition[8][0] = halbeBildschirmBreite - (165/2) + 55 + 55;
		partPosition[8][1] = halbeBildschirmHöhe - (165/2) + 55 + 55;
		//no block is active 
		for(int i = 0; i < partPosition.length; i++){
			partPosition[i][2] = -1;
		}
	
		imagePart = new int[9][2];
		imageBreite = activeImage.getWidth();
		imageHöhe = activeImage.getHeight();
		//Image sectional in 3x3 blocks 
		imagePart[0][0] = imageBreite - 165 + 55; 		//Block 4
		imagePart[0][1] = imageHöhe - 165 + 55;	  		//Block 4
		imagePart[1][0] = imageBreite - 165 + 55; 		//Block 1
		imagePart[1][1] = imageHöhe - 165;				//Block 1
		imagePart[2][0] = imageBreite - 165;			//Block 0
		imagePart[2][1] = imageHöhe - 165;				//Block 0
		imagePart[3][0] = imageBreite - 165 + 55;		//Block 7
		imagePart[3][1] = imageHöhe - 165 + 55 + 55;	//Block 7
		imagePart[4][0] = imageBreite - 165 + 55 + 55;	//Block 8
		imagePart[4][1] = imageHöhe - 165 + 55 + 55;	//Block 8
		imagePart[5][0] = imageBreite - 165;			//Block 6
		imagePart[5][1] = imageHöhe - 165 + 55 + 55;	//Block 6
		imagePart[6][0] = imageBreite - 165 + 55 + 55;	//Block 5
		imagePart[6][1] = imageHöhe - 165 + 55;			//Block 5
		imagePart[7][0] = imageBreite - 165;			//Block 3
		imagePart[7][1] = imageHöhe - 165 + 55;			//Block 3
		imagePart[8][0] = imageBreite - 165 + 55 + 55;	//Block 2
		imagePart[8][1] = imageHöhe - 165;				//Block 2
		
	}
	
	
	/**
	 * Manage what happen when you press a button
	 * @see Story.Riddle#onKeyDown(int)
	 * @param keyCode Value for which button was pressed
	 */
	public void onKeyDown(int keyCode) {
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
				   currentPosition = (short)(currentPosition - 1);
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
		  case GameManager.ATTACK:
		  {
			  if(flag == 0){
				  if(activeBlock == 8){
					  partPosition[currentPosition][2] = activeBlock;
					  flag = 1;
				  }
				  else{
					  if(partPosition[currentPosition][2] == -1){
						  partPosition[currentPosition][2] = activeBlock;
						  activeBlock += 1;  
					  }
				  }
			  }
			  else{
				 activeBlock = (short)(partPosition[currentPosition][2]); 
			  }

			  if(flag == 1){
				  if(changeBlock == 0){
					  changeBlock = 1;
					  changePosition = (byte)(currentPosition);
					  activeBlock = (short)(partPosition[currentPosition][2]);
				  }
				  else{
					  changeBlock = (byte)(partPosition[currentPosition][2]);
					  partPosition[currentPosition][2] = partPosition[changePosition][2];
					  partPosition[changePosition][2] = changeBlock;
					  changeBlock = 0;
					  activeBlock = (short)(partPosition[currentPosition][2]);
				  }
			  }
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
		//Is Destillier solved?
		if(destillierRiddleDissolved()){
			if(endDelay == 30){
				setFinished(true);
				StoryManager.getInstance().onRiddleSolved((byte)2);
				activeImage = null;
			}
			else{
				//For the Background
				g.setColor(0, 0, 0);
				g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
				g.setColor(0, 255, 0);
				g.fillRect(5, 5, IODevice.getInstance().getWidth() - 15, IODevice.getInstance().getHeight() - 15);
				//Zeichnet Lösung 
				g.drawImage(activeImage, IODevice.getInstance().getWidth()/2 -165/2, 
						IODevice.getInstance().getHeight()/2 -165/2, Graphics.TOP | Graphics.LEFT);
				endDelay++;
			}	
		}
		else if(flag == 1){
			//For the Background
			g.setColor(0, 0, 0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
			g.setColor(0, 255, 0);
			g.fillRect(5, 5, IODevice.getInstance().getWidth() - 10, IODevice.getInstance().getHeight() - 10);
			g.setColor(255, 0, 0);
			g.fillRect(IODevice.getInstance().getWidth()/2 - activeImage.getWidth()/2, 
					IODevice.getInstance().getHeight()/2 - activeImage.getHeight()/2, 
					activeImage.getWidth(), activeImage.getHeight());
			//Draw the blocks
			try{
				for(int i = 0; i < partPosition.length; i++){
					if(partPosition[i][2] > -1){
						g.drawRegion(activeImage, imagePart[partPosition[i][2]][0], 
								imagePart[partPosition[i][2]][1], 55, 55, 0, partPosition[i][0], 
								partPosition[i][1], Graphics.TOP|Graphics.LEFT);
					}
				}
			}
			catch(Exception e){
				new Error("destiller render flag==1", e);
			}
			if(changeBlock == 1){
				//draw the active block
				g.setColor(255, 255, 255);
				g.fillRect(partPosition[currentPosition][0] + 10, partPosition[currentPosition][1] + 10, 35, 35);
			}
			else{
				//draw the active block
				g.setColor(255, 255, 255);
				g.drawRect(partPosition[currentPosition][0] + 10, partPosition[currentPosition][1] + 10, 35, 35);
			}
		}
		else{
			//For the Background
			g.setColor(0, 0, 0);
			g.fillRect(0, 0, IODevice.getInstance().getWidth(), IODevice.getInstance().getHeight());
			g.setColor(0, 255, 0);
			g.fillRect(5, 5, IODevice.getInstance().getWidth() - 10, IODevice.getInstance().getHeight() - 10);
			g.setColor(255, 0, 0);
			g.fillRect(IODevice.getInstance().getWidth()/2 - activeImage.getWidth()/2, 
					IODevice.getInstance().getHeight()/2 - activeImage.getHeight()/2, 
					activeImage.getWidth(), activeImage.getHeight());
			//Draw blocks
			try{
				for(int i = 0; i < partPosition.length; i++){
					if(partPosition[i][2] > -1){
						g.drawRegion(activeImage, imagePart[partPosition[i][2]][0], 
								imagePart[partPosition[i][2]][1], 55, 55, 0, partPosition[i][0], 
								partPosition[i][1], Graphics.TOP|Graphics.LEFT);
					}
				}
					g.drawRegion(activeImage, imagePart[activeBlock][0], imagePart[activeBlock][1], 
							55, 55, 0, partPosition[currentPosition][0], 
							partPosition[currentPosition][1], Graphics.TOP|Graphics.LEFT);
			}
			catch(Exception e){
				new Error("render else", e);
			}
		}
	}

	/**
	 * Stop the riddle, whithout solving
	 * @see Story.Riddle#stop()
	 */
	public void stop() {
		setFinished(true);
	}
	
	/**
	 * Check the Destillier if it is solved
	 * @return Solved or not?
	 */
	private boolean destillierRiddleDissolved(){
		if((partPosition[0][2] == 2) && (partPosition[1][2] == 1) && (partPosition[2][2] == 8)){
			if((partPosition[3][2] == 7) && (partPosition[4][2] == 0) && (partPosition[5][2] == 6)){
				if((partPosition[6][2] == 5) && (partPosition[7][2] == 3) && (partPosition[8][2] == 4)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Starts the riddle again from the beginning
	 * @see Story.Riddle#reset()
	 */
	public void reset(){
		//Set x,y
		partPosition[0][0] = halbeBildschirmBreite - (165/2);
		partPosition[0][1] = halbeBildschirmHöhe - (165/2);
		partPosition[1][0] = halbeBildschirmBreite - (165/2) + 55;
		partPosition[1][1] = halbeBildschirmHöhe - (165/2);
		partPosition[2][0] = halbeBildschirmBreite - (165/2) + 55 + 55;
		partPosition[2][1] = halbeBildschirmHöhe - (165/2);
		partPosition[3][0] = halbeBildschirmBreite - (165/2);
		partPosition[3][1] = halbeBildschirmHöhe - (165/2) + 55;
		partPosition[4][0] = halbeBildschirmBreite - (165/2) + 55;
		partPosition[4][1] = halbeBildschirmHöhe - (165/2) + 55;
		partPosition[5][0] = halbeBildschirmBreite - (165/2) + 55 + 55;
		partPosition[5][1] = halbeBildschirmHöhe - (165/2) + 55;
		partPosition[6][0] = halbeBildschirmBreite - (165/2);
		partPosition[6][1] = halbeBildschirmHöhe - (165/2) + 55 + 55;
		partPosition[7][0] = halbeBildschirmBreite - (165/2) + 55;
		partPosition[7][1] = halbeBildschirmHöhe - (165/2) + 55 + 55;
		partPosition[8][0] = halbeBildschirmBreite - (165/2) + 55 + 55;
		partPosition[8][1] = halbeBildschirmHöhe - (165/2) + 55 + 55;
		//no block is active
		for(int i = 0; i < partPosition.length; i++){
			partPosition[i][2] = -1;
		}
		
		//Image sectional in 3x3 blocks
		imagePart[0][0] = imageBreite - 165 + 55; 		//Block 4
		imagePart[0][1] = imageHöhe - 165 + 55;	  		//Block 4
		imagePart[1][0] = imageBreite - 165 + 55; 		//Block 1
		imagePart[1][1] = imageHöhe - 165;				//Block 1
		imagePart[2][0] = imageBreite - 165;			//Block 0
		imagePart[2][1] = imageHöhe - 165;				//Block 0
		imagePart[3][0] = imageBreite - 165 + 55;		//Block 7
		imagePart[3][1] = imageHöhe - 165 + 55 + 55;	//Block 7
		imagePart[4][0] = imageBreite - 165 + 55 + 55;	//Block 8
		imagePart[4][1] = imageHöhe - 165 + 55 + 55;	//Block 8
		imagePart[5][0] = imageBreite - 165;			//Block 6
		imagePart[5][1] = imageHöhe - 165 + 55 + 55;	//Block 6
		imagePart[6][0] = imageBreite - 165 + 55 + 55;	//Block 5
		imagePart[6][1] = imageHöhe - 165 + 55;			//Block 5
		imagePart[7][0] = imageBreite - 165;			//Block 3
		imagePart[7][1] = imageHöhe - 165 + 55;			//Block 3
		imagePart[8][0] = imageBreite - 165 + 55 + 55;	//Block 2
		imagePart[8][1] = imageHöhe - 165;				//Block 2
		
		flag = 0;
		activeBlock = 0;
	}

}
