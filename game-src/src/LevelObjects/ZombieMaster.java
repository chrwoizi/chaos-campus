package LevelObjects;

import Block;
import Level;
import Position;
import Error;

import javax.microedition.lcdui.game.*;
import javax.microedition.lcdui.*;

/**
 * Class represents an enemy (ZombieMaster) with its graphics and properties
 * @author Sascha Lity, Malte Mauritz
 *
 */
public class ZombieMaster extends NPC {	
	// a shared
	
	protected Image enemyImage = null;

	protected static byte UPDATE_FREQUENCY = 3;
	
	/*
	 * Zombie spezifische Eigenschaften
	 * Die Standardwerte für diese Eigenschaften
	 */
    private static byte STANDARD_SPEED = 2;
    private static byte STANDARD_LIFEPOINTS = 15;
    private static short STANDARD_ATTACKDAMAGE = 5;
    private static int STANDARD_CAPTURERADIUS = 5*5;
    
	private static short STANDARD_ATTACKDURATION = 3;
	private static int STANDARD_ATTACKTIME = 10;
    private static byte STANDARD_MOVETIME = 5;
		
	/**
	 * Constructor for the NPC class Zombie
	 * @param id
	 * @param position
	 * @param parentBlock
	 * @param disabled
	 * @param npcType
	 */
	public ZombieMaster(short id, Position position, Block parentBlock, boolean disabled, int npcType, byte itemWhenDying) {
		super(id, position, parentBlock, disabled, npcType, itemWhenDying);
		
		setEnabled(!disabled);
		
		this.setPosition(position);
		this.setCaptureRadius(STANDARD_CAPTURERADIUS); // set the capture radius of the zombie
		this.setMoveDirection(2);  //set the movedirection
		this.setSpeed(STANDARD_SPEED);   //set the speed for the movement
		this.setLifepoints(STANDARD_LIFEPOINTS); // set the lifepoint for the zombie
		this.setAttackDamage(STANDARD_ATTACKDAMAGE);// Set the damage of the NPC
		this.life = new LifeStatus(STANDARD_LIFEPOINTS);
		this.invStunPossibility = 4; //Sets  the possibilty to stun the enemy
		
		
	    this.setAttackTime(0); // Set the time to start an attack - StandardDauer 2
		
		// loads the image for the enemy
		try {
			// load container images if needed
			if( enemyImage == null ) {
				int check = randomize(10);
				if (check >= 0  && check < 5) {
					try {
						enemyImage = Image.createImage("/npc/npc" + npcType + ".png");
					} catch ( Exception e ) {
						new Error("zombie img 1", e);
					}
				} else {
					try {
						enemyImage = Image.createImage("/npc/npc" + npcType + "a.png");
					} catch ( Exception e ) {
						new Error("zombie img 2", e);
					}
				}
			}
			
			sprite = new Sprite(enemyImage, 16, 24);
			sprite.defineCollisionRectangle(3, 15, 12, 10);
			
			updateFrequency = UPDATE_FREQUENCY;
			
			sprite.defineReferencePixel(8, 24);
		}
		catch (Exception e){
			new Error("zombie master sprite init", e);
		}
		
		// update sprite position
		onPixelPositionChanged();		
	}
	/**
	 * Returns the standard move time
	 */
	public byte getStandardMoveTime() {
		return  STANDARD_MOVETIME;
	}
	/**
	 * Returns the standard attack duration
	 */
	public short getStandardAttackDuration() {
		return STANDARD_ATTACKDURATION;
	}
	/**
	 * Returns the standard attack time
	 */
    public int getStandardAttackTime() {
    	return STANDARD_ATTACKTIME;
    }
	/**
	 * Returns the UpdateFrequency
	 */
    public byte getUpdateFrequency() {
    	return UPDATE_FREQUENCY;
    }
	
    
	/**
	 * The Updates performed by every run of the main loop
	 */
	public void onUpdate() {
	 this.sct.update(); //updates the combat scroll text
	 if (this.getLifePoints()>0) { // tests if the enemy is alive
	  if (this.getStunDuration()<=0) { // tests if the enemy is not stunned 
		  if (this.getUnmoveableFrames()<=0){ // tests if the enemy is not frozen
			if (capture()){ // if the enemy detects the player
				this.life.setEnabled(true); // Activates the lifestatus		
				if (inDistanceToPlayer()){
					attack();
				}
				else{
					
					pursuit();
				}
			}
			else{
				moveRandom();
			}
		  }
		  else { // if frozen the enemy can still attack but not move
			if (capture()){
				this.life.setEnabled(true); // Activates the lifestatus
				if (inDistanceToPlayer()){
					attack();
				}
			}
	       this.decreaseUnmoveableFrames((int)UPDATE_FREQUENCY); // decrease the time of frozen
		  }
	  }
	  else {
		  this.testStunEnding(getStunDuration()); // initiate the ending of the strong stun
		  this.decreaseStunDuration(UPDATE_FREQUENCY); // decrease the time of stun

	  }
	 }
	}
	
