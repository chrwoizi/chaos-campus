/**
 * 
 */
package Story;

import javax.microedition.lcdui.*;

import Gui.Manuals;
import Error;

import GameManager;
import IODevice;
import java.util.Random;


/**
 * Class for playing the MastermindRiddle
 * @author Sascha Lity
 *
 */
public class MastermindRiddle extends Riddle {

	private Image activeImage;
	private Random randomFarbe;
	private int[][] reihe;
	private int[][] reihenPosition;
	private int[] farbe;
	private int[] loesung;
	private int[][] kontrolle;
	private short activeBlock;
	private short activePosition;
	private boolean flag = false;
	private int endDelay = 0;
	private short currentRow = 0;
	private short activeColor = -1;
	private short test = 0;
	private boolean reset = false;

	private int height = IODevice.getInstance().getHeight();
	private int width = IODevice.getInstance().getWidth();
	
	Manuals introductionManual = null;
	
	/**
	* Constructor of NumberRiddle
	*/
	public MastermindRiddle(){
		//Load Instruction
		introductionManual = new Manuals(Manuals.MASTERMINDRIDDLE);
		hasManual = true;
		//Load MastermindRiddleImage
		try{
			activeImage = Image.createImage("/riddle/MastermindBack.png");
		}
		catch (Exception e){
			new Error("mastermind img", e);
		}
		
		farbe = new int[6];
		farbe[0] = 0;
		farbe[1] = 1;
		farbe[2] = 2;
		farbe[3] = 3;
		farbe[4] = 4;
		farbe[5] = 5;
		
		randomFarbe = new Random();
		
		loesung = new int[4];
		
		for(int i = 0; i < loesung.length; i++){
			loesung[i] = randomFarbe.nextInt(6);
		}
		
		reihe = new int[7][4];
		for(int i = 0; i < reihe.length; i++){
			for(int j = 0; j < reihe[i].length; j++){
				reihe[i][j] = -1;
			}
		}
		
		reihenPosition = new int[7][2];
		//x and y values
		reihenPosition[0][0] = width/2;
		reihenPosition[0][1] = height/2 - activeImage.getHeight()/2 + 156;
		reihenPosition[1][0] = width/2;
		reihenPosition[1][1] = height/2 - activeImage.getHeight()/2 + 156 - 22;
		reihenPosition[2][0] = width/2;
		reihenPosition[2][1] = height/2 - activeImage.getHeight()/2 + 156 - 44;
		reihenPosition[3][0] = width/2;
		reihenPosition[3][1] = height/2 - activeImage.getHeight()/2 + 156 - 66;
		reihenPosition[4][0] = width/2;
		reihenPosition[4][1] = height/2 - activeImage.getHeight()/2 + 156 - 88;
		reihenPosition[5][0] = width/2;
		reihenPosition[5][1] = height/2 - activeImage.getHeight()/2 + 156 - 110;
		reihenPosition[6][0] = width/2;
		reihenPosition[6][1] = height/2 - activeImage.getHeight()/2 + 156 - 132;

		kontrolle = new int[7][6];
		for(int i = 0; i < kontrolle.length; i++){
			for(int j = 0; j < 4; j++){
				kontrolle[i][j] = 0;
			}
		}
		kontrolle[0][4] = width/2 - 30;
		kontrolle[0][5] = height/2 - activeImage.getHeight()/2 + 158;
		kontrolle[1][4] = width/2 - 30;
		kontrolle[1][5] = height/2 - activeImage.getHeight()/2 + 158 - 22;
		kontrolle[2][4] = width/2 - 30;
		kontrolle[2][5] = height/2 - activeImage.getHeight()/2 + 158 - 44;
		kontrolle[3][4] = width/2 - 30;
		kontrolle[3][5] = height/2 - activeImage.getHeight()/2 + 158 - 66;
		kontrolle[4][4] = width/2 - 30;
		kontrolle[4][5] = height/2 - activeImage.getHeight()/2 + 158 - 88;
		kontrolle[5][4] = width/2 - 30;
		kontrolle[5][5] = height/2 - activeImage.getHeight()/2 + 158 - 110;
		kontrolle[6][4] = width/2 - 30;
		kontrolle[6][5] = height/2 - activeImage.getHeight()/2 + 161 - 132;
		
		activePosition = 0;
		activeBlock = 0;
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
		  case GameManager.DOWN:
		  {
			  if(!flag){
				  if(activePosition == 5){
					  break;
				  }
				  else{
					  activePosition += 1;
					  break;
				  }
			  }
			  else{
				  break;
			  }
		  }
		  case GameManager.UP:
		  {
			  if(!flag){
				  if(activePosition == 0){
					  break;
				  }
				  else{
					  activePosition -= 1;
					  break;
				  }
			  }
			  else{
				  break;
			  }
		  }
		  case GameManager.RIGHT:
		  {
			  if(flag){
				  if(activeBlock == 3){
					  break;
				  }
				  else{
					  activeBlock += 1;
					  break;
				  }
			  }
			  else{
				  break;
			  }
		  }
		  case GameManager.LEFT:
		  {
			  if(flag){
				  if(activeBlock == 0){
					  break;
				  }
				  else{
					  activeBlock -= 1;
					  break;
				  }
			  }
			  else{
				  break;
			  }
		  }
		  case GameManager.ATTACK:
		  {
			  if(flag){
				  if(reihe[currentRow][activeBlock] == -1){
					  reihe[currentRow][activeBlock] = activeColor;
					  test += 1;
				  }
				  else{
					  reihe[currentRow][activeBlock] = activeColor;
				  }
				  if((test == 4) && (currentRow == 6)){
					  checkColor(reihe[currentRow]);
					  test = 0;
					  flag = false;
					  if(!mastermindRiddleDissolved()){
						  reset = true;
					  }
				  }
				  else if((test == 4) && (currentRow != 6)){
					  checkColor(reihe[currentRow]);
					  test = 0;
					  flag = false;
					  if(currentRow < 6){
						  currentRow += 1;
					  }
				  }
				  else{
					  flag = false;
				  }
				  break;
			  }
			  else{
				  activeColor = (short)farbe[activePosition];
				  flag = true;
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
	 * Renders the scene. 
	 * Called by GameManager
	 * @see Story.Riddle#render(javax.microedition.lcdui.Graphics)
	 * @param g The graphics context to draw on.
	 */
	public void render(Graphics g) {
		if( this.isfinished() )
			return;
		//for manual
		if (introductionManual != null) {
				introductionManual.render(g);
				return;
		}
		//For Reset after not solve the riddle
		if(reset == true){
			if(endDelay == 20){
				reset = false;
				endDelay = 0;
				reset();
			}
			else{
				g.setColor(0, 0, 0);
				g.fillRect(0, 0, width, height);
				g.setColor(255, 0, 0);
				g.drawString("Nicht gelöst", width/2 - 30, 
						height/2 - 5, Graphics.BASELINE|Graphics.LEFT);
				g.drawString("Neustart", width/2 - 30, 
						height/2 + 10, Graphics.BASELINE|Graphics.LEFT);
				endDelay++;
			}
		}
		else{
//			Is Mastermind solved?
			if((mastermindRiddleDissolved()) && endDelay < 30){
			//Background
			g.setColor(0, 0, 0);
			g.fillRect(0, 0, width, height);
			g.drawImage(activeImage, width/2 - activeImage.getWidth()/2 , 
					height/2 - activeImage.getHeight()/2, 
					Graphics.TOP | Graphics.LEFT);
			//Draw Solution
			for(int i = 0; i < loesung.length; i++){
				getFarbe(loesung[i], g);
				g.fillArc(width/2 + i*20, height/2 - activeImage.getHeight()/2 + 
						159 - 154, 8, 8, 0, 360);
			}	
			endDelay++;
			}
			else{
				//Background
				g.setColor(0, 0, 0);
				g.fillRect(0, 0, width, height);
				g.drawImage(activeImage, width/2 - activeImage.getWidth()/2 , 
						height/2 - activeImage.getHeight()/2, 
						Graphics.TOP | Graphics.LEFT);
			//	Black box because the solution is hided
				g.fillRect(width/2 - 5, height/2 - activeImage.getHeight()/2 + 
						157 - 154, 80, 10);		
				if(flag){
					for(int i = 0; i < reihe.length; i++){
						for(int j = 0; j < reihe[i].length; j++){
							if((currentRow == i) && (activeBlock == j)){
								g.setColor(255, 127, 36);
								g.fillRect(reihenPosition[i][0] - 2 + j* 20 , 
										reihenPosition[i][1] - 1, 
										12, 10);
							}
						}	
					}
				}
			}
		//	Draw Rows
			for(int i = 0; i < reihe.length; i++){
				for(int j = 0; j < reihe[i].length; j++){
					getFarbe(reihe[i][j],g);
					if(reihe[i][j] == -1){
						g.setColor(0, 0, 0);
						g.drawArc(reihenPosition[i][0] + j* 20 , reihenPosition[i][1], 
								8, 8, 0, 360);
					}
					else{
						g.fillArc(reihenPosition[i][0] + j* 20 , reihenPosition[i][1], 
								8, 8, 0, 360);
					}
					if(kontrolle[i][j] == 1){
						g.setColor(255, 255, 255);
						g.fillArc(kontrolle[i][4] + j*6, 
								kontrolle[i][5], 4, 4, 0, 360);
					}
					else if(kontrolle[i][j] == 2){
						g.setColor(0, 0, 0);
						g.fillArc(kontrolle[i][4] + j*6, 
								kontrolle[i][5], 4, 4, 0, 360);
					}
					else{
						g.setColor(0, 0, 0);
						g.drawArc(kontrolle[i][4] + j*6, 
								kontrolle[i][5], 3, 3, 0, 360);
					}	
				}	
				if(i < 6){
						if(activePosition == i){
								g.setColor(255, 127, 36);
								g.fillRect(width/2 - 62, 
										height/2 - activeImage.getHeight()/2 + 20 + activePosition*25, 
										15, 15);
						}
						getFarbe(farbe[i], g);
						g.fillArc(width/2 -60, 
								height/2 - activeImage.getHeight()/2 + 22 + i*25, 
								10, 10, 0, 360);
				}
			}
			if((endDelay == 30) && mastermindRiddleDissolved()){
				setFinished(true);
				StoryManager.getInstance().onRiddleSolved((byte)5);
				activeImage = null;
			}
		}
	}

	/**
	 * Starts the riddle again from the beginning
	 * @see Story.Riddle#reset()
	 */
	public void reset() {
		for(int i = 0; i < loesung.length; i++){
			loesung[i] = randomFarbe.nextInt(6);
		}
		
		reihe = new int[7][4];
		for(int i = 0; i < reihe.length; i++){
			for(int j = 0; j < reihe[i].length; j++){
				reihe[i][j] = -1;
			}
		}
		
		kontrolle = new int[7][6];
		for(int i = 0; i < kontrolle.length; i++){
			for(int j = 0; j < 4; j++){
				kontrolle[i][j] = 0;
			}
		}
		
		kontrolle[0][4] = width/2 - 30;
		kontrolle[0][5] = height/2 - activeImage.getHeight()/2 + 158;
		kontrolle[1][4] = width/2 - 30;
		kontrolle[1][5] = height/2 - activeImage.getHeight()/2 + 158 - 22;
		kontrolle[2][4] = width/2 - 30;
		kontrolle[2][5] = height/2 - activeImage.getHeight()/2 + 158 - 44;
		kontrolle[3][4] = width/2 - 30;
		kontrolle[3][5] = height/2 - activeImage.getHeight()/2 + 158 - 66;
		kontrolle[4][4] = width/2 - 30;
		kontrolle[4][5] = height/2 - activeImage.getHeight()/2 + 158 - 88;
		kontrolle[5][4] = width/2 - 30;
		kontrolle[5][5] = height/2 - activeImage.getHeight()/2 + 158 - 110;
		kontrolle[6][4] = width/2 - 30;
		kontrolle[6][5] = height/2 - activeImage.getHeight()/2 + 161 - 132;
		
		activePosition = 0;
		activeBlock = 0;
		flag = false;
		currentRow = 0;

	}

	/**
	 * Stop the riddle, whithout solving
	 * @see Story.Riddle#stop()
	 */
	public void stop() {
		setFinished(true);
	}
	
	/**
	 * Check the MastermindRiddle if it is solved
	 * @return Solved or not?
	 */
	private boolean mastermindRiddleDissolved(){
		short dissolved = 0;
		if(currentRow == 0){
			for(int i = 0; i < kontrolle[0].length; i++){
				if(kontrolle[currentRow][i] == 2){
					dissolved += 1;
				}
			}
		}
		else if(currentRow == 6){
			for(int i = 0; i < kontrolle[6].length; i++){
				if(kontrolle[currentRow][i] == 2){
					dissolved += 1;
				}
			}
		}
		else{
			for(int i = 0; i < kontrolle[currentRow - 1].length; i++){
				if(kontrolle[currentRow - 1][i] == 2){
					dissolved += 1;
				}
			}
		}
		if(dissolved == 4){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Checks the order of the colors in the active row
	 * @param reihe active row
	 */
	private void checkColor(int[] reihe){
		boolean[] kontrolltesten = new boolean[4];
		for(int i = 0; i < kontrolltesten.length; i++){
			kontrolltesten[i] = false;
		}
		for(int j = 0; j < reihe.length; j++){
			if(reihe[j] == loesung[j]){
				kontrolltesten[j] = true;
				kontrolle[currentRow][j] = 2;
			}
		}
		for(int k = 0; k < reihe.length; k++){
			if((kontrolltesten[k] == false) && (reihe[k] != loesung[k])){
				for(int i = 0; i < reihe.length; i++){
					if((kontrolltesten[i] == false) && (reihe[i] == loesung[k])){
						kontrolltesten[k] = true;
						kontrolle[currentRow][k] = 1;
					}
					else if((kontrolltesten[i] == true) && (reihe[i] == loesung[k]) 
							&& (kontrolle[currentRow][i] == 1)){
						kontrolltesten[k] = true;
						kontrolle[currentRow][k] = 1;
					}
				}
			}
		}
	}
	
	/**
	 * Give the needed Color for the render method
	 * @param farbe int value for the switch/case
	 * @param g Graphic g
	 * @return the color of Graphic g
	 */
	private Graphics getFarbe(int farbe, Graphics g){
		switch(farbe){
		case 0:
		{
			g.setColor(255, 0, 0);
			break;
		}
		case 1:
		{
			g.setColor(0, 0, 255);
			break;
		}
		case 2:
		{
			g.setColor(255, 255, 0);
			break;
		}
		case 3:
		{
			g.setColor(0, 255, 0);
			break;
		}
		case 4:
		{
			g.setColor(0, 0, 0);
			break;
		}
		case 5:
		{
			g.setColor(255, 255, 255);
			break;
		}
		}
		return g;
	}
}
