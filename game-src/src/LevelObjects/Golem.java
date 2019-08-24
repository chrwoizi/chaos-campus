/**
 * 
 */
package LevelObjects;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import Block;
import Level;
import Player;
import Position;
import Error;

/**
 * Class represents an enemy(Golem) with its graphics and properties
 * @author Sascha Lity, Malte Mauritz
 *
 */
public class Golem extends NPC {

	protected static Image enemyImage = null;

	protected static byte UPDATE_FREQUENCY = 2;
	
	/*
	 * Skeleton spezifische Eigenschaften
	 */
    protected static byte STANDARD_SPEED = 1;
    protected static short STANDARD_LIFEPOINTS = 100;
    protected static short STANDARD_ATTACKDAMAGE = 8;
    private static int STANDARD_CAPTURERADIUS = 4*4;
    
	protected static short STANDARD_ATTACKDURATION = 6;
	protected static int STANDARD_ATTACKTIME = 25;
    protected static byte STANDARD_MOVETIME = 4;
    
    private byte attacktime = 0;
	
	/**
	 * Constructor for the NPC class Skeleton
	 * @param id
	 * @param position
	 * @param parentBlock
	 * @param disabled
	 * @param npcType
	 */
	public Golem(short id, Position position, Block parentBlock, boolean disabled, int npcType, byte itemWhenDying) {
		super(id, position, parentBlock, disabled, npcType, itemWhenDying);
		
		setEnabled(!disabled);
		
		this.setPosition(position);
		this.setCaptureRadius(STANDARD_CAPTURERADIUS); // set the capture radius of the zombie
		this.setMoveDirection(2);  //set the movedirection for attack
		this.setSpeed(STANDARD_SPEED);   //set the speed for the movement
		this.setLifepoints(STANDARD_LIFEPOINTS); // set the lifepoint for the zombie
		this.setAttackDamage(STANDARD_ATTACKDAMAGE);// Set the damage of the NPC
		this.life = new LifeStatus(STANDARD_LIFEPOINTS);
		invStunPossibility = 10;
		
		
	    this.setAttackTime(0); // Set the time to start an attack - StandardDauer 2
		
		
		try {
			// load container images if needed
			if( enemyImage == null ) {
				try {
					enemyImage = Image.createImage("/npc/npc" + npcType + ".png");
				} catch ( Exception e ) {
					new Error("skeleton img", e);
				}
			}
			
			sprite = new Sprite(enemyImage, 32, 40);
			sprite.defineCollisionRectangle(2, 28, 25, 10);
			
			
			updateFrequency = UPDATE_FREQUENCY;
			
			sprite.defineReferencePixel(16, 36);
		}
		catch (Exception e){
			new Error("skeleton sprite init", e);
		}
		
		// update sprite position
		onPixelPositionChanged();		
	}

	public byte getStandardMoveTime() {
		return  STANDARD_MOVETIME;
	}
	
	public short getStandardAttackDuration() {
		return STANDARD_ATTACKDURATION;
	}
	
    public int getStandardAttackTime() {
    	return STANDARD_ATTACKTIME;
    }
    
    public byte getUpdateFrequency() {
    	return UPDATE_FREQUENCY;
    }
		
	public void onUpdate() {
	 this.sct.update(); 
	 if (this.getLifePoints()>0) {
	  if (this.getStunDuration()<=0) {
		  if (this.getUnmoveableFrames()<=0){
			if (capture()){
				this.life.setEnabled(true);				
				if (inDistanceToPlayer()) {
					if (Player.getInstance().getAlcLevel() > 0){		
						if (attacktime <= 0){
							attacktime = (byte)STANDARD_ATTACKTIME;
							setInstantAnimation(ANIM_ATTACK);
						}
						else attacktime--;
					}
				}
				else{
					
					pursuit();
				}
			}
			else{
				//move(Level.dirNone, (byte)0);
				moveRandom();
			}
		  }
		  else {
			if (capture()){
				this.life.setEnabled(true);
				if (inDistanceToPlayer()){
					if (Player.getInstance().getAlcLevel() > 0){		
						if (attacktime <= 0){
							attacktime = (byte)STANDARD_ATTACKTIME;
							setInstantAnimation(ANIM_ATTACK);
						}
						else attacktime--;
					}
				}
			}
	       this.decreaseUnmoveableFrames((int)UPDATE_FREQUENCY);
		  }
	  }
	  else {
		  this.decreaseStunDuration(UPDATE_FREQUENCY);

	  }
	 }
	}
	

	protected int[] getFrameSequence(byte type) {
        sprite.setTransform(Sprite.TRANS_NONE); 

        if( type >= ANIM_UP && type <= ANIM_LEFT ) {
			switch(type) {
				case ANIM_LEFT: sprite.setTransform(Sprite.TRANS_MIRROR); 
				case ANIM_RIGHT: return new int[] {3,4,5};
				case ANIM_UP: return new int[] {0,1,2};
				case ANIM_DOWN: return new int[] {6,7,8};
				
			}
		}
		else if( type == ANIM_ATTACK ) {
			switch(getPlayerDirection()) {
			case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
			case Level.dirRight: return new int[] {18,19,20};
			case Level.dirUp: return new int[] {24,25,26};
			case Level.dirDown: return new int[] {21,22,23};
			}
		}
		else if( type == ANIM_RECEIVE ) {
			switch(getPlayerDirection()) {
			case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
			case Level.dirRight: return new int[] {16};
			case Level.dirUp: return new int[] {15};
			case Level.dirDown: return new int[] {17};
			}
		}
		else if(type == ANIM_DAZED ) {
			switch(getPlayerDirection()) {
			case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
			case Level.dirRight: return new int[] {16};
			case Level.dirUp: return new int[] {15};
			}
		}	
		else if( type == ANIM_DIE) {
			switch(getPlayerDirection()) {
			case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
			case Level.dirRight: return new int[] {9,10,11,27};
			case Level.dirUp: 
			case Level.dirDown: return new int[] {12,13,14,28};
			}
		}		
		else if( type == ANIM_DEAD ) {
			switch(getPlayerDirection()) {
			case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
			case Level.dirRight: return new int[] {27};
			case Level.dirUp: 
			case Level.dirDown: return new int[] {28};
			}
		}		
		else if( type == ANIM_IDLE ) {
			switch(getPlayerDirection()) {
			case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
			case Level.dirRight: return new int[] {4};
			case Level.dirUp: return new int[] {2};
			case Level.dirDown: return new int[] {7};
			}
		}
		return null;
	}
	
	protected byte getFrameDuration(byte animation, byte frame) {
		return (byte)(2*UPDATE_FREQUENCY);
	}
	
	protected void onAnimationFinished(byte animation) {
		if(animation == DynamicLevelObject.ANIM_ATTACK) {
			// inflict damage to player if attack is over
			Player.getInstance().receiveDamage((byte)STANDARD_ATTACKDAMAGE);
		}
	}
	
	public void onRender(Graphics g) {
		super.onRender(g);
		this.sct.render(g);
		this.life.render(g, this.pixelPositionX,this.pixelPositionY-14);
		//g.drawRect(this.pixelPositionX-2,this.pixelPositionY-2, 4, 4);
	}
}
