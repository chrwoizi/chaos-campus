/**
 * 
 */
package LevelObjects;



import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

import Error;
import Block;
import Level;
import Player;
import Position;
import Story.StoryManager;
import ScrollingCombatText;

/**
 * The abstract class NPC represent every NPC in the game with its methods
 * @author Sascha Lity, Malte Mauritz
 *
 */
public abstract class NPC extends DynamicLevelObject {

	private static Image frozenImage = null;
	
	private static final short FROZEN_TIME = 60;
	
	protected int npcType;
	private byte speed = 2; // StandardSpeed 2
	private int captureRadius; // The Radius in which the enemey detects the player
	private int lifepoints; // The amount of Lifepoints an enemy currently has
	public int getLifePoints() { return lifepoints; }
	protected Random random = new Random();
	private int movedirection; //Direction if the current movement
	private int attacktime = 0;  // StandardDauer 2
	private short attackDamage; // StandardDauer 5
	private boolean moveflag = true; //Sets if the NPC is albe to move free 
	private byte movetime = 0; //thetime of a single moveelement - curr = 4
	private byte itemWhenDying;
	private int unmoveableFramesleft = 0; //in sec 
	private int distanceToPlayer = 18;// The range the enemy has to be to the player to attack him
	private boolean isParalyzed = false;  // sets if the enemy is paralyzed - used to start the ending of paralyzed
	
	public short invStunPossibility = 1; // inverted stun possibility. possibility = 1/invStunPossibility 

	protected LifeStatus life; // The Lifestatus which shows the life
	protected ScrollingCombatText sct; // The Text showing hits or damage

	
	// Attribute für das Kampfsystem
	private short attackDuration = 3; 		//Zeit für einen Angriff
	private int stunDuration;			//Zeit der Betäubung nach einem Treffer in sec


	// this npc is damageable in this rectangle, use 0 for default!
	protected short damageableZoneLeft = 0;
	protected short damageableZoneRight = 0;
	protected short damageableZoneTop = 0;
	protected short damageableZoneBottom = 0;

	private static Image getFrozenImage() {
		if( frozenImage == null ) {
			Image tempImage;
			try {
				tempImage = Image.createImage("/Specials.png");
				frozenImage = Image.createImage(tempImage, 
						32, 0, 16, 16, Sprite.TRANS_NONE);
			} catch (IOException e) {
				new Error("getFrozenImage() failed: loading Specials.png", e);
			}
		}
		return frozenImage;
	}
	
	/**
	 * @param id
	 * @param position
	 * @param parentBlock
	 * @param disabled
	 * @param npctype
	 * @param itemWhendying
	 */
	public NPC(short id, Position position, Block parentBlock, boolean disabled,
				int npcType, byte itemWhenDying) 
	{
		super(id, position, parentBlock);
		random = new Random(id);
		this.npcType=npcType;
		this.itemWhenDying = itemWhenDying;
		this.sct = new ScrollingCombatText();
	}
	
	public int getPosX() {
		return this.pixelPositionX;
	}

