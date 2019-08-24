package LevelObjects;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import Player;
import Error;

/**
 * Class of the Lifestatus for all enemys
 * @author Malte Mauritz
 *
 */       
public class LifeStatus{
	private final String sourceBG= "/npc/lifestatus_bg.png";
	private final String sourceLife= "/npc/lifestatus.png";

	private Image background;
	private Image lifeSource;
	
	private boolean isEnabled = false;
	private int max_life=0;
	private int cur_life=0;
	private int drawWidth=0;
	
   /**
    * Constructor
    *
    */
	public LifeStatus(short max) {
		try {
			background = Image.createImage(sourceBG);
		} catch (Exception e) {
			new Error("LifeStatus background img", e);
		}
		try {
			lifeSource = Image.createImage(sourceLife);
		} catch (Exception e) {
			new Error("LifeStatus lifeSource img", e);
		}
		this.max_life = max;
		this.drawWidth = lifeSource.getWidth();
		this.cur_life = this.max_life;
	}
	
	public void setEnabled(boolean value) {
		this.isEnabled = value;
	}
	//Lifepoints übergeben und nicht hier nochma differenz berechene?
	public void onLifechanged(byte lifechange) {
		this.cur_life = this.cur_life - lifechange;
		if (cur_life<=0) {
			this.isEnabled=false;
		}
		else {
		 this.drawWidth = (int) Math.ceil(lifeSource.getWidth() * ((double)this.cur_life/(double)this.max_life));
		} 
	}
	
	public void render(Graphics g,int posX, int posY) {
		if (isEnabled == true) {
			if  (Player.getInstance().getAlcLevel() > 0) {
		      g.drawImage(background,posX-(background.getWidth()/2),posY - 25, Graphics.TOP | Graphics.LEFT);
		      g.drawRegion(lifeSource, 0,0,this.drawWidth,lifeSource.getHeight(),
				Sprite.TRANS_NONE,posX-(lifeSource.getWidth()/2),posY -24,  Graphics.TOP | Graphics.LEFT);
		    }
		    else {
		    	this.isEnabled=false;
		    }
		}
	}
	
	
	
}