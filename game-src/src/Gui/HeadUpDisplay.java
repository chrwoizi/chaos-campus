package Gui;
import java.io.IOException;
import IODevice;
import Player;
import Error;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;



/**
 * @author Paul,Martin
 *use to display  control pannel of player
 *
 *the Skills of Player depend on its substancial progress on Game
 */
public class HeadUpDisplay  implements Menu
{
    private static short timing=1;
    private static short timingTotal=1; 
    private static short showTextTime=0;
    private static String text = null; 

    private Image weaponImage=null,
    	         specialImage=null;
    
    public HeadUpDisplay(){
    	updateWeaponSpecialImages();
    }
    
    /**
     * updates the image that shows which weapon the player has selected
     *
     */
    public void updateWeaponSpecialImages() {
        byte inventoryItemSize = 24;
    	byte weapon = Player.getInstance().getWeapon();
    	if( weapon < 0 )
    		weaponImage = null;
    	else {
    		try {
				Image tempImage = Image.createImage("/InventoryItems.png");
				weaponImage = Image.createImage(tempImage, 
					(3+weapon)*inventoryItemSize, 0, inventoryItemSize,
					inventoryItemSize, Sprite.TRANS_NONE);
			} catch (IOException e) {
				new Error(e);
			}
    	}
    	byte special = Player.getInstance().getSpecialAttack();
    	if( weapon < 0 || special < 0 )
    		specialImage = null;
    	else {
        	if( weapon == 1 ) special += 3;
        	else if( weapon == 2 ) special += 4;
    		try {
				Image tempImage = Image.createImage("/SpecialsBig.png");
				specialImage = Image.createImage(tempImage, 
					special*inventoryItemSize, 0, inventoryItemSize,
					inventoryItemSize, Sprite.TRANS_NONE);
			} catch (IOException e) {
				new Error(e);
			}
    	}
    }

    
    /*draw all the Player control pannels.
     * 
     */
    public void renderMenu(Graphics g)
    {
    	//bottom-left text
    	if( showTextTime > 0 ) {
            g.setColor(0,0,0);
    		g.drawString(text, 9, IODevice.getInstance().getHeight()-35, Graphics.LEFT|Graphics.BASELINE);
            g.setColor(255,255,255);
    		g.drawString(text, 8, IODevice.getInstance().getHeight()-36, Graphics.LEFT|Graphics.BASELINE);
    		showTextTime--;
    	}

    	//Lebensbalken
    	int alcLevelBottom = IODevice.getInstance().getHeight() - 8;
    	int alcLevelX = IODevice.getInstance().getWidth() - 13;
    	// draw filling
        g.setColor(255,0,0);
        g.fillRect(alcLevelX-5, alcLevelBottom-Player.getInstance().getAlcLevel()*3/2,
        		11, Player.getInstance().getAlcLevel()*3/2);
        g.setColor(255,160,160);
        g.fillRect(alcLevelX+1, alcLevelBottom-Player.getInstance().getAlcLevel()*3/2,
        		3, Player.getInstance().getAlcLevel()*3/2);
        // draw borderline
        g.setColor(0,0,0);
        g.drawRect(alcLevelX-5, alcLevelBottom - Player.getInstance().getAlcLevelMaximum()*3/2,
        		11, Player.getInstance().getAlcLevelMaximum()*3/2);
        
        //TimingBalken
        if( timing < timingTotal ) {
	    	int timingBarLeft = 8;
	    	int timingBarY = IODevice.getInstance().getHeight() -8-24-13;
	        int timingBarLength = 24+24+8;
	        // draw filling
	    	g.setColor(255,255,0);
	        g.fillRect(timingBarLeft, timingBarY-5, 
	        		(int)(timingBarLength * timing / timingTotal), 11);
	    	g.setColor(255,255,160);
	        g.fillRect(timingBarLeft, timingBarY-3, 
	        		(int)(timingBarLength * timing / timingTotal), 3);
	        // draw borderline
	        g.setColor(0,0,0);
	        g.drawRect(timingBarLeft, timingBarY-5, timingBarLength, 11);
        }
        
        // specials
        if( weaponImage != null ) {
        	g.drawImage(weaponImage, 20, IODevice.getInstance().getHeight()-20,
        			Graphics.HCENTER | Graphics.VCENTER);
        }
        if( specialImage != null ) {
        	g.drawImage(specialImage, 48, IODevice.getInstance().getHeight()-20,
        			Graphics.HCENTER | Graphics.VCENTER);
        }
        //counter
        if( timing < timingTotal ) {
        	timing++;
        }
        
    }

    /**
     * starts the visualisation of the yellow timing bar 
     * @param time
     */
    public static void setSpecialTimer(short time){
    	if( time <= 0 )
    		new Error("setSpecialTimer must be called with a value greater 0");
    	timingTotal = time;
    	timing = 0;
    }
   
    
    /**
     * starts the visualisation of a wanted text in the bottom left corner
     * @param text
     * @param duration
     */
    public static void showText(String text, short duration) {
    	HeadUpDisplay.text = text;
    	HeadUpDisplay.showTextTime = duration;
    }
    
    //unused
	/* (non-Javadoc)
	 * @see Menu#activeItem(int)
	 */
	public void activeItem(int position) {}

	/* (non-Javadoc)
	 * @see Menu#keyDown(int)
	 */
	//unused
	public void keyDown(int key) {
	}

	/* (non-Javadoc)
	 * @see Menu#keyUp(int)
	 */
	//unused
	public void keyUp(int key) {
	}



	
	

}