	public int getPosY() {
		return this.pixelPositionY;
	}
	/**
	 * States if the player is in the distance to the player
	 */
	protected boolean inDistanceToPlayer() {
		if (Player.getInstance().getPosition().maxPixelDistanceTo(this.getPosition()) <= distanceToPlayer) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Set the movetime for continuos Movement of the NPC in FRAMES
	 * @param mt The Frames the NPC moves in the same direction
	 */
	public void setMoveTime(byte mt) {
		 this.movetime =	mt;
	}
	
	/**
	 * Returns the movetime for continuos Movement of the NPC 
	 * 
	 */
	public byte getMoveTime() {
		 return this.movetime;	
	}
	/**
	 * Set the attack-time of the NPC
	 * @param time The time the NPC has to wait to start another attack
	 */
	public void setAttackTime(int time) {
		this.attacktime = time;
	}
	/**
	 * Returns the attack-time of the NPC
	 */
	public int getAttackTime() {
		return this.attacktime;
	}
	/**
	 * Sets the attack-damage of the NPC
	 * @param damage The amount of damage the NPC does
	 */
	public void setAttackDamage(short damage) {
		this.attackDamage = damage;
	}
	/**
	 * Sets the capture radius for the method captur()
	 * @param radius The radius in which the NPC sees an enemy
	 */
	public void setCaptureRadius(int radius){
		this.captureRadius = radius;
	}
	/**
	 *Sets the movedirection for the method attack() in the lower classes
	 * @param movedirection The direction in which the NPC moces 
	 */
	public void setMoveDirection(int movedirection){
		this.movedirection = movedirection;
	}
	
	/** 
	 * Returns the movedirection 
	 * @return current moverdirection
	 */	
	public int getMoveDirection(){
		return movedirection;
	}
	
	/**
	 * Sets the speed for the movement 
	 * @param speed  The speed the NPC moves
	 */
	public void setSpeed(byte speed) {
	 this.speed = speed;
	}
	
	/**
	 *Returns the speed for the movement
	 *@return the speed of the NPC
	 */
	public byte getSpeed() {
	 return this.speed;
	}
	
	/**
	 * Sets the Lifepoints
	 * @param life the amount of Lifepoints
	 */

	public void setLifepoints(int life) {
		this.lifepoints = life;
	}
	
	/**
	 * Return the Lifepoints
	 * @return the current amount of lifepoints
	 */
	public int getLifepoints() {
		return this.lifepoints;
	}
	
	/**
	 * Returns the time the enemy is Frozen
	 * @return the time of frozen
	 */
	public int getUnmoveableFrames(){
		return this.unmoveableFramesleft;
	}
	
	/**
	 * Return the Lifepoints
	 * @return the current amount of lifepoints
	 */
	public void setUnmoveableFrames(int value){
		this.unmoveableFramesleft= value;
	}
	/**
	 * Decreases the frozen time
	 * @param value the amount the time shall decreased
	 */
	public void decreaseUnmoveableFrames(int value){
		this.unmoveableFramesleft-=value;
	}
	
	/**
	 * Sets the time of an attack
	 * @param value the time an attack takes
	 */
	public void setAttackDuration(short value) {
		this.attackDuration = value;
	}
	
	/**
	 * Return the time the attack still takes
	 * @return the current time
	 */
	public short getAttackDuration() {
		return this.attackDuration;
	}
	
	/**
	 * Sets the time of the stun
	 * @param value the time the stun shall take
	 */
	public void setStunDuration(int value) {
		this.stunDuration = value;
	}
	
	/**
	 * Return the time of the stun
	 * @return  the current time of the stun
	 */
	public int getStunDuration() {
		return this.stunDuration;
	}
	
	/**
	 * Decrease the time of the stun
	 * @param value the time the stun shall decreased
	 */
	public void decreaseStunDuration(int value) {
		this.stunDuration-=value;
	}
	
	
	public int abs(int b) {
		return b < 0 ? -b : b;
	}
	
	/**
	 * Gets the direction of the player relativ to this NPC
	 * @return
	 */
	protected byte getPlayerDirection() {
		int deltax = Player.getInstance().getPosition().getPixelX() - this.getPosition().getPixelX();
		int deltay = Player.getInstance().getPosition().getPixelY() - this.getPosition().getPixelY();
		
		byte result = 0;
		
		if(abs(deltax) > abs(deltay)) {
			result = deltax < 0 ? Level.dirLeft : Level.dirRight;
		}
		else {
			result = deltay < 0 ? Level.dirUp : Level.dirDown;
		}
		
		return result;
	}
	
	/**
	 * Gets the distance of the player relativ to this NPC
	 * @return
	 */
	protected short getPlayerDistance() {
		int deltax = Player.getInstance().getPosition().getPixelX() - this.getPosition().getPixelX();
		int deltay = Player.getInstance().getPosition().getPixelY() - this.getPosition().getPixelY();
		short result = 0;
		
		if(abs(deltax) > abs(deltay)) {
			result = (short)abs(deltax);
		}
		else {
			result = (short)abs(deltay);
		}
		
		return result;
	}
	
	/**
	 * Capture the Player if he is in the proximity of the Enemy
	 * @return Give a boolean back which shows if the Player is in the 
	 * 		   proximity of the Enemy
	 */
	public boolean capture(){
		int deltax = Player.getInstance().getPosition().fieldX - this.getPosition().fieldX;
		int deltay = Player.getInstance().getPosition().fieldY - this.getPosition().fieldY;
		if (captureRadius > ((deltax * deltax) + (deltay * deltay) )){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * The Enemy pursuits the Player
	 * Determinds which direction the enemy has to move to follow the player
	 */
	public void pursuit(){
		// Player links
		if ((Player.getInstance().getPosition().fieldX < this.getPosition().fieldX)
					&& (Player.getInstance().getPosition().fieldY == this.getPosition().fieldY)){
			move(Level.dirLeft, speed);	
			this.movedirection = 3;
		}
		//Player rechts
		else if ((Player.getInstance().getPosition().fieldX > this.getPosition().fieldX)
					&&	(Player.getInstance().getPosition().fieldY == this.getPosition().fieldY)){
			move(Level.dirRight, speed);
			this.movedirection = 1;
		}
		//Player oben
		else if ((Player.getInstance().getPosition().fieldY < this.getPosition().fieldY)
					&& (Player.getInstance().getPosition().fieldX == this.getPosition().fieldX)){
			move(Level.dirUp, speed);
			this.movedirection = 0;
		}
		//Player unten
		else if ((Player.getInstance().getPosition().fieldY > this.getPosition().fieldY)
					&& (Player.getInstance().getPosition().fieldX == this.getPosition().fieldX)){
			move(Level.dirDown, speed);
			this.movedirection = 2;
		}
		// Player diagonal linksoben
		else if ((Player.getInstance().getPosition().fieldX < this.getPosition().fieldX)
					&& (Player.getInstance().getPosition().fieldY < this.getPosition().fieldY)){
			move((byte)(Level.dirUp | Level.dirLeft), speed);
			this.movedirection = 0;
		}
		//Player diagonal rechtsoben
		else if ((Player.getInstance().getPosition().fieldX > this.getPosition().fieldX)
					&& (Player.getInstance().getPosition().fieldY < this.getPosition().fieldY)){
			move((byte)(Level.dirUp | Level.dirRight), speed);
			this.movedirection = 0;
		}
		// Player diagonal rechtsunten
		else if ((Player.getInstance().getPosition().fieldX > this.getPosition().fieldX)
					&& (Player.getInstance().getPosition().fieldY > this.getPosition().fieldY)){
			move((byte)(Level.dirDown | Level.dirRight), speed);
			this.movedirection = 2;
		}
		//Player linksunten
		else {
			move((byte)(Level.dirDown | Level.dirLeft), speed);
			this.movedirection = 2;
		}
	}
	
	/**
	 * Calculates the damage with an random value
	 * @return the random damage
	 */
	protected int calculateDamage() {
		return random.nextInt(this.attackDamage+1);
	}
	
    /**
	 * Return an random value
	 * @param value the upperend of the field to chose the random int from
	 * @return an random int
	 */
	protected int randomize(int value) {
		return random.nextInt(value);
	}
	
	/**
	 * Fills LevelObjectState with life status
	 */
	protected void fillState(LevelObjectState state) {
		//EnemyState estate = (EnemyState)state;
		//estate.isAlive = !isAlive;
		super.fillState(state);
		if( lifepoints > 0 )
			state.state |= LevelObjectState.ALIVE; 
	}
	
	/**1
	 * Loads properties from a LevelObjectState. 
	 */
	public void setState(LevelObjectState state) {
		//EnemyState estate = (EnemyState)state;
		
		// retrieve data from state
		if((state.state & LevelObjectState.ALIVE) == 0 ) {
			setLifepoints(0);
			sprite.setFrameSequence(getFrameSequence(ANIM_DEAD));
		}
		super.setState(state);
	}
	
	/**
	 * Returns true if the enemy is inside the passed region
	 * @param topLeft
	 * @param bottomRight
	 * @return
	 */
	public boolean isInsideRegion(Position topLeft, Position bottomRight) {
		// if damageable zone is not default
		if (damageableZoneLeft == 0 && damageableZoneRight == 0
				&& damageableZoneTop == 0 && damageableZoneBottom == 0) {
	
			// easy way
			return super.isInsideRegion(topLeft, bottomRight);
		}
		else {
			
			// hard way
			
			short[] pointsX = new short[4];
			short[] pointsY = new short[4];
	
			short tpx = getPosition().getPixelX();
			short tpy = getPosition().getPixelY();
	
			short tlx = topLeft.getPixelX();
			short tly = topLeft.getPixelY();
			short brx = bottomRight.getPixelX();
			short bry = bottomRight.getPixelY();
			
			pointsX[0] = (short)(tlx - tpx);
			pointsY[0] = (short)(tly - tpy);
	
			pointsX[1] = (short)(brx - tpx);
			pointsY[1] = (short)(tly - tpy);
	
			pointsX[2] = (short)(brx - tpx);
			pointsY[2] = (short)(bry - tpy);
			
			pointsX[3] = (short)(tlx - tpx);
			pointsY[3] = (short)(bry - tpy);
			
			/* TEST
			try{
				if(getClass() == Class.forName("LevelObjects.Glutamator")) {
					System.out.println("(" + pointsX[0] + "," + pointsY[0] + ")");
					System.out.println("(" + pointsX[1] + "," + pointsY[1] + ")");
					System.out.println("(" + pointsX[2] + "," + pointsY[2] + ")");
					System.out.println("(" + pointsX[3] + "," + pointsY[3] + ")");
				}
			}catch(Exception e) {}*/
			
			// check if one point is in
			for(byte i = 0; i < 4; i++) {
				if((pointsX[i] >= damageableZoneLeft)
				&& (pointsX[i] <= damageableZoneRight)
				&& (pointsY[i] >= damageableZoneTop)
				&& (pointsY[i] <= damageableZoneBottom)) return true;
			}
			
			// check if 2 points are between horizontal bounds and outside vertical bounds	
			if(
				(  
					   (pointsX[0] >= damageableZoneLeft)
					&& (pointsY[0] <= damageableZoneTop)
					&& (pointsY[0] >= damageableZoneBottom)
				)
				&&
				(  
					   (pointsX[1] >= damageableZoneRight)
					&& (pointsY[1] <= damageableZoneTop)
					&& (pointsY[1] >= damageableZoneBottom)
				)
			) return true;
			if(
				(  
					   (pointsX[2] >= damageableZoneLeft)
					&& (pointsY[2] <= damageableZoneTop)
					&& (pointsY[2] >= damageableZoneBottom)
				)
				&&
				(  
					   (pointsX[3] >= damageableZoneRight)
					&& (pointsY[3] <= damageableZoneTop)
					&& (pointsY[3] >= damageableZoneBottom)
				)
			) return true;
			
			// check if 2 points are between vertical bounds and outside horizontal bounds	
			if(
				(  
					   (pointsX[0] >= damageableZoneTop)
					&& (pointsY[0] >= damageableZoneLeft)
					&& (pointsY[0] <= damageableZoneRight)
				)
				&&
				(  
					   (pointsX[2] <= damageableZoneRight)
					&& (pointsY[2] >= damageableZoneLeft)
					&& (pointsY[2] <= damageableZoneRight)
				)
			) return true;	
			if(
				(  
					   (pointsX[1] >= damageableZoneTop)
					&& (pointsY[1] >= damageableZoneLeft)
					&& (pointsY[1] <= damageableZoneRight)
				)
				&&
				(  
					   (pointsX[3] <= damageableZoneRight)
					&& (pointsY[3] >= damageableZoneLeft)
					&& (pointsY[3] <= damageableZoneRight)
				)
			) return true;
	
			// check if the damageable zone is completely inside the rect
			for(byte i = 0; i < 4; i++) {
				if((pointsX[i] <= damageableZoneLeft)
				&& (pointsX[i] >= damageableZoneRight)
				&& (pointsY[i] <= damageableZoneTop)
				&& (pointsY[i] >= damageableZoneBottom)) return true;
			}
			
			return false;
		}
	}
	
	/**
	 * Handles the damage the enemy receives
	 * @return the current amount of lifepoints
	 */
	private void receiveDamage(byte damage) {
		lifepoints -= damage;
		 if (damage>0) { // Shows the CombatText
			    ScrollingCombatText.getInstance().addText(this.pixelPositionX, this.pixelPositionY - 30, 0 , -1 ,15, "-" + damage, 0,0,255);
			 }
		if(lifepoints <= 0) { //Performs the death of the enemy
			lifepoints = 0;
			setInstantAnimation(ANIM_DIE);
			setNextAnimation(ANIM_DEAD);
			this.life.setEnabled(false); //Disables the lifestatus
			this.unmoveableFramesleft = 0;
			StoryManager.getInstance().onEnemyKilled(getId(), (byte)npcType);
			if(itemWhenDying > 0) // drops the item when killed
				Item.spawnItem(this.getPosition(), false, itemWhenDying);
		}
	}
	
	/**
	 * Handle an Attack
	 * @param damage Damage for the Enemy
	 */
	public void receiveAttack(byte damage, byte stun, byte specialId){
	  if (this.lifepoints>0) {
		 this.handleStun(stun, specialId);
		 if (specialId>0) {
				receiveSpecialAttack(specialId, stun);
		     }
		 this.receiveDamage(damage);
		 this.life.onLifechanged(damage); //Updates the Lifestatus
		 this.setAttackTime(this.getStandardAttackTime()); // Resets to the Standard value to perform the interrupt in the fight system
		 this.attackDuration = this.getStandardAttackDuration(); // Resets to the Standard value to perform the interrupt in the fight system
      }

	}
	
	/**
	 * Handle the effects of a special attack
	 * @param id The ID of the special attack
	 * @param stun The time of stun
	 */
	private void receiveSpecialAttack(byte id, int stun){
		switch(id) {
		 //Specialattack: Halten
		 case(3): this.unmoveableFramesleft=FROZEN_TIME; break;
		 //Specialattack: Speed - handled by player	
		 case(1): break;
		 //Specialattack: Mirror Image - handled by player	
		 case(2): break;
		 //Specialattack: Shatterer - Damage by receiveDamage()
		 case(4): this.stunDuration = stun; setInstantAnimation(ANIM_DAZED); 
		          isParalyzed = true;
		          break;
		 //Specialattack: Skullhummer - handled by player	
		 case(5): this.stunDuration = stun; setInstantAnimation(ANIM_DAZED);
		          isParalyzed = true;
		          break;
		 //Specialattack: Earthquake - handled by player	
		 case(6): this.stunDuration = stun; setInstantAnimation(ANIM_DAZED);
		          isParalyzed = true;
		          break;
		 //Specialattack: Drink All - handled by player	
		 case(7): break;
		 //Specialattack: Wilde Strokes
	     case(8): this.stunDuration = stun; setInstantAnimation(ANIM_DAZED);
	              isParalyzed = true;
	              break;
	     //Specialattack: critical Strike	
	     case(9): this.stunDuration = stun; setInstantAnimation(ANIM_DAZED); 
	              isParalyzed = true;
	              break;
		}	
	}
	/**
	 * Handle the effects of a special attack
	 * @param id The ID of the special attack
	 */
	private void handleStun(byte stun, byte id) {
		int r = randomize(invStunPossibility); // determinds if the stun is successful
		if(r == 0) {
			this.stunDuration = stun;
			//ScrollingCombatText.getInstance().addText(this.pixelPositionX, this.pixelPositionY - 30, 0 , -1 ,15, "STUNNED", 0,0,255);
		    setInstantAnimation(ANIM_RECEIVE);
		}
	}
	
	/**
	 * Random Movement of the Enemy 
	 */
	public void moveRandom(){
		this.life.setEnabled(false);
		if (moveflag) {
			if (this.movetime<=0){
				int direction = random.nextInt(9);
				if ((direction == Level.dirNone)||(direction == 3)||(direction == 5)||(direction == 6)
						||(direction == 7)){
					// do not move
				}
				else if (direction == Level.dirUp){
					move(Level.dirUp, speed);
					setMoveTime(this.getStandardMoveTime());
					setMoveDirection(Level.dirUp);
					
				}
				else if (direction == Level.dirRight){
					move(Level.dirRight, speed);
					setMoveTime(this.getStandardMoveTime());
					setMoveDirection(Level.dirRight);
				}
				else if (direction == Level.dirDown){
					move(Level.dirDown, speed);
					setMoveTime(this.getStandardMoveTime());
					setMoveDirection(Level.dirDown);
				}
				else if (direction == Level.dirLeft){
					move(Level.dirLeft, speed);
					setMoveTime(this.getStandardMoveTime());
					setMoveDirection(Level.dirLeft);
				}
			}
			else {
				move((byte)getMoveDirection(), this.speed);
				this.movetime--;
			}
		}

	}
	
	/**
	 * The Enemy attacks the Player
	 *
	 */
	public void attack(){
		if (Player.getInstance().getAlcLevel() > 0){ //the player is alive		
			if (attacktime <= 0){ //can the enemy start an attack
				if (this.testAnimation(ANIM_ATTACK)==false) {
				 setInstantAnimation(ANIM_ATTACK);
				}
				if (attackDuration <= 0) { //how long the attack last
					Player.getInstance().receiveDamage((byte)this.attackDamage);
					//this.sct.addText(this.pixelPositionX, this.pixelPositionY - 30, 0,-1, 7, "Hit " +damage, 0,0,255);
					this.attacktime = this.getStandardAttackTime(); // reseted to start a new attack
					attackDuration = this.getStandardAttackDuration(); // reseted to start a new attack
				}
				else {
					attackDuration-=this.getUpdateFrequency(); // decrease the duration of the attack
				}
			}
			else{
				attacktime-=this.getUpdateFrequency(); // decrease the time to wait till start an attack
			}
		}
		else{
				moveRandom(); // moves when the player is dead
		}
	}
	
	/**
	 * Determinds if the enemy collides with another object
	 *
	 */
	public boolean collidesWith(Sprite otherObject) {
		if( sprite == null || otherObject == null || sprite == otherObject )
			return false;
		else if (this.getLifePoints() <= 0){
			return false;
		}
		return sprite.collidesWith(otherObject, false);
	}
    
	/**
	 * Rendering
	 *
	 */
	public void onRender(Graphics g) {
		super.onRender(g);
		if( this.unmoveableFramesleft > 0 ) {
			g.drawImage(getFrozenImage(), 
					this.pixelPositionX, this.pixelPositionY,
					Graphics.VCENTER|Graphics.HCENTER);
		}
	}
	/**
	 * Sets the inverdet stun when the ending is reached 
	 * @param duration the current time
	 */
	protected void testStunEnding(int duration) {
		if (duration<4 && testAnimation(ANIM_END_DAZED)== false && isParalyzed) {	
		  setInstantAnimation(ANIM_END_DAZED);
		}
		
	}
	//Implemented in the lower classes
	public abstract void onUpdate();
	public abstract byte getStandardMoveTime();
	public abstract short getStandardAttackDuration();
	public abstract int getStandardAttackTime();	
	public abstract byte getUpdateFrequency();

}
