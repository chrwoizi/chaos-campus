package LevelObjects;
import Player;
import Error;
import Position;

import java.util.*;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Powder extends Projectile {

	private byte damage = 5;
	private byte stun = 0;
	private byte specialId = 0;
	private boolean tripmine = false;

	public Powder(Position launchPosition, byte direction, byte speed, short maxDistance) {
		super((byte)1, launchPosition, direction, speed, maxDistance, 8, 8);
	}
	
	protected int[] getFrameSequence(byte animation) {
		switch(animation) {
			case DynamicLevelObject.ANIM_RIGHT:
			case DynamicLevelObject.ANIM_LEFT:
			{
				int[] i = {2,3};
				return i;
			}
			case DynamicLevelObject.ANIM_DOWN:
			{
				int[] i = {0,1};
				return i;
			}
			case DynamicLevelObject.ANIM_UP:
			{
				int[] i = {4,5};
				return i;
			}
			case DynamicLevelObject.ANIM_DIE:
			{
				int[] i = {0,1,2,3};
				return i;
			}
		}
		// else
		int[] i = {0};
		return i;
	}

	protected void onAnimationChanged(byte oldAnimation, byte newAnimation) {
		super.onAnimationChanged(oldAnimation, newAnimation);
		

		if(newAnimation == DynamicLevelObject.ANIM_LEFT)
		{
			sprite.setTransform(Sprite.TRANS_MIRROR);
		}
		
		if(newAnimation == DynamicLevelObject.ANIM_DIE)
		{
			try {
				Image img = Image.createImage("/projectile1_die.png");
				sprite = new Sprite(img,32,32);
				sprite.defineReferencePixel(16, 16);
				sprite.setFrameSequence(this.getFrameSequence(ANIM_DIE));
				sprite.setFrame(0);
				sprite.setRefPixelPosition(pixelPositionX, pixelPositionY);
			}
			catch(Exception e) {
				new Error("Powder sprite", e);
			}
		}
	}
	
	protected byte getFrameDuration(byte animation, byte frame) {
		return 2;
	}
	
	protected void onGroundImpact() {
		this.setInstantAnimation(DynamicLevelObject.ANIM_DIE);
	}
	
	protected void onPlayerImpact() {
		if(tripmine) {
			Player.getInstance().receiveDamage(damage);
		}
		else {
			Player.getInstance().receiveDamage(damage);
		}
		
		this.setInstantAnimation(DynamicLevelObject.ANIM_DIE);
	}
	
	protected void onNpcImpact(Vector objects) {
		for(int i = 0; i < objects.size(); i++) {
			DynamicLevelObject obj = (DynamicLevelObject)objects.elementAt(i);
			try {
				if(obj.getClass() == Class.forName("NPC")) {
					NPC n = (NPC)obj;
					n.receiveAttack(damage, stun, specialId);					
				}
			}
			catch(Exception e) {
				
			}
		}

		this.setInstantAnimation(DynamicLevelObject.ANIM_DIE);
	}
	
	protected void onProjectileDead() {
		delete();
	}
}
