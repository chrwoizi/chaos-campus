package LevelObjects;

import Block;
import Error;
import Level;
import Player;
import Position;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class Witch extends NPC {

	protected static Image image = null;
	
	protected static byte UPDATE_FREQUENCY = 2;

	protected static final byte ANIMATION_FRAME_DURATION = 5;
	
	protected static final short STANDARD_ATTACKDURATION = 4;
	protected static final int STANDARD_ATTACKTIME = 1;
    protected static final byte STANDARD_MOVETIME = 2;
    protected static final byte STANDARD_SPEED = 2;
    protected static final short STANDARD_LIFEPOINTS = 200;
    protected static final short STANDARD_ATTACKDAMAGE = 5;
    
    // true if attacking with melee attack
    private boolean casting = false;
    
    // true if currently attacking
    private boolean attacking = false;
    
    // true if cannot attack
    private boolean stunned = false;
    
    private byte teleporting = 0; // 0: no teleport, 1: vanish, 2: appear
    private byte teleportDistance;
    private byte teleportDirection;
    private byte teleportOvershoot = 49;
    private static final byte ANIM_VANISH = 50;
    private static final byte ANIM_APPEAR = 51;
    
    // max distance to player for melee attack in square fields
    private static final byte CAST_DISTANCE = 36;

    // max pixel offset to vertical or horizontal line with player for starting a vomit attack
    private static final byte MAX_THROW_OFFSET = 8;
    
    // max distance to player for vomit attack in square fields
    private static final byte MAX_THROW_DISTANCE = 64;
    private static final byte MIN_THROW_DISTANCE = 4;

    private static final byte THROW_FREQUENCY = 30;
    private byte previousThrow = 0;
    private static final byte CAST_FREQUENCY = 30;
    private byte previousCast = 0;
    
    private static final byte REACTION_DISTANCE = 64;
    
    private static final byte invTeleportPossibility = 50; // p = 1/40

    private static final byte POWDER_SPEED = 6;
    private static final short MAX_POWDER_DISTANCE = (short)(5*16);
    private Position powderStart;
    private byte powderDir;
    private short throwLength;
	
	public Witch(short id, Position position, Block parentBlock, boolean disabled, int npcType, byte itemWhenDying) {
		super(id, position, parentBlock, disabled, npcType, itemWhenDying);
		
		setEnabled(!disabled);
		
		setPosition(position);
		setCaptureRadius(0);
		setMoveDirection(Level.dirNone);
		setSpeed(STANDARD_SPEED);   //set the speed for the movement
		setLifepoints(STANDARD_LIFEPOINTS); // set the lifepoint for the glutamator
		setAttackDamage(STANDARD_ATTACKDAMAGE);// Set the damage of the NPC
		life = new LifeStatus(STANDARD_LIFEPOINTS);
		invStunPossibility = 20;
		
		
	    setAttackTime(0); // Set the time to start an attack - StandardDauer 2
		
		
		try {
		// load container images if needed
			if( image == null ) {
				try {
					image = Image.createImage("/npc/npc" + npcType + ".png");
				} catch ( Exception e ) {
					new Error("Witch img", e);
				}
			}
			
			sprite = new Sprite(image, 16, 24);
			sprite.setFrame(1);
			sprite.defineCollisionRectangle(2, 16, 12, 6);

			/*damageableZoneLeft = -24;
			damageableZoneRight = 24;
			damageableZoneTop = -24;
			damageableZoneBottom = 8;*/
			
			
			updateFrequency = 1;
			
			sprite.defineReferencePixel(8, 22);
		}
		catch (Exception e){
			new Error("Witch sprite init", e);
		}
		
		// update sprite position
		onPixelPositionChanged();
	}

	public void onRender(Graphics g) {
		this.sct.render(g);
		this.life.render(g, this.pixelPositionX,this.pixelPositionY-2);
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
	
	private void attackThrow() {
		// launch-parameters
		powderDir = getPlayerDirection();
		powderStart = new Position(getPosition());
		switch(powderDir) {
		case Level.dirLeft:
			powderStart.movePixels((byte)-20, (byte)-12);
			break;
		case Level.dirRight:
			powderStart.movePixels((byte)20, (byte)-12);
			break;
		case Level.dirUp:
			powderStart.movePixels((byte)0, (byte)-24);
			break;
		case Level.dirDown:
			powderStart.movePixels((byte)0, (byte)0);
			break;
		}
		
		throwLength = getPlayerDistance();
		
		// state
		casting = false;
		attacking = true;
		setInstantAnimation(DynamicLevelObject.ANIM_ATTACK);
		setNextAnimation(DynamicLevelObject.ANIM_IDLE);
	}
	
	private void attackCast() {
		casting = true;
		attacking = true;
		setInstantAnimation(DynamicLevelObject.ANIM_ATTACK);
		setNextAnimation(DynamicLevelObject.ANIM_IDLE);
	}
	
	private boolean inCastZone() {
		this.life.setEnabled(true);
		int deltax = abs(Player.getInstance().getPosition().fieldX - this.getPosition().fieldX);
		int deltay = abs(Player.getInstance().getPosition().fieldY - this.getPosition().fieldY);
		int squareDistance = (deltax * deltax) + (deltay * deltay);
		return CAST_DISTANCE > squareDistance;
	}
	
	private boolean inThrowZone() {
		this.life.setEnabled(true);
		int deltax = abs(Player.getInstance().getPosition().fieldX - this.getPosition().fieldX);
		int deltay = abs(Player.getInstance().getPosition().fieldY - this.getPosition().fieldY);
		int squareDistance = (deltax * deltax) + (deltay * deltay);
		return (MAX_THROW_DISTANCE >= squareDistance) && (MIN_THROW_DISTANCE <= squareDistance);
	}

	/*
	 * moves to a vertical or horizontal line with the player to start the attack
	 * returns true if this is at a vomit position
	 */
	private boolean moveToThrowPosition() {
		int deltax = Player.getInstance().getPosition().getPixelX() - this.getPosition().getPixelX();
		int deltay = Player.getInstance().getPosition().getPixelY() - this.getPosition().getPixelY();
		
		// if in range
		if((deltax*deltax < MAX_THROW_DISTANCE*Block.PIXELS_PER_FIELD*Block.PIXELS_PER_FIELD) && (deltay*deltay < MAX_THROW_DISTANCE*Block.PIXELS_PER_FIELD*Block.PIXELS_PER_FIELD)) {
			if(abs(deltax) < MAX_THROW_OFFSET) return true; // in position
			if(abs(deltay) < MAX_THROW_OFFSET) return true; // in position
			
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
	
	private byte getThrowPositionDirection() {
		int deltax = Player.getInstance().getPosition().getPixelX() - this.getPosition().getPixelX();
		int deltay = Player.getInstance().getPosition().getPixelY() - this.getPosition().getPixelY();
		byte moveDir = 0;
		if(abs(deltax) < abs(deltay)) {
			moveDir = deltax < 0 ? Level.dirLeft : Level.dirRight;
		}
		else {
			moveDir = deltay < 0 ? Level.dirUp : Level.dirDown;
		}
		
		return moveDir;
	}
	
	private int min(int a, int b) {
		return a < b ? a : b;
	}
	
	private void teleport() {
		int deltax = abs(Player.getInstance().getPosition().fieldX - this.getPosition().fieldX);
		int deltay = abs(Player.getInstance().getPosition().fieldY - this.getPosition().fieldY);
		int squareDistance = (deltax * deltax) + (deltay * deltay);
		teleportDistance = (byte)(min(squareDistance+teleportOvershoot,128)); 
		teleportDirection = getThrowPositionDirection();
		teleporting = 1;
		this.setInstantAnimation(ANIM_VANISH);
		this.setNextAnimation(ANIM_APPEAR);	
	}

	public void onUpdate() {
		if (getLifePoints() > 0) {

			int deltax = abs(Player.getInstance().getPosition().fieldX
					- this.getPosition().fieldX);
			int deltay = abs(Player.getInstance().getPosition().fieldY
					- this.getPosition().fieldY);
			int squareDistance = (deltax * deltax) + (deltay * deltay);

			if (squareDistance < REACTION_DISTANCE) {

				if (this.getStunDuration() > 0) {
					stunned = true;
					decreaseStunDuration(this.updateFrequency);
				} else {
					if (this.getUnmoveableFrames() > 0) {
						stunned = true;
						decreaseUnmoveableFrames(this.updateFrequency);
					} else {
						stunned = false;
						if (!attacking && teleporting == 0) {
							if (moveToThrowPosition()) {
								if (previousThrow == 0) {
									if (inThrowZone()) {
										previousThrow = THROW_FREQUENCY;
										attackThrow();
									} else {
										if (previousCast == 0) {
											if (inCastZone()) {
												previousCast = CAST_FREQUENCY;
												attackCast();
											}
										}
									}
								} else {
									if (previousCast == 0) {
										if (inCastZone()) {
											previousCast = CAST_FREQUENCY;
											attackCast();
										}
									}
								}
							} else {
								if (previousCast == 0) {
									if (inCastZone()) {
										previousCast = CAST_FREQUENCY;
										attackCast();
									}
								}
							}
						}
					}
				}
				if (!attacking && teleporting == 0) {
					int r = randomize(invTeleportPossibility - 1);
					if (r == 0) {
						teleport();
					}
				}
				if (previousThrow > 0)
					previousThrow--;
				if (previousCast > 0)
					previousCast--;
			}
		}
	}

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
				if(casting) {
					return new int[] {21,22};
				}
				else {
					return new int[] {17,18};
				}
			}
			case Level.dirUp:
			{
				if(casting) {
					return new int[] {25,26};
				}
				else {
					return new int[] {19,20};
				}
			}
			case Level.dirDown:
			{
				if(casting) {
					return new int[] {23,24};
				}
				else {
					return new int[] {15,16};
				}
			}
			}
		}
		case DynamicLevelObject.ANIM_RECEIVE:
		{
			switch(getPlayerDirection()) {
			case Level.dirLeft:
			case Level.dirRight:
			{
				return new int[] {11,12};
			}
			case Level.dirUp:
			{
				return new int[] {13,14};
			}
			case Level.dirDown:
			{
				return new int[] {9,10};
			}
			}
		}
		case DynamicLevelObject.ANIM_DIE:
		{
			return new int[] {33,34,35,36,37,38};
		}
		case DynamicLevelObject.ANIM_DEAD:
		{
			return new int[] {38};
		}
		case DynamicLevelObject.ANIM_IDLE:
		{
			switch(getPlayerDirection()) {
			case Level.dirLeft:
			case Level.dirRight:
			{
				return new int[] {4};
			}
			case Level.dirUp:
			{
				return new int[] {7};
			}
			case Level.dirDown:
			{
				return new int[] {1};
			}
			}
		}
		case DynamicLevelObject.ANIM_LEFT:
		case DynamicLevelObject.ANIM_RIGHT:
		{
			return new int[] {3,4,5};
		}
		case DynamicLevelObject.ANIM_UP:
		{
			return new int[] {6,7,8};
		}
		case DynamicLevelObject.ANIM_DOWN:
		{
			return new int[] {0,1,2};
		}
		case ANIM_VANISH:
		{
			return new int[] {27,28,29,30,31,32};
		}
		case ANIM_APPEAR:
		{
			return new int[] {32,31,30,29,28,27};
		}
		case DynamicLevelObject.ANIM_NONE:
		{
			return new int[] {1};
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
		}

		sprite.setTransform(mirror);
	}
	
	protected void onAnimationFinished(byte animation) {
		// inflict damage to player if attack is over
		if(attacking) {
			if(animation == DynamicLevelObject.ANIM_ATTACK) {
				attacking = false;
				if(casting) {
					if(!stunned) {
						if(inCastZone()) {
							int damage = calculateDamage();
							Player.getInstance().receiveDamage((byte)damage);
						}
					}
				}
				else {
					if(!stunned) {
						if(inThrowZone()) {	
							Powder b = new Powder(powderStart, powderDir, POWDER_SPEED, throwLength < MAX_POWDER_DISTANCE ? throwLength : MAX_POWDER_DISTANCE);
							b.launcher = this;
							b.launch();
						}
					}
				}
			}
		}

		if(animation == DynamicLevelObject.ANIM_RECEIVE) {
			attacking = false;
			teleporting = 0;
		}

		if(animation == DynamicLevelObject.ANIM_DIE) {
			setEnabled(false);
		}

		if(animation == ANIM_VANISH) {
			teleporting = 2;
			for(int i = 0; i < teleportDistance/2; i++) {
				this.move(teleportDirection, (byte)4);	
			}
			setNextAnimation(ANIM_APPEAR);
		}

		if(animation == ANIM_APPEAR) {
			teleporting = 0;
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
