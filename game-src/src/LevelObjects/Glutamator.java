package LevelObjects;

import Block;
import Error;
import Level;
import Player;
import Position;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class Glutamator extends NPC {

	protected static Image image = null;
	
	protected static byte UPDATE_FREQUENCY = 2;

	protected static final byte ANIMATION_FRAME_DURATION = 5;
	
	protected static final short STANDARD_ATTACKDURATION = 4;
	protected static final int STANDARD_ATTACKTIME = 1;
    protected static final byte STANDARD_MOVETIME = 2;
    protected static final byte STANDARD_SPEED = 2;
    protected static final short STANDARD_LIFEPOINTS = 150;
    protected static final short STANDARD_ATTACKDAMAGE = 5;
    
    // true if attacking with melee attack
    private boolean melee = false;
    
    // true if currently attacking
    private boolean attacking = false;
    
    // true if cannot attack
    private boolean stunned = false;
    
    // max distance to player for melee attack in square fields
    private static final byte MELEE_DISTANCE = 9;

    // max pixel offset to vertical or horizontal line with player for starting a vomit attack
    private static final byte MAX_VOMIT_OFFSET = 8;
    
    // max distance to player for vomit attack in square fields
    private static final byte VOMIT_DISTANCE = 64;
    
    private static final byte VOMIT_FREQUENCY = 30;
    private byte previousVomit = 0;
    
    private static final byte MELEE_FREQUENCY = 25;
    private byte previousMelee = 0;

    private static final byte BROCCOLI_SPEED = 6;
    private static final short MAX_BROCCOLI_DISTANCE = (short)(5*16);
    private Position broccoliStart;
    private byte broccoliDir;
    private short spitLength;
	
	public Glutamator(short id, Position position, Block parentBlock, boolean disabled, int npcType, byte itemWhenDying) {
		super(id, position, parentBlock, disabled, npcType, itemWhenDying);
		
		setEnabled(!disabled);
		
		setPosition(position);
		setCaptureRadius(0);
		setMoveDirection(Level.dirNone);
		setSpeed(STANDARD_SPEED);   //set the speed for the movement
		setLifepoints(STANDARD_LIFEPOINTS); // set the lifepoint for the glutamator
		setAttackDamage(STANDARD_ATTACKDAMAGE);// Set the damage of the NPC
		this.life = new LifeStatus(STANDARD_LIFEPOINTS);
		invStunPossibility = 4;
		
		
	    setAttackTime(0); // Set the time to start an attack - StandardDauer 2
		
		
		try {
		// load container images if needed
			if( image == null ) {
				try {
					image = Image.createImage("/npc/npc" + npcType + ".png");
				} catch ( Exception e ) {
					new Error("Glutamator img", e);
				}
			}
			
			sprite = new Sprite(image, 48, 48);
			sprite.setFrame(1);
			sprite.defineCollisionRectangle(8, 26, 36, 10);

			damageableZoneLeft = -24;
			damageableZoneRight = 24;
			damageableZoneTop = -24;
			damageableZoneBottom = 8;
			
			
			updateFrequency = 1;
			
			sprite.defineReferencePixel(24, 40);
		}
		catch (Exception e){
			new Error("Glutamator sprite init", e);
		}
		
		// update sprite position
		onPixelPositionChanged();
	}

	public void onRender(Graphics g) {
		this.sct.render(g);
		this.life.render(g, this.pixelPositionX,this.pixelPositionY-5);
		super.onRender(g);
	}

	public boolean collidesWith(Sprite otherObject) {
		if( sprite == null || otherObject == null || sprite == otherObject )
			return false;
		else if (getLifePoints() <= 0){
			return false;
		}
		return sprite.collidesWith(otherObject, false);
	}
	
	private void attackMelee() {
		melee = true;
		attacking = true;
		setInstantAnimation(DynamicLevelObject.ANIM_ATTACK);
		setNextAnimation(DynamicLevelObject.ANIM_IDLE);
	}
	
	private void attackVomit() {
		// launch-parameters
		broccoliDir = getPlayerDirection();
		broccoliStart = new Position(getPosition());
		switch(broccoliDir) {
		case Level.dirLeft:
			broccoliStart.movePixels((byte)-20, (byte)-12);
			break;
		case Level.dirRight:
			broccoliStart.movePixels((byte)20, (byte)-12);
			break;
		case Level.dirUp:
			broccoliStart.movePixels((byte)0, (byte)-24);
			break;
		case Level.dirDown:
			broccoliStart.movePixels((byte)0, (byte)0);
			break;
		}
		
		spitLength = getPlayerDistance();
		
		// state
		melee = false;
		attacking = true;
		setInstantAnimation(DynamicLevelObject.ANIM_ATTACK);
		setNextAnimation(DynamicLevelObject.ANIM_IDLE);
	}
	
	private boolean nearPlayerForMelee() {
		this.life.setEnabled(true);
		int deltax = abs(Player.getInstance().getPosition().fieldX - this.getPosition().fieldX);
		int deltay = abs(Player.getInstance().getPosition().fieldY - this.getPosition().fieldY);
		if (MELEE_DISTANCE > ((deltax * deltax) + (deltay * deltay) )){
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean nearPlayerForVomit() {
		this.life.setEnabled(true);
		int deltax = abs(Player.getInstance().getPosition().fieldX - this.getPosition().fieldX);
		int deltay = abs(Player.getInstance().getPosition().fieldY - this.getPosition().fieldY);
		if (VOMIT_DISTANCE > ((deltax * deltax) + (deltay * deltay) )){
			return true;
		}
		else {
			return false;
		}
	}
	
	/*
	 * moves to a vertical or horizontal line with the player to start the attack
	 * returns true if this is at a vomit position
	 */
	private boolean moveToVomitPosition() {
		int deltax = Player.getInstance().getPosition().getPixelX() - this.getPosition().getPixelX();
		int deltay = Player.getInstance().getPosition().getPixelY() - this.getPosition().getPixelY();
		
		// if in range
		if((deltax*deltax < VOMIT_DISTANCE*Block.PIXELS_PER_FIELD*Block.PIXELS_PER_FIELD) && (deltay*deltay < VOMIT_DISTANCE*Block.PIXELS_PER_FIELD*Block.PIXELS_PER_FIELD)) {
			if(abs(deltax) < MAX_VOMIT_OFFSET) return true; // in position
			if(abs(deltay) < MAX_VOMIT_OFFSET) return true; // in position
			
			// not in position
			// move to position
			byte moveDir = 0;
			if(abs(deltax) < abs(deltay)) {
				moveDir = deltax < 0 ? Level.dirLeft : Level.dirRight;
			}
			else {
				moveDir = deltay < 0 ? Level.dirUp : Level.dirDown;
			}
			move(moveDir, this.getSpeed());
		}
		
		return false;
	}

	public void onUpdate() {
		if(getLifePoints() > 0) {
			if(this.getStunDuration() > 0) {
				stunned = true;
				decreaseStunDuration(this.updateFrequency);
			}
			else {
				if(this.getUnmoveableFrames() > 0) {
					stunned = true;
					decreaseUnmoveableFrames(this.updateFrequency);
				}
				else {
					stunned = false;
					if(!attacking) {
						if(nearPlayerForMelee()) {
							if(previousMelee == 0) {
								previousMelee = MELEE_FREQUENCY;
								attackMelee();
							}
						}
						else {
							if(moveToVomitPosition()) {
								if(previousVomit == 0) {
									if(nearPlayerForVomit()) {
										previousVomit = VOMIT_FREQUENCY;
										attackVomit();
									}
								}
							}
						}
					}
				}
			}
			if(previousVomit > 0) previousVomit--;
			if(previousMelee > 0) previousMelee--;
		}
	}
	

/*
	public void receiveAttack(byte damage, byte stun, byte specialId) {
		receiveDamage(damage);
		setInstantAnimation(ANIM_RECEIVE);
		attacking = false;

		receiveSpecialAttack(specialId);
		
		this.setStunDuration(stun);
		
		if (getLifePoints() <= 0){
			setInstantAnimation(DynamicLevelObject.ANIM_DIE);
			setNextAnimation(DynamicLevelObject.ANIM_DEAD);
		}
	}*/

	protected byte getFrameDuration(byte animation, byte frame) {
		return ANIMATION_FRAME_DURATION;
	}

	protected int[] getFrameSequence(byte animation) {
		switch(animation) {
		case DynamicLevelObject.ANIM_ATTACK:
		{
			switch(getPlayerDirection()) {
			case Level.dirLeft:
			case Level.dirRight:
			{
				if(melee) {
					return new int[] {9,10,11};
				}
				else {
					return new int[] {21,22,21,22,23};
				}
			}
			case Level.dirUp:
			{
				if(melee) {
					return new int[] {18,19};
				}
				else {
					return new int[] {27,28,27,28,29};
				}
			}
			case Level.dirDown:
			{
				if(melee) {
					return new int[] {15,16};
				}
				else {
					return new int[] {24,25,24,25,26};
				}
			}
			}
		}
		case DynamicLevelObject.ANIM_RECEIVE:
		{
			return new int[] {12,13};
		}
		case DynamicLevelObject.ANIM_DIE:
		{
			return new int[] {30,31};
		}
		case DynamicLevelObject.ANIM_DEAD:
		{
			return new int[] {14,17,20,32,14,14,14,14,14,14};
		}
		case DynamicLevelObject.ANIM_IDLE:
		{
			switch(getPlayerDirection()) {
			case Level.dirLeft:
			case Level.dirRight:
			{
				return new int[] {0};
			}
			case Level.dirUp:
			{
				return new int[] {6};
			}
			case Level.dirDown:
			{
				return new int[] {3};
			}
			}
		}
		case DynamicLevelObject.ANIM_LEFT:
		case DynamicLevelObject.ANIM_RIGHT:
		{
			return new int[] {0,1,2};
		}
		case DynamicLevelObject.ANIM_UP:
		{
			return new int[] {6,7,8};
		}
		case DynamicLevelObject.ANIM_DOWN:
		{
			return new int[] {3,4,5};
		}
		case DynamicLevelObject.ANIM_NONE:
		{
			return new int[] {4};
		}
		}
		return null;
	}
	
	protected void onAnimationChanged(byte oldAnimation, byte newAnimation) {
		// Mirror animation
		int mirror = Sprite.TRANS_NONE;
		
		switch(newAnimation) {
		case DynamicLevelObject.ANIM_ATTACK:
		{
			if(getPlayerDirection() == Level.dirLeft)
			{
				mirror = Sprite.TRANS_MIRROR;
			}
		} break;
		case DynamicLevelObject.ANIM_IDLE:
		{			
			if(getPlayerDirection() == Level.dirLeft)
			{
				mirror = Sprite.TRANS_MIRROR;
			}
		} break;
		case DynamicLevelObject.ANIM_NONE:
		{			
			if(getPlayerDirection() == Level.dirLeft)
			{
				mirror = Sprite.TRANS_MIRROR;
			}
		} break;
		case DynamicLevelObject.ANIM_RECEIVE:
		{
			if(getPlayerDirection() == Level.dirLeft)
			{
				mirror = Sprite.TRANS_MIRROR;
			}
		} break;
		case DynamicLevelObject.ANIM_LEFT:
		{
			mirror = Sprite.TRANS_MIRROR;
		}
		case ANIM_DEAD:
		{
			setNextAnimation(ANIM_DEAD);
		}
		}

		sprite.setTransform(mirror);
	}
	
	protected void onAnimationFinished(byte animation) {
		// inflict damage to player if attack is over
		if(attacking) {
			if(animation == DynamicLevelObject.ANIM_ATTACK) {
				attacking = false;
				if(melee) {
					if(!stunned) {
						if(nearPlayerForMelee()) {
							int damage = calculateDamage();
							Player.getInstance().receiveDamage((byte)damage);
						}
					}
				}
				else {
					if(!stunned) {
						if(nearPlayerForVomit()) {	
							Broccoli b = new Broccoli(broccoliStart, broccoliDir, BROCCOLI_SPEED, spitLength < MAX_BROCCOLI_DISTANCE ? spitLength : MAX_BROCCOLI_DISTANCE);
							b.launcher = this;
							b.launch();
						}
					}
				}
			}
		}

		if(animation == DynamicLevelObject.ANIM_RECEIVE) {
			attacking = false;
		}

		if(animation == DynamicLevelObject.ANIM_DEAD) {
			setNextAnimation(ANIM_DEAD);
		}
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
	
	
}