	/**
	 * Choeses the right Framesquence  
	 * @param type The type of the Animation
	 * @return int[] -  the sequence of  frames
	 */
	protected int[] getFrameSequence(byte type) {
        sprite.setTransform(Sprite.TRANS_NONE); 
        if( type >= ANIM_UP && type <= ANIM_LEFT ) { // Movement
			switch(type) { 
				case ANIM_LEFT: sprite.setTransform(Sprite.TRANS_MIRROR); 
				case ANIM_RIGHT: return new int[] {3,4,5};
				case ANIM_UP: return new int[] {6,7,8};
				case ANIM_DOWN: return new int[] {0,1,2};
			}
		}
		else if( type == ANIM_ATTACK ) { //ATTACK
			switch(getPlayerDirection()) { // the direction of the animation
				case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
				case Level.dirRight: return new int[] {12,13,14};
				case Level.dirUp: return new int[] {15,16,17};
				case Level.dirDown: return new int[] {9,10,11};
			}
		}
		else if( type == ANIM_END_DAZED) { //strong STUN ending
			switch(getPlayerDirection()) {
				case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
				case Level.dirRight: return new int[] {14,13,12};
				case Level.dirUp: return new int[] {17,16,15};
				case Level.dirDown: return new int[] {11,10,9};
			}
		}
		else if( type == ANIM_RECEIVE ) { // STUN
			switch(getPlayerDirection()) {
				case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
				case Level.dirRight: return new int[] {30,31,32};
				case Level.dirUp: return new int[] {33,34,35};
				case Level.dirDown: return new int[] {27,28,29};
			}
		}		
		else if(type == ANIM_DAZED) { // strong STUN
				switch(getPlayerDirection()) {
				case Level.dirLeft: sprite.setTransform(Sprite.TRANS_MIRROR); 
				case Level.dirRight: return new int[] {21,22,23};
				case Level.dirUp: return new int[] {24,25,26};
				case Level.dirDown: return new int[] {18,19,20};
			}
		}	
		else if( type == ANIM_DIE ) {
			return new int[] {36,37,38};
		}		
		else if( type == ANIM_DEAD ) {
			return new int[] {38};
		}		
		else if( type == ANIM_IDLE ) {
			return new int[] {0};
		}
		return null;
	}
	
	/**
	 * The lenght of the Frame
	 * @Return the Update frequency
	 */
	protected byte getFrameDuration(byte animation, byte frame) {
		return UPDATE_FREQUENCY;
	}
	
	/**
	 * Rendering 
	 * @param g the Graphic handler
	 */
	public void onRender(Graphics g) {
		super.onRender(g);
		this.sct.render(g);
		this.life.render(g, this.pixelPositionX,this.pixelPositionY);
		//g.drawRect(this.pixelPositionX-2,this.pixelPositionY-2, 4, 4);
	}
	
	/*
	 * Autmatisierter Test der Klasse ZombieMaster
	 */
	public String imageTest(ZombieMaster enemy,int npctype) {
		try {
		   if (enemy.enemyImage == null) return "Image should be npc"+npctype+".png but is "+enemy.enemyImage;
		   else return "Image Test - Success";
		}
		catch(Exception e) {
			return("Exception in imagetest: "+e.toString());
		}
	}
}
