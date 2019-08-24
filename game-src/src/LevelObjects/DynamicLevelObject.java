package LevelObjects;
import Level;
import Block;
import Position;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * An abstract class used for each moving object within a level 
 * @author Martin Wahnschaffe
 */
public abstract class DynamicLevelObject extends LevelObject {
		
	protected Sprite sprite = null;
	public boolean isSprite(Sprite s) {
		return sprite == s;
	}
	
	protected static final byte ANIM_NONE = 0;
	protected static final byte ANIM_IDLE = 1;
	protected static final byte ANIM_UP = 2;
	protected static final byte ANIM_RIGHT = 3;
	protected static final byte ANIM_DOWN = 4;
	protected static final byte ANIM_LEFT = 5;
	protected static final byte ANIM_ATTACK = 6;
	protected static final byte ANIM_RECEIVE = 7;
	protected static final byte ANIM_DAZED = 8;
	protected static final byte ANIM_END_DAZED = 9;
	protected static final byte ANIM_DIE = 10;
	protected static final byte ANIM_DEAD = 11;
	
	private byte animtime = 0;
	private byte nextAnimation = ANIM_NONE;
	private byte animation = ANIM_NONE;
	
	private byte currentDirection = Level.dirNone;

	protected byte updateFrequency = 3;
	private byte updatetime = updateFrequency;
	
	private byte collisionRadius = 6;
	public boolean canCollide = true;
	
	public DynamicLevelObject(short id, Position position, Block parentBlock) {
		super(id, position, parentBlock);
		dynamicLevelObject = true;
		visible = true;
	}

	protected void onRender(Graphics g) {
		if(sprite != null) sprite.paint(g);
	}

	
	public final void update() {
		if(isEnabled()) {
			if(sprite != null) {
				if( animtime > 0 ) {
					animtime--;
					if( animtime == 0 ) {
						//System.out.println(sprite.getFrameSequenceLength() + " " + sprite.getFrame());
						if( sprite.getFrame() == sprite.getFrameSequenceLength()-1 ) {
							onAnimationFinished(animation);
							setInstantAnimation(nextAnimation);
							nextAnimation = ANIM_NONE;
						}
						else {
							sprite.nextFrame();
							animtime = getFrameDuration(animation, (byte)sprite.getFrame());
						}
					}
				}
			}
			
			// slow down update
			if (updatetime <= 1){
				onUpdate();
				updatetime = updateFrequency;
			}
			else{
				updatetime--;
			}
		}
	}
	protected void onUpdate() {
		// notifies derived types
	}
	
	protected void setCollisionRadius(byte radius) { collisionRadius = radius; }
	
	public boolean collidesWith(Sprite otherObject) {
		if( !canCollide || sprite == null || otherObject == null || sprite == otherObject )
			return false;
		return sprite.collidesWith(otherObject, false);
	}
	
	/**
	 * Moves this "speed" pixels in a "direction" if there is no collision.
	 * @param direction Direction as Level.dirX
	 * @param speed Speed in pixels
	 * @return true this it actually moved
	 */
	public boolean move(byte direction, byte speed) {

		// animation
		if(currentDirection != direction) {
			currentDirection = direction;
		}
		
		{
			if (direction == Level.dirUp){
				setInstantAnimation(ANIM_UP);
			}
			else if (direction == Level.dirRight){
				setInstantAnimation(ANIM_RIGHT);
			}
			else if (direction == Level.dirDown){
				setInstantAnimation(ANIM_DOWN);
			}
			else if (direction == Level.dirLeft){
				setInstantAnimation(ANIM_LEFT);
			}
			else if (direction == (Level.dirLeft | Level.dirUp)){
				setInstantAnimation(ANIM_UP);
			}
			else if (direction == (Level.dirLeft | Level.dirDown)){
				setInstantAnimation(ANIM_DOWN);
			}
			else if (direction == (Level.dirRight | Level.dirUp)){
				setInstantAnimation(ANIM_UP);
			}
			else if (direction == (Level.dirRight | Level.dirDown)){
				setInstantAnimation(ANIM_DOWN);
			}
			else {
				setInstantAnimation(ANIM_NONE);
			}
		}

		if( direction != Level.dirNone ) {
			// position
			Position p = new Position(getPosition());
			
			int changedWhat = Level.activeLevel.move(p, sprite, direction, speed, collisionRadius);
			
			if(changedWhat > 0) {
				setPosition(p);
				return true;
			}
		}
		
		return false;
	}
	
	protected void onPixelPositionChanged() {
		if( sprite != null )
			sprite.setRefPixelPosition(pixelPositionX, pixelPositionY);
	}
	
	/**
	 * Returns true if the object is inside the passed region
	 * @param topLeft
	 * @param bottomRight
	 * @return
	 */
	public boolean isInsideRegion(Position topLeft, Position bottomRight) 
	{
		short px = getPosition().getPixelX();
		short py = getPosition().getPixelY();
	
		short tlx = topLeft.getPixelX();
		short tly = topLeft.getPixelY();
		short brx = bottomRight.getPixelX();
		short bry = bottomRight.getPixelY();
			
		for(byte i = 0; i < 4; i++) {
			if((px >= tlx)
			&& (px <= brx)
			&& (py >= tly)
			&& (py <= bry)) return true;
		}
		return false;
	}
		
	protected void setInstantAnimation(byte animation) {
		if( this.animation == animation ) {
			animtime = getFrameDuration(animation, (byte)0);
			if(animation == ANIM_DEAD) {
				// loop dead anim
				sprite.setFrame(0);
			}
			return;
		}
		
		onAnimationChanged(this.animation, animation);
		this.animation = animation;
		this.nextAnimation = ANIM_NONE;
		if( sprite != null ) {
			if( this.animation != ANIM_NONE ) {
				sprite.setFrameSequence(getFrameSequence(animation));
				sprite.setFrame(0);
				animtime = getFrameDuration(animation, (byte)0);
			}
			else {
				animtime = 0;
			}
		}
	}
	protected void onAnimationChanged(byte oldAnimation, byte newAnimation) {
		// notifies derived types
	}
	protected void onAnimationFinished(byte animation) {
		// notifies derived types
	} 
	/**
	 * Tests if the animation is the running
	 * @param ani
	 * @return true/false
	 */
	protected boolean testAnimation(byte ani) {
		if (this.animation == ani) {
			return true;
		} else {
			return false;
		}
			
	}
	/**
	 * Returns the current animation
	 * @return animation - byte
	 */
	protected byte getAnimation() {
		return this.animation;
	}

	protected void setNextAnimation(byte animation) {
		if( this.animation == ANIM_NONE ) {
			setInstantAnimation(animation);
			this.nextAnimation = ANIM_NONE;
		}
		else {
			this.nextAnimation = animation;
		}
	}
	
	protected abstract int[] getFrameSequence(byte animation);
	
	protected abstract byte getFrameDuration(byte animation, byte frame);
	
}

