package LevelObjects;

import Level;
import Error;
import Player;
import Position;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import java.util.*;

/**
 * Broccoli, explosives
 * @author Christian Woizischke
 *
 */
public abstract class Projectile extends DynamicLevelObject {
	
	public interface ImpactListener {
		/**
		 * Called when the projectile hits the ground
		 * (falls down after maxDistance or hits a collision)
		 * @param projectile
		 */
		public void onGroundImpact(Projectile projectile);
		
		/**
		 * Called when the projectile hits the player
		 * @param projectile
		 */
		public void onPlayerImpact(Projectile projectile);
		
		/**
		 * Called when the projectile has played the dying animation and is finally dead now
		 * @param projectile
		 */
		public void onProjectileDead(Projectile projectile);
		
		/**
		 * Called when the projectile hits one ore more objects.
		 * @param projectile
		 * @param npc vector of DynamicLevelObjects
		 */
		public void onNpcImpact(Projectile projectile, Vector objects);
	}
	
	private boolean flying = false;
	private byte direction;
	private byte speed;
	private short maxDistance;
	private byte currentDistance = 0;
	private boolean dead = false;
	public boolean isDead() { return dead; }
	
	public DynamicLevelObject launcher = null;
	
	public byte damageRadius = 16;
	
	// the impactListener is notified when the projectile hits the ground or an enemy
	public ImpactListener impactListener = null;
	
	public Projectile(byte projectileId, Position launchPosition, byte direction, byte speed, short maxDistance, int imgW, int imgH) {
		super(
			LevelObject.NO_ID, 
			launchPosition,
			null
		);
		
		Level.activeLevel.addDynamicObject(this);
		
		this.direction = direction;
		this.speed = speed;
		this.maxDistance = maxDistance;
		
		this.updateFrequency = 1;
		
		try {
			Image img = Image.createImage("/projectile" + projectileId + ".png");
			sprite = new Sprite(img,imgW,imgH);
			sprite.defineReferencePixel(imgW/2, imgH/2);
			setPosition(launchPosition);
		}
		catch(Exception e) {
			new Error("Projectile sprite", e);
		}

		if(direction == Level.dirRight) sprite.setTransform(Sprite.TRANS_MIRROR);
		if(direction == Level.dirDown) sprite.setTransform(Sprite.TRANS_MIRROR);
	}
	
	/**
	 * Launches the projectile
	 *
	 */
	public void launch() {
		if(flying) return;
		
		this.setEnabled(true);
		flying = true;
	}
	
	protected void onUpdate() {
		if(flying) {			
			if(this.currentDistance >= this.maxDistance) {
				// projectile hits the ground
				flying = false;
				this.setInstantAnimation(DynamicLevelObject.ANIM_DIE);
				this.setNextAnimation(DynamicLevelObject.ANIM_DEAD);
				if(impactListener != null) impactListener.onGroundImpact(this);
				onGroundImpact();
			}
			else {
				// check if the projectile hits a damageable object
				
				Position pos = getPosition();
				
				Position topleft = new Position(pos.fieldX, pos.fieldY);
				topleft.pixelOffsetX = pos.pixelOffsetX;
				topleft.movePixels((short)-damageRadius, (short)-damageRadius);
				
				Position bottomright = new Position(pos.fieldX, pos.fieldY);
				bottomright.pixelOffsetX = pos.pixelOffsetX;
				bottomright.movePixels(damageRadius, damageRadius);
				
				Vector objects = Level.activeLevel.getActiveDynamicsInRegion(topleft, bottomright, NPC.class);
				
				if(objects.size() > 0) {
					if(objects.size() == 1 && objects.elementAt(0) == launcher) {
						// do not collide with the launcher
					}
					else {
						// projectile hits an object
						flying = false;
						this.setInstantAnimation(DynamicLevelObject.ANIM_DIE);
						this.setNextAnimation(DynamicLevelObject.ANIM_DEAD);
						if(impactListener != null) impactListener.onNpcImpact(this, objects);
						onNpcImpact(objects);
					}
				}
			}
			
			if(flying) {
				// set collision of launcher off for the movement
				boolean oldLauncherCollide = true;
				if(launcher != null) {
					oldLauncherCollide = launcher.canCollide;
					launcher.canCollide = false;
				}
				
				// move
				if(this.move(direction, speed)) {
					this.currentDistance += speed;
				}
				else {
					// projectile hits a ground collision or the player
					flying = false;
					this.setInstantAnimation(DynamicLevelObject.ANIM_DIE);
					this.setNextAnimation(DynamicLevelObject.ANIM_DEAD);

					// check if this collides with player
					
					int mx = 0;
					int my = 0;
					
					switch(direction) {
					case Level.dirLeft:
						mx = -speed;
						break;
					case Level.dirRight:
						mx = speed;
						break;
					case Level.dirUp:
						my = -speed;
						break;
					case Level.dirDown:
						my = speed;
						break;
					}
					
					sprite.move(mx, my);
					boolean playercollision = Player.getInstance().testCollisionWithSprite(sprite);
					sprite.move(-mx, -my);
					
					if(playercollision) {
						if(impactListener != null) impactListener.onPlayerImpact(this);
						onPlayerImpact();
					}
					else {
						if(impactListener != null) impactListener.onGroundImpact(this);
						onGroundImpact();
					}
				}
				
				if(launcher != null) {
					launcher.canCollide = oldLauncherCollide;
				}
			}
		}
	}
	
	/** 
	 * overload the collidesWith method of DynamicLevelObject to inflict damage to those who collide with this
	 */
	public boolean collidesWith(Sprite otherObject) {
		if( !canCollide || sprite == null || otherObject == null || sprite == otherObject )
			return false;
		if(launcher != null && launcher.isSprite(otherObject)) return false;
		boolean collides = sprite.collidesWith(otherObject, false);
		if(collides) {
			if(Player.getInstance().isPlayerSprite(otherObject)) {
				if(impactListener != null) impactListener.onPlayerImpact(this);
				onPlayerImpact();
			}
		}
		return collides;
	}
	
	/**
	 * removes this from the world
	 *
	 */
	public void delete() {
		Level.activeLevel.removeObjectReference(this);
		this.block.remDynamicObject(this);
	}
	
	protected void onAnimationChanged(byte oldAnimation, byte newAnimation) {
		if(newAnimation == DynamicLevelObject.ANIM_DEAD) {
			// the projectile has played the die animation and is now dead. notify
			dead = true;
			if(impactListener != null) impactListener.onProjectileDead(this);
			onProjectileDead();
		}
	}
	
	protected abstract int[] getFrameSequence(byte animation);
	
	protected abstract byte getFrameDuration(byte animation, byte frame);

	protected void onGroundImpact() {}
	protected void onPlayerImpact() {}
	protected void onNpcImpact(Vector objects) {}
	protected void onProjectileDead() {}
}
